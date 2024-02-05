import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from 'axios'
import { EventSourcePolyfill, NativeEventSource } from "event-source-polyfill";
import Button from "../ui/Button";
import { Client, Stomp } from "@stomp/stompjs";

const userId = "조예진";
const dummyData = {
  tripId: "133",
  tripName: "여행가요 같이",
  senderId: "구본승",
  receiverId: userId,
  position: ["몰라"],
  aspiration: "여행 뿌셔",
  alertType: "REQUEST",
  isRead: false,
};
const sockJSEndPoint = "https://i10a701.p.ssafy.io/api/message/chat";
const stompEndpoint = "wss://i10a701.p.ssafy.io/api/message/chat";
const ChatPage = () => {
  useEffect(() => {
    const EventSource = EventSourcePolyfill || NativeEventSource;
    const eventSource = new EventSource(
      `https://i10a701.p.ssafy.io/api/message/alert/subscribe/${userId}` , {
        heartbeatTimeout : 3600000,
      }
    );

    eventSource.addEventListener("sse", (event) => {
      const { data: receivedConnectData } = event;
      console.log(receivedConnectData);
      if (receivedConnectData === "SSE 연결이 완료되었습니다.") {
        // "SSE connection has been completed."
        console.log("SSE CONNECTED");
      } else {
        console.log(event);
      }
    });

    return () => {
      eventSource.close();
      console.log("SSE CLOSED");
    };
  }, []);

  // useEffect(() => {
  //   // SockJS와 Stomp 설정
  //   const socket = new SockJS(sockJSEndPoint);
  //   const stompClient = new Client({
  //     webSocketFactory: () => socket, // SockJS 인스턴스를 사용하여 웹소켓 연결
  //     onConnect: (frame) => {
  //       console.log('Connected', frame);

  //       // 서버로부터 메시지를 받을 구독 설정
  //       stompClient.subscribe('/sub/chat/room/room1', message => {
  //         console.log(`Received: ${message.body}`);
  //       });

  //       // 서버로 메시지 전송
  //       stompClient.publish({
  //         destination: '/pub/chat/send',
  //         body: JSON.stringify({ chatroomId : "room1", senderId : "조예진", content : "이제 해방이다"}),
  //       });
  //     },
  //   });

  //   // 웹소켓 연결 활성화
  //   stompClient.activate();

  //   // 컴포넌트 언마운트 시 연결 종료
  //   return () => {
  //     stompClient.deactivate();
  //   };
  // }, []);

  const clickHandler = async () => {
    try {
      const response = await axios.post('https://i10a701.p.ssafy.io/api/message/alert/attend', dummyData);
      console.log("요청 보냈어요", response.data)
    } catch (error) {
      console.error('Error', error.response)
    }   
  };

  return (
    <div className="">
      <div className="">
        <label>
          <b>채팅방</b>
          <button onClick={clickHandler}>이거 누르면 알림 가요</button>
        </label>
      </div>
    </div>
  );
};

export default ChatPage;
