# Vertx Text Test

This is the repo that contains the solution of Vertx Test Test

## Prerrequisites

In order to run this you need to have installed
- Maven
- Docker

## Preconfiguration

1. Use `mvn clean install` to download all the necessary dependencies
2. Execute `docker compose -f docker-compose-vertx-test.yaml up` to launch the redis and mysql containers used
3. Execute the app on the Main class

## Notes
1. The execersice told that you will receive the values on the `/analyuze` route so I've made it in that way
2. The execersie told that you need to perform an analysis based on the list previously stored, but also that it will be possible to in a future need that list later so I've decided to add a key called `wordListId` to send the ID of the list that you want to analyze

## Endpoints

### /
Just a Hello World Endpoint

### /analyze

#### Type: POST

#### Sending an array
If you want to store an array simply send a Json Array in the body.
The response will be a json with `wordListId` that contains an UUID related to the list you previously saved

Request:
```shell
curl --location --request POST 'http://localhost:8080/analyze' \
--header 'Content-Type: application/json' \
--data-raw '["hola","juan","zendaya"]'
```
Response:
```json
{
    "wordListId": "1b93510a-4b67-4ce2-a22a-cf3e9a14092b"
}
```

#### Analyzing a word

To analyze a word the request must contain the keys:
1. `word`: The word that you want to analyze
2. `wordListId`: The id of the List that you want to perform the analysis on.

```shell
curl --location --request POST 'http://localhost:8080/analyze' \
--header 'Content-Type: application/json' \
--data-raw '{
    "text":"kilo",
    "wordListId":"1b93510a-4b67-4ce2-a22a-cf3e9a14092b"
}'
```

Response:
```json
{
    "value": "juan",
    "lexical": "juan"
}
```