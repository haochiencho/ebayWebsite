<html>
<head>
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
            <input type="hidden" name="page" value=0>
            <input type="text" name="q" class="col-md-8 col-md-offset-2" placeholder=<%= request.getAttribute("placeholder") %>>
            <input type="submit" value="Submit"> <br>
        </form>
    </div>
    <div class="result_box">
        <div class="result well col-md-8 col-md-offset-2">
            One punch man
        </div>
        <div class="result well col-md-8 col-md-offset-2">
            Sword Art Online
        </div>
        <div class="result well col-md-8 col-md-offset-2">
            Yuri On Ice
        </div>
        <div class="next_row">
            <button type="button" class="next-btn btn-primary col-md-1 col-md-offset-9">Next</button>
        </div>
    </div>

    <h1>Result: <%= request.getAttribute("result") %></h1>

    <script>

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