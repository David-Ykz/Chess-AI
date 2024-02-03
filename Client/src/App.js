import { Container } from "@mui/material";
import React, {Component, useEffect} from 'react'
import Game from "./Game";
export default function App() {
    useEffect(() => {
        document.title = 'Chess AI';
    }, []);

    return (
        <Container>
            <Game />
        </Container>
    );
}