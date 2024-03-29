import dev.clojurephant.plugin.clojure.tasks.ClojureNRepl

plugins {
    id("dev.clojurephant.clojure") version "0.8.0-beta.1"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "gi.am.server"
version = "0.1.0-SNAPSHOT"

val spring_version = "6.0.9"

repositories {
    mavenCentral()
    maven("https://repo.clojars.org")
}

dependencies {
    implementation("org.clojure:clojure:1.11.1")
    implementation("integrant:integrant:0.8.0")
    implementation("com.graphql-java:graphql-java:20.2")
    implementation("org.clojure:java.data:1.0.95")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.springframework:spring-aop:$spring_version")
    implementation("org.springframework:spring-beans:$spring_version")
    implementation("org.springframework:spring-context:$spring_version")
    implementation("org.springframework:spring-core:$spring_version")
    implementation("org.springframework:spring-expression:$spring_version")
    implementation("org.springframework:spring-jcl:$spring_version")
    implementation("org.springframework:spring-web:$spring_version")
    implementation("org.springframework:spring-webflux:$spring_version")
    implementation("io.projectreactor.netty:reactor-netty:1.1.7")
    implementation("io.projectreactor:reactor-core:3.5.6")
    implementation("com.github.seancorfield:honeysql:2.4.1033")
    implementation("org.mariadb:r2dbc-mariadb:1.1.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("metosin:jsonista:0.3.7")
    testRuntimeOnly("dev.clojurephant:jovial:0.4.1")
    testImplementation("com.google.guava:guava:31.1-jre")


    implementation("com.github.seancorfield:next.jdbc:1.3.874")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")

    devImplementation("migratus:migratus:1.5.1")
    
    devImplementation("integrant:repl:0.3.2")
    devImplementation("org.clojure:tools.namespace:1.3.0")
    devImplementation("jonase:eastwood:1.4.0")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(project.the<SourceSetContainer>()["main"].allSource)
}

tasks.withType<ClojureNRepl>() {
    forkOptions.jvmArgs = listOf("-Djava.net.preferIPv4Stack=true")
}

task<Exec>("cljKondoLint") {
    commandLine("clj-kondo", "--lint", ".")
}

task<Exec>("cljKondoFix") {
    commandLine("clj-kondo", "--lint", ".")
}