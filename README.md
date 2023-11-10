- To make changes:
1) "pull" the repo to your local machine by:
```
git pull <yourRemoteName> master 
```
2) then to push your changes to here:
```
git push <yourRemoteName>
```     
# QueryCostCalculator, Salam to all :D
## There are 2 parts: 
1) Create metadata
2) Code the cost calculator 
## create metadata:
- need to create metadata for the tables and records
## code the cost calculator:
- input: a query
- output:
  - 1st line for printing general query info/stats
  - rest of the lines for printing different enumerations of plans, and their cost 

Checklist (note: there doesn't need to be a class for each, and you may add any additional thing to the checklist):

1) ~create tables~
2) ~Class for main menu to write the query (user interface) also has utility function for displaying stats for relations~
3) Class for reading the database (JDBC)
4) Class for parsing the query
5) Class for enumerating each query plan
6) Class for calculating the cost of a query plan using metadata
7) print the query plan plus the cost




