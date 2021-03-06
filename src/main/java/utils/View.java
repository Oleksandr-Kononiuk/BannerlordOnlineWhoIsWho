package utils;

import model.Clan;
import model.Player;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 *@author  Oleksandr Kononiuk
 */

public class View {

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
                        "Состав:\n", c.getClanName(), c.getMembers().size(), DataUtils.relationsState[c.getRelation()], clanArmy));

        if (c.getMembers().size() > 0) {
            for (Player p : c.getMembers()) {
                out.append("    " + p.toClanMemberString() + "\n");
            }
        }
        out.append("```");
        return out.toString();
    }

    public String toDiplomacyString(Map<Integer, List<Clan>> diplomacy) {
        StringBuilder out = new StringBuilder();

        for(Map.Entry<Integer, List<Clan>> entry : diplomacy.entrySet()) {
            out.append(DataUtils.relationsState[entry.getKey()]);
            out.append("\n");
            out.append(Arrays.toString(
                    entry.getValue().stream()
                            .map(Clan::getClanName).toArray()
            ));
            out.append("\n");
        }
        return out.toString();
    }
}
