import { useState, Fragment } from "react";
import { useNavigate } from "react-router-dom";

import TitleInput from "./TitleInput";
import ConceptSelect from "./ConceptSelect";
import DateRangePicker from "./DateRangePicker.jsx";
import FileUploadBox from "./FileUploadBox";
import PositionCheck from "./PositionCheck.jsx";
import { useDispatch } from "react-redux";

export default function TripWriteForm() {
  const [board, setBoard] = useState({
    title: "",
    concept: "",
    img: "",
    startDate: "",
    endDate: "",
    dateRange: [],
    location: "",
    count: "",
    position: [],
    plan: "",
    description: "",
  });

  // 여행 제목
  const [title, setTitle] = useState("");
  // 여행 컨셉
  const [concept, setConcept] = useState("");
  // 여행 시작 날짜
  const [startDate, setStartDate] = useState("");
  // 여행 끝 날짜
  const [endDate, setEndDate] = useState("");
  // 여행 기간
  const [dateRange, setDateRange] = useState([null, null]);
  // 여행 장소
  const [location, setLocation] = useState([null, null, null]);
  // 모집 인원
  const [count, setCount] = useState(0);
  // 여행 설명
  const [description, setDescription] = useState("");

  const dispatch = useDispatch();

  const navigate = useNavigate();

  // 값이 변할 때 추적하기 위한 함수
  const handleChange = (e) => {
    const { name, value } = e.target;
    setBoard({
      ...board,
      [name]: value,
    });
  };

  // 기간 변할 때 추적하는 함수
  const handleDateChange = (range) => {
    setDateRange(range);
    setFormData((prev) => ({
      ...prev,
      startDate: range[0],
      endDate: range[1],
      DateRange: range,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
  }; // handleSubmit 끝

  const saveWrite = () => {
    console.log("저장완료");
  };

  const cancle = () => {
    console.log("취소");
  };

  return (
    <Fragment>
      <div className="text-left">
        <form onSubmit={handleSubmit}>
          <div className="px-8 py-8 mx-auto border rounded-3xl bg-stone-100 border-stone-200">
            <div className="grid grid-cols-3 gap-6 px-8">
              <div className="col-span-3">
                <p className="my-8 text-4xl font-bold text-center">
                  동행 글 작성하기
                </p>
              </div>
              <div className="flex flex-col justify-between col-span-2">
                <TitleInput value={formData.title} onChange={handleChange} />
                <ConceptSelect
                  value={formData.concept}
                  onChange={handleChange}
                />
                <div className="w-full my-5">
                  <label className="mr-10 text-xl font-medium">대표 사진</label>
                  <input type="file" />
                </div>
                <DateRangePicker
                  dateRange={dateRange}
                  onChange={handleDateChange}
                />
                <div className="w-full my-5">
                  <label className="mr-10 text-xl font-medium">여행 장소</label>
                </div>
                <PositionCheck
                  value={formData.position}
                  onChange={handleChange}
                />
                <div className="w-full my-5">
                  <label className="mr-10 text-xl font-medium">
                    여행 계획표
                  </label>
                </div>
              </div>
              <div>
                <div className="mt-5 h-2/5">
                  <img src={`assets/airplain.jpg`} className="h-full" />
                </div>
                <div className="mt-5 h-3/5">
                  <label className="text-xl font-medium">여행 설명</label>
                  <br />
                  <textarea className="w-full h-full" />
                </div>
              </div>
              <div className="flex justify-around col-start-2">
                <button
                  className="px-6 py-2 rounded-md text-stone-800 bg-stone-500 hover:text-stone-950"
                  onClick={saveWrite}
                >
                  저장
                </button>
                <button
                  className="px-6 py-2 rounded-md bg-stone-800 text-stone-50"
                  onClick={cancle}
                >
                  취소
                </button>
              </div>
            </div>
          </div>
        </form>
      </div>
    </Fragment>
  );
}
