package dao;

import model.Player;

import java.util.List;

public interface PlayerDAO {

    void save(Player player);
    Player findById(long id);
    Player findByTempName(String tempName);
    Player findByTMainName(String mainName);
    String getPlayerClan(Player player);
    boolean isClanLeader(Player player);
    void makeClanLeader(Player player);
    boolean isTwink(Player player);
    void markAsTwink(Player player);
    List<Player> findAll();
    void changeMainName(Player player, String newName);
    void changeTempName(Player player, String newName);
    void changeClanName(Player player, String newName);
    void update(Player player, String changes);
    void delete(Player player);
}
