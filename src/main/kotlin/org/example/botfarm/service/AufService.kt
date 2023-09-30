package org.example.botfarm.service

import org.example.botfarm.AppKt
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import kotlin.random.Random

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
                    .readLines(),
            )
        } catch (e: IOException) {
            resultList.add(e.message ?: "file auf.txt not found")
        }
        return resultList
    }

    fun getRandomAufFileFromResources(): File? {
        val folderPath = "/auf_images"
        val folderUrl = AppKt::class.java.getResource(folderPath)
        if (folderUrl != null) {
            val folder = File(folderUrl.toURI())
            if (folder.exists() && folder.isDirectory) {
                val files = folder.listFiles { _, name -> name.startsWith("auf_") }
                if (!files.isNullOrEmpty()) {
                    val randomIndex = Random.nextInt(files.size)
                    return files[randomIndex]
                }
            }
        }
        return null
    }
}
