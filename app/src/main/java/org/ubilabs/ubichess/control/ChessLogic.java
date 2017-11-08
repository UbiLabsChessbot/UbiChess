package org.ubilabs.ubichess.control;


import android.nfc.Tag;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.ubilabs.ubichess.modle.Chess;
import org.ubilabs.ubichess.modle.Chessboard;
import org.ubilabs.ubichess.uitl.ChessUtils;

import java.util.concurrent.ExecutionException;

import me.michaeljiang.movesystemlibs.movesystem.MoveSystem;
import me.michaeljiang.movesystemlibs.movesystem.setting.ProjectSetting;

public class ChessLogic {
    private static final String TAG = ChessLogic.class.getSimpleName();

    private MoveSystem moveSystem;

    public ChessLogic(MoveSystem moveSystem) {
        this.moveSystem = moveSystem;
    }

    /**
     * @param chess 表示待归位的棋子
     * @param row   表示检查的棋盘行号
     * @param col   表示检查的棋盘列号
     * @return true 表示该棋子需要移动   false   表示该棋子不需移动
     */
    private boolean resetChessBoard(Chess chess, int row, int col) {
        Chessboard currentChessBoard = ChessUtils.chessboard[row][col];
        Chess selectedChess = currentChessBoard.getChess();
        boolean needMove = true;


        if (chess.isAlive() && chess.getChessType() == Chessboard.INITCHESSBOARD[chess.getLogicPosition()[0]][chess.getLogicPosition()[1]]) {
            return true;
        }

        if (selectedChess != null) {
            if (selectedChess.getChessType() == chess.getChessType()) {
                needMove = false;
            } else {
                //把棋子移动到空位置去
                int[] emptyLogicPosition = ChessUtils.findEmptyChessboard();
                char[] emptyChessPosition = ChessUtils.logicPosition2ChessPosition(emptyLogicPosition);
                char[] selectedChessPosition = ChessUtils.logicPosition2ChessPosition(selectedChess.getLogicPosition());
                String from = String.valueOf(selectedChessPosition).toUpperCase();
                String to = String.valueOf(emptyChessPosition).toUpperCase();
                Log.e(TAG, "Clear: Move " + from + " to " + to);
                moveSystem.moveA2B(ProjectSetting.INTERNATIONAL_CHESS, from, ProjectSetting.INTERNATIONAL_CHESS, to);

                Chessboard emptyBoard = ChessUtils.chessboard[emptyLogicPosition[0]][emptyLogicPosition[1]];
                selectedChess.setAlive(true);
                selectedChess.setLogicPosition(emptyLogicPosition[0], emptyLogicPosition[1]);
                emptyBoard.setChess(selectedChess);
            }
        }
        if (needMove) {
            //将要移动到的棋盘格子的位置
            int[] currentLogicPosition = {row, col};
            char[] currentChessPosition = ChessUtils.logicPosition2ChessPosition(currentLogicPosition);

            //移动前被选中的棋子的位置
            int[] chessLogicPosition = chess.getLogicPosition();
            char[] chessChessPosition = ChessUtils.logicPosition2ChessPosition(chessLogicPosition);

            String from = String.valueOf(chessChessPosition).toUpperCase();
            String to = String.valueOf(currentChessPosition).toUpperCase();

            String fromBoard;
            int chessboardType;
            if (chess.isAlive()) {
                fromBoard = "chessboard";
                chessboardType = ProjectSetting.INTERNATIONAL_CHESS;
                ChessUtils.chessboard[chessLogicPosition[0]][chessLogicPosition[1]].setChess(null);
            } else {
                fromBoard = "chessboardBowl";
                chessboardType = ProjectSetting.INTERNATIONAL_CHESS_BOWL;
                ChessUtils.chessboardBowl[chessLogicPosition[0]][chessLogicPosition[1]].setChess(null);
            }

            Log.e(TAG, fromBoard + " Move " + from + " to " + to);
            moveSystem.moveA2B(chessboardType, from, ProjectSetting.INTERNATIONAL_CHESS, to);
            chess.setAlive(true);
            chess.setLogicPosition(row, col);
            currentChessBoard.setChess(chess);
        }
        return needMove;
    }

    public void clearStartingPoint() {
        //白子大写 黑子小写
        //战车R 马N 象B 后Q 王K 兵P
        for (int cnt = 0; cnt < 32; cnt++) {
            Chess chess = ChessUtils.chess[cnt];
            switch (chess.getChessType()) {
                case 'r':
                    if (!resetChessBoard(chess, 0, 0)) {
                        resetChessBoard(chess, 0, 7);
                    }
                    break;
                case 'n':
                    if (!resetChessBoard(chess, 0, 1)) {
                        resetChessBoard(chess, 0, 6);
                    }
                    break;
                case 'b':
                    if (!resetChessBoard(chess, 0, 2)) {
                        resetChessBoard(chess, 0, 5);
                    }
                    break;
                case 'q':
                    resetChessBoard(chess, 0, 3);
                    break;
                case 'k':
                    resetChessBoard(chess, 0, 4);
                    break;
                case 'p':
                    for (int col = 0; col < 8; col++) {
                        if (resetChessBoard(chess, 1, col)) {
                            break;
                        }
                    }
                    break;
                case 'R':
                    if (!resetChessBoard(chess, 7, 0)) {
                        resetChessBoard(chess, 7, 7);
                    }
                    break;
                case 'N':
                    if (!resetChessBoard(chess, 7, 1)) {
                        resetChessBoard(chess, 7, 6);
                    }
                    break;
                case 'B':
                    if (!resetChessBoard(chess, 7, 2)) {
                        resetChessBoard(chess, 7, 5);
                    }
                    break;
                case 'Q':
                    resetChessBoard(chess, 7, 3);
                    break;
                case 'K':
                    resetChessBoard(chess, 7, 4);
                    break;
                case 'P':
                    for (int col = 0; col < 8; col++) {
                        if (resetChessBoard(chess, 6, col)) {
                            break;
                        }
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown chess type! Please check the code");
                    break;
            }
        }
    }

    public Chessboard[][] saveCurrentChessboard() {
        return Chessboard.deepCloneArray(ChessUtils.chessboard);
    }

    public String detectChessStep() {
        String start = "";
        String end = "";
        String start2 = "";
        String end2 = "";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Chessboard old = ChessUtils.preChessboard[row][col];
                Chessboard current = ChessUtils.chessboard[row][col];
                int[] logicPosition = {row, col};
                if ((old.getChess() == null || current.getChess() == null) && !(old.getChess() == null && current.getChess() == null)) {
                    if (current.getChess() == null) {
                        if (Character.toUpperCase(old.getChess().getChessType()) == 'K') {
                            start = String.valueOf(ChessUtils.logicPosition2ChessPosition(logicPosition));
                        } else {
                            start2 = String.valueOf(ChessUtils.logicPosition2ChessPosition(logicPosition));
                        }
                    } else if (old.getChess() == null) {
                        if (Character.toUpperCase(current.getChess().getChessType()) == 'K') {
                            end = String.valueOf(ChessUtils.logicPosition2ChessPosition(logicPosition));
                        } else {
                            end2 = String.valueOf(ChessUtils.logicPosition2ChessPosition(logicPosition));
                        }
                    }
                } else if (old.getChess() != null && current.getChess() != null && old.getChess().getChessType() != current.getChess().getChessType()) {
                    end2 = String.valueOf(ChessUtils.logicPosition2ChessPosition(logicPosition));
                }
            }
        }
        return start + end + start2 + end2;
    }

    public void requireResetChess() {
        ChessStepRequest requestChessStep = new ChessStepRequest();
        requestChessStep.execute("123");
        Log.e(TAG, "Reset Chess");
    }

    public String requireRobotChessStep(String playerStep) {
        String robotStep = null;
        int code;
        ChessStepRequest requestChessStep = new ChessStepRequest();
        try {
            String ret = requestChessStep.execute(playerStep).get();
            Log.e(TAG, "Return JSON: " + ret);
            JSONObject jsonObject = new JSONObject(ret);
            robotStep = jsonObject.getString("message");
            code = jsonObject.getInt("code");

            if (code == 1) {
                char[] originChessPosition = {robotStep.charAt(0), robotStep.charAt(1)};
                char[] targetChessPosition = {robotStep.charAt(2), robotStep.charAt(3)};
                String from = String.valueOf(originChessPosition);
                String to = String.valueOf(targetChessPosition);
                int[] originLogicPosition = ChessUtils.chessPosition2LogicPosition(originChessPosition);
                if ((from + to).equals("e8g8") && ChessUtils.chessboard[originLogicPosition[0]][originLogicPosition[1]].getChess().getChessType() == 'k') {
                    robotStep += "h8f8";
                } else if ((from + to).equals("e8c8") && ChessUtils.chessboard[originLogicPosition[0]][originLogicPosition[1]].getChess().getChessType() == 'k') {
                    robotStep += "a8d8";
                }
            } else if(code == 2){
                robotStep = "GGWIN";
            } else if (code == 3) {
                robotStep = "GGLOSE";
            } else {
                robotStep = null;
            }

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return robotStep;
    }

    public void doRobotChessStep(String robotStep, String playerStep) {
        if (robotStep != null) {
            for (int i = 0; i < robotStep.length(); i += 4) {
                char[] originChessPosition = {robotStep.charAt(i), robotStep.charAt(i + 1)};
                char[] targetChessPosition = {robotStep.charAt(i + 2), robotStep.charAt(i + 3)};

                int[] targetLogicPosition = ChessUtils.chessPosition2LogicPosition(targetChessPosition);
                if (ChessUtils.chessboard[targetLogicPosition[0]][targetLogicPosition[1]].getChess() != null) {
                    int[] emptyChessboardBowlLogicPosition = ChessUtils.findEmptyChessboardBowl();
                    char[] emptyChessboardBowlChessPosition = ChessUtils.logicPosition2ChessPosition(emptyChessboardBowlLogicPosition);
                    String from = String.valueOf(targetChessPosition).toUpperCase();
                    String to = String.valueOf(emptyChessboardBowlChessPosition).toUpperCase();
                    Log.e(TAG, "Move " + from + " to chessboardBowl " + to);
                    moveSystem.moveA2B(ProjectSetting.INTERNATIONAL_CHESS, from, ProjectSetting.INTERNATIONAL_CHESS_BOWL, to);
                }

                String from = String.valueOf(originChessPosition).toUpperCase();
                String to = String.valueOf(targetChessPosition).toUpperCase();
                Log.e(TAG, "Move " + from + " to " + to);
                moveSystem.moveA2B(ProjectSetting.INTERNATIONAL_CHESS, from, ProjectSetting.INTERNATIONAL_CHESS, to);
            }
        } else {
            if (playerStep != null) {
                for (int i = 0; i < playerStep.length(); i += 4) {
                    char[] originChessPosition = {playerStep.charAt(i + 2), playerStep.charAt(i + 3)};
                    char[] targetChessPosition = {playerStep.charAt(i), playerStep.charAt(i + 1)};

                    String from = String.valueOf(originChessPosition).toUpperCase();
                    String to = String.valueOf(targetChessPosition).toUpperCase();
                    Log.e(TAG, "Recover: Move " + from + " to " + to);
                    moveSystem.moveA2B(ProjectSetting.INTERNATIONAL_CHESS, from, ProjectSetting.INTERNATIONAL_CHESS, to);

                    int[] originLogicPosition = ChessUtils.chessPosition2LogicPosition(originChessPosition);
                    Chess neededAliveChess = ChessUtils.preChessboard[originLogicPosition[0]][originLogicPosition[1]].getChess();
                    if (neededAliveChess != null) {
                        int[] neededAliveChessLogicPosition = ChessUtils.findSpecialChessFromChessboardBowl(neededAliveChess.getChessType());
                        char[] neededAliveChessChessPosition = ChessUtils.logicPosition2ChessPosition(neededAliveChessLogicPosition);
                        String fromChessBowl = String.valueOf(neededAliveChessChessPosition).toUpperCase();
                        Log.e(TAG, "Recover: Move chessboardBowl " + fromChessBowl + " to " + from);
                        moveSystem.moveA2B(ProjectSetting.INTERNATIONAL_CHESS_BOWL, fromChessBowl, ProjectSetting.INTERNATIONAL_CHESS, from);
                    }
                }
            }
        }
    }
}
