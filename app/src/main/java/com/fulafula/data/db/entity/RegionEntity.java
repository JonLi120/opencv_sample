package com.fulafula.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "region_table",
        indices = @Index(value = {"county_code"}),
        foreignKeys = @ForeignKey(
                entity = CountyEntity.class,
                parentColumns = "code",
                childColumns = "county_code",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ))
public class RegionEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id = 0;

    @NonNull
    @ColumnInfo(name = "county_code")
    public final String countyCode;

    @NonNull
    @ColumnInfo(name = "region_name")
    public final String regionName;

    public RegionEntity(int id, @NonNull String countyCode, @NonNull String regionName) {
        this.id = id;
        this.countyCode = countyCode;
        this.regionName = regionName;
    }

}
