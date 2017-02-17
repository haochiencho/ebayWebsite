CREATE TABLE location(locationID int PRIMARY KEY, location VARCHAR(256) NULL, longtitude double NULL, latitude double NULL, country VARCHAR(256) NULL);
CREATE TABLE seller(sellerID VARCHAR(128) PRIMARY KEY, rating int);
CREATE TABLE bidder(bidderID VARCHAR(512) PRIMARY KEY, rating int, locationID int);
CREATE TABLE item (itemID int PRIMARY KEY, name VARCHAR(256), currently numeric(15,2), buyPrice numeric(15,2), firstBid numeric(15,2), numberOfBids int, locationID int, started TIMESTAMP, ends TIMESTAMP, sellerID VARCHAR(128), description VARCHAR(4000));
CREATE TABLE category(itemID int, category VARCHAR(128), PRIMARY KEY(itemID, category));
CREATE TABLE bid(itemID int, bidderID VARCHAR(128), bidID int PRIMARY KEY, bidTime TIMESTAMP, amount numeric(15,2));
CREATE TABLE categoryList(itemID int PRIMARY KEY, categoryList VARCHAR(512));
CREATE TABLE geoLocation(itemID int PRIMARY KEY, coords POINT NOT NULL) ENGINE=MyISAM;
