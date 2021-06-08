package com.Redis.entity;
import io.netty.channel.ChannelPromiseAggregator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public enum SecondKillTime {
    STARTTIME(2021,5,8,10,4,0),
    ENDTIME(2021,5,8,11,30,00);
    private final Calendar cal;
    private SecondKillTime(Integer a0,Integer a1,Integer a2,Integer a3,Integer a4,Integer a5){
        this.cal=Calendar.getInstance();
        this.cal.set(a0,a1,a2,a3,a4,a5);
    }
    public Calendar getCalendar(){
        return cal;
    }
}

class Test{
    //大于等于为1 小于为-1
    public static void main(String[] args) {
        Calendar cal=Calendar.getInstance();
        cal.setTime(new Date());
        //System.out.println(SecondKillTime.ENDTIME.getCalendar().compareTo(SecondKillTime.STARTTIME.getCalendar()));
        //System.out.println(SecondKillTime.STARTTIME.getCalendar().compareTo(SecondKillTime.ENDTIME.getCalendar()));
        //System.out.println(cal.compareTo(SecondKillTime.STARTTIME.getCalendar()));
        //System.out.println(cal.compareTo(SecondKillTime.ENDTIME.getCalendar()));
        System.out.println(cal.get(Calendar.YEAR));
        System.out.println(cal.get(Calendar.MONTH));
        System.out.println(cal.get(Calendar.DATE));
        System.out.println(cal.get(Calendar.HOUR_OF_DAY));
        System.out.println(cal.get(Calendar.MINUTE));
        System.out.println(cal.get(Calendar.SECOND));
    }
}
