package com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks;

import android.os.AsyncTask;

import com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import java.util.Date;

import retrofit2.Call;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.delay;

/**
 * Created by Rubi on 11/2/2017.
 */

public class AsynkTaskUpdateTime extends AsyncTask< Integer , Boolean, Boolean > {

    private String ip;
    private Date currentDate;
    private boolean result;
    private String mac;

    public interface MyAsyncTaskListener {
        void onPostExecuteConcluded(Boolean isResultOk);
        void onPreExecuteConcluded();
        //void onProgressUpdateExecute();
    }

    private AsynkTaskUpdateTime.MyAsyncTaskListener mListener;

    final public void setListener(AsynkTaskUpdateTime.MyAsyncTaskListener listener) {
        mListener = listener;
    }
    final public void setIp(String ip) {
        this.ip = ip;
    }
    final public void setDate(Date date) {
        this.currentDate = date;
    }
    final public void setMac(String mac) {
        this.mac = mac;
    }



    public AsynkTaskUpdateTime(){}

    @Override
    final protected Boolean doInBackground(Integer... value ) {

        result = false;
        int hour = currentDate.getHours();
        int minute = currentDate.getMinutes();
        int second = currentDate.getSeconds();
        int month = currentDate.getMonth();
        int dayMonth = currentDate.getDate();
        int year = currentDate.getYear();

        month++;
        year+=1900;

        Call call = RequestHttpMetods.httpRequestUpDateTime(ip, hour, minute, second, dayMonth, month, year, new RequestHttpMetods.OnResponseRecived() {
            @Override
            public void onResponse(Placa placa, boolean isResponseCorrect) {
                if(placa!=null){
                    if(placa.getMacAddress().equals(mac)){
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