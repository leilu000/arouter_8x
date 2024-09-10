package com.alibaba.android.arouter.register.launch

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 *
 */

abstract class ModifyClassesTask : DefaultTask() {

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {

        val jarOutput = JarOutputStream(
            BufferedOutputStream(
                FileOutputStream(
                    output.get().asFile
                )
            )
        )
        allJars.get().forEach { file ->
            println("handling " + file.asFile.absolutePath)
            val jarFile = JarFile(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                println("Adding from jar ${jarEntry.name}")
                try {
                    jarOutput.putNextEntry(JarEntry(jarEntry.name))
                    jarFile.getInputStream(jarEntry).use {
                        it.copyTo(jarOutput)
                    }
                    jarOutput.closeEntry()
                } catch (_: Exception) {
                }

            }
            jarFile.close()
        }
        allDirectories.get().forEach { directory ->
            println("handling " + directory.asFile.absolutePath)
            directory.asFile.walk().forEach { file ->
                if (file.isFile) {

                    val relativePath = directory.asFile.toURI().relativize(file.toURI()).getPath()
                    println(
                        "Adding from directory ${
                            relativePath.replace(
                                File.separatorChar,
                                '/'
                            )
                        }"
                    )
                    jarOutput.putNextEntry(JarEntry(relativePath.replace(File.separatorChar, '/')))
                    file.inputStream().use { inputStream ->
                        inputStream.copyTo(jarOutput)
                    }
                    jarOutput.closeEntry()
                }
            }
        }
        jarOutput.close()

    }
}


class LogisticsCenterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        if (isApp) {
            val androidCompoments =
                project.extensions.findByType(AndroidComponentsExtension::class.java)
            androidCompoments?.onVariants { variant ->
                val taskProvider =
                    project.tasks.register(
                        "${variant.name}ModifyClasses",
                        ModifyClassesTask::class.java
                    )
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.ALL)
                    .use<ModifyClassesTask>(taskProvider)
                    .toTransform(
                        type = ScopedArtifact.CLASSES,
                        inputJars = ModifyClassesTask::allJars,
                        inputDirectories = ModifyClassesTask::allDirectories,
                        into = ModifyClassesTask::output
                    )

            }
        }
    }
}