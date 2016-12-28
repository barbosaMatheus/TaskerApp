package com.example.matheus.taskbar;

import java.io.Serializable;

/**
 * Created by matheus on 12/25/16.
 */

public class Task implements Serializable {

    String description; //description of task
    boolean completed; //true if the task is completed
    boolean selected; //true if the task is selected in the listview

    public Task( String description ) { //constructor
        this.description = description;
        this.completed = false;
        this.selected = false;
    }
}
