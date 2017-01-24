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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class Alarm extends AppCompatActivity {
    AlarmManager alarm_manager;                //alarm manager used to set the alarm
    private PendingIntent pending_intent;      //pending intent object starts the alarm service
    public TimePicker time_picker;             //time picker object used to get user input
    public ToggleButton toggle;                //toggle button object
    public int alarm_set;                      //to know if an alarm is set that way we can change the toggle button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getSupportActionBar( ).setTitle( "Alarm/Reminder" );
        getSupportActionBar( ).setDisplayHomeAsUpEnabled( true );

        //initialize members and configure
        //the toggle button accordingly
        time_picker = ( TimePicker ) findViewById( R.id.alarm_time );
        alarm_manager = ( AlarmManager ) getSystemService( ALARM_SERVICE );
        toggle = ( ToggleButton ) this.findViewById( R.id.alarm_toggle );
        if( alarm_is_set( ) ) toggle.setChecked( true );
        else toggle.setChecked( false );
    }

    //called when the alarm toggle
    //button is clicked
    public void on_toggle( View view ) {
        if ( toggle.isChecked( ) ) {  //if it just toggled on
            Calendar calendar = Calendar.getInstance( );                     //get a calendar object
            calendar.set( Calendar.HOUR_OF_DAY, time_picker.getHour( ) );    //set the hour
            calendar.set( Calendar.MINUTE, time_picker.getMinute( ) );       //set the minute
            calendar.set( Calendar.SECOND, 0 );                              //set seconds
            Intent intent = new Intent( Alarm.this, AlarmReceiver.class );   //make the intent to alarm receiver
            final EditText edit = ( EditText ) this.findViewById( R.id.reminder_text );
            final String text = edit.getText( ).toString( );
            intent.putExtra( "msg", text );                                  //put the user message in the intent
            pending_intent = PendingIntent.getBroadcast( Alarm.this, 0, intent, 0 );  //get pending intent from intent
            alarm_manager.setExact( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis( ), pending_intent );
            alarm_set = 1;
            //Toast.makeText( getApplicationContext( ), "alarm is set", Toast.LENGTH_SHORT ).show( );
        } else {  //if it just toggled off
            //stop the ringtone service if it's playing
            Intent ringing = new Intent( getApplicationContext( ), RingtonePlayingService.class );
            stopService( ringing );
            //cancel the pending intent for alarm
            Intent intent = new Intent( Alarm.this, AlarmReceiver.class );
            pending_intent = PendingIntent.getBroadcast( Alarm.this, 0, intent, 0 );
            alarm_manager.cancel( pending_intent );
            alarm_set = 0;
            //Toast.makeText( getApplicationContext( ), "alarm disabled", Toast.LENGTH_SHORT ).show( );
        }
        update_storage( );
    }

    //finds out if an alarm
    //is already set and returns
    //true if it's set, false otw
    public boolean alarm_is_set( ) {
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        alarm_set = sp_file.getInt( "alarm_set", -1 );                          //get number of alarms set
        if( alarm_set < 0 ) {                                                   //if empty we make a new field called alarm_set
            SharedPreferences.Editor editor = sp_file.edit( );
            editor.putInt( "alarm_set", 0 );
            editor.apply( );
            return false;
        }
        return alarm_set == 1;
    }

    //updates persistent data
    //based on temporary changes
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
        update_storage( );
        super.onBackPressed( );
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
                "text will now read \"ON\". You may also wish to provide your own reminder text in" +
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

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch( item.getItemId( ) ) {
            case android.R.id.home:
                this.onBackPressed( );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }
}
