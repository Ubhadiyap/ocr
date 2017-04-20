package com.eneo.ocr;
/*


import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

*/
/**
 * Created by stephineosoro on 16/09/16.
 *//*

public class Test {
    private Runnable mLoop = new Runnable() {

        @Override
        public void run() {
            UsbDevice dev = sDevice;
            if (dev == null)
                return;
            UsbManager usbm = (UsbManager) getSystemService(USB_SERVICE);
            UsbDeviceConnection conn = usbm.openDevice(dev);
            l("Interface Count: " + dev.getInterfaceCount());
            l("Using "
                    + String.format("%04X:%04X", sDevice.getVendorId(),
                    sDevice.getProductId()));

            if (!conn.claimInterface(dev.getInterface(0), true))
                return;

            conn.controlTransfer(0x40, 0, 0, 0, null, 0, 0);// reset
            // mConnection.controlTransfer(0×40,
            // 0, 1, 0, null, 0,
            // 0);//clear Rx
            conn.controlTransfer(0x40, 0, 2, 0, null, 0, 0);// clear Tx
            conn.controlTransfer(0x40, 0x02, 0x0000, 0, null, 0, 0);// flow
            // control
            // none
            conn.controlTransfer(0x40, 0x03, 0x0034, 0, null, 0, 0);// baudrate
            // 57600
            conn.controlTransfer(0x40, 0x04, 0x0008, 0, null, 0, 0);// data bit
            // 8, parity
            // none,
            // stop bit
            // 1, tx off

            UsbEndpoint epIN = null;
            UsbEndpoint epOUT = null;

            byte counter = 0;

            UsbInterface usbIf = dev.getInterface(0);
            for (int i = 0; i < usbIf.getEndpointCount(); i++) {
                l("EP: "
                        + String.format("0x%02X", usbIf.getEndpoint(i)
                        .getAddress()));
                if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    l("Bulk Endpoint");
                    if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
                        epIN = usbIf.getEndpoint(i);
                    else
                        epOUT = usbIf.getEndpoint(i);
                } else {
                    l("Not Bulk");
                }
            }

            for (;;) {// this is the main loop for transferring
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String get = "$fDump G" + "\n";
                l("Sending: " + get);

                byte[] by = get.getBytes();

                // This is where it sends
                l("out " + conn.bulkTransfer(epOUT, by, by.length, 500));

                // This is where it is meant to receive
                byte[] buffer = new byte[4096];

                StringBuilder str = new StringBuilder();

                if (conn.bulkTransfer(epIN, buffer, 4096, 500) >= 0) {
                    for (int i = 2; i < 4096; i++) {
                        if (buffer[i] != 0) {
                            str.append((char) buffer[i]);
                        } else {
                            l(str);
                            break;
                        }
                    }

                }
                // this shows the complete string
                l(str);

                if (mStop) {
                    mStopped = true;
                    return;
                }
                l("sent " + counter);
                counter++;
                counter = (byte) (counter % 16);
            }
        }
    };
}
*/







//TODO GRADLE


/*
*
*
*
* apply plugin: 'com.android.application'

android {
    compileSdkVersion 'android-N'
    buildToolsVersion "24.0.2"
    defaultConfig {
        applicationId "com.google.android.gms.samples.vision.barcodereader"
        minSdkVersion 15
        targetSdkVersion 'N'
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    productFlavors {
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:support-v4:24.2.1'
    compile 'com.android.support:appcompat-v7:24.2.1'
    compile 'com.google.android.gms:play-services-vision:9.2.0'
    compile 'com.android.support:design:24.2.1'
    //    compile 'com.google.android.gms:play-services-vision:9.0.0+'
    /*compile 'org.osmdroid:osmdroid-android:5.2@aar'
        compile 'org.osmdroid:osmdroid-third-party:5.0.1@aar'*/
/*
compile 'com.github.gabrielemariotti.cards:cardslib-core:2.1.0'
        compile 'com.jakewharton:butterknife:7.0.1'
        compile 'com.android.volley:volley:1.0.0'
        compile 'org.litepal.android:core:1.3.2'
        compile 'com.google.android.gms:play-services-location:9.2.0'
        //    compile project(':usb-serial-for-android:usbSerialForAndroid')
        compile 'com.github.paolorotolo:appintro:4.0.0'
        compile 'io.nlopez.smartlocation:library:3.2.1'
        }

*/


/*apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion "23.0.1"

    defaultConfig {
        applicationId "com.google.android.gms.samples.vision.barcodereader"
        minSdkVersion 14
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:support-v4:23.0.1'
    compile 'com.android.support:appcompat-v7:23.0.1'
    compile 'com.google.android.gms:play-services-vision:9.2.0'
    compile 'com.android.support:design:23.0.1'
//    compile 'com.google.android.gms:play-services-vision:9.0.0+'
    /*compile 'org.osmdroid:osmdroid-android:5.2@aar'
    compile 'org.osmdroid:osmdroid-third-party:5.0.1@aar'*/
/*compile 'com.github.gabrielemariotti.cards:cardslib-core:2.1.0'
        compile 'com.jakewharton:butterknife:7.0.1'
        compile 'com.android.volley:volley:1.0.0'
        compile 'org.litepal.android:core:1.3.2'
        compile 'com.google.android.gms:play-services-location:9.2.0'
//    compile project(':usb-serial-for-android:usbSerialForAndroid')
        compile 'com.github.paolorotolo:appintro:4.0.0'
        compile 'io.nlopez.smartlocation:library:3.2.1'
        }
        */


        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;

        import org.opencv.android.OpenCVLoader;

public class Test extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}