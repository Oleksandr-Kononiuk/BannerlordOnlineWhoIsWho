import utils.JpaUtil;

public class BannerlordOnlineWhoIsWho {

    public static void main(String[] args) {
        JpaUtil.init("BannerlordOnlinePlayersMySQL");

//        Player testPlayer = new Player();
//        testPlayer.setMainName("1");
//        testPlayer.setTempName("2");
//        testPlayer.setProfileLink("link");
//        testPlayer.setId(2);
//
//
//        Clan testClan = new Clan();
//
//        testClan.setClanName("clan2");
//        testPlayer.setClan(testClan);
//
//        utils.JpaUtil.performWithinPersistenceContext(entityManager -> {
//            entityManager.persist(testPlayer);
//            entityManager.persist(testClan);
//        });
//
//        Clan clan = utils.JpaUtil.performReturningWithinPersistenceContext(entityManager -> entityManager.find(Clan.class, 4));
//        System.out.println(clan.getClanName());
    }
}
