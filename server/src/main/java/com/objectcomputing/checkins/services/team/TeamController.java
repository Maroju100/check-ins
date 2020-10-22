package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberDTO;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/team")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "team")
public class TeamController {

    private final TeamServices teamService;
    private final TeamMemberServices teamMemberServices;

    public TeamController(TeamServices teamService,
                          TeamMemberServices teamMemberServices) {
        this.teamService = teamService;
        this.teamMemberServices = teamMemberServices;
    }

    @Error(exception = TeamBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, TeamBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and save a new team
     *
     * @param team, {@link TeamCreateDTO}
     * @return {@link HttpResponse<Team>}
     */

    @Post(value = "/")
    public HttpResponse<TeamResponseDTO> createATeam(@Body @Valid TeamCreateDTO team, HttpRequest<TeamCreateDTO> request) {
        Team newTeam = teamService.save(new Team(team.getName(), team.getDescription()));
        return HttpResponse
                .created(toDTOFromEntity(newTeam))
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), newTeam.getId()))));
    }

    /**
     * Create and save multiple teams
     *
     * @param teamsList, array of {@link TeamCreateDTO team create dto} to load {@link Team team(s)}
     * @return
     */

    @Post("/teams")
    public HttpResponse<?> loadTeams(@Body @NotNull @Valid List<TeamCreateDTO> teamsList, HttpRequest<List<TeamCreateDTO>> request) {
        List<String> errors = new ArrayList<>();
        List<Team> teamsCreated = new ArrayList<>();
        for (TeamCreateDTO teamDTO : teamsList) {
            Team team = new Team(teamDTO.getName(), teamDTO.getDescription());
            try {
                teamService.save(team);
                teamsCreated.add(team);
            } catch (TeamBadArgException e) {
                errors.add(String.format("Team %s was not added because: %s", team.getName(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(teamsCreated).headers(headers ->
                    headers.location(request.getUri()));
        } else {
            return HttpResponse.badRequest(errors).headers(headers ->
                    headers.location(request.getUri()));
        }
    }

    /**
     * Get team based on id
     *
     * @param id of team
     * @return {@link Team team matching id}
     */

    @Get("/{id}")
    public TeamResponseDTO readTeam(UUID id) {
        return toDTOFromEntity(teamService.read(id));
    }

    /**
     * Find team(s) given a combination of the following parameters
     *
     * @param name,     name of the team
     * @param memberid, {@link UUID} of the member you wish to inquire in to which teams they are a part of
     * @return {@link List<Team> list of teams}, return all teams when no parameters filled in else
     * return all teams that match all of the filled in params
     */

    @Get("/{?name,memberid}")
    public Set<TeamResponseDTO> findTeams(@Nullable String name, @Nullable UUID memberid) {
        return teamService.findByFields(name, memberid)
                .stream()
                .map(this::toDTOFromEntity)
                .collect(Collectors.toSet());
    }

    /**
     * Update team.
     *
     * @param team, {@link Team}
     * @return {@link HttpResponse<Team>}
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid TeamUpdateDTO team, HttpRequest<Team> request) {
        Team updatedTeam = teamService.update(toTeamEntityFromUpdateDTO(team));
        /*teamMemberServices.clearTeam(team.getId());
        for (TeamMember member : team.getTeamMembers()) {
            member.setTeam(updatedTeam);
            member.setId(null);
            teamMemberServices.save(member);
        }*/
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), team.getId()))))
                .body(toDTOFromEntity(updatedTeam));

    }

    private Team toTeamEntityFromUpdateDTO(TeamUpdateDTO dto) {
        return new Team(dto.getId(), dto.getName(), dto.getDescription());
    }

    private TeamResponseDTO toDTOFromEntity(Team entity) {
        TeamResponseDTO dto = new TeamResponseDTO();
        dto.setDescription(entity.getDescription());
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        dto.setTeamMembers(entity.getTeamMembers().stream().map(teamMember -> {
            TeamMemberDTO memberDTO = new TeamMemberDTO();
            memberDTO.setId(teamMember.getId());
            memberDTO.setLead(teamMember.isLead());
            memberDTO.setMemberid(teamMember.getMemberid());
            return memberDTO;
        }).collect(Collectors.toList()));
        return dto;
    }

}