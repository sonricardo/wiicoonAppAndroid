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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wiicoon.rubi.wicoon_ligh_controller.R;
import com.wiicoon.rubi.wicoon_ligh_controller.adapters.AddRecyclerAdapter;
import com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos;
import com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.app.WifiMetods;
import com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskAddPlaca;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Foco;
import com.wiicoon.rubi.wicoon_ligh_controller.models_realm.Placa;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.LAN_CONNECTION;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.NO_CONNECTED;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.PLACA_CONNECTION;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.GeneralMetdos.PRODUCT_NAME;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.FOCO_COMMAND;
import static com.wiicoon.rubi.wicoon_ligh_controller.app.RequestHttpMetods.httpRequest;
import static com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskAddPlaca.IMPORTANT_STATUS_MESSAGE;
import static com.wiicoon.rubi.wicoon_ligh_controller.asyncTasks.AsynkTaskAddPlaca.NORMAL_STATUS_MESSAGE;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonAgregar;
    private int stateHttp;
    private final int IDDLE = 0;
    private final int WAITING = 1;
    private final int CORRECT = 2;
    private final int INCORRECT = 3;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private int focosChecked[];
    WifiManager wifiManager;
    ConnectivityManager connectivityManager;
    private Button buttonManual;
    private ProgressBar progressBar;
    private TextView txtVieMac;
    private TextView txtStatus;

    private final int PLACA_DISCONNECTED_NO_SHARE = 0;
    private final int PLACA_DISCONNECTED_SHARE = 1;
    private final int PLACA_CONNECTED_LAN_NO_SHARE = 2;
    private final int PLACA_CONNECTED_LAN_SHARE = 3;
    private final int PLACA_CONNECTED_AP_NO_SHARE = 4;
    private final int PLACA_CONNECTED_AP_SHARE = 5;
    private final int NO_PLACA_DISCONNECTED_SHARE = 6;
    private final int NO_PLACA_CONNECTED_LAN_SHARE = 7;
    private final int NO_PLACA_CONNECTED_AP_SHARE = 8;
    private final int NO_PLACA_CONNECTED_AP_NO_SHARE = 9;
    private final int NO_PLACA_CONNECTED_LAN_NO_SHARE = 10;
    private final int NO_PLACA_DISCONNECTED_NO_SHARE = 11;

    private final int SUCCESS_NEW = 0;
    private final int SUCCESS_EDIT = 1;
    private final int FAIL_REQ_NEW = 2;
    private final int FAIL_REQ_EDIT = 3;
    private final int FAIL_COMMUNNICATION = 4;
    private final int SUCCESS_EDIT_LAN = 5;

    private Placa placaActualizada;

    private Realm realm;
    private  RealmResults<Placa> placasCreadas;

    private int idPlaca;
    private int statusOfIntent;
    private String ssidToShare;
    private String passwordToShare;
    private boolean isShareLan;
    private String ssidExpected;
    private int statusAfterGetIntent;
    private int networkIdAlEntrar;
    private String ipLan;
    private boolean mustToFinish;

    private AsynkTaskAddPlaca asynkTaskAddPlaca;
    private int responseHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        this.enforceIconBar();

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);


        realm = Realm.getDefaultInstance();
        placasCreadas = realm.where(Placa.class).findAll();

        buttonManual = (Button)findViewById(R.id.buttonManual);
        txtVieMac = (TextView) findViewById(R.id.textViewMacPlacaAdd);
        txtStatus = (TextView) findViewById(R.id.textViewStatus);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        buttonAgregar = (Button)findViewById(R.id.buttonAgregarFocos);


        buttonManual.setOnClickListener(this);
        buttonAgregar.setOnClickListener(this);

        mustToFinish = false;

        if(getIntent().getExtras() != null) {
            idPlaca = getIntent().getExtras().getInt("id");
            statusOfIntent = getIntent().getExtras().getInt("statusConnection");
            ssidToShare = getIntent().getExtras().getString("ssidLan");
            passwordToShare = getIntent().getExtras().getString("passwordLan");
            isShareLan = getIntent().getExtras().getBoolean("shareLan");
        }

        clasificarEstadosIniciales();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_add_activiy, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.return_item_add:
                if (asynkTaskAddPlaca != null) {
                    asynkTaskAddPlaca.cancel(true);
                }
                Intent intent = new Intent(this, ConfigListActivity.class);

                startActivity(intent);
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    public void enforceIconBar(){
        getSupportActionBar().setIcon(R.mipmap.ic_mesh);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("  AGREGAR / EDITAR  ");
    }

    @Override
    public void onClick(View v) {

            switch (v.getId()) {


                case  R.id.buttonManual:

                    actualizarTextConectadoAhoraA();
                    if( checkToLaunchAction(statusAfterGetIntent)){
                        launchAsynkTaskAdd();

                    }
                    else{

                    }
                    break;

                case R.id.buttonAgregarFocos:
                    if(idPlaca != -1){
                        updateOldPlaca();
                    }
                    else {
                        saveNewPlaca();
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                default: break;

            }
    }

    private void desplegarFocos(Placa placaGral,final int stateResult){

        placaActualizada = placaGral;

        recyclerView = (RecyclerView)findViewById(R.id.reciclerViewAdd);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        adapter = new AddRecyclerAdapter(placaActualizada.getFocos(), R.layout.list_view_add, new AddRecyclerAdapter.OnClickListener() {
            @Override
            public void onBtnClick(final int numSalida) {


                int estadoRequest = Foco.OFF;
                for (int i = 0; i < placaActualizada.getFocos().size(); i++) {
                    if (placaActualizada.getFocos().get(i).getNumDeSalida() == numSalida) {
                        if (placaActualizada.getFocos().get(i).getEstado() == Foco.OFF)
                            estadoRequest = Foco.ON;
                        else
                            estadoRequest = Foco.OFF;
                    }
                }
                String ipCommand;
                ipCommand = null;
                if(stateResult == SUCCESS_EDIT_LAN)
                ipCommand = placaActualizada.getIpAddress();

                if (stateHttp == IDDLE) {
                    stateHttp = WAITING;
                    responseHttp = IDDLE;
                    httpRequest(FOCO_COMMAND, ipCommand, null, null, estadoRequest, numSalida, new RequestHttpMetods.OnResponseRecived() {
                        @Override
                        public void onResponse(Placa placa, boolean isResponseCorrect) {
                            if (isResponseCorrect) {
                                responseHttp = CORRECT;
                                stateHttp = IDDLE;
                                for (int i = 0; i < placaActualizada.getFocos().size(); i++) {
                                    if (placaActualizada.getFocos().get(i).getNumDeSalida() == numSalida) {
                                        placaActualizada.getFocos().get(i).setEstado(placa.getFocos().get(i).getEstado());
                                        Toast.makeText(getApplicationContext(), " togle en  " + numSalida, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
                                responseHttp = INCORRECT;
                                stateHttp = IDDLE;
                                Toast.makeText(getApplicationContext(), "error en conexion con foco: " + numSalida, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        }, new AddRecyclerAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(final int numSalida, boolean checkedPosition) {

                if (checkedPosition) {
                    focosChecked[numSalida] = 1;

                }
                else
                    focosChecked[numSalida] = 0;


            }
        }, new AddRecyclerAdapter.OnTextChangedListener() {
            @Override
            public void onTextChangedListener(int numSalida, String focoName) {
                for(int i = 0; i< placaActualizada.getFocos().size(); i++){
                    if (placaActualizada.getFocos().get(i).getNumDeSalida() == numSalida)
                    placaActualizada.getFocos().get(i).setName( focoName );

                }
            }

            @Override
            public void onProgressChangedListener(int numSalida, boolean checkedPosition) {

            }
        });

        focosChecked = new int[placaActualizada.getFocos().size()+1];
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private String getSsidWhitMac(String mac){

        String productName = "Wiicoon";

        mac = getLastSixLetterMac(mac);
        mac = mac.substring( mac.length()-6 , mac.length());
        mac = productName+"_"+mac;

        return mac;
    }

    private String getMacNoPointsFormat(String mac){

        if( mac.indexOf(":")>= 0){
           mac = mac.replace(":","");
        }
        return mac;
    }

    private String getLastSixLetterMac(String mac){

         mac = getMacNoPointsFormat(mac);

         if(mac.length() >= 6)
         mac = mac.substring( mac.length()-6 , mac.length());

       return mac;
    }

     private void clasificarEstadosIniciales(){

        actualizarTextConectadoAhoraA();
        ssidExpected = new String();
        networkIdAlEntrar = -1;
        ipLan = new String();


        if(idPlaca != -1){

            Placa placaEdit = realm.where(Placa.class).equalTo("ID", idPlaca).findFirst();
            setTextStatus("conectese a la red: " + getSsidWhitMac(placaEdit.getMacAddress()) + " y luego oprima el botón", NORMAL_STATUS_MESSAGE);


            switch (statusOfIntent){

                case NO_CONNECTED:
                    ssidExpected = getSsidWhitMac(placaEdit.getMacAddress());

                    if(isShareLan)
                        statusAfterGetIntent = PLACA_DISCONNECTED_SHARE;
                    else
                        statusAfterGetIntent = PLACA_DISCONNECTED_NO_SHARE;
                    break;

                case LAN_CONNECTION:
                    ssidExpected = getSsidWhitMac(placaEdit.getMacAddress());

                    if(WifiMetods.isConnected(connectivityManager))
                        networkIdAlEntrar = WifiMetods.getNetworkID(wifiManager);

                    if(isShareLan) {
                        statusAfterGetIntent = PLACA_CONNECTED_LAN_SHARE;
                     }
                    else {
                        statusAfterGetIntent = PLACA_CONNECTED_LAN_NO_SHARE;
                        if (WifiMetods.isConnected(connectivityManager) && placaEdit.getStatus() == Placa.CONNECTED) {
                            ssidExpected = WifiMetods.getMyConnectionSSID(wifiManager);
                            ipLan = placaEdit.getIpAddress();
                        }
                        else {
                            statusAfterGetIntent = PLACA_CONNECTED_AP_NO_SHARE;
                        }
                    }

                    if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidExpected)) {
                        launchAsynkTaskAdd();
                    }
                    else {
                        Toast.makeText(this, "tiene que reconectarse a la red: "+ssidExpected+" para editar los valores de esta placa",Toast.LENGTH_LONG).show();
                    }

                    break;

                case PLACA_CONNECTION:

                    ssidExpected = getSsidWhitMac(placaEdit.getMacAddress());

                   if(isShareLan)
                        statusAfterGetIntent = PLACA_CONNECTED_AP_SHARE;
                    else
                        statusAfterGetIntent = PLACA_CONNECTED_AP_NO_SHARE;

                    if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidExpected)) {
                        launchAsynkTaskAdd();
                    }
                    else {
                        Toast.makeText(this, "tiene que reconectarse a la red: "+ssidExpected+" para editar los valores de esta placa",Toast.LENGTH_LONG).show();
                    }
                    break;

                default: break;

            }


        }
        else{
            setTextStatus("conectese a una red " +PRODUCT_NAME+ " y luego oprima el botón", NORMAL_STATUS_MESSAGE);
            switch (statusOfIntent){

                case NO_CONNECTED:
                    if(isShareLan)
                        statusAfterGetIntent = NO_PLACA_DISCONNECTED_SHARE;
                    else {
                        statusAfterGetIntent = NO_PLACA_DISCONNECTED_NO_SHARE;
                    }

                    break;

                case LAN_CONNECTION:
                    if(WifiMetods.isConnected(connectivityManager))
                        networkIdAlEntrar = WifiMetods.getNetworkID(wifiManager);
                    if(isShareLan)
                        statusAfterGetIntent = NO_PLACA_CONNECTED_LAN_SHARE;
                    else{
                        statusAfterGetIntent = NO_PLACA_CONNECTED_LAN_NO_SHARE;
                    }
                    break;

                case PLACA_CONNECTION:

                    if(isShareLan)
                        statusAfterGetIntent = NO_PLACA_CONNECTED_AP_SHARE;
                    else{
                        statusAfterGetIntent = NO_PLACA_CONNECTED_AP_NO_SHARE;
                    }
                    break;

                default:
                    break;

            }
        }

    }

    private boolean checkToLaunchAction(int status){

        switch (status){
            case  PLACA_DISCONNECTED_NO_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidExpected)){

                    return true;

                }
                else {
                    Toast.makeText(this, "tiene que conectarse a la red: "+ssidExpected+" para editar los valores de esta placa",Toast.LENGTH_LONG).show();
                }
                break;

            case PLACA_DISCONNECTED_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidExpected)){

                    return true;

                }
                else {
                    Toast.makeText(this, "tiene que conectarse a la red: "+ssidExpected+" para editar los valores de esta placa",Toast.LENGTH_LONG).show();
                }

                break;
            case PLACA_CONNECTED_LAN_NO_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidExpected)) {

                    return true;
                }
                else {
                    Toast.makeText(this, "tiene que reconectarse a la red: "+ssidExpected+" para editar los valores de esta placa",Toast.LENGTH_LONG).show();
                }
                break;
            case PLACA_CONNECTED_LAN_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidExpected)){

                    return true;

                }
                else{
                    Toast.makeText(this, "tiene que reconectarse a la red: "+ssidExpected+" para editar los valores de esta placa",Toast.LENGTH_LONG).show();
                }
                break;
            case PLACA_CONNECTED_AP_NO_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidExpected)){

                    return true;

                }
                else {
                    Toast.makeText(this, "tiene que reconectarse a la red: "+ssidExpected+" para editar los valores de esta placa",Toast.LENGTH_LONG).show();
                }
                break;

            case PLACA_CONNECTED_AP_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).equals(ssidExpected)){

                    return true;

                }
                else {
                    Toast.makeText(this, "tiene que reconectarse a la red: "+ssidExpected+" para editar los valores de esta placa",Toast.LENGTH_LONG).show();
                }
                break;

            case NO_PLACA_CONNECTED_LAN_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)) {

                    if( !getSsidApList( placasCreadas ).contains( WifiMetods.getMyConnectionSSID(wifiManager) ) ) {
                        ssidExpected =  WifiMetods.getMyConnectionSSID(wifiManager);
                        return true;
                    }
                    else{
                        Toast.makeText(this, "Esta red pertenece a una placa ya agregada, conectese a otra red "+PRODUCT_NAME+"_XXXXXX diferente a esta para agregar nueva placa",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(this, "tiene que conectarse a una red: "+PRODUCT_NAME+"_XXXXXX  para agregar una nueva placa",Toast.LENGTH_LONG).show();
                }
                break;
            case NO_PLACA_CONNECTED_AP_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)){

                    if( !getSsidApList( placasCreadas ).contains( WifiMetods.getMyConnectionSSID(wifiManager) ) ) {
                        ssidExpected =  WifiMetods.getMyConnectionSSID(wifiManager);
                        return true;
                    }
                    else {
                        Toast.makeText(this, "Esta red pertenece a una placa ya agregada, conectese a otra red "+PRODUCT_NAME+"_XXXXXX diferente a esta para agregar nueva placa",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(this, "tiene que conectarse a una red: "+PRODUCT_NAME+"_XXXXXX  para agregar una nueva placa",Toast.LENGTH_LONG).show();
                }
                break;
            case  NO_PLACA_DISCONNECTED_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)){

                    if( !getSsidApList( placasCreadas ).contains( WifiMetods.getMyConnectionSSID(wifiManager) ) ) {
                        ssidExpected =  WifiMetods.getMyConnectionSSID(wifiManager);
                        return true;
                    }
                    else {
                        Toast.makeText(this, "Esta red pertenece a una placa ya agregada, conectese a otra red "+PRODUCT_NAME+"_XXXXXX diferente a esta para agregar nueva placa",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(this, "tiene que conectarse a una red: "+PRODUCT_NAME+"_XXXXXX  para agregar una nueva placa",Toast.LENGTH_LONG).show();
                }
                break;

            case NO_PLACA_CONNECTED_LAN_NO_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)) {

                    if( !getSsidApList( placasCreadas ).contains( WifiMetods.getMyConnectionSSID(wifiManager) ) ) {

                        return true;
                    }
                    else {
                        Toast.makeText(this, "Esta red pertenece a una placa ya agregada, conectese a otra red "+PRODUCT_NAME+"_XXXXXX diferente a esta para agregar nueva placa",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(this, "tiene que conectarse a una red: "+PRODUCT_NAME+"_XXXXXX  para agregar una nueva placa",Toast.LENGTH_LONG).show();
                }
                break;
            case NO_PLACA_CONNECTED_AP_NO_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)){

                    if( !getSsidApList( placasCreadas ).contains( WifiMetods.getMyConnectionSSID(wifiManager) ) ) {

                        return true;
                    }
                    else {
                        Toast.makeText(this, "Esta red pertenece a una placa ya agregada, conectese a otra red "+PRODUCT_NAME+"_XXXXXX diferente a esta para agregar nueva placa",Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(this, "tiene que conectarse a una red: "+PRODUCT_NAME+"_XXXXXX  para agregar una nueva placa",Toast.LENGTH_LONG).show();
                }
                break;
            case  NO_PLACA_DISCONNECTED_NO_SHARE:
                if(WifiMetods.isConnected(connectivityManager) && WifiMetods.getMyConnectionSSID(wifiManager).contains(PRODUCT_NAME)){

                    if( !getSsidApList( placasCreadas ).contains( WifiMetods.getMyConnectionSSID(wifiManager) ) ) {

                        return true;
                    }
                    else {
                        Toast.makeText(this, "Esta red pertenece a una placa ya agregada, conectese a otra red "+PRODUCT_NAME+"_XXXXXX diferente a esta para agregar nueva placa",Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    Toast.makeText(this, "tiene que conectarse a una red: "+PRODUCT_NAME+"_XXXXXX  para agregar una nueva placa",Toast.LENGTH_LONG).show();
                }
                break;

            default: break;
        }
        return false;
    }

    private void actualizarTextConectadoAhoraA(){
        if( WifiMetods.isConnected(connectivityManager) ){

            txtVieMac.setText(WifiMetods.getMyConnectionSSID(wifiManager));
        }
        else
            txtVieMac.setText("");

    }

    private List<String> getSsidApList(List<Placa> listPlacas){

    List<String> listaSsidAp = new ArrayList<>();
    listaSsidAp.clear();
    for(int i=0; i<listPlacas.size();i++){
        listaSsidAp.add(GeneralMetdos.getSsidWhitMac( listPlacas.get(i).getMacAddress() ) );

    }
    return listaSsidAp;
    }

    private void showMessageFailComm(){
        Toast.makeText(this,"Hubo una falla en la comunicacion con la placa, intentelo de nuevo",Toast.LENGTH_SHORT).show();
    }

    private void showMessageFailReq(){
        Toast.makeText(this,"No se ha podido confirmar que la placa se conecto a la red: "+ssidToShare+". Puede continuar con la edicion",Toast.LENGTH_SHORT).show();
    }
    private Placa setNuevaPlacaTosShow(Placa placaGral){

        for(int i = 0; i < placaGral.getFocos().size(); i++){
            placaGral.getFocos().get(i).setName(null);
        }
        return placaGral;
    }

    private Placa setEditPlacaTosShow(Placa placaGral){

        Placa placaEdit = realm.where(Placa.class).equalTo("macAddress",placaGral.getMacAddress()).findFirst();
        if(placaEdit != null) {
            for (int i = 0; i < placaGral.getFocos().size(); i++) {
                for (int j = 0; j < placaEdit.getFocos().size(); j++) {
                    if (placaGral.getFocos().get(i).getNumDeSalida() == placaEdit.getFocos().get(j).getNumDeSalida()) {
                        placaGral.getFocos().get(i).setName(placaEdit.getFocos().get(j).getName());
                    }
                }
            }

        }

        return placaGral;
    }

    private void setVisualOnChargeStatus(){
        buttonManual.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void setVisualOnAfterChargeStatus(){
        progressBar.setVisibility(View.INVISIBLE);
        buttonAgregar.setVisibility(View.VISIBLE);
    }
    private void setVisualOnInitialStatus(){
        buttonManual.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setTextStatus(String message, int kindMessage){

        if(kindMessage == IMPORTANT_STATUS_MESSAGE) {
            txtStatus.setText(message);
            setTextViewImportant();
        }
        else {
            txtStatus.setText(message);
            setTextViewNormal();
        }

    }

    private void setTextViewImportant(){
        txtStatus.setTextColor(getResources().getColor(R.color.colorNegro));
        txtStatus.setTextSize(15);
    }

    private void setTextViewNormal(){
        txtStatus.setTextColor(getResources().getColor(R.color.colorGris));
        txtStatus.setTextSize(10);
    }

    private void saveNewPlaca(){

        Placa placaNueva = new Placa(placaActualizada.getMacAddress(),placaActualizada.getIpAddress(), placaActualizada.getSSIDname(),
                placaActualizada.getSSIDpass(),placaActualizada.getStatus(),placaActualizada.getNumSalidas());

        realm.beginTransaction();
        realm.copyToRealm(placaNueva);
        realm.commitTransaction();

        Placa placaGuardada = realm.where(Placa.class).equalTo("ID", placaNueva.getID()).findFirst();

        realm.beginTransaction();

        for(int i=0; i < placaActualizada.getFocos().size() ;i++){

            if( focosChecked[ placaActualizada.getFocos().get(i).getNumDeSalida() ] == 1 ){
                Foco foco =  new Foco(placaNueva.getID(),placaActualizada.getFocos().get(i).getName(),
                        placaActualizada.getFocos().get(i).getNumDeSalida(),
                        placaActualizada.getFocos().get(i).getEstado());
                if(foco.getName()== null || foco.getName().equals(""))
                    foco.setName("foco_"+foco.getNumDeSalida());

                realm.copyToRealm(foco);
                placaGuardada.getFocos().add(foco);
            }
        }
        realm.commitTransaction();
    }

    private void updateOldPlaca() {

        Placa placaEdit = realm.where(Placa.class).equalTo("ID", idPlaca).findFirst();

        realm.beginTransaction();
        placaEdit.setIpAddress(placaActualizada.getIpAddress());
        placaEdit.setNumSalidas(placaActualizada.getNumSalidas());
        placaEdit.setStatus(placaActualizada.getStatus());
        realm.copyToRealmOrUpdate(placaEdit);
        realm.commitTransaction();

        for (int i = 0; i < placaActualizada.getFocos().size(); i++) {

            if (focosChecked[placaActualizada.getFocos().get(i).getNumDeSalida()] == 1) {

                boolean isFocoExist = false;

                for (int j = 0; j < placaEdit.getFocos().size(); j++) {
                    if (placaEdit.getFocos().get(j).getNumDeSalida() == placaActualizada.getFocos().get(i).getNumDeSalida()) {
                        isFocoExist = true;
                        realm.beginTransaction();
                        placaEdit.getFocos().get(j).setEstado(placaActualizada.getFocos().get(i).getEstado());
                        placaEdit.getFocos().get(j).setName(placaActualizada.getFocos().get(i).getName());
                        if (placaActualizada.getFocos().get(i).getName() == null || placaActualizada.getFocos().get(i).getName().equals("")) {
                            placaEdit.getFocos().get(j).setName("foco_" + placaEdit.getFocos().get(j).getNumDeSalida());
                        }
                        realm.copyToRealmOrUpdate(placaEdit.getFocos().get(j));
                        realm.commitTransaction();

                    }
                }
                if (!isFocoExist) {
                    realm.beginTransaction();
                    Foco foco = new Foco(placaEdit.getID(), placaActualizada.getFocos().get(i).getName(),
                            placaActualizada.getFocos().get(i).getNumDeSalida(),
                            placaActualizada.getFocos().get(i).getEstado());
                    if (foco.getName() == null || foco.getName().equals(""))
                        foco.setName("foco_" + foco.getNumDeSalida());

                    realm.copyToRealm(foco);
                    placaEdit.getFocos().add(foco);
                    realm.commitTransaction();
                    isFocoExist = true;
                }
            } else {

                for (int j = 0; j < placaEdit.getFocos().size(); j++) {
                    if (placaEdit.getFocos().get(j).getNumDeSalida() == placaActualizada.getFocos().get(i).getNumDeSalida()) {
                        realm.beginTransaction();
                        placaEdit.getFocos().get(j).deleteFromRealm();
                        realm.commitTransaction();
                    }
                }
            }
        }
    }
    private void launchAsynkTaskAdd(){

        asynkTaskAddPlaca = new AsynkTaskAddPlaca(){};
        asynkTaskAddPlaca.setInStatus(statusAfterGetIntent);
        asynkTaskAddPlaca.setIpLan(ipLan);
        asynkTaskAddPlaca.setSsidAndPassToShare(ssidToShare,passwordToShare);
        asynkTaskAddPlaca.setWifiConfiguration(wifiManager,connectivityManager);
        asynkTaskAddPlaca.setSsidPlaca(ssidExpected);
        asynkTaskAddPlaca.setListener(new AsynkTaskAddPlaca.MyAsyncTaskListener() {
            @Override
            public void onPostExecuteLauncher(Placa placaGral, int statusResult) {
                mustToFinish = true;
                setVisualOnAfterChargeStatus();
                if(statusResult == FAIL_REQ_EDIT || statusResult == FAIL_REQ_NEW)
                    showMessageFailReq();
                if(statusResult == SUCCESS_NEW || statusResult == FAIL_REQ_NEW){
                    placaGral = setNuevaPlacaTosShow(placaGral);
                }
                else if(statusResult == SUCCESS_EDIT || statusResult == SUCCESS_EDIT_LAN || statusResult == FAIL_REQ_EDIT){
                    placaGral = setEditPlacaTosShow(placaGral);
                }
                desplegarFocos(placaGral, statusResult);
            }

            @Override
            public void onPreExecuteLauncher() {
                setVisualOnChargeStatus();
            }

            @Override
            public void onCanceledLauncher(int statusResult) {
                if(statusResult == FAIL_COMMUNNICATION) {
                    showMessageFailComm();
                    setVisualOnInitialStatus();
                }
            }

            @Override
            public void onProgressUpdateLauncher(String msgToShow, int kindMessage) {
                setTextStatus(msgToShow,kindMessage);

            }
        });
        asynkTaskAddPlaca.execute();
    }

      @Override
    protected void onDestroy() {


        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if(mustToFinish)
            finish();
        super.onStop();
    }

    @Override
    protected void onResume() {
        actualizarTextConectadoAhoraA();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(asynkTaskAddPlaca != null) {
            asynkTaskAddPlaca.cancel(true);
        }
        super.onBackPressed();
     }
}
