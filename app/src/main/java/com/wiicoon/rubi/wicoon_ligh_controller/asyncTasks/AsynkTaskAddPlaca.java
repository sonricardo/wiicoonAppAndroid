package com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.app.WifiMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.delay;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.httpRequestConfig;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.httpRequestDataInfo;

/**
 * Created by Rubi on 10/28/2017.
 */

public class AsynkTaskAddPlaca extends AsyncTask< Integer , Integer, Placa > {

    private final int SUCCESS_NEW = 0;
    private final int SUCCESS_EDIT = 1;
    private final int FAIL_REQ_NEW = 2;
    private final int FAIL_REQ_EDIT = 3;
    private final int FAIL_COMMUNNICATION = 4;
    private final int SUCCESS_EDIT_LAN = 5;

    private final int PLACA_DISCONNECTED_NO_SHARE = 0;
    private final int PLACA_DISCONNECTED_SHARE = 1;
    private final int PLACA_CONNECTED_LAN_NO_SHARE = 2;
    private final int PLACA_CONNECTED_LAN_SHARE = 3;
    private final int PLACA_CONNECTED_AP_NO_SHARE = 4;
    private final int PLACA_CONNECTED_AP_SHARE = 5;
    private final int NO_PLACA_DISCONNECTED_SHARE = 6;
    private final int NO_PLACA_CONNECTED_LAN_SHARE = 7;
    private final int NO_PLACA_CONNECTED_AP_SHARE = 8;
    private final int NO_PLACA_CONNECTED_AP_NO_SHARE = 9;
    private final int NO_PLACA_CONNECTED_LAN_NO_SHARE = 10;
    private final int NO_PLACA_DISCONNECTED_NO_SHARE = 11;

    public AsynkTaskAddPlaca(){}



    public interface MyAsyncTaskListener {
        void onPostExecuteLauncher(Placa placaGral, int statusResult);
        void onPreExecuteLauncher();
        void onCanceledLauncher(int statusResult);
        void onProgressUpdateLauncher(String msgToShow, int kindMessage);
    }

    public static final int IMPORTANT_STATUS_MESSAGE = 1;
    public static final int NORMAL_STATUS_MESSAGE = 0;

    private AsynkTaskAddPlaca.MyAsyncTaskListener mListener;
    private int inStatus;
    private String ipLan;
    private int statusResult;
    private String ssidToShare;
    private String passwordToShare;
    private String ssidPlaca;
    WifiManager wifiManager;
    ConnectivityManager connectivityManager;

    private Placa placaGral;
    boolean exitToStateMchine;
    private String msgStatus;
    private int kindMessage;

    final public void setListener(AsynkTaskAddPlaca.MyAsyncTaskListener listener) {
        mListener = listener;
    }

    final public void setInStatus(int inStatus) {
        this.inStatus = inStatus;
    }
    final public void setIpLan(String ipLan) {
        this.ipLan = ipLan;
    }

    final public void setSsidPlaca(String SSID) {
        this.ssidPlaca = SSID;
    }

    final public void setSsidAndPassToShare(String ssid, String pass) {
        this.ssidToShare = ssid;
        this.passwordToShare = pass;
    }

    private void refreshStatusMessage(String message, int kindM){
        msgStatus = message;
        kindMessage = kindM;
        publishProgress();
    }

    public final void setWifiConfiguration(WifiManager wifiManager, ConnectivityManager connectivityManager){
    this.wifiManager = wifiManager;
    this.connectivityManager = connectivityManager;
    }




    @Override
    final protected Placa doInBackground(Integer... values ) {

        final int TOKEN_COMM_MAX = 3;
        final int TIME_WAIT_COMM = 1000;
        final int TOKEN_REQ_IP_MAX = 7;
        final int TOKEN_RECONNECT_MAX = 14;
        final int TIME_WAIT_REQ_IP = 2000;



        int tokens = 0;
        int tokensReq = 0;
        int tokensReConnect = 0;
        exitToStateMchine = false;
        placaGral = new Placa();
        placaGral = null;

        while(!exitToStateMchine) {
            switch (inStatus) {
                case PLACA_DISCONNECTED_NO_SHARE:
                case PLACA_CONNECTED_AP_NO_SHARE:
                case NO_PLACA_CONNECTED_LAN_NO_SHARE:
                case NO_PLACA_CONNECTED_AP_NO_SHARE:
                case NO_PLACA_DISCONNECTED_NO_SHARE:


                    refreshStatusMessage("Comunicandose con la placa...",NORMAL_STATUS_MESSAGE);

                    httpRequestConfig(null, null, null, new RequestHttpMetods.OnResponseRecived() {
                        @Override
                        public void onResponse(Placa placa, boolean isResponseCorrect) {
                            if(placa!= null){
                                placaGral = placa;
                                exitToStateMchine = true;
                                refreshStatusMessage("Comunicacion exitosa, configure la placa...",IMPORTANT_STATUS_MESSAGE);
                                if(inStatus == PLACA_DISCONNECTED_NO_SHARE || inStatus == PLACA_CONNECTED_AP_NO_SHARE)
                                    statusResult = SUCCESS_EDIT;
                                else
                                    statusResult = SUCCESS_NEW;

                            }
                        }
                    });
                    delay(TIME_WAIT_COMM);
                    if(tokens >= TOKEN_COMM_MAX  && exitToStateMchine == false){
                        exitToStateMchine = true;
                        statusResult = FAIL_COMMUNNICATION;
                        refreshStatusMessage("Fallo en la comunicacion",IMPORTANT_STATUS_MESSAGE);
                        cancel(true);
                    }
                    tokens++;


                    break;

                case PLACA_DISCONNECTED_SHARE:
                case PLACA_CONNECTED_LAN_SHARE:
                case PLACA_CONNECTED_AP_SHARE:
                case NO_PLACA_CONNECTED_LAN_SHARE:
                case NO_PLACA_CONNECTED_AP_SHARE:
                case NO_PLACA_DISCONNECTED_SHARE:

                    if(placaGral == null) {

                        refreshStatusMessage("Comunicandose con la placa...",NORMAL_STATUS_MESSAGE);
                        httpRequestConfig(null, ssidToShare, passwordToShare, new RequestHttpMetods.OnResponseRecived() {
                            @Override
                            public void onResponse(Placa placa, boolean isResponseCorrect) {
                                if (placa != null) {
                                    placaGral = placa;

                                 }
                            }
                        });
                        delay(TIME_WAIT_COMM);
                        if (tokens >= TOKEN_COMM_MAX && placaGral == null) {
                            exitToStateMchine = true;
                            statusResult = FAIL_COMMUNNICATION;
                            refreshStatusMessage("Fallo en la comunicacion",IMPORTANT_STATUS_MESSAGE);
                            cancel(true);
                        }
                        tokens++;
                    }
                    else {
                        delay(TIME_WAIT_REQ_IP);

                        if (WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidPlaca)) {

                            refreshStatusMessage("consiguiendo ip...",NORMAL_STATUS_MESSAGE);
                            httpRequestDataInfo(null, new RequestHttpMetods.OnResponseRecived() {
                                @Override
                                public void onResponse(Placa placa, boolean isResponseCorrect) {
                                    if (placa != null) {
                                        placaGral = placa;
                                        if (placaGral.getIpAddress() != null) {
                                            refreshStatusMessage("Placa conectada a red. configure salidas...",IMPORTANT_STATUS_MESSAGE);
                                            if (inStatus == PLACA_DISCONNECTED_SHARE || inStatus == PLACA_CONNECTED_LAN_SHARE || inStatus == PLACA_CONNECTED_AP_SHARE)
                                                statusResult = SUCCESS_EDIT;
                                            else {
                                                statusResult = SUCCESS_NEW;
                                            }
                                            exitToStateMchine = true;
                                        }
                                    }
                                }
                            });


                            if (tokensReq >= TOKEN_REQ_IP_MAX && exitToStateMchine == false ) {

                                refreshStatusMessage("No se ha conseguido confirmar que la placa se conecto a la red : " + ssidToShare,IMPORTANT_STATUS_MESSAGE);
                                if (inStatus == PLACA_DISCONNECTED_SHARE || inStatus == PLACA_CONNECTED_LAN_SHARE || inStatus == PLACA_CONNECTED_AP_SHARE)
                                    statusResult = FAIL_REQ_EDIT;
                                else {
                                    statusResult = FAIL_REQ_NEW;
                                }
                                exitToStateMchine = true;
                            }
                            tokensReq++;

                        }
                        else{

                            refreshStatusMessage("PORFAVOR RECONECTESE A LA RED: \""+ ssidPlaca + "\" PARA CONTINUAR",IMPORTANT_STATUS_MESSAGE);
                            tokensReq = TOKEN_REQ_IP_MAX - 1;

                            if (tokensReConnect >= TOKEN_RECONNECT_MAX) {

                                refreshStatusMessage("No se ha conseguido confirmar que la placa se conecto a la red : " + ssidToShare+
                                                ".  Puede que los botones de prueba no respondan hasta que se concecte a la red : " + ssidPlaca ,IMPORTANT_STATUS_MESSAGE);

                                if (inStatus == PLACA_DISCONNECTED_SHARE || inStatus == PLACA_CONNECTED_LAN_SHARE || inStatus == PLACA_CONNECTED_AP_SHARE)
                                    statusResult = FAIL_REQ_EDIT;
                                else {
                                    statusResult = FAIL_REQ_NEW;
                                }
                                exitToStateMchine = true;
                            }
                            tokensReConnect++;
                        }
                    }


                    break;
                case PLACA_CONNECTED_LAN_NO_SHARE:

                    refreshStatusMessage("Comunicandose con la placa...",NORMAL_STATUS_MESSAGE);

                    httpRequestConfig(ipLan, null, null, new RequestHttpMetods.OnResponseRecived() {
                        @Override
                        public void onResponse(Placa placa, boolean isResponseCorrect) {
                            if(placa!= null){
                                placaGral = placa;
                                exitToStateMchine = true;
                                statusResult = SUCCESS_EDIT_LAN;
                                refreshStatusMessage("Comunicacion exitosa, configure la placa...",IMPORTANT_STATUS_MESSAGE);
                            }
                        }
                    });
                    delay(TIME_WAIT_COMM);
                    if(tokens >= TOKEN_COMM_MAX ){
                        exitToStateMchine = true;
                        statusResult = FAIL_COMMUNNICATION;
                        refreshStatusMessage("Fallo en la comunicacion",IMPORTANT_STATUS_MESSAGE);
                        cancel(true);
                    }
                    tokens++;

                    break;

                default: cancel(true); exitToStateMchine = true; break;

            }
        }

        return placaGral;
    }


    @Override
    final protected void onPostExecute(Placa placaGral) {

        if (mListener != null)
            mListener.onPostExecuteLauncher(placaGral,statusResult);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        if (mListener != null)
            mListener.onProgressUpdateLauncher(msgStatus, kindMessage);

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null)
            mListener.onPreExecuteLauncher();
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        if (mListener != null)
            mListener.onCanceledLauncher(statusResult);
        super.onCancelled();
    }


}