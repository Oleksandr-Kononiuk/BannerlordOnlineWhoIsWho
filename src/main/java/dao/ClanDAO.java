package dao;

import model.Player;

import java.util.List;

public interface ClanDAO {

    void addMember(Player player);
    boolean deleteMember(Player player);
    List<Player> getMembers(String clanName);
    String getClanLeader(String clanName);
    List<String> getAllClans();


}
