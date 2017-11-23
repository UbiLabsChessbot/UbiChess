package org.ubilabs.ubichess.activity;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.ubilabs.ubichess.R;
import org.ubilabs.ubichess.control.ChessLogic;
import org.ubilabs.ubichess.control.ChessDetection;
import org.ubilabs.ubichess.control.VoiceHint;
import org.ubilabs.ubichess.modle.Chess;
import org.ubilabs.ubichess.modle.Chessboard;
import org.ubilabs.ubichess.uitl.ChessUtils;

import me.michaeljiang.movesystemlibs.movesystem.setting.ProcessControlSetting;
import me.michaeljiang.movesystemlibs.movesystem.setting.ChessStepSetting;

import org.ubilabs.ubichess.uitl.PermissionUtils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import me.michaeljiang.movesystemlibs.movesystem.MoveSystem;


public class DetectChessActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = DetectChessActivity.class.getSimpleName();

    private JavaCameraView openCvCameraView;
    private ChessDetection chessDetection;
    private ChessLogic chessLogic;
    private VoiceHint voiceHint;

    //    private SeekBar hEdit;
//    private SeekBar lEdit;
//    private int generateCnt = 0;

    private List<Object> processControl;

    private MoveSystem moveSystem;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            initCamera();
        }

        Context context = this;
        voiceHint = new VoiceHint(context);
        voiceHint.playVoice(R.raw.systemstarting);

        processControl = new ArrayList<>();
        processControl.add(ProcessControlSetting.PROCESS_TYPE, -1);
        processControl.add(ProcessControlSetting.LAB_STEP, 0);
        processControl.add(ProcessControlSetting.PLAY_STEP, -1);
        processControl.add(ProcessControlSetting.DO_SIGNAL, false);

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

        Button labButton = (Button) findViewById(R.id.labButton);
        labButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processControl.set(ProcessControlSetting.LAB_STEP, ((int) processControl.get(ProcessControlSetting.LAB_STEP) + 1) % 2);
                processControl.set(ProcessControlSetting.PROCESS_TYPE, 0);
                processControl.set(ProcessControlSetting.DO_SIGNAL, true);
            }
        });

        Button resetHardwareButton = (Button) findViewById(R.id.resetHardwareButton);
        resetHardwareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processControl.set(ProcessControlSetting.PROCESS_TYPE, 1);
                processControl.set(ProcessControlSetting.DO_SIGNAL, true);
            }
        });

        Button initButton = (Button) findViewById(R.id.initButton);
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processControl.set(ProcessControlSetting.PROCESS_TYPE, 2);
                processControl.set(ProcessControlSetting.DO_SIGNAL, true);
            }
        });

        Button move2ZeroButton = (Button) findViewById(R.id.move2ZeroButton);
        move2ZeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moveSystem.isConnect()) {
                    moveSystem.move2Zero();
                }
            }
        });

        Button resetChessButton = (Button) findViewById(R.id.resetChessButton);
        resetChessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processControl.set(ProcessControlSetting.PROCESS_TYPE, 3);
                processControl.set(ProcessControlSetting.DO_SIGNAL, true);
            }
        });

        final String[] stepList = {"玩家走", "机械臂走"};
        final Button playChessButton = (Button) findViewById(R.id.playChessButton);
        playChessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processControl.set(ProcessControlSetting.PLAY_STEP, ((int) processControl.get(ProcessControlSetting.PLAY_STEP) + 1) % 2);
                playChessButton.setText(stepList[(int) processControl.get(ProcessControlSetting.PLAY_STEP)]);
                processControl.set(ProcessControlSetting.PROCESS_TYPE, 4);
                processControl.set(ProcessControlSetting.DO_SIGNAL, true);
            }
        });

        moveSystem = new MoveSystem(this);
        Log.e(TAG, "Init MoveSystem Start!");
        moveSystem.initSystem(processControl);
        moveSystem.connect();

        chessLogic = new ChessLogic(moveSystem);
    }

    private void initCamera() {
        openCvCameraView = findViewById(R.id.HelloOpenCvView);
        openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        openCvCameraView.setCvCameraViewListener(this);
        openCvCameraView.setMaxFrameSize(1920, 1080);
        openCvCameraView.enableFpsMeter();
        openCvCameraView.enableView();

        openCvCameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                openCvCameraView.focusPoint(event);
                return false;
            }
        });
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
        chessDetection = new ChessDetection(new Size(width, height));
    }

    @Override
    public void onCameraViewStopped() {
    }


    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        /* Process*/
        Mat ret = inputFrame.rgba();
        if ((boolean) processControl.get(ProcessControlSetting.DO_SIGNAL)) {
            switch ((int) processControl.get(ProcessControlSetting.PROCESS_TYPE)) {
                //实验性方法
                case ChessStepSetting.LAB_STEP:
                    switch ((int) processControl.get(ProcessControlSetting.LAB_STEP)) {
                        case 0:
                            break;
                        case 1:
                            ret = chessDetection.lab(inputFrame);
                            break;
                    }
                    break;
                //硬件归零
                case ChessStepSetting.RESET_STEP:
                    if (moveSystem.isConnect()) {
                        moveSystem.reset();
                        processControl.set(ProcessControlSetting.DO_SIGNAL, false);
                    }
                    break;
                //初始化
                case ChessStepSetting.INIT_STEP:
                    //变量初始化
                    ChessUtils.chess = new Chess[32];
                    ChessUtils.chessboard = new Chessboard[8][8];
                    ChessUtils.chessboardKeyPoints = new Point[4];
                    //识别棋盘
                    chessDetection.detectChessBoard(inputFrame);
                    chessDetection.detectChessBoardBowl(inputFrame);
                    if (ChessUtils.chessboard[7][7] != null && ChessUtils.chessboardKeyPoints[3] != null && ChessUtils.chessboardBowl[7][3] != null && ChessUtils.chessboardBowlKeyPoints[3] != null) {
                        processControl.set(ProcessControlSetting.DO_SIGNAL, false);
                    }
                    break;
                //码棋
                case ChessStepSetting.RESETCHESS_STEP:
                    if (chessDetection.detectChessMen(inputFrame)) {
                        //码棋
                        chessLogic.clearStartingPoint();
                        //第二遍把中间的棋子归位
                        chessLogic.clearStartingPoint();
                        moveSystem.move2Zero();
                        chessLogic.requireResetChess();
                        processControl.set(ProcessControlSetting.DO_SIGNAL, false);
                    }
                    break;
                //下棋
                case ChessStepSetting.PLAY_STEP:
                    switch ((int) processControl.get(ProcessControlSetting.PLAY_STEP)) {
                        //保存旧棋盘状态
                        case 0:
                            if (chessDetection.detectChessMen(inputFrame)) {
                                voiceHint.playVoice(R.raw.playerturn);
                                ChessUtils.preChessboard = chessLogic.saveCurrentChessboard();
                                processControl.set(ProcessControlSetting.DO_SIGNAL, false);
                            }
                            break;
                        //识别棋子
                        case 1:
                            if (chessDetection.detectChessMen(inputFrame)) {
                                //识别玩家走法
                                String playerStep = chessLogic.detectChessStep();
                                Log.e(TAG, "Player Step: " + playerStep);
                                //给出机械臂走法
                                String robotStep = chessLogic.requireRobotChessStep(playerStep);
                                if (robotStep != null) {
                                    Log.e(TAG, "Robot Step: " + robotStep);
                                    switch (robotStep) {
                                        case "GGWIN":
                                            voiceHint.playVoice(R.raw.youwin);
                                            break;
                                        case "GGLOSE":
                                            voiceHint.playVoice(R.raw.youloose);
                                            break;
                                        default:
                                            chessLogic.doRobotChessStep(robotStep, playerStep);
                                            moveSystem.move2Zero();
                                            break;
                                    }
                                } else {
                                    Log.e(TAG, "Wrong Step!");
                                    voiceHint.playVoice(R.raw.wrongstep);
                                    chessLogic.doRobotChessStep(null, playerStep);
                                    moveSystem.move2Zero();
                                }
                                processControl.set(ProcessControlSetting.DO_SIGNAL, false);
                            }
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
                    Core.line(ret, point, point, new Scalar(0, 0, 255), 10);
                }
            }
        }
        if (ChessUtils.chessboardBowl[7][3] != null) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 4; col++) {
                    Point point = new Point(ChessUtils.chessboardBowl[row][col].x, ChessUtils.chessboardBowl[row][col].y);
                    Core.line(ret, point, point, new Scalar(255, 0, 0), 10);
                }
            }
        }
        for (int cnt = 0; cnt < 32 && ChessUtils.chess[cnt] != null; cnt++) {
            double[] position = ChessUtils.chess[cnt].getAbsolutePosition();
            Point point = new Point(position[0], position[1]);
            Core.line(ret, point, point, new Scalar(0, 255, 0), 10);
        }

        return ret;
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
