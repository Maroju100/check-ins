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

  let teamMembers = AppContext.selectMemberProfilesByTeamId(state)(team.id);

  team.teamLeads =
    teamMembers == null
      ? null
      : teamMembers.filter((teamMember) => teamMember.lead);
  team.teamMembers =
    teamMembers == null
      ? null
      : teamMembers.filter((teamMember) => !teamMember.lead);
  console.log("team summary card", JSON.stringify(team.teamMembers));

  const [open, setOpen] = useState(false);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const formatMember = (member, isLead) => {
    if (!member.memberid) {
      member.memberid = member.id;
    }
    member.lead = isLead;
    return member;
  };

  const handleSave = (editedTeam) => {
    console.log("handleSave", JSON.stringify(editedTeam));
    setOpen(false);
    if (editedTeam) {
      let postBody = {
        name: editedTeam.name,
        description: editedTeam.description,
        teamMembers: [
          ...editedTeam.teamMembers.map((member) =>
            formatMember(member, false)
          ),
          ...editedTeam.teamLeads.map((lead) => formatMember(lead, true)),
        ],
        id: editedTeam.id,
      };
      //   editedTeam.teamMembers = [
      //     ...editedTeam.teamMembers,
      //     ...editedTeam.teamLeads,
      //   ];
      updateTeam(postBody);
      //   team = editedTeam;
      //   teamMembers = AppContext.selectMemberProfilesByTeamId(state)(
      //     editedTeam.id
      //   );
      //   editedTeam.teamLeads =
      //     teamMembers == null
      //       ? null
      //       : teamMembers.filter((teamMember) => teamMember.lead);
      //   editedTeam.teamMembers =
      //     teamMembers == null
      //       ? null
      //       : teamMembers.filter((teamMember) => !teamMember.lead);
      console.log("handleSave", JSON.stringify(editedTeam));
      handleUpdate(editedTeam);
    }
  };

  const userCanEdit = () => {
    const leads = teamMembers.filter((teamMember) => teamMember.lead);
    const thisUserLead = leads.filter((lead) =>
      userProfile ? lead.memberid === userProfile.memberProfile.id : false
    );

    const isLead = thisUserLead.length > 0;
    if (
      userProfile &&
      userProfile.role &&
      (userProfile.role.includes("ADMIN") || isLead)
    ) {
      return true;
    }

    return false;
  };

  return (
    <Card>
      <CardHeader title={team.name} subheader={team.description} />
      <CardContent>
        {teamMembers == null ? (
          <React.Fragment>
            <Skeleton />
            <Skeleton />
          </React.Fragment>
        ) : (
          <React.Fragment>
            <strong>Team Leads: </strong>
            {team.teamLeads.map((lead, index) => {
              return index !== team.teamLeads.length - 1
                ? `${lead.name}, `
                : lead.name;
            })}
            <br />
            <strong>Team Members: </strong>
            {team.teamMembers.map((member, index) => {
              return index !== team.teamMembers.length - 1
                ? `${member.name}, `
                : member.name;
            })}
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
