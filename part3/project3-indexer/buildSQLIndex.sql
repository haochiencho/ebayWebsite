CREATE TABLE itemLocation (itemID int) Engine=MyISAM;
ALTER TABLE itemLocation ADD coords POINT;

#LOAD DATA LOCAL INFILE 'itemLocation.csv' INTO TABLE itemLocation FIELDS TERMINATED BY '|*|' LINES TERMINATED BY '\n';

#SET coords = GeomFromText();
#CREATE SPATIAL INDEX itemSpatialIndex ON itemLocation(coords);