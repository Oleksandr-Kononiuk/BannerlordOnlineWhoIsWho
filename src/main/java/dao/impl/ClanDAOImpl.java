package dao.impl;

import dao.ClanDAO;
import model.Clan;
import model.Player;
import utils.JpaUtil;

import java.util.List;
import java.util.stream.Collectors;

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
        toDeleteClan.getMembers().stream()
                .forEach(member -> {
                    member.setClan(null);
                    playerDAO.update(member);
                });
        JpaUtil.performWithinPersistenceContext(
                em -> em.remove(toDeleteClan)
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
                .map(Player::getMainName)
                .findFirst()
                .orElse("Not found!!!");
    }

    @Override
    public boolean changeClanLeader(String clanName, String oldLeaderIdOrName, String newLeaderIdOrName) {
        List<Player> members = getMembers(clanName);
        Player oldLeader = members.stream()
                .filter(Player::isClanLeader)
                .findFirst()
                .get();

        Player newLeader = members.stream()
                .filter(player -> player.getMainName().equals(newLeaderIdOrName) || player.getId() == Long.parseLong(newLeaderIdOrName))
                .findFirst()
                .get();

        oldLeader.setClanLeader(false);
        newLeader.setClanLeader(true);

        Player old = playerDAO.update(oldLeader);
        Player neww = playerDAO.update(newLeader);

        return !old.isClanLeader() && neww.isClanLeader();
    }

    @Override
    public List<String> getAllClans() {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c", Clan.class)
                .getResultList().stream()
                .map(Clan::getClanName)
                .collect(Collectors.toList())
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
