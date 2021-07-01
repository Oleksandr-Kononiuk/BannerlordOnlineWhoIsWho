package utils;

import model.Player;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataUtils {

    private static final String PROFILE_LINK = "https://bannerlord-online.com/forum/index.php?members/";

//    public static void main(String[] args) {
//        utils.DataUtils du = new utils.DataUtils();
//        du.bruteForcePlayers(5193, 5194);
//    }

    public List<Player> bruteForcePlayers(int pageFrom, int pageTo) {
        List<Player> players = new ArrayList<>();

        for (int i = pageFrom; i < pageTo; i++) {
            Player newPlayer = getNewPlayer(i);
            if (newPlayer != null) {
                players.add(newPlayer);
            }
        }

        return players;
    }

    public Player getNewPlayer(long id) {
        Player player = null;

        Document doc = getDocument(PROFILE_LINK + id);
        String link = getLink(doc);

        if (link != null) {
            player = buildPlayer(getIDFromLink(link), getNickFromLink(link), link);
        } else {
            System.out.println("link is null");
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
        //System.out.println(id);
        return Long.parseLong(id);
    }

    private String getNickFromLink(String link) {
        String name = link.substring(link.lastIndexOf("s/") + 2, link.lastIndexOf("."));
        //System.out.println(name);
        return "";
    }


    //Example: https://bannerlord-online.com/forum/index.php?members/storm.1/
    private String getLink(Document doc) {
        if (doc != null) {
            Elements elements = doc.getElementsByTag("meta");

            if (elements.size() != 0) {
                //System.out.println(elements.size());
                String link = elements.get(5).attr("content");
                //System.out.println(link);
                return link;
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
            e.printStackTrace();
            //logger.error(Arrays.toString(e.getStackTrace()));
        }
        return doc;
    }
}