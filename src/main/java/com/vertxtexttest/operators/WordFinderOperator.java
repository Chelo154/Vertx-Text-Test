package com.vertxtexttest.operators;

import com.vertxtexttest.domain.Word;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.Response;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

import java.util.*;
import java.util.stream.Collectors;

public class WordFinderOperator {

    private final RedisAPI redisClient;

    public WordFinderOperator(Vertx vertx){
        RedisOptions options = new RedisOptions()
                .setEndpoints(List.of(RedisOptions.DEFAULT_ENDPOINT));

        this.redisClient = RedisAPI.api(Redis.createClient(vertx,options));
    }

    public void findWords(String word, String wordListId, Handler<AsyncResult<Map<String,String>>> resultHandler){
        this.redisClient.get(wordListId).onSuccess(result -> {

            var wordToAnalyze = new Word(word);

            var words = Arrays.stream(result
                    .toString()
                    .split(","))
                    .collect(Collectors.toList());

            var minimunPowerWord = words
                    .parallelStream()
                    .map(Word::new)
                    .min(Comparator.comparing(item -> item.calculateDelta(wordToAnalyze)))
                    .map(Word::getName)
                    .orElse("");

            var minimunLexicalWord = findClosestLexicalWord(words,word)
                    .orElse("");

            Map<String, String> wordsAnalysisResult = Map.of(
                    "value",minimunPowerWord,
                    "lexical",minimunLexicalWord
            );

            resultHandler.handle(Future.succeededFuture(wordsAnalysisResult));
        });
    }

    private Optional<String> findClosestLexicalWord(List<String> words, String word){
        String rightWord = null, leftWord = null;
        var idealPosition = Collections.binarySearch(words,word);

        if (idealPosition < 0) {
            idealPosition = -idealPosition - 1;
        }

        if (idealPosition - 1 >= 0) {
            leftWord = words.get(idealPosition - 1);
        }

        if (idealPosition < words.size()) {
            rightWord = words.get(idealPosition);
        }

        // Determinar cuál de las palabras es la más cercana
        String closestWord = null;

        if (rightWord != null && leftWord != null) {
            // Calcular la distancia en el diccionario entre las dos palabras
            int rightDistance = Math.abs(word.compareTo(rightWord));
            int leftDistance = Math.abs(word.compareTo(leftWord));

            if (rightDistance <= leftDistance) {
                closestWord = rightWord;
            } else {
                closestWord = leftWord;
            }
        } else if (rightWord != null) {
            closestWord = rightWord;
        } else if (leftWord != null) {
            closestWord = leftWord;
        }

        return Optional.ofNullable(closestWord);
    }
}
