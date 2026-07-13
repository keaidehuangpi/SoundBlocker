import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.pack200.java.Pack200Adapter
import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("gg.essential.loom")
    id("io.github.juuxel.loom-quiltflower")
    id("dev.architectury.architectury-pack200")
    id("com.github.johnrengelman.shadow")
    kotlin("jvm") version "1.9.25"
}

group = "dev.dumatech"
version = "1.0.0"

loom {
    runConfigs {
        named("client") {
            ideConfigGenerated(true)

        }
    }

    launchConfigs {
        getByName("client") {
            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
        }
    }

    forge {
        pack200Provider.set(Pack200Adapter())
    }
}

val embed: Configuration by configurations.creating
configurations.implementation.get().extendsFrom(embed)

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")
    compileOnly("gg.essential:essential-1.8.9-forge:4955+g395141645")
    embed("gg.essential:loader-launchwrapper:1.1.3")
    embed("gg.essential:vigilance:306")
    embed("gg.essential:universalcraft-1.8.9-forge:505")
    implementation(kotlin("stdlib-jdk8"))
}

repositories {
    maven("https://repo.essential.gg/repository/maven-public")
    mavenCentral()
}

tasks {
    jar {
        manifest.attributes(
            mapOf(
                "ModSide" to "CLIENT",
                "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
            )
        )
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("dev")
        configurations = listOf(embed)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest.attributes(
            mapOf(
                "ModSide" to "CLIENT",
                "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
            )
        )

        relocate("gg.essential.vigilance", "top.huiwow.relocated.vigilance")
        relocate("gg.essential.elementa", "top.huiwow.relocated.elementa")
        relocate("gg.essential.universal", "top.huiwow.relocated.universal")
    }

    named<RemapJarTask>("remapJar") {
        val shadowJar = named<ShadowJar>("shadowJar")
        dependsOn(shadowJar)
        input.set(shadowJar.flatMap { it.archiveFile })
    }

    processResources {
        inputs.property("version", project.version)
        inputs.property("mcversion", "1.8.9")
        filesMatching("mcmod.info") {
            expand("version" to project.version, "mcversion" to "1.8.9")
        }
    }

    withType<JavaCompile> {
        options.release.set(8)
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    jvmTarget.set(JvmTarget.JVM_1_8)
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.compilerOptions {
    jvmTarget.set(JvmTarget.JVM_1_8)
}

// Fix: Forge's DirectoryDiscoverer with ASM 5.2 cannot parse Kotlin 1.9.x class files.
// Load the mod from the remapped JAR instead of raw build/classes directory.
val runClient by tasks.getting(JavaExec::class) {
    dependsOn("remapJar")
    val remapJarTask = tasks.named<RemapJarTask>("remapJar")
    classpath = classpath.filter { file ->
        !file.absolutePath.contains("build${File.separator}classes") &&
        !file.absolutePath.contains("build${File.separator}resources")
    } + files(remapJarTask.flatMap { it.archiveFile })
}
