package com.wiicoon.rubi.wicoon_ligh_controller.models_realm;

import com.wiicoon.rubi.wicoon_ligh_controller.app.MyApplication;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Rubi on 8/22/2017.
 */

public class Placa extends RealmObject {


    public static final int CONNECTED = 1;
    public static final int AP_MODE = 2;
    public static final int DISCONNECTED = 0;
    public static final int INTERNET_REMOTE = 3;



    @PrimaryKey
    private int ID;
    @Required
    private String macAddress;
    private String ipAddress;
    RealmList<Foco> focos;
    private String SSIDname;
    private String SSIDpass;
    private int status;    //ap_mode  disconected  ws_mode
    private int numSalidas;



    public Placa() {}

    public Placa(String macAddress, String ipAddress, String SSIDname, String SSIDpass, int status, int capacidadFocos) {

        this.ID = MyApplication.placaID.incrementAndGet();
        this.macAddress = macAddress;
        this.ipAddress =ipAddress;
        this.focos = new RealmList<Foco>();
        this.SSIDname = SSIDname;
        this.SSIDpass = SSIDpass;
        this.status = status;
        this.numSalidas = capacidadFocos;
    }


    public int getID() {
        return ID;
    }

    public String getMacAddress() {
        return macAddress;
    }


    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public RealmList<Foco> getFocos() {
        return focos;
    }

    public void setFocos(RealmList<Foco> focos) {
        this.focos = focos;
    }

    public String getSSIDname() {
        return SSIDname;
    }

    public void setSSIDname(String SSIDname) {
        this.SSIDname = SSIDname;
    }

    public String getSSIDpass() {
        return SSIDpass;
    }

    public void setSSIDpass(String SSIDpass) {
        this.SSIDpass = SSIDpass;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNumSalidas() {
        return numSalidas;
    }

    public void setNumSalidas(int capacidadFocos) {
        this.numSalidas = capacidadFocos;
    }

}

