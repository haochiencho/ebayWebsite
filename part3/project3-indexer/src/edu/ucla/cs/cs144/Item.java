package edu.ucla.cs.cs144;

public class Item {
    
    public Item() {
    }

    public Item(String id, 
                 String name, 
                 String category, 
                 String description) {
        this.itemID = id;     
        this.name = name;   
        this.category = category;
        this.description = description; 
    }
    

    private String name;
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String itemID;
    public String getItemID() {
        return this.itemID;
    }
    public void setItemID(String id) {
        this.itemID = id;
    }

    private String description;
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private String category;
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }


}

