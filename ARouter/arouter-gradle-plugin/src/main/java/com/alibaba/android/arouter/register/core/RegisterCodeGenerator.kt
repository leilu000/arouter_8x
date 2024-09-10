package com.alibaba.android.arouter.register.core

import com.alibaba.android.arouter.register.utils.Logger
import com.alibaba.android.arouter.register.utils.ScanSetting
import com.alibaba.android.arouter.register.utils.ScanUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 *
 */
object RegisterCodeGenerator {


    fun insertInitCodeTo(registerSetting: ScanSetting, logisticsCenterClassFile: File?) {
        if (registerSetting.classList.isNotEmpty()) {
            if (logisticsCenterClassFile?.name?.endsWith(".jar") == true) {
                insertInitCodeIntoJarFile(logisticsCenterClassFile, registerSetting)
            }
        }
    }

    /**
     * generate code into jar file
     * @param jarFile the jar file which contains LogisticsCenter.class
     * @return
     */
    private fun insertInitCodeIntoJarFile(jarFile: File, registerSetting: ScanSetting): File {
        val optJar = File(jarFile.parent, jarFile.name + ".opt")
        if (optJar.exists()) optJar.delete()
        val file = JarFile(jarFile)
        val enumeration = file.entries()
        val jarOutputStream = JarOutputStream(FileOutputStream(optJar))

        while (enumeration.hasMoreElements()) {
            val jarEntry = enumeration.nextElement() as JarEntry
            val entryName = jarEntry.name
            val zipEntry = ZipEntry(entryName)
            val inputStream = file.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)
            if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {

                Logger.i("Insert init code to class >> $entryName")

                val bytes = referHackWhenInit(inputStream, registerSetting)
                jarOutputStream.write(bytes)
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()

        if (jarFile.exists()) {
            jarFile.delete()
        }
        optJar.renameTo(jarFile)
        return jarFile
    }

    //refer hack class when object init
    fun referHackWhenInit(inputStream: InputStream, scanSetting: ScanSetting): ByteArray {
        return referHackWhenInit(inputStream.readAllBytes(), scanSetting)
    }

    fun referHackWhenInit(classByte: ByteArray, scanSetting: ScanSetting): ByteArray {
        val cr = ClassReader(classByte)
        val cw = ClassWriter(cr, 0)
        val cv = MyClassVisitor(Opcodes.ASM7, cw, scanSetting)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    class MyClassVisitor(api: Int, cv: ClassVisitor, private val extension: ScanSetting) :
        ClassVisitor(api, cv) {


        override fun visitMethod(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
            //generate code into this method
            if (name == ScanSetting.GENERATE_TO_METHOD_NAME) {
                return RouteMethodVisitor(Opcodes.ASM7, mv, extension)
            }
            return mv
        }
    }

    class RouteMethodVisitor(api: Int, mv: MethodVisitor, private val extension: ScanSetting) :
        MethodVisitor(api, mv) {

        override fun visitInsn(opcode: Int) {
            //generate code before return
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                extension.classList.forEach { n ->
                    val name = n.replace("/", ".")
                    mv.visitLdcInsn(name)//类名
                    // generate invoke register method into LogisticsCenter.loadRouterMap()
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        ScanSetting.GENERATE_TO_CLASS_NAME,
                        ScanSetting.REGISTER_METHOD_NAME,
                        "(Ljava/lang/String;)V",
                        false
                    )
                }
            }
            super.visitInsn(opcode)
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }
    }

}