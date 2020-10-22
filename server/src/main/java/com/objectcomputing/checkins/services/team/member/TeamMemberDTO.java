package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class TeamMemberDTO {

    @Schema(description = "id of this member to team entry", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "id of the team this entry is associated with", required = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Team team;

    @NotNull
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberid;

    @Nullable
    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }

    @Nullable
    public Boolean getLead() {
        return lead;
    }

    public void setLead(@Nullable Boolean lead) {
        this.lead = lead;
    }
}
