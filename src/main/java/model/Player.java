package model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "players")
public class Player {

    @Id
    private long id;

    @Column(name = "main_name", nullable = false)
    private String mainName;

    @Column(name = "temp_name", nullable = false)
    private String tempName;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "clan", referencedColumnName = "clan_name")
    private Clan clan;

    @Column(name = "is_clan_leader")
    private boolean isClanLeader = false;

    @Column(name = "is_twink")
    private boolean isTwink = false;

    @Column(name = "profile_link", nullable = false)
    private String profileLink;

    public Player() {
    }

    public void deleteFromClan() {
        this.getClan().deleteMember(this);
        this.setClan(null);
    }

    public void addToClan(Clan clan) {
        this.setClan(clan);
        this.getClan().addMember(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public boolean isClanLeader() {
        return isClanLeader;
    }

    public void setClanLeader(boolean clanLeader) {
        isClanLeader = clanLeader;
    }

    public boolean isTwink() {
        return isTwink;
    }

    public void setTwink(boolean twink) {
        isTwink = twink;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
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
}
