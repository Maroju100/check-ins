package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class TeamServicesImpl implements TeamServices {

    private final TeamRepository teamsRepo;
    private final TeamMemberRepository teamMemberRepository;

    public TeamServicesImpl(TeamRepository teamsRepo,
                            TeamMemberRepository teamMemberRepository) {
        this.teamsRepo = teamsRepo;
        this.teamMemberRepository = teamMemberRepository;
    }

    public Team save(Team team) {
        Team newTeam = null;
        if (team != null) {
            if (team.getId() != null) {
                throw new TeamBadArgException(String.format("Found unexpected id %s, please try updating instead",
                        team.getId()));
            } else if (teamsRepo.findByName(team.getName()).isPresent()) {
                throw new TeamBadArgException(String.format("Team with name %s already exists", team.getName()));
            } else {
                newTeam = teamsRepo.save(team);
            }
        }

        return newTeam;
    }

    public Team read(UUID teamId) {
        return teamId != null ? teamsRepo.findById(teamId).orElse(null) : null;
    }

    public Team update(Team team) {
        Team newTeam = null;
        if (team != null) {
            if (team.getId() != null && teamsRepo.findById(team.getId()).isPresent()) {
                newTeam = teamsRepo.update(team);
            } else {
                throw new TeamBadArgException(String.format("Team %s does not exist, can't update.", team.getId()));
            }
        }

        return newTeam;
    }

    public Set<Team> findByFields(String name, UUID memberid) {
        return new HashSet<>(
                teamsRepo.search(name, nullSafeUUIDToString(memberid)));
    }
}
