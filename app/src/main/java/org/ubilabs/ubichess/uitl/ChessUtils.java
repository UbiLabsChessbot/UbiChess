package org.ubilabs.ubichess.uitl;


import android.os.Environment;
import android.util.Log;

import org.opencv.core.Point;
import org.ubilabs.ubichess.modle.Chess;
import org.ubilabs.ubichess.modle.Chessboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChessUtils {
    private static final String TAG = ChessUtils.class.getSimpleName();

    public static final int CHESS_RADIUS = 40;
    public static Chessboard[][] chessboard = new Chessboard[8][8];
    public static Chessboard[][] chessboardBowl = new Chessboard[8][4];
    public static Chessboard[][] preChessboard = new Chessboard[8][8];
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

    //c5 -> [3][2]
    public static int[] chessPosition2LogicPosition(char[] chessPosition) {
        int[] logicPosition = new int[2];
        logicPosition[0] = '8' - chessPosition[1];
        logicPosition[1] = chessPosition[0] - 'a';
        return logicPosition;
    }

    public static void printChessState(Chessboard[][] chessboard) {
        for (int row = 0; row < 8; row++) {
            String tmp = "";
            for (int col = 0; col < 8; col++) {
                Chess chess = chessboard[row][col].getChess();
                if (chess != null) {
                    tmp += chess.getChessType();
                } else {
                    tmp += 1;
                }
            }
            Log.e(TAG, tmp);
        }
    }

    public static boolean checkChessType(String message, File imgFile) {
        String from = "";
        String to = "";
        String[] chessTypes = {"p", "r", "n", "b", "q", "k", "P", "R", "N", "B", "Q", "K"};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String dateString = sdf.format(new Date());
        Pattern pattern;
        Matcher matcher;

        for (String chessType : chessTypes) {
            pattern = Pattern.compile(chessType);
            matcher = pattern.matcher(message);
            String chessTypeUpperCase = chessType.toUpperCase();
            int cnt = 0;
            while (matcher.find()) {
                cnt++;
            }
            int rightCnt;

            switch (chessTypeUpperCase) {
                case "P":
                    rightCnt = 8;
                    break;
                case "Q":
                case "K":
                    rightCnt = 1;
                    break;
                default:
                    rightCnt = 2;
                    break;
            }

            if (cnt != rightCnt) {
                if (cnt < rightCnt) {
                    from += chessType;
                } else if (cnt > rightCnt) {
                    to += chessType;
                }
            }
        }
        if (from.length() != 0 || to.length() != 0) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Chess/" + from + "2" + to + "_" + dateString + ".png");
            if (!file.getParentFile().exists()) {
                boolean isCreated = file.getParentFile().mkdir();
                if (isCreated) {
                    return false;
                }
            }
            boolean isSuccess = imgFile.renameTo(file);
            Log.e(TAG, "Wrong img Rename: " + isSuccess + " about: " + from + "2" + to);
            return false;
        }
        return true;
    }

    public static int[] findEmptyChessboard() {
        int[] position = new int[2];
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                if (chessboard[row][col].getChess() == null) {
                    position[0] = row;
                    position[1] = col;
                    return position;
                }
            }
        }
        return position;
    }

    public static int[] findEmptyChessboardBowl() {
        int[] position = new int[2];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 4; col++) {
                if (chessboardBowl[row][col].getChess() == null) {
                    position[0] = row;
                    position[1] = col;
                    return position;
                }
            }
        }
        return position;
    }

    public static int[] findSpecialChessFromChessboardBowl(char chessType) {
        int[] position = new int[2];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 4; col++) {
                if (chessboardBowl[row][col].getChess() != null && chessboardBowl[row][col].getChess().getChessType() == chessType) {
                    position[0] = row;
                    position[1] = col;
                    return position;
                }
            }
        }
        return position;
    }
}
