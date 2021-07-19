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
import javax.persistence.RollbackException;
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
    private static final String[] ACCESS_ROLE_POSITION = new String[]{"Князь", "Бояре", "Рекрутёр", "Diplomat", "Великий Князь"};
    private static final String WRONG_FORMAT = "> Возможно неправильный формат команды. Попробуйте еще раз.";

    public static void main(String[] args) {
        JpaUtil.init("BO_dev"); // initialize database
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

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (checkChanel(event) && checkPermissions(event)) {
            if (checkMe(event) && event.getMessage().getContentRaw().startsWith("fill_db")) {
                System.out.println("Morgan_Black(Саня)#2160 authorized." );
                String[] words = event.getMessage().getContentRaw().split(" ");
                System.out.println("Fill DB from player ID:" + words[1] + " to:" + words[2]);
                fillDB(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                event.getChannel().sendMessage("Building DB completed").queue();
            } else
            if (checkMe(event) && event.getMessage().getContentRaw().startsWith("update_db")){
                System.out.println("Morgan_Black(Саня)#2160 authorized." );
                String[] words = event.getMessage().getContentRaw().split(" ");
                System.out.println("Update DB from player ID:" + words[1] + " to:" + words[2]);
                updateDB(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                event.getChannel().sendMessage("Update completed").queue();
            } else {
                //System.out.println("Chanel name: " + event.getChannel().getName());

                String command = event.getMessage().getContentRaw();
                System.out.println("Text: " + command);

                String result = parseCommand(command);

                event.getChannel().sendMessage(result).queue();
            }
        }
    }

    private boolean checkChanel(MessageReceivedEvent event) {
        //System.out.println("Chanel name: " + event.getChannel().getName());

        return event.getChannel().getName().equals("commands") || event.getChannel().getName().equals("feedback");
    }

    private boolean checkPermissions(MessageReceivedEvent event) {
        if (event.getMember().getUser().isBot()) return false;

//        List<Role> userRoles = event.getMember().getRoles();
        for (Role role : event.getMember().getRoles()) {
            System.out.println("User role: " + role.getName());

            for (String perm : ACCESS_ROLE_POSITION) {
                if (role.getName().equalsIgnoreCase(perm)) return true;
            }
        }
        return false;
    }

    private boolean checkMe(MessageReceivedEvent event) {
//        System.out.println(event.getMember().getUser().getAsTag());
//        System.out.println(event.getMember().getUser().getName());
        if (event.getMember().getUser().getAsTag().equals("Morgan_Black(Саня)#2160")
                && event.getMember().getUser().getName().equals("Morgan_Black(Саня)")) {
            return true;
        }
        return false;
    }

    private String playerCommands(String command, String[] args) {
        switch (command.toLowerCase()) {

            case "save"://+-
                try {
                    playerDAO.save(Long.parseLong(args[0]));
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
                            if (count > 8) break; // first 9 result line

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


    private String clanCommands(String command, String[] args) {
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
                        //int count = 0;
                        for (Clan c : clans) {
                            //if (count > 8) break; // first 9 result line

                            resultOut.append(view.toStringClan(c));
                            resultOut.append("\n");
                            //count++;
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
            System.out.println("Input arguments: " + Arrays.toString(args));

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
                "Причина: %s %s", e.getClass().getName(), e.getMessage());
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
            String command = "!player save " + i;
            parseCommand(command);
            try {
                Thread.sleep(10); //timeout
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDB(int from, int to) {
        for (int i = from; i <= to; i++) {
            String command = "!player update " + i;
            parseCommand(command);
            try {
                Thread.sleep(10); //timeout
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
