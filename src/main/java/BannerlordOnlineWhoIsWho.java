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
                    return view.print("Игрок добавлен.");
                } catch (PersistenceException p) {
                    return view.print("Игрок уже существует в базе.");
                } catch (NumberFormatException n) {
                    return view.print("Входной параметр не цифра.");
                } catch (IllegalArgumentException i) {
                    return view.print("Id не может быть отрицательным.");
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "find":
                try {
                    Player p = playerDAO.find(args[0]);
                    return view.print(view.toStringPlayer(p));
                } catch (Exception e) {
                    e.printStackTrace();
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "clan":
                try {
                    Clan clan = playerDAO.getPlayerClan(args[0]);
                    if (clan == null) {
                        return view.print("Игрок не состоит в клане.");
                    } else {
                        return view.print(view.toStringClan(clan));
                    }
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "leader":
                try {
                    boolean isLeader = playerDAO.isClanLeader(args[0]);
                    if (isLeader) {
                        return view.print("Игрок лидер клана.");
                    } else {
                        return view.print("Игрок не лидер клана.");
                    }
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "twink":
                try {
                    boolean isTwink = playerDAO.isTwink(args[0]);
                    if (isTwink) {
                        return view.print("Аккаунт твинк.");
                    } else {
                        return view.print("Аккаунт не твинк.");
                    }
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "all":
                try {
                    List<Player> players = playerDAO.findAll(args[0]);
                    if (players.size() > 0) {
                        for (Player p : players) {
                            return view.print(view.toStringPlayer(p));
                        }
                    }
                } catch (Exception e) {
                    return view.print("По запросу игроков не найдено. " + e.getMessage());
                }
            case "change_name":
                try {
                    boolean isChanged = playerDAO.changeTempName(args[0], args[1]);
                    if (!isChanged) {
                        return view.print("Имя игрока изменено.");
                    } else {
                        return view.print("Имя игрока не изменено.");
                    }
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "change_clan":
                try {
                    boolean isChanged = playerDAO.changeClan(args[0], args[1]);

                    if (!isChanged) {
                        return view.print("Клан игрока изменен.");
                    } else {
                        return view.print("Клан игрока не изменен.");
                    }
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "set_leader":
                try {
                    boolean isChanged = playerDAO.setClanLeader(args[0], Boolean.parseBoolean(args[1]));
                    if (!isChanged) {
                        return view.print("Статус лидера изменен.");
                    } else {
                        return view.print("Статус лидера не изменен.");
                    }
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "set_twink":
                try {
                    boolean isChanged = playerDAO.setTwink(args[0], Boolean.parseBoolean(args[1]));
                    if (!isChanged) {
                        return view.print("Статус твинка изменен.");
                    } else {
                        return view.print("Статус твинка не изменен.");
                    }
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            case "delete":
                try {
                    boolean isDeleted = playerDAO.delete(args[0]);
                    if (isDeleted) {
                        return view.print("Игрок удален.");
                    } else {
                        return view.print("Игрок не удален.");
                    }
                } catch (Exception e) {
                    return view.print("Игрок не найден. " + e.getMessage());
                }
            default:
                System.out.println("Maybe wrong command format. Please try again.");
                return view.print("Maybe wrong command format. Please try again.");
        }
    }

    private String clanCommands(String command, String[] args) {
        switch (command.toLowerCase()) {
            case "new":
                try {
                    clanDAO.addNewClan(args[0]);
                    return view.print("Клан добавлен.");
                } catch (Exception e) {
                    return view.print("Клан не найден. " + e.getMessage());
                }
            case "delete":
                try {
                    boolean isDeleted = clanDAO.deleteClan(args[0]);
                    if (isDeleted) {
                        return view.print("Клан удален.");
                    } else {
                        return view.print("Клан не удален.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return view.print("Клан не найден. " + e.getMessage());
                }
            case "add_member":
                try {
                    boolean isAdded = clanDAO.addMember(args[0], args[1]);
                    if (!isAdded) {
                        return view.print("Игрок добавлен в состав клана.");
                    } else {
                        return view.print("Игрок не добавлен в состав клана.");
                    }
                } catch (NoResultException n) {
                    return view.print("Игрок не найден. " + n.getMessage());
                } catch (Exception e) {
                    return view.print("Клан не найден. " + e.getMessage());
                }
            case "delete_member":
                try {
                    boolean isDeleted = clanDAO.deleteMember(args[0], args[1]);
                    if (!isDeleted) {
                        return view.print("Игрок удален из состав клана.");
                    } else {
                        return view.print("Игрок не удален из состав клана.");
                    }
                } catch (NoResultException n) {
                    return view.print("Игрок не найден. " + n.getMessage());
                } catch (Exception e) {
                    return view.print("Клан не найден. " + e.getMessage());
                }
            case "leader":
                try {
                    String clanLeader = clanDAO.getClanLeader(args[0]);
                    return view.print(clanLeader);
                } catch (NullPointerException n) {
                    return view.print("Клан пустой или лидера не пометили. " + n.getMessage());
                } catch (Exception e) {
                    return view.print("Клан не найден. " + e.getMessage());
                }
            case "change_leader":
                try {
                    boolean isChanged = clanDAO.changeClanLeader(args[0], args[1], args[2]);
                    if (isChanged) {
                        return view.print("Лидер сменен.");
                    } else {
                        return view.print("Лидер не сменен.");
                    }
                } catch (NoSuchElementException n) {
                    return view.print("Клан пустой или " + n.getMessage());
                } catch (Exception e) {
                    return view.print("Клан не найден. " + e.getMessage());
                }
            case "all":
                try {
                    List<Clan> clans = clanDAO.getAllClans(args[0]);
                    if (clans.size() > 0) {
                        for (Clan c : clans) {
                            return view.print(view.toStringClan(c));
                        }
                    }
                } catch (Exception e) {
                   return view.print("По запросу кланов не найдено. " + e.getMessage());
                }
            case "find":
                try {
                    Clan clan = clanDAO.findByName(args[0]);
                    if (clan != null) {
                        return view.print(view.toStringClan(clan));
                    }
                } catch (Exception e) {
                    return view.print("Клан не найден. " + e.getMessage());
                }
            default:
                System.out.println("Maybe wrong command format. Please try again.");
                return view.print("Maybe wrong command format. Please try again.");
        }
    }

    //private void parseCommand() {
    private String parseCommand(String command) {
        //String command = getCommand();
        //String command = com;

        String[] words = command.split(" ");
        //System.out.println(words.length);

        //if (words[0].startsWith("!exit")) System.exit(0);;

        if (validateCommand(words)) {
            String[] args = Arrays.copyOfRange(words, 2, words.length);
            System.out.println("Входние аргументи: " + Arrays.toString(args));

            switch (words[0].toLowerCase()) {
                case "!player":
                    return playerCommands(words[1], args);
                case "!clan":
                    return clanCommands(words[1], args);
                default:
                    return "Maybe wrong command format. Please try again.";
            }
        } else {
            return "Maybe wrong command format. Please try again.";
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
            try {
                Thread.sleep(5); //timeout
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
