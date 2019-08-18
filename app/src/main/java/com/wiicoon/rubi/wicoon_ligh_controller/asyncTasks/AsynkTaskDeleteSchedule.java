package com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks;

import android.os.AsyncTask;

import com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import retrofit2.Call;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.delay;

/**
 * Created by Rubi on 11/2/2017.
 */

public class AsynkTaskDeleteSchedule extends AsyncTask< Integer , Boolean, Boolean > {

private String ip;
private String macOfPlaca;
private boolean result;

public interface MyAsyncTaskListener {
    void onPostExecuteConcluded(Boolean isResultOk);
    void onPreExecuteConcluded();
    //void onProgressUpdateExecute();
}

    private AsynkTaskDeleteSchedule.MyAsyncTaskListener mListener;

    final public void setListener(AsynkTaskDeleteSchedule.MyAsyncTaskListener listener) {
        mListener = listener;
    }
    final public void setIp(String ip) {
        this.ip = ip;
    }
    final public void setmacOfPlaca(String macOfPlaca) {
        this.macOfPlaca = macOfPlaca;
    }


    public AsynkTaskDeleteSchedule(){}

    @Override
    final protected Boolean doInBackground(Integer... id ) {

        int idSch = id[0];
        result = false;


        Call call = RequestHttpMetods.httpRequestDeleteSchedule(ip, idSch, new RequestHttpMetods.OnResponseRecived() {
            @Override
            public void onResponse(Placa placa, boolean isResponseCorrect) {
                if(placa!=null){
                    if(placa.getMacAddress().equals(macOfPlaca)){
                        result = true;
                    }
                }
            }
        });
        delay(500);
        call.cancel();

        return result;
    }


    @Override
    final protected void onPostExecute(Boolean isResultOk) {

        if (mListener != null)
            mListener.onPostExecuteConcluded(isResultOk);
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null)
            mListener.onPreExecuteConcluded();
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    final protected void onProgressUpdate(Boolean... result) {
        if (mListener != null){}
        // mListener.onProgressUpdateExecute();
    }
}