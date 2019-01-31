package com.artem.bakuta.logger.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LogEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String tag;

    public String message;

    public String throwableError;

    public String date;

    //0 = i; 1 = d, 2 = w; 3 = e;
    public int type;

}