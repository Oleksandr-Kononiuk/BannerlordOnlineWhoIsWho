package dao;

import model.Clan;
import model.Player;

import java.util.List;

public interface PlayerDAO {

    boolean save(long id);
    Player find(String playerIdOrName);
    Clan getPlayerClan(String playerIdOrName);
    boolean isClanLeader(String playerIdOrName);
    void setClanLeader(String playerIdOrName, boolean status);
    boolean isTwink(String playerIdOrName);
    void setTwink(String playerIdOrName, boolean status);
    List<Player> findAll(String filter);
    boolean changeTempName(String playerOldTempNameOrId, String newName);
    boolean changeClan(String playerIdOrName, String newClan);
    void delete(String playerIdOrName);
}
