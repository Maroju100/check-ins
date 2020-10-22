package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.fixture.TeamMemberFixture;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TeamRepositoryTest extends TestContainersSuite implements TeamFixture, TeamMemberFixture {

    @BeforeEach
    public void before() {
        Team t1 = new Team(UUID.nameUUIDFromBytes("Team1".getBytes()), "team1", "the first team");
        Team t2 = new Team(UUID.nameUUIDFromBytes("Team2".getBytes()), "team2", "the second team");
        TeamMember tm1 = new TeamMember(UUID.nameUUIDFromBytes("tm1".getBytes()), true);
        TeamMember tm2 = new TeamMember(UUID.nameUUIDFromBytes("tm2".getBytes()), false);
        TeamMember tm3 = new TeamMember(UUID.nameUUIDFromBytes("tm3".getBytes()), true);
        t1.setTeamMembers(List.of(tm1, tm2));
        t2.setTeamMembers(List.of(tm3));
        getTeamRepository().save(t1);
        getTeamRepository().save(t2);
    }

    @AfterEach
    public void after() {
        getTeamRepository().deleteAll();
    }

    @Test
    public void testUpdate() {
        Team t1 = new Team(UUID.nameUUIDFromBytes("Team1".getBytes()), "team3", "the first team redux");
        TeamMember tm1 = new TeamMember(UUID.nameUUIDFromBytes("tm1".getBytes()), true);
        TeamMember tm2 = new TeamMember(UUID.nameUUIDFromBytes("tm2".getBytes()), true);
        t1.setTeamMembers(List.of(tm1, tm2));

        Team result = getTeamRepository().save(t1);

        assertEquals(t1, result);

        result = getTeamRepository().findById(t1.getId()).get();

        assertNotNull(result);
        assertEquals(t1, result);

    }

    @Test
    public void testGetById() {

        Team expected = new Team(UUID.nameUUIDFromBytes("Team1".getBytes()), "team3", "the first team redux");
        TeamMember tm1 = new TeamMember(UUID.nameUUIDFromBytes("tm1".getBytes()), true);
        TeamMember tm2 = new TeamMember(UUID.nameUUIDFromBytes("tm2".getBytes()), true);
        expected.setTeamMembers(List.of(tm1, tm2));

        Team actual = getTeamRepository().findById(expected.getId()).get();
        assertEquals(expected, actual);
        assertEquals(actual.getTeamMembers().size(), expected.getTeamMembers().size());
        for (int i = 0; i < actual.getTeamMembers().size(); i++) {
            assertEquals(actual.getTeamMembers().get(i), expected.getTeamMembers().get(i));
        }

    }
}
