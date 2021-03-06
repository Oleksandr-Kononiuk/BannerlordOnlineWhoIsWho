package dao.impl;

import dao.PlayerDAO;
import model.Clan;
import model.Player;
import utils.DataUtils;
import utils.JpaUtil;
import java.util.Arrays;
import java.util.List;

/**
 *@author  Oleksandr Kononiuk
 */

public class PlayerDAOImpl implements PlayerDAO {

    private DataUtils dataUtils = new DataUtils();

    @Override
    public boolean save(String id) {
        Player newPlayer = dataUtils.getNewPlayer(id);

        if (newPlayer != null) {
            return JpaUtil.performReturningWithinPersistenceContext(
                    em -> {
                        em.persist(newPlayer);
                        return true;
                    }
            );
        } else {
            throw new NullPointerException("Метод getNewPlayer(id) вернул NULL");
        }
    }

    @Override
    public Player find(String[] playerIdOrName) {
        String s = buildStringFromArgs(playerIdOrName);
        Player p = getPlayer(s);
        System.out.println(p.toString());
        return p;
    }

    @Override
    public Clan getPlayerClan(String[] playerIdOrName) {
        String s = buildStringFromArgs(playerIdOrName);
        Player player = getPlayer(s);
        return player.getClan();
    }

    @Override
    public boolean isClanLeader(String[] playerIdOrName) {
        String s = buildStringFromArgs(playerIdOrName);
        Player player = getPlayer(s);
        return player.isClanLeader();
    }

    @Override
    public boolean isTwink(String[] playerIdOrName) {
        String s = buildStringFromArgs(playerIdOrName);
        Player player = getPlayer(s);
        return player.isTwink();
    }

    @Override
    public List<Player> findAll(String filter) { //return all players which name starts on filter
        String query = "select p from Player p where p.temp_name LIKE '" + filter + "%'";
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery(query, Player.class)
                        .setMaxResults(9)
                        .getResultList()
        );
    }

    @Override
    public boolean changeTempName(Long playerId, String[] newNameArray) {
        String newName = buildStringFromArgs(Arrays.copyOfRange(newNameArray, 1, newNameArray.length));
        Player old = findById(playerId);
        String oldName = old.getTempName();

        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Player merged = em.merge(old);
                    merged.setTempName(newName);

                    if (!merged.getOldNames().contains(oldName))
                        merged.getOldNames().add(oldName);
                    return newName.equals(merged.getTempName());
                }
        );
    }

    @Override
    public boolean changeClan(Long playerId, String newClanName) {
        Player player = findById(playerId);
        String oldClanName = (player.getClan() == null ? "" : player.getClan().getClanName());
        Clan newClan = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c where c.clan_name = :clanName", Clan.class)
                        .setParameter("clanName", newClanName)
                        .getSingleResult()
        );
        player.setClan(newClan);
        player.setClanLeader(false);
        update(player);

        return oldClanName.equals(player.getClan().getClanName());
    }

    @Override
    public boolean setArmy(int army, String[] playerIdOrName) {
        if (army < 0) throw new IllegalArgumentException();

        String s = buildStringFromArgs(Arrays.copyOfRange(playerIdOrName, 1, playerIdOrName.length));
        Player player = getPlayer(s);

        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Player merged = em.merge(player);
                    merged.setArmy(army);
                    return true;
                }
        );
    }

    @Override
    public boolean setClanLeader(boolean status, String[] nameArray) {
        String s = buildStringFromArgs(Arrays.copyOfRange(nameArray, 1, nameArray.length));
        Player player = getPlayer(s);
        if (player.getClan() != null) {
            boolean oldStatus = player.isClanLeader();
            player.setClanLeader(status);
            Player updatedPlayer = update(player);

            return oldStatus == updatedPlayer.isClanLeader();//true не змінилось
        } else
            return true;
    }

    @Override
    public boolean setTwink(boolean status, String[] nameArray) {
        String s = buildStringFromArgs(Arrays.copyOfRange(nameArray, 1, nameArray.length));
        Player player = getPlayer(s);
        boolean oldStatus = player.isTwink();
        player.setTwink(status);
        Player updatedPlayer = update(player);

        return oldStatus == updatedPlayer.isTwink();//true не змінилось
    }

    private Player update(Player player) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.merge(player)
        );
    }

    @Override
    public boolean delete(Long playerId) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Player ref = em.getReference(Player.class, playerId);
                    Player merged = em.merge(ref);
                    em.remove(merged);
                    return true;
                }
        );
    }

    @Override
    public boolean update(Long playerId) {
        Player forumPlayer = dataUtils.getNewPlayer(Long.toString(playerId));
        Player dbPlayer = findById(playerId);

        if (forumPlayer == null && dbPlayer != null) throw new NullPointerException("Player not found in forum but he is in DB.");
        if (forumPlayer != null && dbPlayer == null) {
            save(Long.toString(playerId));
            return true;
        }
        if (dbPlayer == null && forumPlayer == null) throw new NullPointerException("Player not found in DB and forum.");

        String newName = forumPlayer.getTempName();
        String oldName = dbPlayer.getTempName();
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Player merged = em.merge(dbPlayer);
                    merged.setTempName(newName);

                    if (!merged.getOldNames().contains(oldName))
                        merged.getOldNames().add(oldName);
                    return true;
                }
        );
    }

    public String buildStringFromArgs(String[] array) {
        String result = "";

        for (String s : array) {
            result = result.concat(s);
            result = result.concat(" ");
        }
        System.out.println("buildStringFromArgs " + result);
        return result.trim();
    }

    private boolean isId(String playerIdOrName) {
        if (playerIdOrName.matches("\\D")) { //contain any non-digit character
            return false;
        }
        return playerIdOrName.matches("^\\d+$"); //is contain digit character with 1+ length
    }

    public Player getPlayer(String playerIdOrName) {
        Player player;
        if (isId(playerIdOrName)) {
            player = findById(Long.parseLong(playerIdOrName));
        } else {
            player = findByTempName(playerIdOrName);
        }
        return player;
    }

    private Player findById(long id) {
        Player player;
        player = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p where p.id = :id", Player.class)
                        .setParameter("id", id)
                        .getSingleResult()
        );
        return player;
    }

    private Player findByTempName(String tempName) {
        Player player;
        player = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p where p.temp_name = :tempName", Player.class)
                        .setParameter("tempName", tempName)
                        .getSingleResult()
        );
        return player;
    }
}
