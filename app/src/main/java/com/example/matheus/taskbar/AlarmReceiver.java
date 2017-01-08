package com.example.matheus.taskbar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    Ringtone ringtone;


    @Override
    public void onReceive( final Context context, Intent intent ) {
        //this will update the UI with message
        //Alarm inst = Alarm.instance( );

        //this will sound the alarm once
        /*Uri alarmUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_ALARM );
        if ( alarmUri == null ) {
            alarmUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        }
        ringtone = RingtoneManager.getRingtone( context, alarmUri );
        ringtone.setStreamType( AudioManager.STREAM_ALARM );
        ringtone.play( );*/
        Intent ringing  = new Intent( context, RingtonePlayingService.class );
        context.startService( ringing );

        //this will send a notification message
        ComponentName comp = new ComponentName( context.getPackageName( ),
                AlarmService.class.getName( ) );
        startWakefulService( context, ( intent.setComponent( comp ) ) );
        setResultCode( Activity.RESULT_OK );
    }
}
