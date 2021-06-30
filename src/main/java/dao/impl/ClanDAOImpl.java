package dao.impl;

import dao.ClanDAO;
import model.Player;

import java.util.List;

public class ClanDAOImpl implements ClanDAO {
    @Override
    public void addMember(Player player) {

    }

    @Override
    public boolean deleteMember(Player player) {
        return false;
    }

    @Override
    public List<Player> getMembers(String clanName) {
        return null;
    }

    @Override
    public String getClanLeader(String clanName) {
        return null;
    }

    @Override
    public List<String> getAllClans() {
        return null;
    }
}
