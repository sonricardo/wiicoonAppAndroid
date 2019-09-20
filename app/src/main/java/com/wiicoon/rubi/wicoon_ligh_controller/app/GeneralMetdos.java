package com.wiicoon.rubi.wicoon_ligh_controller.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by Rubi on 10/22/2017.
 */

public class GeneralMetdos {


    public static final int NO_CONNECTED = 0;
    public static final int PLACA_CONNECTION = 1;
    public static final int LAN_CONNECTION = 2;


    public static final int IDDLE = 0;
    public static final int WAITING = 1;
    public static final int CORRECT = 2;
    public static final int INCORRECT = 3;

    public static final int MODE_AP = 0;
    public static final int MODE_WS = 1;

    public static String PRODUCT_NAME = "Wiicoon";

    public static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 100;

    public static void delay(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getSsidWhitMac(String mac){

        String productName = "Wiicoon";

        mac = getLastSixLetterMac(mac);
        mac = mac.substring( mac.length()-6 , mac.length());
        mac = productName+"_"+mac;

        return mac;
    }

    public static String getLastSixLetterMac(String mac){

        mac = getMacNoPointsFormat(mac);

        if(mac.length() >= 6)
            mac = mac.substring( mac.length()-6 , mac.length());

        return mac;
    }

    public static String getMacNoPointsFormat(String mac){

        if( mac.indexOf(":")>= 0){
            mac = mac.replace(":","");
        }
        return mac;
    }

    public static String getApPassword(String macAdress){

        String password = new String();
        int indice;
        char caracteres[] = new  char[4];


        if(macAdress.indexOf(":") >= 0) {
            indice = macAdress.lastIndexOf(":");
            caracteres[0] = macAdress.charAt(indice - 2);
            caracteres[1] = macAdress.charAt(indice - 1);
            caracteres[2] = macAdress.charAt(indice + 1);
            caracteres[3] = macAdress.charAt(indice + 2);
        }
        else{
            caracteres[0] = macAdress.charAt(macAdress.length() - 4);
            caracteres[1] = macAdress.charAt(macAdress.length() - 3);
            caracteres[2] = macAdress.charAt(macAdress.length() - 2);
            caracteres[3] = macAdress.charAt(macAdress.length() - 1);
        }

        for (int i=0; i<4 ; i++){
            switch (caracteres[i]){
                case '0': password = password + "00"; break;
                case '1': password = password + "01"; break;
                case '2': password = password + "02"; break;
                case '3': password = password + "03"; break;
                case '4': password = password + "04"; break;
                case '5': password = password + "05"; break;
                case '6': password = password + "06"; break;
                case '7': password = password + "07"; break;
                case '8': password = password + "08"; break;
                case '9': password = password + "09"; break;
                case 'A': password = password + "10"; break;
                case 'B': password = password + "11"; break;
                case 'C': password = password + "12"; break;
                case 'D': password = password + "13"; break;
                case 'E': password = password + "14"; break;
                case 'F': password = password + "15"; break;
                default: break;
            }
        }
        return password;
    }

    public static List<Placa> copyPlacaList (List<Placa> placasDb){

        List<Placa> newPlacaList = new ArrayList<>();

        for(int i=0;i<placasDb.size();i++){
            Placa currentPlaca = new Placa(placasDb.get(i).getMacAddress(), placasDb.get(i).getIpAddress(), placasDb.get(i).getSSIDname(),
                    placasDb.get(i).getSSIDpass(),  placasDb.get(i).getStatus(),  placasDb.get(i).getNumSalidas());


            newPlacaList.add(currentPlaca);

        }

        return newPlacaList;
    }








    public static void checkLocationPermission(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

        }
    }

    public static List<String> listaBorrandoNombresIguales(List<String> listaCompleta, List<String> listaExistente){

        List<String> listaResponse = new ArrayList<>();
        listaResponse.clear();

        for(int i = 0; i< listaCompleta.size(); i++) {
            listaResponse.add(listaCompleta.get(i));
        }
        for(int i = 0; i< listaCompleta.size(); i++){
            for(int j = 0 ; j < listaExistente.size(); j++){

                if( listaCompleta.get(i).equals( listaExistente.get(j) ) ) {
                    listaResponse.remove(listaResponse.indexOf(listaCompleta.get(i)));
                    break;
                }

            }
        }
        return listaResponse;
    }

    public static int dosAlaN(int n){
        int result = 1;
        if(n == 0)
            return 1;

        for(int i=0;i<n;i++){
            result = result * 2;
        }
        return result;
    }

    public static int daysOfdiff(Date oldDate, Date currentDate){
        int diffDays;
        int daysOnOld = 0;
        int daysOnNew = 0;
        Calendar old = Calendar.getInstance();
        old.setTime(oldDate);

        daysOnOld = daysOnOld + old.get(Calendar.DAY_OF_WEEK);
        daysOnNew = daysOnNew + currentDate.getDate();

        daysOnOld = daysOnOld + daysOnThisYear(oldDate.getYear()+1900, oldDate.getMonth());
        daysOnNew = daysOnNew + daysOnThisYear(currentDate.getYear()+1900, currentDate.getMonth());

        daysOnOld = daysOnOld + daysSince2017(oldDate.getYear()+1900);
        daysOnNew = daysOnNew + daysSince2017(currentDate.getYear()+1900);

        diffDays = daysOnNew - daysOnOld;
        return diffDays;
    }

    private static int daysSince2017(int year){
        int days=0;
        for(int i=0; 2017+i < year; i++){

            if((2017+i)%4 == 0){
                days+=366;
            }
            else
                days+=365;
        }
        return days;
    }

    private static int daysOnThisYear(int year, int month){
        int days = 0;

        for(int i=0; i< month; i++){

            days = days + getDaysOnMonth(i, year%4);
        }

        return days;
    }

    private static  int getDaysOnMonth(int month, int viciesto){
        int dayOnMonth =0;

        switch (month){
            case 0: dayOnMonth = 31; break;

            case 1: if(viciesto == 0)
                        dayOnMonth = 29;
                    else
                        dayOnMonth = 28;
                    break;
            case 2: dayOnMonth = 31;break;
            case 3: dayOnMonth = 30;break;
            case 4: dayOnMonth = 31;break;
            case 5: dayOnMonth = 30;break;
            case 6: dayOnMonth = 31;break;
            case 7: dayOnMonth = 31;break;
            case 8: dayOnMonth = 30;break;
            case 9: dayOnMonth = 31;break;
            case 10: dayOnMonth = 30;break;
            case 11: dayOnMonth = 31; break;
            default:break;
        }
        return dayOnMonth;
    }
}
