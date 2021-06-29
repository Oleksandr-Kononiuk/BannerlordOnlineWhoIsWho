package model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "players")
public class Player {

    @Id
    //@GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @Column(name = "mainName", nullable = false)
    private String mainName;

    @Column(name = "tempName", nullable = false)
    private String tempName;

    @Column(name = "clanName", nullable = false)
    private String clanName;

    @Column(name = "isClanLeader")
    private boolean isClanLeader = false;

    @Column(name = "isTwink")
    private boolean isTwink = false;

    public Player() {
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

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
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
