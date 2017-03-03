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
import myPackage.Item;
import org.xml.sax.InputSource;

class ItemDataParser {

    private Map<String, String> m_itemData = new HashMap<String, String>();

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
    /*
    //Our helper functions to parse item nodes for their catgories to form SQL Table 'category'
    // Uses the item id from getItem to create multiple categories with the same itemID.
    // Also adds an item to categoryList
    static void getCategory(Element item, Item parsedItem){
        if (item.getNodeType() == Node.ELEMENT_NODE) {
            NodeList nodeList = item.getElementsByTagName("Category");
            ArrayList<String> categoryList = new ArrayList();
            StringBuilder categoryListStr = new StringBuilder();
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element)node;
                    String category = eElement.getTextContent();

                    ArrayList<String> data = new ArrayList<String>();
                    data.add(itemID);
                    data.add(category);
                    writeToFile("categoryData.csv", data);

                    if(categoryList.size() == 0){
                        categoryList.add(itemID);
                    }
                    categoryListStr.append(category + " ");
                }
            }
            categoryList.add(categoryListStr.toString());
            writeToFile("categoryListData.csv", categoryList);
        }
    }
    */
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
    /**@var sellerID string cannot be casted into an integer */
    static void getItem(Element eElement, Item parsedItem){
        String ItemID = eElement.getAttribute("ItemID");

        String Name = eElement.getElementsByTagName("Name").item(0).getTextContent();
        String Currently = strip(eElement.getElementsByTagName("Currently").item(0).getTextContent());
        String Buy_price = ""; // default for buy price
        if(eElement.getElementsByTagName("Buy_Price").getLength() > 0) {
            Buy_price = strip(eElement.getElementsByTagName("Buy_Price").item(0).getTextContent());
        }

        String First_bid = strip(eElement.getElementsByTagName("First_Bid").item(0).getTextContent());
        String Number_of_bids = eElement.getElementsByTagName("Number_of_Bids").item(0).getTextContent();

        String Started_xml = eElement.getElementsByTagName("Started").item(0).getTextContent();
        String Ends_xml = eElement.getElementsByTagName("Ends").item(0).getTextContent();

        String Started = convertToSqlDateFormat(Started_xml);
        String Ends = convertToSqlDateFormat(Ends_xml);

        String Description = eElement.getElementsByTagName("Description").item(0).getTextContent();

        // List of Strings that are added in the order to be printed
        ArrayList<String> data = new ArrayList<String>();
        parsedItem.setItemID(ItemID);
        parsedItem.setName(Name);
        parsedItem.setCurrently(Currently);
        parsedItem.setBuyPrice(Buy_price);
        parsedItem.setFirstBid(First_bid);
        parsedItem.setNumberOfBids(Number_of_bids);
        //data.add(Integer.toString(locationID));
        parsedItem.setStarted(Started);
        parsedItem.setEnds(Ends);
        //data.add(sellerID);
        parsedItem.setDescription(Description.substring(0, Math.min(4000, Description.length())));

    }

    // appends row/tuple to a file
    static void writeToFile(String fileName, ArrayList<String> data){
        StringBuilder str = new StringBuilder();
        int length = data.size();
        for(int i = 0; i < length; i++){
            String tempStr = data.get(i);
            if(tempStr == ""){
                tempStr = "\\N";
            }
            str.append(tempStr);
            if(i != length - 1){
                str.append(columnSeparator);
            }
        }

        // append string to file and create file if file doesnt
        try{
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(str.toString());
            out.close();
        } catch (IOException e) {
        }
    }

    static void getData(Document doc, Item parsedItem ){ //Root element should be Item
        Map<String, Integer> sellerMap = new HashMap<String, Integer>();
        Map<String, Integer> bidderMap = new HashMap<String, Integer>();
        int[] anArray = new int[2];

        Node item =  doc.getDocumentElement();

        if (item.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) item;

            /*
            // populates the location table
            getLocation(locationCount, eElement);


            //populates the seller table
            Node tempNode = eElement.getElementsByTagName("Seller").item(0);
            Element tempElement = (Element)tempNode;
            String sellerIdStr = tempElement.getAttribute("UserID");
            if(!sellerMap.containsKey(sellerIdStr)){
                sellerMap.put(sellerIdStr, 0);
                // call getSeller here
                getSeller(eElement);
            }
            */

            //populates the item info
            getItem(eElement, parsedItem); // gets a row/tuple of data for Item table
            
            //populates the item's category info
            //getCategory(eElement, parsedItem); // gets a row/tuple of data for Category table
            
            /*
            //populates the bid and bidder table
            Element bids = (Element) eElement.getElementsByTagName("Bids").item(0);
            NodeList bidList = bids.getElementsByTagName("Bid");
            for(int j = 0; j < bidList.getLength(); j++){
                Node bid = bidList.item(j);
                if (bid.getNodeType() == Node.ELEMENT_NODE){
                    Element bidElement = (Element) bid;
                    String bidderID = bidElement.getAttribute("UserID");
                    if ( getBid(bidElement, ItemID, Integer.toString(bidCount), bidderMap, locationCount) == true )
                        locationCount++;
                    bidCount++;
                }
            }
            */

            
            
        } 

    }

    /*
    //Our helper functions to parse bid node to form SQL Table 'bid'
    static boolean getBid(Element bidElement, String itemID, String bidID, Map<String, Integer> bidderMap, int locationID){
        if (bidElement.getNodeType() == Node.ELEMENT_NODE) {
            String Time_xml = bidElement.getElementsByTagName("Time").item(0).getTextContent();
            String Time = convertToSqlDateFormat(Time_xml);

            String Amount = strip(bidElement.getElementsByTagName("Amount").item(0).getTextContent());

            Element bidderElement = (Element)bidElement.getElementsByTagName("Bidder").item(0);
            String bidderUserID = bidderElement.getAttribute("UserID");

            ArrayList<String> data = new ArrayList<String>();
            data.add(itemID);
            data.add(bidderUserID);
            data.add(bidID);
            data.add(Time);
            data.add(Amount);
            writeToFile("bidData.csv", data);

            return getBidder(bidderUserID, bidderElement, locationID);
        }
        return false;
    }
    */
    /*
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
                writeToFile("sellerData.csv", data);
            }
        }
    }
    */
    /*
    static boolean getBidder(String bidderID, Element bidderElement, int locationID){
        boolean getLocationFuncIsCalled = false;
        String rating = bidderElement.getAttribute("Rating");
        if(bidderElement.getElementsByTagName("Location").getLength() > 0) {
            getLocation(locationID, bidderElement);
            getLocationFuncIsCalled = true;
        }
        else{
            locationID = -1;
        }

        ArrayList<String> data = new ArrayList<String>();
        data.add(bidderID);
        data.add(rating);
        data.add(Integer.toString(locationID));
        String home = System.getProperty("user.home");
        writeToFile("bidderData.csv", data);

        return getLocationFuncIsCalled;

    }
    */
    /*
    //Our helper functions to parse items and bidder  for their locations to form SQL Table 'location'
    //Uses the location id from getData
    static void getLocation(int locationID, Element item) {
        if (item.getNodeType() == Node.ELEMENT_NODE) {
            Element locationElement = getElementByTagNameNR( item, "Location"); //need to use NON-recursive, else will get element of children
            String location = locationElement.getTextContent();
            String latitude = locationElement.getAttribute("Latitude");
            String longitude = locationElement.getAttribute("Longitude");
            String country = "";
            if(getElementsByTagNameNR( item,"Country").length > 0) {
                country = getElementByTagNameNR(item, "Country").getTextContent();
            }

            ArrayList<String> geoLocation = new ArrayList();
            String ItemID = item.getAttribute("ItemID");
            geoLocation.add(ItemID);
            geoLocation.add(longitude);
            geoLocation.add(latitude);
            if(longitude != "" && latitude != "") {
                writeToFile("geoLocationData.csv", geoLocation);
            }

            ArrayList<String> data = new ArrayList<String>();
            data.add(Integer.toString(locationID));
            data.add(location);
            data.add(longitude);
            data.add(latitude);
            data.add(country);
            writeToFile("locationData.csv", data);
        }
    }
    */

    /* Process one item xml data string.
     */
    static void processXMLString (String xmlItemData, Item parsedItem) {
        Document doc = null;

        try {
            InputSource is = new InputSource(new StringReader(xmlItemData));
            doc = builder.parse(is);                    
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on string " + xmlItemData);
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * string. Use doc.getDocumentElement() to get the root Element. */

        /* Fill in code here (you will probably need to write auxiliary
            methods). */
            
        try {

            doc.getDocumentElement().normalize();

            //Root elemnt should be 'Item'
            System.out.println("Root element : " + doc.getDocumentElement().getNodeName());
            getData(doc, parsedItem);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static Item parseItemXMLString(String xmlItemData) {
//        int locationCount = 1;
//        int bidCount = 1;

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

        // Process itemXML data given as a string
        if (xmlItemData != null) {
            Item parsedItem = new Item(); 
            processXMLString(xmlItemData, parsedItem);
            return parsedItem;
        }
        return null;
    }
}
