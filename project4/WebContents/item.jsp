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
            var latlng = new google.maps.LatLng(34.063509,-118.44541);
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

    <div id="map_canvas" style="width:100%; height:100%"></div>

</body>
</html>