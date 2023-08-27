package org.example.botfarm.service

import java.io.File
import java.io.IOException
import java.security.SecureRandom

class AufService {
    private val path = "./auf.txt"

    fun getAuf(): String {
        val aufList = readFileAsLinesUsingBufferedReader()
        val secureRandom = SecureRandom()
        return aufList[secureRandom.nextInt(aufList.size)]
    }

    private fun readFileAsLinesUsingBufferedReader(): List<String> {
        val resultList = mutableListOf<String>()
        try {
            resultList.addAll(
                File(path)
                    .bufferedReader()
                    .readLines()
            )
        } catch (e: IOException) {
            resultList.add(e.message ?: "file auf.txt not found")
        }
        return resultList
    }

}