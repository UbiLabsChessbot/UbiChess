package org.ubilabs.ubichess.control;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.ubilabs.ubichess.modle.Chess;
import org.ubilabs.ubichess.modle.Chessboard;
import org.ubilabs.ubichess.uitl.ChessUtils;
import org.ubilabs.ubichess.uitl.ImgUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.ubilabs.ubichess.uitl.ChessUtils.CHESS_RADIUS;

public class ChessDetection {
    private static final String TAG = ChessDetection.class.getSimpleName();

    private Mat inputRgbaImg;
    private Mat tmpMat;
    private Mat tmpMat2;
    private Mat tmpMat3;
    private Mat tmpMat4;
    private MatOfPoint2f tmpMatOfPoint2f;
    private MatOfPoint tmpMatOfPoint;
    private MatOfPoint3f tmpMatOfPoint3f;
    private Mat zeroMat;
    private Mat zeroMatOfPoint2f;
    private Mat zeroMatOfPoint;
    private MatOfPoint3f zeroMatOfPoint3f;
    private Mat zeroGray;
    private Mat zeroChessMat;
    private Mat chessMaskMat;
    private Mat morphologyExElement;

    private static int CHESS_BOARD_S = 100;
    private static int CHESS_BOARD_V = 100;
    private static int CHESS_BOARD_BOWL_S = 100;
    private static int CHESS_BOARD_BOWL_V = 100;

    public ChessDetection(Size imgSize) {
        Size chessSize = new Size(CHESS_RADIUS * 2 * 8, CHESS_RADIUS * 2 * 4);
        chessMaskMat = new Mat(CHESS_RADIUS * 2, CHESS_RADIUS * 2, CvType.CV_8U, new Scalar(0));
        Core.circle(chessMaskMat, new Point(CHESS_RADIUS, CHESS_RADIUS), CHESS_RADIUS, new Scalar(255), -1);

        inputRgbaImg = new Mat();
        tmpMat = new Mat();
        tmpMat2 = new Mat();
        tmpMat3 = new Mat();
        tmpMat4 = new Mat();
        tmpMatOfPoint2f = new MatOfPoint2f();
        tmpMatOfPoint = new MatOfPoint();
        tmpMatOfPoint3f = new MatOfPoint3f();
        zeroMat = new Mat();
        zeroMatOfPoint2f = new MatOfPoint2f();
        zeroMatOfPoint = new MatOfPoint();
        zeroMatOfPoint3f = new MatOfPoint3f();
        zeroGray = new Mat(imgSize, CvType.CV_8U, new Scalar(0));
        zeroChessMat = new Mat(chessSize, CvType.CV_8UC4, new Scalar(0));
        morphologyExElement = Imgproc.getStructuringElement(MORPH_RECT, new Size(5, 5));
    }

    public boolean detectChessMen(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgbaImg = inputRgbaImg;
        inputFrame.rgba().copyTo(rgbaImg);

        Mat grayImg = tmpMat;
        inputFrame.gray().copyTo(grayImg);

        zeroMatOfPoint3f.copyTo(tmpMatOfPoint3f);
        MatOfPoint3f circleMat = tmpMatOfPoint3f;

        Imgproc.HoughCircles(grayImg, circleMat, Imgproc.CV_HOUGH_GRADIENT, 1, 100, 200, 20, 35, 45);
        List<Point3> circles = circleMat.toList();

        Mat chessImg = tmpMat2;
        zeroChessMat.copyTo(chessImg);

        ChessUtils.chess = new Chess[32];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessUtils.chessboard[row][col].setChess(null);
            }
        }
        if (circles.size() > 0) {
            for (int cnt = 0; cnt < circles.size() && cnt < 32; cnt++) {
                double x = circles.get(cnt).x;
                double y = circles.get(cnt).y;
                double z = circles.get(cnt).z;

//                Log.e(TAG,"Circle R: " + z);

                int chessImageRow = cnt / 8;
                int chessImageCol = cnt % 8;

                Mat subMask = new Mat(chessImg, new Rect(chessImageCol * CHESS_RADIUS * 2, chessImageRow * CHESS_RADIUS * 2, CHESS_RADIUS * 2, CHESS_RADIUS * 2));
                Mat subChess = new Mat(rgbaImg, new Rect(Math.min(1920 - CHESS_RADIUS * 2, Math.max(0, (int) x - CHESS_RADIUS)), Math.min(1080 - CHESS_RADIUS * 2, Math.max(0, (int) y - CHESS_RADIUS)), CHESS_RADIUS * 2, CHESS_RADIUS * 2));
                subChess.copyTo(subMask, chessMaskMat);

                Chess chess = new Chess();
                double chessboardLengthX = ChessUtils.chessboardKeyPoints[1].x - ChessUtils.chessboardKeyPoints[0].x;
                double chessboardLengthY = ChessUtils.chessboardKeyPoints[3].y - ChessUtils.chessboardKeyPoints[0].y;
                int chessboardRow = (int) ((y - ChessUtils.chessboardKeyPoints[0].y) * 1.0 / chessboardLengthY * 8);
                int chessboardCol = (int) ((x - ChessUtils.chessboardKeyPoints[0].x) * 1.0 / chessboardLengthX * 8);

                double chessboardBowlLengthX = ChessUtils.chessboardBowlKeyPoints[1].x - ChessUtils.chessboardBowlKeyPoints[0].x;
                double chessboardBowlLengthY = ChessUtils.chessboardBowlKeyPoints[3].y - ChessUtils.chessboardBowlKeyPoints[0].y;
                int chessboardBowlRow = (int) ((y - ChessUtils.chessboardBowlKeyPoints[0].y) * 1.0 / chessboardBowlLengthY * 8);
                int chessboardBowlCol = (int) ((x - ChessUtils.chessboardBowlKeyPoints[0].x) * 1.0 / chessboardBowlLengthX * 4);

                if (chessboardRow >= 0 && chessboardRow < 8 && chessboardCol >= 0 && chessboardCol < 8) {
                    chess.setAlive(true);
                    chess.setLogicPosition(chessboardRow, chessboardCol);
                    ChessUtils.chessboard[chessboardRow][chessboardCol].setChess(chess);
                } else if (chessboardBowlRow >= 0 && chessboardBowlRow < 8 && chessboardBowlCol >= 0 && chessboardBowlCol < 4) {
                    chess.setAlive(false);
                    chess.setLogicPosition(chessboardBowlRow, chessboardBowlCol);
                    ChessUtils.chessboardBowl[chessboardBowlRow][chessboardBowlCol].setChess(chess);
                } else {
                    chess.setAlive(false);
                    chess.setAbsolutePosition(x, y);
                }
                ChessUtils.chess[cnt] = chess;
            }
            File imgFile = ImgUtils.mat2PngFile(chessImg);
            ChessTypeRequest requestChessType = new ChessTypeRequest();
            try {
                String ret = requestChessType.execute(imgFile).get();
                Log.e(TAG, "Return JSON: " + ret);
                JSONObject jsonObject = new JSONObject(ret);
                String chessType = jsonObject.getString("message");
                for (int i = 0; i < chessType.length(); i++) {
                    ChessUtils.chess[i].setChessType(chessType.charAt(i));
                }
                ChessUtils.printChessState(ChessUtils.chessboard);
                return ChessUtils.checkChessType(chessType, imgFile);
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void detectChessBoard(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgbaImg = inputRgbaImg;
        inputFrame.rgba().copyTo(rgbaImg);

        Mat rgbImg = tmpMat;
        zeroMat.copyTo(rgbImg);
        Imgproc.cvtColor(rgbaImg, rgbImg, Imgproc.COLOR_RGBA2RGB);
        Mat hsvImg = tmpMat;
        Imgproc.cvtColor(rgbImg, hsvImg, Imgproc.COLOR_RGB2HSV);

        Mat hsvSplit1 = tmpMat2;
        Mat hsvSplit2 = tmpMat3;
        Mat hsvSplit3 = tmpMat4;
        zeroMat.copyTo(hsvSplit1);
        zeroMat.copyTo(hsvSplit2);
        zeroMat.copyTo(hsvSplit3);
        List<Mat> hsvSplits = new ArrayList<>(Arrays.asList(hsvSplit1, hsvSplit2, hsvSplit3));

        Core.split(hsvImg, hsvSplits);
        Imgproc.equalizeHist(hsvSplits.get(2), hsvSplits.get(2));
        Core.merge(hsvSplits, hsvImg);

        Mat labelImg = tmpMat;
        Core.inRange(hsvImg, new Scalar(100, CHESS_BOARD_S, CHESS_BOARD_V), new Scalar(124, 255, 255), labelImg);
        Imgproc.morphologyEx(labelImg, labelImg, MORPH_OPEN, morphologyExElement);
        Imgproc.morphologyEx(labelImg, labelImg, MORPH_CLOSE, morphologyExElement);

        List<MatOfPoint> labelContours = new ArrayList<>();
        Mat hierarchy = tmpMat2;
        Imgproc.findContours(labelImg, labelContours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        ChessUtils.chessboard = new Chessboard[8][8];
        ChessUtils.chessboardKeyPoints = new Point[4];
        if (labelContours.size() != 0) {
            List<Point> labelPoints = new ArrayList<>();
            for (MatOfPoint aMatOfPoint : labelContours) {
                labelPoints.addAll(aMatOfPoint.toList());
                aMatOfPoint.release();
            }

            MatOfPoint2f labelMat = tmpMatOfPoint2f;
            zeroMatOfPoint2f.copyTo(labelMat);
            labelMat.fromList(labelPoints);
            RotatedRect chessboardImg = Imgproc.minAreaRect(labelMat);

            //get the chessKeyPoints
            Point[] chessKeyPoints = new Point[4];
            chessboardImg.points(chessKeyPoints);

            //find the left && top point
            int tl = 0;
            double minLength = chessKeyPoints[0].x * chessKeyPoints[0].x + chessKeyPoints[0].y * chessKeyPoints[0].y;
            for (int i = 1; i < 4; i++) {
                double currentLength = chessKeyPoints[i].x * chessKeyPoints[i].x + chessKeyPoints[i].y * chessKeyPoints[i].y;
                if (currentLength < minLength) {
                    minLength = currentLength;
                    tl = i;
                }
            }

            //update the chessKeyPoints
            for (int i = tl, cnt = 0; cnt < 4; cnt++, i = (i + 1) % 4) {
                ChessUtils.chessboardKeyPoints[cnt] = chessKeyPoints[i];
            }
            chessKeyPoints = ChessUtils.chessboardKeyPoints;

            List<List<Point>> chessPoints = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                List<Point> aRow = new ArrayList<>();
                double startX = chessKeyPoints[0].x + (chessKeyPoints[3].x - chessKeyPoints[0].x) / 8 * i;
                double startY = chessKeyPoints[0].y + (chessKeyPoints[3].y - chessKeyPoints[0].y) / 8 * i;
                double endX = chessKeyPoints[1].x + (chessKeyPoints[2].x - chessKeyPoints[1].x) / 8 * i;
                double endY = chessKeyPoints[1].y + (chessKeyPoints[2].y - chessKeyPoints[1].y) / 8 * i;
                for (int j = 0; j < 9; j++) {
                    aRow.add(new Point(startX + (endX - startX) / 8 * j, startY + (endY - startY) / 8 * j));
                }
                chessPoints.add(aRow);
            }

            for (int row = 0; row < chessPoints.size() - 1; row++) {
                List<Point> thisRow = chessPoints.get(row);
                List<Point> nextRow = chessPoints.get(row + 1);
                double y = (thisRow.get(0).y + nextRow.get(0).y) / 2;
                for (int col = 0; col < thisRow.size() - 1; col++) {
                    Chessboard chessboard = new Chessboard();
                    chessboard.x = (thisRow.get(col).x + thisRow.get(col + 1).x) / 2;
                    chessboard.y = y;
                    ChessUtils.chessboard[row][col] = chessboard;
                }
            }
        }
    }

    public void detectChessBoardBowl(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgbaImg = inputRgbaImg;
        inputFrame.rgba().copyTo(rgbaImg);

        Mat rgbImg = tmpMat;
        zeroMat.copyTo(rgbImg);
        Imgproc.cvtColor(rgbaImg, rgbImg, Imgproc.COLOR_RGBA2RGB);
        Mat hsvImg = tmpMat;
        Imgproc.cvtColor(rgbImg, hsvImg, Imgproc.COLOR_RGB2HSV);

        Mat hsvSplit1 = tmpMat2;
        Mat hsvSplit2 = tmpMat3;
        Mat hsvSplit3 = tmpMat4;
        zeroMat.copyTo(hsvSplit1);
        zeroMat.copyTo(hsvSplit2);
        zeroMat.copyTo(hsvSplit3);
        List<Mat> hsvSplits = new ArrayList<>(Arrays.asList(hsvSplit1, hsvSplit2, hsvSplit3));

        Core.split(hsvImg, hsvSplits);
        Imgproc.equalizeHist(hsvSplits.get(2), hsvSplits.get(2));
        Core.merge(hsvSplits, hsvImg);

        Mat labelImg = tmpMat;
        Core.inRange(hsvImg, new Scalar(156, CHESS_BOARD_BOWL_S, CHESS_BOARD_BOWL_V), new Scalar(180, 255, 255), labelImg);
        Imgproc.morphologyEx(labelImg, labelImg, MORPH_OPEN, morphologyExElement);
        Imgproc.morphologyEx(labelImg, labelImg, MORPH_CLOSE, morphologyExElement);

        List<MatOfPoint> labelContours = new ArrayList<>();
        Mat hierarchy = tmpMat2;
        Imgproc.findContours(labelImg, labelContours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(rgbaImg, labelContours, -1, new Scalar(0, 255, 0));

        ChessUtils.chessboardBowl = new Chessboard[8][4];
        ChessUtils.chessboardBowlKeyPoints = new Point[4];
        if (labelContours.size() != 0) {
            zeroMatOfPoint2f.copyTo(tmpMatOfPoint2f);
            MatOfPoint2f labelMat = tmpMatOfPoint2f;

            List<Point> labelPoints = new ArrayList<>();
            for (MatOfPoint aMatOfPoint : labelContours) {
                labelPoints.addAll(aMatOfPoint.toList());
                aMatOfPoint.release();
            }
            labelMat.fromList(labelPoints);
            RotatedRect chessboardImg = Imgproc.minAreaRect(labelMat);

            //get the chessKeyPoints
            Point[] chessboardBowlKeyPoints = new Point[4];
            chessboardImg.points(chessboardBowlKeyPoints);

            //find the left && top point
            int tl = 0;
            double minLength = chessboardBowlKeyPoints[0].x * chessboardBowlKeyPoints[0].x + chessboardBowlKeyPoints[0].y * chessboardBowlKeyPoints[0].y;
            for (int i = 1; i < 4; i++) {
                double currentLength = chessboardBowlKeyPoints[i].x * chessboardBowlKeyPoints[i].x + chessboardBowlKeyPoints[i].y * chessboardBowlKeyPoints[i].y;
                if (currentLength < minLength) {
                    minLength = currentLength;
                    tl = i;
                }
            }

            //update the chessboardBowlKeyPoints
            for (int i = tl, cnt = 0; cnt < 4; cnt++, i = (i + 1) % 4) {
                ChessUtils.chessboardBowlKeyPoints[cnt] = chessboardBowlKeyPoints[i];
            }
            chessboardBowlKeyPoints = ChessUtils.chessboardBowlKeyPoints;

            List<List<Point>> chessPoints = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                List<Point> aRow = new ArrayList<>();
                double startX = chessboardBowlKeyPoints[0].x + (chessboardBowlKeyPoints[3].x - chessboardBowlKeyPoints[0].x) / 4 * i;
                double startY = chessboardBowlKeyPoints[0].y + (chessboardBowlKeyPoints[3].y - chessboardBowlKeyPoints[0].y) / 8 * i;
                double endX = chessboardBowlKeyPoints[1].x + (chessboardBowlKeyPoints[2].x - chessboardBowlKeyPoints[1].x) / 4 * i;
                double endY = chessboardBowlKeyPoints[1].y + (chessboardBowlKeyPoints[2].y - chessboardBowlKeyPoints[1].y) / 8 * i;
                for (int j = 0; j < 5; j++) {
                    aRow.add(new Point(startX + (endX - startX) / 4 * j, startY + (endY - startY) / 8 * j));
                }
                chessPoints.add(aRow);
            }

            for (int row = 0; row < chessPoints.size() - 1; row++) {
                List<Point> thisRow = chessPoints.get(row);
                List<Point> nextRow = chessPoints.get(row + 1);
                double y = (thisRow.get(0).y + nextRow.get(0).y) / 2;
                for (int col = 0; col < thisRow.size() - 1; col++) {
                    Chessboard chessboardBowl = new Chessboard();
                    chessboardBowl.x = (thisRow.get(col).x + thisRow.get(col + 1).x) / 2;
                    chessboardBowl.y = y;
                    ChessUtils.chessboardBowl[row][col] = chessboardBowl;
                }
            }
        }
    }

    /* Lab*/
    public Mat lab(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        return inputFrame.rgba();

        Mat rgbaImg = inputRgbaImg;
        inputFrame.rgba().copyTo(rgbaImg);

        zeroMat.copyTo(tmpMat);
        Mat rgbImg = tmpMat;
        Imgproc.cvtColor(rgbaImg, rgbImg, Imgproc.COLOR_RGBA2RGB);
        Mat hsvImg = tmpMat;
        Imgproc.cvtColor(rgbImg, hsvImg, Imgproc.COLOR_RGB2HSV);

        zeroMat.copyTo(tmpMat2);
        zeroMat.copyTo(tmpMat3);
        zeroMat.copyTo(tmpMat4);
        Mat hsvSplit1 = tmpMat2;
        Mat hsvSplit2 = tmpMat3;
        Mat hsvSplit3 = tmpMat4;
        List<Mat> hsvSplits = new ArrayList<>(Arrays.asList(hsvSplit1, hsvSplit2, hsvSplit3));

        Core.split(hsvImg, hsvSplits);
        Imgproc.equalizeHist(hsvSplits.get(2), hsvSplits.get(2));
        Core.merge(hsvSplits, hsvImg);

        Mat labelImg = tmpMat;
        Core.inRange(hsvImg, new Scalar(35, 35, 35), new Scalar(99, 255, 255), labelImg);
//        Core.inRange(hsvImg, new Scalar(100, CHESS_BOARD_S, CHESS_BOARD_V), new Scalar(124, 255, 255), labelImg);
//        Core.inRange(hsvImg, new Scalar(156, CHESS_BOARD_BOWL_S, CHESS_BOARD_BOWL_V), new Scalar(180, 255, 255), labelImg);
        Imgproc.morphologyEx(labelImg, labelImg, MORPH_OPEN, morphologyExElement);
        Imgproc.morphologyEx(labelImg, labelImg, MORPH_CLOSE, morphologyExElement);
        return labelImg;


//        Mat rgbaImg = inputRgbaImg;
//        inputFrame.rgba().copyTo(rgbaImg);
//
//        Mat grayImg = tmpMat;
//        inputFrame.gray().copyTo(grayImg);
//
//        zeroMatOfPoint3f.copyTo(tmpMatOfPoint3f);
//        MatOfPoint3f circleMat = tmpMatOfPoint3f;
//
//        Imgproc.HoughCircles(grayImg, circleMat, Imgproc.CV_HOUGH_GRADIENT, 1, 100, 200, 20, 35, 50);
//        List<Point3> circles = circleMat.toList();
//
//        Mat chessImg = tmpMat2;
//        zeroChessMat.copyTo(chessImg);
//
//        ChessUtils.chess = new Chess[32];
//        if (circles.size() > 0) {
//            for (int cnt = 0; cnt < circles.size() && cnt < 32; cnt++) {
//                double x = circles.get(cnt).x;
//                double y = circles.get(cnt).y;
//                double z = circles.get(cnt).z;
//
//                Core.circle(rgbaImg, new Point(x, y), (int) z, new Scalar(255), 5);
//                Log.e(TAG,"z: " + z);
//            }
//        }
//        return rgbaImg;
    }
}
