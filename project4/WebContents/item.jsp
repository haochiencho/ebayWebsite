<html>
<head>
    <title><%= request.getAttribute("title") %></title>
</head>
<body>
    This is the item id page. Display specific item here.

    <%@ page import="myPackage.Item" %>
    <% if ( request.getAttribute("result") != null ) { %>
        <div>Result: </div>
        <%  Item parsedItem = (Item) request.getAttribute("result");  %>
        <%= parsedItem.itemID %> <br>
        <%= parsedItem.name %> <br>
        <!-- category --> 
        <%= parsedItem.currently %> <br>
        <%= parsedItem.buy_price %> <br>
        <%= parsedItem.first_bid %> <br> 
        <%= parsedItem.number_of_bids %> <br>
        <!-- bids -->
        <!-- location  -->    
        <%= parsedItem.country %> <br>
        <%= parsedItem.started %> <br>
        <%= parsedItem.ends %> <br>
        <!-- seller -->
        <%= parsedItem.description %> <br>
        <div>Debug: <%= request.getAttribute("debug") %></div>
    <% } %>

</body>
</html>