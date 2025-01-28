package com.rskn.plugins.capacitor.videoplayer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.text.Cue;
import androidx.media3.common.text.CueGroup;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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

        for (int i = 0; i < tracks.getGroups().size(); i++) {
            Tracks.Group trackGroup = tracks.getGroups().get(i);
            Log.d(TAG, "Track Group Type: " + trackGroup.getType() + ", Group Index: " + i);

            if (trackGroup.getType() == C.TRACK_TYPE_TEXT) {
                for (int j = 0; j < trackGroup.length; j++) {
                    if (trackGroup.isTrackSupported(j) && trackGroup.isTrackSelected(j)) {
                        Log.d(TAG, "Subtitle track selected: Language=" + trackGroup.getTrackFormat(j).language);
                    }
                }
            }

            for (int j = 0; j < trackGroup.length; j++) {
                if (trackGroup.isTrackSupported(j)) {
                    Format format = trackGroup.getTrackFormat(j);
                    Log.d(TAG, "Supported track format: " + format.sampleMimeType +
                            ", Bitrate: " + format.bitrate +
                            ", Language: " + format.language);
                }
            }
        }
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
        Log.d(TAG, "Track selection parameters changed: " + parameters.toString());
    }

    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        ExoPlaybackException parsedError = (ExoPlaybackException) error;
        @Nullable Throwable cause = error.getCause();

        Log.e(TAG, "Player error occurred: " + error.getMessage(), cause);

        JSONObject errorInfo = new JSONObject();
        Map<String, Object> info = new HashMap<>();
        try {
            errorInfo.put("error_message", error.getMessage());
            if (cause != null) {
                errorInfo.put("error_cause", cause.getMessage());
            }
            info.put(PlayerEventTypes.ERROR.name(), errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
            info.put("error_message", parsedError.getMessage());
            info.put("error_cause", parsedError.getCause());
        } finally {
            PlayerEventsDispatcher.defaultCenter().postNotification(PlayerEventTypes.ERROR.name(), info);
            Player.Listener.super.onPlayerError(error);
        }
    }
}
