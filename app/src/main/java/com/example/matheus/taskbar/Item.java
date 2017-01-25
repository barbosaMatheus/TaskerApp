package com.example.matheus.taskbar;

import java.io.Serializable;

public class Item implements Serializable {
    String description; //description of the item
    boolean selected;   //true if the item is selected in the listview
    int count;          //how many of this item are listed
    int price;          //price for this item x100 as an int

    public Item( String description, int price, boolean selected/*, int count*/ ) { //constructor
        this.description = description;
        this.selected = selected;
        this.count = 0;
        this.price = price;
    }
}
