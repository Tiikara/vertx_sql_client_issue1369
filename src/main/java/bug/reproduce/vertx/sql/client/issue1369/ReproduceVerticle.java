package bug.reproduce.vertx.sql.client.issue1369;

import io.vertx.core.AbstractVerticle;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.Arrays;

public class ReproduceVerticle extends AbstractVerticle {

    public void start() {

        var poolOptions =
                new PoolOptions()
                        .setMaxSize(2)
                        .setShared(true)
                        .setName("PgMainPool");

        var pgConnectOptions =
                new PgConnectOptions()
                        .setHost("localhost")
                        .setPort(5432)
                        .setDatabase("test")
                        .setUser("postgres")
                        .setPassword("postgres");

        var pgPool = PgPool.pool(vertx, pgConnectOptions, poolOptions);

        System.out.println("withConnection: START");
        pgPool.withConnection(sqlConnection ->
                        sqlConnection
                                .begin()
                                .compose(transaction -> {
                                    System.out.println("query(\"INSERT INTO users (name) VALUES ('test')\"): START");
                                    return sqlConnection
                                            .query("INSERT INTO users (name) VALUES ('test')")
                                            .execute()
                                            .compose(res2 -> {
                                                System.out.println("query(\"INSERT INTO users (name) VALUES ('test')\"): SUCCESS");

                                                System.out.println("query(\"SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): START");
                                                return sqlConnection.query("SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling")
                                                        .execute()
                                                        .compose(res -> {
                                                            System.out.println("query(\"SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): SUCCESS");

                                                            System.out.println("query(\"INSERT INTO users (name) VALUES ('test')\"): START");
                                                            return sqlConnection
                                                                    .query("INSERT INTO users (name) VALUES ('test')")
                                                                    .execute()
                                                                    .compose(res3 -> {
                                                                        System.out.println("query(\"INSERT INTO users (name) VALUES ('test')\"): SUCCESS");

                                                                        System.out.println("query(\"RELEASE SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): START");
                                                                        return sqlConnection.query("RELEASE SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling")
                                                                                .execute()
                                                                                .compose(res4 -> {
                                                                                    System.out.println("query(\"RELEASE SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): SUCCESS");

                                                                                    System.out.println("commit(): START");
                                                                                    return transaction
                                                                                            .commit()
                                                                                            .onSuccess(tranRes -> {
                                                                                                System.out.println("commit(): SUCCESS");
                                                                                            })
                                                                                            .onFailure(tranRes -> {
                                                                                                System.out.println("commit(): FAIL: " + tranRes.toString());
                                                                                            });
                                                                                })
                                                                                .onFailure(fail -> {
                                                                                    System.out.println("query(\"RELEASE SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): FAIL: " + fail.getMessage());
                                                                                });
                                                                    }, res5 -> {
                                                                        System.out.println("query(\"INSERT INTO users (name) VALUES ('test')\"): FAIL. That was planned fail on unique index: " + res5.getMessage());
                                                                        System.out.println("Now we should rollback savepoint and commit transaction");

                                                                        System.out.println("query(\"ROLLBACK TO SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): START");
                                                                        return sqlConnection.query("ROLLBACK TO SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling")
                                                                                .execute()
                                                                                .compose(res4 -> {
                                                                                    System.out.println("query(\"ROLLBACK TO SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): SUCCESS");

                                                                                    System.out.println("query(\"RELEASE SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): START");
                                                                                    return sqlConnection.query("RELEASE SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling")
                                                                                            .execute()
                                                                                            .compose(res6 -> {
                                                                                                System.out.println("query(\"RELEASE SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): SUCCESS");

                                                                                                System.out.println("commit(): START");
                                                                                                return transaction
                                                                                                        .commit()
                                                                                                        .onSuccess(tranRes -> {
                                                                                                            System.out.println("commit(): SUCCESS");
                                                                                                        })
                                                                                                        .onFailure(tranRes -> {
                                                                                                            System.out.println("commit(): FAIL: " + tranRes.toString());
                                                                                                        });
                                                                                            })
                                                                                            .onFailure(fail -> {
                                                                                                System.out.println("query(\"RELEASE SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): FAIL: " + fail.getMessage());
                                                                                            });
                                                                                })
                                                                                .onFailure(fail -> {
                                                                                    System.out.println("query(\"ROLLBACK TO SAVEPOINT AsyncSqlConnectionPgSavepointWithErrorHandling\"): FAIL: " + fail.getMessage());
                                                                                });
                                                                    });
                                                        });
                                            });
                                })
                                .onSuccess(success -> {
                                    System.out.println("begin: SUCCESS");
                                })
                                .onFailure(fail -> {
                                    System.out.println("begin: FAIL: " + fail.toString());
                                })
                )
                .onSuccess(success -> {
                    System.out.println("withConnection: SUCCESS");
                })
                .onFailure(fail -> {
                    System.out.println("withConnection: FAIL: " + fail.toString());
                });
    }
}
