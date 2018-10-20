package com.potter.honeywellfuntest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.honeywell.ezservice.EzServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Set;

public class BroadcastWayActivity extends Activity implements View.OnClickListener {
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
    boolean mStatusbarState = false;
    boolean mHomeKeyState = false;
    boolean mUsbDebugState = false;
    boolean mQrScanState = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_layout);
        initUI();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.honeywell.ezservice.SERIAL_NUMBER");
        registerReceiver(getSnBroadcast, filter, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getSnBroadcast != null) {
            try {
                unregisterReceiver(getSnBroadcast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initUI() {
        switchwayBtn = (Button) findViewById(R.id.switch_way1);
        switchwayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rebootDeviceBtn = (Button) findViewById(R.id.reboot_device1);
        rebootDeviceBtn.setOnClickListener(this);
        powerOffDeviceBtn = (Button) findViewById(R.id.poweroff_device1);
        powerOffDeviceBtn.setOnClickListener(this);
        scanSettingsBtn = (Button) findViewById(R.id.scan_settings1);
        scanSettingsBtn.setOnClickListener(this);
        getSNBtn = (Button) findViewById(R.id.get_sn1);
        getSNBtn.setOnClickListener(this);
        setSysTimeBtn = (Button) findViewById(R.id.set_system_time1);
        setSysTimeBtn.setOnClickListener(this);
        setStatusBarBtn = (Button) findViewById(R.id.set_statusbar1);
        setStatusBarBtn.setOnClickListener(this);
        enableHomeKeyBtn = (Button) findViewById(R.id.enable_homekey1);
        enableHomeKeyBtn.setOnClickListener(this);
        enableUsbDebugModeBtn = (Button) findViewById(R.id.enable_usb_debug1);
        enableUsbDebugModeBtn.setOnClickListener(this);
        silentInstallBtn = (Button) findViewById(R.id.silent_install1);
        silentInstallBtn.setOnClickListener(this);
        silentUninstallBtn = (Button) findViewById(R.id.silent_uninstall1);
        silentUninstallBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.reboot_device1:
                intent = new Intent("com.honeywell.ezservice.REBOOT_DEVICE");
                sendBroadcast(intent);
                break;
            case R.id.poweroff_device1:
                intent = new Intent("com.honeywell.ezservice.POWEROFF_DEVICE");
                sendBroadcast(intent);
                break;
            case R.id.scan_settings1:
                intent = new Intent("com.honeywell.ezservice.ACTION_SCAN_SETTINGS");
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
                intent.putExtra("cmd","CMD_SET_SCAN_RPOPERTIES");
                intent.putExtra("content",jsonArray.toString());
                sendBroadcast(intent);
                mQrScanState=!mQrScanState;
                break;
            case R.id.get_sn1:
                intent = new Intent("com.honeywell.ezservice.GET_SERIAL_NUMBER");
                sendBroadcast(intent);
                break;
            case R.id.set_system_time1:
                intent = new Intent("com.honeywell.ezservice.ACTION_EZ_SETTINGS");
                Calendar c = Calendar.getInstance();
                long time=c.getTimeInMillis();
                String zz=String.valueOf(time+1000*60*30);
                intent.putExtra("setSystime",zz);
                sendBroadcast(intent);
                break;
            case R.id.set_statusbar1:
                intent = new Intent("com.honeywell.ezservice.ACTION_EZ_SETTINGS");
                intent.putExtra("setStatusbar",mStatusbarState);
                sendBroadcast(intent);
                setStatusBarBtn.setText(mStatusbarState?"diasble statusbar":"enable statusbar");
                mStatusbarState=!mStatusbarState;
                break;
            case R.id.enable_homekey1:
                intent = new Intent("com.honeywell.ezservice.ACTION_EZ_SETTINGS");
                intent.putExtra("setHomeKey",mHomeKeyState);
                sendBroadcast(intent);
                enableHomeKeyBtn.setText(mHomeKeyState?"diasble homekey":"enable homekey");
                mHomeKeyState=!mHomeKeyState;
                break;
            case R.id.enable_usb_debug1:
                intent = new Intent("com.honeywell.ezservice.USB_DEBUG_SWITCH");
                intent.putExtra("enable",mUsbDebugState);
                sendBroadcast(intent);
                enableUsbDebugModeBtn.setText(mUsbDebugState?"diasble usb debug":"enable usb debug");
                mUsbDebugState=!mUsbDebugState;
                break;
            case R.id.silent_install1:
                intent=new Intent("com.honeywell.ezservice.SILENT_INSTALL_APKS");
                String[] paths = new String[] {
                        "/storage/emulated/0/CtTool-debug.apk"};
                Bundle b1=new Bundle();
                b1.putStringArray("apkpaths", paths);
                intent.putExtras(b1);
                sendBroadcast(intent);
                break;
            case R.id.silent_uninstall1:
                intent=new Intent("com.honeywell.ezservice.SILENT_UNINSTALL_APKS");
                String[] pkgs = new String[] {
                        "com.ct.tool"};
                Bundle b2=new Bundle();
                b2.putStringArray("pkgs", pkgs);
                intent.putExtras(b2);
                sendBroadcast(intent);

                break;
            default:
                break;
        }
    }
    private BroadcastReceiver getSnBroadcast=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sn=intent.getStringExtra("sn");
            Toast.makeText(BroadcastWayActivity.this,sn,Toast.LENGTH_SHORT).show();
        }
    };
}
