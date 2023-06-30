package com.vertxtexttest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertxtexttest.domain.HelloWorld;
import com.vertxtexttest.dto.FindWordDto;
import com.vertxtexttest.operators.WordFinderOperator;
import com.vertxtexttest.operators.WordListOperator;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class Server extends AbstractVerticle {

    public Server(){
    }

    @Override
    public void start(Promise<Void> start) throws Exception {

        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);

        WordListOperator wordListOperator = new WordListOperator(vertx);

        WordFinderOperator wordFinderOperator = new WordFinderOperator(vertx);

        router.route("/").handler(context -> {
            HttpServerResponse response = context.response();
                response
                        .putHeader("content-type","application/json")
                        .end(Json.encodePrettily(new HelloWorld()));
        });

        router.route(HttpMethod.POST,"/analyze").handler(context -> {
            context
                    .request()
                    .bodyHandler(bodyHandler -> {
                        String body = bodyHandler.toString();
                        if (body.startsWith("[")) {
                            var jsonArray = new JsonArray(body);
                            var words = jsonArray.getList();
                            wordListOperator.saveList(words, result -> {
                                context
                                        .response()
                                        .putHeader("content-type","application/json")
                                        .end(Json.encodePrettily(Map.of("wordListId", result.result())));

                            });
                        }
                        else {
                            JsonObject jsonObject = new JsonObject(body);

                            FindWordDto dto = jsonObject.mapTo(FindWordDto.class);

                            wordFinderOperator.findWords(dto.getText(),dto.getWordListId(), result -> {
                                context
                                        .response()
                                        .putHeader("content-type","application/json")
                                        .end(Json.encodePrettily(result.result()));
                            });
                        }
                    });
        });

        System.out.println("Starting");
        httpServer.requestHandler(router).listen(8080);
    }
}
