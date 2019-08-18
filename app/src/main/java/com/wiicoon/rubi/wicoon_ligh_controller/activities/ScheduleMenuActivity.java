package com.wiicoon.rubi.wicoon_ligh_controller.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.adapters.ScheduleRecyclerAdapter;
import com.wiicoon.rubi.wicoon_ligh_controller.app.WifiMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskDeleteSchedule;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskSaveSchedule;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskUpdateTime;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Schedule;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.daysOfdiff;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.dosAlaN;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.ShowAlerts.showMessageNeedConnection;

public class ScheduleMenuActivity extends AppCompatActivity {


    private RecyclerView.Adapter schAdapeter;
    private RecyclerView recVieSch;
    private RecyclerView.LayoutManager layoutManager;


    private RealmList<Schedule> schedulesDb;
    private Realm realm;

    WifiManager wifiManager;
    ConnectivityManager connectivityManager;

    private int idFoco;
    private AsynkTaskDeleteSchedule asynkTaskDeleteSchedule;
    private AsynkTaskSaveSchedule asynkTaskSaveSchedule;
    private AsynkTaskUpdateTime asynkTaskUpdateTime;

    private ProgressBar progressBarCharge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_menu);


        if(getIntent().getExtras()!= null){
            idFoco = getIntent().getExtras().getInt("ID");
        }

        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        realm = Realm.getDefaultInstance();
        Foco foco = realm.where(Foco.class).equalTo("ID",idFoco).findFirst();
        schedulesDb = foco.getSchedules();


        progressBarCharge = (ProgressBar)findViewById(R.id.progressBarChargeMenuSch);
        recVieSch = (RecyclerView)findViewById(R.id.reciclerViewSchedules);
        setAdapter();
        recVieSch.setLayoutManager(layoutManager);
        recVieSch.setAdapter(schAdapeter);

        Placa placa = realm.where(Placa.class).equalTo("ID",foco.getIDplaca()).findFirst();
        String ip;
        if(placa.getStatus()== Placa.CONNECTED)
            ip = placa.getIpAddress();
        else
            ip = null;
        upDateTimeToPlaca(ip,placa.getMacAddress());

        this.enforceIconBar(foco.getName());

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                schAdapeter.notifyDataSetChanged();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_schedule_menu_activity, menu);


        return true;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case R.id.itemAddScheduleMenu:
                if(WifiMetods.isConnected(connectivityManager)) {
                    Intent intent = new Intent(ScheduleMenuActivity.this, ScheduleActivity.class);
                    intent.putExtra("ID", idFoco);

                    startActivity(intent);
                }
                else
                    showMessageNeedConnection(this);
                return true;

            case R.id.itemReturnSceduleMenu:
                Intent intentBack = new Intent(ScheduleMenuActivity.this, MainActivity.class);
                intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentBack);
                finish();

                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }

    public void enforceIconBar(String nameFoco){
        getSupportActionBar().setIcon(R.mipmap.ic_clock);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(nameFoco);

    }

    private void setAdapter(){
        layoutManager = new LinearLayoutManager(getApplicationContext());

        schAdapeter = new ScheduleRecyclerAdapter(R.layout.recycler_view_schedule_menu, schedulesDb, new ScheduleRecyclerAdapter.OnClickListenerButtonDelete() {
            @Override
            public void onBtnDeleteClick(int idSchedule) {

                Foco foco = realm.where(Foco.class).equalTo("ID",idFoco).findFirst();
                Placa placa = realm.where(Placa.class).equalTo("ID",foco.getIDplaca()).findFirst();
                final String ip;
                if(placa.getStatus() == Placa.CONNECTED)
                    ip = placa.getIpAddress();
                else
                    ip = null;

                launchDeleteSchProcess(ip,placa.getMacAddress(),idSchedule);
            }
        }, new ScheduleRecyclerAdapter.OnSwitchChangeListener() {
            @Override
            public void onSwitchChanged(boolean isChecked, int idSch) {



                Foco foco = realm.where(Foco.class).equalTo("ID",idFoco).findFirst();
                Placa placa = realm.where(Placa.class).equalTo("ID",foco.getIDplaca()).findFirst();
                final String ip;
                if(placa.getStatus() == Placa.CONNECTED)
                    ip = placa.getIpAddress();
                else
                    ip = null;
                if(isChecked){
                    launchActivateSchProcess(ip,placa.getMacAddress(),foco.getNumDeSalida(),idSch);
                    }
                else {
                    launchDesactSchProcess(ip,placa.getMacAddress(),idSch);
                }
            }

        }, new ScheduleRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int idSche) {
                if(WifiMetods.isConnected(connectivityManager)) {
                    Intent intent = new Intent(ScheduleMenuActivity.this, ScheduleActivity.class);
                    intent.putExtra("ID", idFoco);
                    intent.putExtra("IDSCH",idSche);
                    startActivity(intent);
                }
                else
                    showMessageNeedConnection(getApplicationContext());
            }
        });



    }

    private void deleteSchedule(Schedule schedule){

        realm.beginTransaction();
        schedule.deleteFromRealm();
        realm.commitTransaction();

    }
    private void activateSchedule(Schedule schedule) {

        Date currentDate = new Date(System.currentTimeMillis());

        realm.beginTransaction();
        schedule.setActivate(true);
        schedule.setDateOfCreation(currentDate);
        realm.copyToRealmOrUpdate(schedule);
        realm.commitTransaction();
    }

    private void desactivateSchedule(Schedule schedule) {
        realm.beginTransaction();
        schedule.setActivate(false);
        realm.copyToRealmOrUpdate(schedule);
        realm.commitTransaction();
    }


    private void launchDeleteSchProcess(String ip, String mac, final int idSche){

        final Schedule schedule = realm.where(Schedule.class).equalTo("ID",idSche).findFirst();

        asynkTaskDeleteSchedule = new AsynkTaskDeleteSchedule(){};
        asynkTaskDeleteSchedule.setIp(ip);
        asynkTaskDeleteSchedule.setmacOfPlaca(mac);
        asynkTaskDeleteSchedule.setListener(new AsynkTaskDeleteSchedule.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                setOnNormalState();
                if(isResultOk) {
                    deleteSchedule(schedule);
                }
                else {
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {
                setOnChargeState();
            }
        });
        asynkTaskDeleteSchedule.execute(idSche);
    }

    private void activateSchProcess(String ip, String mac, int numSalida, int idSche){

        final Schedule scheduleDb = realm.where(Schedule.class).equalTo("ID",idSche).findFirst();
        final Schedule schedule = new Schedule(scheduleDb.getHour(),scheduleDb.getMinute(),scheduleDb.getDays(),scheduleDb.getIdFoco(),
                                        scheduleDb.getIdPlaca(),scheduleDb.getTypeTurn(),scheduleDb.isActivate(),scheduleDb.getDateOfCreation(),scheduleDb.getLifeCycle());


        asynkTaskSaveSchedule = new AsynkTaskSaveSchedule(){};
        asynkTaskSaveSchedule.setIp(ip);
        asynkTaskSaveSchedule.setmacOfPlaca(mac);
        asynkTaskSaveSchedule.setNumSalida(numSalida);
        asynkTaskSaveSchedule.setListener(new AsynkTaskSaveSchedule.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                setOnNormalState();
                if(isResultOk){

                    activateSchedule(scheduleDb);

                }
                else{
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {

            }
        });
        asynkTaskSaveSchedule.execute(schedule);
    }

    private void launchActivateSchProcess(final String ip, final String mac, final int numSalida, final int idSche){

        Date currentDate = new Date(System.currentTimeMillis());
        asynkTaskUpdateTime = new AsynkTaskUpdateTime(){};
        asynkTaskUpdateTime.setIp(ip);
        asynkTaskUpdateTime.setMac(mac);
        asynkTaskUpdateTime.setDate(currentDate);
        asynkTaskUpdateTime.setListener(new AsynkTaskUpdateTime.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {

                if(isResultOk){
                    activateSchProcess(ip,mac,numSalida,idSche);
                }
                else {
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {
                setOnChargeState();
             }
        });
        asynkTaskUpdateTime.execute();
    }

    private void desactivateSchProcess(String ip, String mac, final int idSche){

        final Schedule schedule = realm.where(Schedule.class).equalTo("ID",idSche).findFirst();

        asynkTaskDeleteSchedule = new AsynkTaskDeleteSchedule(){};
        asynkTaskDeleteSchedule.setIp(ip);
        asynkTaskDeleteSchedule.setmacOfPlaca(mac);
        asynkTaskDeleteSchedule.setListener(new AsynkTaskDeleteSchedule.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                setOnNormalState();
                if(isResultOk) {
                    desactivateSchedule(schedule);
                }
                else {
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {

            }
        });
        asynkTaskDeleteSchedule.execute(idSche);
    }
    private void launchDesactSchProcess(final String ip,final String mac, final int idSche){

        Date currentDate = new Date(System.currentTimeMillis());
        asynkTaskUpdateTime = new AsynkTaskUpdateTime(){};
        asynkTaskUpdateTime.setIp(ip);
        asynkTaskUpdateTime.setMac(mac);
        asynkTaskUpdateTime.setDate(currentDate);
        asynkTaskUpdateTime.setListener(new AsynkTaskUpdateTime.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                if(isResultOk){
                    desactivateSchProcess(ip,mac,idSche);
                }
                else {
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {
                setOnChargeState();
            }
        });
        asynkTaskUpdateTime.execute();


    }

     private void goHome(){
        Toast.makeText(getApplicationContext(),"No se ha realizado la accion\n(problema de comunicacion con la placa)",Toast.LENGTH_LONG).show();
        Intent intentBack = new Intent(ScheduleMenuActivity.this, MainActivity.class);
        startActivity(intentBack);

    }

    private void upDateTimeToPlaca(String ip, String mac){
        Date currentDate = new Date(System.currentTimeMillis());
        asynkTaskUpdateTime = new AsynkTaskUpdateTime(){};
        asynkTaskUpdateTime.setIp(ip);
        asynkTaskUpdateTime.setMac(mac);
        asynkTaskUpdateTime.setDate(currentDate);
        asynkTaskUpdateTime.setListener(new AsynkTaskUpdateTime.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                if(isResultOk){
                    Toast.makeText(getApplicationContext(),"Hora en placa actualizada",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"No se ha actualizado la hora en la placa",Toast.LENGTH_LONG).show();
                    //goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {
            }
        });
        asynkTaskUpdateTime.execute();
    }
    private  void  setOnNormalState() {

       recVieSch.setVisibility(View.VISIBLE);
       progressBarCharge.setVisibility(View.INVISIBLE);

    }

    private  void setOnChargeState(){

        recVieSch.setVisibility(View.INVISIBLE);
        progressBarCharge.setVisibility(View.VISIBLE);


    }

    private void desactivateExpiredSchedules(List<Schedule> schedules){

        for(int i=0; i < schedules.size(); i++){
            if(mustDesactivateSchedule(schedules.get(i))){
                desactivateSchedule(schedules.get(i));
            }
        }

    }

    private boolean mustDesactivateSchedule(Schedule schedule){

        Date currentDate = new Date(System.currentTimeMillis());
        if(schedule.getLifeCycle() == Schedule.REPEAT_ALWAYS)
            return false;

        int daysToBreak = getDaysToBreak(schedule.getDateOfCreation(), schedule);
        int daysTranscurridos = daysOfdiff(schedule.getDateOfCreation(), currentDate);

        if(daysTranscurridos > daysToBreak)
            return true;
        if(daysTranscurridos < daysToBreak)
            return false;
        if(daysTranscurridos == daysToBreak){
            if(currentDate.getHours() > schedule.getHour()){
                return true;
            }
            if(currentDate.getHours() < schedule.getHour()){
                return false;
            }
            if(currentDate.getHours() == schedule.getHour()){
                if(currentDate.getMinutes()>= schedule.getMinute()){
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }


    private int getDaysToBreak(Date dateCreation, Schedule schedule){

        int daysToBreak = 0;
        int dayAlarm = getDayWeekAlarm(schedule.getDays());

        if(dayAlarm < dateCreation.getDay()){
            daysToBreak = 7 - dateCreation.getDay() + dayAlarm;
        }
        else if(dayAlarm > dateCreation.getDay()){
            daysToBreak = dayAlarm - dateCreation.getDay();
        }
        else if(dayAlarm == dateCreation.getDay()){
            if(dateCreation.getHours()> schedule.getHour()){
                daysToBreak = 7;
            }
            else if (dateCreation.getHours()< schedule.getHour()){
                daysToBreak = 0;
            }
            else if (dateCreation.getHours() == schedule.getHour()) {
                if(dateCreation.getMinutes()>= schedule.getMinute()){
                    daysToBreak = 7;
                }
                else {
                    daysToBreak = 0;
                }
            }
        }
        return daysToBreak;
    }

    private int getDayWeekAlarm(int daysCode){

        for(int i=0;i<8;i++){
            if((daysCode & dosAlaN(i)) > 0){
                return i-1;
            }
        }
        return -1;
    }

    @Override
    protected void onResume() {
        desactivateExpiredSchedules(schedulesDb);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
