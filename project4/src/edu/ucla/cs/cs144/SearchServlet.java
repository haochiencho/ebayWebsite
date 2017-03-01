package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Enumeration;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
        String pageTitle = "Search page";
        request.setAttribute("title", pageTitle);
        request.setAttribute("placeholder", "Search...");

        String queryResults = "";        
  
  //      PrintWriter out = response.getWriter();
  //      out.println();
  //      out.println("Parameters:");
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
                String name = (String) paramNames.nextElement();
                String[] values = request.getParameterValues(name);
                System.out.println("    " + name + ":");
                for (int i = 0; i < values.length; i++) {
                System.out.println("      " + values[i]);
            }
        }        

        request.setAttribute("result", queryResults);
        request.getRequestDispatcher("/search.jsp").forward(request, response);

    }
}
