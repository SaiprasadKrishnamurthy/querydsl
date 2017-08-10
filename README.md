### Intent
My goal is to evaluate a library/framework that:
* Abstracts the Data Access.
* Gives me a flexible/typesafe, compiler friendly query building interface.
* Let me work with my Domain Models (Defined using standard techniques eg: JPA, spring-data etc).
* Additionally allowing Free Flowing Data Types (eg: Tuples) for Adhoc querying.
* Supports for all the capabilities offered by SQL (like group_by, aggregations etc).
* Has a modular architecture (ie different database/data access providers (JPA based, Mongo etc)) has to pluggable easily into the system.
* Has a consistent principle in a polyglot persistence environment - ie some models could be present in Mongo, some could be in RDBMS. I don't want the principle of querying to change.

### QueryDSL just does the job for me
* Found QueryDSL to be quite useful and ticks all the boxes for me.

### Unified Data Access
* I've written a thin wrapper on top of QueryDSL that wraps the underlying Query module in the form of a Template.
* Base class: QueryTemplate
* JPAQueryTemplate - JPA Based Queries
* MongoQueryTemplate - Mongo Based Queries.

### Examples are available at FunTest.java

### Relevant Articles:
- [Intro to Querydsl](http://www.baeldung.com/intro-to-querydsl)


