package com.rskn.plugins.capacitor.videoplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

public class CapacitorExoActivity extends Fragment {

    private static final String TAG = "CapacitorExoActivity";

    public FrameLayout mainLayout;
    public FrameLayout frameContainerLayout;
    private VideoPlayerView videoPlayerView;
    private View view;


    public boolean transparentView;

    // important
//    private float opacity;
    public int width;
    public int height;
    public int x;
    public int y;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(getResources().getIdentifier("video_player_activity", "layout", requireActivity().getPackageName()), container, false);
        attachPlayerView();
        return view;
    }

    public void setRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private void attachPlayerView() {
        if (videoPlayerView == null) {
            //set box position and size
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
            layoutParams.setMargins(x, y, 0, 0);
            frameContainerLayout =
                     (FrameLayout) view.findViewById(R.id.frame_container);
            frameContainerLayout.setLayoutParams(layoutParams);

            //video view
            videoPlayerView = new VideoPlayerView(getActivity(), false);
            mainLayout = (FrameLayout) view.findViewById(R.id.video_view);
            mainLayout.setLayoutParams(
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            );
            mainLayout.addView(videoPlayerView);
            mainLayout.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final FrameLayout frameContainerLayout = (FrameLayout) view.findViewById(
                getResources().getIdentifier("frame_container", "id", requireActivity().getPackageName())
        );

        ViewTreeObserver viewTreeObserver = frameContainerLayout.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            frameContainerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            frameContainerLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            Activity activity = getActivity();
                            if (isAdded() && activity != null) {
                                final RelativeLayout frameCamContainerLayout = (RelativeLayout) view.findViewById(
                                        getResources().getIdentifier("frame_camera_cont", "id", getActivity().getPackageName())
                                );

                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                        frameContainerLayout.getWidth(),
                                        frameContainerLayout.getHeight()
                                );
                                frameCamContainerLayout.setLayoutParams(layoutParams);
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setVideoUrl(String url) {
        if (videoPlayerView != null) {
            videoPlayerView.setVideoUrl(url);
        }
    }

    public void play() {
        if (videoPlayerView != null) {
            videoPlayerView.playerPlay();
        }
    }

    public void pausePlayer() {
        videoPlayerView.playerPause();
    }

//    /**
//     * Determine the space between the first two fingers
//     */
//    private static float getFingerSpacing(MotionEvent event) {
//        // ...
//        float x = event.getX(0) - event.getX(1);
//        float y = event.getY(0) - event.getY(1);
//        return (float) Math.sqrt(x * x + y * y);
//    }
}
