package com.wiicoon.rubi.wicoon_ligh_controller.models;

import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rubi on 8/22/2017.
 */

public class PlacaPuente {




    private String macAddress;
    private String ipAdress;
    List<Foco> focos;
    private String SSIDname;
    private String SSIDpass;
    private int status;


    public PlacaPuente() {
        this.focos =  new ArrayList<Foco>();
    }


    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    public List<Foco> getFocos() {
        return focos;
    }

    public void setFocos(List<Foco> focos) {
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
}
