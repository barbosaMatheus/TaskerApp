package com.example.matheus.taskbar;

import java.io.Serializable;

/**
 * Created by matheus on 12/25/16.
 */

public class Task implements Serializable {

    String description; //description of task
    boolean selected; //true if the task is selected in the listview

    public Task( String description, boolean selected ) { //constructor
        this.description = description;
        this.selected = selected;
    }
}
