package com.chiclaim.filepath

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val split = ":".split(":").toTypedArray()

        split.forEach {
            println("value=$it")
        }
        val selectionArgs = arrayOf<String>(split[1])

        println(selectionArgs)
        assertEquals(4, 2 + 2)
    }
}