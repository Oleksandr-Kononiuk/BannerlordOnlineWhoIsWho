package utils;

import model.Clan;
import model.Player;

import java.util.Objects;

public class View {
    private final String[] relationsState = new String[] {"Нейтралитет", "Война", "Дружные"};

    public String toStringPlayer(Player p) {
        return p.toString();
    }

    public String toStringClan(Clan c) {
        int clanArmy = c.getMembers().stream()
                .mapToInt(Player::getArmy)
                .sum();

        StringBuilder out = new StringBuilder(
                String.format("```css\n" +
                        "Название клана [%s]. Количество игроков [%d]. Отношения [%s]. Войска [%s]\n" +
                        "Состав:\n", c.getClanName(), c.getMembers().size(), relationsState[c.getRelation()], clanArmy));

        if (c.getMembers().size() > 0) {
            for (Player p : c.getMembers()) {
                out.append("    " + p.toClanMemberString() + "\n");
            }
        }
        out.append("```");
        return out.toString();
    }
}
