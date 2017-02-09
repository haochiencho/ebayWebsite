#!/bin/bash

# Run the drop.sql batch file to drop existing tables
mysql CS144 < drop.sql

# Run the create.sql batch file to create the database and tables
mysql CS144 < create.sql

# Delete any csv files prior to running
rm -f *.csv


# Compile and run the parser to generate the appropriate load files
ant
ant run-all

# sort get unique inputs
sort -u 'sellerData.csv' -o 'sellerData.csv'
sort -u 'bidData.csv' -o 'bidData.csv'
sort -u 'bidderData.csv' -o 'bidderData.csv'
sort -u 'categoryData.csv' -o 'categoryData.csv'
#sort -u 'itemData.csv' -o 'itemData.csv'
#sort -u 'locationData.csv' -o 'locationData.csv'

# Run the load.sql batch file to load the data
mysql CS144 < load.sql

# Remove all temporary files
rm *.csv
