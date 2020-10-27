package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "team_member")
public class TeamMember {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to team entry", required = true)
    private UUID id;

    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the team this entry is associated with", required = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberid;

    @Nullable
    @Column(name = "lead")
    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public TeamMember(){}

    public TeamMember(UUID memberid, Boolean lead) {
        this(null, memberid, lead);
    }

    public TeamMember(UUID id, UUID memberid, Boolean lead) {
        this.id = id;
        this.memberid = memberid;
        this.lead = lead;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember that = (TeamMember) o;
        return Objects.equals(id, that.id) &&
                //Objects.equals(teamid, that.teamid) &&
                Objects.equals(team, that.team) &&
                Objects.equals(memberid, that.memberid) &&
                Objects.equals(lead, that.lead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, team/*id*/, memberid, lead);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    /*public UUID getTeamid() {
        return teamid;
    }

    public void setTeamid(UUID teamid) {
        this.teamid = teamid;
    }*/

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }

    public boolean isLead() {
        return lead != null && lead;
    }

    public void setLead(boolean lead) {
        this.lead = lead;
    }
}
