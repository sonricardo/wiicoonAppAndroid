package com.wiicoon.rubi.wicoon_ligh_controller.app;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.PRODUCT_NAME;

/**
 * Created by Rubi on 10/26/2017.
 */

public class ShowAlerts {




    public static void showInfoCompany(Context context){

        AlertDialog.Builder build = new AlertDialog.Builder(context);

        build.setTitle(" "+PRODUCT_NAME+" ");
        build.setMessage("Producto creado por: \nIng. Luis Fernando Perez Salcedo \nIng. Ricardo Coronado Galindo \n\nDireccion: Av Americas #211\nGuadalajara, Jalisco, Mexico" +
                            "\n\nContacto: wiicoon.domotica@gmail.com\n(044)-33-10-73-31-41\n(044)-33-13-37-40-50\n\n");



        AlertDialog dialog = build.create();
        dialog.show();
    }

    public static void showMessageNeedConnection(Context context) {


        Toast.makeText(context, "Para acceder a los horarios es necesario que este conectado", Toast.LENGTH_SHORT).show();


    }


}
