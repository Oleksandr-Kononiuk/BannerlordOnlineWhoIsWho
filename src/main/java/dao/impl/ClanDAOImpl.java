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
    public boolean deleteClan(String clanName) {
        return false;//todo
    }

    @Override
    public void addMember(String clanName, String playerIdOrName) {
        Clan clan = getClanByName(clanName);
        Player player = playerDAO.getPlayer(playerIdOrName);
        clan.addMember(player);
        JpaUtil.performWithinPersistenceContext(
                em -> em.merge(clan)
        );
    }

    @Override
    public boolean deleteMember(String clanName, String playerIdOrName) {
        Clan clan = getClanByName(clanName);
        Player player = playerDAO.getPlayer(playerIdOrName);
        clan.deleteMember(player);
        Clan isDeleted = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.merge(clan)
        );
        return clan.getMembers().size() != isDeleted.getMembers().size(); //todo improve
    }

    @Override
    public List<Player> getMembers(String clanName) {
        return getClanByName(clanName).getMembers();
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
        return false;//todo
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

    private Clan getClanByName(String clanName) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c where c.clan_name = :clanName", Clan.class)
                .setParameter("clanName", clanName)
                .getSingleResult()
        );
    }
}
