package com.wiicoon.rubi.wicoon_ligh_controller.models_realm;

import com.wiicoon.rubi.wicoon_ligh_controller.app.MyApplication;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Rubi on 8/22/2017.
 */

public class Foco extends RealmObject {




    public static final int OFF = 0;
    public static final int ON = 1;
    @PrimaryKey
    private int ID;
    //@Required
    private int IDplaca;
    @Required
    private String name;
   // @Required
    private int numDeSalida;
    private int estado;
    RealmList<Schedule> schedules;


    public Foco() {}

    public Foco( int IDplaca, String name, int numDeSalida, int estado) {
        this.ID = MyApplication.focoID.incrementAndGet();
        this.IDplaca = IDplaca;
        this.name = name;
        this.numDeSalida = numDeSalida;
        this.estado = estado;
    }

    public int getID() {
        return ID;
    }

    public int getIDplaca() {
        return IDplaca;
    }

    public void setIDplaca(int iDplaca) { this.IDplaca = iDplaca;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumDeSalida() {
        return numDeSalida;
    }

    public void setNumDeSalida( int numDeSalida ) { this.numDeSalida = numDeSalida; }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public RealmList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(RealmList<Schedule> schedules) {
        this.schedules = schedules;
    }
}
