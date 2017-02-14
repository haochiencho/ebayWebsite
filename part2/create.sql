CREATE TABLE location(locationID int PRIMARY KEY, location VARCHAR(256), longtitude double, latitude double, country VARCHAR(256));
CREATE TABLE seller(sellerID VARCHAR(128) PRIMARY KEY, rating int);
CREATE TABLE bidder(bidderID VARCHAR(512) PRIMARY KEY, rating int, locationID int);
CREATE TABLE item (itemID int PRIMARY KEY, name VARCHAR(256), currently numeric(15,2), buyPrice numeric(15,2), firstBid numeric(15,2), numberOfBids int, locationID int, started TIMESTAMP, ends TIMESTAMP, sellerID VARCHAR(128), description VARCHAR(4000));
CREATE TABLE category(itemID int, category VARCHAR(128), PRIMARY KEY(itemID, category));
CREATE TABLE bid(itemID int, bidderID VARCHAR(128), bidID int PRIMARY KEY, bidTime TIMESTAMP, amount int);
CREATE TABLE categoryList(itemID int PRIMARY KEY, categoryList VARCHAR(512));
