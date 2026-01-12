package com.rskn.plugins.capacitor.videoplayer;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Timeline;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.common.C;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SeekParameters;
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParser;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.PlayerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


@UnstableApi
class VideoPlayerManager implements DefaultLifecycleObserver {

    private final String TAG = "VideoPlayerManager";
    private ExoPlayer exoPlayer;
    private final Context context;
    private final PlayerView playerView;
    private Timer timer;
    private TimerTask timerTask;

    // Add an AtomicBoolean to manage the lock
    private final AtomicBoolean isPlayerReleased = new AtomicBoolean(false);
    private final Handler releaseHandler = new Handler(Looper.getMainLooper());

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
        if (exoPlayer != null && language != null) {
            TrackSelectionParameters.Builder parametersBuilder = exoPlayer.getTrackSelectionParameters().buildUpon();
            parametersBuilder.setPreferredTextLanguage(language);
            exoPlayer.setTrackSelectionParameters(parametersBuilder.build());
            Log.d(TAG, "Subtitle stream selected: " + language);
        }
        if (exoPlayer != null && language == null) {
            TrackSelectionParameters.Builder parametersBuilder = exoPlayer.getTrackSelectionParameters().buildUpon();
            parametersBuilder.setPreferredTextLanguage(null);
            exoPlayer.setTrackSelectionParameters(parametersBuilder.build());
            Log.d(TAG, "Subtitle stream selected: " + language);
        }
    }

    private boolean isTimeshiftStream(String url) {
        return url != null && url.contains("timeshift");
    }

    private DefaultLoadControl createTimeshiftLoadControl() {
        Log.d(TAG, "Creating TIMESHIFT LoadControl configuration");
        return new DefaultLoadControl.Builder()
                // Larger buffers for recorded content with potential gaps
                .setBufferDurationsMs(
                        10000,  // minBufferMs: 10s (increased from 2.5s)
                        60000,  // maxBufferMs: 60s (increased from 5s)
                        2500,   // bufferForPlaybackMs: 2.5s
                        5000    // bufferForPlaybackAfterRebufferMs: 5s
                )
                // Prioritize time over size for better gap handling
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();
    }

    private DefaultLoadControl createLiveLoadControl() {
        Log.d(TAG, "Creating LIVE LoadControl configuration");
        return new DefaultLoadControl.Builder()
                // Balanced buffers for live streams
                .setBufferDurationsMs(
                        5000,   // minBufferMs: 5s (increased from 2.5s)
                        45000,  // maxBufferMs: 45s (increased from 5s)
                        2500,   // bufferForPlaybackMs: 2.5s
                        5000    // bufferForPlaybackAfterRebufferMs: 5s
                )
                .build();
    }

    private DataSource.Factory createDataSourceFactory() {
        // Create HTTP data source with retry and timeout configuration
        DefaultHttpDataSource.Factory httpDataSourceFactory =
                new DefaultHttpDataSource.Factory()
                        .setConnectTimeoutMs(30000)  // 30s connect timeout
                        .setReadTimeoutMs(30000)     // 30s read timeout
                        .setAllowCrossProtocolRedirects(true);

        return new DefaultDataSource.Factory(context, httpDataSourceFactory);
    }

    private void initializePlayer(Context context, PlayerView playerView) {
        // Use live config as default (will be updated when URL is set)
        DefaultLoadControl loadControl = createLiveLoadControl();

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

        // Use EXACT seek parameters for better accuracy with gaps
        // CLOSEST_SYNC can skip over small gaps but may miss target position
        exoPlayer.setSeekParameters(SeekParameters.EXACT);

        exoPlayer.addAnalyticsListener(new ExoPlayerAnalyticsListener());

        playerView.setUseController(false);
        playerView.setShutterBackgroundColor(Color.TRANSPARENT);
        playerView.setPlayer(exoPlayer);
    }

    public long getCurrentTime() {
        if (exoPlayer != null) {
            return exoPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekForward(){
        if (exoPlayer != null) {
            exoPlayer.seekForward();
        }
    }

    public void seekBackward(){
        if (exoPlayer != null) {
            exoPlayer.seekBack();
        }
    }

    public void seekStart() {
        if (exoPlayer != null) {
            exoPlayer.seekTo(0);
        }
    }

    public void seekEnd() {
        if (exoPlayer != null) {
            long duration = exoPlayer.getDuration();
            if (duration != C.TIME_UNSET && duration > 0) {
                // Seek to the end of the video (duration - 1 to avoid going past the end)
                exoPlayer.seekTo(Math.max(0, duration - 1));
            }
        }
    }

    public long getDuration() {
        if (exoPlayer != null) {
            long duration = exoPlayer.getDuration();
            return duration == C.TIME_UNSET ? 0 : duration;
        }
        return 0;
    }

    public long getBuffered() {
        if (exoPlayer != null) {
            long buffered = exoPlayer.getBufferedPosition();
            return buffered == C.TIME_UNSET ? 0 : buffered;
        }
        return 0;
    }

    public long[] getBufferedRanges() {
        if (exoPlayer != null) {
            Timeline timeline = exoPlayer.getCurrentTimeline();
            if (!timeline.isEmpty()) {
                Timeline.Window window = new Timeline.Window();
                timeline.getWindow(0, window);
                
                // Get buffered ranges from the player
                long bufferedPosition = exoPlayer.getBufferedPosition();
                if (bufferedPosition != C.TIME_UNSET && bufferedPosition > 0) {
                    // For now, return a single range from 0 to buffered position
                    // In a more sophisticated implementation, you could get actual ranges from the media source
                    return new long[]{0, bufferedPosition};
                }
            }
        }
        return new long[0];
    }

    public void startCurrentTimeUpdates() {
            if (timer == null) {
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
//                        long currentTime = getCurrentTime();
//                        Map<String, Object> info = new HashMap<>(1);
//                        info.put("currentTime", currentTime);
//                        PlayerEventsDispatcher.defaultCenter().postNotification(PlayerEventTypes.PLAYER_TIME_UPDATE.name(), info);
                    }
                };
                timer.scheduleAtFixedRate(timerTask, 0, 1000); // Schedule task every second
            }
        }

        public void stopCurrentTimeUpdates() {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }

    // Helper method: Prepare ExoPlayer with the given URL
    private void prepareExoPlayer(String url) {
        boolean isTimeshift = isTimeshiftStream(url);
        String streamType = isTimeshift ? "TIMESHIFT" : "LIVE";

        // Reinitialize player if stream type changed or player doesn't exist
        // This ensures we use the correct LoadControl configuration
        if (exoPlayer == null) {
            Log.d(TAG, "Initializing player for " + streamType + " stream");
            initializePlayerForStream(context, playerView, url);
        }

        exoPlayer.clearMediaItems();
        exoPlayer.seekTo(0);

        // Use enhanced data source factory with retry configuration
        DataSource.Factory dataSourceFactory = createDataSourceFactory();
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);
        MediaItem mediaItem = MediaItem.fromUri(url);

        Log.d(TAG, "Preparing " + streamType + " stream: " + url);

        if (url.contains(".m3u8")) {
            extractHlsPlayListParams(url);
        }

        MediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaItem);
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();

        Log.d(TAG, "ExoPlayer prepared with " + streamType + " configuration");
    }

    // Initialize player with stream-specific configuration
    private void initializePlayerForStream(Context context, PlayerView playerView, String url) {
        boolean isTimeshift = isTimeshiftStream(url);
        DefaultLoadControl loadControl = isTimeshift
                ? createTimeshiftLoadControl()
                : createLiveLoadControl();

        Log.d(TAG, "Initializing player with " + (isTimeshift ? "TIMESHIFT" : "LIVE") + " LoadControl");

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
                .setMaxVideoSize(Integer.MAX_VALUE, Integer.MAX_VALUE)
                .setForceHighestSupportedBitrate(true)
                .setPreferredAudioLanguages("bg", "en")
                .build();

        exoPlayer.addListener(new ExoPlayerListener(exoPlayer));
        exoPlayer.setTrackSelectionParameters(updatedTrackSelectionParams);

        // Use EXACT seek parameters for better accuracy with gaps
        exoPlayer.setSeekParameters(SeekParameters.EXACT);

        exoPlayer.addAnalyticsListener(new ExoPlayerAnalyticsListener());

        playerView.setUseController(false);
        playerView.setShutterBackgroundColor(Color.TRANSPARENT);
        playerView.setPlayer(exoPlayer);
    }

    private void extractHlsPlayListParams(String streamURL) {
        new Thread(() -> {
            try {
                URL url = new URL(streamURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                Uri uri = Uri.parse(url.toString());
                Object playlist = new HlsPlaylistParser().parse(uri, inputStream);

                if (playlist instanceof HlsMultivariantPlaylist multivariantPlaylist) {
                    if (!multivariantPlaylist.variants.isEmpty()) {
                        Uri mediaPlaylistUri = multivariantPlaylist.variants.get(0).url;
                        connection = (HttpURLConnection) new URL(mediaPlaylistUri.toString()).openConnection();
                        inputStream = connection.getInputStream();
                        playlist = new HlsPlaylistParser().parse(mediaPlaylistUri, inputStream);
                    }
                }

                if (playlist instanceof HlsMediaPlaylist mediaPlaylist) {
                    if (mediaPlaylist.hasProgramDateTime) {
                        final long programDateTimeMs = mediaPlaylist.startTimeUs / 1000;
//                        System.out.println("EXT-X-PROGRAM-DATE-TIME: " + programDateTimeMs);
                        PlayerEventsDispatcher.defaultCenter().postNotification(
                                PlayerEventTypes.HLS_X_PROGRAM_DATE_TIME.name(),
                                Collections.singletonMap("playlist_datetime", programDateTimeMs)
                        );
                    }
                    final long segmentsDuration = mediaPlaylist.targetDurationUs / 1000;
//                    System.out.println("EXT-X-TARGET-DURATION: " + segmentsDuration);
                    PlayerEventsDispatcher.defaultCenter().postNotification(
                            PlayerEventTypes.HLS_TARGET_DURATION.name(),
                            Collections.singletonMap("target_duration", segmentsDuration)
                    );
                }
                // TODO handle properly
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
        startCurrentTimeUpdates();
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        Log.d(TAG, "onPause called");
        stopCurrentTimeUpdates();
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
                final ExoPlayer playerToRelease = exoPlayer;
                exoPlayer = null;  // Immediately null out the reference

                // Post release to background thread with error handling
                new Thread(() -> {
                    try {
                        Log.d(TAG, "Starting player release on background thread...");

                        // Stop first (quick operation)
                        try {
                            playerToRelease.stop();
                            Log.d(TAG, "Player stopped successfully");
                        } catch (Exception e) {
                            Log.w(TAG, "Error stopping player (continuing with release)", e);
                        }

                        // Release with timeout monitoring
                        final long startTime = System.currentTimeMillis();
                        final AtomicBoolean releaseCompleted = new AtomicBoolean(false);

                        // Start release operation
                        Thread releaseThread = new Thread(() -> {
                            try {
                                playerToRelease.release();
                                releaseCompleted.set(true);
                                Log.d(TAG, "Player released successfully in " + (System.currentTimeMillis() - startTime) + "ms");
                            } catch (Exception e) {
                                Log.e(TAG, "Error during player release", e);
                            }
                        });
                        releaseThread.start();

                        // Wait max 5 seconds for release to complete
                        try {
                            releaseThread.join(5000);
                        } catch (InterruptedException e) {
                            Log.w(TAG, "Release thread interrupted", e);
                        }

                        if (!releaseCompleted.get()) {
                            Log.w(TAG, "Player release timed out after 5s, but continuing anyway");
                            // Don't throw error to Sentry - this is expected on slow devices
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Unexpected error in releasePlayer", e);
                    } finally {
                        // Reset lock for next initialization
                        releaseHandler.post(() -> {
                            isPlayerReleased.set(false);
                            Log.d(TAG, "Release lock reset, player can be re-initialized");
                        });
                    }
                }).start();
            }
        }
    }
}
