package dao.impl;

import dao.ClanDAO;
import model.Clan;
import model.Player;
import utils.JpaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
                            .orElseThrow(() -> new NoSuchElementException("Лидер клана не найден в списке мемберов."))
                            .setClanLeader(false);

                    mergedClan.getMembers().stream()
                            .filter(player -> player.getId() == newId)
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Кандидат в лидера клана не найден в списке мемберов."))
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
        if (filter.matches("\\d{1,}")) {        //return N first clans
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
        Clan clan = findByName(clanName);
        return JpaUtil.performReturningWithinPersistenceContext(
                em -> {
                    Clan merged = em.merge(clan);
                    merged.setRelation(relation);
                    return true;
        });
    }
}
