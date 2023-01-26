package com.myfirstapp.smsdetection;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

//

public class ReceiveSms extends BroadcastReceiver {
    FusedLocationProviderClient fusedLocationProviderClient;
    final String password = "pass";


    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        Bundle b = intent.getExtras();
        if (intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")) {
            Toast.makeText(context, "SmsReceived!", Toast.LENGTH_SHORT).show();
            if (b != null) {

                final Object[] pdusObj = (Object[]) b.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdusObj.length];
                for (int i = 0; i < messages.length; i++) {
                    String format = b.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                    ///SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String senderNum = messages[i].getOriginatingAddress();
                    String message = messages[i].getMessageBody();//
                    String[] messagess = message.split(" ");
                    //setlocation(senderNum,context);
                    if (messagess[0].equalsIgnoreCase("myhelper")&&messagess[1].equalsIgnoreCase(password)) {

                        SmsManager mySmsManager = SmsManager.getDefault();
                        Toast.makeText(context, senderNum + ":" + message, Toast.LENGTH_SHORT).show();

                        //location
                        if (messagess[2].equalsIgnoreCase("location")) {
                            setlocation(senderNum, context);

                        }

                        //ring phone
                        else if (messagess[2].equalsIgnoreCase("ringphone")) {
                            ringphone(context);
                            message = "Phone is ringing";
                            Toast.makeText(context, senderNum + ":" + message, Toast.LENGTH_SHORT).show();
                            mySmsManager.sendTextMessage(senderNum, null, message, null, null);

                        }
                    } else {
                        Toast.makeText(context, "message not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    //methods

    public void ringphone(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        //audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        MediaPlayer player = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        player.start();
        Toast.makeText(context, "phone is ringing", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("MissingPermission")
    public void setlocation(String phno, Context context) {

        Toast.makeText(context, "null1", Toast.LENGTH_SHORT).show();
        final String[] cordinates = new String[2];
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Toast.makeText(context, "null2", Toast.LENGTH_SHORT).show();

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(context, "null3", Toast.LENGTH_SHORT).show();

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    cordinates[0] = String.valueOf(location.getLatitude());
                    cordinates[1] = String.valueOf(location.getLongitude());
                    String messages = "latitude = " + cordinates[0] + " longitude = " + cordinates[1] + "1";

                    Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
                    SmsManager mySmsManager = SmsManager.getDefault();
                    mySmsManager.sendTextMessage(phno, null, messages, null, null);

                } else {
                    Toast.makeText(context, "null4", Toast.LENGTH_SHORT).show();

                    LocationRequest locationRequest = LocationRequest.create().setPriority(Priority.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                    LocationCallback locationCallback = new LocationCallback() {


                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            Location location1 = locationResult.getLastLocation();

                            assert location1 != null;
                            cordinates[0] = String.valueOf(location1.getLatitude());
                            cordinates[1] = String.valueOf(location1.getLongitude());
                            String messages = "latitude = " + cordinates[0] + " longitude = " + cordinates[1] + "2";

                            Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();

                            SmsManager mySmsManager = SmsManager.getDefault();
                            mySmsManager.sendTextMessage(phno, null, messages, null, null);


                        }

                    };
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            });

        } else {
            Toast.makeText(context, "null5", Toast.LENGTH_SHORT).show();

            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }


    //
}

//E/wifi_forwarder: RemoteConnection failed to initialize: RemoteConnection failed to open pipe
// E/netmgr: qemu_pipe_open_ns:62: Could not connect to the 'pipe:qemud:network' service: Invalid argument
//E/netmgr: Failed to open QEMU pipe 'qemud:network': Invalid argument
//E/wifi_forwarder: qemu_pipe_open_ns:62: Could not connect to the 'pipe:qemud:wififorward' service: Invalid argument

//E/android.system.suspend@1.0-service: Error opening kernel wakelock stats for: wakeup34: Permission denied
//E/android.system.suspend@1.0-service: Error opening kernel wakelock stats for: wakeup35: Permission denied

