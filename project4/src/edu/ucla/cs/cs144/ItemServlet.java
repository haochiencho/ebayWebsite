package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.cs.cs144.AuctionSearch;
import edu.ucla.cs.cs144.ItemDataParser;
import myPackage.Item;
//import java.util.*;
//import java.lang.reflect.*;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
        String pageTitle = "Item page";
        request.setAttribute("title", pageTitle);

        String debug = "This is a debug message";
        String itemID = "";
        
        if (request.getParameter("id") != null) {
            itemID = request.getParameter("id");

            String xmlItemData = AuctionSearch.getXMLDataForItemId(itemID);
            request.setAttribute("result", xmlItemData);
            
            //parse xml Item data
            if (xmlItemData != null ) {
                Item parsedItem = ItemDataParser.parseItemXMLString(xmlItemData);
                if (parsedItem != null) {
                   request.setAttribute("result", parsedItem);
                   request.setAttribute("debug", debug);
                }
            }
            
            /*
            //TODO:
            Item parsedItem = ItemDataParser.parseItemXMLString(xmlItemData);

            try {
                Map<String, String> result = new HashMap<String, String>();
                for (Field field : parsedItem.getClass().getDeclaredFields()) {
                    field.setAccessible(true); // if you want to modify private fields
                    // System.out.println(field.getName() + " - " + field.getType()  + " - " + field.get(obj));
                    result.put(field.getName(), (String) field.get(parsedItem));
                }
                request.setAttribute("result", result);
            }
            catch (IllegalAccessException e) {
                System.out.println("Error parsing Item Object");
                System.exit(-1);
            }
            */
        }

        request.getRequestDispatcher("/item.jsp").forward(request, response);

    }
}
