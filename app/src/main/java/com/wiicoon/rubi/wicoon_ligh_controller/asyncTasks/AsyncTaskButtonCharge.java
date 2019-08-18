package com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks;

import android.os.AsyncTask;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.delay;

/**
 * Created by Rubi on 10/25/2017.
 */

public abstract class AsyncTaskButtonCharge extends AsyncTask< Integer , Integer, Integer > {

    public interface MyAsyncTaskListener {
        void onPostExecuteConcluded();
    }

    private MyAsyncTaskListener mListener;

    final public void setListener(AsyncTaskButtonCharge.MyAsyncTaskListener listener) {
        mListener = listener;
    }



    public AsyncTaskButtonCharge(){}

    @Override
    final protected Integer doInBackground(Integer... values ) {
        delay(300);
        return 0;
     }


    @Override
    final protected void onPostExecute(Integer progress) {

        if (mListener != null)
            mListener.onPostExecuteConcluded();
    }

    @Override
    final protected void onProgressUpdate(Integer... value) {

    }
}


