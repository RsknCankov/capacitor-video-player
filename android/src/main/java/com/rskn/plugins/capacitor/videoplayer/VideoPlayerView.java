package com.rskn.plugins.capacitor.videoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.SubtitleView;


@UnstableApi class VideoPlayerView extends RelativeLayout implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    private final String TAG = "VideoPlayerView";

    CustomSurfaceView mSurfaceView;
    CustomTextureView mTextureView;
    SurfaceHolder mHolder;
    SurfaceTexture mSurface;
    private ExoPlayer exoPlayer;
    private SubtitleView subtitleView;
    private String playingUrl;

    VideoPlayerView(Context context) {
        this(context, false);
    }

    VideoPlayerView(Context context, boolean enableOpacity) {
        super(context);
        Log.println(Log.INFO, TAG, "Preview NOT Opacity");

        // Initialize SurfaceView
        mSurfaceView = new CustomSurfaceView(context);

        // Initialize and configure SubtitleView
        subtitleView = new SubtitleView(context);
        RelativeLayout.LayoutParams subtitleLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        subtitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        subtitleView.setLayoutParams(subtitleLayoutParams);
        subtitleView.setPadding(16, 16, 16, 16); // Padding for readability
        subtitleView.setApplyEmbeddedStyles(true); // Optional: apply styles embedded in the subtitles

        addView(mSurfaceView);
        addView(subtitleView);
        requestLayout();

        // SurfaceHolder callback
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
        // Prepare the media item using the provided URL
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
            MediaItem mediaItem = MediaItem.fromUri(url);
            exoPlayer.setMediaItem(mediaItem);
            // Prepare and start the player
            exoPlayer.prepare();
        }
    }

    public void playerPlay(){
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

    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // The Surface has been created, tell it where
        // to draw.
        try {
            // prepare video player
            Surface surfaceForVideo = holder.getSurface();
            initializePlayer(surfaceForVideo);
        } catch (Exception exception) {
            Log.e(TAG, "Exception caused by setPreviewDisplay()", exception);
        }
    }

    private void initializePlayer(Surface surface) {
        exoPlayer = new ExoPlayer.Builder(getContext()).build();
        // Set the Surface for video output
        exoPlayer.setVideoSurface(surface);
        if (playingUrl != null && !playingUrl.isEmpty()) {
            MediaItem mediaItem = MediaItem.fromUri(playingUrl);
            exoPlayer.setMediaItem(mediaItem);
            // Prepare and start the player
            exoPlayer.prepare();
            exoPlayer.play();
        }
        exoPlayer.addListener(new ExoPlayerListener(subtitleView));
        TrackSelectionParameters trackSelectionParams = exoPlayer.getTrackSelectionParameters();
        TrackSelectionParameters updatedTrackSelectionParams = trackSelectionParams.buildUpon()
                .setPreferredAudioLanguage("bg")
                .setPreferredTextLanguage("bg")
                .setPreferredAudioLanguages("bg", "en")
                .build();
        exoPlayer.setTrackSelectionParameters(updatedTrackSelectionParams);
    }

    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    }


    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int w, int h) {
    }

    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            mSurface = surface;

        } catch (Exception exception) {
            Log.e(TAG, "Exception caused by onSurfaceTextureAvailable()", exception);
        }
    }

    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {

        return true;
    }


    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
    }

}
