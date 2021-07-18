package model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "players")
public class Player {

    @Id
    private long id;

    //first name registered in DB
    @Column(name = "main_name", nullable = false)
    private String main_name;

    //Nickname can be changer 1 per week, so it`s actual name
    @Column(name = "temp_name", nullable = false)
    private String temp_name;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "clan_id")
    private Clan clan;

    @Column(name = "army", nullable = false, columnDefinition = "int default 0")
    private Integer army = 0;

    @Column(name = "is_clan_leader")
    private boolean is_clan_leader = false;

    @Column(name = "is_twink")
    private boolean is_twink = false;

    @Column(name = "profile_link", nullable = false)
    private String profile_link;

    public Player() {
    }

    //todo добавити поле "друг" яке буде синхронізуватись з кланом
    //todo add list of all known names of this player: update and change name methods

//    public void deleteFromClan() {
//        this.getClan().getMembers().remove(this);
//        this.setClan(null);
//    }
//
//    public void addToClan(Clan clan) {
//        this.setClan(clan);
//        this.getClan().getMembers().add(this);
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMainName() {
        return main_name;
    }

    public void setMainName(String mainName) {
        this.main_name = mainName;
    }

    public String getTempName() {
        return temp_name;
    }

    public void setTempName(String tempName) {
        this.temp_name = tempName;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public boolean isClanLeader() {
        return is_clan_leader;
    }

    public void setClanLeader(boolean clanLeader) {
        is_clan_leader = clanLeader;
    }

    public boolean isTwink() {
        return is_twink;
    }

    public void setTwink(boolean twink) {
        is_twink = twink;
    }

    public String getProfileLink() {
        return profile_link;
    }

    public void setProfileLink(String profileLink) {
        this.profile_link = profileLink;
    }

    public Integer getArmy() {
        return army;
    }

    public void setArmy(int army) {
        this.army = army;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
        //return 10;
    }

    @Override
    public String toString() {
        return  "```css\n" +
                "id: '" + id + "'" +
                ", Актуальный ник '" + temp_name + "'" +
                ", Клан '" + (clan != null ? clan.getClanName() : "отсуствует") + "'" +
                ", Лидер клана '" + (is_clan_leader ? "Да" : "Нет") + "'" +
                ", Размер отряда '" + army + "'" +
                ", Твинк '" + (is_twink ? "Да" : "Нет") + "'\n" +
                ", Основной ник '" + main_name + "'" +
                ", Ссылка на профиль '" + profile_link + "'\n" +
                "```";
    }

    public String toClanMemberString() {
        return  "id '" + id + "'" +
                ", Актуальный ник '" + temp_name + "'" +
                ", Лидер клана '" + (is_clan_leader ? "Да" : "Нет") + "'" +
                ", Размер отряда '" + army + "'" +
                ", Твинк '" + (is_twink ? "Да" : "Нет") + "'" +
                ", Основной ник '" + main_name + "'";
    }
}
