package com.rskn.plugins.capacitor.videoplayer;

import android.util.Log;

import java.util.Map;

public class PlayerEventRunnable implements Runnable {

    private Map<String, Object> info;

    public Map<String, Object> getInfo() {
        return this.info;
    }

    public void setInfo(Map<String, Object> _info) {
        this.info = _info;
        if (_info != null) {
            Log.d("PlayerEventRunnable", "Info set: " + _info.toString());
        } else {
            Log.d("PlayerEventRunnable", "Info is null: ");
        }
    }

    @Override
    public void run() {
        if (info != null) {
            Log.d("PlayerEventRunnable", "Running with info: " + info.toString());
        } else {
            Log.d("PlayerEventRunnable", "Running with no info");
        }
    }

    public void run(Map<String, Object> info) {
        setInfo(info);
        run();
    }
}
