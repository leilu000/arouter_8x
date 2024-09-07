package com.alibaba.android.arouter.register.launch

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 *
 */
object HelloWorldClassVisitor {

    fun modify(file: File): ByteArray {
        val cr = ClassReader(file.inputStream())
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        val cv = object : ClassVisitor(Opcodes.ASM9, cw) {

            override fun visitMethod(
                access: Int,
                name: String?,
                descriptor: String?,
                signature: String?,
                exceptions: Array<out String>?
            ): MethodVisitor {
                val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
                if (name == "onCreate") {
                    println("ooooooooooooooooooooooooooooooo")

                    return object : MethodVisitor(Opcodes.ASM9, mv) {
                        override fun visitCode() {
                            super.visitCode()
                            mv.visitFieldInsn(
                                Opcodes.GETSTATIC,
                                "java/lang/System",
                                "out",
                                "Ljava/io/PrintStream;"
                            );
                            mv.visitLdcInsn("Hello, ASM!");
                            mv.visitMethodInsn(
                                Opcodes.INVOKEVIRTUAL,
                                "java/io/PrintStream",
                                "println",
                                "(Ljava/lang/String;)V",
                                false
                            );
                        }
                    }
                }
                return mv
            }

        }
        cr.accept(cv, 0)
        return cw.toByteArray()
    }

}