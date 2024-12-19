package com.rskn.plugins.capacitor.videoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SeekParameters;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.SubtitleView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


@UnstableApi
class VideoPlayerView extends RelativeLayout implements TextureView.SurfaceTextureListener {

    private final String TAG = "VideoPlayerView";

    TextureView mTextureView;
    SurfaceTexture mSurface;
    private ExoPlayer exoPlayer;
    private String playingUrl;
    private final SubtitleView subtitleView;
    private final AtomicBoolean isFFprobeRunning = new AtomicBoolean(false);
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);
    private Thread ffprobeAnalyzeThread;
    private Thread ffmpegAudioDecoding;

    VideoPlayerView(Context context) {
        this(context, false);
    }

    VideoPlayerView(Context context, boolean enableOpacity) {
        super(context);
        Log.println(Log.INFO, TAG, "Preview NOT Opacity");
        mTextureView = new TextureView(context);
        addView(mTextureView);
        mTextureView.setSurfaceTextureListener(this);

        if (mTextureView.isAvailable()) {
            mSurface = mTextureView.getSurfaceTexture();
            assert mSurface != null;
            onSurfaceTextureAvailable(mSurface, mTextureView.getWidth(), mTextureView.getHeight());
        }

        // Initialize and configure SubtitleView
        subtitleView = new SubtitleView(context);
        RelativeLayout.LayoutParams subtitleLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        subtitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        subtitleView.setLayoutParams(subtitleLayoutParams);
        subtitleView.setPadding(16, 16, 16, 16); // Padding for readability
        subtitleView.setApplyEmbeddedStyles(true); // Optional: apply styles embedded in the subtitles

        addView(subtitleView);
        requestLayout();

        // SurfaceHolder callback
//        mHolder = mTextureView.getHolder();
//        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Config.enableLogCallback(message -> {
            Log.d("FFmpeg", message.getText());
        });

        Config.enableStatisticsCallback(statistics -> {
            Log.d("FFmpeg Stats", "Video Frame: " + statistics.getVideoFrameNumber() +
                    ", Time: " + statistics.getTime());
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the video view instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            int width = r - l;
            int height = b - t;


            int nW;
            int nH;
            int top;
            int left;

            float scale = 1.0f;

            // Center the child SurfaceView within the parent.
            if (width * height < height * width) {
                Log.d(TAG, "center horizontally");
                int scaledChildWidth = (int) (((float) (width * height) / height) * scale);
                nW = (width + scaledChildWidth) / 2;
                nH = (int) (height * scale);
                top = 0;
                left = (width - scaledChildWidth) / 2;
            } else {
                Log.d(TAG, "center vertically");
                int scaledChildHeight = (int) (((float) (height * width) / width) * scale);
                nW = (int) (width * scale);
                nH = (height + scaledChildHeight) / 2;
                top = (height - scaledChildHeight) / 2;
                left = 0;
            }
            child.layout(left, top, nW, nH);

            Log.d("layout", "left:" + left);
            Log.d("layout", "top:" + top);
            Log.d("layout", "right:" + nW);
            Log.d("layout", "bottom:" + nH);
        }
    }

    public void setVideoUrl(String url) {
        this.playingUrl = url;
        // Prepare ExoPlayer
        if (exoPlayer != null) {

            // Safely interrupt FFmpeg and FFprobe if already running
            stopFFmpegIfRunning();
            cancelFFprobeIfRunning();

            // Supported Android audio codecs
            String[] supportedCodecs = {"aac", "opus", "mp3", "vorbis", "flac", "pcm"};
            AtomicBoolean requiresDecoding = new AtomicBoolean(false);

            // Analyze stream with FFprobe asynchronously
            analyzeStreamWithFFprobe(url, supportedCodecs, requiresDecoding, () -> {
                // Callback: Runs when FFprobe analysis is complete
                if (requiresDecoding.get()) {
                    // Start FFmpeg to decode audio and stream it
                    this.playingUrl = startFFmpegForAudioDecoding(url);
                }

                // Run ExoPlayer preparation on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // Post new url if only needed
                    if (playingUrl.contains("http://127.0.0.1")) {
                        prepareExoPlayer(this.playingUrl);
                    }
                });
            });

            prepareExoPlayer(this.playingUrl);
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


    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mSurface = surface;
        initializePlayer(mTextureView);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (exoPlayer != null) {
            playerDestroy();
        }
        return true; // Return true to release the SurfaceTexture.
    }
    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
    }

    private void initializePlayer(TextureView surface) {
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        2500,
                        5000,
                        1000,
                        1500
                ).build();

        exoPlayer = new ExoPlayer.Builder(getContext())
                .setRenderersFactory(new DefaultRenderersFactory(getContext())
                        .setEnableDecoderFallback(true))
                .setLoadControl(loadControl)
                .build();
        // Set the Surface for video output
        Surface playerSurface = new Surface(surface.getSurfaceTexture());
        exoPlayer.setVideoSurface(playerSurface);
        if (playingUrl != null && !playingUrl.isEmpty()) {
            MediaItem mediaItem = MediaItem.fromUri(playingUrl);
            exoPlayer.setMediaItem(mediaItem);
            // Prepare and start the player
            exoPlayer.prepare();
            exoPlayer.play();
        }
        exoPlayer.addListener(new ExoPlayerListener(subtitleView, exoPlayer));

        TrackSelectionParameters trackSelectionParams = exoPlayer.getTrackSelectionParameters();
        TrackSelectionParameters updatedTrackSelectionParams = trackSelectionParams.buildUpon()
                .setPreferredAudioLanguage("bg")
                .setPreferredTextLanguage("bg")
                .setMaxAudioBitrate(Integer.MAX_VALUE)
                .setPreferredAudioLanguages("bg", "en")
                .build();
        exoPlayer.setTrackSelectionParameters(updatedTrackSelectionParams);
        exoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC);

    }

    private void cancelFFprobeIfRunning() {
        if (ffprobeAnalyzeThread != null && ffprobeAnalyzeThread.isAlive()) {
            Log.d("FFprobe", "Cancelling FFprobe analysis...");
            isCancelled.set(true); // Signal cancellation
            try {
                ffprobeAnalyzeThread.join(); // Wait for thread to terminate
                Log.d("FFprobe", "FFprobe thread successfully cancelled.");
            } catch (InterruptedException e) {
                Log.e("FFprobe", "Error while cancelling FFprobe thread", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    // Helper method: Interrupt FFmpeg thread if running
    private void stopFFmpegIfRunning() {
        if (ffmpegAudioDecoding != null && ffmpegAudioDecoding.isAlive()) {
            FFmpeg.cancel();
            try {
                ffmpegAudioDecoding.join(); // Wait for thread to terminate
                Log.d("FFmpeg", "Previous FFmpeg process terminated.");
            } catch (InterruptedException e) {
                Log.e("FFmpeg", "Error while stopping FFmpeg thread.", e);
            }
        }
    }

    // Helper method: Analyze stream with FFprobe
    private void analyzeStreamWithFFprobe(String url, String[] supportedCodecs, AtomicBoolean requiresDecoding, Runnable onComplete) {
        // Cancel any ongoing analysis
        cancelFFprobeIfRunning();

        // Reset the state flags
        isFFprobeRunning.set(true);
        isCancelled.set(false);

        // Start FFprobe in a new thread
        ffprobeAnalyzeThread = new Thread(() -> {
            Log.d("FFprobe", "Starting FFprobe analysis for URL: " + url);

            Config.enableLogCallback(message -> {
                if (isCancelled.get()) {
                    Log.d("FFprobe", "FFprobe analysis cancelled.");
                    return; // Stop processing logs if cancelled
                }

                if (message.getText().contains("Audio:")) {
                    String audioLine = message.getText();
                    Log.d("StreamCheck", "Audio Line: " + audioLine);

                    String detectedCodec = extractAudioCodec(audioLine);
                    Log.d("StreamCheck", "Detected Audio Codec: " + detectedCodec);

                    boolean isSupported = Arrays.stream(supportedCodecs)
                            .anyMatch(audioLine::contains);

                    if (!isSupported) {
                        requiresDecoding.set(true);
                        Log.d("StreamCheck", "Unsupported audio codec detected: " + detectedCodec);
                    } else {
                        Log.d("StreamCheck", "Supported audio codec: " + detectedCodec);
                    }
                }
            });

            // Execute FFprobe (check for cancellation)
            if (!isCancelled.get()) {
                int rc = FFprobe.execute(String.format("-i %s", url));
                if (rc == Config.RETURN_CODE_SUCCESS) {
                    Log.d("FFprobe", "Stream analysis completed.");
                } else {
                    Log.e("FFprobe", "FFprobe failed with code: " + rc);
                }
            }

            // Clean up after execution
            isFFprobeRunning.set(false);
            if (!isCancelled.get()) {
                onComplete.run(); // Trigger callback if not cancelled
            }
        });
        ffprobeAnalyzeThread.start();
    }

    // Helper method: Start FFmpeg for audio decoding
    private String startFFmpegForAudioDecoding(String url) {
        String httpUrl = "http://127.0.0.1:8080";
        String ffmpegCommand = buildFFmpegCommand(url, httpUrl);

        ffmpegAudioDecoding = new Thread(() -> {
            int rc = FFmpeg.execute(ffmpegCommand);
            if (rc == Config.RETURN_CODE_SUCCESS) {
                Log.d("FFmpeg", "FFmpeg HTTP streaming started successfully.");
            } else {
                Log.e("FFmpeg", "FFmpeg HTTP streaming failed with code: " + rc);
            }
        });
        ffmpegAudioDecoding.start();

        Log.d("FFmpeg", "FFmpeg HTTP server started at: " + httpUrl);
        return httpUrl;
    }

    private String buildFFmpegCommand(String inputUrl, String outputUrl) {
        return String.format("-i %s -c:v copy -c:a aac -strict experimental -f mpegts -listen 1 %s",
                inputUrl, outputUrl);
    }

    // Helper method: Extract the audio codec from FFprobe log
    private String extractAudioCodec(String audioLine) {
        String[] parts = audioLine.split("Audio:");
        if (parts.length > 1) {
            return parts[1].split(",")[0].trim().split(" ")[0];
        }
        return "unknown";
    }

    // Helper method: Prepare ExoPlayer with the given URL
    private void prepareExoPlayer(String url) {
        playerReset();
        // Create a custom DataSource.Factory for HTTP requests
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();

        // DefaultMediaSourceFactory auto-detects content type (HLS, MPEG-TS, MP4, etc.)
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);

        // Create the MediaItem
        MediaItem mediaItem = MediaItem.fromUri(url);

        // Create a MediaSource from the MediaItem (content type auto-detected)
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaItem);

        // Set the new MediaSource
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        exoPlayer.seekTo(0);
        Log.d("ExoPlayer", "ExoPlayer started with URL: " + url);
    }

    private void playerReset() {
        exoPlayer.stop();
        exoPlayer.clearMediaItems();
        exoPlayer.seekTo(0);
    }

    private void playerDestroy() {
        playerStop();
        exoPlayer.release();
        exoPlayer = null;
    }
}
