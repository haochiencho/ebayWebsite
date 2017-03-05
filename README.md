## Overall Goal: Build the ebay website


#### part 2: XML and mySQL database
Analyzed the XML data format from XML Data Type Definition file. Designed a relational schema and create mySQL relations. Used Java's XML parser to parse data and loaded data from xml files to mySQL database. 


#### part 3: mySQL JDBC, Java Lucene Index, and data migration
Queried our mySQL databases with JDBC. Built indexes on top of exists databases to allow efficient searches. Used Java Lucene Index to optimialize keyword bases searches while Utilizing mySQL features to construct a spatial index to support location based searches. Allowed easy migration of data by implementing a function to convert item into XML format.


#### part 4: Java Servlets, Java Server Pages, JavaScript, AJAX, DOM, Google Maps, Google Suggest
Implemented ebay search page and item page with Java Servlets(Controller) and Java Server Pages(View). Attached Javascript event listeners to asynchronously load suggests from google suggest server. Built a proxy server to support JavaScript's same-origin policy. Sent requests via the RESTful API as well as AJAX requests and added google maps API to display item location.