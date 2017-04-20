package com.eneo.ocr;

/**
 * Created by stephineosoro on 11/10/2016.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;


import com.eneo.ocr.Model.Datalocal;
import com.eneo.ocr.Model.MyShortcuts;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.tablemanager.Connector;

import static android.provider.Settings.NameValueTable.NAME;
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static android.provider.Settings.NameValueTable.NAME;

public class BluetoothServerActivity extends Activity {
    public static final int MESSAGE_READ = 1;

    private static final String TAG = "BluetoothServer";
    private static final boolean DEBUG = true;

    private static final UUID MY_UUID = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothAdapter mBTAdapter;
    private AcceptThread mAcceptThread;
    private EditText view;
    private static JSONObject jsonObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_client);
        SendToDesktop();
        view = new EditText(this);
        view.setGravity(Gravity.TOP | Gravity.LEFT);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null) {
            Log.i(TAG, "device does not support Bluetooth");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            setDiscoveralbe();
        } else {
            setDiscoveralbe();
        }
    }

    @Override
    protected void onPause() {
        //mAcceptThread.cancel();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setDiscoveralbe();
        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                view.append("Enabled BT\n");

                setDiscoveralbe();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class AcceptThread extends Thread {
        // The local server socket
        private BluetoothServerSocket mmServerSocket;
        UUID MY_UUID = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");

        public AcceptThread() {
        }

        public void run() {
            BluetoothSocket socket = null;

            BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

            // Listen to the server socket if we're not connected
            while (true) {

                try {
                    // Create a new listening server socket
                    Log.d("INFO", ".....Initializing RFCOMM SERVER....");

                    // MY_UUID is the UUID you want to use for communication
                    mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                    //mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);  you can also try using In Secure connection...

                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                    Log.e("socket",socket+"");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Log.d("INFO", "Closing Server Socket.....");
                    mmServerSocket.close();



                    InputStream tmpIn = null;
                    OutputStream tmpOut = null;

                    // Get the BluetoothSocket input and output streams

                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();


                    DataInputStream mmInStream = new DataInputStream(tmpIn);


                    byte[] buffer = new byte[1024];
                    int bytes;

                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer);
                    Log.e("Buffer",buffer.toString()+readMessage);

                    DataOutputStream mmOutStream = new DataOutputStream(tmpOut);
                    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(mmOutStream));
                    printWriter.write("Hello ! found you");
                    printWriter.flush();
                    printWriter.close();


                    // here you can use the Input Stream to take the string from the client whoever is connecting
                    //similarly use the output stream to send the data to the client
                } catch (Exception e) {
                    //catch your exception here
                }

            }
        }

    }

    /*private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {

            if (mBTAdapter.isEnabled()) {
                SharedPreferences prefs_btdev = getSharedPreferences("btdev", 0);
                String btdevaddr=prefs_btdev.getString("btdevaddr","?");

                if (btdevaddr != "?")
                {
                    BluetoothDevice device = btAdapter.getRemoteDevice(btdevaddr);

                    UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // bluetooth serial port service
                    //UUID SERIAL_UUID = device.getUuids()[0].getUuid(); //if you don't know the UUID of the bluetooth device service, you can get it like this from android cache

                    BluetoothSocket socket = null;

                    try {
                        socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                    } catch (Exception e) {Log.e("","Error creating socket");}

                    try {
                        socket.connect();
                        Log.e("","Connected");
                    } catch (IOException e) {
                        Log.e("",e.getMessage());
                        try {
                            Log.e("","trying fallback...");

                            socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                            socket.connect();

                            Log.e("","Connected");
                        }
                        catch (Exception e2) {
                            Log.e("", "Couldn't establish Bluetooth connection!");
                        }
                    }
                }
                else
                {
                    Log.e("","BT device not selected");
                }
            }


            BluetoothServerSocket tmp = null;
            try {
                tmp = mBTAdapter.listenUsingRfcommWithServiceRecord("BluetoothServer", MY_UUID);
            } catch (IOException e) {
                Log.e("Accept Error", e.getMessage());
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                    Log.e("now listening", "listen");
                } catch (IOException e) {
                    e.printStackTrace();

//                    Log.e("Accept Socket Error", );
                    break;
                }

                if (socket != null) {
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        Log.e("Socket Close Error", e.getMessage());
                    }
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("Socket Close Error", e.getMessage());

            }
        }
    }*/

    public void manageConnectedSocket(BluetoothSocket socket) {
        if (DEBUG) Log.d(TAG, "Connected");
        ConnectedThread server = new ConnectedThread(mHandler, socket);
        server.start();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.e("Message", readMessage);
                    view.append(readMessage);
            }
        }
    };

    private void setDiscoveralbe() {
        view.append("set BT as being discoverable during 2 minutes\n");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600); // 10 minutes
        startActivity(discoverableIntent);
    }


    private void SendToDesktop() {
        Cursor cursor = null;
        JSONArray jsonArray= new JSONArray();

        try {
            cursor = Connector.getDatabase().rawQuery("select * from datalocal order by id",
                    null);
            if (cursor.moveToFirst()) {
                do {
                    JSONObject jsonObject = new JSONObject();

                    String agency = cursor.getString(cursor.getColumnIndex("agency"));
                    String agencyID = cursor.getString(cursor.getColumnIndex("agencyID"));
                    String datetime = cursor.getString(cursor.getColumnIndex("datetime"));
                    String island = cursor.getString(cursor.getColumnIndex("island"));
                    String zone = cursor.getString(cursor.getColumnIndex("zone"));
                    String meter_serial = cursor.getString(cursor.getColumnIndex("meter_serial"));
                    String meter_index = cursor.getString(cursor.getColumnIndex("meter_index"));
                    String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                    String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                    String locationName = cursor.getString(cursor.getColumnIndex("locationName"));

                    jsonObject.put("agency",agency);
                    jsonObject.put("agencyID",agencyID);
                    jsonObject.put("date",datetime);
                    jsonObject.put("island",island);
                    jsonObject.put("zone",zone);
                    jsonObject.put("serialNo",meter_serial);
                    jsonObject.put("index",meter_index);
                    jsonObject.put("latitude",latitude);
                    jsonObject.put("longitude",longitude);
                    jsonObject.put("locationName",locationName);
                    jsonArray.put(jsonObject);


                } while (cursor.moveToNext());
                Log.e("jsonArray", jsonArray.toString());
                jsonObject= new JSONObject();
                jsonObject.put("data",jsonArray);
                Log.e("Final Object", jsonObject.toString());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }
}