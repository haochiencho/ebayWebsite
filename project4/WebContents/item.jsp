<html>
<head>
    <title><%= request.getAttribute("title") %></title>
</head>
<body>
    This is the item id page. Display specific item here.
    <div>Result: <%= request.getAttribute("result") %></div>
</body>
</html>