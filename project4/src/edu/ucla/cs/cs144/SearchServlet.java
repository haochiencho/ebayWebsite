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

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
        String pageTitle = "Search page";
        request.setAttribute("title", pageTitle);
        request.setAttribute("placeholder", "Search...");

        String queryResults = "";        
        int page = 0;

  //      PrintWriter out = response.getWriter();
  //      out.println();
  //      out.println("Parameters:");
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();
            String[] values = request.getParameterValues(name);
            queryResults += name + ": ";
            System.out.println(name);

            if (name == "page") {
                request.setAttribute("page", values[0]);
                page = Integer.parseInt(values[0]);
                queryResults += values[0];
            }

            if (name == "q") {
                for (int i = 0; i < values.length; i++) {
                    /**@var String searchQuery user search string */
                    String searchQuery = values[i];

                    SearchResult[] sq = AuctionSearch.basicSearch(searchQuery, page*20, (page+1)*20);
                    queryResults += Integer.toString(sq.length);
                    for(int j = 0; j < sq.length; j++){
                        queryResults += sq[j].getName() + "</br>";
                    }
                }                
            }

        }        

        request.setAttribute("result", queryResults);
        request.getRequestDispatcher("/search.jsp").forward(request, response);

    }
}
