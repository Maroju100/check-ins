import React, { useEffect, useReducer, useMemo } from "react";
import { getCurrentUser, updateMember } from "../api/member.js";
import { getCheckinByMemberId, createCheckin } from "../api/checkins";

export const MY_PROFILE_UPDATE = "update_profile";
export const UPDATE_USER_BIO = "update_bio";
export const UPDATE_CHECKINS = "update_checkins";
export const UPDATE_INDEX = "update_index";

const AppContext = React.createContext();

const reducer = (state, action) => {
  switch (action.type) {
    case MY_PROFILE_UPDATE:
      state.userProfile = action.payload;
      break;
    case UPDATE_USER_BIO:
      state.userProfile.bioText = action.payload;
      updateMember(state.userProfile);
      break;
    case UPDATE_CHECKINS:
      state.checkins = action.payload;
      state.index = state.checkins.length - 1;
      break;
    case UPDATE_INDEX:
      state.index = action.payload;
      break;
    default:
  }
  return { ...state };
};

const initialState = {
  userProfile: undefined,
  checkins: [],
  index: -1,
};

const AppContextProvider = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const memberProfile =
    state && state.userProfile && state.userProfile.memberProfile
      ? state.userProfile.memberProfile
      : undefined;
  const id = memberProfile ? memberProfile.uuid : undefined;

  const pdlId = memberProfile ? memberProfile.pdlId : undefined;

  useEffect(() => {
    async function updateUserProfile() {
      if (initialState.userProfile === undefined) {
        let res = await getCurrentUser();
        let profile =
          res.payload && res.payload.data && !res.error
            ? res.payload.data
            : undefined;

        if (profile) {
          dispatch({ type: MY_PROFILE_UPDATE, payload: profile });
        }
      }
    }
    updateUserProfile();
  }, []);

  const date = (months, prevCheckinDate) => {
    let newDate = prevCheckinDate ? new Date(...prevCheckinDate) : new Date();
    newDate.setMonth(newDate.getMonth() + months);
    const year = newDate.getFullYear();
    const month = newDate.getMonth() + 1;
    const day = newDate.getDate();
    const monthArray = [year, month, day];
    return monthArray;
  };

  useEffect(() => {
    async function getCheckins() {
      if (id) {
        const res = await getCheckinByMemberId(id);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data && data.length > 0) {
          const allComplete = data.every(
            (checkin) => checkin.completed === true
          );
          if (allComplete) {
            const prevCheckinDate = data[data.length - 1].checkInDate;
            const res = await createCheckin({
              teamMemberId: id,
              pdlId: pdlId,
              checkInDate: date(3, prevCheckinDate),
              completed: false,
            });
            const checkin =
              res.payload && res.payload.data && !res.error
                ? res.payload.data
                : null;
            data.push(checkin);
          }
        } else if (data.length === 0) {
          const res = await createCheckin({
            teamMemberId: id,
            pdlId: pdlId,
            checkInDate: date(1),
            completed: false,
          });
          const checkin =
            res.payload && res.payload.data && !res.error
              ? res.payload.data
              : null;
          data = [checkin];
        }
        dispatch({ type: UPDATE_CHECKINS, payload: data });
      }
    }
    getCheckins();
  }, [id, pdlId]);

  const value = useMemo(() => {
    return { state, dispatch };
  }, [state]);
  return (
    <AppContext.Provider value={props.value || value}>
      {props.children}
    </AppContext.Provider>
  );
};

export { AppContext, AppContextProvider };
