package com.example.matheus.taskbar;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by matheus on 12/30/16.
 */

public class AlarmService extends IntentService {

    private NotificationManager alarmNotificationManager;

    public AlarmService( ) {
        super( "AlarmService" );
    }

    @Override
    public void onHandleIntent( Intent intent ) {
        sendNotification( "Hello, your alarm is sounding." );
    }

    private void sendNotification( String msg ) {
        alarmNotificationManager = ( NotificationManager ) this
                .getSystemService( Context.NOTIFICATION_SERVICE );

        PendingIntent contentIntent = PendingIntent.getActivity( this, 0,
                new Intent( this, Alarm.class ), 0 );

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
                this ).setContentTitle( "Alarm" ).setSmallIcon( R.drawable.logo )
                .setStyle( new NotificationCompat.BigTextStyle( ).bigText( msg ) )
                .setContentText( msg );


        alarmNotificationBuilder.setContentIntent( contentIntent );
        alarmNotificationManager.notify( 1, alarmNotificationBuilder.build( ) );
    }
}
