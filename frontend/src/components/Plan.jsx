import { useState } from "react";
import DayPlan from "./DayPlan";
import { Droppable } from "react-beautiful-dnd";
import { useSelector, useDispatch } from "react-redux";
import { scheduleActions } from "../store/scheduleSlice";

// import * as Y from "yjs";
// import { WebrtcProvider } from "y-webrtc";

// const ydoc = new Y.Doc();

// const ymap = ydoc.getMap("test");
// const ymapNested = new Y.Map();

// ymap.set("my nested map", ymapNested);
// ymap.set("list", []);

// const provider = new WebrtcProvider("test-demo-room", ydoc, {
//   signaling: ["ws://localhost:5173"],
// });

// const awareness = provider.awareness;

// awareness.setLocalStateField("user", {
//   name: "Emmanuelle Charpentier",
//   color: "#ffb61e",
// });

// awareness.on("change", () => {
//   console.log(Array.from(awareness.getStates().values()));
// });

const Plan = () => {
  const list = useSelector((state) => state.schedule);
  const tmplist = [];
  const dispatch = useDispatch();

  // ydoc.on("afterTransaction", () => {
  //   setList(Array.from(ymap.get("list")));
  // });

  // useMemo(() => {
  //   ymap.set("list", list);
  // }, [JSON.stringify(list)]);

  const update = () => {};

  const add = () => {
    tmplist.push([]);
    dispatch(scheduleActions.setSchedule(tmplist));
    // dispatch(scheduleActions.setSchedule(sss));
    // dispatch(scheduleActions.setSchedule(sss));
  };

  // const onDragEnd = ({ source, destination }) => {
  //   if (!destination) return;

  //   console.log(source);
  //   console.log(destination);

  //   const scourceKey = Number(source.droppableId.replace("list", ""));
  //   const destinationKey = Number(destination.droppableId.replace("list", ""));

  //   const tmplist = Array.from(list);
  //   const [value] = tmplist.at(scourceKey).splice(source.index, 1);
  //   tmplist.at(destinationKey).splice(destination.index, 0, value);
  //   setList(tmplist);
  // };

  return (
    <div style={{ display: "flex" }}>
      {/* <DragDropContext onDragEnd={onDragEnd} style={{ display: "flex" }}> */}
      {list.map((data, index) => (
        <div style={{ flexDirection: "column" }} key={index}>
          <Droppable droppableId={`list${index}`}>
            {(provided) => (
              <div ref={provided.innerRef} {...provided.droppableProps}>
                <DayPlan
                  data={{ index: index + 1, list: list.at(index) }}
                  update={update}
                  key={index}
                />
              </div>
            )}
          </Droppable>
        </div>
      ))}
      {/* </DragDropContext> */}
      <button onClick={add}>날짜추가</button>
    </div>
  );
};

export default Plan;
