package dao;

import model.Clan;
import model.Player;

import java.util.List;

public interface PlayerDAO {

    void save(long id);
    Player find(String playerIdOrName);
    String getPlayerClan(String playerIdOrName);
    boolean isClanLeader(String playerIdOrName);
    void setClanLeader(String playerIdOrName, boolean status);
    boolean isTwink(String playerIdOrName);
    void setTwink(String playerIdOrName, boolean status);
    List<Player> findAll();
    void changeTempName(String playerOldTempNameOrId, String newName);
    void changeClan(String playerIdOrName, Clan newClan);
    void delete(String playerIdOrName);
}
