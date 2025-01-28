package com.rskn.plugins.capacitor.videoplayer;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.util.UnstableApi;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "CapacitorVideoPlayer")
public class CapacitorVideoPlayerPlugin extends Plugin {

    private final JSObject ret = new JSObject();
    private final Float[] rateList = {0.25f, 0.5f, 0.75f, 1f, 2f, 4f};
    private static final String TAG = "CapacitorVideoPlayer";

    private CapacitorExoActivity exoActivity;
    private int containerViewId = 20;


    @Override
    public void load() {
        super.load();
        subscribeForPlayerEvents();
    }

    @PluginMethod
    public void setVideoUrl(PluginCall call) {
        bridge.getActivity().runOnUiThread(() -> exoActivity.setVideoUrl(call.getString("url")));
    }

    @PluginMethod
    public void playerPlay(PluginCall call){
        bridge.getActivity().runOnUiThread(() -> exoActivity.play());
    }

    @PluginMethod
    public void playerPause(PluginCall call) {
        bridge.getActivity().runOnUiThread(() -> {
            exoActivity.pausePlayer();
        });

    }

    @PluginMethod
    public void selectSubtitleStream(PluginCall call) {
        String language = call.getString("language");
        bridge.getActivity().runOnUiThread(() -> {
            if (exoActivity != null) {
                exoActivity.selectSubtitleStream(language);
                call.resolve();
            } else {
                call.reject("Player not initialized");
            }
        });
    }

    @PluginMethod
    public void initPlayer(PluginCall call) {
        final Integer x = call.getInt("x", 0);
        final Integer y = call.getInt("y", 0);
        final Integer width = call.getInt("width", 0);
        final Integer height = call.getInt("height", 0);
        final Integer paddingBottom = call.getInt("paddingBottom", 0);


        bridge
                .getActivity()
                .runOnUiThread(
                        () -> {
                            DisplayMetrics metrics = getBridge().getActivity().getResources().getDisplayMetrics();

                            // offset
                            int computedX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, metrics);
                            int computedY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y, metrics);

                            // size
                            int computedWidth;
                            int computedHeight;
                            int computedPaddingBottom;

                            if (paddingBottom != 0) {
                                computedPaddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingBottom, metrics);
                            } else {
                                computedPaddingBottom = 0;
                            }

                            if (width != 0) {
                                computedWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, metrics);
                            } else {
                                Display defaultDisplay = getBridge().getActivity().getWindowManager().getDefaultDisplay();
                                final Point size = new Point();
                                defaultDisplay.getSize(size);

                                computedWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size.x, metrics);
                            }

                            if (height != 0) {
                                computedHeight =
                                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, metrics) - computedPaddingBottom;
                            } else {
                                Display defaultDisplay = getBridge().getActivity().getWindowManager().getDefaultDisplay();
                                final Point size = new Point();
                                defaultDisplay.getSize(size);

                                computedHeight =
                                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size.y, metrics) - computedPaddingBottom;
                            }

                            exoActivity = new CapacitorExoActivity();
                            exoActivity.transparentView = false;

                            exoActivity.setRect(computedX, computedY, computedWidth, computedHeight);

                            FrameLayout containerView = getBridge().getActivity().findViewById(containerViewId);
                            if (containerView == null) {
                                containerView = new FrameLayout(getActivity().getApplicationContext());
                                containerView.setId(containerViewId);

                                getBridge().getWebView().setBackgroundColor(Color.TRANSPARENT);
                                ((ViewGroup) getBridge().getWebView().getParent()).addView(containerView);
                                getBridge().getWebView().getParent().bringChildToFront(getBridge().getWebView());
//                                if (transparentView == true) {
//                                    getBridge().getWebView().getParent().bringChildToFront(getBridge().getWebView());
//                                }

                                FragmentManager fragmentManager = getBridge().getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.add(containerView.getId(), exoActivity);
                                fragmentTransaction.commitNow();

                                bridge.saveCall(call);

                                call.resolve();
                            } else {
                                call.reject("video player already started");
                            }
                        }
                );
    }

    @PluginMethod
    public void playerStop(final PluginCall call) {
        bridge
                .getActivity()
                .runOnUiThread(
                        () -> {
                            FrameLayout containerView = getBridge().getActivity().findViewById(containerViewId);

                            if (containerView != null) {
                                ((ViewGroup) getBridge().getWebView().getParent()).removeView(containerView);
                                getBridge().getWebView().setBackgroundColor(Color.BLACK);
                                FragmentManager fragmentManager = getBridge().getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.remove(exoActivity);
                                fragmentTransaction.commitAllowingStateLoss();
                                exoActivity = null;
                                call.resolve();
                            } else {
                                call.reject("player already stopped");
                            }
                        }
                );
    }

    @PluginMethod
    public void removeAllListeners(final PluginCall call) {
        PlayerEventsDispatcher.defaultCenter().removeAllNotifications();
        PlayerEventsDispatcher.defaultCenter().removeAllMethodsForNotification();
        call.resolve();
    }

    private Boolean isInRate(Float[] arr, Float rate) {
        boolean ret = false;
        for (Float el : arr) {
            if (el.equals(rate)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void subscribeForPlayerEvents() {

        // IDLE status of Exo
        PlayerEventsDispatcher.defaultCenter().addMethodForNotification(PlayerEventTypes.IDLE.name(), new PlayerEventRunnable() {
            @Override
            public void run() {
                JSObject data = new JSObject();
                data.put("idle", "true");
                notifyListeners("CapVideoPlayerIdle", data);
            }
        });

        PlayerEventRunnable playerReadyRunnable = new PlayerEventRunnable() {
            @Override
            public void run() {
                JSObject data = new JSObject();
                data.put("ready", "true");
                notifyListeners("CapVideoPlayerReady", data);
            }
        };

        PlayerEventsDispatcher.defaultCenter().addMethodForNotification(PlayerEventTypes.READY.name(), playerReadyRunnable);

        PlayerEventsDispatcher.defaultCenter().addMethodForNotification(PlayerEventTypes.BUFFERING.name(), new PlayerEventRunnable() {
            @Override
            public void run() {
                JSObject data = new JSObject();
                data.put("buffering", "true");
                notifyListeners("CapVideoPlayerBuffering", data);
            }
        });

        PlayerEventsDispatcher.defaultCenter().addMethodForNotification(PlayerEventTypes.END.name(), new PlayerEventRunnable() {
            @Override
            public void run() {
                JSObject data = new JSObject();
                data.put("end", "true");
                notifyListeners("CapVideoPlayerEnd", data);
            }
        });

        PlayerEventsDispatcher.defaultCenter().addMethodForNotification(PlayerEventTypes.IS_PLAYING.name(), new PlayerEventRunnable() {
            @Override
            public void run() {
                JSObject data = new JSObject();
                data.put("isPlaying", getInfo().get(PlayerEventTypes.IS_PLAYING.name()));
                notifyListeners("CapVideoPlayerPlaying", data);
            }
        });

        PlayerEventsDispatcher.defaultCenter().addMethodForNotification(PlayerEventTypes.ERROR.name(), new PlayerEventRunnable() {
            @Override
            public void run() {
                JSObject data = new JSObject();
                data.put("error", getInfo().get(PlayerEventTypes.ERROR.name()));
                notifyListeners("CapVideoPlayerError", data);
            }
        });

        PlayerEventsDispatcher.defaultCenter().addMethodForNotification(PlayerEventTypes.SUBTITLES_STREAMS.name(), new PlayerEventRunnable() {
            @Override
            public void run() {
                JSObject data = new JSObject();
                data.put("subtitleStreams", getInfo().get("subtitle_streams"));
                notifyListeners("CapVideoPlayerSubtitleStreams", data);
            }
        });
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
    }

}
