package commands;

import dao.PlayerDAO;
import dao.impl.PlayerDAOImpl;
import net.dv8tion.jda.api.entities.Message;

import javax.persistence.RollbackException;

/**
 * @author Oleksandr Kononiuk
 */

public class RootCommands {
    private final PlayerDAO playerDAO = new PlayerDAOImpl();

    public String updateDB(Message command, String[] words) {
        if (checkMe(command.getMember().getUser().getAsTag())) {
            System.out.println("Morgan_Black(Саня)#2160 authorized." );
            System.out.println("Updating DB from player ID: " + words[1] + " to: " + words[2]);

            for (long i = Long.parseLong(words[1]); i <= Long.parseLong(words[2]); i++) {
                try {
                    playerDAO.update(i);
                } catch (NullPointerException n) {
                    System.out.println(n.getMessage() + " " + i);
                } catch (Exception n) {
                    System.out.println(n.getMessage() + " " + i);
                }
                try {
                    Thread.sleep(100); //timeout
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return String.format("> Database was updated for player ID`s %s - %s", words[1], words[2]);
        }
        return "> You are not allowed to use this command";
    }

    public String fillDB(Message command, String[] words) {
        if (checkMe(command.getMember().getUser().getAsTag())) {
            System.out.println("Morgan_Black(Саня)#2160 authorized." );
            System.out.println("Filling DB from player ID:" + words[1] + " to:" + words[2]);

            for (long i = Long.parseLong(words[1]); i <= Long.parseLong(words[2]); i++) {
                try {
                    playerDAO.save(Long.toString(i));
                } catch (RollbackException p) {
                    System.out.println("Игрок уже существует в базе.");
                } catch (Exception n) {
                    System.out.println(n.getMessage() + " " + i);
                }
                try {
                    Thread.sleep(100); //timeout
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return String.format("> Database was filled for player ID`s %s - %s", words[1], words[2]);
        }
        return "> You are not allowed to use this command";
    }

    private boolean checkMe(String tag) {
        return tag.equals("Morgan_Black(Саня)#2160");
    }
}
