package com.vertxtexttest.operators;

import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordListOperator {

    private final RedisAPI redisClient;

    private final SqlClient mysqlClient;

    private final Logger logger;

    public WordListOperator(Vertx vertx){
        RedisOptions options = new RedisOptions()
                .setEndpoints(List.of(RedisOptions.DEFAULT_ENDPOINT));

        this.redisClient = RedisAPI.api(Redis.createClient(vertx,options));

        MySQLConnectOptions mySqlOptions = new MySQLConnectOptions()
                .setHost("localhost")
                .setPort(3306)
                .setUser("root")
                .setPassword("root")
                .setDatabase("VERTX_TEST");

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

        this.mysqlClient = MySQLPool.client(vertx,mySqlOptions,poolOptions);

        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public void saveList(List<String> words, Handler<AsyncResult<UUID>> resultHandler){
        UUID wordsId = UUID.randomUUID();

        List<String> sortedWords = words
                .parallelStream()
                .sorted()
                .collect(Collectors.toList());

        String wordsByComma = String.join(",",sortedWords);

        redisClient.set(List.of(wordsId.toString(),wordsByComma),successHandler -> {
             resultHandler.handle(Future.succeededFuture(wordsId));
        });

        sortedWords.forEach(word -> {
            String sql = "INSERT INTO WORDS (listId,word) VALUES (?,?)";

            Tuple preparedArguments = Tuple.of(wordsId,word);

             mysqlClient.preparedQuery(sql)
                    .execute(preparedArguments)
                    .onSuccess(result -> {
                        logger.info(String.format("%s saved", word));
                    });
        });


    }
}
