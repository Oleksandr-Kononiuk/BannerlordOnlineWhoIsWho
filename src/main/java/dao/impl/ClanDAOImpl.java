package dao.impl;

import dao.ClanDAO;
import model.Clan;
import model.Player;
import utils.JpaUtil;
import java.util.*;
import java.util.stream.Collectors;

/**
 *@author  Oleksandr Kononiuk
 */

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
        Clan toDeleteClan = findByName(clanName);

        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Clan merged = em.merge(toDeleteClan);
                    merged.getMembers().forEach(
                            member -> {
                                member.setClanLeader(false);
                                member.setClan(null);
                            }
                    );
                    merged.setMembers(new ArrayList<>());
                    em.remove(merged);
                    return true;
                }
        );
    }

    @Override
    public boolean addMember(String clanName, String[] playerIdOrName) {
        Clan clan = findByName(clanName);
        Player player = playerDAO.getPlayer(playerDAO.buildStringFromArgs(playerIdOrName));

        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Clan mergedClan = em.merge(clan);
                    Player mergedPlayer = em.merge(player);
                    mergedPlayer.setClanLeader(false);

                    int oldSize = mergedClan.getMembers().size();
                    mergedClan.addMember(mergedPlayer);
                    int newSize = mergedClan.getMembers().size();
                    return oldSize == newSize;
                }
        );
    }

    @Override
    public boolean deleteMember(String clanName, long id) {
        Clan clan = findByName(clanName);
        Player player = playerDAO.getPlayer(Long.toString(id));

        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Clan mergedClan = em.merge(clan);
                    Player mergedPlayer = em.merge(player);
                    mergedPlayer.setClanLeader(false);

                    int oldSize = mergedClan.getMembers().size();
                    mergedClan.deleteMember(mergedPlayer);
                    int newSize = mergedClan.getMembers().size();
                    return oldSize == newSize;
                }
        );
    }

    public List<Player> getMembers(String clanName) {
        return findByName(clanName).getMembers();
    }

    @Override
    public String getClanLeader(String clanName) {
        return getMembers(clanName).stream()
                .filter(Player::isClanLeader)
                .map(Player::getTempName)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    @Override
    public boolean changeClanLeader(String clanName, long oldId, long newId) {
        Clan clan = findByName(clanName);

        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Clan mergedClan = em.merge(clan);

                    mergedClan.getMembers().stream()
                            .filter(player -> player.isClanLeader() && player.getId() == oldId)
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("?????????? ?????????? ???? ???????????? ?? ???????????? ????????????????."))
                            .setClanLeader(false);

                    mergedClan.getMembers().stream()
                            .filter(player -> player.getId() == newId)
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("???????????????? ?? ???????????? ?????????? ???? ???????????? ?? ???????????? ????????????????."))
                            .setClanLeader(true);
                    return true;
                }
        );
    }

    @Override
    public List<Clan> getAllClans(String filter) {
        List<Clan> allClans = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c", Clan.class)
                        .getResultList()
        );
        if (filter.matches("\\d+")) {        //return N first clans
            return allClans.stream()
                    .limit(Long.parseLong(filter))
                    .collect(Collectors.toList());
        } else {
            return allClans.stream()              //return all clans which name starts on filter
                    .filter(clan -> clan.getClanName().startsWith(filter))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Clan findByName(String clanName) {
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c where c.clan_name = :clanName", Clan.class)
                        .setParameter("clanName", clanName)
                        .getSingleResult()
        );
    }

    @Override
    public boolean setRelation(String clanName, int relation) {
        if (relation < 0 || relation > 2) throw new IllegalArgumentException();

        Clan clan = findByName(clanName);
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Clan merged = em.merge(clan);
                    merged.setRelation(relation);
                    return true;
        });
    }

    @Override
    public Map<Integer, List<Clan>> buildDiplomacy() {
        List<Clan> clans = JpaUtil.performReturningWithinPersistenceContext(
                em -> em.createQuery("select c from Clan c", Clan.class).getResultList()
        );

        return clans.stream()
                .collect(Collectors.groupingBy(Clan::getRelation));
    }

    @Override
    public void updateClan(String clanName) {
        Clan clan = findByName(clanName);

        for(Player member : clan.getMembers()) {
            playerDAO.update(member.getId());
        }
    }
}
