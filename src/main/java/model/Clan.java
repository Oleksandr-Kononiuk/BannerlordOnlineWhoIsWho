package model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clans")
public class Clan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @NaturalId(mutable = false)
    @Column(name = "clan_name", unique = true, nullable = false)
    private String clanName;

    @Column(name = "members")
    @OneToMany(mappedBy = "clan")
    private List<Player> members = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    private void setMembers(List<Player> members) {
        this.members = members;
    }
}
