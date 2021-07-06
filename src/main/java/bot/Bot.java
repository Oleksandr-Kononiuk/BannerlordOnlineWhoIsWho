package bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Bot extends ListenerAdapter {
    private static final String BOT_TOKEN = "ODYxOTI0MjcwMDYxMjU2NzA0.YOQ3hw.h8tBbMsPKYog0pKr8aY-nypu4Os";
    private static final int ACCESS_ROLE_POSITION = 1;

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
}
