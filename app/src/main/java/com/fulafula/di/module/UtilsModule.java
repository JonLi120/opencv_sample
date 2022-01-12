package com.fulafula.di.module;

import android.content.Context;

import com.fulafula.Constants;
import com.fulafula.utils.AppUtil;
import com.fulafula.utils.ImageUtil;
import com.fulafula.utils.SharedPrefUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UtilsModule {

    @Provides
    @Singleton
    SharedPrefUtil provideSharedPreferences(Context context) {
        return new SharedPrefUtil(
                context.getSharedPreferences(Constants.PREF_NAME_APP_DATA, Context.MODE_PRIVATE)
        );
    }

    @Provides
    @Singleton
    AppUtil provideAppUtil(Context context) {
        return new AppUtil(context);
    }

    @Provides
    @Singleton
    ImageUtil provideImageUtil(Context context) {
        return new ImageUtil(context);
    }
}
