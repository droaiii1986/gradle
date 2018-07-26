import org.gradle.kotlin.dsl.plugins.dsl.ProgressiveModeState
import org.gradle.kotlin.dsl.plugins.precompiled.PrecompiledScriptPlugins
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {

    val pluginsExperiments = "gradle.plugin.org.gradle.kotlin:gradle-kotlin-dsl-plugins-experiments:0.1.10"

    dependencies {
        classpath(pluginsExperiments)
    }

    project.dependencies {
        "compile"(pluginsExperiments)
    }

    configure(listOf(repositories, project.repositories)) {
        gradlePluginPortal()
    }
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    progressive.set(ProgressiveModeState.ENABLED)
}

apply(plugin = "org.gradle.kotlin.ktlint-convention")

apply<PrecompiledScriptPlugins>()

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xjsr305=strict",
            "-Xskip-runtime-version-check",
            "-Xskip-metadata-version-check"
        )
    }
}

dependencies {
    compileOnly(gradleKotlinDsl())

    compile(kotlin("gradle-plugin"))
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))

    compile("com.gradle.publish:plugin-publish-plugin:0.9.10")
    compile("org.ow2.asm:asm-all:5.1")

    testCompile("junit:junit:4.12")
    testCompile(gradleTestKit())
}
