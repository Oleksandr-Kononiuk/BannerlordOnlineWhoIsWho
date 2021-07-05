import dao.ClanDAO;
import dao.PlayerDAO;
import dao.impl.ClanDAOImpl;
import dao.impl.PlayerDAOImpl;
import model.Clan;
import model.Player;
import utils.DataUtils;
import utils.JpaUtil;
import utils.View;

import javax.crypto.spec.DESKeySpec;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class BannerlordOnlineWhoIsWho {

    private final DataUtils dataUtils = new DataUtils();
    private final PlayerDAO playerDAO = new PlayerDAOImpl();
    private final ClanDAO clanDAO = new ClanDAOImpl();
    private final View view = new View();

    public void test() {
        System.out.println("Enter command: ");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String c;
            do {
                c = reader.readLine();
                parseCommand(c);
            } while (!c.toLowerCase().equals("!exit"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JpaUtil.init("BannerlordOnlinePlayersMySQL"); // initialize database
        BannerlordOnlineWhoIsWho BOWIW = new BannerlordOnlineWhoIsWho();
        BOWIW.test();

        //BOWIW.dataUtils.getNewPlayer(1);

    }

    private void playerCommands(String command, String[] args) {
        switch (command.toLowerCase()) {

            case "save":
                try {
                    playerDAO.save(Long.parseLong(args[0]));
                    view.print("Игрок добавлен.");
                } catch (PersistenceException p) {
                    view.print("Игрок уже существует в базе.");
                } catch (NumberFormatException n) {
                    view.print("Входной параметр не цифра.");
                } catch (IllegalArgumentException i) {
                    view.print("Id не может быть отрицательным.");
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+

            case "find":
                try {
                    Player p = playerDAO.find(args[0]);
                    view.print(view.toStringPlayer(p));
                } catch (Exception e) {
                    e.printStackTrace();
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+

            case "clan":
                try {
                    Clan clan = playerDAO.getPlayerClan(args[0]);
                    if (clan == null) {
                        view.print("Игрок не состоит в клане.");
                    } else {
                        view.print(view.toStringClan(clan));
                    }
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+-

            case "leader":
                try {
                    boolean isLeader = playerDAO.isClanLeader(args[0]);
                    if (isLeader) {
                        view.print("Игрок лидер клана.");
                    } else {
                        view.print("Игрок не лидер клана.");
                    }
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+-

            case "twink":
                try {
                    boolean isTwink = playerDAO.isTwink(args[0]);
                    if (isTwink) {
                        view.print("Аккаунт твинк.");
                    } else {
                        view.print("Аккаунт не твинк.");
                    }
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+-

            case "all":
                try {
                    List<Player> players = playerDAO.findAll(args[0]);
                    if (players.size() > 0) {
                        for (Player p : players) {
                            view.print(view.toStringPlayer(p));
                        }
                    }
                } catch (Exception e) {
                    view.print("По запросу игроков не найдено.");
                    view.print(e.getMessage());
                }
                break;//+

            case "change_name":
                try {
                    boolean isChanged = playerDAO.changeTempName(args[0], args[1]);
                    if (!isChanged) {
                        view.print("Имя игрока изменено.");
                    } else {
                        view.print("Имя игрока не изменено.");
                    }
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+

            case "change_clan":
                try {
                    boolean isChanged = playerDAO.changeClan(args[0], args[1]);;
                    if (!isChanged) {
                        view.print("Клан игрока изменен.");
                    } else {
                        view.print("Клан игрока не изменен.");
                    }
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//-

            case "set_leader":
                try {
                    boolean isChanged = playerDAO.setClanLeader(args[0], Boolean.parseBoolean(args[1]));
                    if (!isChanged) {
                        view.print("Статус лидера изменен.");
                    } else {
                        view.print("Статус лидера не изменен.");
                    }
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+

            case "set_twink":
                try {
                    boolean isChanged = playerDAO.setTwink(args[0], Boolean.parseBoolean(args[1]));
                    if (!isChanged) {
                        view.print("Статус твинка изменен.");
                    } else {
                        view.print("Статус твинка не изменен.");
                    }
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+

            case "delete":
                try {
                    boolean isDeleted = playerDAO.delete(args[0]);
                    if (isDeleted) {
                        view.print("Игрок удален.");
                    } else {
                        view.print("Игрок не удален.");
                    }
                } catch (Exception e) {
                    view.print("Игрок не найден.");
                    view.print(e.getMessage());
                }
                break;//+

            default:
                System.out.println("Maybe wrong command format. Please try again.");
                view.print("Maybe wrong command format. Please try again.");
        }
    }

    private void clanCommands(String command, String[] args) {
        switch (command.toLowerCase()) {
            case "new":
                try {
                    clanDAO.addNewClan(args[0]);
                    view.print("Клан добавлен.");
                } catch (Exception e) {
                    view.print("Клан не найден.");
                    view.print(e.getMessage());
                }
                break;//+

            case "delete":
                try {
                    boolean isDeleted = clanDAO.deleteClan(args[0]);
                    if (isDeleted) {
                        view.print("Клан удален.");
                    } else {
                        view.print("Клан не удален.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    view.print("Клан не найден.");
                    view.print(e.getMessage());
                }
                break;//+-

            case "add_member":
                try {
                    boolean isAdded = clanDAO.addMember(args[0], args[1]);
                    if (!isAdded) {
                        view.print("Игрок добавлен в состав клана.");
                    } else {
                        view.print("Игрок не добавлен в состав клана.");
                    }
                } catch (NoResultException n) {
                    view.print("Игрок не найден.");
                    view.print(n.getMessage());
                } catch (Exception e) {
                    view.print("Клан не найден.");
                    view.print(e.getMessage());
                }
                break;//+

            case "delete_member":
                try {
                    boolean isDeleted = clanDAO.deleteMember(args[0], args[1]);
                    if (!isDeleted) {
                        view.print("Игрок удален из состав клана.");
                    } else {
                        view.print("Игрок не удален из состав клана.");
                    }
                } catch (NoResultException n) {
                    view.print("Игрок не найден.");
                    view.print(n.getMessage());
                } catch (Exception e) {
                    view.print("Клан не найден.");
                    view.print(e.getMessage());
                }
                break;//+-

            case "leader":
                try {
                    String clanLeader = clanDAO.getClanLeader(args[0]);
                    view.print(clanLeader);
                } catch (NullPointerException n) {
                    view.print("Клан пустой или лидера не пометили.");
                    view.print(n.getMessage());
                } catch (Exception e) {
                    view.print("Клан не найден.");
                    view.print(e.getMessage());
                }
                break;//+-

            case "change_leader":
                try {
                    boolean isChanged = clanDAO.changeClanLeader(args[0], args[1], args[2]);
                    if (isChanged) {
                        view.print("Лидер сменен.");
                    } else {
                        view.print("Лидер не сменен.");
                    }
                } catch (NoSuchElementException n) {
                    view.print("Клан пустой или " + n.getMessage());
                    view.print(n.getMessage());
                } catch (Exception e) {
                    view.print("Клан не найден.");
                    view.print(e.getMessage());
                }
                break;//-

            case "all":
                try {
                    List<Clan> clans = clanDAO.getAllClans(args[0]);
                    if (clans.size() > 0) {
                        for (Clan c : clans) {
                            view.print(view.toStringClan(c));
                        }
                    }
                } catch (Exception e) {
                    view.print("По запросу кланов не найдено.");
                    view.print(e.getMessage());
                }
                break;//-

            case "find":
                try {
                    Clan clan = clanDAO.findByName(args[0]);
                    if (clan != null) {
                        view.print(view.toStringClan(clan));
                    }
                } catch (Exception e) {
                    view.print("Клан не найден.");
                    view.print(e.getMessage());
                }
                break;

            default:
                System.out.println("Maybe wrong command format. Please try again.");
                view.print("Maybe wrong command format. Please try again.");
        }
    }

    //private void parseCommand() {
    private void parseCommand(String com) {
        //String command = getCommand();
        String command = com;

        String[] words = command.split(" ");
        //System.out.println(words.length);

        //if (words[0].startsWith("!exit")) System.exit(0);;

        if (!validateCommand(words)) {
            return;
        }
        String[] args = Arrays.copyOfRange(words, 2, words.length);
        System.out.println("Входние аргументи: " + Arrays.toString(args));

        switch (words[0].toLowerCase()) {
            case "!player":
                playerCommands(words[1], args);
                break;
            case "!clan":
                clanCommands(words[1], args);
                break;
            default:
                System.out.println("Maybe wrong command format. Please try again.");
        }
    }

    private String getCommand() {
        System.out.println("Enter command: ");
        String command = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            command = reader.readLine();
        } catch (IOException e) {
            //logger.error(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
        return command;
    }

    private boolean validateCommand(String[] command) {
        if (!command[0].toLowerCase().startsWith("!")) {
            System.out.println("Wrong command. Command must starts with '!'");
            return false;
        }
        if (command.length < 3) {
            System.out.println("Wrong command. Command is to short!");
            return false;
        }
        return true;
    }

    private void fillDB(int from, int to) {
        for (int i = from; i < to; i++) {
            String s = "!player save " + i;
            //parseCommand(s);
        }
    }
}
