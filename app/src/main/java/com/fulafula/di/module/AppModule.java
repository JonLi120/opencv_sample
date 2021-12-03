package com.fulafula.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {
        NetworkModule.class,
        UtilsModule.class
})
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

//    @Provides
//    @Singleton
//    Application provideApplication(Application application) {
//        return application;
//    }
}
