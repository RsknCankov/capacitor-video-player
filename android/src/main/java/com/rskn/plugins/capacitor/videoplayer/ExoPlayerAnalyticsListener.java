package com.rskn.plugins.capacitor.videoplayer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.analytics.AnalyticsListener;

@UnstableApi
public class ExoPlayerAnalyticsListener implements AnalyticsListener {

    private static final String TAG = "ExoPlayerAnalytics";

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
        Log.d(TAG, "Bandwidth estimate: " + bitrateEstimate + " bps");
    }
}
