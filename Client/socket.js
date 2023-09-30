import { io } from "socket.io-client"; // import connection function

const socket = io('127.0.0.1:8080'); // initialize websocket connection

export default socket;