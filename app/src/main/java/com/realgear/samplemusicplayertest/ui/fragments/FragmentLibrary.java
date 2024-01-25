package com.realgear.samplemusicplayertest.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.realgear.extensions.gridlayout.GridLayoutManagerExtended;
import com.realgear.mediaplayer.model.Song;
import com.realgear.samplemusicplayertest.R;
import com.realgear.samplemusicplayertest.ui.adapters.BaseRecyclerViewAdapter;
import com.realgear.samplemusicplayertest.ui.adapters.LibraryRecyclerViewAdapter;
import com.realgear.samplemusicplayertest.ui.adapters.models.BaseRecyclerViewItem;
import com.realgear.samplemusicplayertest.ui.adapters.models.SongRecyclerViewItem;
import com.realgear.samplemusicplayertest.ui.adapters.viewmodels.LibraryViewModel;
import com.realgear.samplemusicplayertest.utils.BaseDataSource;
import com.realgear.samplemusicplayertest.utils.BaseFactory;
import com.realgear.samplemusicplayertest.utils.UIThreadExecutor;

public class FragmentLibrary extends Fragment {

    private View m_vRootView;

    private RecyclerView m_vLibraryRecyclerView;
    private BaseRecyclerViewAdapter m_vLibraryAdapter;
    private GridLayoutManagerExtended m_vGridLayout;

    private LibraryViewModel m_vLibraryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.m_vRootView = inflater.inflate(R.layout.fragment_library, container, false);
        return this.m_vRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.m_vLibraryRecyclerView = findViewById(R.id.library_recyclerView);
        this.m_vLibraryAdapter = new LibraryRecyclerViewAdapter(this.m_vLibraryRecyclerView);

        int panelHeights = getResources().getDimensionPixelSize(R.dimen.navigation_bar_height) + getResources().getDimensionPixelSize(R.dimen.media_player_bar_height);
        this.m_vLibraryRecyclerView.setPadding(0, 0, 0, panelHeights);

        this.m_vLibraryViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        this.m_vLibraryViewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            PagedList.Config config = new PagedList.Config.Builder()
                    .setPageSize(40)
                    .setInitialLoadSizeHint(80)
                    .setPrefetchDistance(10)
                    .setEnablePlaceholders(false)
                    .build();

            BaseFactory baseDataSource = new BaseFactory(new BaseDataSource(this.getContext(), songs, SongRecyclerViewItem.class, Song.class));
            PagedList<BaseRecyclerViewItem> items = new PagedList.Builder<>(baseDataSource.create(), config)
                    .setNotifyExecutor(new UIThreadExecutor())
                    .setFetchExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                    .build();

            this.m_vLibraryAdapter.setItems(items);
        });

        this.m_vLibraryAdapter.setHasStableIds(true);
        this.m_vLibraryRecyclerView.setHasFixedSize(true);
        this.m_vLibraryRecyclerView.setDrawingCacheEnabled(true);
        this.m_vLibraryRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        this.m_vLibraryRecyclerView.setItemViewCacheSize(200);
        setAdapterViewType(BaseRecyclerViewAdapter.ViewType.LIST);

        this.m_vLibraryRecyclerView.setAdapter(this.m_vLibraryAdapter);

        FloatingActionButton btn = findViewById(R.id.btn_test_layout);
        btn.setOnClickListener(v -> {
            setAdapterViewType((
                    this.m_vLibraryAdapter.getViewType() == BaseRecyclerViewAdapter.ViewType.GRID) ?
                    BaseRecyclerViewAdapter.ViewType.LIST : BaseRecyclerViewAdapter.ViewType.GRID);
        });
    }

    private void setAdapterViewType(BaseRecyclerViewAdapter.ViewType viewType) {
        int rowCount = (viewType == BaseRecyclerViewAdapter.ViewType.LIST) ? 1 : 3;

        if(m_vGridLayout == null) {
            this.m_vGridLayout = new GridLayoutManagerExtended(getContext(), rowCount);
            this.m_vLibraryRecyclerView.setLayoutManager(this.m_vGridLayout);
        }
        else {
            this.m_vGridLayout.setSpanCount(rowCount);
        }

        this.m_vLibraryAdapter.setAdapterViewType(viewType);
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return this.m_vRootView.findViewById(id);
    }
}
