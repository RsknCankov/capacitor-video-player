package com.rskn.plugins.capacitor.videoplayer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.Metadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.text.Cue;
import androidx.media3.common.text.CueGroup;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.extractor.metadata.emsg.EventMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UnstableApi
public class ExoPlayerListener implements Player.Listener {

    private final String TAG = "ExoPlayerListener";

    public @OptIn(markerClass = UnstableApi.class) ExoPlayerListener(ExoPlayer exoPlayer) {
        Log.d(TAG, "ExoPlayerListener initialized.");
    }

    @Override
    public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
        Player.Listener.super.onEvents(player, events);
        Log.d(TAG, "onEvents triggered: " + events);

        Map<String, Object> info = new HashMap<>(1);
        info.put("player_events", events);
        PlayerEventsDispatcher.defaultCenter().postNotification("player_events", info);
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        Player.Listener.super.onPlaybackStateChanged(playbackState);

        String stateDescription = switch (playbackState) {
            case Player.STATE_IDLE -> "IDLE";
            case Player.STATE_BUFFERING -> "BUFFERING";
            case Player.STATE_READY -> "READY";
            case Player.STATE_ENDED -> "ENDED";
            default -> "UNKNOWN";
        };

        Log.d(TAG, "Playback state changed: " + stateDescription);

        PlayerEventsDispatcher.defaultCenter().postNotification(stateDescription, null);
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        Player.Listener.super.onIsPlayingChanged(isPlaying);
        Log.d(TAG, "Is playing changed: " + isPlaying);

        HashMap<String, Object> info = new HashMap<>(1);
        info.put(PlayerEventTypes.IS_PLAYING.name(), isPlaying);
        PlayerEventsDispatcher.defaultCenter().postNotification(PlayerEventTypes.IS_PLAYING.name(), info);
    }

    @Override
    public void onTracksChanged(@NonNull Tracks tracks) {
        Player.Listener.super.onTracksChanged(tracks);
        Log.d(TAG, "Tracks changed. Total groups: " + tracks.getGroups().size());

        List<String> subtitleStreams = new ArrayList<>();
        for (int i = 0; i < tracks.getGroups().size(); i++) {
            Tracks.Group trackGroup = tracks.getGroups().get(i);
            if (trackGroup.getType() == C.TRACK_TYPE_TEXT) {
                for (int j = 0; j < trackGroup.length; j++) {
                    if (trackGroup.isTrackSupported(j)) {
                        Format format = trackGroup.getTrackFormat(j);
                        if (format.sampleMimeType != null && format.sampleMimeType.startsWith("image/")) {
                            subtitleStreams.add(format.language);
                        } else {
                            subtitleStreams.add(format.language);
                        }
                    }
                }
            }
        }

        Map<String, Object> info = new HashMap<>();
        info.put("subtitle_streams", new JSONArray(subtitleStreams).toString());
        PlayerEventsDispatcher.defaultCenter().postNotification(PlayerEventTypes.SUBTITLES_STREAMS.name(), info);
    }

    @Override
    public void onCues(CueGroup cueGroup) {
        Log.d(TAG, "Cues received. Total cues: " + cueGroup.cues.size());

        for (Cue cue : cueGroup.cues) {
            if (cue.text != null) {
                Log.d(TAG, "Cue Text: " + cue.text.toString());
            } else if (cue.bitmap != null) {
                Log.d(TAG, "Bitmap Cue: Width=" + cue.bitmap.getWidth() + ", Height=" + cue.bitmap.getHeight());
            } else {
                Log.d(TAG, "Cue: No text or bitmap available.");
            }
        }
    }

@Override
public void onTrackSelectionParametersChanged(@NonNull TrackSelectionParameters parameters) {
    Player.Listener.super.onTrackSelectionParametersChanged(parameters);
    Log.d(TAG, "Track selection parameters changed: " + parameters.preferredTextLanguages);
    String language = parameters.preferredTextLanguages.isEmpty() ? null : parameters.preferredTextLanguages.get(0);
    Map<String, Object> info = new HashMap<>();
    info.put("language", language);
    PlayerEventsDispatcher.defaultCenter().postNotification(PlayerEventTypes.SELECTED_SUBTITLES_STREAM.name(), info);
}

    @Override
    public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition,
                                        @NonNull Player.PositionInfo newPosition,
                                        int reason) {
        Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);

        long oldPositionMs = oldPosition.positionMs;
        long newPositionMs = newPosition.positionMs;
        long jumpMs = newPositionMs - oldPositionMs;

        String reasonStr = switch (reason) {
            case Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> "AUTO_TRANSITION";
            case Player.DISCONTINUITY_REASON_SEEK -> "SEEK";
            case Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT -> "SEEK_ADJUSTMENT";
            case Player.DISCONTINUITY_REASON_SKIP -> "SKIP";
            case Player.DISCONTINUITY_REASON_REMOVE -> "REMOVE";
            case Player.DISCONTINUITY_REASON_INTERNAL -> "INTERNAL";
            default -> "UNKNOWN";
        };

        Log.d(TAG, "Position discontinuity: " + oldPositionMs + "ms -> " + newPositionMs +
                "ms (jump: " + jumpMs + "ms, reason: " + reasonStr + ")");

        // Detect significant gaps that may indicate buffer holes or missing segments
        if (Math.abs(jumpMs) > 5000 && reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
            Log.w(TAG, "Large automatic position jump detected: " + jumpMs +
                    "ms - possible buffer gap or missing segment");

            // Send analytics event for gap detection
            Map<String, Object> info = new HashMap<>();
            info.put("old_position_ms", oldPositionMs);
            info.put("new_position_ms", newPositionMs);
            info.put("jump_size_ms", jumpMs);
            info.put("reason", reasonStr);
            PlayerEventsDispatcher.defaultCenter().postNotification("BUFFER_GAP_DETECTED", info);
        }
    }

    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        ExoPlaybackException exoError = (ExoPlaybackException) error;
        @Nullable Throwable cause = error.getCause();

        // Enhanced error classification
        String errorType;
        String errorPhase;
        String errorSeverity;
        boolean recoverable = true;

        switch (exoError.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                errorType = "SOURCE_ERROR";
                errorPhase = "loading";
                errorSeverity = "warning";
                // Network/manifest errors - often recoverable with retry
                Log.e(TAG, "Source error (network/manifest): " + error.getMessage(), cause);
                break;

            case ExoPlaybackException.TYPE_RENDERER:
                errorType = "DECODER_ERROR";
                errorPhase = "decode";
                errorSeverity = "error";
                // Codec/rendering errors - may need player fallback
                Log.e(TAG, "Renderer error (codec/decode): " + error.getMessage(), cause);
                break;

            case ExoPlaybackException.TYPE_UNEXPECTED:
                errorType = "UNEXPECTED_ERROR";
                errorPhase = "unknown";
                errorSeverity = "error";
                recoverable = false;
                Log.e(TAG, "Unexpected error: " + error.getMessage(), cause);
                break;

            case ExoPlaybackException.TYPE_REMOTE:
                errorType = "REMOTE_ERROR";
                errorPhase = "remote";
                errorSeverity = "warning";
                Log.e(TAG, "Remote component error: " + error.getMessage(), cause);
                break;

            default:
                errorType = "UNKNOWN_ERROR";
                errorPhase = "unknown";
                errorSeverity = "error";
                Log.e(TAG, "Unknown error type: " + error.getMessage(), cause);
        }

        // Create detailed error information
        JSONObject errorInfo = new JSONObject();
        Map<String, Object> info = new HashMap<>();
        try {
            errorInfo.put("error_message", error.getMessage());
            errorInfo.put("error_type", errorType);
            errorInfo.put("error_phase", errorPhase);
            errorInfo.put("error_severity", errorSeverity);
            errorInfo.put("recoverable", recoverable);
            errorInfo.put("player_type", "EXOPLAYER");
            errorInfo.put("error_code", exoError.type);

            if (cause != null) {
                errorInfo.put("error_cause", cause.getMessage());
                errorInfo.put("error_cause_class", cause.getClass().getSimpleName());
            }

            // Add renderer-specific information for decode errors
            if (exoError.type == ExoPlaybackException.TYPE_RENDERER) {
                errorInfo.put("renderer_index", exoError.rendererIndex);
                if (exoError.rendererFormat != null) {
                    errorInfo.put("renderer_format", exoError.rendererFormat.toString());
                }
            }

            info.put(PlayerEventTypes.ERROR.name(), errorInfo);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating error JSON", e);
            info.put("error_message", exoError.getMessage());
            info.put("error_cause", exoError.getCause());
        } finally {
            PlayerEventsDispatcher.defaultCenter().postNotification(PlayerEventTypes.ERROR.name(), info);
            Player.Listener.super.onPlayerError(error);
        }
    }
}
