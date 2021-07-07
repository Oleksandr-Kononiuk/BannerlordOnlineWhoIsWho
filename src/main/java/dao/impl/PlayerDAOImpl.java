package dao.impl;

import dao.PlayerDAO;
import model.Clan;
import model.Player;
import utils.DataUtils;
import utils.JpaUtil;

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
    public Player find(String playerIdOrName) {
        return getPlayer(playerIdOrName);
    }

    @Override
    public Clan getPlayerClan(String playerIdOrName) {
        Player player = getPlayer(playerIdOrName);
        return player.getClan();
    }

    @Override
    public boolean isClanLeader(String playerIdOrName) {
        if (isId(playerIdOrName)) {
            return findById(Long.parseLong(playerIdOrName)).isClanLeader();
        } else {
            return findByTempName(playerIdOrName).isClanLeader();
        }
    }

    @Override
    public boolean isTwink(String playerIdOrName) {
        if (isId(playerIdOrName)) {
            return findById(Long.parseLong(playerIdOrName)).isTwink();
        } else {
            return findByTempName(playerIdOrName).isTwink();
        }
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
    public boolean changeTempName(String playerOldTempNameOrId, String[] newNameArray) {
        Player player = getPlayer(playerOldTempNameOrId);//todo робить якийсь дивний зайвий селект
        String oldName = player.getTempName();
        String newName = buildStringFromArgs(newNameArray);
        player.setTempName(newName);
        update(player);

        return oldName.equals(newName);
    }

    @Override
    public boolean changeClan(String playerIdOrName, String newClanName) {
        Player player = getPlayer(playerIdOrName);
        String oldClanName = (player.getClan() == null ? "": player.getClan().getClanName());
        //System.out.println(oldClanName);
        Clan newClan = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c where c.clan_name = :clanName", Clan.class)
                        .setParameter("clanName", newClanName)
                        .getSingleResult()
        );
        player.setClan(newClan);
        update(player);

        return oldClanName.equals(player.getClan().getClanName());
    }

    @Override
    public boolean setClanLeader(String playerIdOrName, boolean status) {
        Player player = getPlayer(playerIdOrName);
        boolean oldStatus = player.isClanLeader();
        player.setClanLeader(status);
        Player updatedPlayer = update(player);

        return oldStatus == updatedPlayer.isClanLeader();//true не змінилось
    }

    @Override
    public boolean setTwink(String playerIdOrName, boolean status) {
        Player player = getPlayer(playerIdOrName);
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

    private String buildStringFromArgs(String[] array) {
        String result = "";
        if (array.length == 2) return array[1];

        for (int i = 1; i < array.length; i++) {
            result = result.concat(array[i]);
            result = result.concat(" ");
        }
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
            System.out.println("11");
        }
        return player;
    }
}
