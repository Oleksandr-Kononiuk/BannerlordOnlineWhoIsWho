package utils;

import model.Player;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

/**
 *@author  Oleksandr Kononiuk
 */

public class DataUtils {

    public static final String[] relationsState = new String[] {"Нейтралитет", "Война", "Дружные"};
    private static final String PROFILE_LINK = "https://bannerlord-online.com/forum/index.php?members/";

    public Player getNewPlayer(String idOrLink) {
        Player player = null;

        if (idOrLink.matches("^\\d+$")) {
            if (Long.parseLong(idOrLink) < 0) throw new IllegalArgumentException("Id не может быть отрицательным.");

            Document doc = getDocument(PROFILE_LINK + idOrLink);
            String link = getLink(doc);

            if (link != null) {
                player = buildPlayer(getIDFromLink(link), getName(doc), link);
                System.out.println(player.toString());
            } else {
                System.out.println("link is null");
            }
        } else {
            player = buildPlayer(getIDFromLink(idOrLink), getNameFromLink(idOrLink), idOrLink);
            System.out.println(player.toString());
        }
        return player;
    }

    private Player buildPlayer(long id, String name, String link) {
        Player player = new Player();

        player.setId(id);
        player.setMainName(name);
        player.setTempName(name);
        player.setProfileLink(link);

        return player;
    }

    private long getIDFromLink(String link) {
        String id = link.substring(link.lastIndexOf(".") + 1, link.length() - 1);
        return Long.parseLong(id);
    }

    private String getName(Document doc) {
        if (doc != null) {
            Elements elements = doc.getElementsByTag("title");

            if (elements.size() != 0) {
                String tagText = elements.get(0).text();
                //System.out.println(tagText);
                return tagText.split("\\|")[0].trim();
            }
        } else {
            System.out.println("doc is null");
        }
        return null;
    }

    //Example: https://bannerlord-online.com/forum/index.php?members/piracent.23390/
    private String getNameFromLink(String link) {
        int start = link.lastIndexOf("rs/") + 3;
        int end = link.lastIndexOf(".");
        String name = link.substring(start, end);
        name = name.replaceAll("-", " ");
        System.out.println(name);

        return name;
    }


    //Example: https://bannerlord-online.com/forum/index.php?members/storm.1/
    private String getLink(Document doc) {
        if (doc != null) {
            Elements elements = doc.getElementsByTag("meta");

            if (elements.size() != 0) {
                return elements.get(5).attr("content");
            }
        } else {
            System.out.println("doc is null");
        }
        return null;
    }

    //get Document from source using JSoup lib
    private Document getDocument(String source) {
        Document doc = null;
        try {
            doc = Jsoup.connect(source)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 OPR/76.0.4017.177")
                    .referrer("no-referrer-when-downgrade")//no-referrer-when-downgrade
                    .timeout(0)
                    .method(Connection.Method.GET)
                    .get();
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(String.format("getDocument(String %s) return NULL", source));
        }
        return doc;
    }
}
