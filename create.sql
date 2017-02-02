CREATE TABLE location(locationID int PRIMARY KEY, location VARCHAR(256), longtitude double, latitude double, country VARCHAR(256));
CREATE TABLE seller(sellerID VARCHAR(128) PRIMARY KEY, rating int);
CREATE TABLE bidder(bidderID VARCHAR(128) PRIMARY KEY, rating int, locationID int);
CREATE TABLE item (itemID int PRIMARY KEY, name VARCHAR(128), currently numeric(15,2), firstBid numeric(15,2), buyPrice numeric(15,2), numberOfBids int, locationID int, started TIMESTAMP, ends TIMESTAMP, sellerID VARCHAR(128), FOREIGN KEY (locationID) REFERENCES location(locationID), FOREIGN KEY(sellerID) REFERENCES seller(sellerID));
CREATE TABLE category(itemID int, category VARCHAR(128), PRIMARY KEY(itemID, category), FOREIGN KEY (itemID) REFERENCES item(itemID));
CREATE TABLE bid(itemID int, bidderID VARCHAR(128), bidID int PRIMARY KEY, time TIMESTAMP, amount int, FOREIGN KEY (itemID) REFERENCES item(itemID), FOREIGN KEY(bidderID) REFERENCES bidder(bidderID));
