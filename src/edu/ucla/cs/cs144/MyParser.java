/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

class MyParser {

    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

    static final String[] typeName = {
            "none",
            "Element",
            "Attr",
            "Text",
            "CDATA",
            "EntityRef",
            "Entity",
            "ProcInstr",
            "Comment",
            "Document",
            "DocType",
            "DocFragment",
            "Notation",
    };

    static class MyErrorHandler implements ErrorHandler {

        public void warning(SAXParseException exception)
                throws SAXException {
            fatalError(exception);
        }

        public void error(SAXParseException exception)
                throws SAXException {
            fatalError(exception);
        }

        public void fatalError(SAXParseException exception)
                throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                    "in the supplied XML files.");
            System.exit(3);
        }

    }

    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }

    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }

    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }

    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }

    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                        "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }

	//Our helper functions to parse item nodes for their catgories to form SQL Table 'category'
    // Uses the item id from getItem to create multiple categories with the same itemID.
    static void getCategory(String itemID, Element item){
        if (item.getNodeType() == Node.ELEMENT_NODE) {
            NodeList nodeList = item.getElementsByTagName("Category");
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element)node;
                    String category = eElement.getTextContent();

                    ArrayList<String> data = new ArrayList<String>();
                    data.add(itemID);
                    data.add(category);
                    writeToFile("/home/cs144/ebayData/categoryData", data);
                }
            }
        }
    }

	static String convertToSqlDateFormat(String xmlFormattedDate){
		SimpleDateFormat xmlFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
		SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //'1970-01-01 00:00:01' to '2038-01-19 03:14:07'
		String sqlDate = "";
		
		try {
		Date xmlDate = xmlFormat.parse(xmlFormattedDate);
        sqlDate = sqlDateFormat.format(xmlDate);		
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sqlDate;
	}
	
    //Our helper functions to parse item nodes to form SQL Table 'item'
    static void getItem(Element eElement, int locationID, String sellerID){
        String ItemID = eElement.getAttribute("ItemID");

        String Name = eElement.getElementsByTagName("Name").item(0).getTextContent();
        String Currently = strip(eElement.getElementsByTagName("Currently").item(0).getTextContent());
        String Buy_price = ""; // default for buy price
        if(eElement.getElementsByTagName("Buy_Price").getLength() > 0) {
            strip(Buy_price = eElement.getElementsByTagName("Buy_Price").item(0).getTextContent());
        }

        String First_bid = strip(eElement.getElementsByTagName("First_Bid").item(0).getTextContent());
        String Number_of_bids = eElement.getElementsByTagName("Number_of_Bids").item(0).getTextContent();
        String Location_id = Integer.toString(locationID);
        
		String Started_xml = eElement.getElementsByTagName("Started").item(0).getTextContent();
		String Ends_xml = eElement.getElementsByTagName("Ends").item(0).getTextContent();
		
		String Started = convertToSqlDateFormat(Started_xml);
		String Ends = convertToSqlDateFormat(Started_xml);
		
		String Seller_id = sellerID;
        String Description = eElement.getElementsByTagName("Description").item(0).getTextContent();

        // List of Strings that are added in the order to be printed
        ArrayList<String> data = new ArrayList<String>();
        data.add(ItemID);
        data.add(Name);
        data.add(Currently);
        data.add(Buy_price);
        data.add(First_bid);
        data.add(Number_of_bids);
        data.add(Location_id);
        data.add(Started);
        data.add(Ends);
        data.add(Seller_id);
        data.add(Description);
        writeToFile("/home/cs144/ebayData/itemData", data);
    }

    // appends row/tuple to a file
    static void writeToFile(String fileName, ArrayList<String> data){
        StringBuilder str = new StringBuilder();
        int length = data.size();
        for(int i = 0; i < length; i++){
            str.append(data.get(i));
            if(i != length - 1){
                str.append(",");
            }
            else{
                str.append("\n");
            }
        }
        //System.out.println(str.toString());
        // append string to file and create file if file doesnt
    }

    static void getData(Document doc){
        Map<String, Integer> locationMap = new HashMap<String, Integer>();
        Map<String, Integer> sellerMap = new HashMap<String, Integer>();
        Map<String, Integer> bidderMap = new HashMap<String, Integer>();

        if (doc.hasChildNodes()) {
            NodeList nodeList = doc.getElementsByTagName("Item");
            int locationCount = 1;
            int sellerCount = 1;
            int bidCount = 1;
            int bidderCount = 1;
            for(int i = 0; i < nodeList.getLength(); i++){
                Node Item = nodeList.item(i);
                if (Item.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) Item;

                    // populates the item table
                    String location = eElement.getElementsByTagName("Location").item(0).getTextContent();
                    int locationID;
                    if(!locationMap.containsKey(location)){
                        locationMap.put(location, locationCount);
                        locationID = locationCount;						
                        locationCount++;						
                        getLocation(Integer.toString(locationID), eElement);
                    }
                    else{
                        locationID = locationMap.get(location);
                    }
					
					
					//populates the seller table
                    Node tempNode = eElement.getElementsByTagName("Seller").item(0);
                    Element tempElement = (Element)tempNode;
                    String sellerIdStr = tempElement.getAttribute("UserID");
                    if(!sellerMap.containsKey(sellerIdStr)){
                        sellerMap.put(sellerIdStr, 0);
                        // call getSeller here
                        //System.out.println(sellerIdStr);
						getSeller(eElement);						
                    }
					

					//populates the item table
                    getItem(eElement, locationID, sellerIdStr); // gets a row/tuple of data for Item table
					
                    //populates the category table
                    String ItemID = eElement.getAttribute("ItemID");
                    getCategory(ItemID, eElement); // gets a row/tuple of data for Category table
					
					
					//populates the bid and bidder table
                    Element bids = (Element) eElement.getElementsByTagName("Bids").item(0);
					NodeList bidList = bids.getElementsByTagName("Bid");
                    for(int j = 0; j < bidList.getLength(); j++){
                        Node bid = bidList.item(j);
                        if (bid.getNodeType() == Node.ELEMENT_NODE){
                            Element bidElement = (Element) bid;
                            String bidderID = bidElement.getAttribute("UserID");
                            bidderCount = getBid(bidElement, ItemID, Integer.toString(bidCount), bidderCount, bidderMap);
                            bidCount++;
                        }
                    }
					
                }
            }
        }
    }

    //Our helper functions to parse bid node to form SQL Table 'bid'
    /**@return int returns new bidderCount */
    static int getBid(Element bidElement, String itemID, String bidID, int bidderCount, Map<String, Integer> bidderMap){
        if (bidElement.getNodeType() == Node.ELEMENT_NODE) {
	        String Time = bidElement.getElementsByTagName("Time").item(0).getTextContent();
	        String Amount = strip(bidElement.getElementsByTagName("Amount").item(0).getTextContent());

			ArrayList<String> data = new ArrayList<String>();
			data.add(itemID);
            data.add(Integer.toString(bidderCount));
			data.add(bidID);
			data.add(Time);
			data.add(Amount);
			writeToFile("/home/cs144/ebayData/itemData", data);

            Element bidderElement = (Element)bidElement.getElementsByTagName("Bidder").item(0);
            String bidderUserID = bidderElement.getAttribute("UserID");
            if(!bidderMap.containsKey(bidderUserID)){
                bidderMap.put(bidderUserID, 0);
                bidderCount++;

                // call getBidder here
            }
        }
        return bidderCount;

    }
	

    //Our helper functions to parse item nodes for their sellers to form SQL Table 'seller'
    static void getSeller(Element item) {
		if (item.getNodeType() == Node.ELEMENT_NODE) {
			Node sellerNode = item.getElementsByTagName("Seller").item(0);
			if (sellerNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) sellerNode;
				String sellerID = eElement.getAttribute("UserID");
				String rating = eElement.getAttribute("Rating");

				ArrayList<String> data = new ArrayList<String>();
				data.add(sellerID);
				data.add(rating);
				writeToFile("/home/cs144/ebayData/sellerData", data);
			}
		}
	}

	//TODO: call in getBid
	static void getBidder(String bidderID, String locationID, Element item) {
		
	}
	
    //Our helper functions to parse items and bidder  for their locations to form SQL Table 'location'
	//Uses the location id from getData
    static void getLocation(String locationID, Element item) {
		if (item.getNodeType() == Node.ELEMENT_NODE) {
			Node locationNode = item.getElementsByTagName("Location").item(0);
			if (locationNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) locationNode;
				String location = eElement.getTextContent();
				String latitude = eElement.getAttribute("Latitude");
				String longitude = eElement.getAttribute("Longitude");
				String country = item.getElementsByTagName("Country").item(0).getTextContent();			

				ArrayList<String> data = new ArrayList<String>();
				data.add(locationID);
				data.add(location);
				data.add(latitude);
				data.add(longitude);
				data.add(country);
				writeToFile("/home/cs144/ebayData/locationData", data);
				
			}
		}

    }
	

    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        try {

            doc.getDocumentElement().normalize();

            //Root elemnt should be 'Items'
            //System.out.println("Root element : " + doc.getDocumentElement().getNodeName());
            getData(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**************************************************************/

    }

    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        }
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

    }
}
