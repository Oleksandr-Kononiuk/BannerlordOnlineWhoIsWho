package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clans")
public class Clan {

    @Column(name = "clanName", nullable = false)
    private String clanName;

    @OneToMany
    private List<Player> members = new ArrayList<>();
}
