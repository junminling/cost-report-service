# cost-report-service
This is the core api to process itemized cost and generate and store consolidated cost report
 
 ## Project setup instructions:
 * clone the repository using the command and checkout develop branch
 
       git clone git@github.com:junminling/cost-report-service.git
       git checkout develop
 
 * Once cloned, run a maven build either from your IDE or use below command:

        mvn clean install
        java -jar target/cost-report-service-0.0.1-SNAPSHOT.jar 
 
* Once application is started type the url in a browser:
 
      http://localhost:8080/swagger-ui.html
      
* NOTE* Make sure no other application is using port 8080.
 
 ## Toolset:
  1) JDK 11.0.3
  2) Spring Boot v2.4.3
  4) spring-boot-starter-data-jpa v2.4.3 (Spring data JPA defaults to hibernate)
  5) springfox-swagger-ui/springfox-swagger2 v2.9.4
  
  ### APIs
   * POST /api/report/production/{production}/ep (process single EP cost report)
   * GET api/report/production/{production}?epCode={epCode} (get aggregated cost report given production name and EP number)
   * POST /api/report/production/{production} (process production cost report)
   * GET api/report/production/{production} (get aggregated production cost report)
   * POST /api/report/production/{production}/amortized (process production cost report with amortized costs)
   * GET api/report/production/{production} (get aggregated final production cost report including amortized costs)

**NOTE:** I strongly recommend using swagger-ui to see the complete API documentation.

## Error Scenarios and Exception Handling:

Customized InvalidProductionException and InvalidReportException are created to log all application exceptions generated
by the service during request handling.
 Error scenarios includes:
  1) get aggregated cost report by invalid production name (404 status code)
  2) get cost report by invalid production/EP name combination (404 status code)
  3) post request with empty {production} path param in URL (400 status code)
  4) single EP Cost input payload contains more than one epCode (400 status code)
  5) production cost input payload is empty (400 status code)


# Database:

Java In-memory h2 database is used. During start up table create scripts are run and sample data set inserted.

## Logging:

slf4j is used for logging. 


## Documentation:

 swagger-ui
 
## DataModel:
 
 The datamodel has 1 table:
  1) EpTotalCostEntity (store aggregated cost on production EP level)

    long id;    (auto-generated id as primary key)
  	String prodName;  (production name, could be used as foreign key to join with proudction table to get production details)
  	String epCode;  (epCode)
  	double itemizedTotalCost;   (aggregated itemized total cost)
  	double amortizedCost;   (calculated amortized cost for this proudction and epCode)
  	double totalCost;   (itemizedTotalCost + amortizedCost)
  	long timestamp; (used for version control. we should always fetch the cost with latest timestamp)
  
 Potentially we could add one more table to record each itemized cost from input data for historical data retrieval purpose:
  2) EPItemizedCostEntity 
  
    long itemizedId;
  	String prodName;
  	String epCode;
  	double itemizedCost;
  	long timestamp;

## System Design:

 Maintained a simple design with Controller, service and repository pattern. (CostReportController and EPCostReportService).
 
## Performance consideration:

1. In-memory database, in-memory processing for a single report. Fast for current needs
2. If in future we want to store each itemized cost from input data, we should take performance into consideration, like async DB persist, or bulkSave + multi-thread processing to save all itemized cost.
3. when data gets really big, we could consider to hash based on production  name and EP code to shard the DB into multiple partitions.
4. we could also consider caching some results for cost retrieval if needed.
 



## Maintainability:

Code is modularized and proper class level and method level comments included. Loose coupling of components enables easy modification of code.


