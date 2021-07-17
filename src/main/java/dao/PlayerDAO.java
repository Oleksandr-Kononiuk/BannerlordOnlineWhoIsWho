package dao;

import model.Clan;
import model.Player;

import java.util.List;

public interface PlayerDAO {

    boolean save(long id);
    Player find(String[] playerIdOrName);
    Clan getPlayerClan(String[] playerIdOrName);
    boolean isClanLeader(String[] playerIdOrName);
    boolean isTwink(String[] playerIdOrName);
    boolean setClanLeader(boolean status, String[] playerIdOrName);
    boolean setTwink(boolean status, String[] playerIdOrName);
    List<Player> findAll(String filter);
    boolean changeTempName(Long playerId, String[] newName);
    boolean changeClan(Long playerId, String newClan);
    boolean setArmy(int army, String[] playerIdOrName);
    boolean delete(Long playerId);
    boolean update(Long playerId);
}
