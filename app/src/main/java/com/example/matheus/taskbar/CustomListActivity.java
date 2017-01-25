package com.example.matheus.taskbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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

public class CustomListActivity extends AppCompatActivity {

    public ListView list;                  //list view object
    public ArrayList<Task> object_list;   //list of objects for this task
    public SimpleAdapter adapter;          //list adapter object
    public List<Map<String, String>> data; //holds list metadata
    public int custom_index;               //index for which custom list this is
    public int size;                       //number of tasks for this list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        //change action bar title according to button title
        String title = getIntent( ).getStringExtra( "title" );
        getSupportActionBar( ).setTitle( title );
        getSupportActionBar( ).setDisplayHomeAsUpEnabled( true );

        //initialize member variables
        custom_index = getIntent( ).getIntExtra( "index", 0 );
        list = ( ListView ) this.findViewById( R.id.custom_list );
        object_list = new ArrayList<>( );
        data = new ArrayList<>( );

        //Idk what this is but cell color changing doesn't work without it
        list.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );

        //get data from file and update array list
        //then update adapter data
        update_list( );
        update_adapter_data( );

        //create and set adapter
        adapter = new SimpleAdapter( CustomListActivity.this, data, android.R.layout.simple_list_item_2,
                new String[] { "title", "subtitle" },
                new int[] { android.R.id.text1, android.R.id.text2 } ) {
            @Override //override getView method so we can paint the cell before it goes out
            public View getView( int pos, View convertView, ViewGroup parent ) {
                View view = super.getView( pos, convertView, parent );
                if( object_list.get( pos ).selected ) {                       //if this item was selected before, paint it green
                    view.setBackgroundResource( R.color.selected_row );
                }
                else {                                                         //else just leave it transparent
                    view.setBackgroundResource( android.R.color.transparent );
                }
                return view;                                                   //return a new list item (view)
            }

            @Override //override the getCount method so we don't crash on an empty list
            public int getCount( ) {
                return object_list.size( );
            }
        };
        list.setAdapter( adapter );

        //refresh list adapter on UI thread
        runOnUiThread( new Runnable( ) {
            @Override
            public void run( ) {
                if( adapter == null ) {
                    adapter = new SimpleAdapter( CustomListActivity.this, data, android.R.layout.simple_list_item_2,
                            new String[] { "title", "subtitle" },
                            new int[] { android.R.id.text1, android.R.id.text2 } ) {
                        @Override //override getView method so we can paint the cell before it goes out
                        public View getView( int pos, View convertView, ViewGroup parent ) {
                            View view = super.getView( pos, convertView, parent );
                            if( object_list.get( pos ).selected ) {                       //if this item was selected before, paint it green
                                view.setBackgroundResource( R.color.selected_row );
                            }
                            else {                                                         //else just leave it transparent
                                view.setBackgroundResource( android.R.color.transparent );
                            }
                            return view;                                                   //return a new list item (view)
                        }

                        @Override //override the getCount method so we don't crash on an empty list
                        public int getCount( ) {
                            return object_list.size( );
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
        list.setOnItemClickListener( new AdapterView.OnItemClickListener( ) {
            //this method toggles the color and selection status of
            //each item on the list view and each car in the array
            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
                if( !( object_list.get( i ).selected ) ) {
                    view.setBackgroundResource( R.color.selected_row );
                    object_list.get( i ).selected = true;
                }
                else {
                    view.setBackgroundResource( android.R.color.transparent );
                    object_list.get( i ).selected = false;
                }
                update_adapter_data( );
                adapter.notifyDataSetChanged( );
            }
        } );

        //setting long click listener to edit text
        list.setLongClickable( true );
        list.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener( ) {
            @Override
            public boolean onItemLongClick( AdapterView<?> arg0, View view, final int pos, long id ) {
                show_edit_pop_up( pos );                         //show the pop up to edit text
                return true;                                     //return true for some reason
            }
        } );

        //setting up long click listener
        //for the clear all button
        Button clear_all = ( Button ) this.findViewById( R.id.ca_button3 );
        clear_all.setOnLongClickListener( new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick(View view) {
                object_list.clear( );                         //clear all contents
                update_adapter_data( );                         //update adapter data
                adapter.notifyDataSetChanged( );                //refresh table
                update_storage( );                              //update internal storage
                return true;
            }
        } );
    }

    //gets data from "shared preferences"
    //and updates the array list
    public void update_list( ) {
        SharedPreferences g_file = PreferenceManager.getDefaultSharedPreferences( getBaseContext( ) );     //make shared preferences object
        final String size_key = "size" + Integer.toString( custom_index + 2 );
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );
        size = g_file.getInt( size_key, 0 );                                    //get size from the file
        if( size < 1 ) {                                                        //if empty we make a new field called size
            SharedPreferences.Editor g_editor = g_file.edit( );
            g_editor.putInt( size_key, 0 );
            g_editor.apply( );
            return;
        }

        //if we have some data then we can update the array list
        object_list.clear( );                                                 //clear array list
        for( int i = 0; i < size; ++i ) {                                     //loop through the file
            String key = "custom" + Integer.toString( custom_index )
                    + "_" + Integer.toString( i );
            final String data = sp_file.getString( key, "" );
            final String text = extract_text( data );
            object_list.add( new Task( text, extract_selected( data ) ) );      //add data to array list
        }
    }

    //returns an adapter object based on modified task data
    public void update_adapter_data( ) {
        //clear the List first
        data.clear( );
        size = object_list.size( );
        //repopulate the list
        for( int i = 0; i < size; ++i ) {
            //String key = "task_" + Integer.toString( i+1 );                           //the key for the ith object is 'task_i'
            //Task task = (Task)getIntent( ).getSerializableExtra( key );               //save the serialized task object
            //current_tasks.add( task );                                                //add this new object to the current cars in the vie
            Map<String, String> task_data = new HashMap<>(2);                           //this map will hold one item to be displayed on the list
            String title = object_list.get( i ).description;                            //build the title
            String subtitle =  object_list.get( i ).selected ? "Completed" : "Pending"; //build subtitle
            task_data.put( "title", title );                                            //put these in the map
            task_data.put( "subtitle", subtitle );
            data.add( task_data );                                                      //add map to the item (map) list
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

    //shows pop up to add new item
    //to the list
    public void show_add_pop_up( View view ) {
        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Add Entry" );
        pop_up.setIcon( R.drawable.logo );
        pop_up.setMessage( "Enter your text below and press \"ADD\"" );

        //create edit text object
        final EditText input = new EditText( this );
        input.setHint( "item description" );
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

                object_list.add( new Task( text, false ) );      //add new Task to objects list
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

    //updates the persistent data based
    //on the current array list data
    public void update_storage( ) {
        SharedPreferences g_file = PreferenceManager.getDefaultSharedPreferences( getBaseContext( ) );
        SharedPreferences.Editor g_editor = g_file.edit( );
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        SharedPreferences.Editor editor = sp_file.edit( );                      //make editor object

        for( int i = 0; i < size; ++i ) {                                       //remove only task objects
            final String key = "custom" + Integer.toString( custom_index )
                + "_" + Integer.toString( i );
            editor.remove( key );
            editor.apply( );                                                    //apply changes
        }

        final String size_key = "size" + Integer.toString( custom_index + 2 );
        g_editor.putInt( size_key, object_list.size( ) );                         //write new size
        g_editor.apply( );
        size = object_list.size( );                                           //update size

        for( int i = 0; i < size; ++i ) {                      //loop through array list
            final String key = "custom" + Integer.toString( custom_index )
                    + "_" + Integer.toString( i );
            final String value = object_list.get( i ).description +
                    "\\" + ( object_list.get( i ).selected ? "1" : "0" );
            editor.putString( key, value );        //write to storage
            editor.apply( );
        }
    }

    public void custom_button_click_listener( View view ) {
        if( view == this.findViewById( R.id.ca_button3 ) ) { //if clear all is pressed
            if( object_list.isEmpty( ) ) return;                    //does nothing for an empty list
            Toast.makeText( getApplicationContext( ), "hold down to clear all", Toast.LENGTH_SHORT ).show( );
        }
        else if( view == this.findViewById( R.id.clear_button3 ) ) {   //if clear is pressed
            for( int i = 0; i < object_list.size( ); ++i ) {        //remove the selected task objects
                if( object_list.get( i ).selected ) {
                    object_list.remove( i );
                    --i;                                              //move i back one to compensate for removal of item
                }
            }
            update_adapter_data( );
            adapter.notifyDataSetChanged( );                          //refresh list
            update_storage( );                                        //update internal storage
        }
    }

    //shows pop up on long press so the
    //user can edit item contents
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
        input.setText( object_list.get( pos ).description );
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

                object_list.get( _pos ).description = text;    //update object list
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

    @Override
    public void onStop( ) {
        super.onStop( );
        update_storage( );
    }

    @Override
    public void onBackPressed( )  {
        update_storage( );
        super.onBackPressed( );
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
