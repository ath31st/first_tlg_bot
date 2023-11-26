package org.example.botfarm.service

import java.io.IOException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * This class provides functionality to retrieve jokes from the "https://башорг.рф" website.
 *
 * @property url The URL of the website from which jokes are retrieved.
 * @property userAgent The user agent string used for HTTP requests.
 */
class JokeService {
    private val url = "https://башорг.рф/random"
    private val userAgent = "Mozilla/5.0"

    /**
     * Retrieves a joke from the specified website.
     *
     * @return A joke as a string, or an error message if there are issues with the website.
     */
    fun getJoke(): String {
        val rawDoc = getRawDataFromBashOrg()
        return parseFromRawData(rawDoc)
    }

    /**
     * Retrieves raw HTML data from the "https://башорг.рф" website.
     *
     * @return A Document object representing the raw HTML data, or null if there are issues
     * with the connection.
     */
    private fun getRawDataFromBashOrg(): Document? {
        var document: Document? = null
        try {
            document = Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(5000)
                .get()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return document
    }

    /**
     * Parses jokes from raw HTML data.
     *
     * @param rawData The raw HTML data as a Document object.
     * @return A joke as a string, or an error message if there are issues with parsing or no
     * jokes are found.
     */
    private fun parseFromRawData(rawData: Document?): String {
        var result: String? = null
        rawData!!.outputSettings(Document.OutputSettings().prettyPrint(false))
        // select all <br> tags and append \n after that
        rawData.select("br").after("\\n")
        // select all <p> tags and prepend \n before that
        rawData.select("p").before("\\n")
        val elements = rawData.getElementsByClass("quote__body")
        for (element in elements) {
            result = element.select("div").last()!!.text().replace("\\\\n".toRegex(), "\n")
        }
        if (result == null) {
            result = "Возникли проблемы с башорг.рф, повторите попытку через несколько минут."
        }
        return result
    }
}

