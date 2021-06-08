package com.Redis.entity;
import io.netty.channel.ChannelPromiseAggregator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public enum SecondKillTime {
    STARTTIME(2021,5,8,10,4,0),
    ENDTIME(2021,5,8,18,2,50);
    private final Calendar cal;
    private SecondKillTime(Integer a0,Integer a1,Integer a2,Integer a3,Integer a4,Integer a5){
        this.cal=Calendar.getInstance();
        this.cal.set(a0,a1,a2,a3,a4,a5);
    }
    public Calendar getCalendar(){
        return cal;
    }
}


