package dao.impl;

import dao.ClanDAO;
import model.Clan;
import model.Player;
import utils.JpaUtil;

import java.util.List;
import java.util.NoSuchElementException;

public class ClanDAOImpl implements ClanDAO {

    private PlayerDAOImpl playerDAO = new PlayerDAOImpl();

    @Override
    public void addNewClan(String clanName) {
        JpaUtil.performWithinPersistenceContext(
                em -> em.persist(new Clan(clanName))
        );
    }

    @Override
    public void deleteClan(String clanName) {
        Clan toDeleteClan = findByName(clanName);

        JpaUtil.performWithinPersistenceContext(
                em -> {
                    Clan merged = em.merge(toDeleteClan);
                    merged.getMembers()
                            .forEach(Player::deleteFromClan);
                    em.remove(merged);
                }
        );
    }

    @Override
    public void addMember(String clanName, String playerIdOrName) {
        Clan clan = findByName(clanName);
        Player player = playerDAO.getPlayer(playerIdOrName);
        player.addToClan(clan);
        //clan.addMember(player);
        JpaUtil.performWithinPersistenceContext(
                em -> em.merge(clan)
        );
    }

    @Override
    public boolean deleteMember(String clanName, String playerIdOrName) {
        Clan clan = findByName(clanName);
        Player player = playerDAO.getPlayer(playerIdOrName);
        player.deleteFromClan();
        //clan.deleteMember(player);
        Clan isDeleted = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.merge(clan)
        );
        return clan.getMembers().size() != isDeleted.getMembers().size(); //todo improve перевірку на видалення
    }

    @Override
    public List<Player> getMembers(String clanName) {
        return findByName(clanName).getMembers();
    }

    @Override
    public String getClanLeader(String clanName) {
        return getMembers(clanName).stream()
                .filter(Player::isClanLeader)
                .map(Player::getTempName)
                .findFirst()
                .orElse("Not found!!!");
    }

    @Override
    public boolean changeClanLeader(String clanName, String oldLeaderIdOrName, String newLeaderIdOrName) {
        Clan clan = findByName(clanName);
        List<Player> members = clan.getMembers();

        members.stream()
                .filter(Player::isClanLeader)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Лидер клана не найден."))
                .setClanLeader(false);

        members.stream()
                .filter(player -> player.getTempName().equals(newLeaderIdOrName) || player.getId() == Long.parseLong(newLeaderIdOrName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Кандидат в лидера клана не найден."))
                .setClanLeader(true);

        JpaUtil.performWithinPersistenceContext(
                em -> em.merge(clan)
        );

        return true;
    }

    @Override
    public List<Clan> getAllClans() {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c", Clan.class)
                .getResultList()
        );
    }

    @Override
    public Clan findByName(String clanName) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c where c.clan_name = :clanName", Clan.class)
                .setParameter("clanName", clanName)
                .getSingleResult()
        );
    }
}
