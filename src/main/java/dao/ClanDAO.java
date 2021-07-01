package dao;

import model.Clan;
import model.Player;

import java.util.List;

public interface ClanDAO {

    void addNewClan(String clanName);
    void deleteClan(String clanName);
    void addMember(String clanName, String playerIdOrName);
    boolean deleteMember(String clanName, String playerIdOrName);
    List<Player> getMembers(String clanName);
    String getClanLeader(String clanName);
    boolean changeClanLeader(String clanName, String oldLeaderIdOrName, String newLeaderIdOrName);
    List<String> getAllClans();


}
