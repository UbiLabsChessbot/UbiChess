package org.ubilabs.ubichess.modle;


import org.ubilabs.ubichess.uitl.ChessUtils;

public class Chess {
    private char chessType;
    private boolean alive;
    private int[] logicPosition;
    private double[] absolutePosition;

    public Chess() {
        alive = true;
        logicPosition = new int[2];
        absolutePosition = new double[2];
    }

    public char getChessType() {
        return chessType;
    }

    public void setChessType(char chessType) {
        this.chessType = chessType;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAbsolutePosition(double x, double y) {
        this.absolutePosition[0] = x;
        this.absolutePosition[1] = y;
    }

    public int[] getLogicPosition() {
        return logicPosition;
    }

    public void setLogicPosition(int row, int col) {
        this.logicPosition[0] = row;
        this.logicPosition[1] = col;
    }

    public double[] getAbsolutePosition() {
        if (ChessUtils.chessboard[7][7] != null && ChessUtils.chessboardBowl[7][3] != null) {
            return ChessUtils.logicPosition2AbsolutePosition(logicPosition, alive);
        }
        return absolutePosition;
    }

    public Chess deepClone() {
        Chess chess = new Chess();
        chess.setChessType(this.chessType);
        chess.setAbsolutePosition(this.absolutePosition[0], this.absolutePosition[1]);
        chess.setAlive(this.alive);
        chess.setLogicPosition(this.logicPosition[0], this.logicPosition[1]);
        return chess;
    }
}
