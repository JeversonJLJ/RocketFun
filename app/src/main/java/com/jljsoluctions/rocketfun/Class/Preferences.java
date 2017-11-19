package com.jljsoluctions.rocketfun.Class;

import android.content.Context;



/**
 * Created by Telecon on 18/09/2015.
 */
public class Preferences {
    Properties properties;


    public Preferences(Context ctx)
    {
        properties =new Properties(ctx);
    }


    public boolean getUpdateNewsWifiConnected() throws Exception {
        return properties.Read("chk_pref_update_news_when_start_only_wifi", false);
    }
    public boolean getUpdateTrackerWifiConnected() throws Exception {
        return properties.Read("chk_pref_update_tracker_when_start_only_wifi", false);
    }




}
