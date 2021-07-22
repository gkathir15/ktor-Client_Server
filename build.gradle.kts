plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.google.cloud.tools.jib") version "2.7.0"
}

val main_class = "io.ktor.server.netty.EngineMain"
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))
    implementation(kotlin("reflect"))
    implementation("io.ktor:ktor-server-core:1.5.4")
    implementation("io.ktor:ktor-server-cio:1.5.4")
    implementation("io.ktor:ktor-client-core:1.5.4")
    implementation("io.ktor:ktor-client-cio:1.5.4")
    implementation("com.google.code.gson:gson:2.8.7")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
}
application {
    mainClassName = main_class

    applicationDefaultJvmArgs = listOf(
        "-server",
        "-Djava.awt.headless=true",
        "-Xms128m",
        "-Xmx256m",
        "-XX:+UseG1GC",
        "-XX:MaxGCPauseMillis=100"
    )
}

// The projectId can be overridden by adding a `-P projectId=...` flag
// at the comment line.
val projectId = project.findProperty("projectId") ?: "singular-elixir-318114"
val image = "gcr.io/$projectId/ktorreddit"

jib {
    to.image = image

    container {
        ports = listOf("8080")
        mainClass = main_class

        // good defauls intended for Java 8 (>= 8u191) containers
        jvmFlags = listOf(
            "-server",
            "-Djava.awt.headless=true",
            "-XX:InitialRAMFraction=2",
            "-XX:MinRAMFraction=2",
            "-XX:MaxRAMFraction=2",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100",
            "-XX:+UseStringDeduplication"
        )
    }
}

val deploy by tasks.registering(Exec::class) {
    commandLine = "gcloud run deploy singular-elixir-318114 --image $image --project $projectId --platform managed --region us-central1".split(" ")
    logger.info(commandLine.toString())
    dependsOn += tasks.findByName("jib")
}