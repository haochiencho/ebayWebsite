<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%= request.getAttribute("title") %></title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>

<body>
    <div class="container title_box">
        <div class="title">
            Ebay Website
        </div>
        <form action="search" method="GET">
            <input type="hidden" name="numResultsToSkip" value=<%= request.getAttribute("numResultsToSkip") %> >
            <input type="hidden" name="numResultsToReturn" value=<%= request.getAttribute("numResultsToReturn") %> >
            <input type="text" name="q" class="col-md-8 col-md-offset-2" id="search_box" placeholder=<%= request.getAttribute("placeholder") %> >
            <input type="submit" value="Submit"> <br>
        </form>

    </div>

    <div class="result_box">
    <%@ page import="java.util.ArrayList" %>
    <% ArrayList<String> list = (ArrayList<String>) request.getAttribute("result"); %>
    <% for (int i = 0; i < list.size(); i++) { %>
        <div class="result well col-md-8 col-md-offset-2">
        <%= list.get(i) %>
        </div>
    <% } %>
    </div>

    <div class="result_box">
        <div class="next_row input_box">
            <form action="search" method="GET">
                <input type="hidden" name="numResultsToSkip" value=<%= request.getAttribute("numResultsToSkip") %> >
                <input type="hidden" name="numResultsToReturn" value=<%= request.getAttribute("numResultsToReturn") %> >
                <input type="hidden" name="q" value=<%= request.getAttribute("q") %> >
                <input type="submit" class="next-btn btn-primary col-md-1 col-md-offset-9" value="Next"> <br>
            </form>
        </div>
    </div>
    <pre id="suggestion"></pre>

    <div>Debug: <%= request.getAttribute("debug") %></div>
    <script>
        var UIcontroller = function(){
            var initEventListeners = function(){
                document.addEventListener('keyup', function(event) {
                    // TODO: make request to proxy server
                    sendAjaxRequest();
                });
            };

            var sendAjaxRequest = function(){
                var query = document.getElementById('search_box').value;
                var ajax_request = "/eBay/suggest?output=toolbar&q=" + encodeURI(query);

                var xmlHttp = new XMLHttpRequest();

                var showSuggestion = function(){
                    if(xmlHttp.readyState == 4) {
                        var response = xmlHttp.responseText;
                        response = response.replace(/</g, "&lt");
                        response = response.replace(/>/g, "&gt");
                        document.getElementById("suggestion").interHTML = response;
                        console.log(response);
                    }
                };

                xmlHttp.open("GET", ajax_request);
                xmlHttp.onreadystatechange = showSuggestion;
                xmlHttp.send();

//                console.log(ajax_request);
            };



            return {
                init: function(){
                    initEventListeners();
                }
            };
        }();

        UIcontroller.init();
    </script>
</body>
</html>

<style>
    .title{
        font-size: 72px;
        text-align: center;
        margin-bottom: 25px;
        margin-top: 25px;
    }
    .result{
        font-size: 28px;
    }
    form a{
        margin: auto;
    }
    .title_box{
        margin-bottom: 25px;
    }

</style>