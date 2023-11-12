# QueryCostCalculator, Salam to all :D
## There are 2 parts: 
1) Create metadata
2) Code the cost calculator

>[!warning]
>The input query format **must** be as follows (taking care of where to put spaces):
> - For selection on 1 table:
>```plsql
>SELECT * FROM tableName WHERE attribute = "theAttribute"
>```
>- For selection on 2 tables (JOIN):
>```plsql
>SELECT * FROM tableName1,tableName2 WHERE tableName1.attribute = tableName2.attribute
>```
>- We will NOT put a semicolon at the end of the query (;)

>[!warning]
>We are not considering cost of PROJECT operation, so all selection on attributes will return same cost as selecting from * (wildcard operator)

>[!warning]
>- Did not use CS2 cost formula because our tables are only sorted (ordering field is) on primary keys, which does not contain same values.
>

>[!warning]
>- Did not use S5 cost formula because our tables are sorted on (ordering field is) on primary key (no clustering index), which does not contain the same values.




