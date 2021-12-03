package com.fulafula.utils;

import android.content.SharedPreferences;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefUtil {

    public static String PREF_KEY_UUID = "uuid";

    private final SharedPreferences appDataShared;

    @Inject
    public SharedPrefUtil(SharedPreferences appDataShared) {
        this.appDataShared = appDataShared;
    }

    public void setUUID(String uuid) {
        put(appDataShared, PREF_KEY_UUID, "");
    }

    public String getUUID() {
        return get(appDataShared, PREF_KEY_UUID, "");
    }

    private void put(SharedPreferences sp, String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    private void put(SharedPreferences sp, String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    private void put(SharedPreferences sp, String key, Boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    private void put(SharedPreferences sp, String key, BigDecimal value) {
        sp.edit().putFloat(key, value.floatValue()).apply();
    }

    private String get(SharedPreferences sp, String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    private Integer get(SharedPreferences sp, String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    private Boolean get(SharedPreferences sp, String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    private BigDecimal get(SharedPreferences sp, String key, float defaultValue) {
        return BigDecimal.valueOf(sp.getFloat(key, defaultValue));
    }

    private void delete(SharedPreferences sp, String key) {
        sp.edit().remove(key).apply();
    }
}
