import { useState, useMemo, useCallback, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import { Chess } from "chess.js";
import axios from 'axios';
import {Button, Card, Col, Row, Table} from 'react-bootstrap';
import CustomDialog from "./components/CustomDialog";
let playerColor = "white";

function Game() {
    const chess = useMemo(() => new Chess(), []);
    const [fen, setFen] = useState(chess.fen());
    const [over, setOver] = useState("");
    const [gameId, setGameId] = useState(-1);
    const makeAMove = useCallback(
        (move) => {
            try {
                const result = chess.move(move);
                setFen(chess.fen());
                checkGameEnd();
                return result;
            } catch (e) {
                return null;
            }
        },
        [chess]
    );

    function setBoard(fen) {
        chess.load(fen);
        setFen(fen);
    }

    function initializeNewGame() {
        axios.post('http://localhost:8080/new-game', playerColor).then(response => {
            const id = response.data.substring(0, response.data.indexOf("|"));
            console.log("Started new game with id: " + id);
            setGameId(id);
            console.log(response.data.substring(response.data.indexOf("|") + 1));
            setBoard(response.data.substring(response.data.indexOf("|") + 1));
        });
    }

    useEffect(() => {
        initializeNewGame()
    }, [])

    function checkGameEnd() {
        if (chess.isGameOver()) {
            if (chess.isCheckmate()) {
                setOver(
                    `Checkmate! ${chess.turn() === "w" ? "Black" : "White"} wins!`
                );
            } else if (chess.isDraw()) {
                setOver("Draw");
            } else {
                setOver("Game over");
            }
        }
    }

    function revertMove() {
        axios.post('http://localhost:8080/undo-move', gameId.toString()).then(response => {
            console.log(response.data);
            setBoard(response.data);
        });
    }

    function switchColors() {
        playerColor = playerColor === "white" ? "black" : "white";
        initializeNewGame();
    }


    function onDrop(sourceSquare, targetSquare, promotionPiece) {
        const moveData = {
            from: sourceSquare,
            to: targetSquare,
            color: chess.turn(),
            promotion: promotionPiece.substring(1).toLowerCase()
        };
        const move = makeAMove(moveData);
        if ((chess.turn() !== playerColor) && (move !== null)) {
            var data = {id: gameId, move: moveData};
            axios.post('http://localhost:8080/process-move', data).then(response => {
                    console.log(response.data);
                    if (response.data === "checkmate") {
                        setOver(
                            `Checkmate! ${chess.turn() === "w" ? "Black" : "White"} wins!`
                        );
                    } else if (response.data === "draw") {
                        setOver("Draw");
                    } else {
                        setBoard(response.data);
                        checkGameEnd();
                    }
                })
                .catch(error => {
                    console.error('Error fetching data:', error);
                });
        }
        return move !== null;
    }

    return (
        <Row>
            <Col>
                <div style={{display: 'flex', alignItems: 'center'}}>
                    <Chessboard position={fen} onPieceDrop={onDrop} customBoardStyle={{display: 'flex', alignItems: 'center'}} boardOrientation={playerColor} />
                </div>
                <CustomDialog
                    open={Boolean(over)}
                    title={over}
                    contentText={over}
                    handleContinue={() => {
                        initializeNewGame()
                        setOver("");
                    }}
                />

                <button onClick={revertMove}>Revert Move</button> {/** Add a button to call revertMove */}
                <button onClick={switchColors}>Swap Sides</button> {/** Add a button to call revertMove */}
                <button onClick={initializeNewGame}>New Game</button> {/** Add a button to call revertMove */}

            </Col>
            <Col>
                <div style={{fontSize: '24px'}}>
                    About Chess AI
                </div>
                <div style={{fontSize: '14px'}}>
                    <br/>
                    The AI is split into 3 parts: move generation, search, and evaluation
                    <br/><br/>
                    Move generation: The AI uses bitboards to represent pieces, with each bit in a long representing a square on the chessboard.
                    This approach leverages the efficiency of bitwise and bitshift operations to generate ~4 million positions per second
                    <br/><br/>
                    Search: The AI employs a minimax recursive search to evaluates potential moves.
                    This search process is speed up by alpha-beta pruning and move-ordering: a method of pruning the search tree to
                    avoid searching unnecessary moves. Finally, the AI uses a transposition table to store search results to avoid spending
                    time computing the same positions.
                    <br/><br/>
                    Evaluation: To measure how good a position is, the AI counts the material value of each board and uses
                    additional heuristics like piece position or the mobility of pieces.
                    <br/><br/>
                    Performance: The AI is estimated to be rated ~2100 on Lichess, which ranks it in the top 1% of players.
                    It is able to easily beat Stockfish Level 5 and is evenly matched against Stockfish Level 6. Some of the games are linked below:
                    <br/><br/>
                    <Table>
                        <Row style={{fontWeight: 'bold'}}>
                            <Col xs={2}>White</Col>
                            <Col xs={2}>Black</Col>
                            <Col xs={2}>Result</Col>
                            <Col xs={6}>Opening</Col>
                        </Row>
                        <Row style={{paddingTop: '7px'}}>
                            <Col xs={2}>Chess AI</Col>
                            <Col xs={2}>Stockfish 6</Col>
                            <Col xs={2}><a href='https://lichess.org/A6cWu2wx/white' style={{color: '#0D77FD'}}>1-0</a></Col>
                            <Col xs={6}>Sicilian Defense, Dragon Variation</Col>
                        </Row>
                        <Row style={{paddingTop: '7px'}}>
                            <Col xs={2}>Stockfish 6</Col>
                            <Col xs={2}>Chess AI</Col>
                            <Col xs={2}><a href='https://lichess.org/bQ9LX5aV/black' style={{color: '#0D77FD'}}>1-0</a></Col>
                            <Col xs={6}>English Opening</Col>
                        </Row>
                        <Row style={{paddingTop: '7px'}}>
                            <Col xs={2}>Stockfish 5</Col>
                            <Col xs={2}>Chess AI</Col>
                            <Col xs={2}><a href='https://lichess.org/7GfeyRT0/black' style={{color: '#0D77FD'}}>0-1</a></Col>
                            <Col xs={6}>Indian Defense</Col>
                        </Row>
                    </Table>
                </div>
            </Col>
        </Row>
    );
}

export default Game;