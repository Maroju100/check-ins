ALTER TABLE team_member
DROP COLUMN teamid;

ALTER TABLE team_member
ADD COLUMN team_id varchar;

ALTER TABLE team_member
ADD PRIMARY KEY (team_id);