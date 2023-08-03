//package org.example.botfarm.service;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//
//@Getter
//@Setter
//@RequiredArgsConstructor
//public class JokeService extends Service {
//    private final static String URL = "http://www.bashorg.org/casual";
//    private final static String USER_AGENT = "Mozilla/5.0";
//
//    public String getResult() {
//        String result;
//        Document rawDoc = getRawDataFromBashOrg();
//        result = parseFromRawData(rawDoc);
//        return result;
//    }
//
//
//    private Document getRawDataFromBashOrg() {
//        Document document = null;
//        try {
//            document = Jsoup.connect(URL)
//                    .userAgent(USER_AGENT)
//                    .timeout(5000)
//                    .get();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return document;
//    }
//
//    private String parseFromRawData(Document rawData) {
//        String result = null;
//
//        rawData.outputSettings(new Document.OutputSettings().prettyPrint(false));
//        //select all <br> tags and append \n after that
//        rawData.select("br").after("\\n");
//        //select all <p> tags and prepend \n before that
//        rawData.select("p").before("\\n");
//
//        Elements elements = rawData.getElementsByClass("q");
//        for (Element element : elements) {
//            result = element.select("div").last().text().replaceAll("\\\\n", "\n");
//        }
//        if (result == null) {
//            result = "Возникли проблемы с Bash.org, повторите попытку через несколько минут.";
//        }
//        return result;
//    }
//}
