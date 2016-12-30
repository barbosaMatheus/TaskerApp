package com.example.matheus.taskbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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
    //public static Alarm inst;

    /*public static Alarm instance( ) {
        return inst;
    }*/

    /*@Override
    public void onStart( ) {
        super.onStart( );
        inst = this;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        time_picker = ( TimePicker ) findViewById( R.id.alarm_time );
        ToggleButton alarm_toggle = ( ToggleButton ) findViewById( R.id.alarm_toggle );
        alarm_manager = ( AlarmManager ) getSystemService( ALARM_SERVICE );
    }

    public void on_toggle( View view ) {
        if ( ( ( ToggleButton ) view ).isChecked( ) ) {
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
        }
    }
}
