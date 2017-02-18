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

	/**
	 * @param String query
	 * @param int numResultsToskip
	 * @param int numResultsToReturn
	 *
	 * @return SearchResult[]
	 * */
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		
		// instantiate the search engine
		try {
			SearchEngine se = new SearchEngine();

			// retrieve top matching document list for the query
			TopDocs topDocs = se.performSearch(query, numResultsToSkip + numResultsToReturn);

			// obtain the ScoreDoc (= documentID, relevanceScore) array from topDocs
			ScoreDoc[] hits = topDocs.scoreDocs;

			// retrieve each matching document from the ScoreDoc array
			int resultCount = 0;

			int size = hits.length - numResultsToSkip;
			if(size < 0)
				size = 0;
			SearchResult[] resultArray = new SearchResult[size];
			for (int i = numResultsToSkip; i < numResultsToSkip + numResultsToReturn && i < hits.length; i++) {
				Document doc = se.getDocument(hits[i].doc);
				resultArray[resultCount] = new SearchResult(doc.get("itemID"), doc.get("name"));
				resultCount++;
			}
			
			return resultArray;

		} catch (Exception exception) {
			exception.printStackTrace();
        	System.exit(-1); 
		}	

		return new SearchResult[0];
	}

	/**
	 * @param String query
	 * @param SearchRegion region
	 * @param int numResultsToskip
	 * @param int numResultsToReturn
	 *
	 * @return SearchResult[]
	 * */
	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {

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
			Statement stmt = conn.createStatement();

			String spatialQuery = "SELECT * FROM geoLocation WHERE MBRContains(" + searchRectangle + ",coords)";
			ResultSet rs = stmt.executeQuery(spatialQuery);

			Integer itemID; 
			String coords;
			while (rs.next()) {
				itemID = rs.getInt("itemID");
				coords = rs.getString("coords");
				spatialItemID.add(itemID);
	   		}

	   		stmt.close();
			rs.close();

			// basic query
			SearchEngine se = new SearchEngine();

			// retrieve top matching document list for the query
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

		// Create a connection to the database to retrieve Items from Spatial Index
		Connection conn = null;	
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}	

		String itemQuery = "SELECT * FROM item " +
				"JOIN location ON location.locationID=item.locationID " +
				"JOIN seller ON seller.sellerID=item.sellerID " +
				" WHERE item.itemID=" + itemId;

		String categoryQuery = "SELECT category FROM category WHERE itemID=" + itemId;
		String bidQuery = "SELECT * FROM bid " +
				"JOIN bidder ON bid.bidderID=bidder.bidderID " +
				"LEFT OUTER JOIN location ON location.locationID=bidder.locationID " +
				"WHERE bid.itemID=" + itemId;
		String xmlResult = "";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(itemQuery);

			if ( rs.isBeforeFirst() ) { //is the result non-empty?
				rs.first();
				String itemID = 		escapeXml( rs.getString("itemID"), true );
				String name = 			escapeXml( rs.getString("name"), false );
				String description = 	escapeXml( rs.getString("description"), false );
				String currently = 		"$" + escapeXml( rs.getString("currently"), false );
				String itemLocation = 	escapeXml( rs.getString("location"), false );
				String itemLatitude = 	escapeXml( rs.getString("latitude"), true );
				String itemLongitude =	escapeXml( rs.getString("longtitude"), true );
				String itemCountry = 	escapeXml( rs.getString("country"), false );
				String buyPrice = 		escapeXml( rs.getString("buyPrice"), false );
				String firstBid = 		"$" + escapeXml( rs.getString("firstBid"), false );
				String numberOfBids = 	escapeXml( rs.getString("numberOfBids"), false );
				String sellerID = 		escapeXml( rs.getString("sellerID"), true );
				String rating = 		escapeXml( rs.getString("rating"), true );

				String started = 	escapeXml( convertToXmlDateFormat(rs.getTimestamp("started")), false );
				String ends = 		escapeXml( convertToXmlDateFormat(rs.getTimestamp("ends")), false );

				xmlResult += "<Item ItemID=\"" + itemID + "\">\n";
				xmlResult += "<Name>" + name + "</Name>\n";


				ResultSet rsCategory = stmt.executeQuery(categoryQuery);
				while (rsCategory.next()) {
					String category = escapeXml( rsCategory.getString("category"), false );
					xmlResult += "<Category>" + category + "</Category>\n";
				}

				xmlResult += "<Currently>" + currently + "</Currently>\n";
				if(buyPrice != ""){
					xmlResult += "<Buy_Price>$" + buyPrice + "</Buy_Price>\n";
				}
				xmlResult += "<First_Bid>" + firstBid + "</First_Bid>\n";
				xmlResult += "<Number_of_Bids>" + numberOfBids + "</Number_of_Bids>\n";
				boolean hasBid = false;


				ResultSet rsBid = stmt.executeQuery(bidQuery);
				while (rsBid.next()) {
					if(!hasBid){
						xmlResult += "<Bids>\n";
					}
					hasBid = true;
					String bidderRating = 	escapeXml( rsBid.getString("rating"), true );
					String bidderID = 		escapeXml( rsBid.getString("bidderID"), true );
					String bidderLocation = escapeXml( rsBid.getString("location"), false );
					String bidderCountry = 	escapeXml( rsBid.getString("country"), false );
					String bidTime = 		escapeXml( convertToXmlDateFormat(rsBid.getTimestamp("bidTime")), false );
					String bidAmount = 		"$" + escapeXml( rsBid.getString("amount"), false );

					xmlResult += "<Bid>\n" + "<Bidder Rating=\"" + bidderRating + "\" UserID=\"" + bidderID + "\">\n";
					if(bidderLocation != ""){
						xmlResult += "<Location>" + bidderLocation + "</Location>\n";
					}
					if(bidderCountry != ""){
						xmlResult += "<Country>" + bidderCountry + "</Country>\n";
					}
					xmlResult += "</Bidder>\n";
					xmlResult += "<Time>" + bidTime + "</Time>\n";
					xmlResult += "<Amount>" + bidAmount + "</Amount>\n";
					xmlResult += "</Bid>\n";
				}

				if(!hasBid){
					xmlResult += "<Bids />\n";
				}
				else{
					xmlResult += "</Bids>\n";
				}

				xmlResult += "<Location";
				if(itemLatitude != ""){
					xmlResult += " Latitude=\"" + itemLatitude + "\"";
				}
				if(itemLongitude != ""){
					xmlResult += " Longitude=\"" + itemLongitude + "\"";
				}
				xmlResult += ">" + itemLocation + "</Location>\n";

				xmlResult += "<Country>" + itemCountry + "</Country>\n";
				xmlResult += "<Started>" + started + "</Started>\n";
				xmlResult += "<Ends>" + ends + "</Ends>\n";
				xmlResult += "<Seller Rating=\"" + rating + "\" UserID=\"" + sellerID + "\" />\n";
				if(description == ""){
					xmlResult += "<Description />\n";
				}
				else{
					xmlResult += "<Description>" + description + "</Description>\n";
				}
				xmlResult += "</Item>";
			}

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

	//org.apache.commons.lang.StringEscapeUtils
	//String escapedXml = StringEscapeUtils.escapeXml("the data might contain & or ! or % or ' or # etc");
	public String escapeXml(String s, boolean isAttribute) {
		if (s == null)
			return "";
		// escape character inspired by: http://stackoverflow.com/questions/1091945/what-characters-do-i-need-to-escape-in-xml-documents
		if(isAttribute)
			return s.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
		else
			return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
	}


}
