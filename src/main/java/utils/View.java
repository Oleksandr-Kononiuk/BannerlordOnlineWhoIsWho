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
                        "Название клана [%s]. Количество игроков [%d]\n" +
                        "Состав:\n", c.getClanName(), c.getMembers().size()));

        if (c.getMembers().size() > 0) {
            for (Player p : c.getMembers()) {
                out.append("    " + p.toClanMemberString() + "\n");
            }
        }
        out.append("```");
        return out.toString();
    }
}
