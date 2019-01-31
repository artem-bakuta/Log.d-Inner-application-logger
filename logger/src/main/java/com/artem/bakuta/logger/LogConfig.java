package com.artem.bakuta.logger;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.artem.bakuta.logger.service.LoggerService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class LogConfig {
    public boolean LOGGER_ENABLE;
    public String DATABASE_NAME = "QaLogger";
    public String DATE_FORMAT = "HH:mm:ss dd.MM.YYYY";
    public static final int DB_VERSION = 3;
    private static LogConfig instance;
    private Context context;
    private AppDatabase db;

    private ExecutorService backgroundExecutor;


    public static synchronized LogConfig getInstance() {
        if (instance == null) {
            instance = new LogConfig();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();

        backgroundExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                Thread thread = new Thread(runnable, "Background executor service");
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    /**
     * Submits request to be executed in background.
     *
     * @param runnable
     */
    public void runInBackground(final Runnable runnable) {
        backgroundExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    Log.e(Thread.currentThread().getName(), "Background work exception.", e);
                }
            }
        });
    }

    public void enableLogger() {
        LOGGER_ENABLE = true;
        context.startService(new Intent(context, LoggerService.class));
    }

    public void disableLogger() {
        LOGGER_ENABLE = false;
        context.stopService(new Intent(context, LoggerService.class));
    }


    public void clearDatabase() {
        db.logEntityDao().deleteAllTable();
    }

    public AppDatabase getDatabase() {
        return db;
    }
}
