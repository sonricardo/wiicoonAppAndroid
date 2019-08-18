package com.wiicoon.rubi.wicoon_ligh_controller.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.adapters.PlacaAdapter;
import com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.app.WifiMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskButtonCharge;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskRefresh;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.LanData;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.PRODUCT_NAME;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.checkLocationPermission;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.copyPlacaList;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.getApPassword;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.getLastSixLetterMac;

import static com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskRefresh.MODE_AP_REFRESH;
import static com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskRefresh.MODE_WS_REFRESH;

public class ConfigListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private String KEY =    "pass";
    private String KEY_NEW = "new";
    private String KEY_EDIT = "edit";

    private final int NO_CONNECTED = 0;
    private final int PLACA_CONNECTION = 1;
    private final int LAN_CONNECTION = 2;



    private Realm realm;
    private RealmResults<Placa> placas;
    private RealmResults<LanData> lans;

    private ListView listView;
    private PlacaAdapter placaAdapter;

    private EditText edtTxtPass;
    private TextView txtVieSsid;
    private Button verBtn;
    private ImageView imgVieVerInd;
    private ProgressBar prgBarBtnCha;
    private Button btnSsidList;

    private AsyncTaskRefresh asyncTaskRefresh;




    WifiManager wifiManager;
    ConnectivityManager connectivityManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_list);

        this.enforceIconBar();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiConfiguration wifiConfig = new WifiConfiguration();

        realm = Realm.getDefaultInstance();

        placas = realm.where(Placa.class).findAll();
        lans = realm.where(LanData.class).findAll();

        listView = (ListView) findViewById(R.id.listViewConfigActivity);
        edtTxtPass = (EditText) findViewById(R.id.editTextPass);
        txtVieSsid = (TextView) findViewById(R.id.textViewSSIDname);
        verBtn = (Button)findViewById(R.id.buttonGuardarLan);
        btnSsidList = (Button)findViewById(R.id.buttonSsidList);
        imgVieVerInd = (ImageView) findViewById(R.id.imageViewVerify);
        prgBarBtnCha = (ProgressBar)findViewById(R.id.progressBarButtonCharge);


        placaAdapter = new PlacaAdapter(this, R.layout.list_config_activity, placas);
        listView.setAdapter(placaAdapter);

        listView.setOnItemClickListener(this);
        verBtn.setOnClickListener(this);
        btnSsidList.setOnClickListener(this);


        registerForContextMenu(listView);

        int connectionResult = connectionCheck();
        toolConnectionRefresh(connectionResult);


        placas.addChangeListener(new RealmChangeListener<RealmResults<Placa>>() {
            @Override
            public void onChange(RealmResults<Placa> placas) {
                placaAdapter.notifyDataSetChanged();
            }
        });

        lans.addChangeListener(new RealmChangeListener<RealmResults<LanData>>() {
            @Override
            public void onChange(RealmResults<LanData> lanDatas) {

            }
        });


        edtTxtPass.clearFocus();



    }

    public void enforceIconBar(){
        getSupportActionBar().setIcon(R.mipmap.ic_mesh);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(" Configurar ");


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        actionPassActivity(connectionCheck(), placas.get(position).getID(),false);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()){

            case R.id.return_item:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;

            case R.id.add_item:

               if(isLandDataNeeded())
                   avisoAgregarPlacaSinRed();
               else
                    actionPassActivity(connectionCheck(),-1,true);




                return true;

            case R.id.refresh_item_config_activity:
                item.setEnabled(false);
                int connectionResult = connectionCheck();
                toolConnectionRefresh(connectionResult);
                placas = realm.where(Placa.class).findAll();
                List<Placa> listaPlacas = copyPlacaList(placas);
                asyncTaskRefresh = new AsyncTaskRefresh(){};
                if( WifiMetods.isConnected(connectivityManager) && !WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)) {
                    asyncTaskRefresh.setModeRefresh(MODE_WS_REFRESH);
                }
                else
                    asyncTaskRefresh.setModeRefresh(MODE_AP_REFRESH);

                asyncTaskRefresh.setListener(new AsyncTaskRefresh.MyAsyncTaskListener() {
                    @Override
                    public void onPostExecuteConcluded(List<Placa> placasActualizadas) {

                        actualizarEstadoPlacas(placasActualizadas);
                        item.setEnabled(true);
                    }
                });

                asyncTaskRefresh.execute(listaPlacas);

                return true;


            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_configlist_activity, menu);

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        MenuInflater  inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(this.placas.get(info.position).getMacAddress());

        inflater.inflate(R.menu.config_list_context_menu,menu);



    }



     @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            case R.id.contextMenuBorPla:

                avisoBorrarPlaca(placas.get(info.position));
                break;

            case R.id.contextMenuComRed:
                actionPassActivity(connectionCheck(),placas.get(info.position).getID(),true);



                break;

            case R.id.contextMenuRegresar:

                 break;

            case R.id.contextMenuActivarAp:

                setEneableApPlaca(true,placas.get(info.position));
                break;

            case  R.id.contextMenuDesactivarAP:


                setEneableApPlaca(false,placas.get(info.position));
                break;

            case R.id.contextMenuReset:
                String ip = new String();
                if(placas.get(info.position).getStatus() == Placa.CONNECTED)
                    ip = placas.get(info.position).getIpAddress();
                else if( placas.get(info.position).getStatus() == Placa.AP_MODE)
                    ip = null;

                if(placas.get(info.position).getStatus() != Placa.DISCONNECTED)
                avisoResetPlaca(ip);
                else
                Toast.makeText(this, "necesita estar conectado para resetear",Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onContextItemSelected(item);


        }
        return true;
    }

    @Override
    public void onClick(View v) {

         switch (v.getId()) {

             case R.id.buttonGuardarLan:

                     verBtn.setVisibility(View.INVISIBLE);
                     prgBarBtnCha.setVisibility(View.VISIBLE);
                     AsyncTaskButtonCharge asyncTaskButtonCharge = new AsyncTaskButtonCharge() {
                     };
                     asyncTaskButtonCharge.setListener(new AsyncTaskButtonCharge.MyAsyncTaskListener() {
                         @Override
                         public void onPostExecuteConcluded() {
                             verBtn.setVisibility(View.VISIBLE);
                             prgBarBtnCha.setVisibility(View.INVISIBLE);

                             String ssid = txtVieSsid.getText().toString();
                             String password = edtTxtPass.getText().toString();
                             deleteLan(ssid);
                             saveNewLan(ssid, password, -1, LanData.LOCAL_LAN);
                             toolRefresh();


                         }

                      }
                      );
                     asyncTaskButtonCharge.execute();

                 break;

             case R.id.buttonSsidList:

                 checkLocationPermission( ConfigListActivity.this );
                 if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                             checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) ) {

                     AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
                     builderSingle.setIcon(R.mipmap.ic_wifi_signal);
                     builderSingle.setTitle("Seleccione una red Wifi");


                     final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);

                     if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
                         arrayAdapter.addAll(WifiMetods.lanOnTheArea(wifiManager));


                     builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             dialog.dismiss();
                         }
                     });


                     builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             String wichName = arrayAdapter.getItem(which);
                             txtVieSsid.setText(wichName);
                             if (isSsidOnLanData(wichName)) {
                                 edtTxtPass.setText(findLanDataWhitThisSsid(wichName).getPsk());
                                 imgVieVerInd.setImageResource(R.mipmap.ic_palomita);
                             } else
                                 setToolBarLanNew();

                         }
                     });
                     builderSingle.show();
                 }

             break;

         default: break;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                else

                break;

            default: super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }


    }





    private int connectionCheck() {

        if (!WifiMetods.isConnected(connectivityManager)) {
            return NO_CONNECTED;
        }

        String mySsid = WifiMetods.getMyConnectionSSID(wifiManager);

        if ( mySsid.contains(PRODUCT_NAME)){
            if(!isLanSaved()){
                int myNetworkId = WifiMetods.getNetworkID(wifiManager);
                saveNewLan(mySsid,getApPassword(getLastSixLetterMac(mySsid)),myNetworkId,LanData.AP_DE_PLACA);
            }

            return PLACA_CONNECTION;
        }

        return LAN_CONNECTION;
    }

    private boolean isLanSaved(){

        String mySsid = WifiMetods.getMyConnectionSSID(wifiManager);
        LanData currentLan = new LanData();
        for (LanData i : lans) {
            if (i.getSSID().equals(mySsid)) {
                return true;
            }
        }

        return false;
    }

    private void saveNewLan(String ssid, String password, int networkId, int typeConn){

        LanData newLan = new LanData(ssid, false, typeConn, networkId);
        newLan.setPsk(password);
        realm.beginTransaction();
        realm.copyToRealm(newLan);
        realm.commitTransaction();
        lans = realm.where(LanData.class).findAll();
    }

    private void deleteLan(String ssid){

        if(isLanSaved()){

            LanData currentLan = new LanData();
            for (LanData i : lans) {
                if (i.getSSID().equals(ssid)) {
                    realm.beginTransaction();
                    i.deleteFromRealm();
                    realm.commitTransaction();
                    break;
                }
            }


        }
        lans = realm.where(LanData.class).findAll();
    }

    private LanData findMyCurrentLanData(){

        if(isLanSaved()){
            String mySsid = WifiMetods.getMyConnectionSSID(wifiManager);
            LanData currentLan = new LanData();
            for (LanData i : lans) {
                if (i.getSSID().equals(mySsid)) {
                    return i;
                }
            }
        }
        return null;
    }

    private LanData findLanDataWhitThisSsid(String ssid){

        for(int i=0; i< lans.size(); i++){
            if(lans.get(i).getSSID().equals(ssid))
                return lans.get(i);

        }
        return null;
    }

    private boolean isSsidOnLanData(String ssidToFind){


        return getSsidListFromLanData(lans).contains(ssidToFind);

    }

    private List<String> getSsidListFromLanData(List<LanData> lanDatas){

    List<String> listaSsids = new ArrayList<>();
    listaSsids.clear();
    for(int i = 0; i < lanDatas.size(); i++){

        listaSsids.add(lanDatas.get(i).getSSID());
    }
    return listaSsids;
    }

    private void toolConnectionRefresh(int connectionResult){

             if(connectionResult != NO_CONNECTED ){
                 txtVieSsid.setText(WifiMetods.getMyConnectionSSID(wifiManager));

                 if(isLanSaved()) {
                     imgVieVerInd.setImageResource(R.mipmap.ic_palomita);
                     edtTxtPass.setText(findMyCurrentLanData().getPsk());
                 }
             }

    }

    private boolean isLandDataNeeded(){


            String ssidTxt = txtVieSsid.getText().toString();

            if(isSsidOnLanData(ssidTxt))
                return false;
            else
            return true;
    }

    private void toolRefresh(){


        if(isSsidOnLanData(txtVieSsid.getText().toString())) {
            edtTxtPass.setText(findLanDataWhitThisSsid( txtVieSsid.getText().toString() ).getPsk());
            imgVieVerInd.setImageResource(R.mipmap.ic_palomita);
        }

    }


    private void setToolBarLanNew(){

        imgVieVerInd.setImageResource(R.mipmap.ic_marco);
        edtTxtPass.setText("");
    }

    private void askForPass(){
        edtTxtPass.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtTxtPass, InputMethodManager.SHOW_IMPLICIT);
    }

    private  void showMsgAskPass(){
        Toast.makeText(this,"password requerido",Toast.LENGTH_LONG).show();
    }


    private void actionPassActivity(int connectionResult, int ID, boolean isShareLan){

         String passwordLan = new String();
         String ssidLan =     new String();


         if(isShareLan){
            if(! isLandDataNeeded()) {
                 passwordLan = edtTxtPass.getText().toString();
                 ssidLan = txtVieSsid.getText().toString();
                 avisoPasarSsidYpass(ssidLan,passwordLan,ID,connectionResult);
            }
            else {
                showMsgAskPass();
                askForPass();
            }
         }
        else {
            passwordLan = null;
            ssidLan = null;
            passActivityAdd(ID,passwordLan,ssidLan,connectionResult, isShareLan);
        }

     }

    private void passActivityAdd(int ID,String passwordLan, String ssidLan, int connectionResult, boolean isShareLan){
        Intent intent = new Intent(ConfigListActivity.this, AddActivity.class);

        intent.putExtra("id", ID);
        intent.putExtra("passwordLan", passwordLan);
        intent.putExtra("ssidLan", ssidLan);
        intent.putExtra("statusConnection",connectionResult);
        intent.putExtra("shareLan",isShareLan);
        startActivity(intent);

    }

    private void actualizarEstadoPlacas(List<Placa> placasActualizadas){

        realm.beginTransaction();

        for(int i = 0; i<placas.size(); i++){
            for(int j =0; j<placasActualizadas.size();j++){
                if(placas.get(i).getMacAddress().equals(placasActualizadas.get(j).getMacAddress())){

                    placas.get(i).setStatus(placasActualizadas.get(j).getStatus());
                    if(placasActualizadas.get(j).getStatus()==Placa.CONNECTED)
                        placas.get(i).setIpAddress(placasActualizadas.get(j).getIpAddress());

                    realm.copyToRealmOrUpdate(placas.get(i));


                }
            }
        }
        realm.commitTransaction();
        placas = realm.where(Placa.class).findAll();
    }
    private void avisoPasarSsidYpass(final String ssid, final String password, final int ID, final int connectinResult){

        AlertDialog.Builder build = new AlertDialog.Builder(this);

        build.setTitle("ATENCION");
        build.setMessage("Esta seguro que quiere pasar esta red a su placa?  (revise password)");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_red_confirmation, null);
        build.setView(view);

        TextView txtSsid = (TextView) view.findViewById(R.id.textViewSsidConf);
        TextView txtPass = (TextView) view.findViewById(R.id.textViewPassConf);
        txtPass.setText(password);
        txtSsid.setText(ssid);


        build.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                passActivityAdd(ID,password,ssid,connectinResult,true);
            }
        });
        build.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(ID == -1)
                avisoAgregarPlacaSinRed();
            }
        });

        AlertDialog dialog = build.create();
        dialog.show();
    }

    private void avisoResetPlaca(final String ip){

        AlertDialog.Builder build = new AlertDialog.Builder(this);


        build.setTitle("ALERTA");
        build.setMessage("desea resetar la placa? olvidara su conexion a red y horarios");

        build.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestHttpMetods.httpRequestResetPlaca( ip, new RequestHttpMetods.OnResponseRecived() {
                    @Override
                    public void onResponse(Placa placa, boolean isResponseCorrect) {
                        if (placa != null) {
                            if(placa.getMacAddress() != null){
                                Toast.makeText( getApplicationContext(),"placa reseteada", Toast.LENGTH_SHORT).show();
                                Placa placaReset = realm.where(Placa.class).contains("macAddress",placa.getMacAddress()).findFirst();
                                if(placaReset != null) {
                                    deleteScheduleFromPlaca(placaReset);
                                    launchAsynnkRefresh();
                                    Toast.makeText( getApplicationContext(),"placa reseteada", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                        else {


                        }
                    }
                });
            }
        });
        build.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = build.create();
        dialog.show();
    }

    private  void avisoBorrarPlaca(final Placa placa){

        AlertDialog.Builder build = new AlertDialog.Builder(this);


        build.setTitle("Aviso");
        build.setMessage("¿seguro quiere borrar placa: "+placa.getMacAddress()+" ?");

        build.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            borrarPlaca(placa);

            }
        });
        build.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = build.create();
        dialog.show();
    }

    private  void avisoAgregarPlacaSinRed(){

        AlertDialog.Builder build = new AlertDialog.Builder(this);


        build.setTitle("Aviso");
        build.setMessage("desea acceder al menu de agregar placas sin tener ninguna red que proporcionarle \n desea continuar?");

        build.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                actionPassActivity(connectionCheck(),-1,false);
            }
        });
        build.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askForPass();
            }
        });

        AlertDialog dialog = build.create();
        dialog.show();
    }

    private void borrarPlaca(Placa placa){
        realm.beginTransaction();

        for(int i = 0; i<placa.getFocos().size(); i++){
            placa.getFocos().get(i).getSchedules().deleteAllFromRealm();
        }
        placa.getFocos().deleteAllFromRealm();

        placa.deleteFromRealm();
        realm.commitTransaction();
    }

    private void deleteScheduleFromPlaca(Placa placa){
        realm.beginTransaction();

        for(int i = 0; i<placa.getFocos().size(); i++){
            placa.getFocos().get(i).getSchedules().deleteAllFromRealm();
        }
        realm.commitTransaction();
    }

    private void launchAsynnkRefresh(){

        placas = realm.where(Placa.class).findAll();
        List<Placa> listaPlacas = copyPlacaList(placas);
        asyncTaskRefresh = new AsyncTaskRefresh(){};
        if( WifiMetods.isConnected(connectivityManager) && !WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)) {
            asyncTaskRefresh.setModeRefresh(MODE_WS_REFRESH);
        }
        else
            asyncTaskRefresh.setModeRefresh(MODE_AP_REFRESH);

        asyncTaskRefresh.setListener(new AsyncTaskRefresh.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(List<Placa> placasActualizadas) {

                actualizarEstadoPlacas(placasActualizadas);

            }
        });

        asyncTaskRefresh.execute(listaPlacas);

    }

    private void setEneableApPlaca(final boolean eneable, Placa placa){

        if (placa.getStatus() == Placa.CONNECTED) {

            String ip = placa.getIpAddress();


            RequestHttpMetods.httpSetApEneable(ip, eneable, new RequestHttpMetods.OnResponseRecived() {
                @Override
                public void onResponse(Placa placa, boolean isResponseCorrect) {
                    if(placa!=null){
                        if(eneable) {
                            Toast.makeText(getApplicationContext(), "AP activado", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "AP desactivado", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"fallo en la comunicacion",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        else{

            Toast.makeText(this,"es necesario estar conectado a una red",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onResume() {
        //setToolBarLanNew();
        int connectionResult = connectionCheck();
        //toolConnectionRefresh(connectionResult);

        placas = realm.where(Placa.class).findAll();
        List<Placa> listaPlacas = copyPlacaList(placas);
        asyncTaskRefresh = new AsyncTaskRefresh(){};
        if( WifiMetods.isConnected(connectivityManager) && !WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)) {
            asyncTaskRefresh.setModeRefresh(MODE_WS_REFRESH);
        }
        else
            asyncTaskRefresh.setModeRefresh(MODE_AP_REFRESH);

        asyncTaskRefresh.setListener(new AsyncTaskRefresh.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded(List<Placa> placasActualizadas) {

                actualizarEstadoPlacas(placasActualizadas);



            }
        });

        asyncTaskRefresh.execute(listaPlacas);


        super.onResume();
    }



    @Override
    protected void onStop() {

        asyncTaskRefresh.cancel(true);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
