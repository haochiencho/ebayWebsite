<html>
<head>
    <title><%= request.getAttribute("title") %></title>
</head>

<body>
    This is the search page. Display search bar and search results here.

    <input type="text" name="search" placeholder=<%= request.getAttribute("placeholder") %> >

</body>
</html>