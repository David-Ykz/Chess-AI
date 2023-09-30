import { useState, useMemo, useCallback, useEffect } from "react";
import { Chessboard } from "react-chessboard";
import { Chess } from "chess.js";
import CustomDialog from "./components/CustomDialog";
import socket from "./socket";

function Game({ players, room, orientation, cleanup }) {
    const chess = useMemo(() => new Chess(), []); // <- 1
    const [fen, setFen] = useState(chess.fen()); // <- 2
    const [over, setOver] = useState("");

    const makeAMove = useCallback(
        (move) => {
            try {
                const result = chess.move(move); // update Chess instance
                setFen(chess.fen()); // update fen state to trigger a re-render

                console.log("over, checkmate", chess.isGameOver(), chess.isCheckmate());

                if (chess.isGameOver()) {
                    if (chess.isCheckmate()) {
                        setOver(
                            `Checkmate! ${chess.turn() === "w" ? "black" : "white"} wins!`
                        );
                    } else if (chess.isDraw()) {
                        setOver("Draw");
                    } else {
                        setOver("Game over");
                    }
                }

                return result;
            } catch (e) {
                return null;
            } // null if the move was illegal, the move object if the move was legal
        },
        [chess]
    );
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
        if (move !== null) {
            console.log("Transmitted Move Data");
            socket.emit("Move",  moveData);
        }
        // illegal move
        return move !== null;


    }
    // Game component returned jsx
    return (
        <>
            <div className="board">
                <Chessboard position={fen} onPieceDrop={onDrop}  boardWidth={800} />  {/**  <- 4 */}
            </div>
            <CustomDialog // <- 5
                open={Boolean(over)}
                title={over}
                contentText={over}
                handleContinue={() => {
                    setOver("");
                }}
            />
        </>
    );
}

export default Game;