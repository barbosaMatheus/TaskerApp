package com.example.matheus.taskbar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

//TODO: comment this damn file

public class Alarm extends AppCompatActivity {
    AlarmManager alarm_manager;
    private PendingIntent pending_intent;
    public TimePicker time_picker;
    public ToggleButton toggle;
    public int alarm_set;
    private final int OFFSET = 12000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getSupportActionBar( ).setTitle( "Alarm/Reminder" );

        time_picker = ( TimePicker ) findViewById( R.id.alarm_time );
        alarm_manager = ( AlarmManager ) getSystemService( ALARM_SERVICE );
        toggle = ( ToggleButton ) this.findViewById( R.id.alarm_toggle );
        if( alarm_is_set( ) ) toggle.setChecked( true );
        else toggle.setChecked( false );

        /*//create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Information" );
        pop_up.setIcon( R.drawable.logo );
        pop_up.setMessage( "This feature is still under development. The alarm works, it will sound " +
                "at the time set, and cancelling it will keep it from sounding. Right now the main " +
                "issue is not being able to cancel the alarm while it's ringing. Currently, the " +
                "sound plays for a few seconds and stops itself. One way to manually stop it is to " +
                "keep the app open and then close it when the alarm sounds. Another main issue is that" +
                " the user must reset the ALARM toggle manually after the alarm sounds.");

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", null );

        //show pop-up
        pop_up.show( );*/
    }

    public void on_toggle( View view ) {
        if ( toggle.isChecked( ) ) {
            Calendar calendar = Calendar.getInstance( );
            calendar.set( Calendar.HOUR_OF_DAY, time_picker.getHour( ) );
            calendar.set( Calendar.MINUTE, time_picker.getMinute( ) );
            Intent intent = new Intent( Alarm.this, AlarmReceiver.class );
            final EditText edit = ( EditText ) this.findViewById( R.id.reminder_text );
            final String text = edit.getText( ).toString( );
            intent.putExtra( "msg", text );
            pending_intent = PendingIntent.getBroadcast( Alarm.this, 0, intent, 0 );
            alarm_manager.setExact( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis( )-OFFSET, pending_intent );
            alarm_set = 1;
            //Toast.makeText( getApplicationContext( ), "alarm is set", Toast.LENGTH_SHORT ).show( );
        } else {
            Intent ringing = new Intent( getApplicationContext( ), RingtonePlayingService.class );
            stopService( ringing );
            Intent intent = new Intent( Alarm.this, AlarmReceiver.class );
            pending_intent = PendingIntent.getBroadcast( Alarm.this, 0, intent, 0 );
            alarm_manager.cancel( pending_intent );
            alarm_set = 0;
            //Toast.makeText( getApplicationContext( ), "alarm disabled", Toast.LENGTH_SHORT ).show( );
        }
        update_storage( );
    }

    public boolean alarm_is_set( ) {
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        alarm_set = sp_file.getInt( "alarm_set", -1 );                          //get number of alarms set
        if( alarm_set < 0 ) {                                                        //if empty we make a new field called size
            SharedPreferences.Editor editor = sp_file.edit( );
            editor.putInt( "alarm_set", 0 );
            editor.apply( );
            return false;
        }
        return alarm_set == 1;
    }

    public void update_storage( ) {
        SharedPreferences sp_file = getPreferences(Context.MODE_PRIVATE);     //make shared preferences object
        SharedPreferences.Editor editor = sp_file.edit();                      //make editor object
        editor.putInt("alarm_set", alarm_set);
        editor.apply();
    }

    //overriding method that gets called
    //when the back button is pressed
    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
        update_storage( );
    }

    //shows a pop-up with some help information
    public void show_help_pop( View view ) {
        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Alarm/Reminder Help" );
        pop_up.setIcon( R.drawable.logo );
        final String help_message = "This alarm is mainly designed for daily reminders, such as" +
                " meetings and timed activities, it is not particularly meant to be used as a wake" +
                "-up alarm in the morning.\n\nThe interface provides simplicity. To set the alarm" +
                ", pick the hour and minute using the hands of the clock (make sure to pick AM/PM" +
                " correctly) and set it by clicking the toggle button in the center. The button " +
                "text will now read \"ON\"You may also wish to provide your own reminder text in" +
                " the area just below time picker. Use the same toggle button to disable the " +
                "alarm. Once the alarm sounds, a notification will appear on your screen. Click " +
                "the notification (may have to unlock screen first) to open up the alarm screen" +
                " and then click the toggle button to disable the alarm and turn off the sound.";
        pop_up.setMessage( help_message );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", null );

        //show pop-up
        pop_up.show( );
    }
}
