package com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks;

import android.os.AsyncTask;

import com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.CORRECT;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.IDDLE;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.INCORRECT;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.WAITING;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.delay;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.AP_GET_IP;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.WS_GET_DATA;

/**
 * Created by Ricardo Coronado Galindo (sonricardo) on 22/10/2017.
 */

 public  class AsyncTaskRefresh extends AsyncTask < List<Placa>, Placa , List<Placa> > {

    public interface MyAsyncTaskListener {
        void onPostExecuteConcluded(List<Placa> placasActualizadas);
    }

    private MyAsyncTaskListener mListener;
    private int modeRefresh;
    private int stateHttp = IDDLE;
    private int responseHttp = IDDLE;
    public static final int  MODE_AP_REFRESH = 0;
    public static final int MODE_WS_REFRESH = 1;
    public static final int MODE_INT_REM_REFRESH = 2;


    final public MyAsyncTaskListener setListener(MyAsyncTaskListener listener) {
        mListener = listener;
        return mListener;
    }

    final public void setModeRefresh(int modeRefresh) {
       this.modeRefresh = modeRefresh;
    }


    public AsyncTaskRefresh(){}

    @Override
    protected void onPreExecute() {

    }

    @Override
    final protected List<Placa> doInBackground(List<Placa>... placas) {





        stateHttp = IDDLE;
        responseHttp = IDDLE;
        int modeRequest;

        if (modeRefresh == MODE_AP_REFRESH)
            modeRequest = AP_GET_IP;
        else
            modeRequest = WS_GET_DATA;

        List<Placa> listaPlacas = placas[0];
        final List<Placa> placasConnected = new ArrayList<>();
        placasConnected.clear();
        List<Placa> placasRebeldes = new ArrayList<>();
        placasRebeldes.clear();
        Call call = callInitial();

        for (int i = 0; i < listaPlacas.size(); i++) {

            String currentIp;
            if (modeRefresh == MODE_WS_REFRESH)
            currentIp = listaPlacas.get(i).getIpAddress();
            else {
                currentIp = null;
                i = listaPlacas.size();
            }


                stateHttp = WAITING;
                responseHttp = IDDLE;
                //call.cancel();
                call = RequestHttpMetods.httpRequest(modeRequest, currentIp, null, null, -1, -1, new RequestHttpMetods.OnResponseRecived() {
                    @Override
                    public void onResponse(Placa placa, boolean isResponseCorrect) {
                        if (isResponseCorrect) {
                            responseHttp = CORRECT;
                            stateHttp = IDDLE;
                            if (placa != null ) {
                                if (placa.getMacAddress() != null)
                                    placasConnected.add(placa);
                                publishProgress(placa);
                            }
                        } else {
                            responseHttp = INCORRECT;
                            stateHttp = IDDLE;
                        }

                    }
                });


         }
        delay(4000);
        call.cancel();
        placasRebeldes = noFoundPlacas(listaPlacas, placasConnected);
        for (int i = 0; i < placasRebeldes.size(); i++) {
            placasRebeldes.get(i).setStatus(Placa.DISCONNECTED);
        }
        for (int i = 0; i < placasConnected.size(); i++) {
            if (modeRefresh == MODE_AP_REFRESH)
            placasConnected.get(i).setStatus(Placa.AP_MODE);
            else
                placasConnected.get(i).setStatus(Placa.CONNECTED);
        }

        listaPlacas = placasConnected;
        listaPlacas.addAll(placasRebeldes);

        return listaPlacas;

    }



    @Override
    final protected void onPostExecute(List<Placa> placasActualizadas) {

             if (mListener != null) {
                 mListener.onPostExecuteConcluded(placasActualizadas);
             }
    }

    private List<Placa> noFoundPlacas(List<Placa> listaCompleta, List<Placa> listaExistente){

        List<Placa> listaResponse = new ArrayList<>();
        listaResponse.clear();

        for(int i = 0; i< listaCompleta.size(); i++) {
            listaResponse.add(listaCompleta.get(i));
        }
        for(int i = 0; i< listaCompleta.size(); i++){
            for(int j = 0 ; j < listaExistente.size(); j++){

                if( listaCompleta.get(i).getMacAddress().equals( listaExistente.get(j).getMacAddress() ) ) {
                    listaResponse.remove(listaResponse.indexOf(listaCompleta.get(i)));
                    break;
                }

            }
        }
         return listaResponse;
    }

    @Override
    protected void onProgressUpdate(Placa... values) {
        super.onProgressUpdate(values[0]);
    }

    @Override
    protected void onCancelled(List<Placa> placas) {
        super.onCancelled(placas);
    }

    @Override
    protected void onCancelled() {

    }
    private Call callInitial(){

        Call call = new Call() {
            @Override
            public Response execute() throws IOException {
                return null;
            }

            @Override
            public void enqueue(Callback callback) {

            }

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
        return call;

    }
}
