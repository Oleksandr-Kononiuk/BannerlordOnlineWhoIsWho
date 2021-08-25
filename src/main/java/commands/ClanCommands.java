package commands;

import dao.ClanDAO;
import dao.impl.ClanDAOImpl;
import model.Clan;
import utils.View;
import javax.persistence.NoResultException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Oleksandr Kononiuk
 */

public class ClanCommands {
    private final ClanDAO clanDAO = new ClanDAOImpl();
    private final View view = new View();
    private static final String WRONG_FORMAT = "> Возможно неправильный формат команды. Попробуйте еще раз.";


    public String command(String command, String[] args) {
        switch (command.toLowerCase()) {
            case "new"://+
                try {
                    clanDAO.addNewClan(args[0]);
                    return "> Клан добавлен.";
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "delete": //+
                try {
                    boolean isDeleted = clanDAO.deleteClan(args[0]);
                    return String.format("> Клан%s удален.", (isDeleted ? "" : " не"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return clanNotFoundString(e);
                }
            case "add_member"://+
                try {
                    boolean isAdded = clanDAO.addMember(args[0], Arrays.copyOfRange(args, 1, args.length));
                    return String.format("> Игрок%s добавлен в состав клана.", (!isAdded ? "" : " не"));
                } catch (NoResultException n) {
                    return playerNotFoundString(n);
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "delete_member"://+
                try {
                    boolean isDeleted = clanDAO.deleteMember(args[0], Long.parseLong(args[1]));
                    return String.format("> Игрок%s удален из состав клана.", (!isDeleted ? "" : " не"));
                } catch (IllegalArgumentException i) {
                    return "> Id отрицательный или не цифра.";
                } catch (NoResultException n) {
                    return playerNotFoundString(n);
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "leader"://+
                try {
                    String clanLeader = clanDAO.getClanLeader(args[0]);
                    return "```css\n Лидер клана [" + clanLeader + "]```";
                } catch (NullPointerException n) {
                    return "> Клан пустой или лидера не пометили. " + n.getMessage();
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "change_leader"://+
                try {
                    boolean isChanged = clanDAO.changeClanLeader(args[0], Long.parseLong(args[1]), Long.parseLong(args[2]));
                    return String.format("> Лидер%s сменен.", (isChanged ? "" : " не"));
                } catch (NoSuchElementException n) {
                    return "> Клан пустой или " + n.getMessage();
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "all"://+
                try {
                    List<Clan> clans = clanDAO.getAllClans(args[0]);
                    if (clans.size() > 0) {
                        StringBuilder resultOut = new StringBuilder();
                        for (Clan c : clans) {
                            resultOut.append(view.toStringClan(c));
                            resultOut.append("\n");
                        }
                        return resultOut.toString();
                    }
                } catch (Exception e) {
                    return "> По запросу кланов не найдено. " + e.getMessage();
                }
            case "find":
                try {
                    Clan clan = clanDAO.findByName(args[0]);
                    if (clan != null) {
                        return view.toStringClan(clan);
                    }
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "relations"://+
                try {
                    boolean isChanged = clanDAO.setRelation(args[0], Integer.parseInt(args[1]));
                    return String.format("> > Дипломатические отношения%s изменены.", (isChanged ? "" : " не"));
                } catch (IllegalArgumentException i) {
                    return "> Параметр дипломатических отношений указан неправильно. " +
                            "Правильные параметры: 0 -> нейтралитет, 1 -> война, 2 -> дружные.";
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "diplomacy"://
                try {
                    Map<Integer, List<Clan>> diplomacy = clanDAO.buildDiplomacy();
                    return view.toDiplomacyString(diplomacy);
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "update":
                try {
                    clanDAO.updateClan(args[0]);
                    return "> Состав клана обновлен.";
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            default:
                System.out.println("Maybe wrong command format. Please try again.");
                return WRONG_FORMAT;
        }
    }

    private String clanNotFoundString(Exception e) {
        return String.format("> Клан не найден. " +
                "Причина: %s", e.getMessage());
    }

    private String playerNotFoundString(Exception e) {
        return String.format("> Игрок не найден. " +
                "Причина: %s %s", e.getClass().getName(), e.getMessage());
    }
}
