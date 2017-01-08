package com.example.matheus.taskbar;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class RingtonePlayingService extends Service {
    private Ringtone ringtone;

    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        Uri alarmUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_ALARM );
        if ( alarmUri == null ) {
            alarmUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        }
        ringtone = RingtoneManager.getRingtone( this, alarmUri );
        ringtone.setStreamType( AudioManager.STREAM_ALARM );
        ringtone.play( );

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy( ) {
        ringtone.stop( );
    }
}
