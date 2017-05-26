package org.ubilabs.ubichess.modle;


public class Chessboard {
    public double x;
    public double y;
    private Chess chess;

    public final static char[][] INITCHESSBOARD = {
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {'1', '1', '1', '1', '1', '1', '1', '1'},
            {'1', '1', '1', '1', '1', '1', '1', '1'},
            {'1', '1', '1', '1', '1', '1', '1', '1'},
            {'1', '1', '1', '1', '1', '1', '1', '1'},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };

    public Chess getChess() {
        return chess;
    }

    public void setChess(Chess chess) {
        this.chess = chess;
    }

    private Chessboard deepClone() {
        Chessboard chessboard = new Chessboard();
        chessboard.x = this.x;
        chessboard.y = this.y;
        if (this.chess != null) {
            chessboard.setChess(this.chess.deepClone());
        } else {
            chessboard.setChess(null);
        }
        return chessboard;
    }

    public static Chessboard[][] deepCloneArray(Chessboard[][] oldArray) {
        Chessboard[][] chessboard = new Chessboard[oldArray.length][oldArray[0].length];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                chessboard[row][col] = oldArray[row][col].deepClone();
            }
        }
        return chessboard;
    }
}
