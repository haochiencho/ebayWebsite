select count(*) from((select sellerID from seller) union (select bidderID from bidder)) as user; 

select count(*) from item join location on item.locationID=location.locationID where BINARY(location)=BINARY('New York'); 

select count(*)
from (select count(*) as number
from category
group by itemID) as itemCount
where number=4; 

select itemID
from bid
where amount = (select max(amount)
from item
join bid on item.itemID=bid.itemID
where `started` < '2001-12-20 00:00:01' and `ends` > '2001-12-20 00:00:01'); 

select count(*)
from seller
where rating > 1000; 

select count(*) from seller join bidder on seller.sellerID=bidder.bidderID; 

select  COUNT(DISTINCT category)
from (category join bid on category.itemID=bid.itemID)
where amount > 100; 
