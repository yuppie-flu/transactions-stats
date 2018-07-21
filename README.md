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

There are some unclear things in the task description. I found that only at
Saturday, so I was not able to clarify this and made decisions myself.

* Transactions data are stored in an array of buckets. Each bucket stores
aggregated data for all transactions for the specific second.
This implementation allows to have only 60 buckets, which should guarantee
good performance. However it causes `/statistics` endpoint precision to be
seconds, not milliseconds. For example:
If the service received a transaction with the timestamp `2018-07-21 16:00:00,999`,
the last time the transaction will be included into `/statistics` result is
`2018-07-21 16:00:59,999`.
A call to the `/statistics` endpoint at `2018-07-21 16:01:00,000` will not include 
the mentioned transaction into a result response.

   This limitation can be fixed by increasing number of bucket to 60K. One bucket
in this case would store aggregated stats only for the specific millisecond.
However I assumed, that performance for this service is more important that
milliseconds precision.

* `double` type is used to work transaction amount everywhere. However it causes
inaccurate `sum` and `avg` computation. Again, looks like it's not critical 
for the service. Specifying some fixed precision at the service level and
using `long` instead should solve this issue if required.

* The service returns `400 Bad Request` for a transaction with a timestamp in the future.





