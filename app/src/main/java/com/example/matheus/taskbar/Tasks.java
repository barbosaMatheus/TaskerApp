package com.example.matheus.taskbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tasks extends AppCompatActivity {

    public ArrayList<Task> current_tasks;           //holds task objects
    public List<Map<String, String>> tasks;         //array list to hold info for ListView
    public SimpleAdapter adapter;                     //adapter object for the list
    ListView task_list;                             //ListView instance
    public int size;                                //number of saved tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        getSupportActionBar( ).setTitle( "Tasks" );
        //initialize members
        current_tasks = new ArrayList<>( );
        tasks = new ArrayList<>( );
        task_list = (ListView) this.findViewById( R.id.list );

        //Idk what this is but cell color changing doesn't work without it
        task_list.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );

        //gets data from file and updates array list
        update_list( );

        //update the list adapter
        update_adapter_data( );

        //set the adapter
        adapter = new SimpleAdapter( Tasks.this, tasks, android.R.layout.simple_list_item_2,
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
        task_list.setAdapter( adapter );

        //refresh list adapter on UI thread
        runOnUiThread( new Runnable( ) {
            @Override
            public void run( ) {
                if( adapter == null ) {
                    adapter = new SimpleAdapter( Tasks.this, tasks, android.R.layout.simple_list_item_2,
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
                else {
                    adapter.notifyDataSetChanged( );
                }
            }
        } );

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
                }
                else {
                    view.setBackgroundResource( android.R.color.transparent );
                    current_tasks.get( i ).selected = false;
                }
                update_adapter_data( );
                adapter.notifyDataSetChanged( );
            }
        } );

        //setting long click listener to edit text
        task_list.setLongClickable( true );
        task_list.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener( ) {
            @Override
            public boolean onItemLongClick( AdapterView<?> arg0, View view, final int pos, long id ) {
                show_edit_pop_up( pos );                         //show the pop up to edit text
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
                update_adapter_data( );                         //update adapter data
                adapter.notifyDataSetChanged( );                //refresh table
                update_storage( );                              //update internal storage
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
        pop_up.setMessage( "Enter your text below and press \"ADD\"" );

        //create edit text object
        final EditText input = new EditText( this );
        input.setHint( "task description" );
        pop_up.setView( input );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "ADD", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                final String text = input.getText( ).toString( );
                if( text.isEmpty( ) ) { //check for no text
                    Toast.makeText( getApplicationContext( ), "cannot add empty",
                            Toast.LENGTH_SHORT ).show( );
                    return; //leave so we don't take any info
                }
                if( text.contains( "\\" ) ) {
                    Toast.makeText( getApplicationContext( ),
                            "cannot use backslash in name",
                            Toast.LENGTH_SHORT ).show( );
                    return;
                }

                current_tasks.add( new Task( text, false ) );    //add new Task to tasks list
                update_adapter_data( );                          //update adapter data
                adapter.notifyDataSetChanged( );                 //refresh list
                update_storage( );                               //update internal storage
            }
        } );
        pop_up.setNegativeButton( "Cancel", null );
        final AlertDialog alert = pop_up.create( );
        //force the keyboard to come up
        alert.getWindow( ).setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );
        alert.setCanceledOnTouchOutside( false ); //prevent the user from "touching out" of the view

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
        pop_up.setMessage( "Edit your entry below and press \"SAVE\"" );

        //create edit text object and fill with
        //current task contents to be in dialog box
        final EditText input = new EditText( this );
        final int _pos = pos;
        input.setText( current_tasks.get( pos ).description );
        pop_up.setView( input );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "SAVE", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                final String text = input.getText( ).toString( );
                if( text.isEmpty( ) ) { //check for no text
                    Toast.makeText( getApplicationContext( ), "cannot add empty",
                            Toast.LENGTH_SHORT ).show( );
                    return; //leave so we don't take any info
                }
                if( text.contains( "\\" ) ) {
                    Toast.makeText( getApplicationContext( ),
                            "cannot use backslash in name",
                            Toast.LENGTH_SHORT ).show( );
                    return;
                }

                current_tasks.get( _pos ).description = text;    //update object list
                update_adapter_data( );                          //update adapter data
                adapter.notifyDataSetChanged( );                 //refresh list
                update_storage( );                               //update internal storage
            }
        } );
        pop_up.setNegativeButton( "CANCEL", null );
        final AlertDialog alert = pop_up.create( );
        //forces keyboard to come up
        alert.getWindow( ).setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );
        alert.setCanceledOnTouchOutside( false );  //prevents user from "touching out"  of the view

        //show pop-up
        alert.show( );
    }

    //returns an adapter object based on modified task data
    public void update_adapter_data( ) {
        //clear the List first
        tasks.clear( );
        size = current_tasks.size( );
        //repopulate the list
        for( int i = 0; i < size; ++i ) {
            //String key = "task_" + Integer.toString( i+1 );                           //the key for the ith object is 'task_i'
            //Task task = (Task)getIntent( ).getSerializableExtra( key );               //save the serialized task object
            //current_tasks.add( task );                                                //add this new object to the current cars in the vie
            Map<String, String> task_data = new HashMap<>(2);                           //this map will hold one item to be displayed on the list
            String title = current_tasks.get( i ).description;                          //build the title
            String subtitle =  current_tasks.get( i ).selected ? "Completed" : "Pending";   //build subtitle
            task_data.put( "title", title );                                            //put these in the map
            task_data.put( "subtitle", subtitle );
            tasks.add( task_data );                                                     //add map to the item (map) list
        }
    }

    //this method will be set up as the click listener
    //for all the buttons on this view
    public void button_click_listener( View view ) {
        if( view == this.findViewById( R.id.ca_button ) ) { //if clear all is pressed
            if( current_tasks.isEmpty( ) ) return;                    //does nothing for an empty list
            Toast.makeText( getApplicationContext( ), "hold down to clear all", Toast.LENGTH_SHORT ).show( );
        }
        else if( view == this.findViewById( R.id.clear_button ) ) {   //if clear is pressed
            for( int i = 0; i < current_tasks.size( ); ++i ) {        //remove the selected task objects
                if( current_tasks.get( i ).selected ) {
                    current_tasks.remove( i );
                    --i;                                              //move i back one to compensate for removal of item
                }
            }
            update_adapter_data( );
            adapter.notifyDataSetChanged( );                          //refresh list
            update_storage( );                                        //update internal storage
        }
        else if( view == this.findViewById( R.id.add_button ) ) {   //if add is pressed
            show_add_pop_up( );                                     //show the add pop-up
        }
    }

    //gets data from "shared preferences"
    //and updates the array list
    public void update_list( ) {
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        size = sp_file.getInt( "size", 0 );                                     //get size from the file
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
            final String data = sp_file.getString( key, "" );
            final String text = extract_text( data );
            current_tasks.add( new Task( text, extract_selected( data ) ) );      //add data to array list
        }
    }

    //extracts the selected boolean from
    //a Task object metadata in the form
    //of a string
    public boolean extract_selected( String data ) {
        return ( data.charAt( data.length( )-1 ) == '1' );
    }

    //extracts the description String from
    //a Task object metadata in the form
    //of a string
    public String extract_text( String data ) {
        StringBuilder builder = new StringBuilder( );

        for( int i = 0; i < data.length( ); ++i ) {
            if( data.charAt( i ) == '\\' ) return builder.toString( );
            builder.append( data.charAt( i ) );
        }
        return builder.toString( );
    }

    //updates the persistent data based
    //on the current array list data
    public void update_storage( ) {
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        SharedPreferences.Editor editor = sp_file.edit( );                      //make editor object

        for( int i = 0; i < size; ++i ) {                                       //remove only task objects
            final String key = "task_" + Integer.toString( i );
            editor.remove( key );
            editor.apply( );                                                    //apply changes
        }

        editor.putInt( "size", current_tasks.size( ) );                         //write new size
        editor.apply( );
        size = current_tasks.size( );                                           //update size

        for( int i = 0; i < current_tasks.size( ); ++i ) {                      //loop through array list
            String key = "task_" + Integer.toString( i );                       //create key
            final String value = current_tasks.get( i ).description +
                    "\\" + ( current_tasks.get( i ).selected ? "1" : "0" );
            editor.putString( key, value );        //write to storage
            editor.apply( );
        }
    }

    //overriding method that gets called
    //when the back button is pressed
    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
        update_storage( );
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        finishAffinity( );
    }

    //shows a pop-up with some help information
    public void show_help_pop( View view ) {
        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Tasks Help" );
        pop_up.setIcon( R.drawable.logo );
        final String help_message = "This section can be used for your tasks, things " +
                "that you need to do at some point. You can add a new task by clicking" +
                " \"ADD TASK\", which will trigger a dialog box asking you to enter" +
                " text for the new task. This text can be anything (including emojis) " +
                "and may contains dates, or anything else that you please. When a task" +
                " is completed, it can be highlighted by a single click, and clicking " +
                "\"CLEAR\" will remove all the highlighted tasks. All tasks can be " +
                "cleared at once by holding down \"CLR ALL\". To edit the text of any" +
                " task, just hold down on that entry and a dialog box will pop up with" +
                " editable text. Click \"SAVE\" to save changes, or \"CANCEL\" to abort.";
        pop_up.setMessage( help_message );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", null );

        //show pop-up
        pop_up.show( );
    }

    @Override
    public void onStop( ) {
        super.onStop( );
        update_storage( );
    }
}
