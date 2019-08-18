package com.wiicoon.rubi.wicoon_ligh_controller.app;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rubi on 9/1/2017.
 */
public class WifiMetods {

    private final static int WEP = 1;
    private final static int WPA = 2;
    private final static int WPA2 = 3;
    private final static int OPEN = 0;

    public WifiMetods() {

    }

    public static int getNetworkID(WifiManager wifiMan ){

        WifiInfo connectionInfo;
        connectionInfo = wifiMan.getConnectionInfo();

        return connectionInfo.getNetworkId();

    }




    public static boolean isSsidExist (String SSID , WifiManager wifiMan) {



        wifiMan.startScan();
        List<ScanResult> wifiScanResults = wifiMan.getScanResults();



        for(int i=0; i<wifiScanResults.size();i++) {

            if (SSID.equals(wifiScanResults.get(i).SSID))
                return true;

        }


        return false;
    }

    public static List<String> lanOnTheArea(WifiManager wifiMan){


        List<String> listSsidScan = new ArrayList<>();
        listSsidScan.clear();

        wifiMan.startScan();
        List<ScanResult> scanResult = wifiMan.getScanResults();

        for(int i=0; i<scanResult.size();i++) {

            listSsidScan.add(scanResult.get(i).SSID);

        }
        return listSsidScan;
    }



    public static  boolean isConnected( ConnectivityManager cm ) {


        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info == null)
           return false;

        if ( info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected())
            return true;

        return false;
    }

    public static boolean connectTo(String SSID, String password, WifiManager wifiMan) {

        if (!wifiMan.isWifiEnabled())
            wifiMan.setWifiEnabled(true);

        List<WifiConfiguration> listWifi = wifiMan.getConfiguredNetworks();
        for (WifiConfiguration i : listWifi) {
            if ( i.SSID != null && i.SSID.equals("\"" + SSID + "\"") ){

                    wifiMan.removeNetwork(i.networkId);

            }
         }

        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = "\"" + SSID + "\"";
        wifiConfig.preSharedKey = "\"" + password + "\"";
        wifiConfig.priority = 40;


         wifiConfig = loadConfigWifi( wifiConfig, WPA);
         wifiConfig.networkId = wifiMan.addNetwork(wifiConfig);


        if(wifiConfig.networkId >= 0) {

            wifiMan.disconnect();
            wifiMan.enableNetwork(wifiConfig.networkId, true);
            wifiMan.reconnect();

            return true;

        }
        else
        return false;

    }

    public static boolean connectToGral(String SSID, String password, WifiManager wifiMan) {

        final int WEP = 1;
        final int WPA = 2;
        final int WPA2 = 3;
        final int OPEN = 0;

        if (!wifiMan.isWifiEnabled())
            wifiMan.setWifiEnabled(true);

        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = "\"".concat(SSID).concat("\"");
        wifiConfig.status = WifiConfiguration.Status.DISABLED;
        wifiConfig.priority = 40;

        int securityType = getSecurityType(wifiMan,SSID);
        loadConfigWifi( wifiConfig, securityType);

        switch (securityType){

            case WEP:
                if ( isHexString (password)) {
                    wifiConfig.wepKeys[0] = password;
                }
                else {
                    wifiConfig.wepKeys[0] = "\"".concat(password).concat("\"");
                }
                wifiConfig.wepTxKeyIndex = 0;
                break;
            case WPA:
                wifiConfig.preSharedKey = "\"" + password + "\"";
                break;
            case WPA2:
                wifiConfig.preSharedKey = "\"" + password + "\"";
                break;
            case OPEN:
                 break;
            default: break;
        }

        int networkId = wifiMan.addNetwork(wifiConfig);
        if (networkId != -1) {

            wifiMan.disconnect();
            wifiMan.enableNetwork(wifiConfig.networkId, true);
            wifiMan.reconnect();
            return true;
        }
        else
             return false;

    }

    private static int getSecurityType(WifiManager wifiMan, String ssid){



        int result= -1;
        List<ScanResult> networkList = wifiMan.getScanResults();

        String currentSSID = ssid;

        if (networkList != null) {
            for (ScanResult network : networkList)
            {
                //check if current connected SSID
                if (currentSSID.equals(network.SSID)){
                    //get capabilities of current connection
                    String Capabilities =  network.capabilities;


                    if (Capabilities.contains("WPA2")) {
                        result = WPA2;
                        break;
                    }
                    else if (Capabilities.contains("WPA")) {
                        result = WPA;
                        break;
                    }
                    else if (Capabilities.contains("WEP")) {
                        result = WEP;
                        break;
                    }
                    result = OPEN;
                    break;


                }

            }
        }

        return result;
    }

    public static  String getMyConnectionSSID(WifiManager wifiMan){


            WifiInfo connectionInfo;
            connectionInfo = wifiMan.getConnectionInfo();
            String SSID = connectionInfo.getSSID();
            if (SSID.charAt(SSID.length() - 1) == '"' && SSID.charAt(0) == '"')
                SSID = SSID.substring(1, SSID.length() - 1);

            return SSID;



    }

    public static void connectByNetworkId(WifiManager wifiMan, int networkId){

        wifiMan.disconnect();
        wifiMan.enableNetwork(networkId, true);
        wifiMan.reconnect();


    }

    private static WifiConfiguration loadConfigWifi(WifiConfiguration wifiConfiguration, int securityType){



        switch (securityType){

            case WEP:
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                break;

            case WPA:
                wifiConfiguration.allowedAuthAlgorithms.set(0);
                wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                break;
            case WPA2:
                wifiConfiguration.allowedAuthAlgorithms.set(0);
                wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                break;
            case OPEN:
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfiguration.allowedAuthAlgorithms.clear();
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                break;
            default: break;
        }


        return wifiConfiguration;
    }

    public static boolean isFailOver(ConnectivityManager cm) {

        NetworkInfo info = cm.getActiveNetworkInfo();

        return info.isFailover();

    }

    private static boolean isHexString( String password ){

      if( password.matches("^[0-9a-fA-F]+$"))
      return true;
      else
      return false;
    }


}

