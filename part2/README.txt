Part B:
	1. Relations:
		location:
			locationID -> location, longtitude, latitude, country
		item:
			itemID -> name, currently, buyPrice, firstBid, numberOfBids, locationID, started, ends, sellerID, description
		category:
			itemID, category -> itemID, category (they formed on the category relation)
		bid:
			bidID -> itemId, bidderID, bidTime, amount
		bidder:
			bidderID -> rating, locationID
		seller:
			sellerID -> rating

	2. They are no non-trival dependencies within our relation because all keys contain all attributes in their closure.

	3. Yes all relations are in BCNF. There are no function dependencies that do not contain all attributes.

	4. Yes all relations are in 4-NF. There are no multi-valued dependencies in our relations.

Part C:
	./runLoad.sh takes about 1 minute 30 seconds and queries.sql runs instantaneously
