package com.fulafula.di.module;

import com.fulafula.BuildConfig;
import com.fulafula.repository.FulaService;
import com.fulafula.utils.OkHttpUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_DOMAIN)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideLogInterceptors() {
        return new HttpLoggingInterceptor(Timber::i)
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor logInterceptor) {
        return OkHttpUtil.createOkHttpClient(logInterceptor);
    }

    @Provides
    @Singleton
    FulaService provideFulaService(Retrofit retrofit) {
        return retrofit.create(FulaService.class);
    }
}
