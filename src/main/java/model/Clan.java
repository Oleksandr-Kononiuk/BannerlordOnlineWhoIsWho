package model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clans")
public class Clan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long clan_id;

    @NaturalId(mutable = false)
    @Column(name = "clan_name", unique = true, nullable = false)
    private String clanName;

    @Column(name = "members")
    @OneToMany(mappedBy = "clan", orphanRemoval = false, fetch = FetchType.LAZY)
    private List<Player> members = new ArrayList<>();

    public long getClan_id() {
        return clan_id;
    }

    public void setClan_id(long clan_id) {
        this.clan_id = clan_id;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clan)) return false;
        Clan clan = (Clan) o;
        return clanName.equals(clan.clanName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clanName);
        //return 10;
    }
}
