package com.fulafula.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "county_table")
public class CountyEntity {

    @NonNull
    @ColumnInfo(name = "code")
    @PrimaryKey
    public final String code;

    @NonNull
    @ColumnInfo(name = "county_name")
    public final String countyName;

    public CountyEntity(@NonNull String code, @NonNull String countyName) {
        this.code = code;
        this.countyName = countyName;
    }
}
