package com.example.matheus.taskbar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddTask extends AppCompatActivity {

    public ArrayList<Task> tasks;  //holds array of tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //initialize members
        tasks = new ArrayList<>( );

        //fill array of tasks
        int size = getIntent( ).getIntExtra( "size", 0 );  //number of tasks coming in

        for( int i = 0; i < size; ++i ) {                  //loop through and add the objects
            String key = "task_" + i;                                       //generate key
            Task task = (Task) getIntent( ).getSerializableExtra( key );    //retrieve and cast the object
            tasks.add( task );                                              //add to array list
        }
    }

    //set as the click listener for all buttons
    public void button_clicked_listener( View view ) {             //if add is pressed
        if( view == this.findViewById( R.id.add_button2 ) ) {
            //get the description from the text view
            TextView text_view = (TextView) this.findViewById( R.id.description );
            String text = text_view.getText( ).toString( );

            //check if it's empty
            if( text.isEmpty( ) ) {
                Toast.makeText( getApplicationContext( ), "Task description cannot be empty", Toast.LENGTH_LONG ).show( );
                return;
            }

            //add a new task to the list
            tasks.add( new Task( text ) );

            //go back to main and send updated packet
            Intent intent = new Intent( this, MainActivity.class );  //make intent object
            //Log.d( "DEBUG", "Size: " + Integer.toString( tasks.size( ) ) );
            intent.putExtra( "size", tasks.size( ) );                //include size in extras
            for( int i = 0; i < tasks.size( ); ++i ) {               //loop through list of tasks to send
                String key = "task_" + Integer.toString( i );        //create key
                intent.putExtra( key, tasks.get( i ) );              //insert into intent
            }
            setResult( RESULT_OK, intent );                          //set result of this activity launch
            finish( );                                               //finish launch and go back to Main
        }
    }
}
