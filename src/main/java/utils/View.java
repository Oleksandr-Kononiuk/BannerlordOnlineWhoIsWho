package utils;

import model.Clan;
import model.Player;

import java.util.Objects;

public class View {

    public String toStringPlayer(Player p) {
        return p.toString();
    }

    public String toStringClan(Clan c) {
        StringBuilder out = new StringBuilder(
                String.format("```css\n" +
                        "Clan name [%s]. Clan size [%d]\n" +
                        "Members:\n", c.getClanName(), c.getMembers().size()));

        if (c.getMembers().size() > 0) {
            for (Player p : c.getMembers()) {
                out.append("    " + p.toClanMemberString() + "\n");
            }
        }
        out.append("```");
        return out.toString();
    }
//    ```css
//    Clan name [%s] Clan size [%d]
//    Members:
//    {asdasda: 'sdsd'}
//    Игрок{asdasda}
//```
}
