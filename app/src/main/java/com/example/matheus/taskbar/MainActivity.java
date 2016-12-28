package com.example.matheus.taskbar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//NOTE: ALL GRAPHICS GENERATED BY ME OR ACQUIRED FROM PIXABAY.COM

public class MainActivity extends AppCompatActivity {

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
    }
}
