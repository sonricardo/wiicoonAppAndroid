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

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.IDDLE;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.httpRequestDataInfo;

/**
 * Created by Rubi on 10/22/2017.
 */

public abstract class AsyncTaskCast extends AsyncTask< List<Placa>, Integer, List<Placa> > {

    public interface MyAsyncTaskListener {
        void onPostExecuteConcluded(List<Placa> placasActualizadas);
        void onProgressUpdateConcluded(int progressBar);
    }

    private MyAsyncTaskListener mListener;
    private String myAppIp;
    private int stateHttp = IDDLE;
    private int responseHttp = IDDLE;


    final public void setListener(MyAsyncTaskListener listener) {
        mListener = listener;
    }

    final public void setMyAppIp(String myAppIp) {
        this.myAppIp = myAppIp;
    }


    public AsyncTaskCast(){}

    @Override
    final protected List<Placa> doInBackground(List<Placa>... placas) {

        stateHttp = IDDLE;
        responseHttp = IDDLE;
        String ipBaseToCast = new String();



        final List<Placa> listaPlacas = placas[0];
        final List<Placa> placasConnected = new ArrayList<>();
        placasConnected.clear();
        List<Placa> placasRebeldes = new ArrayList<>();
        placasRebeldes.clear();

        if (myAppIp != null)
            ipBaseToCast = getIpCast(myAppIp);

         Call call = callInitial();

        for (int i = 1; i < 255; i++) {

            if(isCancelled())
            break;

            final String currentIpCast = ipBaseToCast + i;

            if(listaPlacas.size() == placasConnected.size())
            break;


               call = httpRequestDataInfo(currentIpCast, new RequestHttpMetods.OnResponseRecived() {
                    @Override
                    public void onResponse(Placa placa, boolean isResponseCorrect) {
                        if(placa != null){
                            if(placa.getMacAddress()!= null){
                                for(int j = 0;j<listaPlacas.size();j++){
                                    if(listaPlacas.get(j).getMacAddress().equals(placa.getMacAddress())){
                                       placasConnected.add(placa);

                                    }
                                 }
                             }
                        }
                        else{}
                    }
                });

            publishProgress(i);
        }
        call.cancel();
        placasRebeldes = noFoundPlacas(listaPlacas, placasConnected);

        for (int i = 0; i < placasRebeldes.size(); i++) {
            placasRebeldes.get(i).setStatus(Placa.DISCONNECTED);
        }
        for (int i = 0; i < placasConnected.size(); i++) {
            placasConnected.get(i).setStatus(Placa.CONNECTED);
        }

        placasConnected.addAll(placasRebeldes);

        return placasConnected;
    }


    @Override
    final protected void onPostExecute(List<Placa> placasActualizadas) {
        if (mListener != null)
            mListener.onPostExecuteConcluded(placasActualizadas);
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

    private String getIpCast(String ip){
        String ipCast;
        ipCast = ip.substring( 0, ip.lastIndexOf(".")+1 );
        return ipCast;

    }

    @Override
    final protected void onProgressUpdate(Integer... value) {
        if (mListener != null)
            mListener.onProgressUpdateConcluded(value[0]);
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
