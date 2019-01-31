package com.artem.bakuta.logger;


import com.artem.bakuta.logger.model.LogEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    @SuppressWarnings("unused")
    public static void i(final String tag, final String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag, String.valueOf(msg));

            if (LogConfig.getInstance().LOGGER_ENABLE)
                LogConfig.getInstance().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        LogConfig.getInstance().getDatabase().logEntityDao().insert(createLogEntity(tag, String.valueOf(msg), null, 0));
                    }
                });
        }
    }

    @SuppressWarnings("unused")
    public static void d(final String tag, final String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, String.valueOf(msg));

            if (LogConfig.getInstance().LOGGER_ENABLE)
                LogConfig.getInstance().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        LogConfig.getInstance().getDatabase().logEntityDao().insert(createLogEntity(tag, String.valueOf(msg), null, 1));
                    }
                });


        }
    }

    public static void w(final String tag, final String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.w(tag, String.valueOf(msg));

            if (LogConfig.getInstance().LOGGER_ENABLE)
                LogConfig.getInstance().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        LogConfig.getInstance().getDatabase().logEntityDao().insert(createLogEntity(tag, String.valueOf(msg), null, 2));
                    }
                });
        }
    }

    public static void e(final String tag, final String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, msg);

            if (LogConfig.getInstance().LOGGER_ENABLE)
                LogConfig.getInstance().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        LogConfig.getInstance().getDatabase().logEntityDao().insert(createLogEntity(tag, String.valueOf(msg), null, 3));
                    }
                });

        }
    }


    public static void e(final String tag, final String msg, final Throwable t) {
        if (BuildConfig.DEBUG) {

            android.util.Log.e(tag, String.valueOf(msg), t);
            t.printStackTrace();

            if (LogConfig.getInstance().LOGGER_ENABLE)
                LogConfig.getInstance().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        LogConfig.getInstance().getDatabase().logEntityDao().insert(createLogEntity(tag, String.valueOf(msg), t, 3));
                    }
                });

        }
    }

    public static LogEntity createLogEntity(String tag, String message, Throwable t, int type) {
        LogEntity logEntity = new LogEntity();
        logEntity.tag = tag;
        logEntity.message = message;
        logEntity.throwableError = t != null ? t.getMessage() : "";
        logEntity.type = type;
        logEntity.date = new SimpleDateFormat(LogConfig.getInstance().DATE_FORMAT).format(new Date());
        return logEntity;
    }
}
