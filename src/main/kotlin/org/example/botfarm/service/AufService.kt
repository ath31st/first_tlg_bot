package org.example.botfarm.service

import java.io.File
import java.io.IOException
import java.security.SecureRandom
import kotlin.random.Random

class AufService {
    private val pathToTextAuf = "./fb_resources/auf.txt"
    private val pathToImagesAuf = "./fb_resources/auf_images"

    fun getAuf(): String {
        val aufList = readFileAsLinesUsingBufferedReader()
        val secureRandom = SecureRandom()
        return aufList[secureRandom.nextInt(aufList.size)]
    }

    private fun readFileAsLinesUsingBufferedReader(): List<String> {
        val resultList = mutableListOf<String>()
        try {
            resultList.addAll(
                File(pathToTextAuf)
                    .bufferedReader()
                    .readLines(),
            )
        } catch (e: IOException) {
            resultList.add(e.message ?: "file auf.txt not found")
        }
        return resultList
    }

    fun getRandomAufFileFromResources(): File? {
        val resourceDirectory = File(pathToImagesAuf)
        if (resourceDirectory.exists() && resourceDirectory.isDirectory) {
            val fileList =
                resourceDirectory.listFiles { _, name -> name.endsWith(".jpg") }
            if (!fileList.isNullOrEmpty()) {
                val randomIndex = Random.nextInt(fileList.size)
                return fileList[randomIndex]
            }
        }
        return null
    }
}
