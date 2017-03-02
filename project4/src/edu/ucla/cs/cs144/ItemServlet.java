package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.cs.cs144.AuctionSearch;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
        String pageTitle = "Item page";
        request.setAttribute("title", pageTitle);

        String debug = "This is a debug message";
        String itemID = "-1";
        if(request.getParameter("id") == null){
            request.setAttribute("id", itemID);
        }
        else if (request.getParameter("id") != "-1") {
            itemID = request.getParameter("id");

            String xmlItemData = AuctionSearch.getXMLDataForItemId(itemID);
            request.setAttribute("result", xmlItemData);

        }

        request.setAttribute("debug", debug);
        request.getRequestDispatcher("/item.jsp").forward(request, response);

    }
}
