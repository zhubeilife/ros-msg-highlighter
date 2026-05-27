plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "org.ros.clion"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    intellijPlatform {
        clion("2024.1")
        bundledPlugin("org.jetbrains.plugins.textmate")
    }
}

intellijPlatform {
    pluginConfiguration {
        version = project.version.toString()
    }
    instrumentCode = false
}

tasks.patchPluginXml {
    sinceBuild.set("241")
    untilBuild.set("243.*")
}

tasks {
    // Copy the TextMate bundle to the plugin's sandbox
    val copyTextmate by registering(Copy::class) {
        from(layout.projectDirectory.dir("src/main/resources/textmate")) {
            into("textmate")
        }
        into(layout.buildDirectory.dir("idea-sandbox/plugins/${project.name}/"))
    }

    // Ensure textmate bundle is copied before running the IDE
    runIde {
        dependsOn(copyTextmate)
    }

    buildSearchableOptions {
        enabled = false
    }
}