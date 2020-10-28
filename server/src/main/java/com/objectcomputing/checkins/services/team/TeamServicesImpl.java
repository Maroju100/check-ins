package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

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
        Team thisTeam = teamId != null ? teamsRepo.findById(teamId).orElse(null) : null;
        //thisTeam.setTeamMembers(teamMemberRepository.findByTeamid(thisTeam.getId()));
        return thisTeam;
    }

    public Team update(Team team) {
        Team newTeam = null;
        if (team != null) {
            Optional<Team> existingTeam = teamsRepo.findById(team.getId());
            if (team.getId() != null && existingTeam.isPresent()) {
                //teamMemberRepository.deleteByTeamid(team.getId());
                newTeam = teamsRepo.update(team);
                crudMembers(team.getTeamMembers(), existingTeam.get().getTeamMembers().stream().map(TeamMember::getId).collect(Collectors.toList()));
                //newTeam.setTeamMembers(teamMemberRepository.saveAll(team.getTeamMembers()));

            } else {
                throw new TeamBadArgException(String.format("Team %s does not exist, can't update.", team.getId()));
            }
        }

        return newTeam;
    }

    //It looks like there are complications with updates of joins with JDBC and fetches. Micronaut will not reliably
    //update child records when updated along with a parent and the associated join annotations seem to restrict allowing
    //direct updates of those child records. This prevents the use of a deleteByTeamId method on teamMemberRepository,
    //for example. For this reason I am simply blowing away and replacing the child records here.
    private void crudMembers(List<TeamMember> newList, List<UUID> existingIds) {
        for (UUID memberId : existingIds) {
            teamMemberRepository.deleteById(memberId);
        }
        for (TeamMember member : newList) {
            teamMemberRepository.save(member);
        }
    }

    public Set<Team> findByFields(String name, UUID memberid) {
        return new HashSet<>(
                teamsRepo.search(name, nullSafeUUIDToString(memberid)));
    }
}
