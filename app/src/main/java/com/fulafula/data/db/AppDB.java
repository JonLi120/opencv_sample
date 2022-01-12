package com.fulafula.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.fulafula.data.db.dao.AddressDao;
import com.fulafula.data.db.entity.CountyEntity;
import com.fulafula.data.db.entity.RegionEntity;

@Database(entities = {CountyEntity.class, RegionEntity.class}, version = 1, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    public static final String DATABASE_NAME = "fula_db";

    public abstract AddressDao addressDao();
}
