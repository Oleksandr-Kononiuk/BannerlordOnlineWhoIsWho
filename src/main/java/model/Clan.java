package model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clans")
public class Clan{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long clan_id;

    @Column(name = "clan_name", unique = true, nullable = false)
    private String clan_name;

    @Column(name = "members")
    @OneToMany(mappedBy = "clan", orphanRemoval = false, fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Player> members = new ArrayList<>();

    @Column(name = "relation", nullable = false, columnDefinition = "int default 0")
    private Integer relation = 0;           //["Нейтралитет", "Война", "Дружные"]

    //todo добавити поле "друг". Воно цифрове.
    // Від значення цифри можна визначати дружній клан чи ні. І на основі цього створити карту дипломатії для свого клану

    public Clan() {
    }

    public Clan(String name) {
        this.clan_name = name;
    }

    public void deleteMember(Player player) {
        members.remove(player);
        player.setClan(null);
    }

    public void addMember(Player player) {
        members.add(player);
        player.setClan(this);
    }

    public long getClanId() {
        return clan_id;
    }

    public void setClanId(long clan_id) {
        this.clan_id = clan_id;
    }

    public String getClanName() {
        return clan_name;
    }

    public void setClanName(String clanName) {
        this.clan_name = clanName;
    }

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clan)) return false;
        Clan clan = (Clan) o;
        return clan_name.equals(clan.clan_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clan_name);
        //return 10;
    }
}
