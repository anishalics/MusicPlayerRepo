package com.realgear.samplemusicplayertest.threads;

import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.realgear.mediaplayer.LibraryManager;
import com.realgear.multislidinguppanel.MultiSlidingPanelAdapter;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;
import com.realgear.multislidinguppanel.PanelStateListener;
import com.realgear.samplemusicplayertest.MainActivity;
import com.realgear.samplemusicplayertest.R;
import com.realgear.samplemusicplayertest.views.panels.RootMediaPlayerPanel;
import com.realgear.samplemusicplayertest.views.panels.RootNavigationBarPanel;

import java.util.ArrayList;
import java.util.List;

public class UIThread {
    private static UIThread instance;

    private final MainActivity m_vMainActivity;

    private MultiSlidingUpPanelLayout m_vMultiSlidingPanel;

    private MediaPlayerThread m_vMediaPlayerThread;

    private boolean m_vCanUpdatePanelsUI;

    public UIThread(MainActivity activity) {
        instance = this;

        this.m_vMainActivity = activity;
        onCreate();

        LibraryManager.initLibrary(activity.getApplicationContext());

        this.m_vMediaPlayerThread = new MediaPlayerThread(activity, getCallback());
        this.m_vMediaPlayerThread.onStart();
    }

    public MediaController.Callback getCallback() {
        return new MediaController.Callback() {
            @Override
            public void onSessionDestroyed() {
                super.onSessionDestroyed();
            }

            @Override
            public void onSessionEvent(@NonNull String event, @Nullable Bundle extras) {
                super.onSessionEvent(event, extras);
            }

            @Override
            public void onPlaybackStateChanged(@Nullable PlaybackState state) {
                super.onPlaybackStateChanged(state);

                UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onPlaybackStateChanged(state);
            }

            @Override
            public void onMetadataChanged(@Nullable MediaMetadata metadata) {
                super.onMetadataChanged(metadata);

                if (metadata != null)
                    UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateMetadata(metadata);
            }

            @Override
            public void onQueueChanged(@Nullable List<MediaSession.QueueItem> queue) {
                super.onQueueChanged(queue);
            }

            @Override
            public void onQueueTitleChanged(@Nullable CharSequence title) {
                super.onQueueTitleChanged(title);
            }

            @Override
            public void onExtrasChanged(@Nullable Bundle extras) {
                super.onExtrasChanged(extras);
            }

            @Override
            public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
                super.onAudioInfoChanged(info);
            }
        };
    }

    public static UIThread getInstance() { return instance; }

    public MediaPlayerThread getMediaPlayerThread() {
        return this.m_vMediaPlayerThread;
    }

    public void onCreate() {
        this.m_vMultiSlidingPanel = findViewById(R.id.root_sliding_up_panel);

        List<Class<?>> items = new ArrayList<>();

        items.add(RootMediaPlayerPanel.class);
        items.add(RootNavigationBarPanel.class);

        this.m_vMultiSlidingPanel.setAdapter(new MultiSlidingPanelAdapter(this.m_vMainActivity, items));
    }

    public void setEnablePanelUpdateUI(boolean enable) {
        this.m_vCanUpdatePanelsUI = enable;
    }

    public boolean canUpdatePanelUI() {
        return this.m_vCanUpdatePanelsUI;
    }

    public <T extends android.view.View> T findViewById(@IdRes int id) {
        return this.m_vMainActivity.findViewById(id);
    }
}
