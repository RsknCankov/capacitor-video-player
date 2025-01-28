package com.rskn.plugins.capacitor.videoplayer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.util.Clock;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SeekParameters;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.PlayerView;

import java.util.concurrent.atomic.AtomicBoolean;


@UnstableApi
class VideoPlayerManager implements DefaultLifecycleObserver {

    private final String TAG = "VideoPlayerManager";
    private ExoPlayer exoPlayer;
    private final Context context;
    private final PlayerView playerView;

    // Add an AtomicBoolean to manage the lock
    private final AtomicBoolean isPlayerReleased = new AtomicBoolean(false);

    VideoPlayerManager(Context context, PlayerView playerView, LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
        this.initializePlayer(context, playerView);
        this.context = context;
        this.playerView = playerView;
    }

    public void setVideoUrl(String url) {
        if (exoPlayer != null) {
            prepareExoPlayer(url);
        }
    }

    public void playerPlay() {
        if (!exoPlayer.isPlaying()) {
            exoPlayer.play();
        }
    }

    public void playerStop() {
        exoPlayer.stop();
    }

    public void playerPause() {
        exoPlayer.pause();
    }

    public void selectSubtitleStream(String language) {
        if (exoPlayer != null) {
            TrackSelectionParameters.Builder parametersBuilder = exoPlayer.getTrackSelectionParameters().buildUpon();
            parametersBuilder.setPreferredTextLanguage(language);
            exoPlayer.setTrackSelectionParameters(parametersBuilder.build());
            Log.d(TAG, "Subtitle stream selected: " + language);
        }
    }

    private void initializePlayer(Context context, PlayerView playerView) {
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        2500,
                        5000,
                        1000,
                        1500
                ).build();

        exoPlayer = new ExoPlayer.Builder(context)
                .setRenderersFactory(
                        new DefaultRenderersFactory(context)
                                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                                .setEnableDecoderFallback(true)
                )
                .setLoadControl(loadControl)
                .build();

        TrackSelectionParameters trackSelectionParams = exoPlayer.getTrackSelectionParameters();
        TrackSelectionParameters updatedTrackSelectionParams = trackSelectionParams.buildUpon()
                .setPreferredAudioLanguage("bg")
                .setPreferredTextLanguage("bg")
                .setMaxAudioBitrate(Integer.MAX_VALUE)
                .setMaxVideoSize(Integer.MAX_VALUE, Integer.MAX_VALUE) // No limit on resolution
                .setForceHighestSupportedBitrate(true)
                .setPreferredAudioLanguages("bg", "en")
                .build();

        exoPlayer.addListener(new ExoPlayerListener(exoPlayer));
        exoPlayer.setTrackSelectionParameters(updatedTrackSelectionParams);
        exoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC);

        exoPlayer.addAnalyticsListener(new ExoPlayerAnalyticsListener());

        playerView.setUseController(false);
        playerView.setShutterBackgroundColor(Color.TRANSPARENT);
        playerView.setPlayer(exoPlayer);
    }

    // Helper method: Prepare ExoPlayer with the given URL
    private void prepareExoPlayer(String url) {
        exoPlayer.clearMediaItems();
        exoPlayer.seekTo(0);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);
        MediaItem mediaItem = MediaItem.fromUri(url);
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaItem);
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        Log.d(TAG, "ExoPlayer started with URL: " + url);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onStart called");
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onResume called");
        if (exoPlayer == null) {
            initializePlayer(context, playerView);
        }
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        Log.d(TAG, "onPause called");
        releasePlayer();
    }

    @Override
    public void onStop(LifecycleOwner owner) {
        Log.d(TAG, "onStop called");
        releasePlayer();
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        Log.d(TAG, "onDestroy called");
        releasePlayer();
        owner.getLifecycle().removeObserver(this);
    }

    private void releasePlayer() {
        // Check and set the lock to ensure releasePlayer is called only once
        if (isPlayerReleased.compareAndSet(false, true)) {
            if (exoPlayer != null) {
                exoPlayer.stop();
                exoPlayer.release();
                exoPlayer = null;
                Log.d(TAG, "Player released");
            }
        }
    }
}
