package com.fulafula.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.fulafula.data.db.entity.CountyEntity;
import com.fulafula.data.model.vo.AddressVO;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCounties(CountyEntity... entities);

    @Transaction
    @Query("SELECT * FROM county_table")
    public Single<List<AddressVO>> getAddressData();
}
