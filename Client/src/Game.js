import { useState, useMemo, useCallback, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import { Chess } from "chess.js";
import axios from 'axios';
import CustomDialog from "./components/CustomDialog";
const previousMoves = [];
const DEFAULT_POSITION = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
let playerColor = "white";

function Game({ players, room, orientation, cleanup }) {
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

    // Initialize game instance
    useEffect(() => {
        axios.get('http://localhost:8080/new-game').then(response => {
            console.log("Started new game with id: " + response.data);
            setGameId(response.data);
        });
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

    function onDrop(sourceSquare, targetSquare, promotionPiece, castleSquare) {
        console.log(castleSquare);
        const moveData = {
            from: sourceSquare,
            to: targetSquare,
            color: chess.turn(),
            promotion: promotionPiece.substring(1).toLowerCase(),
        };
        const move = makeAMove(moveData);
        if ((chess.turn() !== playerColor) && (move !== null)) {
            var data = {id: gameId, move: moveData};
            axios.post('http://localhost:8080/process-move', data).then(response => {
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
        return move !== null;
    }

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