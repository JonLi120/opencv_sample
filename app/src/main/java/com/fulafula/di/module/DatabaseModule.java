package com.fulafula.di.module;

import android.content.Context;

import androidx.room.Room;

import com.fulafula.data.db.AppDB;
import com.fulafula.data.db.dao.AddressDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    AppDB provideDB(Context context) {
        return Room.databaseBuilder(context, AppDB.class, AppDB.DATABASE_NAME)
                .build();
    }

    @Provides
    @Singleton
    AddressDao provideAddressDao(AppDB db) {
        return db.addressDao();
    }
}
