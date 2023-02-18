# Hierarchies showcase API

## Requirements

For building and running the application you need:

- [JDK 11](https://www.oracle.com/nl/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `de.codecentric.springbootsample.Application` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Brief description

This API represents a hierarchical structure of teams within a company. It has a complete CRUD of all hierarchical elemets.

## Components

The API is build up of several components to represent the hierarchical structure. In the full structure there is a hierarchy chain
that consists of a top manager, which is always 1 person. The manager can manage a number of other managers and a number of teams.
Consequently all managers underneath that managers can by themselves manage multiple teams and managers. I team consists of
a team lead and a number of team members. The hierarchy can go as deep as is required recursively. When displaying the hierarchy
the API can filter on. The API can also set a limit and offset on the teams displayed. 

The components with different collections in MongoDB and as such a full CRUD functionality are: employees, roles, team-types
and teams

Embedded mongoDB is used for data storage, so that it can be easily run from anyone without extra setup beyond running 
the project with Springboot.

Swagger link once the API is running: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
Embedded mongo connection: [localhost:27018](localhost:27018)