package com.rskn.plugins.capacitor.videoplayer;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerEventsDispatcher {
    private static PlayerEventsDispatcher _instance;

    private final HashMap<String, ArrayList<PlayerEventRunnable>> registeredObjects;

    private PlayerEventsDispatcher() {
        registeredObjects = new HashMap<String, ArrayList<PlayerEventRunnable>>();
    }

    public static synchronized PlayerEventsDispatcher defaultCenter() {
        if (_instance == null) _instance = new PlayerEventsDispatcher();
        return _instance;
    }

    public synchronized void addMethodForNotification(String notificationName, PlayerEventRunnable r) {
        ArrayList<PlayerEventRunnable> list = registeredObjects.get(notificationName);
        if (list == null) {
            list = new ArrayList<PlayerEventRunnable>();
            registeredObjects.put(notificationName, list);
        }
        list.add(r);
    }

    public synchronized void removeMethodForNotification(String notificationName, PlayerEventRunnable r) {
        ArrayList<PlayerEventRunnable> list = registeredObjects.get(notificationName);
        if (list != null) {
            list.remove(r);
        }
    }

    public synchronized void removeAllNotifications() {
        for (Iterator<Map.Entry<String, ArrayList<PlayerEventRunnable>>> entry = registeredObjects.entrySet().iterator(); entry.hasNext(); ) {
            Map.Entry<String, ArrayList<PlayerEventRunnable>> e = entry.next();
            String key = e.getKey();
            ArrayList<PlayerEventRunnable> value = e.getValue();
            removeMethodForNotification(key, value.get(0));
            entry.remove();
        }
    }

    public synchronized void postNotification(String notificationName, Map<String, Object> _info) {
        ArrayList<PlayerEventRunnable> list = registeredObjects.get(notificationName);
        Log.d("PlayerEventsDispatcher", "Posting notification for " + notificationName);
        if (list != null) {
            for (PlayerEventRunnable r : new ArrayList<>(list)) {
                r.run(_info);
            }
        }
    }
}
