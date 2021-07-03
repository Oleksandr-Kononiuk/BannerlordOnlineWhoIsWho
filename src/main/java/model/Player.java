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
    @JoinColumn(name = "clan", referencedColumnName = "clan_name")
    private Clan clan;

    @Column(name = "is_clan_leader")
    private boolean is_clan_leader = false;

    @Column(name = "is_twink")
    private boolean is_twink = false;

    @Column(name = "profile_link", nullable = false)
    private String profile_link;

//todo добавити поле з датою останььго онлайну

    public Player() {
    }

    public void deleteFromClan() {
        this.getClan().getMembers().remove(this);
        this.setClan(null);
    }

    public void addToClan(Clan clan) {
        this.setClan(clan);
        this.getClan().getMembers().add(this);
    }

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
        return "Player{" +
                "id=" + id +
                ", mainName='" + main_name + '\'' +
                ", tempName='" + temp_name + '\'' +
                ", clan=" + clan +
                ", isClanLeader=" + is_clan_leader +
                ", isTwink=" + is_twink +
                ", profileLink='" + profile_link + '\'' +
                '}';
    }
}
