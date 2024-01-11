import { useState, useMemo, useCallback, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import { Chess } from "chess.js";
import axios from 'axios';
import CustomDialog from "./components/CustomDialog";
const previousMoves = [];
const DEFAULT_POSITION = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
let playerColor = "white";

function Game({ players, room, orientation, cleanup }) {
    const chess = useMemo(() => new Chess(), []); // <- 1
    const [fen, setFen] = useState(chess.fen()); // <- 2
    const [over, setOver] = useState("");
    const makeAMove = useCallback(
        (move) => {
            try {
                const result = chess.move(move); // update Chess instance
                setFen(chess.fen()); // update fen state to trigger a re-render
                checkGameEnd();
                return result; // returns the move object
            } catch (e) {
                return null; // returns null if the move was illegal
            }
        },
        [chess]
    );

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
        if ((previousMoves.length > 1) && (chess.turn() === playerColor.substring(0,1))) {
            previousMoves.pop();
            const oldFen = previousMoves[previousMoves.length - 1];
            console.log(oldFen);
            chess.load(oldFen);
            setFen(oldFen);
        }
    }

    function switchColors() {
        if (playerColor === "white") {
            playerColor = "black";
        } else {
            playerColor = "white";
        }
        newGame();
    }

    function newGame() {
        while (previousMoves.length > 0) {
            previousMoves.pop();
        }
        chess.load(DEFAULT_POSITION);
        setFen(chess.fen());
        if (playerColor === "black") {
            getData(chess.fen());
        }
    }

    function getData(fen) {
        fen = fen.toString();
        const backendIP = "y-backend.com";
        const backendPort = "8080"; // 80, 443
//        const url = `https://${backendIP}:${backendPort}/move`
        const url = "http://localhost:8080/move";
        console.log(fen.toString());
//        //const url = "https://main.d3kqvs59i8mifl.amplifyapp.com/move";
    //    const url = "https://ec2-3-133-93-27.us-east-2.compute.amazonaws.com/move";
        axios.post(url, fen)
            .then(response => {
                console.log(response.data);
                chess.load(response.data);
                setFen(response.data);
                previousMoves.push(response.data);
                checkGameEnd();
            })
            .catch(error => {
                 console.error('Error fetching data:', error);
            });
    }
    // onDrop function
    function onDrop(sourceSquare, targetSquare, promotionPiece) {
        console.log(promotionPiece);
        const moveData = {
            from: sourceSquare,
            to: targetSquare,
            color: chess.turn(),
            promotion: promotionPiece.substring(1).toLowerCase(),
        };
        const move = makeAMove(moveData);
        if ((chess.turn() !== playerColor) && (move !== null)) {
            console.log(previousMoves.length);
            console.log("Transmitted Move Data");
            getData(chess.fen());
        }
        // illegal move
        return move !== null;
    }
    // Game component returned jsx
    return (
        <>
            <div className="board">
                <Chessboard position={fen} onPieceDrop={onDrop}  boardWidth={600} boardOrientation={playerColor} />  {/**  <- 4 */}
            </div>
            <CustomDialog // <- 5
                open={Boolean(over)}
                title={over}
                contentText={over}
                handleContinue={() => {
                    setOver("");
                }}
            />
            <button onClick={revertMove}>Revert Move</button> {/** Add a button to call revertMove */}
            <button onClick={switchColors}>Swap Sides</button> {/** Add a button to call revertMove */}
            <button onClick={newGame}>New Game</button> {/** Add a button to call revertMove */}
        </>
    );
}

export default Game;