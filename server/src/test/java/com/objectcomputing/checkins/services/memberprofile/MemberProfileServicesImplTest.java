package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.member_skill.MemberSkillAlreadyExistsException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberProfileServicesImplTest {

    @Mock
    private MemberProfileRepository mockMemberProfileRepository;

    @InjectMocks
    private MemberProfileServicesImpl testObject;

    @BeforeAll
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAll() {
        MemberProfile profileOne = mkMemberProfile();
        MemberProfile profileTwo = mkMemberProfile("2");
        MemberProfile profileThree = mkMemberProfile("3");

        when(mockMemberProfileRepository.findAll())
                .thenReturn(List.of(profileOne, profileTwo, profileThree));
        Mockito.verifyNoMoreInteractions(mockMemberProfileRepository);

        Set<MemberProfile> actual = testObject.findByValues(null, null, null, null);

        assertEquals(3, actual.size());
        assertTrue(actual.contains(profileOne));
        assertTrue(actual.contains(profileTwo));
        assertTrue(actual.contains(profileThree));
    }

    @Test
    public void testFindSpecific() {
        MemberProfile profileOne = mkMemberProfile();
        profileOne.setPdlId(testPdlId);
        MemberProfile profileTwo = mkMemberProfile("2");
        profileTwo.setName(profileOne.getName());
        profileTwo.setRole(profileOne.getRole());
        MemberProfile profileThree = mkMemberProfile("3");
        profileThree.setName(profileOne.getName());
        profileThree.setPdlId(testPdlId);

        when(mockMemberProfileRepository.findAll())
                .thenReturn(List.of(profileOne, profileTwo, profileThree));
        when(mockMemberProfileRepository.findByName(profileOne.getName()))
                .thenReturn(List.of(profileOne, profileTwo, profileThree));
        when(mockMemberProfileRepository.findByRole(profileOne.getRole()))
                .thenReturn(List.of(profileOne, profileTwo));
        when(mockMemberProfileRepository.findByPdlId(profileOne.getPdlId()))
                .thenReturn(List.of(profileOne, profileThree));
        when(mockMemberProfileRepository.findByWorkEmail(profileOne.getWorkEmail()))
                .thenReturn(java.util.Optional.of(profileOne));

        Set<MemberProfile> actual = testObject.findByValues(profileOne.getName(), profileOne.getRole(), profileOne.getPdlId(), profileOne.getWorkEmail());

        assertEquals(1, actual.size());
        assertTrue(actual.contains(profileOne));
    }

    @Test
    public void testFindByIdSuccess() {
        MemberProfile expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.findById(expected.getId())).thenReturn(java.util.Optional.of(expected));

        MemberProfile actual = testObject.getById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void testFindByIdNotFound() {
        MemberProfile expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.findById(expected.getId())).thenReturn(null);
        verify(mockMemberProfileRepository, atLeastOnce()).findById(testUuid);

        MemberProfileDoesNotExistException thrown = assertThrows(MemberProfileDoesNotExistException.class, () -> {
            testObject.getById(testUuid);
        });

        assertEquals("No member profile for id", thrown.getMessage());
    }

    @Test
    public void testSaveNew() {
        MemberProfile in = mkMemberProfile();
        MemberProfile expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.save(in)).thenReturn(expected);

        MemberProfile actual = testObject.saveProfile(in);

        assertEquals(expected, actual);
    }

    @Test
    public void testSaveMemberSameEmail() {
        MemberProfile alreadyExists = mkMemberProfile();
        alreadyExists.setId(UUID.randomUUID());

        when(mockMemberProfileRepository.findByWorkEmail(eq(alreadyExists.getWorkEmail()))).thenReturn(java.util.Optional.of(alreadyExists));

        MemberProfile in = mkMemberProfile("3");
        in.setWorkEmail(alreadyExists.getWorkEmail());

        MemberSkillAlreadyExistsException response = assertThrows(MemberSkillAlreadyExistsException.class, () -> testObject.saveProfile(in));

        assertEquals(String.format("Email %s already exists in database", in.getWorkEmail()), response.getMessage());
    }

    @Test
    public void testSaveUpdate() {
        MemberProfile expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.findById(testUuid)).thenReturn(java.util.Optional.of(expected));
        when(mockMemberProfileRepository.update(expected)).thenReturn(expected);

        MemberProfile actual = testObject.saveProfile(expected);

        assertEquals(expected, actual);
    }

    @Test
    public void testSaveUpdateNoExistingRecord() {
        MemberProfile expected = mkMemberProfile();
        expected.setId(testUuid);

        when(mockMemberProfileRepository.findById(testUuid)).thenReturn(null);

        MemberProfileBadArgException thrown = assertThrows(MemberProfileBadArgException.class, () -> {
            testObject.saveProfile(expected);
        });

        assertEquals("No member profile exists for the ID", thrown.getMessage());
    }
}
