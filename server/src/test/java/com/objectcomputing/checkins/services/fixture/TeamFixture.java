package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamResponseDTO;
import com.objectcomputing.checkins.services.team.TeamUpdateDTO;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface TeamFixture extends RepositoryFixture{

    default TeamResponseDTO fromEntity(Team entity) {
        TeamResponseDTO dto = new TeamResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setTeamMembers(new ArrayList<>());
        if (entity.getTeamMembers() != null) {
            for (TeamMember member : entity.getTeamMembers()) {
                dto.getTeamMembers().add(fromMemberEntity(member));
            }
        }
        return dto;
    }

    default TeamMemberDTO fromMemberEntity(TeamMember entity) {
        TeamMemberDTO dto = new TeamMemberDTO();
        dto.setMemberid(entity.getMemberid());
        dto.setId(entity.getId());
        dto.setLead(entity.isLead());
        return dto;
    }

    default Team createDefultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID(),"Ninja","Warriors"));
    }

    default TeamUpdateDTO makeDefaultTeamUpdateDTO(UUID fromUUID, List<MemberProfile> members) {
        TeamUpdateDTO newDTO = new TeamUpdateDTO();
        newDTO.setId(fromUUID);
        newDTO.setName("different name");
        newDTO.setDescription("different description");
        newDTO.setTeamMembers(new ArrayList<>());
        for (MemberProfile member : members) {
            TeamMemberDTO newTeamMember = new TeamMemberDTO();
            newTeamMember.setMemberid(member.getId());
            newTeamMember.setLead(false);
            newDTO.getTeamMembers().add(newTeamMember);
        }
        newDTO.getTeamMembers().get(0).setLead(true);
        return newDTO;
    }

    default Team createAnotherDefultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID(),"Coding","Warriors"));
    }
}
