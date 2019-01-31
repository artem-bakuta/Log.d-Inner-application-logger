package com.artem.bakuta.logger.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.artem.bakuta.logger.model.LogEntity;

import java.util.List;

@Dao
public interface LogEntityDao {

    @Query("SELECT * FROM logentity")
    LiveData<List<LogEntity>> getAll();

    @Query("SELECT * FROM logentity WHERE id = :id")
    LogEntity getById(long id);

    @Query("SELECT * FROM logentity WHERE tag = :tag")
    LiveData<List<LogEntity>> getByTag(String tag);

    @Query("SELECT DISTINCT tag FROM logentity")
    LiveData<List<String>> getAllTags();

    @Insert
    void insert(LogEntity employee);

    @Update
    void update(LogEntity employee);

    @Delete
    void delete(LogEntity employee);

    @Query("DELETE FROM logentity")
    void deleteAllTable();

}