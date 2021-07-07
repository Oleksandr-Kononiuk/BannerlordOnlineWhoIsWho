package dao;

import model.Clan;
import model.Player;

import java.util.List;

public interface PlayerDAO {

    boolean save(long id);
    Player find(String playerIdOrName);
    Clan getPlayerClan(String playerIdOrName);
    boolean isClanLeader(String playerIdOrName);
    boolean setClanLeader(String playerIdOrName, boolean status);
    boolean isTwink(String playerIdOrName);
    boolean setTwink(String playerIdOrName, boolean status);
    List<Player> findAll(String filter);
    boolean changeTempName(String playerOldTempNameOrId, String[] newName);
    boolean changeClan(String playerIdOrName, String newClan);
    boolean delete(Long playerId);
}
