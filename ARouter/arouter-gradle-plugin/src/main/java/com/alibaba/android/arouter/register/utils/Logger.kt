package com.alibaba.android.arouter.register.utils

import org.gradle.api.Project


/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 *
 */
object Logger {


    fun i(info: String) {
        println("HSBC LAB Arouter >>>  $info")
    }

    fun e(error: String) {
        println("HSBC LAB Arouter >>> $error")
    }

    fun w(warning: String) {
        println("HSBC LAB Arouter >>>  $warning")
    }
}