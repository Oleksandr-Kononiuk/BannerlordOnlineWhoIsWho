import model.Player;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUtils {

    public List<Player> brutforsePlayers(int pageFrom, int pageTo) {
        List<Player> players = new ArrayList<>();
        return players;
    }

    public Player getNewPlayer() {
        Player player = new Player();
        return player;
    }

    private Player buildPlayer(long id, String name) {
        Player player = new Player();
        return player;
    }

    private long getIDFromLink(String link) {
        return 1L;
    }

    private String getNickFromLink(String link) {
        return "";
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
