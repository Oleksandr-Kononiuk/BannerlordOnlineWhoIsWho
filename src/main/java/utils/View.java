package utils;

import model.Clan;
import model.Player;

import java.util.Objects;

public class View {

    public void result(Object s) {
        print(s.toString());
    }

    public String toStringPlayer(Player p) {
        return p.toString();
    }

    public String toStringClan(Clan c) {
        StringBuilder out = new StringBuilder(
                String.format("Clan name: '%s'. Clan size: '%d'", c.getClanName(), c.getMembers().size()));

        if (c.getMembers().size() > 0) {
            out.append("\nMembers:");
            for (Player p : c.getMembers()) {
                out.append(p.toString() + "\n");
            }
        }
        return out.toString();
    }

    public void print(String s) {
        System.out.println(s);
    }
}
