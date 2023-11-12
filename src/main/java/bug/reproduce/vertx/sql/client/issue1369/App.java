package bug.reproduce.vertx.sql.client.issue1369;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class App {
    public static void main(String[] args) {
        var vertxOptions = new VertxOptions();

        var vertx = Vertx.vertx(vertxOptions);
        vertx.deployVerticle(ReproduceVerticle.class, new DeploymentOptions())
                .onFailure(fail -> {
                    System.out.println("deployVerticle: FAIL: " + fail.toString());
                });
    }

}
