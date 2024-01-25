package com.realgear.samplemusicplayertest.ui.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.realgear.samplemusicplayertest.ui.adapters.models.BaseRecyclerViewItem;
import com.realgear.samplemusicplayertest.ui.adapters.viewholders.BaseViewHolder;
import com.realgear.samplemusicplayertest.ui.adapters.viewholders.ViewHolderState;
import com.realgear.samplemusicplayertest.utils.UIThreadExecutor;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter extends PagedListAdapter<BaseRecyclerViewItem, BaseViewHolder> {

    public enum ViewType {
        LIST, GRID
    }

    ViewType m_vLayoutViewType;

    RecyclerView m_vRecyclerView;

    int m_vRecyclerViewState = -1;

    public UIThreadExecutor m_vMainThread;

    public List<BaseViewHolder> m_vBaseViewHolders;

    boolean isFlinging = false;

    private static final DiffUtil.ItemCallback<BaseRecyclerViewItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull BaseRecyclerViewItem oldItem, @NonNull BaseRecyclerViewItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull BaseRecyclerViewItem oldItem, @NonNull BaseRecyclerViewItem newItem) {
            return oldItem.getHashCode() == newItem.getHashCode();
        }
    };

    public BaseRecyclerViewAdapter(RecyclerView recyclerView) {
        super(DIFF_CALLBACK);

        this.m_vRecyclerView = recyclerView;
        this.m_vMainThread = new UIThreadExecutor();
        this.m_vBaseViewHolders = new ArrayList<>();

        this.m_vRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                m_vRecyclerViewState = newState;
            }
        });

        this.m_vRecyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                isFlinging = true;
                Log.e("ON_FLING", "Fling Velocity : " + velocityY);
                return false;
            }
        });

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ViewHolderState.getInstance().addRecycledView(holder.getAdapterPosition());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewHolderState.getInstance().removeRecycledView(holder.getAdapterPosition());
    }

    public void onUpdateItemsLayout() {
        for (BaseViewHolder viewHolder : m_vBaseViewHolders) {
            viewHolder.onInitializeView(this.m_vLayoutViewType);
        }

        for (BaseViewHolder viewHolder : m_vBaseViewHolders) {
            viewHolder.onBindData();
        }
    }

    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBindViewHolder(this.getItem(position));

        if (this.m_vRecyclerView.getLayoutManager().isSmoothScrolling() || ViewHolderState.getInstance().isViewRecycled(position))
            return;

        holder.onBindData();
    }

    public void setAdapterViewType(ViewType viewType) {
        if (this.m_vLayoutViewType == viewType)
            return;

        this.m_vLayoutViewType = viewType;
        this.onUpdateItemsLayout();
    }

    public void setItems(PagedList<BaseRecyclerViewItem> items) {
        this.submitList(items);
    }

    @Override
    public long getItemId(int position) {
        return this.getItem(position).getHashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).getItemType().ordinal();
    }

    public ViewType getViewType() {
        return this.m_vLayoutViewType;
    }
}
