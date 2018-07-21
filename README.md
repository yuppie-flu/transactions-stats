## Transactions Statistics service

### Implementation tech stack

* Java 8
* [Spring Boot 2.0](https://spring.io/projects/spring-boot)
* [Lombok](https://projectlombok.org/)
* [Maven](https://maven.apache.org/)

To run all tests:
```
mvn clean test
```

### Implementation details

Transactions data are stored in an array of buckets. Each bucket stores
aggregated data for all transactions happened during the specific millisecond of a minute.
The size of the array is 60K elements. When a transaction from the next minute arrives, it
overwrites all old stats in the corresponding bucket.
A method for counting statistics use only buckets with fresh data
(`lastAddedTimestamp` <= 60 seconds).

This implementation guaranties O(1) memory usage cause the array size is fixed.
Time performance of '/transactions' method is O(1), cause the solution uses only one
`ArrayList.get(index)` call.
Performance of `/statistics` is also O(1), cause it requires a loop the constant size array.

There are some unclear things in the task description. I found that only at
Saturday, so I was not able to clarify this and made decisions myself.

* `double` type is used to work transaction amount everywhere. However it causes
inaccurate `sum` and `avg` computation. Again, looks like it's not critical 
for the service. Specifying some fixed precision at the service level and
using `long` instead should solve this issue if required.

* The service returns `400 Bad Request` for a transaction with a timestamp in the future.
