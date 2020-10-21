import React, { useEffect, useContext, useState } from "react";
import PropTypes from "prop-types";
import Container from "@material-ui/core/Container";
import TeamSummaryCard from "./TeamSummaryCard";
import { AppContext, UPDATE_TEAMS } from "../../context/AppContext";
import { getAllTeams } from "../../api/team";
import "./TeamResults.css";

const propTypes = {
  teams: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string,
    })
  ),
};

const displayName = "TeamResults";

const TeamResults = () => {
  const { dispatch, state } = useContext(AppContext);
  const [teams, setTeams] = useState([]);

  // Every time the state changes, get team data from the database
  // and populate them with the correct leads and members.
  useEffect(() => {
    async function getTeams() {
      console.log('TeamResults.jsx useEffect: calling getAllTeams');
      let res = await getAllTeams();
      let data =
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      console.log('TeamResults useEffect: data =', data);
      if (data) {
        const { memberProfiles, teamMembers } = state;

        const membersById = {};
        for (const profile of memberProfiles) {
          membersById[profile.id] = profile;
        }

        const membersByTeamId = {};
        for (const teamMember of teamMembers) {
          const { memberid, teamid } = teamMember;
          let members = membersByTeamId[teamid];
          if (!members) membersByTeamId[teamid] = members = [];
          const member = membersById[memberid];
          members.push(member);
        }

        for (const team of data) {
          const allMembers = membersByTeamId[team.id];
          team.teamLeads = allMembers.filter(m => m.lead);
          team.teamMembers = allMembers.filter(m => !m.lead);
        }
        console.log('TeamResults useEffect: data =', data);
        setTeams(data);
      }
    }

    const { memberProfiles = [], teamMembers = [] } = state;
    if (memberProfiles.length && teamMembers.length) getTeams();
  }, [state]);

  const updateTeam = (updatedTeam) => {
    const newTeams = [...teams];
    const index = teams.findIndex(team => team.id === updatedTeam.id);
    newTeams[index] = { ...updatedTeam };
    setTeams(newTeams);
    const t = newTeams.find(t => t.name === 'Micronaut Genii');
    console.log('TeamResults updateTeam: Micronaut Genii =', JSON.stringify(t, null, 2));
    dispatch({ type: UPDATE_TEAMS, payload: newTeams });
  };

  return (
    <Container maxWidth="md">
      {teams.map(team => (
        <TeamSummaryCard
          key={`team-summary-${team.id}`}
          team={{ ...team }}
          handleUpdate={updateTeam}
        />
      ))}
    </Container>
  );
};

TeamResults.propTypes = propTypes;
TeamResults.displayName = displayName;

export default TeamResults;
