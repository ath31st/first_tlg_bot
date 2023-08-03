package org.example.botfarm.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class JokeService : Service() {
    private val url = "http://www.bashorg.org/casual"
    private val userAgent = "Mozilla/5.0"

    override fun getResult(): String {
        val result: String
        val rawDoc = getRawDataFromBashOrg()
        result = parseFromRawData(rawDoc)
        return result
    }


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

    private fun parseFromRawData(rawData: Document?): String {
        var result: String? = null
        rawData!!.outputSettings(Document.OutputSettings().prettyPrint(false))
        //select all <br> tags and append \n after that
        rawData.select("br").after("\\n")
        //select all <p> tags and prepend \n before that
        rawData.select("p").before("\\n")
        val elements = rawData.getElementsByClass("q")
        for (element in elements) {
            result = element.select("div").last()!!.text().replace("\\\\n".toRegex(), "\n")
        }
        if (result == null) {
            result = "Возникли проблемы с Bash.org, повторите попытку через несколько минут."
        }
        return result
    }
}