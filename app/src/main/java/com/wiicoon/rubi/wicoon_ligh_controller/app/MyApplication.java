package com.wiicoon.rubi.wicoon_ligh_controller.app;

import android.app.Application;

import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.LanData;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Schedule;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;



/**
 * Created by Rubi on 8/22/2017.
 */

public class MyApplication extends Application {

    public  static AtomicInteger placaID = new AtomicInteger();
    public  static AtomicInteger focoID = new AtomicInteger();
    public  static AtomicInteger lanID = new AtomicInteger();
    public  static AtomicInteger ScheduleID = new AtomicInteger();



    @Override
    public void onCreate() {


        super.onCreate();
        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        placaID = getIdByTable(realm, Placa.class);
        focoID = getIdByTable(realm, Foco.class);
        lanID = getIdByTable(realm, LanData.class);
        ScheduleID = getIdByTable(realm, Schedule.class);
        realm.close();


    }

    private void setUpRealmConfig(){

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size()>0) ? new AtomicInteger( results.max("ID").intValue() ): new AtomicInteger();

    }
}