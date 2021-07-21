package dao;

import model.Clan;

import java.util.List;
import java.util.Map;

/**
 *@author  Oleksandr Kononiuk
 */

public interface ClanDAO {

    void addNewClan(String clanName);
    boolean deleteClan(String clanName);
    boolean addMember(String clanName, String[] playerIdOrName);
    boolean deleteMember(String clanName,long id);
    String getClanLeader(String clanName);
    boolean changeClanLeader(String clanName, long oldId, long newId);
    List<Clan> getAllClans(String filter);
    Clan findByName(String clanName);
    boolean setRelation(String clanName, int relation);
    Map<Integer, List<Clan>> buildDiplomacy();
    //todo add method which builds diplomacy map
}
