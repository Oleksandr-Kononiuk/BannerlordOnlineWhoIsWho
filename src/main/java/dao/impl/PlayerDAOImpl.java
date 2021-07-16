package dao.impl;

import dao.PlayerDAO;
import model.Clan;
import model.Player;
import utils.DataUtils;
import utils.JpaUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDAOImpl implements PlayerDAO {

    private DataUtils dataUtils = new DataUtils();

    @Override
    public boolean save(long id) {
        if (id < 0) throw new IllegalArgumentException("Id не может быть отрицательным.");

        Player newPlayer = dataUtils.getNewPlayer(id);

        if (newPlayer != null) {
            JpaUtil.performWithinPersistenceContext(
                    em -> em.persist(newPlayer)
            );
        } else {
            throw new NullPointerException("Метод getNewPlayer(id) вернул NULL");
        }
        return true;
    }

    @Override
    public Player find(String[] playerIdOrName) {
        String s = buildStringFromArgs(playerIdOrName);
        return getPlayer(s);
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
    public List<Player> findAll(String filter) {
        List<Player> allPlayers = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p", Player.class)
                        .getResultList()
        );

        if (filter.matches("\\d{1,}")) {        //return N first players
            return allPlayers.stream()
                    .limit(Long.parseLong(filter))
                    .collect(Collectors.toList());
        } else {
            return allPlayers.stream()              //return all players which name starts on filter
                    .filter(player -> player.getTempName().startsWith(filter))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean changeTempName(Long playerId, String[] newNameArray) {
        Player player = findById(playerId);//todo робить якийсь дивний зайвий селект
        String oldName = player.getTempName();
        String newName = buildStringFromArgs(Arrays.copyOfRange(newNameArray, 1, newNameArray.length));
        player.setTempName(newName);
        update(player);

        return oldName.equals(newName);
    }

    @Override
    public boolean changeClan(Long playerId, String newClanName) {
        Player player = findById(playerId);
        String oldClanName = (player.getClan() == null ? "" : player.getClan().getClanName());
        //System.out.println(oldClanName);
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
    public boolean setClanLeader(boolean status, String[] newNameArray) {
        String s = buildStringFromArgs(Arrays.copyOfRange(newNameArray, 1, newNameArray.length));
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
    public boolean setTwink(boolean status, String[] newNameArray) {
        String s = buildStringFromArgs(Arrays.copyOfRange(newNameArray, 1, newNameArray.length));
        Player player = getPlayer(s);
        boolean oldStatus = player.isTwink();
        player.setTwink(status);
        Player updatedPlayer = update(player);

        return oldStatus == updatedPlayer.isTwink();//true не змінилось
    }

    public Player update(Player player) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.merge(player)
        );
    }

    @Override
    public boolean delete(Long playerId) {
        Player playerToDelete = findById(playerId);

        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Player merged = em.merge(playerToDelete);
                    em.remove(merged);
                    return true;
                }
        );
    }

    @Override
    public boolean update(Long playerId) {
        Player updated = dataUtils.getNewPlayer(playerId);
        Player player = getPlayer(playerId.toString());

        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Player merged = em.merge(player);
                    merged.setTempName(updated.getTempName());
                    merged.setArmy(player.getArmy() == null ? 0 : player.getArmy());
                    return true;
                }
        );
    }

    public String buildStringFromArgs(String[] array) {
        String result = "";

        for (int i = 0; i < array.length; i++) {
            result = result.concat(array[i]);
            result = result.concat(" ");
        }
        System.out.println("buildStringFromArgs " + result);
        return result.trim();
    }

    private boolean isId(String playerIdOrName) {
        if (playerIdOrName.matches("\\D")) { //contain any non-digit character
            return false;
        }
        return playerIdOrName.matches("\\d{1,}"); //is contain digit character with 1+ length
    }

    public Player getPlayer(String playerIdOrName) {
        Player player;
        if (isId(playerIdOrName)) {
            player = findById(Long.parseLong(playerIdOrName));//todo getReference()??????
        } else {
            player = findByTempName(playerIdOrName);
        }
        return player;
    }

    private Player findById(long id) {
        Player player = null;
        player = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p where p.id = :id", Player.class)
                        .setParameter("id", id)
                        .getSingleResult()
        );
        return player;
    }

    private Player findByTempName(String tempName) {
        Player player = null;
        try {
            player = JpaUtil.performReturningWithinPersistenceContext(
                    em -> em.createQuery("select p from Player p where p.temp_name = :tempName", Player.class)
                            .setParameter("tempName", tempName)
                            .getSingleResult()
            );
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return player;
    }
}
