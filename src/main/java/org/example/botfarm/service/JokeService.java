package org.example.botfarm.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

@Getter
@Setter
@RequiredArgsConstructor
public class JokeService {
    private final static String URL = "http://www.bashorg.org/casual";
    private final static String USER_AGENT = "Mozilla/5.0";

    public String getRandomJoke() {
        String result;
        Document rawDoc = getRawDataFromBashOrg();
        result = parseFromRawData(rawDoc);
        return formatResult(result);
    }


    private Document getRawDataFromBashOrg() {
        Document document = null;
        try {
            document = Jsoup.connect(URL)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return document;
    }

    private String parseFromRawData(Document rawData) {
        String result = null;
        Elements elements = rawData.getElementsByClass("q");
        for (Element element : elements) {
            result = element.select("div").first().text();
        }
        if (result == null) {
            result = "Возникли проблемы с Bash.org, повторите попытку через несколько минут.";
        }
        return result;
    }

    private String formatResult(String result) {
        result = result.substring(result.indexOf(")") + 1);
        return result;

    }

}