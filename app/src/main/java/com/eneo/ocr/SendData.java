package com.eneo.ocr;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.eneo.ocr.Model.MyShortcuts;

import java.util.HashMap;

//import com.google.android.gms.samples.vision.barcodereader.R;

public class SendData extends AppCompatActivity {
    static UsbManager manager;
    PendingIntent mPermissionIntent;
    private byte[] bytes;
    private static int TIMEOUT = 0;
    private boolean forceClaim = true;


    private static final String ACTION_USB_PERMISSION =
            "com.google.android.gms.samples.vision.ocrreader.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        UsbDevice Device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        /*HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            //your code
        }*/

//        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);


        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        /*IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);*/

//Listening for new devices when connected
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter2.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbReceiver, filter2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not available", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            openPort(device);
                        }
                    } else {
                        Log.d("Permission denied", "permission denied for device " + device);
                        MyShortcuts.showToast("Permission denied", getBaseContext());
                        manager.requestPermission(device, mPermissionIntent);
                    }
                }
            }
        }
    };


    public void onResume() {
        super.onResume();

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        for (HashMap.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
            UsbDevice aDevice = entry.getValue();
//            if ((aDevice.getProductId() == 8965) && (aDevice.getVendorId() == 1659) ){

            if (manager.hasPermission(aDevice))
                openPort(aDevice);
            else
                manager.requestPermission(aDevice, mPermissionIntent);
            break;
//            }
        }


    }

    private void openPort(UsbDevice device) {

        UsbInterface intf = device.getInterface(0);
        final UsbEndpoint endpoint = intf.getEndpoint(0);
        final UsbDeviceConnection connection = manager.openDevice(device);
        connection.claimInterface(intf, forceClaim);




            new Thread(new Runnable() {
                public void run() {
                    String test="I am happy";
//                    byte= test.getBytes();

                    String stringToConvert = "This String is 76 characters long and will be converted to an array of bytes";
                    byte[] theByteArray = stringToConvert.getBytes();
                    connection.bulkTransfer(endpoint,theByteArray, theByteArray.length, TIMEOUT);
                    Log.e("Connecting",bytes.toString());
                }
            }).start();
        }
}
