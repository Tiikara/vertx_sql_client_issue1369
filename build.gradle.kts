plugins {
    id("java")

    application
}

group = "bug.reproduce.vertx.sql.client.issue1369"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.vertx:vertx-stack-depchain:4.4.6"))
    implementation("io.vertx:vertx-core")
    implementation("io.vertx:vertx-pg-client")

    // java.lang.NoClassDefFoundError: com/ongres/scram/common/exception/ScramException
    implementation("com.ongres.scram:common:2.1")
    implementation("com.ongres.scram:client:2.1")

    runtimeOnly("org.postgresql:postgresql:42.6.0")
}

application {
    mainClass.set("bug.reproduce.vertx.sql.client.issue1369.App")
}
