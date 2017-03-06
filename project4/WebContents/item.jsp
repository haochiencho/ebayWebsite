<html>
<head>
    <title><%= request.getAttribute("title") %></title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <style type="text/css">
        html { height: 100% }
        body { height: 100%; margin: 0px; padding: 0px }
        #map_canvas { height: 100% }
    </style>
    <script type="text/javascript"
            src="http://maps.google.com/maps/api/js?sensor=false">
    </script>
    <script type="text/javascript">
        function initialize() {
            var latlng;
            var latitude = '<% Item thisItem = (Item) request.getAttribute("result"); %> <%= thisItem.latitude %>';
            if ( isNaN(parseFloat(latitude)) )
                latitude = 34.063509; //Default latlng to Westwood
            else 
                latitude = parseFloat(latitude);
            
            var longitude = '<%= thisItem.longitude %>';
            if (isNaN(parseFloat(longitude)) )
                longitude = -118.44541;
            else
                longitude = parseFloat(longitude);
    
            latlng = new google.maps.LatLng( latitude, longitude);

            var myOptions = {
                zoom: 14, // default is 8
                center: latlng,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            var map = new google.maps.Map(document.getElementById("map_canvas"),
                myOptions);
            var marker = new google.maps.Marker({
                position: latlng,
                map: map,
                title: 'Item Location'
            });
        }

    </script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body onload="initialize()">
    This is the item id page. Display specific item here.

    <div>Result: </div>

    <%@ page import="myPackage.Item" %>
    <% if ( request.getAttribute("result") != null ) { %>
        <table style="width:100%">
        <%  Item parsedItem = (Item) request.getAttribute("result");  %>
        <tr>
            <td>ItemID</td>
            <td><%= parsedItem.itemID %> </td>
        </tr>

        <tr>
            <td>Name</td>
            <td><%= parsedItem.name %> </td>
        </tr>

        <tr>
            <td>Category</td>
            <td>
                <% for (int i = 0; i < parsedItem.categories.size()-1; i++)  { %>
                    <%= parsedItem.categories.get(i) %>; &nbsp;
                <% } %>
                <%= parsedItem.categories.get(parsedItem.categories.size()-1) %>
            </td>
        </tr>

        <tr> 
            <td>Currently</td>
            <td>$<%= parsedItem.currently %> </td>
        </tr>

        <% if (parsedItem.buy_price != "") { %>
        <tr>
            <td>Buy Price</td>
            <td>$<%= parsedItem.buy_price %> </td>
        </tr>
        <% } %>

        <tr>
            <td>First Bid</td>
            <td>$<%= parsedItem.first_bid %> </td>
        </tr> 

        <tr>
            <td>Number of Bids</td>
            <td><%= parsedItem.number_of_bids %> </td>
        </tr>
        
        <tr>
            <td>Location</td>
            <td><%= parsedItem.location %> </td>
        </tr>

        <% if (parsedItem.latitude != "") { %>
        <tr>
            <td>Latitude</td>
            <td><%= parsedItem.latitude %> </td>
        </tr>
        <% } %>

        <% if (parsedItem.longitude != "") { %>
        <tr> 
            <td>Longitude</td>
            <td><%= parsedItem.longitude%> </td>
        </tr>
        <% } %>


        <% if (parsedItem.country != "" && 
                parsedItem.country != null) { %>
        <tr> 
            <td>Country</td>
            <td><%= parsedItem.country %> </td>
        </tr>
        <% } %>

        <tr>
            <td>Started</td>
            <td><%= parsedItem.started %> </td>
        </tr>

        <tr>
            <td>Ends</td>
            <td><%= parsedItem.ends %> </td>
        </tr>
        

        <tr>
            <td>SellerID</td>
            <td><%= parsedItem.sellerID %> </td>
        </tr>

        <tr>
            <td>Seller Rating</td>
            <td><%= parsedItem.sellerRating %> </td>
        </tr>

        <tr>
            <td>Description</td>
            <td><%= parsedItem.description %> </td>
        </tr>

        </table>
        
        <% if (parsedItem.bids != null) { %>
            <br>
            <% 	for (int j = 0; j < parsedItem.bids.size(); j++)  { %>
                <table>
                <tr>Bid &nbsp; <%=j+1%></tr>
                <tr>
                    <td>BidderID</td>
                    <td><%= parsedItem.bids.get(j).bidderID %> </td>
                </tr>
                <tr>
                    <td>Bidder Rating</td>
                    <td><%= parsedItem.bids.get(j).bidderRating %> </td>
                </tr>

                <% if (parsedItem.bids.get(j).bidderLatitude != "" && 
                         parsedItem.bids.get(j).bidderLongitude != "") { %>
                <tr>
                    <td>Bidder Location</td>
                    <td><%= parsedItem.bids.get(j).bidderLatitude %>, 
                        <%= parsedItem.bids.get(j).bidderLongitude %></td>
                </tr>
                <% } %>
                
                <% if (parsedItem.bids.get(j).bidderCountry != "" &&
                        parsedItem.bids.get(j).bidderCountry != null) { %>
                <tr>
                    <td>Bidder Country</td>
                    <td><%= parsedItem.bids.get(j).bidderCountry %> </td>
                </tr>                
                <% } %>

                <tr>
                    <td>Time</td>
                    <td><%= parsedItem.bids.get(j).time %> </td>
                </tr>
                <tr>
                    <td>Amount</td>
                    <td>$<%= parsedItem.bids.get(j).amount %> </td>
                </tr>
                </table><br>         
            <% } %>
        <% } %>

    <% } %>

    <div id="map_canvas" style="width:100%; height:100%"></div>

</body>
</html>

<style>
    table, th, td {
        border: 3px solid black;
        background-color: #f1f1c1;
    }
    
</style>