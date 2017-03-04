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
            <input list="suggestions" name="q" class="col-md-8 col-md-offset-2" id="search_box" autocomplete="off"
                   placeholder=<%= request.getAttribute("placeholder") %> >
            <datalist onchange="this.form.submit()" id="suggestions">
                <option value="Maine">
                <option value="Maryland">
                <option value="Massachusetts">
            </datalist>
            <input type="submit" value="Submit"> <br>
        </form>
        <div class="suggestions" style="display: none;">
            <div class="current">Maine</div>
            <div>Maryland</div>
            <div>Massachusetts</div>
            <div>Michigan</div>
            <div>Minnesota</div>
            <div>Mississippi</div>
            <div>Missouri</div>
            <div>Montana</div>
        </div>
    </div>

    <div class="result_box">
    <%@ page import="java.util.ArrayList" %>
    <% ArrayList<String> list = (ArrayList<String>) request.getAttribute("result"); %>
    <% for (int i = 0; i < list.size(); i++) { %>
        <div class="result well col-md-8 col-md-offset-2 medium-font">
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
                <input type="submit" class="next-btn btn-primary col-md-1 col-md-offset-9" value="Next" style="visibility: hidden;"> <br>
            </form>
        </div>
    </div>
    <pre id="suggestion" style="visibility: hidden;"></pre>

    <%--<div>Debug: <%= request.getAttribute("debug") %></div>--%>
    <script>

        // auto suggest drop down menu
        function AutoSuggestControl(oTextbox, oProvider, div, utilCtrl) {
            this.layer = div;
            this.provider = oProvider;
            this.textbox = oTextbox;
            this.utilCtrl = utilCtrl;
        };


        AutoSuggestControl.prototype.showSuggestions = function () {
            this.layer.style.visibility = "visible";
        };

        AutoSuggestControl.prototype.hideSuggestions = function () {
            this.layer.style.visibility = "hidden";
        };

        AutoSuggestControl.prototype.highlightSuggestion = function (oSuggestionNode) {

            for (var i=0; i < this.layer.childNodes.length; i++) {
                var oNode = this.layer.childNodes[i];
                if (oNode == oSuggestionNode) {
                    oNode.className = "current"
                } else if (oNode.className == "current") {
                    oNode.className = "";
                }
            }
        };

        AutoSuggestControl.prototype.createDropDown = function (suggestions) {
            // delete children nodes
            this.utilCtrl.deleteChildren(this.layer);

            if(suggestions.length > 0){
                this.layer.style.visibility = "visible";
                for (i = 0; i < Math.min(suggestions.length, 7);i++) {
                    var suggestion = this.utilCtrl.getData(suggestions, i);
                    var html = '<div class="drop_down_node';
                    if(i === 0)
                        html += ' current';
                    html += '">' + suggestion + '</div>';
                    this.layer.insertAdjacentHTML( 'beforeend', html);
                }
            } else{
                this.layer.style.visibility = "hidden";
            }

            this.layer.style.width = this.textbox.offsetWidth;

            // always hides
            this.layer.style.visibility = "hidden";
        };

        var htmlUtiltyController = (function(){
            return {
                getData: function(suggestionArr, index){
                    return suggestionArr[index].getAttribute('data');
                },

                deleteChildren: function(el){
                    while (el.firstChild) {
                        el.removeChild(el.firstChild);
                    }
                }
            }
        })();

        var UIcontroller = (function(utilCtrl){

            function redirectQuery(inputQuery){
                var url = window.location.href;
                var urlArr = url.split('?');
                console.log(urlArr[0]);

                window.location.replace(urlArr[0] + '?' + encodeURI(inputQuery));
            }

            function eventListenerHelper(event){
                if (event.keyCode === 13) {
                    // enter key
                    var query = document.getElementById('search_box').value;
                    redirectQuery('numResultsToSkip=0&numResultsToReturn=20&q=' + query);
                }
                if (event.keyCode === 38){
                    // up key
                    let node = document.querySelector('.current');
                    if(node.previousElementSibling !== null){
                        node.previousElementSibling.classList.add('current');
                        node.classList.remove('current');
                    }
                }
                if(event.keyCode === 40){
                    // down key
                    let node = document.querySelector('.current');
                    if(node.nextElementSibling !== null) {
                        node.nextElementSibling.classList.add('current');
                        node.classList.remove('current');
                    }
                }
                event.preventDefault();
            }

            var addEventSearch = function(){
                document.addEventListener('keyup', function(event){
                    eventListenerHelper(event);
                });

                document.getElementById('search_box').addEventListener('keyup', function(event){
                    eventListenerHelper(event);
                });
            };

            var initEventListeners = function(){
                document.addEventListener('keyup', function(event) {
                    if(event.keyCode !== 13 && event.keyCode !== 38 && event.keyCode !== 40)
                        sendAjaxRequest();
                });
                addEventSearch();
            };

            var addEventSearchClick = function(query){
                console.log(query);

                var nodes = document.querySelectorAll(query);

                for(let node of nodes){
                    node.addEventListener('click', function(event){
                        var query = event.target.textContent;
                        redirectQuery('numResultsToSkip=0&numResultsToReturn=20&q=' + query);
                    });
                }

            };

            function newDropDown(suggestions){
                var parentNode = document.getElementById('suggestions');

                // delete children nodes
                while (parentNode.firstChild) {
                    parentNode.removeChild(parentNode.firstChild);
                }

                for (i = 0; i < Math.min(suggestions.length, 7);i++) {
                    var suggestion = suggestions[i].getAttribute('data');
                    var html = '<option value="' +
                        suggestion + '">';
                    parentNode.insertAdjacentHTML( 'beforeend', html);
                }
            }

            /**
             *
             * @param str           string to be placed in div
             */
            function displayResult(str){
                var DOM = document.querySelector('.result_box');

                // delete all children
                utilCtrl.deleteChildren(DOM);

                parser = new DOMParser();
                xmlDoc = parser.parseFromString(str,"text/xml");

                var suggestions = xmlDoc.getElementsByTagName('suggestion');

                if(suggestions.length > 0){
                    document.querySelector('.next-btn').style.visibility = 'visible';
                }

                for (i = 0; i < suggestions.length ;i++) {
                    var suggestion = utilCtrl.getData(suggestions, i);
                    var html = '<div class="result well col-md-8 col-md-offset-2 medium-font">' +
                        suggestion + '</div>';
                    DOM.insertAdjacentHTML( 'beforeend', html);
                }

                // auto suggest drop down menu
                var textBoxEl = document.getElementById('search_box');
                var dataProvider = "something";
                var div = document.querySelector('div.suggestions');
                var autoObj = new AutoSuggestControl(textBoxEl, dataProvider, div, utilCtrl);
                console.log(autoObj);
                autoObj.createDropDown(suggestions);

                // TODO: update with suggestions here
                newDropDown(suggestions);

                addEventSearchClick('.drop_down_node');
            }


            var sendAjaxRequest = function(){
                var query = document.getElementById('search_box').value;

                if(query !== ""){
                    var ajax_request = "/eBay/suggest?output=toolbar&q=" + encodeURI(query);

                    var xmlHttp = new XMLHttpRequest();

                    var showSuggestion = function(){
                        if(xmlHttp.readyState == 4) {
                            var response = xmlHttp.responseText;
                            document.getElementById("suggestion").interHTML = response;
                            displayResult(response);
                        }
                    };

                    xmlHttp.open("GET", ajax_request);
                    xmlHttp.onreadystatechange = showSuggestion;
                    xmlHttp.send();
                }
            };


            return {
                init: function(){
                    initEventListeners();
                }
            };
        })(htmlUtiltyController);

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
    div.suggestions {
        -moz-box-sizing: border-box;
        box-sizing: border-box;
        border: 1px solid black;
        position: absolute;
        z-index: -1;

    }
    div.suggestions div {
        cursor: default;
        padding: 0px 3px;
    }
    div.suggestions div.current {
        background-color: #3366cc;
        color: white;
    }

    .medium-font {
        font-size: 18px;
    }
</style>