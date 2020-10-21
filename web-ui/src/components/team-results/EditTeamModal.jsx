import React, { useContext, useState } from "react";
import Modal from "@material-ui/core/Modal";
import TextField from "@material-ui/core/TextField";
import Autocomplete from "@material-ui/lab/Autocomplete";
import "./EditTeamModal.css";
import { Button } from "@material-ui/core";
import {
  AppContext,
  UPDATE_TEAM_MEMBER,
  UPDATE_TEAM,
} from "../../context/AppContext";

const EditTeamModal = ({ team = {}, open, onSave, onClose }) => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles } = state;
  const [editedTeam, setTeam] = useState(team);

  const teamMemberOptions = memberProfiles;

  const updateTeamId = (isLead, oldTeam, newTeam) => {
    // Find the team members that are being added.
    let memberIds = oldTeam.map(member => member.id);
    const newMembers = newTeam.filter(
      member => !memberIds.includes(member.id)
    );

    // Find the team members that are being removed.
    memberIds = newTeam.map(member => member.id);
    const oldMembers = editedTeam.teamMembers.filter(
      member => !memberIds.includes(member.id)
    );

    let list = isLead ? editedTeam.teamLeads : editedTeam.teamMembers;
    let member;
    if (newMembers.length > 0) {
      member = newMembers[0];
      member.teamid = editedTeam.id;
      list.push(member);
    } else if (oldMembers.length > 0) {
      member = oldMembers[0];
      member.teamid = null;
      list = list.filter((m) => m.id !== member.id);
    }

    setTeam({ ...editedTeam });

    //dispatch({ type: UPDATE_TEAM_MEMBER, payload: member });
    //dispatch({ type: UPDATE_TEAM, payload: editedTeam });

    return member;
  };

  const onLeadsChange = (event, teamLeads) => {
    const member = updateTeamId(true, editedTeam.teamLeads, teamLeads);
    member.lead = true;
    setTeam({...editedTeam, teamLeads});
  };

  const onTeamMembersChange = (event, teamMembers) => {
    const member = updateTeamId(false, editedTeam.teamMembers, teamMembers);
    member.lead = false;
    setTeam({...editedTeam, teamMembers});
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      aria-labelledby="edit-team-modal-title"
    >
      <div className="EditTeamModal">
        <h2>Edit your team</h2>
        <TextField
          id="team-name-input"
          label="Team Name"
          required
          className="halfWidth"
          placeholder="Awesome Team"
          value={editedTeam.name ? editedTeam.name : ""}
          onChange={(e) => setTeam({ ...editedTeam, name: e.target.value })}
        />
        <TextField
          id="team-description-input"
          label="Description"
          className="fullWidth"
          placeholder="What do they do?"
          value={editedTeam.description ? editedTeam.description : ""}
          onChange={(e) =>
            setTeam({ ...editedTeam, description: e.target.value })
          }
        />
        <Autocomplete
          multiple
          options={teamMemberOptions}
          value={editedTeam.teamLeads ? editedTeam.teamLeads : []}
          onChange={onLeadsChange}
          getOptionLabel={(option) => option.name}
          getOptionSelected={(option, value) =>
            value ? value.id === option.id : false
          }
          renderInput={(params) => (
            <TextField
              {...params}
              className="fullWidth"
              label="Team Leads"
              placeholder="Add a team lead..."
            />
          )}
        />
        <Autocomplete
          multiple
          options={teamMemberOptions}
          value={editedTeam.teamMembers ? editedTeam.teamMembers : []}
          onChange={onTeamMembersChange}
          getOptionLabel={(option) => option.name}
          getOptionSelected={(option, value) =>
            value ? value.id === option.id : false
          }
          renderInput={(params) => (
            <TextField
              {...params}
              className="fullWidth"
              label="Team Members"
              placeholder="Add a team member..."
            />
          )}
        />
        <div className="EditTeamModal-actions fullWidth">
          <Button onClick={onClose} color="secondary">
            Cancel
          </Button>
          <Button onClick={() => onSave(editedTeam)} color="primary">
            Save Team
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default EditTeamModal;
