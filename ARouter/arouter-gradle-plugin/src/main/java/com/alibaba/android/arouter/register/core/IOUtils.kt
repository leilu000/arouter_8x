package com.alibaba.android.arouter.register.core

import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 *
 */
object IOUtils {
    fun toByteArray(`is`: InputStream): ByteArray {
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len = 0
        try {
            while (`is`.read(buffer).also { len = it } != -1) {
                bos.write(buffer, 0, len)
            }
        } catch (e: Exception) {
        } finally {
            bos.flush()
            bos.close()
        }
        return bos.toByteArray()
    }
}