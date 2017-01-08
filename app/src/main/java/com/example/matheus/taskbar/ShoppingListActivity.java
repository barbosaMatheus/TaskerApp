package com.example.matheus.taskbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.renderscript.Double2;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    public int size;                                //number of items saved
    public double highlighted_total;                //total price for highlighted items

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        getSupportActionBar( ).setTitle( "Shopping List" );

        //initialize members
        current_items = new ArrayList<>( );
        items = new ArrayList<>( );
        item_list = (ListView) this.findViewById( R.id.list2 );
        highlighted_total = 0.0;

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
                if( !( current_items.get( i ).selected ) ) { //if being highlighted
                    view.setBackgroundResource( R.color.selected_row );
                    current_items.get( i ).selected = true;
                    current_items.get( i ).completed = true;
                    highlighted_total += current_items.get( i ).price;
                }
                else { //if being lowlighted
                    view.setBackgroundResource( android.R.color.transparent );
                    current_items.get( i ).selected = false;
                    current_items.get( i ).completed = false;
                    highlighted_total -= current_items.get( i ).price;
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
        clear_all.setLongClickable( true );
        clear_all.setOnLongClickListener( new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick( View view ) {
                current_items.clear( );                         //clear all contents
                item_list.setAdapter( get_list_adapter( ) );    //make new adapter
                update_storage( );                              //update internal storage
                item_list.refreshDrawableState( );              //redraw list
                return true;
            }
        } );

        //long click listener for add button
        Button add = ( Button ) this.findViewById( R.id.add_button2 );
        add.setLongClickable( true );
        add.setOnLongClickListener( new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick( View view ) {
                if( highlighted_total > 0.0 ) {
                    Toast.makeText( getApplicationContext(),
                            "highlighted total: " + Double.toString( highlighted_total ),
                            Toast.LENGTH_SHORT ).show( );
                }
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
            final String data = sp_file.getString( key, "" );
            final String text = extract_text( data );
            final double price = extract_price( data );
            current_items.add( new Item( text, price ) );      //add data to array list
        }
    }

    public String extract_text( String data ) {
        StringBuilder builder = new StringBuilder( );
        for( int i = 0; i < data.length( ); ++i ) {
            if( data.charAt( i ) == '\\' ) {
                return builder.toString( );
            }
            builder.append( data.charAt( i ) );
        }
        return builder.toString( );
    }

    public double extract_price( String data ) {
        int i = 0;
        while( ( i < data.length( ) ) && ( data.charAt( i ) != '\\' ) )  i += 1;
        i += 1;
        StringBuilder builder = new StringBuilder( );
        while( i < data.length( ) ) {
            builder.append( data.charAt( i ) );
            i += 1;
        }
        if( builder.length( ) >= 3 ) return Double.valueOf( builder.toString( ) );
        return 0.0;
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
            String title = current_items.get( i ).description + "\t\t($"
                    + Double.toString( current_items.get( i ).price ) + ")";            //build the title
            String subtitle =  current_items.get( i ).completed ? "Picked Up" : "Needed";   //build subtitle
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
        pop_up.setMessage( "Edit your entry below and press \"SAVE\"" );

        //create edit text object and fill with
        //current item contents to be in dialog box
        final EditText input = new EditText( this );
        final EditText price = new EditText( this );
        final LinearLayout layout = new LinearLayout( this );
        final int _pos = pos;
        input.setText( current_items.get( pos ).description );
        price.setInputType( InputType.TYPE_NUMBER_FLAG_DECIMAL );
        price.setRawInputType( Configuration.KEYBOARD_12KEY );
        price.setText( Double.toString( current_items.get( pos ).price ) );
        layout.setOrientation( LinearLayout.VERTICAL );
        layout.addView( input );
        layout.addView( price );
        pop_up.setView( layout );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "SAVE", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                String text = input.getText( ).toString( );
                final String price_as_text = price.getText( ).toString( );
                Double item_price = current_items.get( _pos ).price;
                if( text.isEmpty( ) ) { //check for no text
                    text = current_items.get( _pos ).description;
                }
                if( text.contains( "\\" ) ) {
                    text = current_items.get( _pos ).description;
                    Toast.makeText( getApplicationContext( ),
                            "cannot use backslash in name",
                            Toast.LENGTH_SHORT ).show( );
                }
                if( !price_as_text.isEmpty( ) ) {
                    item_price = Double.valueOf( price_as_text );
                }

                current_items.get( _pos ).description = text;
                current_items.get( _pos ).price = item_price;
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
            final String value = current_items.get( i ).description +
                    "\\" + Double.toString( current_items.get( i ).price );
            editor.putString( key, value );                                     //write to storage
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
        pop_up.setMessage( "Enter your text below and press \"ADD\"" );

        //create edit text object
        final LinearLayout layout = new LinearLayout( this );
        final EditText input = new EditText( this );
        final EditText price = new EditText( this );
        input.setHint( "item name" );
        price.setInputType( InputType.TYPE_NUMBER_FLAG_DECIMAL );
        price.setRawInputType( Configuration.KEYBOARD_12KEY );
        price.setHint( "price" );
        layout.setOrientation( LinearLayout.VERTICAL );
        layout.addView( input );
        layout.addView( price );
        pop_up.setView( layout );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "ADD", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                final String text = input.getText( ).toString( );
                final String price_as_string = price.getText( ).toString( );
                double item_price = 0.0;
                if( text.isEmpty( ) ) { //check for no text
                    Toast.makeText( getApplicationContext( ), "cannot add empty",
                            Toast.LENGTH_SHORT ).show( );
                    return; //leave so we don't take any info
                }
                if( text.contains( "\\" ) ) { //check for back slashes
                    Toast.makeText( getApplicationContext( ), "cannot use backslash in name",
                            Toast.LENGTH_SHORT ).show( );
                    return; //leave so we don't take any info
                }
                if( !price_as_string.isEmpty( ) ) {
                    item_price = Double.valueOf( price_as_string );
                }

                current_items.add( new Item( text, item_price ) );
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
    //when the back button is pressed
    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
        update_storage( );
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        finish( );
    }

    //shows a pop-up with some help information
    public void show_help_pop( View view ) {
        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( "Shopping List Help" );
        pop_up.setIcon( R.drawable.logo );
        final String help_message = "This section can be used to list things that you " +
                "need to buy at some point, groceries or otherwise. To add an item, click" +
                " \"ADD ITEM\", which will trigger a dialog box asking for you to enter" +
                " text for the new item. This text can be anything (including emojis) " +
                "or anything else that you please. When an item has been picked up (" +
                "bought), it can be highlighted by a single click, and clicking " +
                "\"CLEAR\" will remove all the highlighted items. However, holding" +
                " down on \"ADD ITEM\" will add up the price of all highlighted items" +
                " and flash it at the bottom of the screen. All items can be cleared " +
                "at once by holding down \"CLR ALL\". To edit the text of any item, " +
                "just hold down on that entry and a dialog box will pop up with editable" +
                " text. Click \"SAVE\" to save changes, or \"CANCEL\" to abort.";
        pop_up.setMessage( help_message );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", null );

        //show pop-up
        pop_up.show( );
    }
}