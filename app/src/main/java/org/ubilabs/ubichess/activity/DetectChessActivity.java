package org.ubilabs.ubichess.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
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
import org.ubilabs.ubichess.R;
import org.ubilabs.ubichess.control.RequestChessStep;
import org.ubilabs.ubichess.control.RequestChessType;
import org.ubilabs.ubichess.modle.Chess;
import org.ubilabs.ubichess.modle.Chessboard;
import org.ubilabs.ubichess.uitl.ChessUtils;
import org.ubilabs.ubichess.uitl.PermissionUtils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;

public class DetectChessActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = DetectChessActivity.class.getSimpleName();
    private static final int ChessRadius = 45;

    private CameraBridgeViewBase openCvCameraView;
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
    private Mat zeroChessesMat;
    private Mat chessMaskMat;
    private Mat morphologyExElement;

    //    private SeekBar hEdit;
//    private SeekBar lEdit;
    private int generateCnt = 0;

    private Button initButton;
    private int initCnt = 0;
    private Button resetChessButton;
    private int resetChessCnt = 0;
    private Button playChessButton;
    private int playChessCnt = 0;
    private int processType;
    private boolean doSignal;

    private Chessboard oldChessboard[][];
    private String playerStep;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Opencv not loaded");
        } else {
            Log.d(TAG, "Opencv loaded");
        }
    }

    /*
     *   Activity Callbacks
     *
     *   */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

//        hEdit = (SeekBar) findViewById(R.id.hEdit);
//        hEdit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                TextView hDisplay = (TextView) findViewById(R.id.hDisplay);
//                hDisplay.setText(String.valueOf(i));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        hEdit.setProgress(110);
//
//        lEdit = (SeekBar) findViewById(R.id.lEdit);
//        lEdit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                TextView lDisplay = (TextView) findViewById(R.id.lDisplay);
//                lDisplay.setText(String.valueOf(i));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        lEdit.setProgress(46);

        final List<String> initProcess = new ArrayList<>();
        initProcess.addAll(Arrays.asList("未选择", "识别棋盘"));

        final List<String> resetChessProcess = new ArrayList<>();
        resetChessProcess.addAll(Arrays.asList("未选择", "识别棋子", "码棋"));

        final List<String> playChessProcess = new ArrayList<>();
        playChessProcess.addAll(Arrays.asList("未选择", "识别棋子", "等待棋手", "识别棋子", "识别玩家走法", "给出机械臂走法"));

        initButton = (Button) findViewById(R.id.initButton);
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processType = 0;
                initCnt = (initCnt + 1) % initProcess.size();
                initButton.setText(initProcess.get(initCnt));
                doSignal = true;
            }
        });
        initButton.setText(initProcess.get(0));

        resetChessButton = (Button) findViewById(R.id.resetChessButton);
        resetChessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processType = 1;
                resetChessCnt = (resetChessCnt + 1) % resetChessProcess.size();
                resetChessButton.setText(resetChessProcess.get(resetChessCnt));
                doSignal = true;
            }
        });
        resetChessButton.setText(resetChessProcess.get(0));

        playChessButton = (Button) findViewById(R.id.playChessButton);
        playChessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processType = 2;
                playChessCnt = (playChessCnt + 1) % playChessProcess.size();
                playChessButton.setText(playChessProcess.get(playChessCnt));
                doSignal = true;
            }
        });
        playChessButton.setText(playChessProcess.get(0));

        processType = -1;
        doSignal = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            initCamera();
        }
    }

    private void initCamera() {
        openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        openCvCameraView.setCvCameraViewListener(this);
        openCvCameraView.setMaxFrameSize(1920, 1080);
        openCvCameraView.enableFpsMeter();
        openCvCameraView.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (openCvCameraView != null) {
            openCvCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openCvCameraView != null) {
            openCvCameraView.disableView();
        }
    }


    /*
     *   Opencv Callbacks
     *
     *   */
    @Override
    public void onCameraViewStarted(int width, int height) {
        Size imgSize = new Size(width, height);
        Size chessesSize = new Size(ChessRadius * 2 * 8, ChessRadius * 2 * 4);
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
        zeroChessesMat = new Mat(chessesSize, CvType.CV_8UC4, new Scalar(0));
        morphologyExElement = Imgproc.getStructuringElement(MORPH_RECT, new Size(5, 5));

        chessMaskMat = new Mat(ChessRadius * 2, ChessRadius * 2, CvType.CV_8U, new Scalar(0));
        Core.circle(chessMaskMat, new Point(ChessRadius, ChessRadius), ChessRadius, new Scalar(255), -1);
    }

    @Override
    public void onCameraViewStopped() {
    }

    private File mat2PngFile(Mat mat) {
        Bitmap bmp = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        Log.e(TAG, "Transform: finish! " + generateCnt);

        File file = null;
        FileOutputStream fos;
        try {
            file = new File(Environment.getExternalStorageDirectory() + "/Chess/Chess" + ".png");
//            file = new File(Environment.getExternalStorageDirectory() + "/Chess/Chess" + generateCnt + ".png");
//            if (generateCnt < 30) {
//                generateCnt++;
//            } else {
//                generateCnt = 0;
//            }
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        /* Lab Test
        Mat ret = inputFrame.rgba();
        if (doSignal) {
            switch (initCnt) {
                case 0:
                    ret = inputFrame.rgba();
                    break;
                case 1:
                    ret = lab(inputFrame);
                    break;
                default:
                    break;
            }
        }
*/

        /* Process*/
        Mat ret = inputFrame.rgba();
        if (doSignal) {
            switch (processType) {
                case 0://初始化
                    switch (initCnt) {
                        case 0://变量初始化
                            ChessUtils.chesses = new Chess[32];
                            ChessUtils.chessboard = new Chessboard[8][8];
                            ChessUtils.chessKeypoints = new Point[4];
                            doSignal = false;
                            break;
                        case 1://识别棋盘
                            detectChessBoard(inputFrame);
                            if (ChessUtils.chessboard[7][7] != null && ChessUtils.chessKeypoints[3] != null) {
                                doSignal = false;
                            }
                            break;
                        default:
                            doSignal = false;
                            break;
                    }
                    break;
                case 1://码棋
                    switch (resetChessCnt) {
                        case 0:
                            doSignal = false;
                            break;
                        case 1:
                            if (detectChessMen(inputFrame)) {
                                doSignal = false;
                            }
                            break;
                        case 2://码棋
                            clearStartingPoint();
                            //第二遍把中间的棋子归位
                            clearStartingPoint();
                            doSignal = false;
                            break;
                        default:
                            doSignal = false;
                            break;
                    }
                    break;
                case 2://下棋
                    switch (playChessCnt) {
                        case 0:
                            doSignal = false;
                            break;
                        case 1:
                            if (detectChessMen(inputFrame)) {
                                doSignal = false;
                            }
                            break;
                        case 2://保存旧棋盘状态
                            oldChessboard = saveCurrentChessBoard();
                            doSignal = false;
                            break;
                        case 3://识别棋子
                            if (detectChessMen(inputFrame)) {
                                doSignal = false;
                            }
                            break;
                        case 4://识别玩家走法
                            playerStep = detectChessStep(oldChessboard);
                            Log.e(TAG, "Player Step: " + playerStep);
                            doSignal = false;
                            break;
                        case 5://给出机械臂走法
                            String robotStep = requireChessStep(playerStep);
                            Log.e(TAG, "Robot Step: " + robotStep);
                            doSignal = false;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }


        /*Debug*/
        if (ChessUtils.chessboard[7][7] != null) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Point point = new Point(ChessUtils.chessboard[row][col].x, ChessUtils.chessboard[row][col].y);
                    Core.line(ret, point, point, new Scalar(255), 10);
                }
            }
        }
        for (int cnt = 0; cnt < 32 && ChessUtils.chesses[cnt] != null; cnt++) {
            double[] position = ChessUtils.chesses[cnt].getAbsolutePosition();
            Point point = new Point(position[0], position[1]);
            Core.line(ret, point, point, new Scalar(0, 255, 0), 10);
        }

        return ret;
    }

    private void printChessState(Chessboard[][] chessboard) {
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

    private boolean autoCheckChessType(String message, File imgFile) {
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
            boolean isSuccess = imgFile.renameTo(new File(Environment.getExternalStorageDirectory() + "/Chess/" + from + "2" + to + "_" + dateString + ".png"));
            Log.e(TAG, "Wrong img Rename: " + isSuccess + " about: " + from + "2" + to);
            return false;
        }
        return true;
    }

    private boolean detectChessMen(CvCameraViewFrame inputFrame) {
        Mat rgbaImg = inputRgbaImg;
        inputFrame.rgba().copyTo(rgbaImg);

        Mat grayImg = tmpMat;
        inputFrame.gray().copyTo(grayImg);

        zeroMatOfPoint3f.copyTo(tmpMatOfPoint3f);
        MatOfPoint3f circleMat = tmpMatOfPoint3f;

        Imgproc.HoughCircles(grayImg, circleMat, Imgproc.CV_HOUGH_GRADIENT, 1, 100, 200, 20, 40, 50);
        List<Point3> circles = circleMat.toList();

        Log.d(TAG, "Circle Numbers: " + circles.size());

        Mat chessImg = tmpMat2;
        zeroChessesMat.copyTo(chessImg);

        ChessUtils.chesses = new Chess[32];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessUtils.chessboard[row][col].setChess(null);
            }
        }
        if (circles.size() > 0) {
            for (int cnt = 0; cnt < circles.size() && cnt < 32; cnt++) {
                double x = circles.get(cnt).x;
                double y = circles.get(cnt).y;
                int chessImageRow = cnt / 8;
                int chessImageCol = cnt % 8;

                Mat subMask = new Mat(chessImg, new Rect(chessImageCol * ChessRadius * 2, chessImageRow * ChessRadius * 2, ChessRadius * 2, ChessRadius * 2));
                Mat subChess = new Mat(rgbaImg, new Rect(Math.min(1920 - ChessRadius * 2, Math.max(0, (int) x - ChessRadius)), Math.min(1080 - ChessRadius * 2, Math.max(0, (int) y - ChessRadius)), ChessRadius * 2, ChessRadius * 2));
                subChess.copyTo(subMask, chessMaskMat);

                Chess chess = new Chess();
                double lengthX = ChessUtils.chessKeypoints[1].x - ChessUtils.chessKeypoints[0].x;
                double lengthY = ChessUtils.chessKeypoints[3].y - ChessUtils.chessKeypoints[0].y;
                int row = (int) ((y - ChessUtils.chessKeypoints[0].y) * 1.0 / lengthY * 8);
                int col = (int) ((x - ChessUtils.chessKeypoints[0].x) * 1.0 / lengthX * 8);
                if (row >= 0 && row < 8 && col >= 0 && col < 8 && ChessUtils.chessboard[7][7] != null) {
                    chess.setAlive(true);
                    chess.setLogicPosition(row, col);
                    ChessUtils.chessboard[row][col].setChess(chess);
                } else {
                    chess.setAlive(false);
                    chess.setAbsolutePosition(x, y);
                }
                ChessUtils.chesses[cnt] = chess;
            }
            File imgFile = mat2PngFile(chessImg);
            RequestChessType requestChessType = new RequestChessType();
            try {
                String ret = requestChessType.execute(imgFile).get();
                Log.e(TAG, "Return JSON: " + ret);
                JSONObject jsonObject = new JSONObject(ret);
                String chessType = jsonObject.getString("message");
                for (int i = 0; i < chessType.length(); i++) {
                    ChessUtils.chesses[i].setChessType(chessType.charAt(i));
                }
                printChessState(ChessUtils.chessboard);
                return autoCheckChessType(chessType, imgFile);
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void detectChessBoard(CvCameraViewFrame inputFrame) {
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
        Core.inRange(hsvImg, new Scalar(100, 120, 80), new Scalar(124, 255, 255), labelImg);
        Imgproc.morphologyEx(labelImg, labelImg, MORPH_OPEN, morphologyExElement);
        Imgproc.morphologyEx(labelImg, labelImg, MORPH_CLOSE, morphologyExElement);

        List<MatOfPoint> labelContours = new ArrayList<>();
        Mat hierarchy = tmpMat2;
        Imgproc.findContours(labelImg, labelContours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(rgbaImg, labelContours, -1, new Scalar(0, 255, 0));

        ChessUtils.chessboard = new Chessboard[8][8];
        ChessUtils.chessKeypoints = new Point[4];
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
                ChessUtils.chessKeypoints[cnt] = chessKeyPoints[i];
            }
            chessKeyPoints = ChessUtils.chessKeypoints;

            zeroMatOfPoint.copyTo(tmpMatOfPoint);
            MatOfPoint polyMat = tmpMatOfPoint;

            polyMat.fromArray(chessKeyPoints);
            List<MatOfPoint> polyMatList = new ArrayList<>();
            polyMatList.add(polyMat);

            zeroGray.copyTo(tmpMat2);
            Mat mask = tmpMat2;

            Core.fillPoly(mask, polyMatList, new Scalar(255));
            for (MatOfPoint aMatOfPoint : polyMatList) {
                aMatOfPoint.release();
            }

            rgbaImg.copyTo(tmpMat2, mask);
            tmpMat2.copyTo(rgbaImg);

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

    private int[] selectEmptyPlace() {
        int[] position = new int[2];
        for (int row = 2; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                if (ChessUtils.chessboard[row][col].getChess() == null) {
                    position[0] = row;
                    position[1] = col;
                    return position;
                }
            }
        }
        return position;
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
                //TODO 把原来的棋子移走

                int[] emptyLogicPosition = selectEmptyPlace();
                char[] emptyChessPosition = ChessUtils.logicPosition2ChessPosition(emptyLogicPosition);
                char[] selectedLogicPosition = ChessUtils.logicPosition2ChessPosition(selectedChess.getLogicPosition());
                Log.e(TAG, "Clear: Move " + Arrays.toString(selectedLogicPosition) + " to " + Arrays.toString(emptyChessPosition));

                Chessboard emptyBoard = ChessUtils.chessboard[emptyLogicPosition[0]][emptyLogicPosition[1]];
                selectedChess.setAlive(true);
                selectedChess.setLogicPosition(emptyLogicPosition[0], emptyLogicPosition[1]);
                emptyBoard.setChess(selectedChess);
            }
        }
        if (needMove) {
            //TODO 把棋子移过去

            //将要移动到的棋盘格子的位置
            int[] currentLogicPosition = {row, col};
            char[] currentChessPosition = ChessUtils.logicPosition2ChessPosition(currentLogicPosition);

            //移动前被选中的棋子的位置
            int[] chessLogicPosition = chess.getLogicPosition();
            char[] chessChessPosition = ChessUtils.logicPosition2ChessPosition(chessLogicPosition);

            Log.e(TAG, "Move " + Arrays.toString(chessChessPosition) + " to " + Arrays.toString(currentChessPosition));

            ChessUtils.chessboard[chessLogicPosition[0]][chessLogicPosition[1]].setChess(null);
            chess.setAlive(true);
            chess.setLogicPosition(row, col);
            currentChessBoard.setChess(chess);
        }
        return needMove;
    }

    private void clearStartingPoint() {
        //白子大写 黑子小写
        //战车R 马N 象B 后Q 王K 兵P
        for (int cnt = 0; cnt < 32; cnt++) {
            Chess chess = ChessUtils.chesses[cnt];
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

    private Chessboard[][] saveCurrentChessBoard() {
        Chessboard[][] chessboard = Chessboard.deepCloneArray(ChessUtils.chessboard);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (chessboard[row][col].getChess() != null) {
                    chessboard[row][col].setChess(ChessUtils.chessboard[row][col].getChess().deepClone());
                }
            }
        }

        return chessboard;
    }

    private String detectChessStep(Chessboard[][] oldChessboard) {
        String start = "";
        String end = "";
        String start2 = "";
        String end2 = "";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Chessboard old = oldChessboard[row][col];
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
                    end = String.valueOf(ChessUtils.logicPosition2ChessPosition(logicPosition));
                }
            }
        }
        return start + end + start2 + end2;
    }

    private String requireChessStep(String playerStep) {
        String robotStep = "null";
        RequestChessStep requestChessStep = new RequestChessStep();
        try {
            String ret = requestChessStep.execute(playerStep).get();
            Log.e(TAG, "Return JSON: " + ret);
            JSONObject jsonObject = new JSONObject(ret);
            robotStep = jsonObject.getString("message");
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return robotStep;
    }

    /* Lab*/
    private Mat lab(CvCameraViewFrame inputFrame) {
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
        Core.inRange(hsvImg, new Scalar(35, 110, 50), new Scalar(99, 255, 255), labelImg);
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
//        Imgproc.HoughCircles(grayImg, circleMat, Imgproc.CV_HOUGH_GRADIENT, 1, 100, 200, 20, 40, 50);
//        List<Point3> circles = circleMat.toList();
//
//        Log.d(TAG, "Circle Numbers: " + circles.size());
//
//        Mat chessImg = tmpMat2;
//        zeroChessesMat.copyTo(chessImg);
//
//        ChessUtils.chesses = new Chess[32];
//        if (circles.size() > 0) {
//            for (int cnt = 0; cnt < circles.size() && cnt < 32; cnt++) {
//                double x = circles.get(cnt).x;
//                double y = circles.get(cnt).y;
//                double z = circles.get(cnt).z;
//
//                Core.circle(rgbaImg, new Point(x, y), (int) z, new Scalar(255), 5);
//            }
//        }
//        return rgbaImg;

//        detectChessBoard(inputFrame);
//        detectChessMen(inputFrame);
    }


    private void requestPermission() {
        PermissionUtils.requestMultiPermissions(this, mPermissionGrant);
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA:
                    Toast.makeText(DetectChessActivity.this, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    Toast.makeText(DetectChessActivity.this, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    Toast.makeText(DetectChessActivity.this, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(DetectChessActivity.this, "Result Permission Grant CODE_MULTI_PERMISSION", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
        initCamera();
    }
}
