package commands;

import dao.PlayerDAO;
import dao.impl.PlayerDAOImpl;
import model.Clan;
import model.Player;
import utils.View;
import javax.persistence.RollbackException;
import java.util.List;

/**
 * @author Oleksandr Kononiuk
 */

public class PlayerCommands {
    private final PlayerDAO playerDAO = new PlayerDAOImpl();
    private final View view = new View();
    private static final String WRONG_FORMAT = "> Возможно неправильный формат команды. Попробуйте еще раз.";

    public String command(String command, String[] args) {
        switch (command.toLowerCase()) {

            case "save"://+-
                try {
                    playerDAO.save(args[0]);
                    return "> Игрок добавлен.";
                } catch (RollbackException p) {
                    return "> Игрок уже существует в базе.";
                } catch (NumberFormatException n) {
                    return "> Входной параметр не цифра.";
                } catch (IllegalArgumentException i) {
                    return "> Id не может быть отрицательным.";
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "find"://+
                try {
                    Player p = playerDAO.find(args);
                    return view.toStringPlayer(p);
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "clan"://+
                try {
                    Clan clan = playerDAO.getPlayerClan(args);
                    return (clan == null ? "> Игрок не состоит в клане." : view.toStringClan(clan));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "leader"://+
                try {
                    boolean isLeader = playerDAO.isClanLeader(args);
                    return String.format("> Игрок%s лидер клана.", (isLeader ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "twink"://+
                try {
                    boolean isTwink = playerDAO.isTwink(args);
                    return String.format("> Аккаунт%s твинк.", (isTwink ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "all"://+
                try {
                    List<Player> players = playerDAO.findAll(args[0]);
                    if (players.size() > 0) {
                        StringBuilder resultOut = new StringBuilder();
                        int count = 0;
                        for (Player p : players) {
                            if (count > 7) break; // first 8 result line

                            resultOut.append(view.toStringPlayer(p));
                            resultOut.append("\n");
                            count++;
                        }
                        return resultOut.toString();
                    }
                } catch (Exception e) {
                    return "> По запросу игроков не найдено. Причина: " + e.getMessage();
                }
            case "change_name"://+
                try {
                    boolean isChanged = playerDAO.changeTempName(Long.parseLong(args[0]), args);
                    return String.format("> Имя игрока%s изменено.", (isChanged ? "" : " не"));
                } catch (NumberFormatException n) {
                    return "> Id игрока не указан.";
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "change_clan"://+
                try {
                    boolean isChanged = playerDAO.changeClan(Long.parseLong(args[0]), args[1]);
                    return String.format("> Клан игрока%s изменен.", (!isChanged ? "" : " не"));
                } catch (IllegalStateException n) {
                    return "> Игрок уже в клане.";
                } catch (NumberFormatException n) {
                    return "> Id игрока не указан.";
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "army":
                try {
                    boolean isChanged = playerDAO.setArmy(Integer.parseInt(args[0]), args);
                    return String.format("> Количество войск%s обновлено.", (isChanged ? "" : " не"));
                } catch (NumberFormatException n) {
                    return "> Первый параметр должен быть числом!";
                } catch (IllegalArgumentException i) {
                    return "> Размер армии не может быть отрицательным!";
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "set_leader"://+
                try {
                    boolean isChanged = playerDAO.setClanLeader(Boolean.parseBoolean(args[0]), args);
                    return String.format("> Статус лидера%s изменен.", (!isChanged ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "set_twink"://+
                try {
                    boolean isChanged = playerDAO.setTwink(Boolean.parseBoolean(args[0]), args);
                    return String.format("> Статус твинка%s изменен.", (!isChanged ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "delete"://+
                try {
                    boolean isDeleted = playerDAO.delete(Long.parseLong(args[0]));
                    return String.format("> Игрок%s удален.", (isDeleted ? "" : " не"));
                } catch (IllegalArgumentException i) {
                    return "> Id отрицательный или не цифра.";
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "update"://+
                try {
                    boolean isUpdated = playerDAO.update(Long.parseLong(args[0]));
                    return String.format("> Игрок%s обновлен.", (isUpdated ? "" : " не"));
                } catch (IllegalArgumentException i) {
                    return "> Id отрицательный или не цифра.";
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            default:
                System.out.println("Maybe wrong command format. Please try again.");
                return WRONG_FORMAT;
        }
    }

    private String playerNotFoundString(Exception e) {
        return String.format("> Игрок не найден. " +
                "Причина: %s %s", e.getClass().getName(), e.getMessage());
    }
}
