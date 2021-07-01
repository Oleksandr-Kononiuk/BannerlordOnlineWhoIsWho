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
    public Player findById(long id) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p where p.id = :id", Player.class)
                .setParameter("id", id)
                .getSingleResult()
        );
    }

    @Override
    public Player findByTempName(String tempName) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p where p.temp_name = :tempName", Player.class)
                        .setParameter("tempName", tempName)
                        .getSingleResult()
        );
    }

    @Override
    public Player findByMainName(String mainName) {
        return JpaUtil.performReturningWithinPersistenceContext(
                entityManager -> entityManager.createQuery("select p from Player p where p.main_name = :mainName", Player.class)
                        .setParameter("mainName", mainName)
                        .getSingleResult()
        );
    }

    @Override
    public String getPlayerClan(String playerIdOrName) {
        Player player = getPlayer(playerIdOrName);
        return player.getClan().getClanName();
    }

    @Override
    public boolean isClanLeader(String playerIdOrName) {
        if (isId(playerIdOrName)) {
            return findById(Long.parseLong(playerIdOrName)).isClanLeader();
        } else {
            return findByMainName(playerIdOrName).isClanLeader();
        }
    }

    @Override
    public boolean isTwink(String playerIdOrName) {
        if (isId(playerIdOrName)) {
            return findById(Long.parseLong(playerIdOrName)).isTwink();
        } else {
            return findByMainName(playerIdOrName).isTwink();
        }
    }

    @Override
    public List<Player> findAll() {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select p from Player p", Player.class)
                        .getResultList()
        );
    }

    @Override
    public void changeMainName(String playerOldMainNameOrId, String newName) {
        Player player = getPlayer(playerOldMainNameOrId);
        player.setMainName(newName);
        Player updatedPlayer = update(player);

        System.out.println("Old name:" + player.getMainName());
        System.out.println("New name:" + updatedPlayer.getMainName());
    }

    @Override
    public void changeTempName(String playerOldTempNameOrId, String newName) {
        Player player = getPlayer(playerOldTempNameOrId);
        player.setTempName(newName);
        Player updatedPlayer = update(player);

        System.out.println("Old name:" + player.getTempName());
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
        player.setClanLeader(status);
        Player updatedPlayer = update(player);

        System.out.println("Old leader status:" + player.isClanLeader());
        System.out.println("New leader status:" + updatedPlayer.isClanLeader());
    }

    @Override
    public void setTwink(String playerIdOrName, boolean status) {
        Player player = getPlayer(playerIdOrName);
        player.setTwink(status);
        Player updatedPlayer = update(player);

        System.out.println("Old twink status:" + player.isTwink());
        System.out.println("New twink status:" + updatedPlayer.isTwink());
    }

    private Player update(Player player) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.merge(player)
        );
    }

    @Override
    public void delete(String playerIdOrName) {
        Player playerToDelete = getPlayer(playerIdOrName);

        if (playerToDelete != null) {
            JpaUtil.performWithinPersistenceContext(
                    em -> em.remove(playerToDelete)
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
            player = findByMainName(playerIdOrName);
        }
        return player;
    }
}
