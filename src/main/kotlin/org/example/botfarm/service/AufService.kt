package org.example.botfarm.service

import java.io.File
import java.io.IOException
import java.security.SecureRandom
import kotlin.random.Random

/**
 * This class provides functionality related to the "Auf" resources.
 * It allows you to retrieve a random "Auf" text from a file and get a random "Auf" image file
 * from resources.
 *
 * @property pathToTextAuf The path to the text file containing "Auf" phrases.
 * @property pathToImagesAuf The path to the directory containing "Auf" image files.
 */
class AufService {
    private val pathToTextAuf = "./fb_resources/auf.txt"
    private val pathToImagesAuf = "./fb_resources/auf_images"

    /**
     * Retrieves a random "Auf" text from the specified file.
     *
     * @return A random "Auf" text.
     */
    fun getAuf(): String {
        val aufList = readFileAsLinesUsingBufferedReader()
        val secureRandom = SecureRandom()
        return aufList[secureRandom.nextInt(aufList.size)]
    }

    /**
     * Reads the lines from the specified text file using a BufferedReader and returns them as a
     * list of strings.
     *
     * @return A list of strings containing the lines from the file, or an error message if the
     * file is not found.
     */
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

    /**
     * Retrieves a random "Auf" image file from the resources directory.
     *
     * @return A random "Auf" image file or null if no image files are found.
     */
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
