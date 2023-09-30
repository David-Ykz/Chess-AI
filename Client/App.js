import { Container } from "@mui/material";
import Game from "./Game";
import CustomDialog from "./components/CustomDialog";
import socket from "./socket";

export default function App() {
    return (
        <Container>
            <Game />
        </Container>
    );
}