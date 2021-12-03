package com.fulafula.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppUtil {

    private final Context mContext;

    @Inject
    public AppUtil(Context context) {
        this.mContext = context;
    }

    // Fixed font size is not affected by the system
    public static Context configFontScale(Context context) {

        if (context != null) {
            Resources res = context.getResources();
            Configuration config = res.getConfiguration();
            DisplayMetrics displayMetrics = res.getDisplayMetrics();

            config.fontScale = 1f;
            displayMetrics.scaledDensity = displayMetrics.density * config.fontScale;

            return context.createConfigurationContext(config);
        }

        return null;
    }

    public String generateUserAgent() {
        return WebSettings.getDefaultUserAgent(mContext);
    }
}
