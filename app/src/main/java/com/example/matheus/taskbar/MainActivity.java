package com.example.matheus.taskbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

//NOTE: ALL GRAPHICS GENERATED BY ME OR ACQUIRED FROM PIXABAY.COM

public class MainActivity extends AppCompatActivity {

    public final String help_message = "Welcome, and thank you for downloading Tasker!\n\n" +
            "To set an alarm go to the alarm screen by clicking on \"ALARM\" on the main " +
            "screen. Once in the alarm screen, choose a time using the time picker and make " +
            "sure the \"SET\" button is toggled on. Toggle the button off to deactivate the" +
            " alarm.\n\nTo manage your tasks go to the tasks screen by clicking on \"TASKS\"" +
            " on the main screen. Once in the tasks screen you can add entries by clicking " +
            "\"ADD\" or clear all entries by double-clicking \"CLEAR ALL\". To clear only " +
            "certain entries, single-click to highlight each entry and press \"CLEAR\" to " +
            "clear the highlighted entries. You may also edit entries after adding them by " +
            "holding down (long-pressing) on the entry and a pop-up window will appear on-" +
            "screen.\n\nFor questions/issues please e-mail Matheus (asaph@okstate.edu).";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //set as the onClick listener for all
    //buttons in this activity
    public void button_click_listener( View view ) {
        if( view == this.findViewById( R.id.tasks_button ) ) { //if tasks is pressed
            Intent intent = new Intent( this, Tasks.class );   //create intent object
            startActivity( intent );                           //go to tasks activity
        }
        else if( view == this.findViewById( R.id.help_button ) ) { //if help is pressed
            show_help_pop( );
        }
    }

    //shows a pop-up with some help information
    public void show_help_pop( ) {
        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Tasker Help" );
        pop_up.setMessage( help_message );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", null );

        //show pop-up
        pop_up.show( );
    }
}

