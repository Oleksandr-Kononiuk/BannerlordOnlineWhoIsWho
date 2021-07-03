import dao.ClanDAO;
import dao.PlayerDAO;
import dao.impl.ClanDAOImpl;
import dao.impl.PlayerDAOImpl;
import model.Player;
import utils.DataUtils;
import utils.JpaUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class BannerlordOnlineWhoIsWho {

    private final DataUtils dataUtils = new DataUtils();
    private final PlayerDAO playerDAO = new PlayerDAOImpl();
    private final ClanDAO clanDAO = new ClanDAOImpl();

    public void test() {
        parseCommand();
    }

    public static void main(String[] args) {
        JpaUtil.init("BannerlordOnlinePlayersMySQL"); // initialize database
        BannerlordOnlineWhoIsWho BOWIW = new BannerlordOnlineWhoIsWho();
        BOWIW.test();

        //BOWIW.dataUtils.getNewPlayer(1);

    }

    private void playerCommands(String command, String[] args) {
        switch (command.toLowerCase()) {
            case "save"             : playerDAO.save(Long.parseLong(args[0]));                          break;//+
            case "find"             : playerDAO.find(args[0]);                                          break;
            case "clan"             : playerDAO.getPlayerClan(args[0]);                                 break;//+-
            case "leader"           : playerDAO.isClanLeader(args[0]);                                  break;//+
            case "twink"            : playerDAO.isTwink(args[0]);                                       break;//+
            case "all"              : playerDAO.findAll(); break; //todo фільтр по першій букві чи обмежити числом
            case "change_name"      : playerDAO.changeTempName(args[0], args[1]);                       break;//+
            //case "change_clan" : playerDAO.changeClan(args[0], args[1]); break;//todo
            case "set_leader"       : playerDAO.setClanLeader(args[0], Boolean.parseBoolean(args[1]));  break;//+-
            case "set_twink"        : playerDAO.setTwink(args[0], Boolean.parseBoolean(args[1]));       break;//+-
            case "delete"           : playerDAO.delete(args[0]);                                        break;//+

            default: System.out.println("Maybe wrong command format. Please try again.");
        }
    }

    private void clanCommands(String command, String[] args) {
        switch (command.toLowerCase()) {
            case "new"              : clanDAO.addNewClan(args[0]);                          break;
            case "delete"           : clanDAO.deleteClan(args[0]);                          break;
            case "add_member"       : clanDAO.addMember(args[0], args[1]);                  break;
            case "delete_member"    : clanDAO.deleteMember(args[0], args[1]);               break;
            case "members"          : clanDAO.getMembers(args[0]);                          break;
            case "leader"           : clanDAO.getClanLeader(args[0]);                       break;
            case "change_leader"    : clanDAO.changeClanLeader(args[0], args[1], args[2]);  break;
            case "all"              : clanDAO.getAllClans(); break;//todo фільтр по першій букві чи обмежити числом
            case "find"             : clanDAO.findByName(args[0]);                          break;

            default: System.out.println("Maybe wrong command format. Please try again.");
        }
    }

    private void parseCommand() {
        String command = getCommand();

        String[] words = command.split(" ");
        //System.out.println(words.length);
        if (!validateCommand(words)) {
            return;
        }
        String[] args = Arrays.copyOfRange(words, 2, words.length);
        System.out.println(Arrays.toString(args));

        switch (words[0].toLowerCase()) {
            case "!player" :    playerCommands(words[1], args);   break;
            case "!clan" :      clanCommands(words[1], args);     break;
            default: System.out.println("Maybe wrong command format. Please try again.");
        }
    }

    private String getCommand() {
        System.out.println("Enter command: ");
        String command = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            command = reader.readLine();
        } catch (IOException e){
            //logger.error(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
        return command;
    }

    private boolean validateCommand(String[] command) {
        if (!command[0].toLowerCase().startsWith("!")) {
            System.out.println("Wrong command. Command must starts with!");
            return false;
        }
        if (command.length < 3) {
            System.out.println("Wrong command. Command is to short!");
            return false;
        }
        return true;
    }
}
