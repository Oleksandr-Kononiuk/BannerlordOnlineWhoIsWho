package dao.impl;

import dao.PlayerDAO;
import model.Player;

import java.util.List;

public class PlayerDAOImpl implements PlayerDAO {
    @Override
    public void save(Player player) {

    }

    @Override
    public Player findById(long id) {
        return null;
    }

    @Override
    public Player findByTempName(String tempName) {
        return null;
    }

    @Override
    public Player findByTMainName(String mainName) {
        return null;
    }

    @Override
    public String getPlayerClan(Player player) {
        return null;
    }

    @Override
    public boolean isClanLeader(Player player) {
        return false;
    }

    @Override
    public void makeClanLeader(Player player) {

    }

    @Override
    public boolean isTwink(Player player) {
        return false;
    }

    @Override
    public void markAsTwink(Player player) {

    }

    @Override
    public List<Player> findAll() {
        return null;
    }

    @Override
    public void changeMainName(Player player, String newName) {

    }

    @Override
    public void changeTempName(Player player, String newName) {

    }

    @Override
    public void changeClanName(Player player, String newName) {

    }

    @Override
    public void update(Player player, String changes) {

    }

    @Override
    public void delete(Player player) {

    }
}
