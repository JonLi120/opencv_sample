package com.fulafula.data.model.vo;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fulafula.data.db.entity.CountyEntity;
import com.fulafula.data.db.entity.RegionEntity;

import java.util.List;

public class AddressVO {

    @Embedded
    public CountyEntity county;

    @Relation(
            parentColumn = "code",
            entityColumn = "county_code"
    )
    public List<RegionEntity> regions;
}
