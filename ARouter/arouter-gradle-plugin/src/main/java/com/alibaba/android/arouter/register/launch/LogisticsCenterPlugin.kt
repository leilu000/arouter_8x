package com.alibaba.android.arouter.register.launch

import com.alibaba.android.arouter.register.utils.ScanSetting
import com.alibaba.android.arouter.register.utils.ScanUtil
import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import groovy.transform.Internal
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
import java.io.InputStream
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

    @org.gradle.api.tasks.Internal
    val jarPaths = mutableSetOf<String>()

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
            val jarFile = JarFile(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                jarOutput.writeEntity(jarEntry.name, jarFile.getInputStream(jarEntry))
            }
            jarFile.close()
        }
        allDirectories.get().forEach { directory ->
            directory.asFile.walk().forEach { file ->
                if (file.isFile) {
                    val relativePath = directory.asFile.toURI().relativize(file.toURI()).path
                    jarOutput.writeEntity(
                        relativePath.replace(File.separatorChar, '/'),
                        file.inputStream()
                    )
                }
            }
        }
        jarOutput.close()
    }

    private fun JarOutputStream.writeEntity(name: String, inputStream: InputStream) {
        if (jarPaths.contains(name)) {
            printDuplicatedMessage(name)
        } else {
            inputStream.use {
                putNextEntry(JarEntry(name))
                if (name == ScanSetting.GENERATE_TO_CLASS_FILE_NAME) {
                    val data = ScanUtil.logisticsCenter(project.rootDir.absolutePath, inputStream)
                    write(data, 0, data.size)
                } else {
                    inputStream.copyTo(this)
                }
                closeEntry()
                jarPaths.add(name)
            }

        }
    }

    private fun printDuplicatedMessage(name: String) {
        //   println("Cannot add ${name}, because output Jar already has file with the same name.")
    }
}


class LogisticsCenterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        if (isApp) {
            val androidComponents =
                project.extensions.findByType(AndroidComponentsExtension::class.java)
            androidComponents?.onVariants { variant ->
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