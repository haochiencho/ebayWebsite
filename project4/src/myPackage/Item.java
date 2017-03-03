package myPackage;

import myPackage.Bid;
import java.util.*;

public class Item {
    public Item() {} 
	//TODO: make member variables private, and use getters/setters in itemServlet+itemDataParser
    //TODO: handle nested items like category, bid, location, seller 
    public String itemID;
    public String name;
    public List<String> categories = new ArrayList<String>();
    public String currently;
    public String buy_price;
    public String first_bid;
    public String number_of_bids;
    public List<Bid> bids = new ArrayList<Bid>();
    public String location;
	public String latitude;
	public String longitude;
    public String country;
    public String started;
    public String ends;
    public String sellerID;
	public String sellerRating; 
    public String description;

    
	public String getItemID() {
		return itemID;
	}
	
	public void setItemID(String itemId) {
		this.itemID = itemId;
	}

    public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

    //GetCategory
    //SetCategory

    public String getCurrently() {
		return currently;
	}
	
	public void setCurrently(String currently) {
		this.currently = currently;
	}

    public String getBuyPrice() {
		return buy_price;
	}
	
	public void setBuyPrice(String buy_price) {
		this.buy_price = buy_price;
	}

    public String getFirstBid() {
		return first_bid;
	}
	
	public void setFirstBid(String first_bid) {
		this.first_bid = first_bid;
	}

    public String getNumberOfBids() {
		return number_of_bids;
	}
	
	public void setNumberOfBids(String number_of_bids) {
		this.number_of_bids = number_of_bids;
	}

    //Bid

    //Location

    public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

    public String getStarted() {
		return started;
	}
	
	public void setStarted(String started) {
		this.started = started;
	}

    public String getEnds() {
		return ends;
	}
	
	public void setEnds(String ends) {
		this.ends = ends;
	}
 
    //Seller
    public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

    
}