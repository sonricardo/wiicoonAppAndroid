package com.wiicoon.rubi.wicoon_ligh_controller.models_realm;

import com.wiicoon.rubi.wicoon_ligh_controller.app.MyApplication;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rubi on 10/31/2017.
 */

public class Schedule extends RealmObject {

    public static final int TYPE_TURN_OFF = 0;
    public static final int TYPE_TURN_ON = 1;

    public static final int REPEAT_ALWAYS = 0;
    public static final int REPEAT_ONCE = 1;

    @PrimaryKey
    private int ID;
    private int hour;
    private int minute;
    private int days;
    private int typeTurn;
    private int IdFoco;
    private int idPlaca;
    private boolean isActivate;
    private int lifeCycle;
    private Date dateOfCreation;

   // private Date dateOfCreation;

    public Schedule() {}

    public Schedule( int hour, int minute, int days, int idFoco, int idPlaca,int typeTurn, boolean isActivate, Date dateOfCreation, int lifeCycle) {

        this.ID = MyApplication.ScheduleID.incrementAndGet();
        this.hour = hour;
        this.minute = minute;
        this.days = days;
        IdFoco = idFoco;
        this.idPlaca = idPlaca;
        this.isActivate = isActivate;
        this.typeTurn = typeTurn;
        this.dateOfCreation = dateOfCreation;
        this.lifeCycle = lifeCycle;
    }

    public int getID() {
        return ID;
    }

    public static int getTypeTurnOff() {
        return TYPE_TURN_OFF;
    }

    public int getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(int lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }


    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getTypeTurn() {
        return typeTurn;
    }

    public void setTypeTurn(int typeTurn) {
        this.typeTurn = typeTurn;
    }

    public int getIdFoco() {
        return IdFoco;
    }

    public void setIdFoco(int idFoco) {
        IdFoco = idFoco;
    }

    public int getIdPlaca() {
        return idPlaca;
    }

    public void setIdPlaca(int idPlaca) {
        this.idPlaca = idPlaca;
    }

    public boolean isActivate() {
        return isActivate;
    }

    public void setActivate(boolean activate) {
        isActivate = activate;
    }
}
