import React, { useContext, useState } from "react";
import PropTypes from "prop-types";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
} from "@material-ui/core";
import { Skeleton } from "@material-ui/lab";
import { AppContext } from "../../context/AppContext";
import EditTeamModal from "./EditTeamModal";
import { updateTeam } from "../../api/team";

const propTypes = {
  team: PropTypes.shape({
    id: PropTypes.string,
    name: PropTypes.string,
    description: PropTypes.string,
  }),
};

const displayName = "TeamSummaryCard";

const TeamSummaryCard = ({ team, handleUpdate }) => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const [time, setTime] = useState(Date.now());

  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  // For debugging
  //if (team.name !== 'Micronaut Genii') return null;

  const formatMember = (member, isLead) => {
    if (!member.memberid) member.memberid = member.id;
    member.lead = isLead;
    return member;
  };

  const handleSave = async (team) => {
    setOpen(false);

    const postBody = {
      name: team.name,
      description: team.description,
      teamMembers: [
        ...team.teamMembers.map((member) => formatMember(member, false)),
        ...team.teamLeads.map((lead) => formatMember(lead, true)),
      ],
      id: team.id,
    };
    await updateTeam(postBody);

    handleUpdate(team);
    setTime(Date.now());
  };

  const userCanEdit = () => {
    const leads = team.teamMembers.filter((teamMember) => teamMember.lead);
    const thisUserLead = leads.filter((lead) =>
      userProfile ? lead.memberid === userProfile.memberProfile.id : false
    );

    const isLead = thisUserLead.length > 0;
    return userProfile && userProfile.role &&
      (userProfile.role.includes("ADMIN") || isLead);
  };


  return (
    <Card>
      <div>time = {time}</div>
      <CardHeader title={team.name} subheader={team.description} />
      <CardContent>
        {!team.teamMembers ? (
          <React.Fragment>
            <Skeleton />
            <Skeleton />
          </React.Fragment>
        ) : (
          <React.Fragment>
            <strong>Team Leads: </strong>
            {team.teamLeads.map(member => member.name).join(', ')}
            <br />
            <strong>Team Members: </strong>
            {team.teamMembers.map(member => member.name).join(', ')}
          </React.Fragment>
        )}
      </CardContent>
      <CardActions style={{ display: userCanEdit() ? "block" : "none" }}>
        <Button onClick={handleOpen}>Edit Team</Button>
        <Button>Delete Team</Button>
      </CardActions>
      <EditTeamModal
        team={team}
        open={open}
        onClose={handleClose}
        onSave={handleSave}
      />
    </Card>
  );
};

TeamSummaryCard.displayName = displayName;
TeamSummaryCard.propTypes = propTypes;

export default TeamSummaryCard;
