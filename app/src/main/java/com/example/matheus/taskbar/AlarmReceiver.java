package com.example.matheus.taskbar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by matheus on 12/30/16.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive( final Context context, Intent intent ) {
        //this will update the UI with message
        //Alarm inst = Alarm.instance( );

        //this will sound the alarm once
        Uri alarmUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_ALARM );
        if ( alarmUri == null ) {
            alarmUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        }
        Ringtone ringtone = RingtoneManager.getRingtone( context, alarmUri );
        ringtone.play( );

        //this will send a notification message
        ComponentName comp = new ComponentName( context.getPackageName( ),
                AlarmService.class.getName( ) );
        startWakefulService( context, ( intent.setComponent( comp ) ) );
        setResultCode( Activity.RESULT_OK );
    }
}