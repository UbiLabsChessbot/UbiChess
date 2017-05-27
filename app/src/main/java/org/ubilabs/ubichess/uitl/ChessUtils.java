package org.ubilabs.ubichess.uitl;


import org.opencv.core.Point;
import org.ubilabs.ubichess.modle.Chess;
import org.ubilabs.ubichess.modle.Chessboard;

import java.nio.channels.Pipe;

public class ChessUtils {
    public static Chessboard[][] chessboard = new Chessboard[8][8];
    public static Chessboard[][] chessboardBowl = new Chessboard[8][4];
    public static Chess[] chess = new Chess[32];
    public static Point[] chessboardKeyPoints = new Point[4];
    public static Point[] chessboardBowlKeyPoints = new Point[4];

    //[7][0] -> x,y
    public static double[] logicPosition2AbsolutePosition(int[] logicPosition, boolean isAlive) {
        double[] absolutePosition = new double[2];
        int row = logicPosition[0];
        int col = logicPosition[1];
        if (isAlive) {
            absolutePosition[0] = chessboard[row][col].x;
            absolutePosition[1] = chessboard[row][col].y;
        } else {
            absolutePosition[0] = chessboardBowl[row][col].x;
            absolutePosition[1] = chessboardBowl[row][col].y;
        }
        return absolutePosition;
    }

    //[3][2] -> c5
    public static char[] logicPosition2ChessPosition(int[] logicPosition) {
        char[] chessPosition = new char[2];
        chessPosition[0] = (char) (logicPosition[1] + 'a');
        chessPosition[1] = (char) (8 - logicPosition[0] + '0');
        return chessPosition;
    }
}
