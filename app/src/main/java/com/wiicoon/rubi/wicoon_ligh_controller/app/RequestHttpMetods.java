package com.wiicoon.rubi.wicoon_ligh_controller.app;

import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;
import com.wiicoon.rubi.wicoon_ligh_controller.services.RequestHttpService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rubi on 9/2/2017.
 */

public class RequestHttpMetods {

    public static final String IP_PLACA_DEFAULT = "192.168.4.1";
    private static final String URL_AP = "http://"+IP_PLACA_DEFAULT+"/";


    public final static int  AP_NEW = 0;
    public final static int  AP_GET_IP = 1;
    public final static int  WS_GET_DATA = 2;
    public final static int  FOCO_COMMAND = 3;
    public final static int  RESET_PLACA = 4;

    public final static String  IP_SERVIDOR = "iotcontrol.ddns.net/api";

    private final int IDDLE = 0;
    private final int WAITING = 1;
    private final int CORRECT = 2;
    private final int INCORRECT = 3;





    public static Call<Placa> httpRequest(int typeRequest, String ipWs, String SSID, String password, int estado, int numSalida, final OnResponseRecived onResponseRecived){

        String URL;

        if( ( typeRequest == WS_GET_DATA || typeRequest == FOCO_COMMAND ||  typeRequest == RESET_PLACA) && ipWs != null )
            URL = "http://"+ipWs+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);
        Call<Placa> placaCall;

        switch (typeRequest){

            case AP_NEW:        placaCall = service.getPlacaAP( SSID, password);
                break;

            case AP_GET_IP:   placaCall = service.getIP( );
                break;

            case WS_GET_DATA:   placaCall = service.getPlacaWS( SSID, password);
                break;

            case FOCO_COMMAND:   placaCall = service.setFoco(estado, numSalida, null);
                break;

            case RESET_PLACA:
                placaCall = service.resetPlaca();
                break;

            default:             placaCall = service.getPlacaWS( SSID, password);
                break;

        }
        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {
                onResponseRecived.onResponse( null , false);
            }
        });
        return placaCall;
    }

    public static Call<Placa> httpRequestCommand(String ip, String macTarget,int salida, int estado, final OnResponseRecived onResponseRecived){

        String URL;

        if( ip!= null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);
        Call<Placa> placaCall;
        placaCall = service.setFoco(estado,salida,macTarget);

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {
                onResponseRecived.onResponse( null , false);
            }
        });
        return placaCall;
    }



    public static Call<Placa> httpRequestConfig(String ip, String SSID, String password, final OnResponseRecived onResponseRecived){

         String URL;

        if( ip!= null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);
        Call<Placa> placaCall;
        placaCall = service.getPlacaAP( SSID, password);

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {
                onResponseRecived.onResponse( null , false);
            }
        });
        return placaCall;
    }


    public static Call<Placa> httpRequestDataInfo(String ip, final OnResponseRecived onResponseRecived){

        String URL;

        if( ip!= null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
         RequestHttpService service = retrofit.create(RequestHttpService.class);

        Call<Placa> placaCall;
        placaCall = service.getIP();

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {

                onResponseRecived.onResponse(null , false);

            }
        });
        return placaCall;
    }


    public static Call<Placa> httpRequestDataInfoWithMac(String ip, final String macReq, final OnResponseRecivedWhitMac onResponseRecivedWhitMac){

        String URL;
        final String mac = macReq;

        if( ip!= null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);

        Call<Placa> placaCall;
        placaCall = service.dataInfoWhithMac(macReq);

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecivedWhitMac.onResponseWhitMac(placa , true, macReq);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {

                onResponseRecivedWhitMac.onResponseWhitMac(null , false, macReq);

            }
        });
        return placaCall;
    }

    public static Call<Placa> httpRequestCreateSchedule(String ip,int hora, int minute, int days, int schID, int numSal, int typeTurn, int isRepetitive, final OnResponseRecived onResponseRecived){

        String URL;

        if( ip!= null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);

        Call<Placa> placaCall;
        placaCall = service.createSchedule(hora,minute,days,schID,numSal,typeTurn,isRepetitive);

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {
                onResponseRecived.onResponse( null , false);
            }
        });
        return placaCall;
    }

    public static Call<Placa> httpRequestDeleteSchedule(String ip, int idSche, final OnResponseRecived onResponseRecived){

        String URL;

        if( ip!= null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);

        Call<Placa> placaCall;
        placaCall = service.deleteSchedule(idSche);

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {
                onResponseRecived.onResponse( null , false);
            }
        });
        return placaCall;
    }

    public static Call<Placa> httpRequestResetPlaca(String ip, final OnResponseRecived onResponseRecived){

        String URL;

        if( ip != null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);

        Call<Placa> placaCall;
        placaCall = service.resetPlaca();

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {
                onResponseRecived.onResponse( null , false);
            }
        });
        return placaCall;
    }


    public static Call<Placa> httpRequestUpDateTime(String ip, int hour, int minute, int second, int day, int month, int year , final OnResponseRecived onResponseRecived){

        String URL;

        if( ip!= null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);

        Call<Placa> placaCall;
        placaCall = service.upDateTimeForPlaca(hour,minute,second,day,month,year);

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {
                onResponseRecived.onResponse( null , false);
            }
        });
        return placaCall;
    }

    public static Call<Placa> httpSetApEneable(String ip, boolean eneableCommand , final OnResponseRecived onResponseRecived){

        String URL;

        if( ip!= null )
            URL = "http://"+ip+"/";
        else
            URL = URL_AP;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestHttpService service = retrofit.create(RequestHttpService.class);

        Call<Placa> placaCall;
        int command;
        if(eneableCommand)
        command = 1;
        else
        command = 0;


        placaCall = service.setApEneable(command);

        placaCall.enqueue(new Callback<Placa>() {
            @Override
            public void onResponse(Call<Placa> call, Response<Placa> response) {
                Placa placa = response.body();
                onResponseRecived.onResponse(placa , true);

            }

            @Override
            public void onFailure(Call<Placa> call, Throwable t) {
                onResponseRecived.onResponse( null , false);
            }
        });
        return placaCall;
    }


    public interface OnResponseRecived {
        void onResponse(Placa placa, boolean isResponseCorrect );

    }

    public interface OnResponseRecivedWhitMac {
        void onResponseWhitMac(Placa placa, boolean isResponseCorrect, String mac );

    }

}
