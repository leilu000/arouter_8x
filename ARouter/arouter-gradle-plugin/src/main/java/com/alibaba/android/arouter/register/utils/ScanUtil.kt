package com.alibaba.android.arouter.register.utils

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 *
 */
object ScanUtil {

    var registerList = ArrayList<ScanSetting>().apply {
        add(ScanSetting("IRouteRoot"))
        add(ScanSetting("IInterceptorGroup"))
        add(ScanSetting("IProviderGroup"))
    }

    fun shouldProcessPreDexJar(path: String): Boolean {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    fun shouldProcessClass(entryName: String): Boolean {
        //  return entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
        return entryName.contains(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
    }

    fun scanJar(jarFile: File) {
        val file = JarFile(jarFile)
        val enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            val jarEntry: JarEntry = enumeration.nextElement()
            val entryName = jarEntry.name
            if (entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)) {
                val inputStream = file.getInputStream(jarEntry)
                scanClass(inputStream, entryName)
            } else if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                // mark this jar file contains LogisticsCenter.class
                // After the scan is complete, we will generate register code into this file
            }
        }
        file.close()
    }

    fun scanClass(inputStream: InputStream, filePath: String) {
        if (!filePath.endsWith(".class")) {
            inputStream.close()
            Logger.i("scan class warning,the file is not a class:$filePath")
            return
        }
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = ScanClassVisitor(Opcodes.ASM7, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    class ScanClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String,
            interfaces: Array<out String>?
        ) {
            super.visit(version, access, name, signature, superName, interfaces)
            registerList.forEach { ext ->
                if (ext.interfaceName.isNotEmpty()) {
                    interfaces?.forEach { itName ->
                        if (itName == ext.interfaceName) {
                            //fix repeated inject init code when Multi-channel packaging
                            if (!ext.classList.contains(name)) {
                                ext.classList.add(name)
                            }
                        }
                    }
                }
            }
        }
    }
}