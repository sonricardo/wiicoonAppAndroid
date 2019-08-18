package com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks;

import android.os.AsyncTask;

import com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Schedule;

import retrofit2.Call;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.delay;

/**
 * Created by Rubi on 11/2/2017.
 */

public class AsynkTaskSaveSchedule extends AsyncTask< Schedule , Boolean, Boolean > {

    private String ip;
    private int numSal;
    private String macOfPlaca;
    private boolean result;

    public interface MyAsyncTaskListener {
        void onPostExecuteConcluded(Boolean isResultOk);
        void onPreExecuteConcluded();
        //void onProgressUpdateExecute();
    }

    private AsynkTaskSaveSchedule.MyAsyncTaskListener mListener;

    final public void setListener(AsynkTaskSaveSchedule.MyAsyncTaskListener listener) {
        mListener = listener;
    }
    final public void setIp(String ip) {
        this.ip = ip;
    }
    final public void setNumSalida(int numSal) {
        this.numSal = numSal;
    }
    final public void setmacOfPlaca(String macOfPlaca) {
        this.macOfPlaca = macOfPlaca;
    }


    public AsynkTaskSaveSchedule(){}

    @Override
    final protected Boolean doInBackground(Schedule... schedules ) {

        Schedule newSchedule = schedules[0];
        result = false;

        int hour = newSchedule.getHour();
        int minute = newSchedule.getMinute();
        int days = newSchedule.getDays();
        int schID = newSchedule.getID();
        int numSalida = numSal;
        int typeTurn = newSchedule.getTypeTurn();
        int isRepetitive = newSchedule.getLifeCycle();

        Call call = RequestHttpMetods.httpRequestCreateSchedule(ip, hour, minute, days, schID, numSalida, typeTurn, isRepetitive, new RequestHttpMetods.OnResponseRecived() {
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