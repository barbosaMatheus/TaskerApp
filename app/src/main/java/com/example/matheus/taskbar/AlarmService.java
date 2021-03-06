package com.example.matheus.taskbar;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlarmService extends IntentService {

    private NotificationManager alarmNotificationManager;

    public AlarmService( ) {
        super( "AlarmService" );
    }

    @Override
    public void onHandleIntent( Intent intent ) {
        final String intent_message = intent.getStringExtra( "msg" );
        final String message = intent_message.isEmpty( ) ? "Reminder alarm. Click here to go to app and disable." : intent_message;
        sendNotification( message );
    }

    private void sendNotification( String msg ) {
        alarmNotificationManager = ( NotificationManager ) this
                .getSystemService( Context.NOTIFICATION_SERVICE );

        Intent intent = new Intent( this, Alarm.class );
        PendingIntent contentIntent = PendingIntent.getActivity( this, 0, intent, 0 );
        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
                this ).setContentTitle( "Alarm" ).setSmallIcon( R.drawable.logo )
                .setStyle( new NotificationCompat.BigTextStyle( ).bigText( msg ) )
                .setAutoCancel( true ).setContentText( msg );


        alarmNotificationBuilder.setContentIntent( contentIntent );
        alarmNotificationManager.notify( 1, alarmNotificationBuilder.build( ) );
    }
}
