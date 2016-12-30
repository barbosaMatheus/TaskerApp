package com.example.matheus.taskbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class Alarm extends AppCompatActivity {

    AlarmManager alarm_manager;
    private PendingIntent pending_intent;
    public TimePicker time_picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        time_picker = ( TimePicker ) findViewById( R.id.alarm_time );
        alarm_manager = ( AlarmManager ) getSystemService( ALARM_SERVICE );

        //TODO: fix so we don't have to see this message
        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Information" );
        pop_up.setIcon( R.drawable.logo );
        pop_up.setMessage( "This feature is not fully implemented yet. But this is what the screen" +
                " will look like to interface with the alarm." );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", null );

        //show pop-up
        pop_up.show( );
    }

    public void on_toggle( View view ) {
        /*if ( ( ( ToggleButton ) view ).isChecked( ) ) {
            Calendar calendar = Calendar.getInstance( );
            calendar.set( Calendar.HOUR_OF_DAY, time_picker.getHour( ) );
            calendar.set( Calendar.MINUTE, time_picker.getMinute( ) );
            Intent intent = new Intent( Alarm.this, Alarm.class );
            pending_intent = PendingIntent.getBroadcast( Alarm.this, 0, intent, 0 );
            alarm_manager.set( AlarmManager.RTC, calendar.getTimeInMillis( ), pending_intent );
            Toast.makeText( getApplicationContext( ), "alarm is set", Toast.LENGTH_SHORT ).show( );
        } else {
            alarm_manager.cancel( pending_intent );
            Toast.makeText( getApplicationContext( ), "alarm disabled", Toast.LENGTH_SHORT ).show( );
        }*/
    }
}
