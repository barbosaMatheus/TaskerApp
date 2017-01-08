package com.example.matheus.taskbar;

import java.io.Serializable;

public class Item implements Serializable {
    String description; //description of the item
    boolean completed;  //true if the item has been bought
    boolean selected;   //true if the item is selected in the listview
    int count;          //how many of this item are listed
    double price;       //price for this item

    public Item( String description/*, int count*/ ) { //constructor
        this.description = description;
        this.completed = false;
        this.selected = false;
        this.count = 0;
        this.price = 0.0;
    }

    public Item( String description, double price/*, int count*/ ) { //constructor
        this.description = description;
        this.completed = false;
        this.selected = false;
        this.count = 0;
        this.price = price;
    }
}
