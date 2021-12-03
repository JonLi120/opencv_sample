package com.fulafula.utils;

import com.fulafula.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtil {

    public static OkHttpClient createOkHttpClient(HttpLoggingInterceptor logInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
}
