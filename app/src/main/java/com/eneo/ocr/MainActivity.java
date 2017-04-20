/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eneo.ocr;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.eneo.ocr.Model.MyShortcuts;

import java.util.HashMap;

import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
    private static CardGridArrayAdapter mCardArrayAdapter;
    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";

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
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);
        setSupportActionBar(toolbar);
        setTitle("  Read Meter Index");
        TextView textView = (TextView) findViewById(R.id.status_message);
        textView.setText("Click the pink button below to read the Meter Index using the camera");
        statusMessage = (TextView) findViewById(R.id.status_message);
        textValue = (TextView) findViewById(R.id.text_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_text).setOnClickListener(this);
        Button read = (Button) findViewById(R.id.read_text);
        read.setText("Read Meter Index");








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

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {

//                    TODO handle after detecting text here. Draw a popup to continue if successful to another activity
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);
                    final String index = text;
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setTitle("Please confirm. Is this the meter INDEX number?");
                    alert.setMessage(index);

                    // Set an EditText view to get user input


                    alert.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                    // store the meter index to sp
                                    MyShortcuts.setDefaults("index", index, getBaseContext());
                                    Intent intent = new Intent(getBaseContext(), SerialActivity.class);
                                    startActivity(intent);


                                }
                            });

                    alert.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // Canceled.
                                    textValue.setText("Click detect meter details to capture the Meter Index again.");
                                }
                            });

                    alert.show();


                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                            send(device);
                        }
                    } else {
                        Log.e("Permission denied", "permission denied for device " + device);
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
                send(aDevice);
            else
                manager.requestPermission(aDevice, mPermissionIntent);
            break;
//            }
        }


    }


    private void send(UsbDevice device){
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbif = device.getInterface(i);

            UsbEndpoint tOut = null;
            UsbEndpoint tIn = null;

            int tEndpointCnt = usbif.getEndpointCount();
            if (tEndpointCnt >= 2) {
                for (int j = 0; j < tEndpointCnt; j++) {
                    Log.e("Endpoint of usb in no.", usbif.getEndpoint(j).getType() + "");
                    if (usbif.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT) {
                            tOut = usbif.getEndpoint(j);
                            Log.e("gotten an out usb", "out");
                            openPort(device,tOut);
                        } else if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN) {
                            tIn = usbif.getEndpoint(j);
                            Log.e("gotten an in usb", "in");
                        }
                    }
                }

                if (tOut != null && tIn != null) {
                    // This interface have both USB_DIR_OUT
                    // and USB_DIR_IN of USB_ENDPOINT_XFER_BULK
                   /* usbInterface = usbif;
                    epOUT = tOut;
                    epIN = tIn;*/


                }
            }

        }

    }

    private void openPort(UsbDevice device,UsbEndpoint ep) {

        final UsbInterface intf = device.getInterface(0);
        final UsbEndpoint endpoint = intf.getEndpoint(0);
        final UsbDeviceConnection connection = manager.openDevice(device);
        connection.claimInterface(intf, forceClaim);
        UsbInterface usbIf = device.getInterface(0);
        Log.e("countd", device.getInterfaceCount() + " +" + usbIf.getEndpointCount());

        UsbEndpoint epIN = null;
        UsbEndpoint epOUT = ep;

        byte counter = 0;


       /* for (int i = 0; i < usbIf.getEndpointCount(); i++) {
            Log.e("Endpoint address","EP: " + String.format("0x%02X", usbIf.getEndpoint(i).getAddress()));
//            if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                Log.e("bulk","Bulk Endpoint");
                if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                    epIN = usbIf.getEndpoint(i);
                    Log.e("IN","IN Endpoint"); }
                else {
                    epOUT = usbIf.getEndpoint(i);
                    Log.e("out","out Endpoint");
                }

//            } else {
//                Log.e("Not bulk","usb interface Not Bulk");
//            }
        }*/


        UsbInterface usbInterface;


        final UsbEndpoint finalEpOUT = epOUT;
        final UsbEndpoint finalEpIN = epIN;
        new Thread(new Runnable() {
            public void run() {

                String stringToConvert = "This String is 76 characters long and will be converted to an array of bytes";
                byte[] theByteArray = stringToConvert.getBytes();

               /* int bufferMaxLength=endpoint.getMaxPacketSize();
                ByteBuffer buffer = ByteBuffer.allocate(bufferMaxLength);
                UsbRequest request = new UsbRequest(); // create an URB
                request.initialize(connection, endpoint);
                buffer.put(theByteArray);

                // queue the outbound request
                boolean retval = request.queue(buffer, 1);
                if (connection.requestWait() == request) {
                    // wait for confirmation (request was sent)
                    UsbRequest inRequest = new UsbRequest();
                    // URB for the incoming data
                    inRequest.initialize(connection, endpoint);
                    // the direction is dictated by this initialisation to the incoming endpoint.
                    if(inRequest.queue(buffer, bufferMaxLength) == true){
                        connection.requestWait();
                        // wait for this request to be completed
                        // at this point buffer contains the data received
                    }
                }*/
                int r = connection.bulkTransfer(finalEpOUT, theByteArray, theByteArray.length, TIMEOUT);
                if (r != -1) {
                    Log.e("success", String.format("Written %s bytes to the dongle. Data written: %s", r, composeString(bytes)));

                    new Thread(new Runnable() {
                        public void run() {
//                            MyShortcuts.showToast("error RObe side",getBaseContext());
                        }
                    }).start();

                } else {
                    new Thread(new Runnable() {
                        public void run() {
//                            MyShortcuts.showToast("error on my side",getBaseContext());
                        }
                    }).start();

                    Log.e("error", "Error happened while writing data. No ACK");
                }

                // Release the usb interface.
                connection.releaseInterface(intf);
                connection.close();
                connection.close();
                Log.e("Connecting", stringToConvert.toString());





          /*      String get = "$fDump G" + "\n";
                Log.e("Sending: " , get);

                byte[] by = get.getBytes();

                // This is where it sends
                Log.e("out","out " + connection.bulkTransfer(finalEpOUT, by, by.length, 500));

                // This is where it is meant to receive
                byte[] buffer = new byte[4096];

                StringBuilder str = new StringBuilder();

                if (connection.bulkTransfer(finalEpIN, buffer, 4096, 500) >= 0) {
                    for (int i = 2; i < 4096; i++) {
                        if (buffer[i] != 0) {
                            str.append((char) buffer[i]);
                        } else {
                            Log.e("String",str.toString());
                            break;
                        }
                    }

                }
                // this shows the complete string
                Log.e("complete String",str.toString());*/
            }
        }).start();


    }

    @Override
    protected void onStop() {

        /*if (mUsbReceiver != null) {
            unregisterReceiver(mUsbReceiver);
        }*/
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

       /* try {
            if (mUsbReceiver != null)
                unregisterReceiver(mUsbReceiver);
        } catch (Exception e) {

        }
*/        super.onDestroy();

    }

    private String composeString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(b);
            builder.append(" ");
        }

        return builder.toString();
    }


}
