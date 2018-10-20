package com.potter.honeywellfuntest;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.honeywell.ezservice.EzServiceManager;
import com.honeywell.ezservice.ICallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener {
    protected static final String TAG = "MainActivity";
    Button switchwayBtn;
    Button rebootDeviceBtn;
    Button powerOffDeviceBtn;
    Button scanSettingsBtn;
    Button getSNBtn;
    Button setSysTimeBtn;
    Button setStatusBarBtn;
    Button enableHomeKeyBtn;
    Button enableUsbDebugModeBtn;
    Button silentInstallBtn;
    Button silentUninstallBtn;
    EzServiceManager honeywellFunManager;
    boolean mStatusbarState=false;
    boolean mHomeKeyState=false;
    boolean mUsbDebugState=false;
    boolean mQrScanState=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        honeywellFunManager=EzServiceManager.getInstance(this);


        initUI();
    }
    public void initUI(){
        switchwayBtn=(Button)findViewById(R.id.switch_way);
        switchwayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,BroadcastWayActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        rebootDeviceBtn=(Button)findViewById(R.id.reboot_device);
        rebootDeviceBtn.setOnClickListener(this);
        powerOffDeviceBtn=(Button)findViewById(R.id.poweroff_device);
        powerOffDeviceBtn.setOnClickListener(this);
        scanSettingsBtn=(Button)findViewById(R.id.scan_settings);
        scanSettingsBtn.setOnClickListener(this);
        getSNBtn=(Button)findViewById(R.id.get_sn);
        getSNBtn.setOnClickListener(this);
        setSysTimeBtn=(Button)findViewById(R.id.set_system_time);
        setSysTimeBtn.setOnClickListener(this);
        setStatusBarBtn=(Button)findViewById(R.id.set_statusbar);
        setStatusBarBtn.setOnClickListener(this);
        enableHomeKeyBtn=(Button)findViewById(R.id.enable_homekey);
        enableHomeKeyBtn.setOnClickListener(this);
        enableUsbDebugModeBtn=(Button)findViewById(R.id.enable_usb_debug);
        enableUsbDebugModeBtn.setOnClickListener(this);
        silentInstallBtn=(Button)findViewById(R.id.silent_install);
        silentInstallBtn.setOnClickListener(this);
        silentUninstallBtn=(Button)findViewById(R.id.silent_uninstall);
        silentUninstallBtn.setOnClickListener(this);
    }
    ICallback callback = new ICallback.Stub() {

        @Override
        public void onSuccess(String result) throws RemoteException {
            Log.d(TAG, "onSuccess result = " + result);

        }

        @Override
        public void onProgress(int progress, String message) throws RemoteException {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(String errorCode, String errorMessage) throws RemoteException {
            // TODO Auto-generated method stub
            Log.d(TAG, "onError errorCode = " + errorCode + ",errorMessage = " + errorMessage);
        }
    };
    @Override
    public void onClick(View v) {
        if(honeywellFunManager!=null){
            switch (v.getId()) {
                case R.id.reboot_device:
                    honeywellFunManager.rebootDevice();
                    break;
                case R.id.poweroff_device:
                    honeywellFunManager.powerOffDevice();
                    break;
                case R.id.scan_settings:
                    JSONArray jsonArray =new JSONArray();
                    JSONObject jobj;
                    try {
                        jobj = new JSONObject();
                        jobj.put("k","DEC_QR_ENABLED");//IMG_EXPOSURE_MODE");
                        jobj.put("v",mQrScanState?"true":"false");//contextSensitive");
                        jsonArray.put(jobj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    honeywellFunManager.scanSettings("CMD_SET_SCAN_RPOPERTIES",jsonArray.toString());
                    mQrScanState=!mQrScanState;
                    break;
                case R.id.get_sn:
                    Toast.makeText(this,honeywellFunManager.getSerialNumber(),Toast.LENGTH_SHORT).show();
                    break;
                case R.id.set_system_time:
                    Calendar c = Calendar.getInstance();
                    long time=c.getTimeInMillis();
                    Log.e("potter","time="+time);
                    String zz=String.valueOf(time+1000*60*30);
                    honeywellFunManager.setSysTime(zz);
                    break;
                case R.id.set_statusbar:
                    honeywellFunManager.setStatusBar(mStatusbarState);
                    setStatusBarBtn.setText(mStatusbarState?"diasble statusbar":"enable statusbar");
                    mStatusbarState=!mStatusbarState;

                    break;
                case R.id.enable_homekey:
                    honeywellFunManager.enableHomeKey(mHomeKeyState);
                    enableHomeKeyBtn.setText(mHomeKeyState?"diasble homekey":"enable homekey");
                    mHomeKeyState=!mHomeKeyState;
                    break;
                case R.id.enable_usb_debug:
                    honeywellFunManager.enableUsbDebugMode(mUsbDebugState);
                    enableUsbDebugModeBtn.setText(mUsbDebugState?"diasble usb debug":"enable usb debug");
                    mUsbDebugState=!mUsbDebugState;
                    break;
                case R.id.silent_install:
                    String[] paths = new String[] {
                            "/storage/emulated/0/CtTool-debug.apk"};
                    honeywellFunManager.silentInstallApks(paths,callback);
                    break;
                case R.id.silent_uninstall:
                    String[] pkgs = new String[] {
                            "com.ct.tool"};
                    honeywellFunManager.silentUninstallApks(pkgs,callback);
                    break;
                default:
                    break;
            }
        }
    }
}
