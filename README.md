# akka-monitor-demo

## Kamon Status page 

- Visit: `http://localhost:53599/#/`

## Akka http server

- GET: `curl "http://localhost:8088/users2"`
- POST: `curl "http://localhost:8088/users2" -H "Content-Type: application/json" -X POST -d '{"name": "123", "age":100, "countryOfResidence":"12"}`

## Jaeger page

- Visit: `http://localhost:16686/search`
- Install Jaeger and run

## MySQL

- database: `trace`
- table: `asset`
```sql
CREATE TABLE IF NOT EXISTS `asset`(
    `id` INT UNSIGNED AUTO_INCREMENT,
   `assetId` VARCHAR(100) NOT NULL,
   `assetCode` VARCHAR(100) NOT NULL,
   `assetName` VARCHAR(100) NOT NULL,
   `baseAssetCode` VARCHAR(100) NOT NULL,
   PRIMARY KEY ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```