import bot.Bot;
import dao.ClanDAO;
import dao.PlayerDAO;
import dao.impl.ClanDAOImpl;
import dao.impl.PlayerDAOImpl;
import model.Clan;
import model.Player;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import utils.DataUtils;
import utils.JpaUtil;
import utils.View;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class BannerlordOnlineWhoIsWho extends ListenerAdapter {

    private final DataUtils dataUtils = new DataUtils();
    private final PlayerDAO playerDAO = new PlayerDAOImpl();
    private final ClanDAO clanDAO = new ClanDAOImpl();
    private final View view = new View();

    private static final String BOT_TOKEN = "ODYxOTI0MjcwMDYxMjU2NzA0.YOQ3hw.h8tBbMsPKYog0pKr8aY-nypu4Os";
    private static final int ACCESS_ROLE_POSITION = 1;
    private static final String WRONG_FORMAT = "> Возможно неправильный формат команды. Попробуйте еще раз.";

    public static void main(String[] args) {
        JpaUtil.init("BannerlordOnlinePlayersMySQL"); // initialize database
        BannerlordOnlineWhoIsWho BOWIW = new BannerlordOnlineWhoIsWho();

        try {
            JDA jda = JDABuilder.createDefault(BOT_TOKEN).build(); // initialize bot
            jda.addEventListener(BOWIW);
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

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

    public static JDA init() {
        JDA jda = null;
        try {
            jda = JDABuilder.createDefault(BOT_TOKEN).build();
            jda.addEventListener(new Bot());
        } catch (LoginException e) {
            e.printStackTrace();
        }
        return jda;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (checkChanel(event) && checkPermissions(event)) {
            System.out.println("Chanel name: " + event.getChannel().getName());

            String command = event.getMessage().getContentRaw();
            System.out.println("Text: " + command);

            String result = parseCommand(command);

            event.getChannel().sendMessage(result).queue();
        } else {
            System.out.println("Wrong chanel or user don`t have permissions!");
        }
    }

    private boolean checkChanel(MessageReceivedEvent event) {
        System.out.println("Chanel name: " + event.getChannel().getName());

        return event.getChannel().getName().equals("commands");
    }

    private boolean checkPermissions(MessageReceivedEvent event) {
        List<Role> userRoles = event.getMember().getRoles();

        for (Role role : userRoles) {
            System.out.println("User role: " + role.getName());
            System.out.println("User role position: " + role.getPosition());
        }
        for (Role role : event.getMember().getRoles()) {
            if (role.getPosition() == ACCESS_ROLE_POSITION) return true;
        }
        return false;
    }

    private String playerCommands(String command, String[] args) {
        switch (command.toLowerCase()) {

            case "save":
                try {
                    playerDAO.save(Long.parseLong(args[0]));
                    return "> Игрок добавлен.";
                } catch (PersistenceException p) {
                    return "> Игрок уже существует в базе.";
                } catch (NumberFormatException n) {
                    return "> Входной параметр не цифра.";
                } catch (IllegalArgumentException i) {
                    return "> Id не может быть отрицательным.";
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "find":
                try {
                    Player p = playerDAO.find(args[0]);
                    return view.toStringPlayer(p);
                } catch (Exception e) {
                    e.printStackTrace();
                    return playerNotFoundString(e);
                }
            case "clan":
                try {
                    Clan clan = playerDAO.getPlayerClan(args[0]);
                    if (clan == null) {
                        return "> Игрок не состоит в клане.";
                    } else {
                        return view.toStringClan(clan);
                    }
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "leader":
                try {
                    boolean isLeader = playerDAO.isClanLeader(args[0]);
                    return String.format("> Игрок%s лидер клана.", (isLeader ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "twink":
                try {
                    boolean isTwink = playerDAO.isTwink(args[0]);
                    return String.format("> Аккаунт%s твинк.", (isTwink ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "all":
                try {
                    List<Player> players = playerDAO.findAll(args[0]);
                    if (players.size() > 0) {
                        for (Player p : players) {
                            return view.toStringPlayer(p);
                        }
                    }
                } catch (Exception e) {
                    return "> По запросу игроков не найдено. Причина: " + e.getMessage();
                }
            case "change_name":
                try {
                    boolean isChanged = playerDAO.changeTempName(args[0], args[1]);
                    return String.format("> Имя игрока%s изменено.", (isChanged ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "change_clan":
                try {
                    boolean isChanged = playerDAO.changeClan(args[0], args[1]);
                    return String.format("> Клан игрока%s изменен.", (isChanged ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "set_leader":
                try {
                    boolean isChanged = playerDAO.setClanLeader(args[0], Boolean.parseBoolean(args[1]));
                    return String.format("> Статус лидера%s изменен.", (isChanged ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "set_twink":
                try {
                    boolean isChanged = playerDAO.setTwink(args[0], Boolean.parseBoolean(args[1]));
                    return String.format("> Статус твинка%s изменен.", (isChanged ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            case "delete":
                try {
                    boolean isDeleted = playerDAO.delete(args[0]);
                    return String.format("> Игрок%s удален.", (isDeleted ? "" : " не"));
                } catch (Exception e) {
                    return playerNotFoundString(e);
                }
            default:
                System.out.println("Maybe wrong command format. Please try again.");
                return WRONG_FORMAT;
        }
    }


    private String clanCommands(String command, String[] args) {
        switch (command.toLowerCase()) {
            case "new":
                try {
                    clanDAO.addNewClan(args[0]);
                    return "> Клан добавлен.";
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "delete":
                try {
                    boolean isDeleted = clanDAO.deleteClan(args[0]);
                    return String.format("> Клан%s удален.", (isDeleted ? "" : " не"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return clanNotFoundString(e);
                }
            case "add_member":
                try {
                    boolean isAdded = clanDAO.addMember(args[0], args[1]);
                    return String.format("> Игрок%s добавлен в состав клана.", (isAdded ? "" : " не"));
                } catch (NoResultException n) {
                    return playerNotFoundString(n);
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "delete_member":
                try {
                    boolean isDeleted = clanDAO.deleteMember(args[0], args[1]);
                    return String.format("> Игрок%s удален из состав клана.", (isDeleted ? "" : " не"));
                } catch (NoResultException n) {
                    return playerNotFoundString(n);
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "leader":
                try {
                    String clanLeader = clanDAO.getClanLeader(args[0]);
                    return "Лидер клана: " + clanLeader;//todo  asdasdadadsadasd
                } catch (NullPointerException n) {
                    return "> Клан пустой или лидера не пометили. " + n.getMessage();
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "change_leader":
                try {
                    boolean isChanged = clanDAO.changeClanLeader(args[0], args[1], args[2]);
                    return String.format("> Лидер%s сменен.", (isChanged ? "" : " не"));
                } catch (NoSuchElementException n) {
                    return "> Клан пустой или " + n.getMessage();
                } catch (Exception e) {
                    return clanNotFoundString(e);
                }
            case "all":
                try {
                    List<Clan> clans = clanDAO.getAllClans(args[0]);
                    if (clans.size() > 0) {
                        for (Clan c : clans) {
                            return view.toStringClan(c);
                        }
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
            default:
                System.out.println("Maybe wrong command format. Please try again.");
                return WRONG_FORMAT;
        }
    }

    private String parseCommand(String command) {
        //String command = getCommand();

        String[] words = command.split(" ");
        //System.out.println(words.length);
        //if (words[0].startsWith("!exit")) System.exit(0);;

        if (validateCommand(words)) {
            String[] args = Arrays.copyOfRange(words, 2, words.length);
            System.out.println("Входные аргументы: " + Arrays.toString(args));

            switch (words[0].toLowerCase()) {
                case "!player":
                    return playerCommands(words[1], args);
                case "!clan":
                    return clanCommands(words[1], args);
                default:
                    return WRONG_FORMAT;
            }
        } else {
            return WRONG_FORMAT;
        }
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

    private String clanNotFoundString(Exception e) {
        return String.format("> Клан не найден. " +
                "Причина: %s", e.getMessage());
    }

    private String playerNotFoundString(Exception e) {
        return String.format("> Игрок не найден. " +
                "Причина: %s", e.getMessage());
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

    private void fillDB(int from, int to) {
        for (int i = from; i < to; i++) {
            String s = "!player save " + i;
            //parseCommand(s);
            try {
                Thread.sleep(5); //timeout
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
