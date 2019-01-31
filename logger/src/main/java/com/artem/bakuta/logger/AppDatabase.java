package com.artem.bakuta.logger;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.artem.bakuta.logger.dao.LogEntityDao;
import com.artem.bakuta.logger.model.LogEntity;

import static com.artem.bakuta.logger.LogConfig.DB_VERSION;

@Database(entities = {LogEntity.class}, version = DB_VERSION, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LogEntityDao logEntityDao();
}