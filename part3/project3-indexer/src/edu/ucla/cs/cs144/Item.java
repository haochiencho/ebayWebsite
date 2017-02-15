package edu.ucla.cs.cs144;

public class Item {
    
    public Item() {}

    public Item(String id, 
                 String name, 
                 String category, 
                 String description) {
        this.itemID = id;     
        this.name = name;   
        this.category = category;
        this.description = description; 
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemID() {
        return this.itemID;
    }

    public void setItemID(String id) {
        this.itemID = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String name;
    private String itemID;
    private String description;
    private String category;
}

