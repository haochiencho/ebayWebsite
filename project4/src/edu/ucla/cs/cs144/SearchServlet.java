package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Enumeration;
import edu.ucla.cs.cs144.AuctionSearch;
import edu.ucla.cs.cs144.SearchResult;
import java.util.ArrayList;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
        String pageTitle = "Search page";
        request.setAttribute("title", pageTitle);
        request.setAttribute("placeholder", "Search...");

        String debug = "";
        ArrayList<String> queryResults = new ArrayList<String>();
        int numResultsToSkip, numResultsToReturn;
        numResultsToSkip = 0;
        numResultsToReturn = 20;
        if(request.getParameter("numResultsToSkip") == null) {
            request.setAttribute("numResultsToSkip", numResultsToSkip);
        }
        if(request.getParameter("numResultsToReturn") == null){
            request.setAttribute("numResultsToReturn", numResultsToReturn);
        }

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();
            String[] values = request.getParameterValues(name);
            debug += name + ": ";
            System.out.println(name);

            if (name.equals("numResultsToSkip")) {
                request.setAttribute("numResultsToSkip", Integer.toString(Integer.parseInt(values[0]) + 20));
                numResultsToSkip = Integer.parseInt(values[0]);
                debug += values[0];
            }

            if(name.equals("numResultsToReturn")){
                request.setAttribute("numResultsToReturn", values[0]);
                numResultsToReturn = Integer.parseInt(values[0]);
                debug += values[0];
            }

            //TODO: check if query is empty
            if (name.equals("q")) {
                if(values.length == 0){
                    request.setAttribute("numResultsToSkip", 0);
                    request.setAttribute("numResultsToReturn", 20);
                }
                for (int i = 0; i < values.length; i++) {
                    if(values[i].equals(null)){
                        request.setAttribute("numResultsToSkip", 0);
                        request.setAttribute("numResultsToReturn", 20);
                    }
                    if(values[i].equals("")){
                        request.setAttribute("numResultsToSkip", 0);
                        request.setAttribute("numResultsToReturn", 20);
                    }
                    request.setAttribute("q", values[i]);
                    /**@var String searchQuery user search string */
                    String searchQuery = values[i];

                    SearchResult[] sq = AuctionSearch.basicSearch(searchQuery, numResultsToSkip, numResultsToReturn);
                    debug += Integer.toString(sq.length);
                    for(int j = 0; j < sq.length; j++){
                        queryResults.add("<a href=\"/item?id=" + sq[j].getItemId() + "\">" + sq[j].getName() + "</a><br>");
                    }
                }                
            }

        }        

        request.setAttribute("debug", debug);
        request.setAttribute("result", queryResults);
        request.getRequestDispatcher("/search.jsp").forward(request, response);

    }
}
