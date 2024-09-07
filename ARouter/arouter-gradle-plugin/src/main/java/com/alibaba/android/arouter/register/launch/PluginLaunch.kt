package com.alibaba.android.arouter.register.launch

import com.alibaba.android.arouter.register.core.RegisterCodeGenerator
import com.alibaba.android.arouter.register.utils.Logger
import com.alibaba.android.arouter.register.utils.ScanUtil
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.tasks.TransformClassesWithAsmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File


/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 *
 */
class PluginLaunch : Plugin<Project> {

    private fun hookTransformTask(project: Project) {
        val app =
            project.extensions.getByType(AppExtension::class.java)
        app.applicationVariants.all { va ->
            val name = "transform${va.name.capitalized()}ClassesWithAsm"
            val task = project.tasks.findByName(name) as TransformClassesWithAsmTask

            task.doLast {
                val classesDir = task.classesOutputDir.get().asFile
                scanClassesDir(classesDir)
                val jarsDir = task.jarsOutputDir.get().asFile
                scanJarsDir(jarsDir)
                injectCodes()
            }
        }

    }

    private fun injectCodes() {
//        ScanUtil.mLogisticsCenterClassFile?.let { logisticsCenterClassFile ->
//            ScanUtil.registerList.forEach { ext ->
//                Logger.i("Insert register code to file ${logisticsCenterClassFile.absolutePath}")
//
//                if (ext.classList.isEmpty()) {
//                    Logger.e("No class implements found for interface:" + ext.interfaceName)
//                } else {
//                    ext.classList.forEach {
//                        Logger.i(it)
//                    }
//                    //RegisterCodeGenerator.insertInitCodeTo(ext)
//                }
//            }
//        }

    }

    private fun scanJarsDir(jarsDir: File) {
        if (jarsDir.isFile) {
            if (ScanUtil.shouldProcessPreDexJar(jarsDir.absolutePath)) {
                ScanUtil.scanJar(jarsDir)
            }
        } else {
            jarsDir.listFiles()?.forEach {
                scanClassesDir(it)
            }
        }
    }

    private fun scanClassesDir(file: File) {
        if (file.isFile) {
            ScanUtil.scanClass(file.inputStream(), file.absolutePath)
//            if (file.name.contains("TestActivity.class")) {
//                val byteCode = HelloWorldClassVisitor.modify(file)
//                file.outputStream().write(byteCode)
//            }
        } else {
            file.listFiles()?.forEach {
                scanClassesDir(it)
            }
        }
    }

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw RuntimeException("This plugin only support application module ...")
        }
        hookTransformTask(project)
    }
}