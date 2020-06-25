# The Workforce Management API
------------------
The Workforce Management API is a platform for tracking who is in and out of the office and general management of sick leaves,
requests for vacations (paid and non-paid) and the respective approvals. 
The system orchestrates the workforce availability, tracking time offs, approvals and sick leaves.
## Prerequisites
------------------
First, need to pull two images from [docker hub](https://hub.docker.com/) to install project.

```bash
docker push xpresser/workforce-api:v0.1
docker push xpresser/workforce-database:v2.0

```
Next, create docker-compose.yml file
```yml
version: '3.5'
services:
  oracledb:
    container_name: oracleDb
    image: xpresser/workforce-database:v2.0
    shm_size: '3gb'
    ports:
        - 1521:1521
        - 5500:5500
    restart: always
    volumes:
        - oracleDbData:/opt/oracle/oradata
  api:
    restart: always
    image: xpresser/workforce-api:v0.1
    container_name: workforce-api
    environment:
      spring.datasource.driverClassName: "oracle.jdbc.OracleDriver"
      spring.datasource.url: "jdbc:oracle:thin:@oracledb:1521/ORACLEDB"
      spring.datasource.username: "java_soul"
      spring.datasource.password: "java_soul"
    ports:
        - 8080:8080
    depends_on:
        - oracledb
volumes:
    oracleDbData:
        external: true
```
## Installation
------------------
Execute docker-compose.yml file
```bash
docker volume create --name=oracleDbData
docker-compose up
```
once both servers are up you can access http://localhost:8080


## API documentation
------------------
For complete documentation of Workforce Management API, visit http://ec2-3-18-220-73.us-east-2.compute.amazonaws.com:8080/swagger-ui.html

## Built With
------------------
- [Spring Boot 2.2.7](https://spring.io/projects/spring-boot)
- [Oracle SQL 12.0.2](https://www.oracle.com/database/technologies/appdev/sqldeveloper-landing.html)
- [Docker](https://hub.docker.com/)
- [OpenAPI 3](https://springdoc.org/faq.html#how-can-i-map-pageable-spring-date-commons-object-to-correct-url-parameter-in-swagger-ui)
