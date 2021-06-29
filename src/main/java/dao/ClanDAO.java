package dao;

import model.Player;

import java.util.List;

public interface ClanDAO {

    List<Player> getMembers(String clanName);
    String getClanLeader(String clanName);
    List<String> getAllClans();

}
