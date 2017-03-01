<html>
<head>
    <title><%= request.getAttribute("title") %></title>
</head>

<body>
    This is the search page. Display search bar and search results here. <br>

    <form action="search" method="GET">
        <input type="hidden" name="page" value=0>
        <input type="text" name="q" placeholder=<%= request.getAttribute("placeholder") %>> <br>
        <input type="submit" value="Submit"> <br>
    </form>

    <h1>Result: <%= request.getAttribute("result") %></h1>
</body>
</html>