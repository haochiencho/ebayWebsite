package edu.ucla.cs.cs144;

import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Scanner;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLConnection;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
        String pageTitle = "Proxy Servlet page";
        request.setAttribute("title", pageTitle);

        String url = "http://google.com/complete/search";
        String charset = "UTF-8";
        String param = request.getParameter("q"); // get query input

        String query = String.format("output=toolbar&q=%s",
                URLEncoder.encode(param, charset));

        URLConnection connection = new URL(url + "?" + query).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        InputStream response_stream = connection.getInputStream();

        try (Scanner scanner = new Scanner(response_stream)) {
            String responseBody = scanner.useDelimiter("\\A").next();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/xml");
            response.getWriter().write(responseBody);
        }
    }
}
