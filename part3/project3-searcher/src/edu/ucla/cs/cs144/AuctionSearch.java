package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.*;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

import edu.ucla.cs.cs144.SearchEngine;
import java.sql.Timestamp;
//import org.apache.commons.lang.StringEscapeUtils;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		// TODO: Your code here!
		
		// instantiate the search engine
		try {
			SearchEngine se = new SearchEngine();

			// retrieve top matching document list for the query
			TopDocs topDocs = se.performSearch(query, numResultsToSkip + numResultsToReturn); //TODO: more specific queries
			//TopDocs topDocs = se.performSearch(query, 2000);

			// obtain the ScoreDoc (= documentID, relevanceScore) array from topDocs
			ScoreDoc[] hits = topDocs.scoreDocs;

			// retrieve each matching document from the ScoreDoc array
			int resultCount = 0;

			SearchResult[] resultArray = new SearchResult[numResultsToReturn];	
			for (int i = numResultsToSkip; i < numResultsToSkip + numResultsToReturn && i < hits.length; i++) { //TODO: potential off-by-one error?
				Document doc = se.getDocument(hits[i].doc);
				resultArray[resultCount] = new SearchResult(doc.get("itemID"), doc.get("name"));
				resultCount++;
			}

			
//			SearchResult[] resultArray = new SearchResult[hits.length];
//			for (int i = 0; i < hits.length; i++) {
//				Document doc = se.getDocument(hits[i].doc);
//				resultArray[resultCount] = new SearchResult(doc.get("itemID"), doc.get("name"));
//				resultCount++;
//				System.out.println("ResultCOunt: " + Integer.toString(resultCount));
//			}
			
			return resultArray;

		} catch (Exception exception) {
			exception.printStackTrace();
        	System.exit(-1); 
		}	

		return new SearchResult[0];
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!

		// Create a connection to the database to retrieve Items from Spatial Index
		Connection conn = null;

		// set of item ids from spatial query
		Set<Integer> spatialItemID = new HashSet<Integer>();

		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}

		ArrayList<SearchResult> searchResultList = new ArrayList<SearchResult>();

		try {

			String searchRectangle = "PointFromText('Polygon((" +
										region.getLx() + " " + region.getLy() + "," +
										region.getRx() + " " + region.getLy() + "," +
										region.getRx() + " " + region.getRy() + "," +
										region.getLx() + " " + region.getRy() + "," +
										region.getLx() + " " + region.getLy() + "))')";
			String example = "PointFromText('Polygon((-200 -200,-200 200,200 200,200 -200,-200 -200))')";
			System.out.println(searchRectangle);
			Statement stmt = conn.createStatement();

			String spatialQuery = "SELECT * FROM geoLocation WHERE MBRContains(" + searchRectangle + ",coords)";
			System.out.println(spatialQuery);
			ResultSet rs = stmt.executeQuery(spatialQuery);

			Integer itemID; 
			String coords;
			while (rs.next()) {
				itemID = rs.getInt("itemID");
				coords = rs.getString("coords");
				spatialItemID.add(itemID);
				//System.out.println("ItemID: " + Integer.toString(itemID) + " Coords: " + coords);

	   		}

	   		stmt.close();
			rs.close();

			// basic query
			SearchEngine se = new SearchEngine();

			// retrieve top matching document list for the query
			//TopDocs topDocs = se.performSearch(query, numResultsToSkip + numResultsToReturn); //TODO: more specific queries
			TopDocs topDocs = se.performSearch(query, Integer.MAX_VALUE);

			// obtain the ScoreDoc (= documentID, relevanceScore) array from topDocs
			ScoreDoc[] hits = topDocs.scoreDocs;

			// retrieve each matching document from the ScoreDoc array
			int resultCount = 0;

			String itemName;
			String itemIDstr;
			for(int i = 0; i < hits.length; i++) {
				Document doc = se.getDocument(hits[i].doc);
				itemIDstr = doc.get("itemID");
				itemID = Integer.parseInt(itemIDstr);
				if(spatialItemID.contains(itemID)){
					if(numResultsToSkip > 0)
						numResultsToSkip--;
					else{
						if(numResultsToReturn > 0){
							numResultsToReturn--;
							searchResultList.add(new SearchResult(itemIDstr, doc.get("name")));
						}
						else
							break;
					}
				}
			}


		} catch (Exception exception) {
			exception.printStackTrace();
        	System.exit(-1); 			
		}

		// Close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}

		SearchResult[] resultArray = new SearchResult[searchResultList.size()];
		for(int i = 0; i < searchResultList.size(); i++){
			resultArray[i] = searchResultList.get(i);
		}

		return resultArray;
	}

	public boolean isNull(String str){
		return str == "\\N";
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!

		// Create a connection to the database to retrieve Items from Spatial Index
		Connection conn = null;	
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}	

		String xmlResult = "";
		String itemQuery = "SELECT * FROM item " +
				"JOIN location ON location.locationID=item.locationID " +
				"JOIN seller ON seller.sellerID=item.sellerID " +
				" WHERE item.itemID=" + itemId;

		String categoryQuery = "SELECT category FROM category WHERE itemID=" + itemId;
		String bidQuery = "SELECT * FROM bid " +
				"JOIN bidder ON bid.bidderID=bidder.bidderID " +
				"LEFT OUTER JOIN location ON location.locationID=bidder.locationID " +
				"WHERE bid.itemID=" + itemId;

		try {
			Statement stmt = conn.createStatement();
			System.out.println(itemQuery);
			ResultSet rs = stmt.executeQuery(itemQuery);

			//TODO: price variables only have 1 zero after the . if theyre an exact amount; ie. want 16.00 not 16.0
			//TODO: lat and long variables seem to be return 0.0 values if null, want string itemLocation = "" if null
			if ( rs.isBeforeFirst() ) { //is the result non-empty?
				rs.first();
				String itemID = 		escapeXml( Integer.toString(rs.getInt("itemID")) );
				String name = 			escapeXml( rs.getString("name") );
				String description = 	escapeXml( rs.getString("description") );
				String currently = 		"$" + escapeXml( Double.toString(rs.getDouble("currently")) );
				String itemLocation = 	escapeXml( rs.getString("location") );
				String itemLatitude = 	escapeXml( Double.toString(rs.getDouble("latitude")) );
				System.out.println("itemLatitude:_" + itemLatitude + "_");
				String itemLongitude =	escapeXml( Double.toString(rs.getDouble("longtitude")) );
				String itemCountry = 	escapeXml( rs.getString("country") );
				String buyPrice = 		"$" + escapeXml( Double.toString(rs.getDouble("buyPrice")) );
				String firstBid = 		"$" + escapeXml( Double.toString(rs.getDouble("firstBid")) );
				String numberOfBids = 	escapeXml( Integer.toString(rs.getInt("numberOfBids")) );
				String sellerID = 		escapeXml( rs.getString("sellerID") );
				String rating = 		escapeXml( Integer.toString(rs.getInt("rating")) );
				System.out.println("Item: " + name);
//				System.out.println("Item: " + itemID + ", " + name + ", "+ description + ", " + currently + ", " + buyPrice + ", " + firstBid + ", " + numberOfBids);
				System.out.println("Location: " + itemLocation + ", " + itemLatitude + ", " + itemLongitude + ", " + itemCountry);
//				System.out.println("Seller: " + sellerID + ", " + rating);

				String started = 	escapeXml( convertToXmlDateFormat(rs.getTimestamp("started")) );
				String ends = 		escapeXml( convertToXmlDateFormat(rs.getTimestamp("ends")) );
				System.out.println("Timestamps: " + started + ", " + ends);
				
				// TODO: tokenize category and bid
				// format into XML
				// check all tokens

				ResultSet rsCategory = stmt.executeQuery(categoryQuery);
				while (rsCategory.next()) {
					String category = escapeXml( rsCategory.getString("category") );
					System.out.println("Category: " + category);
				}

				ResultSet rsBid = stmt.executeQuery(bidQuery);
				while (rsBid.next()) {
					String bidderRating = 	escapeXml( Integer.toString(rsBid.getInt("rating")) );					
					String bidderID = 		escapeXml( rsBid.getString("bidderID") );
					String bidderLocation = escapeXml( rsBid.getString("location") );
					String bidderCountry = 	escapeXml( rsBid.getString("country") );
					String bidTime = 		escapeXml( convertToXmlDateFormat(rsBid.getTimestamp("bidTime")) );
					String bidAmount = 		"$" + escapeXml( Double.toString(rsBid.getDouble("amount")) );
					System.out.println("Bid: " + bidderRating + ", " + bidderLocation + ", " + bidderCountry +
										", " + bidTime + ", " + bidAmount);
				}

				System.out.println(itemID);
				if(isNull(description))
					System.out.println(description);
				System.out.println(currently);
				System.out.println("here");
			}

			//rs = stmt.executeQuery(categoryQuery);
			//rs = stmt.executeQuery(bidQuery);

			//TODO: Fill in xmlResult string
			rs.close();			
			stmt.close();


		} catch (Exception exception) {
			exception.printStackTrace();
        	System.exit(-1); 
		} 

		// Close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}

		return xmlResult;
	}
	
	public String echo(String message) {
		return message;
	}

	public String convertToXmlDateFormat(Timestamp javaTimeStamp) { 
		SimpleDateFormat xmlDateFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
		String xmlDateString = "";
		xmlDateString = xmlDateFormat.format(javaTimeStamp);
		return xmlDateString;
	}

	//TODO: Maybe replace this function with 
	//org.apache.commons.lang.StringEscapeUtils
	//String escapedXml = StringEscapeUtils.escapeXml("the data might contain & or ! or % or ' or # etc");
	public String escapeXml(String s) {
		if (s == null)
			return "";
		if (s == "0.0")
			return "";
		return s.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
	}


}
