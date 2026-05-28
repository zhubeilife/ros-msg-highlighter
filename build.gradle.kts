plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

group = "org.ros.highlighter"
version = "1.0.4"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

val clionHome = "/home/zhangbeipc/.local/share/JetBrains/Toolbox/apps/clion"

dependencies {
    compileOnly(files("$clionHome/plugins/textmate-plugin/lib/modules/intellij.textmate.jar"))
    intellijPlatform {
        local(clionHome)
        bundledPlugin("org.jetbrains.plugins.textmate")
    }
}

intellijPlatform {
    pluginConfiguration {
        version = project.version.toString()
    }
    instrumentCode = false
    publishing {
        token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }
}

tasks.patchPluginXml {
    sinceBuild.set("243")
    untilBuild.set(provider { null })
}

tasks {
    // Textmate bundle must live at the plugin root (not inside the JAR) so that
    // PluginManagerCore.getPlugin(...).getPluginPath().resolve("textmate/ROS") resolves correctly.
    jar {
        exclude("textmate/**")
    }

    // prepareSandbox populates the sandbox directory; buildPlugin packages it into the ZIP.
    // Both runIde and buildPlugin depend on prepareSandbox, so adding the textmate dir here
    // ensures it is present in both the dev sandbox and the final distribution.
    prepareSandbox {
        from(layout.projectDirectory.dir("src/main/resources/textmate")) {
            into("${project.name}/textmate")
        }
    }

    buildSearchableOptions {
        enabled = false
    }
}
