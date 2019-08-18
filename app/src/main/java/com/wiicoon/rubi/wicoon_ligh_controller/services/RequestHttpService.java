package com.wiicoon.rubi.wicoon_ligh_controller.services;

import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Rubi on 9/2/2017.
 */

public interface RequestHttpService {

           //AP_NEW
         @GET("config")
         Call<Placa> getPlacaAP(@Query("ssid") String ssid , @Query("pass") String password );


       //AP_GET_DATA
         @GET("data_info")
         Call<Placa> getIP ();

        @GET("data_info")
        Call<Placa> dataInfoWhithMac (@Query("mac") String mac);

        @GET("ap_config")
        Call<Placa> setApEneable(@Query("estado") int eneable );

      //WS_GET_DATA
        @GET("data_info")
        Call<Placa> getPlacaWS (@Query("ssid") String ssid , @Query("pass") String password );

        //FOCO_COMMAND
        @GET("foco_control")
        Call<Placa> setFoco(@Query("estado") int setEstado , @Query("foco") int numFoco, @Query("mac") String mac);

        @GET("data_reset")
            Call<Placa> resetPlaca();
        //borrar schedule
        @GET("alarma_borrar")
        Call<Placa> deleteSchedule(@Query("id") int schID);

        //borrar schedule
        @GET("alarma_config")
        Call<Placa> createSchedule(@Query("hora") int schHora, @Query("min") int schMinute, @Query("dia") int schDays, @Query("id") int schID,
                                    @Query("foco") int schNumSal, @Query("estado") int typeTurn,@Query("rep") int schIsRep);

        @GET("time_config")
        Call<Placa> upDateTimeForPlaca(@Query("hora") int hora, @Query("min") int minute, @Query("seg") int second, @Query("dia") int dayMonth, @Query("mes") int month, @Query("ano") int año);

}
