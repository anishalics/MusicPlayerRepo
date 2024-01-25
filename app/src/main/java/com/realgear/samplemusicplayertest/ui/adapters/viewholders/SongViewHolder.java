package com.realgear.samplemusicplayertest.ui.adapters.viewholders;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.realgear.samplemusicplayertest.R;
import com.realgear.samplemusicplayertest.glide.audiocover.AudioFileCover;
import com.realgear.samplemusicplayertest.ui.adapters.BaseRecyclerViewAdapter;
import com.realgear.samplemusicplayertest.ui.adapters.helpers.CacheHelper;
import com.realgear.samplemusicplayertest.ui.adapters.models.BaseRecyclerViewItem;
import com.realgear.samplemusicplayertest.ui.adapters.models.SongRecyclerViewItem;
import com.realgear.samplemusicplayertest.utils.AsyncDataLoader;
import com.realgear.samplemusicplayertest.utils.ViewsUtil;

public class SongViewHolder extends BaseViewHolder {

    private ConstraintLayout m_vRootView;

    private CardView m_vImageView_Art_Parent;
    private ConstraintLayout m_vLayout_Description;
    private TextView m_vTextView_Title;
    private TextView m_vTextView_Artist;

    private ImageView m_vImageView_Art;

    private Runnable m_vImageLoader;

    public SongViewHolder(@NonNull View itemView) {
        super(itemView);

        this.m_vRootView = findViewById(R.id.item_root_view);

        m_vLayout_Description = findViewById(R.id.item_song_description_parent);

        this.m_vTextView_Title = findViewById(R.id.item_song_title_text_view);
        this.m_vTextView_Artist = findViewById(R.id.item_song_artist_text_view);

        this.m_vImageView_Art = findViewById(R.id.item_song_art_image_view);
        this.m_vImageView_Art_Parent = findViewById(R.id.item_song_art_image_view_parent);

        this.m_vImageLoader = () -> {
            AsyncDataLoader.getInstance().onRemove(this);
            Size def_img_size = CacheHelper.getInstance().getSize((m_vViewType == BaseRecyclerViewAdapter.ViewType.GRID) ? CacheHelper.IMAGE_SIZE.MEDIUM : CacheHelper.IMAGE_SIZE.SMALL);

            Glide.with(itemView.getContext())
                    .load(new AudioFileCover(m_vItem.getFilePath()))
                    .set(Downsampler.DECODE_FORMAT, DecodeFormat.PREFER_RGB_565)
                    .onlyRetrieveFromCache(true)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(com.realgear.icons_pack.R.drawable.ic_album_24px)
                    .centerCrop()
                    .dontAnimate()
                    .apply(new RequestOptions()).override(def_img_size.getWidth(), def_img_size.getHeight())
                    .signature(new ObjectKey(m_vItem.getId()))
                    .priority(Priority.IMMEDIATE)
                    .listener(new RequestListener<>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            AsyncDataLoader.getInstance().Dequeue();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            AsyncDataLoader.getInstance().Dequeue();
                            return false;
                        }
                    })
                    .into(m_vImageView_Art);

            m_vUIHandler.removeCallbacks(m_vImageLoader);
        };
    }

    @Override
    public void onInitializeView(BaseRecyclerViewAdapter.ViewType viewType) {
        if (viewType == this.m_vViewType)
            return;

        int paddingSize = ViewsUtil.dp2px(this.itemView.getContext(), R.dimen.item_library_song_padding);
        ConstraintLayout parentLayout = (ConstraintLayout) this.itemView;

        ConstraintLayout.LayoutParams imgLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        imgLayoutParams.startToStart = m_vRootView.getId();
        imgLayoutParams.topToTop = m_vRootView.getId();
        imgLayoutParams.dimensionRatio = "1:1";

        ConstraintLayout.LayoutParams descLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descLayoutParams.bottomToBottom = m_vRootView.getId();

        switch (viewType) {
            case GRID:
                Size def_img_size = CacheHelper.getInstance().getSize(CacheHelper.IMAGE_SIZE.MEDIUM);

                this.m_vRootView.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
                this.m_vLayout_Description.setPadding(0, (int)(paddingSize * 0.75F), 0, 0);

                imgLayoutParams.endToEnd = m_vRootView.getId();
                imgLayoutParams.bottomToTop = R.id.item_song_description_parent;

                descLayoutParams.startToStart = m_vRootView.getId();
                descLayoutParams.topToBottom = m_vImageView_Art_Parent.getId();

                this.m_vImageView_Art_Parent.setLayoutParams(new LinearLayout.LayoutParams(def_img_size.getWidth(), def_img_size.getHeight()));
                this.m_vImageView_Art.setMinimumHeight(def_img_size.getHeight());
                this.m_vImageView_Art.setMinimumWidth(def_img_size.getWidth());

                parentLayout.updateViewLayout(m_vImageView_Art_Parent, imgLayoutParams);
                parentLayout.updateViewLayout(m_vLayout_Description, descLayoutParams);

                break;

            case LIST:
                this.m_vRootView.setPadding(paddingSize, paddingSize, paddingSize, 0);
                this.m_vLayout_Description.setPadding(paddingSize, 0, 0, 0);
                imgLayoutParams.endToStart = R.id.item_song_description_parent;
                imgLayoutParams.bottomToBottom = m_vRootView.getId();

                descLayoutParams.startToEnd = m_vImageView_Art_Parent.getId();
                descLayoutParams.topToTop = m_vRootView.getId();

                int img_size = ViewsUtil.dp2px(itemView.getContext(), R.dimen.item_library_song_art_size);

                this.m_vImageView_Art_Parent.setLayoutParams(new LinearLayout.LayoutParams(img_size, img_size));
                this.m_vImageView_Art.setMinimumHeight(img_size);
                this.m_vImageView_Art.setMinimumWidth(img_size);
                parentLayout.updateViewLayout(m_vImageView_Art_Parent, imgLayoutParams);
                parentLayout.updateViewLayout(m_vLayout_Description, descLayoutParams);

                break;
        }

        this.m_vViewType = viewType;
    }

    private SongRecyclerViewItem m_vItem;

    @Override
    public void onBindViewHolder(BaseRecyclerViewItem viewItem) {
        m_vItem = (SongRecyclerViewItem) viewItem;

        this.m_vTextView_Title.setText(this.m_vItem.getTitle());
        this.m_vTextView_Artist.setText(this.m_vItem.getSong().getArtistName());

        this.m_vImageView_Art.setImageDrawable(null);
    }

    @Override
    public void onBindData() {
        AsyncDataLoader.getInstance().Enqueue(this);
    }

    @Override
    public void onLoadData() {
        if(m_vUIHandler.hasMessages(0)) {
            return;
        }

        m_vUIHandler.post(this.m_vImageLoader);
    }
}
