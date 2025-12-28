package com.rskn.plugins.capacitor.videoplayer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.exoplayer.source.LoadEventInfo;
import androidx.media3.exoplayer.source.MediaLoadData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@UnstableApi
public class ExoPlayerAnalyticsListener implements AnalyticsListener {

    private static final String TAG = "ExoPlayerAnalytics";
    private static final int SIGNIFICANT_FRAME_DROP_THRESHOLD = 10;
    private static final long SLOW_LOAD_THRESHOLD_MS = 5000;

    @Override
    public void onPlaybackStateChanged(@NonNull EventTime eventTime, int state) {
        String playbackState = switch (state) {
            case Player.STATE_IDLE -> "IDLE";
            case Player.STATE_BUFFERING -> "BUFFERING";
            case Player.STATE_READY -> "READY";
            case Player.STATE_ENDED -> "ENDED";
            default -> "UNKNOWN";
        };
        Log.d(TAG, "Playback state changed: " + playbackState);
    }

    @Override
    public void onBandwidthEstimate(@NonNull EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
        Log.d(TAG, "Bandwidth estimate: " + bitrateEstimate + " bps (loaded " + totalBytesLoaded + " bytes in " + totalLoadTimeMs + "ms)");
    }

    @Override
    public void onDroppedVideoFrames(@NonNull EventTime eventTime, int droppedFrames, long elapsedMs) {
        if (droppedFrames > SIGNIFICANT_FRAME_DROP_THRESHOLD) {
            Log.w(TAG, "Significant frame drops detected: " + droppedFrames + " frames in " + elapsedMs + "ms - potential gap or performance issue");

            // Send event notification for monitoring
            Map<String, Object> info = new HashMap<>();
            info.put("dropped_frames", droppedFrames);
            info.put("elapsed_ms", elapsedMs);
            info.put("frames_per_second", droppedFrames / (elapsedMs / 1000.0));
            PlayerEventsDispatcher.defaultCenter().postNotification("DROPPED_FRAMES", info);
        }
    }

    @Override
    public void onLoadCompleted(@NonNull EventTime eventTime,
                                @NonNull LoadEventInfo loadEventInfo,
                                @NonNull MediaLoadData mediaLoadData) {
        long loadDurationMs = loadEventInfo.loadDurationMs;
        long bytesLoaded = loadEventInfo.bytesLoaded;

        if (loadDurationMs > SLOW_LOAD_THRESHOLD_MS) {
            Log.w(TAG, "Slow segment load detected: " + loadDurationMs + "ms for " +
                    bytesLoaded + " bytes (" + (bytesLoaded / 1024) + " KB)");

            // Send event notification for slow loads
            Map<String, Object> info = new HashMap<>();
            info.put("load_duration_ms", loadDurationMs);
            info.put("bytes_loaded", bytesLoaded);
            info.put("url", loadEventInfo.uri.toString());
            PlayerEventsDispatcher.defaultCenter().postNotification("SLOW_SEGMENT_LOAD", info);
        } else {
            Log.d(TAG, "Segment loaded: " + bytesLoaded + " bytes in " + loadDurationMs + "ms");
        }
    }

    @Override
    public void onLoadError(@NonNull EventTime eventTime,
                           @NonNull LoadEventInfo loadEventInfo,
                           @NonNull MediaLoadData mediaLoadData,
                           @NonNull IOException error,
                           boolean wasCanceled) {
        if (!wasCanceled) {
            Log.e(TAG, "Load error: " + error.getMessage() + " for URL: " + loadEventInfo.uri);

            // Send error event for tracking
            Map<String, Object> info = new HashMap<>();
            info.put("error_message", error.getMessage());
            info.put("url", loadEventInfo.uri.toString());
            info.put("data_type", mediaLoadData.dataType);
            PlayerEventsDispatcher.defaultCenter().postNotification("LOAD_ERROR", info);
        }
    }

    @Override
    public void onAudioUnderrun(@NonNull EventTime eventTime,
                                int bufferSize,
                                long bufferSizeMs,
                                long elapsedSinceLastFeedMs) {
        Log.w(TAG, "Audio underrun detected - buffer starved (buffer: " + bufferSizeMs +
                "ms, elapsed since last feed: " + elapsedSinceLastFeedMs + "ms)");

        // Send event for potential buffer stall/gap
        Map<String, Object> info = new HashMap<>();
        info.put("buffer_size_ms", bufferSizeMs);
        info.put("elapsed_since_last_feed_ms", elapsedSinceLastFeedMs);
        PlayerEventsDispatcher.defaultCenter().postNotification("AUDIO_UNDERRUN", info);
    }

    @Override
    public void onVideoSizeChanged(@NonNull EventTime eventTime,
                                   @NonNull androidx.media3.common.VideoSize videoSize) {
        Log.d(TAG, "Video size changed: " + videoSize.width + "x" + videoSize.height +
                " (pixel ratio: " + videoSize.pixelWidthHeightRatio + ")");
    }

    @Override
    public void onRenderedFirstFrame(@NonNull EventTime eventTime,
                                     @NonNull Object output,
                                     long renderTimeMs) {
        Log.d(TAG, "First frame rendered (took " + renderTimeMs + "ms)");

        Map<String, Object> info = new HashMap<>();
        info.put("render_time_ms", renderTimeMs);
        PlayerEventsDispatcher.defaultCenter().postNotification("FIRST_FRAME_RENDERED", info);
    }
}
