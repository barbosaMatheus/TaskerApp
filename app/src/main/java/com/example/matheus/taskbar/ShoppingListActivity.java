package com.example.matheus.taskbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ShoppingListActivity extends AppCompatActivity {

    public ArrayList<Item> current_items;           //holds item objects
    public List<Map<String, String>> items;         //array list to hold info for ListView
    ListView item_list;                             //ListView instance
    public int alarms_set;                          //number of alarms we have set
    public int size;                                //number of items saved

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        //initialize members
        current_items = new ArrayList<>( );
        items = new ArrayList<>( );
        item_list = (ListView) this.findViewById( R.id.list2 );

        //Idk what this is but cell color changing doesn't work without it
        item_list.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );

        //gets data from file and updates array list
        update_list( );

        //set adapter for the list
        item_list.setAdapter( get_list_adapter( ) );

        //overrides the onItemClick method and sets it as
        //the new click listener for list items
        item_list.setOnItemClickListener( new AdapterView.OnItemClickListener( ) {
            //this method toggles the color and selection status of
            //each item on the list view and each car in the array
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l ) {
                if( !( current_items.get( i ).selected ) ) {
                    view.setBackgroundResource( R.color.selected_row );
                    current_items.get( i ).selected = true;
                    current_items.get( i ).completed = true;
                }
                else {
                    view.setBackgroundResource( android.R.color.transparent );
                    current_items.get( i ).selected = false;
                    current_items.get( i ).completed = false;
                }

                item_list.setAdapter( get_list_adapter( ) );
                item_list.refreshDrawableState( );
            }
        } );

        //setting long click listener to edit text
        item_list.setLongClickable( true );
        item_list.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener( ) {
            @Override
            public boolean onItemLongClick( AdapterView<?> arg0, View view, final int pos, long id ) {
                show_edit_pop_up( pos );                         //show the pop up to edit text
                item_list.refreshDrawableState( );               //redraw list
                return true;                                     //return true for some reason
            }
        } );

        //setting up long click listener
        //for the clear all button
        Button clear_all = ( Button ) this.findViewById( R.id.ca_button2 );
        clear_all.setOnLongClickListener( new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick(View view) {
                current_items.clear( );                         //clear all contents
                item_list.setAdapter( get_list_adapter( ) );    //make new adapter
                update_storage( );                              //update internal storage
                item_list.refreshDrawableState( );              //redraw list
                return true;
            }
        } );
    }

    //gets data from "shared preferences"
    //and updates the array list
    public void update_list( ) {
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        size = sp_file.getInt( "size2", 0 );                                //get size from the file
        if( size < 1 ) {                                                        //if empty we make a new field called size
            SharedPreferences.Editor editor = sp_file.edit( );
            editor.putInt( "size2", 0 );
            editor.apply( );
            return;
        }

        //if we have some data then we can update the array list
        current_items.clear( );                                                 //clear array list
        for( int i = 0; i < size; ++i ) {                                       //loop through the file
            String key = "item_" + Integer.toString( i );                       //create key
            current_items.add( new Item( sp_file.getString( key, "" ) ) );      //add data to array list
        }
    }

    //returns an adapter object based on modified task data
    public SimpleAdapter get_list_adapter( ) {
        //clear the List first
        items.clear( );
        size = current_items.size( );
        //repopulate the list
        for( int i = 0; i < current_items.size( ); ++i ) {
            //String key = "item_" + Integer.toString( i+1 );                           //the key for the ith object is 'item_i'
            //Item item = (Task)getIntent( ).getSerializableExtra( key );               //save the serialized task object
            //current_items.add( item );                                                //add this new object to the current cars in the vie
            Map<String, String> task_data = new HashMap<>(2);                           //this map will hold one item to be displayed on the list
            String title = current_items.get( i ).description;                          //build the title
            String subtitle =  current_items.get( i ).completed ? "Completed" : "Pending";   //build subtitle
            task_data.put( "title", title );                                            //put these in the map
            task_data.put( "subtitle", subtitle );
            items.add( task_data );                                                     //add map to the item (map) list
        }

        //then create, set up and return new list adapter
        return new SimpleAdapter( this, items, android.R.layout.simple_list_item_2,
                new String[] { "title", "subtitle" },
                new int[] { android.R.id.text1, android.R.id.text2 } ) {
            @Override //override getView method so we can paint the cell before it goes out
            public View getView( int pos, View convertView, ViewGroup parent ) {
                View view = super.getView( pos, convertView, parent );
                if( current_items.get( pos ).selected ) {                       //if this item was selected before, paint it green
                    view.setBackgroundResource( R.color.selected_row );
                }
                else {                                                         //else just leave it transparent
                    view.setBackgroundResource( android.R.color.transparent );
                }
                return view;                                                   //return a new list item (view)
            }

            @Override //override the getCount method so we don't crash on an empty list
            public int getCount( ) {
                return current_items.size( );
            }
        };
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
        //current item contents to be in dialog box
        final EditText input = new EditText( this );
        final int _pos = pos;
        input.setText( current_items.get( pos ).description );
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

                current_items.get( _pos ).description = text;
                item_list.setAdapter( get_list_adapter( ) );     //get a new adapter
                update_storage( );                               //update internal storage
            }
        } );
        pop_up.setNegativeButton( "Cancel", null );
        final AlertDialog alert = pop_up.create( );
        //forces keyboard to come up
        alert.getWindow( ).setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );
        alert.setCanceledOnTouchOutside( false );  //prevents user from "touching out"  of the view

        //show pop-up
        alert.show( );
    }

    //updates the persistent data based
    //on the current array list data
    public void update_storage( ) {
        SharedPreferences sp_file = getPreferences( Context.MODE_PRIVATE );     //make shared preferences object
        SharedPreferences.Editor editor = sp_file.edit( );                      //make editor object

        for( int i = 0; i < size; ++i ) {                                       //remove only item objects
            final String key = "item_" + Integer.toString( i );
            editor.remove( key );
            editor.apply( );                                                    //apply changes
        }

        editor.putInt( "size2", current_items.size( ) );                        //write new size
        editor.apply( );
        size = current_items.size( );                                           //update size

        for( int i = 0; i < current_items.size( ); ++i ) {                      //loop through array list
            String key = "item_" + Integer.toString( i );                       //create key
            editor.putString( key, current_items.get( i ).description );        //write to storage
            editor.apply( );
        }
    }

    //this method will be set up as the click listener
    //for all the buttons on this view
    public void button_click_listener2( View view ) {
        if( view == this.findViewById( R.id.ca_button2 ) ) { //if clear all is pressed
            if( current_items.isEmpty( ) ) return;                     //does nothing for an empty list
            Toast.makeText( getApplicationContext( ), "hold down to clear all", Toast.LENGTH_SHORT ).show( );
        }
        else if( view == this.findViewById( R.id.clear_button2 ) ) {  //if clear is pressed
            for( int i = 0; i < current_items.size( ); ++i ) {        //remove the selected task objects
                if( current_items.get( i ).completed ) {
                    current_items.remove( i );
                    --i;                                              //move i back one to compensate for removal of item
                }
            }
            item_list.setAdapter( get_list_adapter( ) );              //set new adapter
            item_list.refreshDrawableState( );                        //redraw list
            update_storage( );                                        //update internal storage
        }
        else if( view == this.findViewById( R.id.add_button2 ) ) {   //if add is pressed
            /*Intent intent = new Intent( this, AddTask.class );      //create intent object
            intent.putExtra( "size", current_tasks.size( ) );       //send size of array list with intent
            for( int i = 0; i < current_tasks.size( ); ++i ) {      //loop through array list and but objects in intent
                String key = "task_" + Integer.toString( i );       //create key
                intent.putExtra( key, current_tasks.get( i ) );     //add object to intent
            }
            startActivityForResult( intent, 1 );                    //start Add task activity for result with id "1"*/
            show_add_pop_up( );                                     //show the add pop-up
            item_list.refreshDrawableState( );                      //redraw list
        }
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

                current_items.add( new Item( text ) );
                item_list.setAdapter( get_list_adapter( ) );     //get a new adapter
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

    //overriding method that gets called
    //when the home button is pressed
    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
        update_storage( );
        finish( );
    }
}
