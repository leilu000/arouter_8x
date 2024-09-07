package com.alibaba.android.arouter.register.launch

import com.alibaba.android.arouter.register.core.RegisterCodeGenerator
import com.alibaba.android.arouter.register.utils.Logger
import com.alibaba.android.arouter.register.utils.ScanUtil
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
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 *
 */

abstract class ArouterASMTask : DefaultTask() {
    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val outputJarFile = output.asFile.get()
        Logger.i("output jar file:${outputJarFile.absolutePath}")

        val jarOutput = JarOutputStream(BufferedOutputStream(FileOutputStream(outputJarFile)))
        allJars.get().forEach { file ->
            val jarFile = JarFile(file.asFile)
            ScanUtil.scanJar(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                try {
                    jarOutput.putNextEntry(JarEntry(jarEntry.name))
                    jarFile.getInputStream(jarEntry).use {
                        it.copyTo(jarOutput)
                    }
                } catch (_: Exception) {
                } finally {
                    jarOutput.closeEntry()
                }
            }
            jarFile.close()
        }
        allDirectories.get().forEach { directory ->
            directory.asFile.walk().forEach { file ->
                if (file.isFile) {
                    val path = file.absolutePath.replace("\\", "/")
                    if (ScanUtil.shouldProcessClass(path)) {
                        ScanUtil.scanClass(file.inputStream(), file.absolutePath)
                    }
                    try {
                        jarOutput.putNextEntry(JarEntry(file.name))
                        file.inputStream().use {
                            it.copyTo(jarOutput)
                        }
                    } catch (_: Exception) {
                    } finally {
                        jarOutput.closeEntry()
                    }

                }
            }
        }

        jarOutput.close()

        Logger.i("${javaClass.simpleName} outputJarFile:" + outputJarFile.absolutePath)
        ScanUtil.registerList.forEach { ext ->
            if (ext.classList.isEmpty()) {
                Logger.i("No class implements found for interface:" + ext.interfaceName)
            } else {
                RegisterCodeGenerator.insertInitCodeTo(ext, outputJarFile)
            }
        }
    }
}

class ArouterASMPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            return
        }
        val androidComponent = project.extensions.findByType(AndroidComponentsExtension::class.java)
        androidComponent?.onVariants { va ->
            val testTask =
                project.tasks.register(
                    "hsbcArouter${va.name}ModifyClass",
                    ArouterASMTask::class.java
                )
            va.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                .use(testTask)
                .toTransform(
                    type = ScopedArtifact.CLASSES,
                    inputJars = ArouterASMTask::allJars,
                    inputDirectories = ArouterASMTask::allDirectories,
                    into = ArouterASMTask::output
                )
        }
    }
}