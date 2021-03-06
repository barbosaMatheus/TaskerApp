package com.example.matheus.taskbar;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        getSupportActionBar( ).setTitle( "More" );
        getSupportActionBar( ).setDisplayHomeAsUpEnabled( true );
    }

    public void show_pop_up( View view ) {
        String title = "", message = "";

        if( view == this.findViewById( R.id.ads ) ) { //if why ads button is pressed
            title = "Why do I See Ads?";
            message = "This application is provided to any and all Android users, at no cost whatsoever." +
                    " In short, to make up for the time spent building and (still) maintaining this app," +
                    " it has ads. However, we know that ads are annoying, so we have put them in this" +
                    " page where you don't always have to see them in your daily use. Please keep in mind" +
                    " that clicking on the ads (or even visiting this page of the app) does help out, even" +
                    " if you do not purchase/download what is being advertised. Thanks for understanding!";
        }
        else if( view == this.findViewById( R.id.contribute ) ) { //if contribute is pressed
            title = "Contribute";
            message = "First of all, a million thanks for your interest in contributing. There are" +
                    " three ways to contribute to the development of this application:" +
                    "\n\n1. Bug Reports: if you find a bug/error, please e-mail a bug report to asaph@ok" +
                    "state.edu. Please include a brief description of the bug/error and if you can repro" +
                    "duce it please describe how to do so. Bug reports help improve the overall user " +
                    "experience and app quality." +
                    "\n\n2. Programming: if you are a programmer, head over to github.com/barbosaMatheus/Tasker" +
                    " and feel free to download our source code. You should also e-mail asaph@okstate.edu " +
                    "so we know that you are interested in contributing. From then on, you may get e-mails" +
                    " with bug reports, and you can choose to help fix it if/when you want." +
                    "\n\n3. Ads: if you would like to contribute but are on a time budget, one good way " +
                    " to do it is to visit the \"MORE\" page every once in a while. By visiting the page" +
                    " , or even clicking on the ad (if you are feeling extra generous) you are generating" +
                    " a very tiny but still appreciated amount of ad revenue. All sd revenues go towards " +
                    "paying for programming hours spent on building and maintenance." +
                    "\n\nIf you really feel that for some reason, using your own money is the best way " +
                    "for you to contribute, you can send an e-mail to asaph@okstate.edu about it. You will " +
                    "be given a a P.O. Box address to which you can send a contribution directly.";
        }
        else if( view == this.findViewById( R.id.tips ) ) { //if usage tips is clicked
            title = "Usage Tips";
            message = "This section describes how to use this application daily and productively." +
                    " It may be updated from time to time as new features are added.\n\n- Check " +
                    "your lists multiple times per day.\n\n- Separate your tasks project by project." +
                    " Do this by using the customizable lists in Home. Simply long click on one of" +
                    " the buttons and a pop-up will allow you to change its name.\n\n- Use emojis in" +
                    " your task descriptions to help your brain make connections. I have five or " +
                    "six different emojis that mean specific things so one quick glance at the " +
                    "screen can tell me a lot of what's on the list without actually reading it." +
                    "\n\n- Use the reminder alarm throughout the day, but use your phone's built-" +
                    "in alarm app to wake up in the morning. I often use the reminders to help " +
                    "myself leave for work on time.\n\n- Use the shopping list, and use the \'price" +
                    " add\' feature to keep yourself on budget. To use this feature simply click " +
                    "on the items you have picked up in order to highlight them. Instead of " +
                    " pressing \'CLEAR\' to clear the items off the list, long press on \'ADD ITEM\'" +
                    " and the app will add up the total price of the highlighted items then show" +
                    " it on the screen momentarily.\n\n- Use highlight to mark tasks/items that" +
                    " you are currently working on, or should work on next.\n\n- If for some reason" +
                    " you need to reinstall the app, or are switching phones, take screenshots of " +
                    "your lists and you can easily replicate them on a fresh install of the app.";
        }
        else if( view == this.findViewById( R.id.credits ) ) { //if credits is clicked
            title = "Credits";
            message = "Many thanks to all that have made this possible!\n\nDevelopers/Testers\n-> "
            + "Matheus Barbosa (main developer/creator)\n-> Ryan McIver (alpha tester, contributor)"
            + "\n\nImages\n-> pixabay.com (background)\n-> flamingtext.com (logo & graphical name)";
        }
        else if( view == this.findViewById( R.id.features ) ) { //if new features is clicked
            title = "New Features";
            message = "This page will be periodically updated to show the last five new features " +
                    "which have been implemented into Tasker, from newest to oldest (1-5).\n\n\n1. " +
                    "Change custom list names (on the left side of the screen in Home) by long-pr" +
                    "essing on the button.\n\n2. Highlighted list items will stay highlighted even " +
                    "if the app closes and restarts.\n\n3. Any text entered in the field above the " +
                    "reminder \"ON\" button, will be in the notification when the reminder goe" +
                    "s off.\n\n4. When \"ADD ITEM\" (in shopping list) is long-pressed, the price o" +
                    "f all highlighted items will appear on the screen momentarily.\n\n5. You now h" +
                    "ave the ability to add a price to any item in your shopping list.";
        }

        //create and set up dialog box object
        AlertDialog.Builder pop_up = new AlertDialog.Builder( this );
        pop_up.setTitle( title );
        pop_up.setIcon( R.drawable.logo );
        pop_up.setMessage( message );

        //set up buttons for dialog box
        pop_up.setPositiveButton( "OK", null );

        //show pop-up
        pop_up.show( );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch( item.getItemId( ) ) {
            case android.R.id.home:
                super.onBackPressed( );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }
}
