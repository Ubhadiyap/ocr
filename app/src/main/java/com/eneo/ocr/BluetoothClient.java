package com.eneo.ocr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class BluetoothClient extends AppCompatActivity {
    TextView textView;
    UUID MY_UUID = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");
    String send_msg;
    String rcv_msg;
    DataInputStream mmInStream ;
    DataOutputStream mmOutStream;
    private static final String TAG = "MainActivity";
    private static final String UUID_STRING = "00000000-0000-0000-0000-00000000ABCD"; // 32 hex digits
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_client);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        textView = (TextView) findViewById(R.id.tv);
        textView.setText("Not set");
        if(adapter == null) {
            textView.append("Bluetooth NOT Supported!");
            return;
        }
        new SendMessageToServer().execute(send_msg);
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendMessageToServer().execute(send_msg);
            }
        });

        // Request user to turn ON Bluetooth
        if(!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, RESULT_OK);
        }
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void onClick(View view) {
        Log.e(TAG, "onClick");
        new SendMessageToServer().execute(send_msg);
    }

    private class SendMessageToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... msg) {
            Log.d(TAG, "doInBackground");
            BluetoothSocket clientSocket = null;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.enable();
            // Client knows the server MAC address
            BluetoothDevice mmDevice = mBluetoothAdapter.getRemoteDevice("00:25:00:C3:1C:FE");
            Log.d(TAG, "got hold of remote device");
            try {
                // UUID string same used by server
                clientSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID
                        .fromString(UUID_STRING));

                Log.d(TAG, "bluetooth socket created");

                mBluetoothAdapter.cancelDiscovery(); 	// Cancel, discovery slows connection

                clientSocket.connect();
                Log.d(TAG, "connected to server");

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                out.writeUTF(msg[0]); 			// Send message to server
                Log.d(TAG, "Message Successfully sent to server");
                return in.readUTF();            // Read response from server
            } catch (Exception e) {

                Log.d(TAG, "Error creating bluetooth socket");
                Log.d(TAG, e.getMessage());
                return "";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute");
            rcv_msg = result;
            textView.setText(rcv_msg);
        }

    }



//    TODO

    private class AcceptThread extends Thread {
        // The local server socket
        private BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
        }

        public void run() {
            BluetoothSocket socket = null;

            BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

            // Listen to the server socket if we're not connected
            while (true) {

                try {
                    // Create a new listening server socket
                    Log.d(TAG, ".....Initializing RFCOMM SERVER....");

                    // MY_UUID is the UUID you want to use for communication
                    mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("GTI500", MY_UUID);
                    //mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);  you can also try using In Secure connection...

                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();

                } catch (Exception e) {

                }

                try {
                    Log.d(TAG, "Closing Server Socket.....");
                    mmServerSocket.close();



                    InputStream tmpIn = null;
                    OutputStream tmpOut = null;

                    // Get the BluetoothSocket input and output streams

                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();


                    DataInputStream mmInStream = new DataInputStream(tmpIn);
                    DataOutputStream mmOutStream = new DataOutputStream(tmpOut);

                    mmInStream = new DataInputStream(tmpIn);
                    mmOutStream = new DataOutputStream(tmpOut);

                    // here you can use the Input Stream to take the string from the client whoever is connecting
                    //similarly use the output stream to send the data to the client
                } catch (Exception e) {
                    //catch your exception here
                }

            }
        }

    }


}
