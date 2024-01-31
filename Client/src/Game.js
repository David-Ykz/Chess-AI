import { useState, useMemo, useCallback, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import { Chess } from "chess.js";
import axios from 'axios';
import CustomDialog from "./components/CustomDialog";
const previousMoves = [];
//const DEFAULT_POSITION = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1';
const DEFAULT_POSITION = '8/k7/3p4/p2P1p2/P2P1P2/8/8/K7 w -- - 0 1';
//const DEFAULT_POSITION = 'k7/8/8/P5n1/NN6/QN6/QN4NN/QN3BK1 w -- - 0 1';
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

    function initializeNewGame() {
        axios.get('http://localhost:8080/new-game').then(response => {
            console.log("Started new game with id: " + response.data);
            setGameId(response.data);
        });
    }

    // Initialize game instance
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
            chess.load(response.data);
            setFen(response.data);
        });
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
        initializeNewGame();
        chess.load(DEFAULT_POSITION);
        setFen(chess.fen());
        if (playerColor === "black") {
            console.log("Game is broken");
        }
    }

    function onDrop(sourceSquare, targetSquare, promotionPiece) {
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