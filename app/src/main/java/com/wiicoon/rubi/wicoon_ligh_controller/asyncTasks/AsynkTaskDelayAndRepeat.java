package com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks;

import android.os.AsyncTask;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.delay;

/**
 * Created by Rubi on 1/21/2018.
 */

public class AsynkTaskDelayAndRepeat extends AsyncTask< Integer , Integer, Integer > {


    private int delay;
    private MyAsyncTaskListener mListener;

    public interface MyAsyncTaskListener {
        void onPostExecuteConcluded();
    }



    final public MyAsyncTaskListener setListener(MyAsyncTaskListener listener) {
        mListener = listener;
        return mListener;
    }
    public void setDelay(int delay){ this.delay = delay; }



    public AsynkTaskDelayAndRepeat(){}

    @Override
    final protected Integer doInBackground(Integer... values ) {
        delay(delay);
        return 0;
    }


    @Override
    final protected void onPostExecute(Integer progress) {

        if (mListener != null) {
            mListener.onPostExecuteConcluded();
        }
    }

    @Override
    final protected void onProgressUpdate(Integer... value) {

    }
}