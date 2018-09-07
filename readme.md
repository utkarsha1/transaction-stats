#### PROBLEM ####

Given concurrent transactions posted to a service, implement endpoints to:

(i) Post a new transaction

(ii) Delete a transaction

(iii) Find latest statistics about posted transactions (default to the last 60 seconds)
Statistics include:
* SUM : sum of of all amounts for transactions posted over the last n seconds (default 60)
* AVERAGE : average of all amounts for transactions posted over the last n seconds (default 60)
* MAX : maximum amount for transactions posted over the last n seconds (default 60)
* MIN : maximum amount for transactions posted over the last n seconds (default 60)
* COUNT : total number of transactions posted over the last n seconds (default 60)

The service should serves RESTful API, adheres to single responsibility principles and is designed 
 to handle concurrent requests.

##### API supported #####

Transactions consist of a fixed amount and a timestamp. Amount is a BigDecimal
truncated to two decimal spaces using the ROUND_HALF_UP strategy. The timestamp is
stored in UTC and represents at what time a transaction occurred.

##### Sample Requests #####

(i) `POST /transactions`
Request JSON:
```json
{"amount":"83.31","timestamp":"2018-09-07T03:00:48.827Z"}
```
Response codes:
* 201 (Created) - Success, stored transaction
* 204 (No Content) - Old transaction, will not be part of statistics calculations
* 422 (Invalid input) - Transaction has a future date, cannot parse timestamp, cannot parse amount, etc.
(ii) `GET /statistics`
Response JSON
```json
 {"sum":"1869.55","avg":"46.74","max":"89.42","min":"2.11","count":40}
```
Response codes:
* 200 (OK) - Success

(iii) `DELETE /transactions`
Response codes:
* 204 (No Content) - Success

##### Technology Stack #####
* Spring Boot
* Gradle
* JUnit

#### Run commands ####
* Build Application
```groovy
./gradlew build
```
* Run unit tests
```groovy
./gradlew test
```

* Start the application:
```groovy
./gradlew bootRun
```

* Execute a GET /statistics request
```json
curl http://localhost:8080/statistics
{"sum":"0.00","avg":"0.00","max":"0.00","min":"0.00","count":0}
```

* Hit /health endpoint
```json
curl http://localhost:8080/health
Service is up!
```
