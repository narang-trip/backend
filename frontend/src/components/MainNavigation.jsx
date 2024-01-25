import { NavLink } from "react-router-dom";
import { PencilSquareIcon } from "@heroicons/react/24/outline";
import { GoHome, GoPersonAdd, GoSearch } from "react-icons/go";
import { LiaCalendarDaySolid } from "react-icons/lia";
import Tooltip from "./ToolTip";
function MainNavigation() {
  return (
    <div>
      <nav className="flex justify-items-center">
        <ul className="flex-row">
          <Tooltip text="메인화면">
            <li className="mb-[100px] bg-white bg-opacity-0 hover:bg-opacity-100">
              <NavLink to="/">
                <GoHome className="w-14 h-14" />
              </NavLink>
            </li>
          </Tooltip>
          <li className="bg-white bg-opacity-0 hover:bg-opacity-100">
            <Tooltip text="동행모집작성">
              <NavLink to="/write">
                <PencilSquareIcon className="w-14 h-14" />
              </NavLink>
            </Tooltip>
          </li>
          <li className="bg-white bg-opacity-0 hover:bg-opacity-100">
            <Tooltip text="동행모집검색">
              <NavLink to="/search">
                <GoSearch className="w-14 h-14" />
              </NavLink>
            </Tooltip>
          </li>
          <li className="bg-white bg-opacity-0 hover:bg-opacity-100">
            <Tooltip text="신청현황">
              <NavLink to="/applicantList">
                <GoPersonAdd className="w-14 h-14" />
              </NavLink>
            </Tooltip>
          </li>
          <li className="bg-white bg-opacity-0 hover:bg-opacity-100">
            <Tooltip text="여행일정생성">
              <NavLink to="/planning">
                <LiaCalendarDaySolid className="w-14 h-14" />
              </NavLink>
            </Tooltip>
          </li>
          <li>
            <NavLink to="/chatRoomTest">ChatroomTest</NavLink>
          </li>
          <li>
            <NavLink to="/practice">Practice</NavLink>
          </li>
        </ul>
      </nav>
    </div>
  );
}

export default MainNavigation;
