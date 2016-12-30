package com.example.matheus.taskbar;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tasks extends ListActivity {

    public ArrayList<Task> current_tasks;           //holds task objects
    public List<Map<String, String>> tasks;         //array list to hold info for ListView
    ListView task_list;                             //ListView instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        //initialize globals
        current_tasks = new ArrayList<>( );
        tasks = new ArrayList<>( );
        task_list = (ListView) this.findViewById( android.R.id.list );

        //Idk what this is but cell color changing doesn't work without it
        task_list.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );

        //gets data from file and updates array list
        update_list( );

        //set adapter for the list
        task_list.setAdapter( get_list_adapter( ) );

        //overrides the onItemClick method and sets it as
        //the new click listener for list items
        task_list.setOnItemClickListener( new AdapterView.OnItemClickListener( ) {
            //this method toggles the color and selection status of
            //each item on the list view and each car in the array
            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
                if( !( current_tasks.get( i ).selected ) ) {
                    view.setBackgroundResource( R.color.selected_row );
                    current_tasks.get( i ).selected = true;
                    current_tasks.get( i ).completed = true;
                }
                else {
                    view.setBackgroundResource( android.R.color.transparent );
                    current_tasks.get( i ).selected = false;
                    current_tasks.get( i ).completed = false;
                }

                task_list.setAdapter( get_list_adapter( ) );
                task_list.refreshDrawableState( );
            }
        } );

        //setting long click listener to edit text
        task_list.setLongClickable( true );
        task_list.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener( ) {
            @Override
            public boolean onItemLongClick( AdapterView<?> arg0, View view, final int pos, long id ) {
                show_edit_pop_up( pos );                         //show the pop up to edit text
                task_list.refreshDrawableState( );               //redraw list
                return true;                                     //return true for some reason
            }
        } );

        //setting up long click listener
        //for the clear all button
        Button clear_all = ( Button ) this.findViewById( R.id.ca_button );
        clear_all.setOnLongClickListener( new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick(View view) {
                current_tasks.clear( );                         //clear all contents
                task_list.setAdapter( get_list_adapter( ) );    //make new adapter
                update_storage( );                              //update internal storage
                task_list.refreshDrawableState( );              //redraw list
                return true;
            }
        } );
    }

    //shows pop up to add new item
    //to the list
    public void show_add_pop_up( ) {
        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Add Entry" );
        pop_up.setIcon( R.drawable.logo );
        pop_up.setMessage( "Enter your text below and press \'OK\'" );

        //create edit text object
        final EditText input = new EditText( this );
        pop_up.setView( input );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                final String text = input.getText( ).toString( );
                if( text.isEmpty( ) ) { //check for no text
                    Toast.makeText( getApplicationContext( ), "cannot add empty",
                            Toast.LENGTH_SHORT ).show( );
                    return; //leave so we don't take any info
                }

                current_tasks.add( new Task( text ) );
                task_list.setAdapter( get_list_adapter( ) );     //get a new adapter
                update_storage( );                               //update internal storage
            }
        } );
        pop_up.setNegativeButton( "Cancel", null );
        final AlertDialog alert = pop_up.create( );
        //force the keyboard to come up
        alert.getWindow( ).setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );

        //show pop-up
        alert.show( );
    }

    //shows pop up on long press so the
    //user can edit task contents
    public void show_edit_pop_up( int pos ) {
        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Edit Entry" );
        pop_up.setIcon( R.drawable.logo );
        pop_up.setMessage( "Edit your entry below and press \'OK\'" );

        //create edit text object and fill with
        //current task contents to be in dialog box
        final EditText input = new EditText( this );
        final int _pos = pos;
        input.setText( current_tasks.get( pos ).description );
        pop_up.setView( input );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                final String text = input.getText( ).toString( );
                if( text.isEmpty( ) ) { //check for no text
                    Toast.makeText( getApplicationContext( ), "cannot add empty",
                            Toast.LENGTH_SHORT ).show( );
                    return; //leave so we don't take any info
                }

                current_tasks.get( _pos ).description = text;
                task_list.setAdapter( get_list_adapter( ) );     //get a new adapter
                update_storage( );                               //update internal storage
            }
        } );
        pop_up.setNegativeButton( "Cancel", null );
        final AlertDialog alert = pop_up.create( );
        //forces keyboard to come up
        alert.getWindow( ).setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );

        //show pop-up
        alert.show( );
    }

    //returns an adapter object based on modified task data
    public SimpleAdapter get_list_adapter( ) {
        //clear the List first
        tasks.clear( );

        //repopulate the list
        for( int i = 0; i < current_tasks.size( ); ++i ) {
            //String key = "task_" + Integer.toString( i+1 );                           //the key for the ith object is 'task_i'
            //Task task = (Task)getIntent( ).getSerializableExtra( key );               //save the serialized task object
            //current_tasks.add( task );                                                //add this new object to the current cars in the vie
            Map<String, String> task_data = new HashMap<>(2);                           //this map will hold one item to be displayed on the list
            String title = current_tasks.get( i ).description;                          //build the title
            String subtitle =  current_tasks.get( i ).completed ? "Completed" : "Pending";   //build subtitle
            task_data.put( "title", title );                                            //put these in the map
            task_data.put( "subtitle", subtitle );
            tasks.add( task_data );                                                     //add map to the item (map) to the list
        }

        //then create, set up and return new list adapter
        return new SimpleAdapter( this, tasks, android.R.layout.simple_list_item_2,
                new String[] { "title", "subtitle" },
                new int[] { android.R.id.text1, android.R.id.text2 } ) {
            @Override //override getView method so we can paint the cell before it goes out
            public View getView( int pos, View convertView, ViewGroup parent ) {
                View view = super.getView( pos, convertView, parent );
                if( current_tasks.get( pos ).selected ) {                       //if this item was selected before, paint it green
                    view.setBackgroundResource( R.color.selected_row );
                }
                else {                                                         //else just leave it transparent
                    view.setBackgroundResource( android.R.color.transparent );
                }
                return view;                                                   //return a new list item (view)
            }

            @Override //override the getCount method so we don't crash on an empty list
            public int getCount( ) {
                return current_tasks.size( );
            }
        };
    }

    //this method will be set up as the click listener
    //for all the buttons on this view
    public void button_click_listener( View view ) {
        if( view == this.findViewById( R.id.ca_button ) ) { //if clear all is pressed
            if( current_tasks.isEmpty( ) );                     //does nothing for an empty list
            Toast.makeText( getApplicationContext( ), "hold down to clear all", Toast.LENGTH_SHORT ).show( );
        }
        else if( view == this.findViewById( R.id.clear_button ) ) {   //if clear is pressed
            for( int i = 0; i < current_tasks.size( ); ++i ) {        //remove the selected task objects
                if( current_tasks.get( i ).completed ) {
                    current_tasks.remove( i );
                    --i;                                              //move i back one to compensate for removal of item
                }
            }
            task_list.setAdapter( get_list_adapter( ) );              //set new adapter
            task_list.refreshDrawableState( );                        //redraw list
            update_storage( );                                        //update internal storage
        }
        else if( view == this.findViewById( R.id.add_button ) ) {   //if add is pressed
            /*Intent intent = new Intent( this, AddTask.class );      //create intent object
            intent.putExtra( "size", current_tasks.size( ) );       //send size of array list with intent
            for( int i = 0; i < current_tasks.size( ); ++i ) {      //loop through array list and but objects in intent
                String key = "task_" + Integer.toString( i );       //create key
                intent.putExtra( key, current_tasks.get( i ) );     //add object to intent
            }
            startActivityForResult( intent, 1 );                    //start Add task activity for result with id "1"*/
            show_add_pop_up( );                                     //show the add pop-up
            task_list.refreshDrawableState( );                      //redraw list
        }
    }

    //this method gets called when
    //we return from a launched activity
    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        if( requestCode == 1 && resultCode == RESULT_OK ) {   //if we are returning from add
            int size = data.getIntExtra( "size", 0 ); //get size of list
            if( size > 0 ) current_tasks.clear( );            //if the list gets cleared we don't clear the data yet
            else {
                //Log.d( "DEBUG", "Size: " + Integer.toString( size ) );
                return;
            }

            for( int i = 0; i < size; ++i ) {                                  //loop through and refill array list
                String key = "task_" + Integer.toString( i );                  //create key
                Task task = (Task) data.getSerializableExtra( key );           //pull data and cast
                current_tasks.add( task );                                     //add task to list
            }

            task_list.setAdapter( get_list_adapter( ) );                        //set new adapter
            task_list.refreshDrawableState( );                                  //call for redraw
            update_storage( );                                                  //update store with new data
        }
    }

    //gets data from "shared preferences"
    //and updates the array list
    public void update_list( ) {
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        int size = sp_file.getInt( "size", 0 );                                 //get size from the file
        if( size < 1 ) {                                                        //if empty we make a new field called size
            SharedPreferences.Editor editor = sp_file.edit( );
            editor.putInt( "size", 0 );
            editor.apply( );
            return;
        }

        //if we have some data then we can update the array list
        current_tasks.clear( );                                                 //clear array list
        for( int i = 0; i < size; ++i ) {                                       //loop through the file
            String key = "task_" + Integer.toString( i );                       //create key
            current_tasks.add( new Task( sp_file.getString( key, "" ) ) );      //add data to array list
        }
    }

    //updates the persistent data based
    //on the current array list data
    public void update_storage( ) {
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        SharedPreferences.Editor editor = sp_file.edit( );                      //make editor object
        editor.clear( );                                                        //clear all current data
        editor.commit( );                                                       //commit changes right away

        editor.putInt( "size", current_tasks.size( ) );                         //write new size
        editor.apply( );

        for( int i = 0; i < current_tasks.size( ); ++i ) {                      //loop through array list
            String key = "task_" + Integer.toString( i );                       //create key
            editor.putString( key, current_tasks.get( i ).description );        //write to storage
            editor.apply( );
        }
    }

    //overriding method that gets called
    //when the home button is pressed
    @Override
    public void onBackPressed( ) {
        update_storage( );
        finish( );
    }
}
