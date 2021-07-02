package model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clans")
public class Clan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long clan_id;

    @NaturalId(mutable = false)
    @Column(name = "clan_name", unique = true, nullable = false)
    private String clanName;

    @Column(name = "members")
    @OneToMany(mappedBy = "clan", orphanRemoval = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Player> members = new ArrayList<>();

    public Clan() {
    }

    public Clan(String name) {
        this.clanName = name;
    }

//    public void deleteMember(Player member) {//todo спробувати видалити
//        this.getMembers().remove(member);
//        member.setClan(null);
//    }
//
//    public void addMember(Player player) {//todo спробувати видалити
//        this.getMembers().add(player);
//        player.setClan(this);
//    }

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
