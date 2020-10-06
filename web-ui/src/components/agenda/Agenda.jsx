import React, { useState, useEffect } from "react";
import "./Agenda.css";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import {
  getAgendaItem,
  deleteAgendaItem,
  updateAgendaItem,
} from "../../api/agenda.js";
import DragIndicator from "@material-ui/icons/DragIndicator";

async function getAgendaItems(checkinId, mockAgendaItems, setAgendaItems) {
  if (mockAgendaItems) {
    setAgendaItems(mockAgendaItems);
    return;
  }

  let res = await getAgendaItem(checkinId, null);
  if (res && res.payload) {
    let agendaItemList =
      res.payload.data && !res.error ? res.payload.data : undefined;
    setAgendaItems(agendaItemList);
  }
}

const AgendaItems = ({ checkinId, mockAgendaItems }) => {
  let [agendaItems, setAgendaItems] = useState();

  async function deleteItem(id) {
    if (id) {
      await deleteAgendaItem(id);
    }
  }

  async function doUpdate(agendaItem) {
    if (agendaItem) {
      await updateAgendaItem(agendaItem);
    }
  }

  useEffect(() => {
    getAgendaItems(checkinId, mockAgendaItems, setAgendaItems);
  }, [checkinId, mockAgendaItems, setAgendaItems]);

  const getAgendaItemStyle = (agendaItem) => {
    if (agendaItem && agendaItem.description) {
      return "agenda-items-info";
    }
    return "agenda-items-info-hidden";
  };

  const getAgendaItemText = (agendaItem) => {
    if (agendaItem && agendaItem.description) {
      return agendaItem.description;
    }
    return "Lorem Ipsum Etcetera";
  };

  const reorder = (list, startIndex, endIndex) => {
    const result = Array.from(list);
    const [removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);
    return result;
  };

  const grid = 8;

  const getListStyle = (isDraggingOver) => ({
    padding: grid,
  });

  const getItemStyle = (isDragging, draggableStyle) => ({
    userSelect: "none",
    padding: grid * 2,
    margin: "0 0 {grid}px 0",
    textAlign: "left",
    marginBottom: "1px",
    marginTop: "1px",
    display: "flex",
    flexDirection: "row",

    background: isDragging ? "lightgreen" : "#fafafa",

    ...draggableStyle,
  });

  const onDragEnd = (result) => {
    if (!result || !result.destination) {
      return;
    }

    agendaItems = reorder(
      agendaItems,
      result.source.index,
      result.destination.index
    );

    let precedingPriority = 0;
    if (result.destination.index > 0) {
      precedingPriority = agendaItems[result.destination.index - 1].priority;
    }

    let followingPriority = agendaItems[agendaItems.length - 1].priority + 1;
    if (result.destination.index < agendaItems.length - 1) {
      followingPriority = agendaItems[result.destination.index + 1].priority;
    }

    let newPriority = (precedingPriority + followingPriority) / 2;

    agendaItems[result.destination.index].priority = newPriority;

    doUpdate(agendaItems[result.destination.index]);
  };

  const killAgendaItem = (id, event) => {
    deleteItem(id);
    var arrayDupe = agendaItems;
    for (var i = 0; i < arrayDupe.length; i++) {
      if (arrayDupe[i].id === id) {
        arrayDupe.splice(i, 1);
        break;
      }
    }
    setAgendaItems(arrayDupe);
  };

  const createFakeEntry = (item) => {
    return (
      <div key={item.id} className="image-div">
        <span>
          <DragIndicator />
        </span>
        <div className="description-field">
          <p className="agenda-items-info-hidden">Lorem Ipsum etc</p>
        </div>
      </div>
    );
  };

  const createAgendaItemEntries = () => {
    if (agendaItems && agendaItems.length > 0) {
      return agendaItems.map((agendaItem, index) => (
        <Draggable
          key={agendaItem.id}
          draggableId={agendaItem.id}
          index={index}
        >
          {(provided, snapshot) => (
            <div
              key={agendaItem.id}
              ref={provided.innerRef}
              {...provided.draggableProps}
              style={getItemStyle(
                snapshot.isDragging,
                provided.draggableProps.style
              )}
            >
              <div className="description-field">
                <span {...provided.dragHandleProps}>
                  <DragIndicator />
                </span>
                <p className={getAgendaItemStyle(agendaItem)}>
                  {getAgendaItemText(agendaItem)}
                </p>
              </div>
              <div>
                <button
                  className="delete-button"
                  onClick={(e) => killAgendaItem(agendaItem.id, e)}
                >
                  -
                </button>
              </div>
            </div>
          )}
        </Draggable>
      ));
    } else {
      let fake = Array(3);
      for (let i = 0; i < fake.length; i++) {
        fake[i] = createFakeEntry({ id: `${i + 1}Agenda` });
      }
      return fake;
    }
  };

  return (
    <fieldset className="action-items-container">
      <legend>My Agenda Items</legend>
      <DragDropContext onDragEnd={onDragEnd}>
        <Droppable droppableId="droppable">
          {(provided, snapshot) => (
            <div
              {...provided.droppableProps}
              ref={provided.innerRef}
              style={getListStyle(snapshot.isDraggingOver)}
            >
              {createAgendaItemEntries()}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </DragDropContext>
    </fieldset>
  );
};

export default AgendaItems;
