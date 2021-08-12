import commands.ClanCommands;
import commands.PlayerCommands;
import commands.RootCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import utils.JpaUtil;
import javax.security.auth.login.LoginException;
import java.util.Arrays;

/**
 * @author Oleksandr Kononiuk
 */

public class BannerlordOnlineWhoIsWho extends ListenerAdapter {

    private final PlayerCommands player = new PlayerCommands();
    private final ClanCommands clan = new ClanCommands();
    private final RootCommands root = new RootCommands();

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

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (checkChanel(event) && checkPermissions(event)) {
            Message command = event.getMessage();
            System.out.println("Text: " + command.getContentRaw());
            String result = parseCommand(command);
            event.getChannel().sendMessage(result).queue();
        }
    }

    private boolean checkChanel(MessageReceivedEvent event) {
        //System.out.println("Chanel name: " + event.getChannel().getName());

        return event.getChannel().getName().equals("commands") || event.getChannel().getName().equals("tests");
    }

    private boolean checkPermissions(MessageReceivedEvent event) {
        if (event.getMember().getUser().isBot()) return false;

        for (Role role : event.getMember().getRoles()) {
            System.out.println("User role: " + role.getName());

            for (String perm : ACCESS_ROLE_POSITION) {
                if (role.getName().equalsIgnoreCase(perm)) return true;
            }
        }
        return false;
    }

    private String parseCommand(Message command) {
        String[] words = command.getContentRaw().split(" ");

        if (validateCommand(words)) {
            String[] args = Arrays.copyOfRange(words, 2, words.length);
            System.out.println("Input arguments: " + Arrays.toString(args));

            switch (words[0].toLowerCase()) {
                case "!player":
                    return player.command(words[1], args);
                case "!clan":
                    return clan.command(words[1], args);
                case "!update_db":
                    try {
                        return root.updateDB(command, words);
                    } catch (NumberFormatException n) {
                        return WRONG_FORMAT;
                    }
                case "!fill_db":
                    try {
                        return root.fillDB(command, words);
                    } catch (NumberFormatException n) {
                        return WRONG_FORMAT;
                    }
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
}
