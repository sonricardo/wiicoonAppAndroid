package com.wiicoon.rubi.wicoon_ligh_controller.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskDeleteSchedule;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskSaveSchedule;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskUpdateTime;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Schedule;

import java.util.Date;

import io.realm.Realm;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.dosAlaN;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TimePicker timePicker;
    private Button btnGuardar;
    private Button btnCancelar;
    private CheckBox chBoxDom;
    private CheckBox chBoxLun;
    private CheckBox chBoxMar;
    private CheckBox chBoxMie;
    private CheckBox chBoxJue;
    private CheckBox chBoxVie;
    private CheckBox chBoxSab;
    private CheckBox cheBoxSiempre;
    private RadioGroup rdGrpOnOff;
    private RadioButton rdBtnOn;
    private RadioButton rdBtnOff;
    private CheckBox cheBoxUnaVez;
    private ProgressBar prgBarSchChar;
    private ImageView imgVieFocInd;
    private RelativeLayout relativeLayout;
    private int idFoco;
    private int idSche;

    private Realm realm;
    private AsynkTaskSaveSchedule asynkTaskSaveSchedule;
    private AsynkTaskDeleteSchedule asynkTaskDeleteSchedule;
    private AsynkTaskUpdateTime asynkTaskUpdateTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        timePicker = (TimePicker)findViewById(R.id.timePicker);
        btnGuardar = (Button)findViewById(R.id.buttonGuardarYsalir);
        btnCancelar = (Button)findViewById(R.id.buttonCancelarYsalir);
        chBoxDom = (CheckBox)findViewById(R.id.checkBoxDomingo);
        chBoxLun = (CheckBox)findViewById(R.id.checkBoxLunes);
        chBoxMar = (CheckBox)findViewById(R.id.checkBoxMartes);
        chBoxMie = (CheckBox)findViewById(R.id.checkBoxMiercoles);
        chBoxJue = (CheckBox)findViewById(R.id.checkBoxJueves);
        chBoxVie = (CheckBox)findViewById(R.id.checkBoxViernes);
        chBoxSab = (CheckBox)findViewById(R.id.checkBoxSabado);
        rdGrpOnOff = (RadioGroup)findViewById(R.id.radioGroupOnOff);
        cheBoxSiempre = (CheckBox)findViewById(R.id.checkBoxNoRepetirSiempre);
        cheBoxUnaVez = (CheckBox)findViewById(R.id.checkBoxRepetirUnaVez);
        rdBtnOn =  (RadioButton)findViewById(R.id.radioButtonOn);
        rdBtnOff = (RadioButton)findViewById(R.id.radioButtonOff);
        prgBarSchChar = (ProgressBar)findViewById(R.id.progressBarScheduleCharge);
        relativeLayout = (RelativeLayout) findViewById(R.id.layoutScheduleActivity);
        imgVieFocInd = (ImageView) findViewById(R.id.imageViewLightIndicator);

        Date currentDate = new Date(System.currentTimeMillis());

        if(getIntent().getExtras()!= null){
            idFoco = getIntent().getExtras().getInt("ID");
        }
        if( getIntent().getExtras().getInt("IDSCH") > 0){
            idSche = getIntent().getExtras().getInt("IDSCH");
         }
        else
            idSche = 0;

        realm = Realm.getDefaultInstance();
        this.enforceTiittleBar(realm.where(Foco.class).equalTo("ID",idFoco).findFirst().getName());

        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        cheBoxSiempre.setOnCheckedChangeListener(this);
        cheBoxUnaVez.setOnCheckedChangeListener(this);
        chBoxDom.setOnCheckedChangeListener(this);
        chBoxLun.setOnCheckedChangeListener(this);
        chBoxMar.setOnCheckedChangeListener(this);
        chBoxMie.setOnCheckedChangeListener(this);
        chBoxJue.setOnCheckedChangeListener(this);
        chBoxVie.setOnCheckedChangeListener(this);
        chBoxSab.setOnCheckedChangeListener(this);
        rdBtnOff.setOnCheckedChangeListener(this);
        rdBtnOn.setOnCheckedChangeListener(this);
        timePicker.setIs24HourView(false);

        if(idSche > 0) {
            setPreDefinidedValueSchedule(idSche);
        }
        else {
            setDefaultInitialValuesSchedule(currentDate);
        }




     }


    public void enforceTiittleBar(String tittle){

        getSupportActionBar().setTitle(tittle);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.buttonGuardarYsalir:
                if(isAnyDayChecked()){

                    final Foco foco = realm.where(Foco.class).equalTo("ID",idFoco).findFirst();
                    final Placa placa =  realm.where(Placa.class).equalTo("ID",foco.getIDplaca()).findFirst();
                    final String ip;
                    if(placa.getStatus() == Placa.CONNECTED)
                        ip = placa.getIpAddress();
                    else
                        ip = null;


                    if(idSche>0){

                            launchDeleteAndSaveScheProcess(ip,placa.getMacAddress(),foco);

                    }

                    else {

                            launchSaveScheProcess(ip, placa.getMacAddress(), foco);


                    }
                }
                else {
                    Toast.makeText(this,"Por favor seleccione que dia(s)",Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.buttonCancelarYsalir:
                Intent intentBack = new Intent(ScheduleActivity.this, ScheduleMenuActivity.class);
                intentBack.putExtra("ID",idFoco);
                startActivity(intentBack);
                finish();

                break;
            default: break;
        }
    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        switch (buttonView.getId()){

            case R.id.radioButtonOn:
                if(rdBtnOn.isChecked()){
                    imgVieFocInd.setImageResource(R.mipmap.ic_light_on);
                }

            break;

            case R.id.radioButtonOff:
                if(rdBtnOff.isChecked()){
                    imgVieFocInd.setImageResource(R.mipmap.ic_light_off);
                }

            break;

            case R.id.checkBoxNoRepetirSiempre:
                if(cheBoxSiempre.isChecked()){
                    cheBoxUnaVez.setChecked(false);
                }
                else
                    cheBoxUnaVez.setChecked(true);

            break;

            case R.id.checkBoxRepetirUnaVez:

                if(cheBoxUnaVez.isChecked()){
                    cheBoxSiempre.setChecked(false);
                    cheBoxOffAllExept(cheBoxUnaVez,cheBoxUnaVez.isChecked());

                }
                else
                    cheBoxSiempre.setChecked(true);
                     break;
            case R.id.checkBoxDomingo:
                if(cheBoxUnaVez.isChecked()){
                    cheBoxOffAllExept(chBoxDom,chBoxDom.isChecked());
                }
                break;
            case R.id.checkBoxLunes:
                if(cheBoxUnaVez.isChecked()){
                    cheBoxOffAllExept(chBoxLun,chBoxLun.isChecked());
                }
                break;
            case R.id.checkBoxMartes:
                if(cheBoxUnaVez.isChecked()){
                    cheBoxOffAllExept(chBoxMar,chBoxMar.isChecked());
                }
                break;
            case R.id.checkBoxMiercoles:
                if(cheBoxUnaVez.isChecked()){
                    cheBoxOffAllExept(chBoxMie,chBoxMie.isChecked());
                }
                break;
            case R.id.checkBoxJueves:
                if(cheBoxUnaVez.isChecked()){
                    cheBoxOffAllExept(chBoxJue,chBoxJue.isChecked());
                }
                break;
            case R.id.checkBoxViernes:
                if(cheBoxUnaVez.isChecked()){
                    cheBoxOffAllExept(chBoxVie,chBoxVie.isChecked());
                }
                break;
            case R.id.checkBoxSabado:
                if(cheBoxUnaVez.isChecked()){
                    cheBoxOffAllExept(chBoxSab,chBoxSab.isChecked());
                }
                break;

            default: break;

        }
    }
    private void refreshTheTimeOnTimePicker(Date date){
        timePicker.setCurrentHour(date.getHours());
        timePicker.setCurrentMinute(date.getMinutes());
    }

    private Schedule createSchedule(){
        Date currentDate = new Date(System.currentTimeMillis());
        int days = getTheWeekCode();
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        int typeTurn;
        boolean isActivate = true;
        Foco foco = realm.where(Foco.class).equalTo("ID",idFoco).findFirst();
        int idPlaca = foco.getIDplaca();
        int lifeCycle;

        if(rdBtnOff.isChecked())
            typeTurn = Schedule.TYPE_TURN_OFF;
        else
            typeTurn = Schedule.TYPE_TURN_ON;

        if(cheBoxSiempre.isChecked())
            lifeCycle = Schedule.REPEAT_ALWAYS;
        else
            lifeCycle = Schedule.REPEAT_ONCE;

        Schedule newSchedule = new Schedule(hour,minute,days,idFoco,idPlaca,typeTurn,isActivate,currentDate,lifeCycle);
        return newSchedule;

    }

    private void deleteSchedule(Schedule schedule){

        realm.beginTransaction();
        schedule.deleteFromRealm();
        realm.commitTransaction();

    }

    private void saveNewSchedule(Schedule schedule, Foco foco){
        realm.beginTransaction();
        realm.copyToRealm(schedule);
        foco.getSchedules().add(schedule);
        realm.commitTransaction();
    }

    private void setDefaultInitialValuesSchedule(Date currentDate){

        refreshTheTimeOnTimePicker(currentDate);
        rdBtnOn.setChecked(true);
        cheBoxUnaVez.setChecked(true);
        cheBoxSiempre.setChecked(false);

    }

    private void setPreDefinidedValueSchedule(int idSche){

        Schedule scheduleEdit = realm.where(Schedule.class).equalTo("ID",idSche).findFirst();

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(scheduleEdit.getHour());
        timePicker.setCurrentMinute(scheduleEdit.getMinute());
        timePicker.setIs24HourView(false);

        if(scheduleEdit.getTypeTurn() == Schedule.TYPE_TURN_ON){
            rdBtnOn.setChecked(true);
        }
        else {
            rdBtnOff.setChecked(true);
        }
        if(scheduleEdit.getLifeCycle() == Schedule.REPEAT_ALWAYS){
            cheBoxSiempre.setChecked(true);
            cheBoxUnaVez.setChecked(false);
        }
        else{
            cheBoxUnaVez.setChecked(true);
            cheBoxSiempre.setChecked(false);
        }
        for(int i = 0; i<7; i++){
            if( isThisBitHigh( i+1,scheduleEdit.getDays()) ){
                switch(i){
                    case 0: chBoxDom.setChecked(true);  break;
                    case 1: chBoxLun.setChecked(true);  break;
                    case 2: chBoxMar.setChecked(true);  break;
                    case 3: chBoxMie.setChecked(true);  break;
                    case 4: chBoxJue.setChecked(true);  break;
                    case 5: chBoxVie.setChecked(true);  break;
                    case 6: chBoxSab.setChecked(true);  break;
                    default: break;

                }
            }

        }
    }

    private void goHome(){
        Toast.makeText(getApplicationContext(),"No se ha realizado la accion\n(problema de comunicacion con la placa)",Toast.LENGTH_LONG).show();
        Intent intentBack = new Intent(ScheduleActivity.this, MainActivity.class);
        startActivity(intentBack);
        finish();

    }
    private boolean isAnyDayChecked(){
        if(getTheWeekCode()!=0){
            return true;
        }
        else {
            return false;
        }

    }


    private int getTheWeekCode(){
        int codeWeek = 0;
        if(chBoxDom.isChecked())
            codeWeek+=2;
        if(chBoxLun.isChecked())
            codeWeek+=4;
        if(chBoxMar.isChecked())
            codeWeek+=8;
        if(chBoxMie.isChecked())
            codeWeek+=16;
        if(chBoxJue.isChecked())
            codeWeek+=32;
        if(chBoxVie.isChecked())
            codeWeek+=64;
        if(chBoxSab.isChecked())
            codeWeek+=128;

        return codeWeek;
    }
    private void cheBoxOffAllExept(CheckBox cheBox, boolean isChecked){

        chBoxDom.setChecked(false);
        chBoxSab.setChecked(false);
        chBoxLun.setChecked(false);
        chBoxMar.setChecked(false);
        chBoxMie.setChecked(false);
        chBoxJue.setChecked(false);
        chBoxVie.setChecked(false);

        cheBox.setChecked(isChecked);
    }

    private boolean isThisBitHigh(int bit, int word){

        if ( ( word & ( dosAlaN(bit) )) > 0)
            return true;
        else
            return false;
    }

    private void setActivityInChargeState(){
        prgBarSchChar.setVisibility(View.VISIBLE);
        relativeLayout.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnCancelar.setEnabled(false);
    }

    private void saveScheProcess(String ip, String mac, final Foco foco){

        final Schedule newSchedule = createSchedule();

        asynkTaskSaveSchedule = new AsynkTaskSaveSchedule(){};
        asynkTaskSaveSchedule.setIp(ip);
        asynkTaskSaveSchedule.setmacOfPlaca(mac);
        asynkTaskSaveSchedule.setNumSalida(foco.getNumDeSalida());
        asynkTaskSaveSchedule.setListener(new AsynkTaskSaveSchedule.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                if(isResultOk){

                    saveNewSchedule(newSchedule,foco);
                    Intent intentBack = new Intent(ScheduleActivity.this, ScheduleMenuActivity.class);
                    intentBack.putExtra("ID",idFoco);
                    startActivity(intentBack);
                    finish();
                }
                else{
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {
                setActivityInChargeState();
            }
        });
        asynkTaskSaveSchedule.execute(newSchedule);
    }

    private void deleteAndSaveScheProcess(final String ip,final String mac, final Foco foco){

        final Schedule scheduleEdit = realm.where(Schedule.class).equalTo("ID",idSche).findFirst();
        asynkTaskDeleteSchedule = new AsynkTaskDeleteSchedule(){};
        asynkTaskDeleteSchedule.setIp(ip);
        asynkTaskDeleteSchedule.setmacOfPlaca(mac);
        asynkTaskDeleteSchedule.setListener(new AsynkTaskDeleteSchedule.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                if(isResultOk) {
                    deleteSchedule(scheduleEdit);
                    launchSaveScheProcess(ip,mac,foco);

                }
                else {
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {
                setActivityInChargeState();
            }
        });
        asynkTaskDeleteSchedule.execute(idSche);

    }

    private void launchDeleteAndSaveScheProcess(final String ip, final String mac, final Foco foco){

        Date currentDate = new Date(System.currentTimeMillis());
        asynkTaskUpdateTime = new AsynkTaskUpdateTime(){};
        asynkTaskUpdateTime.setIp(ip);
        asynkTaskUpdateTime.setMac(mac);
        asynkTaskUpdateTime.setDate(currentDate);
        asynkTaskUpdateTime.setListener(new AsynkTaskUpdateTime.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                if(isResultOk){
                    deleteAndSaveScheProcess(ip,mac,foco);
                }
                else {
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {
                setActivityInChargeState();

            }
        });
        asynkTaskUpdateTime.execute();

    }

    private void launchSaveScheProcess(final String ip, final String mac, final Foco foco){

        Date currentDate = new Date(System.currentTimeMillis());
        asynkTaskUpdateTime = new AsynkTaskUpdateTime(){};
        asynkTaskUpdateTime.setIp(ip);
        asynkTaskUpdateTime.setMac(mac);
        asynkTaskUpdateTime.setDate(currentDate);
        asynkTaskUpdateTime.setListener(new AsynkTaskUpdateTime.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(Boolean isResultOk) {
                if(isResultOk){
                    saveScheProcess(ip, mac, foco);
                }
                else {
                    goHome();
                }
            }

            @Override
            public void onPreExecuteConcluded() {
                setActivityInChargeState();

            }
        });
        asynkTaskUpdateTime.execute();
    }

}
