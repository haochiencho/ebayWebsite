package myPackage;

public class Bid {
    public Bid(String bidderID, String bidderRating, String time, String amount) {
        this.bidderID = bidderID;
        this.bidderRating = bidderRating;
        this.time = time;
        this.amount = amount;
    }

	//TODO: make member variables private, and use getters/setters in itemServlet+itemDataParser
    public String bidderID;
    public String bidderRating;
    public String time;
    public String amount; 
    public String bidderLatitude;
    public String bidderLongitude;
    public String bidderCountry;
}