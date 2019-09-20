package com.wiicoon.rubi.wicoon_ligh_controller.activities;

/*
    Ricardo Coronado Galindo
    Sonricardo 2017
    Wiicoon
    documented sep 2019

    MAIN ACTIVITY
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.adapters.FocoAdapter;
import com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.app.WifiMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskCast;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskDelayAndRepeat;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskRefresh;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.IP_SERVIDOR;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.httpRequestDataInfoWithMac;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.ShowAlerts.showInfoCompany;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.ShowAlerts.showMessageNeedConnection;
import static com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskRefresh.MODE_AP_REFRESH;
import static com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskRefresh.MODE_INT_REM_REFRESH;
import static com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsyncTaskRefresh.MODE_WS_REFRESH;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;   //
    private FocoAdapter focoAdapter;

    private Realm realm;
    private RealmResults<Foco> focos;
    private RealmResults<Placa> placas;
    private Placa placa;
    private List<Foco> focosList;
    private final int INTERNET_CODE = 100;
    private String productName ="Wiicoon";

    private final int IDDLE = 0;
    private final int WAITING = 1;
    private final int CORRECT = 2;
    private final int INCORRECT = 3;

    private final int COUNTER_EACH = 12;

    private int counterCasting = COUNTER_EACH;
    private boolean focosActualizadosCommand = false;

    boolean firsRefreshConcluded;

    private ProgressBar prgBarCast;
    private MenuItem itemSearch;
    private boolean doingCasting = false;
    private Switch switchInternet;

    private int stateHttp = IDDLE;
    private int responseHttp = IDDLE;
    private int commandReqActivate = IDDLE;

    AsyncTaskRefresh asyncTaskRefresh = new AsyncTaskRefresh();
    AsyncTaskCast asyncTaskCast = new AsyncTaskCast();
    AsynkTaskDelayAndRepeat asynkTaskDelayAndRepeat = new AsynkTaskDelayAndRepeat();

    AsyncTaskRefresh.MyAsyncTaskListener listener;
    AsynkTaskDelayAndRepeat.MyAsyncTaskListener listenerDelayAndRep;

    WifiManager wifiManager;
    ConnectivityManager connectivityManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  //default configuration
        setContentView(R.layout.activity_main);  //set the layout

        this.enforceIconBar();

        firsRefreshConcluded = false;
        realm = Realm.getDefaultInstance();   //get a realm instance

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiConfiguration wifiConfig = new WifiConfiguration();

        prgBarCast = (ProgressBar)findViewById(R.id.progressBarCasting);
        switchInternet = (Switch)findViewById(R.id.switchInternet);

        focos = realm.where(Foco.class).findAll();
        focosList = new ArrayList<>();
        focosList = copyFocosList(focos, focosList);
        placas = realm.where(Placa.class).findAll();


        focos.addChangeListener(new RealmChangeListener<RealmResults<Foco>>() {
            @Override
            public void onChange(RealmResults<Foco> focosChange) {

                focosList = copyFocosList(focosChange, focosList);
                focoAdapter.notifyDataSetChanged();

            }
        });


        listView = (ListView) findViewById(R.id.listViewMain);

        focoAdapter = new FocoAdapter(MainActivity.this, R.layout.list_main_activity, focosList, placas);
        listView.setAdapter(focoAdapter);

        listView.setOnItemClickListener(MainActivity.this);
        registerForContextMenu(listView);

    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        final Placa placaCommand = realm.where(Placa.class).equalTo("ID", focosList.get(position).getIDplaca() ).findFirst();

        int estadoRequest;

        if (focosList.get(position).getEstado() ==  Foco.ON)
        estadoRequest = Foco.OFF;
        else
        estadoRequest = Foco.ON;

        String ipCommand;
        String macTarget = placaCommand.getMacAddress();

        switch (placaCommand.getStatus()){
            case Placa.AP_MODE: ipCommand = null; break;
            case Placa.CONNECTED: ipCommand = placaCommand.getIpAddress(); break;
            case Placa.INTERNET_REMOTE: ipCommand = IP_SERVIDOR; break;
            default: ipCommand = null; break;

        }




        if ( placaCommand.getStatus()!= Placa.DISCONNECTED) {

            RequestHttpMetods.httpRequestCommand( ipCommand, macTarget ,focosList.get(position).getNumDeSalida(), estadoRequest , new RequestHttpMetods.OnResponseRecived() {
                @Override
                public void onResponse(Placa placa, boolean isResponseCorrect) {
                    if (isResponseCorrect) {
                        responseHttp = CORRECT;
                        stateHttp = IDDLE;
                        commandReqActivate = IDDLE;
                        if (placa != null) {
                            for (int i = 0; i < focosList.size(); i++) {
                                if (focosList.get(i).getIDplaca() == placaCommand.getID()) {
                                    for (int j = 0; j < placa.getFocos().size(); j++) {
                                        if (focosList.get(i).getNumDeSalida() == placa.getFocos().get(j).getNumDeSalida()) {
                                            focosList.get(i).setEstado(placa.getFocos().get(j).getEstado());
                                        }
                                    }


                                }
                            }

                            focoAdapter.notifyDataSetChanged();
                            focosActualizadosCommand = true;

                        }
                    }
                    else {
                        realm.beginTransaction();
                        placaCommand.setStatus(Placa.DISCONNECTED);
                        realm.copyToRealmOrUpdate(placaCommand);
                        realm.commitTransaction();
                        focoAdapter.notifyDataSetChanged();
                        responseHttp = INCORRECT;
                        stateHttp = IDDLE;
                        commandReqActivate = IDDLE;

                    }
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_main_activity, menu);
        itemSearch = (MenuItem) menu.findItem(R.id.search_item);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        actualizarEstadoFocosDB(focosList);
        switch (item.getItemId()){

            case R.id.info_item:
                showInfoCompany(this);
                return true;

            case R.id.config_item:
                Intent intent = new Intent(MainActivity.this, ConfigListActivity.class);
                //asyncTaskRefresh.cancel(true);
                startActivity(intent);

                return true;

            case R.id.search_item:

                    if(doingCasting != true) {

                        lanzarCasting();
                    }


                return true;
            case R.id.Schedule_item:
                showFocosForScheduleNew(focos);

                return true;


            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        MenuInflater  inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(this.focos.get(info.position).getName());

        inflater.inflate(R.menu.main_context_menu,menu);



    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        actualizarEstadoFocosDB(focosList);


        switch (item.getItemId()) {

            case R.id.contextMenuBorFoc:
                showAlertDeleteFoco(focos.get(info.position));

            break;

            case R.id.contextMenuEdtNom:

                showAlertForEditingFocoName("Nuevo Nombre", "Cambio de nombre", focos.get(info.position) );


            break;

            case R.id.contextMenuReg:


            break;

            case R.id.contextMenuSch:

                int idFoco = focos.get(info.position).getID();
                passToScheduleMenuActivity(idFoco);


            break;

            default:
             return super.onContextItemSelected(item);
        }

    return true;
    }


    public void enforceIconBar(){
        getSupportActionBar().setIcon(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

    }

    public void showInfoMessage(){
        //mostrar pantalla de informacion de wicoon fuck yeah!!!
    }

    private void showAlertForEditingFocoName(String title, String message,final Foco foco){

        AlertDialog.Builder build = new AlertDialog.Builder(this);

        if( title != null ){ build.setTitle(title);}
        if(message != null){ build.setMessage(message); }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_foco_name, null);
        build.setView(view);

        final EditText input = (EditText) view.findViewById(R.id.editTextFocoNameInput);
        input.setText(foco.getName());
        input.setSelection(input.length());


        build.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String focoName = input.getText().toString().trim();
                if( focoName.length() == 0)

                    Toast.makeText(getApplicationContext(),"No escribio nada joven",Toast.LENGTH_SHORT).show();
                else
                    editFocoName(focoName, foco);
            }
        });


        AlertDialog dialog = build.create();
        dialog.show();
    }

    private void showAlertDeleteFoco(final Foco foco){

        AlertDialog.Builder build = new AlertDialog.Builder(this);


         build.setTitle("Borrar " );
         build.setMessage("esta seguro que quiere borrar el foco "+foco.getName());

        build.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFoco(foco);
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

    private void editFocoName(String focoName, Foco foco) {
        realm.beginTransaction();
        foco.setName(focoName);
        realm.copyToRealmOrUpdate(foco);
        realm.commitTransaction();
    }

    private void deleteFoco(Foco foco){
        realm.beginTransaction();
        foco.getSchedules().deleteAllFromRealm();
        foco.deleteFromRealm();
        realm.commitTransaction();
    }

    private List<Foco> copyFocosList( List<Foco> focosDb, List<Foco> listFocos ) {

        for(int i=0;i<focosDb.size();i++){


            Foco foco = new Foco(focosDb.get(i).getIDplaca(), focosDb.get(i).getName(), focosDb.get(i).getNumDeSalida(), focosDb.get(i).getEstado());
            foco.setSchedules(focosDb.get(i).getSchedules());
            if(listFocos.size() <= i)
                listFocos.add(foco);
            else {
                foco.setEstado(listFocos.get(i).getEstado());
                listFocos.set(i, foco);
            }
        }
        if(listFocos.size() > focosDb.size())
            for(int i =listFocos.size(); i > focosDb.size(); i--){
            listFocos.remove(i-1);

            }
        return listFocos;
    }

    private List<Placa> copyPlacaList (List<Placa> placasDb){

        List<Placa> newPlacaList = new ArrayList<>();

        for(int i=0;i<placasDb.size();i++){
            Placa currentPlaca = new Placa(placasDb.get(i).getMacAddress(), placasDb.get(i).getIpAddress(), placasDb.get(i).getSSIDname(),
                                            placasDb.get(i).getSSIDpass(),  placasDb.get(i).getStatus(),  placasDb.get(i).getNumSalidas());


                newPlacaList.add(currentPlaca);

        }

        return newPlacaList;
    }

    private void actualizarEstadoFocosDB( List<Foco> focosLista){

        realm.beginTransaction();

        for (int i=0; i < focos.size(); i++){

            focos.get(i).setEstado(focosLista.get(i).getEstado());


        }
        realm.copyToRealmOrUpdate(focos);
        realm.commitTransaction();
    }



    private String getSsidWhitMac(String mac){

        String productName = "Wiicoon";

        mac = getLastSixLetterMac(mac);
        mac = mac.substring( mac.length()-6 , mac.length());
        mac = productName+"_"+mac;

        return mac;
    }

    private String getLastSixLetterMac(String mac){

        mac = getMacNoPointsFormat(mac);

        if(mac.length() >= 6)
            mac = mac.substring( mac.length()-6 , mac.length());

        return mac;
    }

    private String getMacNoPointsFormat(String mac){

        if( mac.indexOf(":")>= 0){
            mac = mac.replace(":","");
        }
        return mac;
    }



    protected String wifiIpAddress(int ipAddress) {


         if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
         }

         byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

         String ipAddressString;
          try {
                ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
          } catch (UnknownHostException ex) {

                ipAddressString = null;
          }

          return ipAddressString;
    }

     private List<Placa> noFoundPlacas(List<Placa> listaCompleta, List<Placa> listaExistente){

        List<Placa> listaResponse = new ArrayList<>();
        listaResponse.clear();

        for(int i = 0; i< listaCompleta.size(); i++) {
            listaResponse.add(listaCompleta.get(i));
        }
        for(int i = 0; i< listaCompleta.size(); i++){
            for(int j = 0 ; j < listaExistente.size(); j++){

                if( listaCompleta.get(i).getMacAddress().equals( listaExistente.get(j).getMacAddress() ) ) {
                    listaResponse.remove(listaResponse.indexOf(listaCompleta.get(i)));
                    break;
                }

            }
        }
        return listaResponse;
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
        focoAdapter.notifyDataSetChanged();
        placas = realm.where(Placa.class).findAll();
    }

    private void actualizarEstadoPlaca(String ip, String mac, int status){

        for(int i = 0; i<placas.size(); i++){

                if( placas.get(i).getMacAddress().equals(mac)) {

                    if(ip != null) {
                        if( placas.get(i).getIpAddress()== null ) {
                            realm.beginTransaction();
                            placas.get(i).setIpAddress(ip);
                            realm.copyToRealmOrUpdate(placas.get(i));
                            realm.commitTransaction();
                        }

                        else if (!placas.get(i).getIpAddress().equals(ip)) {
                            realm.beginTransaction();
                            placas.get(i).setIpAddress(ip);
                            realm.copyToRealmOrUpdate(placas.get(i));
                            realm.commitTransaction();
                        }
                    }

                    if( placas.get(i).getStatus() != status){
                        realm.beginTransaction();
                        placas.get(i).setStatus(status);
                        realm.copyToRealmOrUpdate(placas.get(i));
                        realm.commitTransaction();

                    }

                }

        }

        //focoAdapter.notifyDataSetChanged();
         placas = realm.where(Placa.class).findAll();
    }

    private void actualizarEstadoPlacasCast(List<Placa> placasActualizadas){

        realm.beginTransaction();

        for(int i = 0; i<placas.size(); i++){
            for(int j =0; j<placasActualizadas.size();j++){
                if(placas.get(i).getMacAddress().equals(placasActualizadas.get(j).getMacAddress())){

                    placas.get(i).setStatus(placasActualizadas.get(j).getStatus());
                    placas.get(i).setIpAddress(placasActualizadas.get(j).getIpAddress());
                    realm.copyToRealmOrUpdate(placas.get(i));


                }
            }
        }
        realm.commitTransaction();
        focoAdapter.notifyDataSetChanged();
        placas = realm.where(Placa.class).findAll();
    }


    private void actualizarEstadoFocosOnePlaca(Placa placaActualizada){


        Placa placaCommand = realm.where(Placa.class).equalTo("macAddress",placaActualizada.getMacAddress()).findFirst();

        if(placaCommand != null) {
            for (int i = 0; i < focosList.size(); i++) {
                if (focosList.get(i).getIDplaca() == placaCommand.getID()) {
                    for (int j = 0; j < placaActualizada.getFocos().size(); j++) {
                        if (focosList.get(i).getNumDeSalida() == placaActualizada.getFocos().get(j).getNumDeSalida()) {
                            focosList.get(i).setEstado(placaActualizada.getFocos().get(j).getEstado());
                        }
                    }


                }
            }

            focosActualizadosCommand = true;
            focoAdapter.notifyDataSetChanged();
        }
    }

    private void actualizarEstadoFocos(List<Placa> placasActualizadas){

        realm.beginTransaction();

        for(int i = 0; i<placas.size(); i++){
            for(int j =0; j<placasActualizadas.size();j++){
                if(placas.get(i).getMacAddress().equals(placasActualizadas.get(j).getMacAddress())){

                    for(int k=0; k< placasActualizadas.get(j).getFocos().size(); k++){
                        for(int l = 0; l < placas.get(i).getFocos().size(); l++){
                            if(placas.get(i).getFocos().get(l).getNumDeSalida() == placasActualizadas.get(j).getFocos().get(k).getNumDeSalida() ){

                                placas.get(i).getFocos().get(l).setEstado( placasActualizadas.get(j).getFocos().get(k).getEstado());
                                realm.copyToRealmOrUpdate(placas.get(i).getFocos().get(l));
                            }
                        }

                    }
                }
            }
        }
        realm.commitTransaction();
        focoAdapter.notifyDataSetChanged();
        placas = realm.where(Placa.class).findAll();
    }


    private List<Placa> foundPlacasDesconectadas(List<Placa> listaPlacas){

        List<Placa> listaDesconectada = new ArrayList<>();
        listaDesconectada.clear();

        for(int i = 0; i< listaPlacas.size(); i++) {
            if(listaPlacas.get(i).getStatus() == Placa.DISCONNECTED )
            listaDesconectada.add( listaPlacas.get(i) );
        }

        return listaDesconectada;
    }

    private void lanzarCasting(){

        if(doingCasting != true) {

            placas = realm.where(Placa.class).findAll();
            List<Placa> listaPlacasDesconectadas = copyPlacaList(placas);
            listaPlacasDesconectadas = foundPlacasDesconectadas(listaPlacasDesconectadas);
            if (WifiMetods.isConnected(connectivityManager) && !(WifiMetods.getMyConnectionSSID(wifiManager).contains(productName)) && listaPlacasDesconectadas.size() > 0) {
                String myIpAddress = wifiIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                asyncTaskCast = new AsyncTaskCast() {};

                asyncTaskCast.setMyAppIp(myIpAddress);
                asyncTaskCast.setListener(new AsyncTaskCast.MyAsyncTaskListener() {
                    @Override
                    public void onPostExecuteConcluded(List<Placa> placasActualizadas) {
                        actualizarEstadoPlacasCast(placasActualizadas);
                        prgBarCast.setVisibility(View.INVISIBLE);
                        doingCasting = false;
                    }

                    @Override
                    public void onProgressUpdateConcluded(int progressBar) {
                        prgBarCast.setProgress(progressBar);
                    }
                });
                prgBarCast.setVisibility(View.VISIBLE);
                prgBarCast.setMax(254);
                prgBarCast.setProgress(0);
                doingCasting = true;
                asyncTaskCast.execute(listaPlacasDesconectadas);

            }
        }
    }

    private void showFocosForScheduleNew(final List<Foco> focosSch){

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.mipmap.ic_clock);
        builderSingle.setTitle("Seleccione un foco");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builderSingle.setAdapter(focoAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int idFoco = focosSch.get(which).getID();
                passToScheduleMenuActivity(idFoco);
            }
        });

        builderSingle.show();
    }

    private void passToScheduleMenuActivity(int id){

        Foco foco = realm.where(Foco.class).equalTo("ID",id).findFirst();
        Placa placa = realm.where(Placa.class).equalTo("ID",foco.getIDplaca()).findFirst();

        if(WifiMetods.isConnected(connectivityManager) && placa.getStatus()!=Placa.DISCONNECTED) {
            Intent intent = new Intent(MainActivity.this, ScheduleMenuActivity.class);
            intent.putExtra("ID", id);
            startActivity(intent);
        }
        else
            showMessageNeedConnection(this);

    }



    @Override
    protected void onResume() {

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        placas = realm.where(Placa.class).findAll();
        List<Placa> listaPlacas = copyPlacaList(placas);
        int connectionMode;

        if (switchInternet.isChecked()){
            connectionMode = MODE_INT_REM_REFRESH;
        }

        else if( WifiMetods.isConnected(connectivityManager) && !WifiMetods.getMyConnectionSSID(wifiManager).contains(productName)) {
            connectionMode =  MODE_WS_REFRESH;
        }
        else
            connectionMode = MODE_AP_REFRESH;

        reloadFocosState(listaPlacas, connectionMode);
        asynkTaskDelayAndRepeat = new AsynkTaskDelayAndRepeat(){};
        asynkTaskDelayAndRepeat.setDelay(3000);
        listenerDelayAndRep = asynkTaskDelayAndRepeat.setListener(new AsynkTaskDelayAndRepeat.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteConcluded() {

                placas = realm.where(Placa.class).findAll();
                List<Placa> listaPlacas = copyPlacaList(placas);
                int connectionMode;

                if (switchInternet.isChecked()){
                    connectionMode = MODE_INT_REM_REFRESH;
                }

                else if( WifiMetods.isConnected(connectivityManager) && !WifiMetods.getMyConnectionSSID(wifiManager).contains(productName)) {
                    connectionMode =  MODE_WS_REFRESH;
                }
                else
                    connectionMode = MODE_AP_REFRESH;

                itemSearch.setVisible(false);

                if( connectionMode ==  MODE_WS_REFRESH) {

                    for (int i = 0; i < listaPlacas.size(); i++) {
                        if (listaPlacas.get(i).getStatus() == Placa.DISCONNECTED)
                            itemSearch.setVisible(true);
                    }
                }

                reloadFocosState(listaPlacas, connectionMode);

                asynkTaskDelayAndRepeat = new AsynkTaskDelayAndRepeat(){};
                asynkTaskDelayAndRepeat.setDelay(3000);
                asynkTaskDelayAndRepeat.setListener(listenerDelayAndRep);
                asynkTaskDelayAndRepeat.execute();
            }
        });

        asynkTaskDelayAndRepeat.execute();

        super.onResume();
    }

    private void reloadFocosState(final List<Placa> allPlacas, final int connectionStatus){

        final List<String> macWithouResponseList = new ArrayList<>();
        String currenIpReq;

        for(int i = 0; i< allPlacas.size(); i++){

            if ( !macWithouResponseList.contains(allPlacas.get(i).getMacAddress())){

                String currentMac = allPlacas.get(i).getMacAddress();

                if (connectionStatus == MODE_INT_REM_REFRESH){
                    currenIpReq = IP_SERVIDOR;
                }
                else if (connectionStatus == MODE_WS_REFRESH){
                    currenIpReq = allPlacas.get(i).getIpAddress();

                }
                else {
                    currenIpReq = null;
                    i = allPlacas.size();
                }

                macWithouResponseList.add(currentMac);

                httpRequestDataInfoWithMac(currenIpReq, currentMac, new RequestHttpMetods.OnResponseRecivedWhitMac() {
                    @Override
                    public void onResponseWhitMac(Placa placa, boolean isResponseCorrect, String mac) {
                        macWithouResponseList.remove(mac);

                        if(placa!= null){

                            if( connectionStatus == MODE_INT_REM_REFRESH){
                                placa.setStatus(Placa.INTERNET_REMOTE);
                                if(!realm.isClosed()) {
                                    actualizarEstadoPlaca(placa.getIpAddress(), placa.getMacAddress(), placa.getStatus());
                                    //actualizarEstadoFocosOnePlaca(placa);
                                }
                            }
                             else if( connectionStatus == MODE_WS_REFRESH){
                                placa.setStatus(Placa.CONNECTED);
                                if(!realm.isClosed()) {
                                    actualizarEstadoPlaca(placa.getIpAddress(), placa.getMacAddress(), placa.getStatus());
                                    //actualizarEstadoFocosOnePlaca(placa);
                                }
                             }
                            else{
                                placa.setStatus(Placa.AP_MODE);
                                for(int i = 0; i < allPlacas.size(); i++){
                                    if(allPlacas.get(i).getMacAddress().equals(placa.getMacAddress())){
                                        if(!realm.isClosed())
                                        actualizarEstadoPlaca(placa.getIpAddress(),placa.getMacAddress(),placa.getStatus());

                                    }
                                    else{
                                        if(!realm.isClosed())
                                        actualizarEstadoPlaca(null,allPlacas.get(i).getMacAddress(),Placa.DISCONNECTED);
                                    }

                                }

                            }
                            if(!realm.isClosed())
                            actualizarEstadoFocosOnePlaca(placa);
                        }
                        else {
                            if(!realm.isClosed()) {
                                actualizarEstadoPlaca(null, mac, Placa.DISCONNECTED);
                                focoAdapter.notifyDataSetChanged();
                            }

                        }

                    }
                });


            }
        }
    }

    @Override
    protected void onPause() {
        asyncTaskRefresh.cancel(true);
        asyncTaskCast.cancel(true);
        asynkTaskDelayAndRepeat.cancel(true);
        prgBarCast.setVisibility(View.INVISIBLE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        asyncTaskRefresh.cancel(true);
        asyncTaskCast.cancel(true);
        asynkTaskDelayAndRepeat.cancel(true);
        prgBarCast.setVisibility(View.INVISIBLE);
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        realm.close();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }




























}