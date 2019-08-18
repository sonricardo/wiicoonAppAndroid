package com.wiicoon.rubi.wicoon_ligh_controller.models_realm;

import com.wiicoon.rubi.wicoon_ligh_controller.app.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rubi on 9/26/2017.
 */

public class LanData extends RealmObject {




    public static final int AP_DE_PLACA = 0;
    public static final int LOCAL_LAN = 1;

    @PrimaryKey
    private int ID;
    private String SSID;
    private String psk;
    private boolean isPskOk;
    private int networkId;
    private int typeConnection;



    public LanData() {}

    public LanData(  String SSID, boolean isPskOk, int typeConnection, int networkId ) {
        this.ID = MyApplication.lanID.incrementAndGet();
        this.SSID = SSID;
        this.isPskOk = isPskOk;
        this.typeConnection = typeConnection;
        this.networkId = networkId;
    }



    public int getID() {
        return ID;
    }





    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPsk() {
        return psk;
    }

    public void setPsk(String psk) {
        this.psk = psk;
    }

    public boolean isPskOk() {
        return isPskOk;
    }

    public void setPskOk(boolean pskOk) {
        isPskOk = pskOk;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public int getTypeConnection() {
        return typeConnection;
    }

    public void setTypeConnection(int typeConnection) {
        this.typeConnection = typeConnection;
    }
}

