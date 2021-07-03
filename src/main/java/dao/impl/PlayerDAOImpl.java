package dao.impl;

import dao.PlayerDAO;
import model.Clan;
import model.Player;
import utils.DataUtils;
import utils.JpaUtil;

import java.util.List;

public class PlayerDAOImpl implements PlayerDAO {

    private DataUtils dataUtils = new DataUtils();

    @Override
    public void save(long id) {
        Player newPlayer = dataUtils.getNewPlayer(id);

        if (newPlayer != null) {
            JpaUtil.performWithinPersistenceContext(
                    em -> em.persist(newPlayer)
            );
        } else {
            System.out.println("getNewPlayer(id) return NULL player");
        }
    }

    @Override
    public Player find(String playerIdOrName) {
        return getPlayer(playerIdOrName);
    }

    @Override
    public String getPlayerClan(String playerIdOrName) {
        Player player = getPlayer(playerIdOrName);

        if (player.getClan() == null) {
            System.out.println("Player is`n in any clan.");
            return "Player is`n in any clan.";
        } else {
            return player.getClan().getClanName();
        }
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
    public List<Player> findAll() { //todo implement pagination or add filters by first letter or first 100
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p", Player.class)
                        .getResultList()
        );
    }

    @Override
    public void changeTempName(String playerOldTempNameOrId, String newName) {
        Player player = getPlayer(playerOldTempNameOrId);//todo робить якийсь дивний зайвий селект
        String oldName = player.getTempName();
        player.setTempName(newName);
        Player updatedPlayer = update(player);

        System.out.println("Old name:" + oldName);
        System.out.println("New name:" + updatedPlayer.getTempName());
    }

    @Override
    public void changeClan(String playerIdOrName, Clan newClan) {
        Player player = getPlayer(playerIdOrName);
        player.deleteFromClan();
        player.addToClan(newClan);

        Player updatedPlayer = update(player);

        System.out.println("Old clan:" + player.getClan().getClanName());
        System.out.println("New clan:" + updatedPlayer.getClan().getClanName());
    }

    @Override
    public void setClanLeader(String playerIdOrName, boolean status) {
        Player player = getPlayer(playerIdOrName);
        boolean oldStatus = player.isClanLeader();
        player.setClanLeader(status);
        Player updatedPlayer = update(player);

        System.out.println("Old leader status:" + oldStatus);
        System.out.println("New leader status:" + updatedPlayer.isClanLeader());
    }

    @Override
    public void setTwink(String playerIdOrName, boolean status) {
        Player player = getPlayer(playerIdOrName);
        boolean oldStatus = player.isTwink();
        player.setTwink(status);
        Player updatedPlayer = update(player);

        System.out.println("Old twink status:" + oldStatus);
        System.out.println("New twink status:" + updatedPlayer.isTwink());
    }

    public Player update(Player player) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.merge(player)
        );
    }

    @Override
    public void delete(String playerIdOrName) {
        Player playerToDelete = getPlayer(playerIdOrName);

        if (playerToDelete != null) {
            JpaUtil.performWithinPersistenceContext(
                    em -> {
                        Player merged = em.merge(playerToDelete);
                        em.remove(merged);
                    }
            );
        } else {
            System.out.println("Player not found");
        }
    }


    private boolean isId(String playerIdOrName) {
        if (playerIdOrName.matches("\\D")) { //contain any non-digit character
            return false;
        }
        return playerIdOrName.matches("\\d{1,}"); //is contain digit character with 1+ length
    }

    public Player getPlayer(String playerIdOrName) {
        Player player = null;
        if (isId(playerIdOrName)) {
            player = findById(Long.parseLong(playerIdOrName));//todo getReference()??????
        } else {
            player = findByTempName(playerIdOrName);
        }
        return player;
    }

    private Player findById(long id) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p where p.id = :id", Player.class)
                        .setParameter("id", id)
                        .getSingleResult()
        );
    }

    private Player findByTempName(String tempName) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p where p.temp_name = :tempName", Player.class)
                        .setParameter("tempName", tempName)
                        .getSingleResult()
        );
    }
}
