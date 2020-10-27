package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.fixture.TeamMemberFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TeamRepositoryTest extends TestContainersSuite implements TeamFixture, TeamMemberFixture, MemberProfileFixture {

    MemberProfile pdl;
    MemberProfile member;
    MemberProfile unrelated;

    Team t1;
    Team t2;

    //@BeforeEach TODO: when using this annotation instead of calling this method within the tests there is an error. Investigate why.
    public void before() {
        t1 = new Team("team1", "the first team");
        t2 = new Team("team2", "the second team");
        pdl = createADefaultMemberProfile();
        member = createADefaultMemberProfileForPdl(pdl);
        unrelated = createAnUnrelatedUser();
        TeamMember tm1 = new TeamMember(pdl.getId(), true);
        TeamMember tm2 = new TeamMember(member.getId(), false);
        TeamMember tm3 = new TeamMember(unrelated.getId(), true);
        t1.setTeamMembers(List.of(tm1, tm2));
        t2.setTeamMembers(List.of(tm3));
        getTeamRepository().save(t1);
        getTeamRepository().save(t2);
    }

    @AfterEach
    public void after() {
        getTeamMemberRepository().deleteAll();
        getTeamRepository().deleteAll();
    }

    @Test
    public void testUpdateNewMember() {
        before();
        TeamMember tm1 = new TeamMember(pdl.getId(), true);
        TeamMember tm2 = new TeamMember(member.getId(), true);
        TeamMember tm3 = new TeamMember(unrelated.getId(), false);
        t1.setTeamMembers(List.of(tm1, tm2, tm3));

        Team actual = getTeamRepository().update(t1);

        assertEquals(t1, actual);

        for (int i = 0; i < actual.getTeamMembers().size(); i++) {
            assertEquals(actual.getTeamMembers().get(i).getId(), t1.getTeamMembers().get(i).getId());
            assertEquals(actual.getTeamMembers().get(i).getMemberid(), t1.getTeamMembers().get(i).getMemberid());
        }

        actual = getTeamRepository().findById(t1.getId()).get();

        assertEquals(t1, actual);

        for (int i = 0; i < actual.getTeamMembers().size(); i++) {
            assertEquals(actual.getTeamMembers().get(i).getId(), t1.getTeamMembers().get(i).getId());
            assertEquals(actual.getTeamMembers().get(i).getMemberid(), t1.getTeamMembers().get(i).getMemberid());
        }

    }

    @Test
    public void testUpdate() {
        before();
        TeamMember tm1 = new TeamMember(pdl.getId(), true);
        TeamMember tm2 = new TeamMember(member.getId(), true);
        t1.setTeamMembers(List.of(tm1, tm2));

        Team actual = getTeamRepository().update(t1);

        assertEquals(t1, actual);

        for (int i = 0; i < actual.getTeamMembers().size(); i++) {
            assertEquals(actual.getTeamMembers().get(i).getId(), t1.getTeamMembers().get(i).getId());
            assertEquals(actual.getTeamMembers().get(i).getMemberid(), t1.getTeamMembers().get(i).getMemberid());
        }

    }

    @Test
    public void testGetById() {
        before();
        Team actual = getTeamRepository().findById(t1.getId()).get();
        assertEquals(t1, actual);
        assertEquals(actual.getTeamMembers().size(), t1.getTeamMembers().size());
        for (int i = 0; i < actual.getTeamMembers().size(); i++) {
            assertEquals(actual.getTeamMembers().get(i).getId(), t1.getTeamMembers().get(i).getId());
            assertEquals(actual.getTeamMembers().get(i).getMemberid(), t1.getTeamMembers().get(i).getMemberid());
        }

    }
}
