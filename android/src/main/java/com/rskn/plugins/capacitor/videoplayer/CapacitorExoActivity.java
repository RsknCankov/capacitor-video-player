package com.rskn.plugins.capacitor.videoplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;

@UnstableApi
public class CapacitorExoActivity extends Fragment {

    private static final String TAG = "CapacitorExoActivity";

    public FrameLayout frameContainerLayout;
    private VideoPlayerManager videoPlayerView;
    private View view;


    public boolean transparentView;

    // important
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
                    view.findViewById(R.id.frame_container);
            frameContainerLayout.setLayoutParams(layoutParams);

            //video view
            videoPlayerView = new VideoPlayerManager(getContext(), view.findViewById(R.id.video_view), this);
            frameContainerLayout.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

}
