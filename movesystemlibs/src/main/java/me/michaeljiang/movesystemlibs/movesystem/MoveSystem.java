package me.michaeljiang.movesystemlibs.movesystem;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import me.michaeljiang.movesystemlibs.movesystem.component.ComponentSetting;
import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.Arm7Bot;
import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.model.MotoPosition;
import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.model.ReceiverResult;
import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.model.TransformResult;
import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.util.ArmReceiver;
import me.michaeljiang.movesystemlibs.movesystem.component.bluetooth.MyBluetooth;
import me.michaeljiang.movesystemlibs.movesystem.component.bluetooth.model.MyBlueToothDevice;
import me.michaeljiang.movesystemlibs.movesystem.component.bluetooth.util.BluetoothTool;
import me.michaeljiang.movesystemlibs.movesystem.component.conveyerband.ConveyerBand;
import me.michaeljiang.movesystemlibs.movesystem.component.conveyerband.model.ConveyerReceiver;
import me.michaeljiang.movesystemlibs.movesystem.component.mqtt.MyMqtt;
import me.michaeljiang.movesystemlibs.movesystem.model.Chess;
import me.michaeljiang.movesystemlibs.movesystem.model.MoveData;
import me.michaeljiang.movesystemlibs.movesystem.setting.MoveSystemSetting;
import me.michaeljiang.movesystemlibs.movesystem.setting.ProjectSetting;
import me.michaeljiang.movesystemlibs.movesystem.thread.MoveSystemSend;

/**
 * Created by MichaelJiang on 2017/5/7.
 * Modify by MichaelJiang  on 2017/5/23
 * 修改内容：
 * -新增上层调用API
 * -优化代码结构
 * -修改函数 reset  同时初始化滑轨和机械手
 * -新增函数 contrastConveyerBandPosition  计算滑轨移动命令
 * -新增函数 readArm7BotData 计算机械手移动轨迹节点
 */

public class MoveSystem {
    /**
     * Android系统相关
     **/
    private Context context;
    private String TAG = "MoveSystem";
    private Gson moveGson = new Gson();
    private boolean isDebug = false;

    /**
     * MoveSystem相关
     **/
    private MoveSystemSend moveSystemSend = null;
    private MoveData armState = new MoveData();
    private boolean isConnect = false;

    /**
     * 机械手相关
     **/
    private Arm7Bot arm7Bot = new Arm7Bot();
    private boolean isCatch = false;

    /**
     * 传送带相关
     **/
    private ConveyerBand conveyerBand = new ConveyerBand();

    /**
     * MQTT相关
     **/
    private MyMqtt mqtt;

    /**
     * 蓝牙相关
     **/
    MyBluetooth bluetooth;

    /**
     * 逻辑相关
     **/
    private Chess[][] chinaChess = new Chess[8][8];//col,row
    private Chess[][] chesses = new Chess[8][8];//col,row
    private Chess[][] goChess = new Chess[19][19];//col,row
    private Chess[][] internation_chess_bowl = new Chess[4][8];

    /*
        ************* 滑轨
        A8 B8 . . . .
        A7 B7 . . . .
        . . . . . . .
        A1 B1 . . . .

     */

    /**
     * 测试用数据
     **/
    //internation_chess
    MotoPosition Internation_1 = new MotoPosition(new int[]{ 84, 39,100, 90, 50, 90, 70});
    MotoPosition Internation_2 = new MotoPosition(new int[]{ 84, 60,107, 90, 46, 90, 70});
    MotoPosition Internation_3 = new MotoPosition(new int[]{ 83, 80,112, 90, 40, 90, 70});
    MotoPosition Internation_4 = new MotoPosition(new int[]{ 82,104,110, 90, 44, 90, 70});

    MotoPosition Internation_5 = new MotoPosition(new int[]{132, 72, 109, 90, 44, 90, 70});
    MotoPosition Internation_6 = new MotoPosition(new int[]{138, 86, 110, 90, 43, 90, 70});
    MotoPosition Internation_7 = new MotoPosition(new int[]{143, 98, 109, 90, 46, 90, 70});
    MotoPosition Internation_8 = new MotoPosition(new int[]{151,114, 108, 90, 46, 90, 70});

    //internation_chess_bowl
    MotoPosition Internation_Bow_1 = new MotoPosition(new int[]{113, 33,  102, 90, 45, 90, 70});
    MotoPosition Internation_Bow_2 = new MotoPosition(new int[]{114, 51, 103, 90, 49, 90, 70});
    MotoPosition Internation_Bow_3 = new MotoPosition(new int[]{116, 69, 109, 90, 44, 90, 70});
    MotoPosition Internation_Bow_4 = new MotoPosition(new int[]{119, 84, 114, 90, 40, 90, 70});

    MotoPosition Internation_Bow_5 = new MotoPosition(new int[]{135, 70, 110, 90, 43, 90, 70});
    MotoPosition Internation_Bow_6 = new MotoPosition(new int[]{140, 74, 122, 90, 23, 90, 70});
    MotoPosition Internation_Bow_7 = new MotoPosition(new int[]{146, 93, 110, 90, 46, 90, 70});
    MotoPosition Internation_Bow_8 = new MotoPosition(new int[]{152,100, 112, 90, 42, 90, 70});
    //
    //
    MotoPosition reset = new MotoPosition(new int[]{90, 90, 65, 90, 90, 90, 70});


    /**
     * 获取Context，并进行基本的初始化
     *
     * @return 是否初始化成功
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    public MoveSystem(Context context) {
        this.context = context;

        //格子之间相差5.1cm
        double tempPositionA = 0;//cm
        double tempPositionB = 33.75;//cm

        //格子之间相差4.5cm
        double tempPositionC = 0;//cm
        double tempPositionD = 11.25;//cm
        //col=column 竖行row=row 横行


        for (int row = 0; row < 8; row++) {
            //按照循环依次为 A->B->C->D
            chesses[row][0] = new Chess(Internation_1, tempPositionA);
            chesses[row][1] = new Chess(Internation_2, tempPositionA);
            chesses[row][2] = new Chess(Internation_3, tempPositionA);
            chesses[row][3] = new Chess(Internation_4, tempPositionA);
            chesses[row][4] = new Chess(Internation_5, tempPositionB);
            chesses[row][5] = new Chess(Internation_6, tempPositionB);
            chesses[row][6] = new Chess(Internation_7, tempPositionB);
            chesses[row][7] = new Chess(Internation_8, tempPositionB);
            //每排移动完后,改变方向变量
            tempPositionA += 5.1;//cm
            tempPositionB += 5.1;//cm
        }

        for (int row = 0; row < 4; row++) {
            // -- Position 0 --
            internation_chess_bowl[row][0] = new Chess(Internation_Bow_1, tempPositionC);
            internation_chess_bowl[row][1] = new Chess(Internation_Bow_2, tempPositionC);
            internation_chess_bowl[row][2] = new Chess(Internation_Bow_3, tempPositionC);
            internation_chess_bowl[row][3] = new Chess(Internation_Bow_4, tempPositionC);

            // -- Position 15 --
            internation_chess_bowl[row][4] = new Chess(Internation_Bow_5, tempPositionD);
            internation_chess_bowl[row][5] = new Chess(Internation_Bow_6, tempPositionD);
            internation_chess_bowl[row][6] = new Chess(Internation_Bow_7, tempPositionD);
            internation_chess_bowl[row][7] = new Chess(Internation_Bow_8, tempPositionD);
            tempPositionC += 4.5;//cm
            tempPositionD += 4.5;//cm
        }

    }

    /**
     * 对MoveSysten中的Mqtt和Bluetooth进行初始化
     *
     * @param handler 来自主类的Handle，向其他Activity传递信息
     * @return 是否初始化成功
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    public boolean initSystem(Handler handler) {
        mqtt = new MyMqtt(handler);
        bluetooth = new MyBluetooth(context, handler);
        moveSystemSend = new MoveSystemSend(conveyerBand, arm7Bot, mqtt, bluetooth);
        moveSystemSend.start();
        return true;
    }

    /**
     * 对MoveSysten中的Mqtt和Bluetooth进行初始化
     *
     * @return 是否初始化成功
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    public boolean initSystem(List<Object> args) {
        final List<Object> processControl = args;

        Handler uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ComponentSetting.MQTT_STATE_CONNECTED) {
                    mqtt.subTopic(MoveSystemSetting.TAG_RECEIVER_FROM_TRANSPORTER);
                    mqtt.subTopic(MoveSystemSetting.TAG_RECEIVER_FROM_CHESS_BELL);
                    Log.d(TAG + ":MQTT", "连接成功");
                } else if (msg.what == ComponentSetting.MQTT_STATE_LOST) {
                    Log.d(TAG, "连接丢失，进行重连");
                } else if (msg.what == ComponentSetting.MQTT_STATE_FAIL) {
                    Log.d(TAG, "连接成功");
                } else if (msg.what == ComponentSetting.BLUETOOTH_LINK_SUCCESS_MESSAGE) {
                    Log.d(TAG + ":Bluetooth", "连接成功");
                } else if (msg.what == ComponentSetting.BLUETOOTH_LINK_WAITING_MESSAGE) {
                    Log.d(TAG, (String) msg.obj);
                } else if (msg.what == ComponentSetting.BLUETOOTH_LINK_ERROR_MESSAGE) {
                    bluetooth.disconnectBluetooth();
                    bluetooth.linkBluetooth();
                    Log.d(TAG, (String) msg.obj);
                } else if (msg.what == ComponentSetting.MQTT_STATE_RECEIVE) {
                    //MqttReceiver
                    Bundle data = msg.getData();
                    String topic = data.getString("topic");
                    if (topic != null) {
                        if (topic.equals(MoveSystemSetting.TAG_RECEIVER_FROM_CHESS_BELL)) {
                            if (processControl != null) {
                                processControl.set(0, Integer.valueOf(data.getString("obj")));
                                switch ((int) processControl.get(0)) {
                                    case 0:
                                        processControl.set(1, ((int) processControl.get(1) + 1) % 2);
                                        break;
                                    case 4:
                                        processControl.set(2, ((int) processControl.get(2) + 1) % 2);
                                        break;
                                    default:
                                        break;
                                }
                                processControl.set(3, true);
                            }
                        } else if (topic.equals(MoveSystemSetting.TAG_RECEIVER_FROM_TRANSPORTER)) {
                            try {
                                ConveyerReceiver myReceiver = moveGson.fromJson(data.getString("obj"), ConveyerReceiver.class);
                                conveyerBand.setPosition(myReceiver.getPlatformState().getPlatformPosition());
                                conveyerBand.setMove(myReceiver.getPlatformState().isMove());
                                if (myReceiver.getCode() == 2) {
                                    moveSystemSend.setCanArmMove(true);
                                }
                                Log.d(TAG, conveyerBand.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (msg.what == ComponentSetting.BLUETOOTH_MESSAGE_RECEIVER) {
                    //BluetoothReceiver
                    Bundle temp = (Bundle) msg.obj;
                    int[] receiver = temp.getIntArray("receiver");
                    byte[] ttemp = new byte[receiver.length];
                    if (receiver.length == 17 && receiver[0] == (byte) 0xFE) {
                        for (int i = 0; i < receiver.length; i++) {
                            ttemp[i] = (byte) receiver[i];
                        }
                        if (isDebug)
                            Log.d(TAG, "Byte[] : " + BluetoothTool.bytesToHexString(ttemp));
                        try {
                            ReceiverResult result = ArmReceiver.analysisReceived(receiver);
                            arm7Bot.setMove(result.isMoveing());
                            if (isDebug)
                                Log.d(TAG, String.valueOf(result.isMoveing()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                super.handleMessage(msg);
            }
        };
        mqtt = new MyMqtt(uiHandler);
        bluetooth = new MyBluetooth(context, uiHandler);
        moveSystemSend = new MoveSystemSend(conveyerBand, arm7Bot, mqtt, bluetooth);
        moveSystemSend.start();
        return true;
    }

    public boolean disConnect() {
        disconnectBluetooth();
        disconnectMqtt();
        return true;
    }

    public boolean connect() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(10000);
                    disconnectBluetooth();
                    sleep(2000);
                    connectMqtt();
                    sleep(2000);
                    connectBluetooth();
                    sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("ThreadMessage", "FunctionFinish");
            }
        }.start();
        return true;
    }

    public void dontUse() {
        moveSystemSend.clearWaiting();
        moveSystemSend.setCanArmMove(true);
    }

    /**
     * 连接蓝牙连接
     *
     * @return 调用是否成功
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    private boolean connectBluetooth() {
        if (bluetooth != null) {
            bluetooth.linkBluetooth();
        }
        return true;
    }

    /**
     * 打开MQTT连接
     *
     * @return 调用是否成功
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    private boolean connectMqtt() {
        if (mqtt != null)
            mqtt.connectMqtt();
        return true;
    }

    /**
     * 断开蓝牙连接
     *
     * @return 是否成功断开连接
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    private boolean disconnectBluetooth() {
        if (bluetooth != null) {
            bluetooth.disconnectBluetooth();
        }
        return true;
    }

    /**
     * 断开MQTT连接
     *
     * @return 是否成功断开连接
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    private boolean disconnectMqtt() {
        if (mqtt != null) {
            mqtt.disConnectMqtt();
        }
        return true;
    }

    /**
     * 设置蓝牙信息
     *
     * @param BluetoothType    蓝牙设备类型
     * @param BluetoothName    蓝牙设备名称
     * @param BluetoothAddress 蓝牙设备地址
     * @return 是否连接成功
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    public boolean settingBluetooth(int BluetoothType, String BluetoothName, String BluetoothAddress) {
        if (bluetooth == null) {
            Log.d(TAG, "请先初始化Bluetooth");
            return false;
        }
        bluetooth.BlueToothAddress = BluetoothAddress;
        bluetooth.BlueToothName = BluetoothName;
        bluetooth.BlueToothType = BluetoothType;
        return true;
    }

    /**
     * 直接根据给予的蓝牙参数进行连接
     *
     * @param BluetoothType 设置蓝牙类型，是否为蓝牙4.0
     * @param mDevice       自定义的设备类，包括地址和名称
     * @return 是否连接成功
     * @State Finish
     * @Version 1.0
     * @User MichaelJiang
     */
    public boolean settingBluetooth(int BluetoothType, MyBlueToothDevice mDevice) {
        if (bluetooth == null) {
            Log.d(TAG, "请先初始化Bluetooth");
            return false;
        }
        bluetooth.BlueToothAddress = mDevice.getBlueToothAddress();
        bluetooth.BlueToothName = mDevice.getBlueToothName();
        bluetooth.BlueToothType = BluetoothType;
        return true;
    }

    /**
     * 初始化Mqtt连接的信息
     *
     * @param Host     Mqtt连接地址
     * @param Port     Mqtt的连接端口
     * @param userId   连接的用户名ID
     * @param password 连接的密码
     * @param clientId 登录时的设备ID
     * @return
     */
    public boolean settingMqtt(String Host, String Port, String userId, String password, String clientId) {
        if (mqtt == null) {
            Log.d(TAG, "请先初始化MQTT");
            return false;
        }
        mqtt.host = Host;
        mqtt.port = Port;
        mqtt.userID = userId;
        mqtt.passWord = password;
        mqtt.clientID = clientId;
        return true;
    }


    /**
     * 封装API，主线程传入棋子的位置的行列
     *
     * @param chessType 传入棋子的类型
     * @param row       传入棋子的行
     * @param col       传入棋子的列
     * @param isCatch   是否抓取棋子
     * @return 放入线程中是否成功
     * @State Doing
     * @Version 1.0
     * @User MichaelJiang
     */
    public boolean move(int chessType, int col, int row, boolean isCatch) {
        //(0,0) A8 0 7 (7,7) A1
        //(0,1) B8
        switch (chessType) {
            case ProjectSetting.INTERNATIONAL_CHESS: {
                if (row >= 0 && row < 8 && col >= 0 && col < 8)
                    return changeToCommand(chessType, row, 7 - col, isCatch);
                return false;
            }
            case ProjectSetting.INTERNATIONAL_CHESS_BOWL: {
                if (row >= 0 && row < 4 && col >= 0 && col < 8)
                    return changeToCommand(chessType, row, 7 - col, isCatch);
                return false;
            }
        }
        return false;
    }

    /**
     * 封装API，主线程传入棋子的位置A1、A2、A3这种感觉
     *
     * @param chessType 传入棋子的类型
     * @param command   "A1"之类的回传参数
     * @param isCatch   是否抓取棋子
     * @return 是否成功
     * @State Doing
     * @Version 1.0
     * @User MichaelJiang
     */
    public boolean move(int chessType, String command, boolean isCatch) {
        byte[] temp = command.getBytes();
        switch (chessType) {
            case ProjectSetting.INTERNATIONAL_CHESS: {
                if (temp[0] >= 'A' && temp[0] <= 'H' && temp[1] > '0' && temp[1] < '9') {
                    return changeToCommand(chessType, temp[0] - 'A', temp[1] - '0' - 1, isCatch);
                }
            }
            case ProjectSetting.INTERNATIONAL_CHESS_BOWL: {
                if (temp[0] >= 'A' && temp[0] <= 'D' && temp[1] > '0' && temp[1] < '9') {
                    return changeToCommand(chessType, temp[0] - 'A', temp[1] - '0' - 1, isCatch);
                }
            }
        }
        return false;
    }

    public boolean moveA2B(int ChessType1, String command1, int ChessType2, String command2) {
        move(ChessType1, command1, true);
        move(ChessType2, command2, false);
        ArmReset();
        return true;
    }

    /**
     * 让系统根据棋盘上的某一点进行指定位移
     *
     * @param chessType 现在下棋的棋子类型
     * @param row       行
     * @param col       类
     * @return 返回移动结果
     * @State Doing
     * @Version 0.4
     * @User MichaelJiang
     * V0.2版本更新内容：
     * -每次移动到下一个子之前首先会回到原始位置
     * -以增加滑轨反馈
     * -目前是先移动滑轨，在移动机械手的
     * <p>
     * V0.3版本更新内容：
     * -解决了机械手和滑轨之间协同运动的问题（对Arm7Bot和ConveyeryBand的SetMove进行了线程控制）
     * 待进行事项：
     * -滑轨发送数据不及时，导致上位机无法接受到数据
     * <p>
     * V0.4版本更新
     * -解决了上位机无法收到数据的问题，采用的方式是延时等待2分钟，并且增加下位机发送接受到数据的次数
     * 待解决事项：
     * -部分地区抓取不稳定
     */
    public boolean changeToCommand(int chessType, int row, int col, boolean isCatch) {
        //Step One      如果不在复位的位置上则抬起机械手
        MoveData stepOne = armState;
        armState = new MoveData();
        moveSystemSend.addMoveData(stepOne);

        //Step Two      回到复位的位置
        MoveData stepTwo = new MoveData();
        MotoPosition resetPosition = readArm7BotData(reset, false, this.isCatch, false);
        TransformResult transformResult = arm7Bot.toStandMotoByte(resetPosition);
        stepTwo.setTransformResult(transformResult);
        moveSystemSend.addMoveData(stepTwo);


        //Step Three    移动到指定位置的上方
        MoveData stepThree = new MoveData();
        Chess targetChess;
        switch (chessType) {
            case ProjectSetting.INTERNATIONAL_CHESS: {
                targetChess = chesses[row][col];
                break;
            }
            case ProjectSetting.INTERNATIONAL_CHESS_BOWL: {
                targetChess = internation_chess_bowl[row][col];
                break;
            }
            default: {
                targetChess = null;
                break;
            }
        }

        if (targetChess == null)
            return false;

        //计算首先移动滑轨
        //如果不需要移动则返回null,发送的时候已经做过null的检测，无需在进行检查
        String conveyerBandCommand = contrastConveyerBandPosition(targetChess);
        stepThree.setConveyerBandData(conveyerBandCommand);

        //计算机械手位移
        MotoPosition prePosition = readArm7BotData(targetChess.getMotoPosition(), true, this.isCatch, false);
        TransformResult stepThreeCommad = arm7Bot.toStandMotoByte(prePosition);
        stepThree.setTransformResult(stepThreeCommad);
        moveSystemSend.addMoveData(stepThree);

        prePosition = readArm7BotData(targetChess.getMotoPosition(), true, isCatch, false);
        armState.setTransformResult(arm7Bot.toStandMotoByte(prePosition));//设置原始的位置为isCatch

        //StepFourth
        MoveData stepFourth = new MoveData();
        MotoPosition truePosition = readArm7BotData(targetChess.getMotoPosition(), false, this.isCatch, false);
        TransformResult stepFourthCommad = arm7Bot.toStandMotoByte(truePosition);
        stepFourth.setTransformResult(stepFourthCommad);
        moveSystemSend.addMoveData(stepFourth);

        //StepFive
        this.isCatch = isCatch;
        MoveData stepFive = new MoveData();
        MotoPosition finishPosition = readArm7BotData(targetChess.getMotoPosition(), false, this.isCatch, true);
        stepFive.setTransformResult(arm7Bot.toStandMotoByte(finishPosition));
        moveSystemSend.addMoveData(stepFive);

        return true;
    }


    /**
     * 根据条件返回调整和都舵机值
     *
     * @param data             目标的舵机值
     * @param isReadyPosition  是否为准备位置
     * @param isFinishPosition 是否为最终位置
     * @param isCatch          当前状态是否在抓取
     * @return 返回舵机值
     * @State Doing
     * @Version 1.0
     * @User MichaelJiang
     */
    private MotoPosition readArm7BotData(MotoPosition data, boolean isReadyPosition, boolean isCatch, boolean isFinishPosition) {
        MotoPosition readData = new MotoPosition();
        readData.setMotoData(data.getAngleInts());
        //如果是抓的则设置Moto7为0
        if (isCatch)
            readData.setMoto7(0);
        else
            readData.setMoto7(70);

        //如果是预备位置，则设置Moto-15
        if (isReadyPosition)
            readData.setMoto3(readData.getMoto3() - 15);

        if(isFinishPosition){
            int tempMoto5 = readData.getMoto5();
            int tempMOto3 = readData.getMoto3();
            readData.setMoto5(tempMoto5+12);
            readData.setMoto3((tempMOto3+1));
        }
        return readData;
    }

    /**
     * 对比棋子和当前位置的位置差距，组装下位机需要移动的Command
     *
     * @param chess 目标棋子的位置
     * @return 例子"FF+1600EE"
     * @State Doing
     * @Version 1.0
     * @User MichaelJiang
     */
    private String contrastConveyerBandPosition(Chess chess) {
        if (conveyerBand.getPosition() != chess.getPosition()) {
            String flag = "+";
            double dispariPosition = chess.getPosition() - conveyerBand.getPosition();//要到达的位置减去现在的位置
            conveyerBand.setPosition(chess.getPosition());
            if (dispariPosition < 0) {
                dispariPosition = -dispariPosition;
                flag = "-";
            }
            int step = (int) ((dispariPosition / 7.5) * 6400);
            String msg = "FF" + flag + String.valueOf(step) + "EE";
            Log.d("MoveSystem", msg);
            return msg;
        }
        return null;
    }

    private boolean ConveyerBandReset() {
        ArmReset();
        MoveData moveData = new MoveData();
        Chess temp = new Chess();
        temp.setPosition(1);
        moveData.setConveyerBandData(contrastConveyerBandPosition(temp));
        moveSystemSend.addMoveData(moveData);

        MoveData moveData2 = new MoveData();
        moveData2.setConveyerBandData("AALBB");
        moveSystemSend.addMoveData(moveData2);
        return true;
    }

    public boolean move2Zero() {
        MoveData moveData = new MoveData();
        Chess temp = new Chess();
        temp.setPosition(53.625);
        moveData.setConveyerBandData(contrastConveyerBandPosition(temp));
        moveSystemSend.addMoveData(moveData);
        return true;
    }

    public boolean ArmReset() {
        moveSystemSend.addMoveData(armState);
        armState = new MoveData();
        MoveData resetData = new MoveData();
        resetData.setTransformResult(arm7Bot.toStandMotoByte(reset));
        moveSystemSend.addMoveData(resetData);
        return true;
    }

    /**
     * 初始化整个系统
     *
     * @return 返回初始化结果
     * @State Doing
     * @Version 0.2
     * @User MichaelJiang
     * V0.1
     * 待进行事项：
     * 1.滑轨的初始化还没有做
     * 2.存在多个需要返回的点（左右）
     * <p>
     * V0.2版本更新内容：
     * -已经可以进行滑轨的初始化
     * 带解决事项
     * -还没有做右边的返回
     */
    public boolean reset() {
        armState = new MoveData();
        MoveData resetData = new MoveData();
        resetData.setTransformResult(arm7Bot.toStandMotoByte(reset));
        moveSystemSend.addMoveData(resetData);
        MoveData moveData = new MoveData();
        Chess temp = new Chess();
        temp.setPosition(1);
        moveData.setConveyerBandData(contrastConveyerBandPosition(temp));
        moveSystemSend.addMoveData(moveData);

        MoveData moveData2 = new MoveData();
        moveData2.setConveyerBandData("AALBB");
        moveSystemSend.addMoveData(moveData2);
        return true;
    }

    public boolean isConnect() {
        Log.e("MyMqtt", String.valueOf(MyMqtt.isConnect()));
        Log.e("MyBluetooth", String.valueOf(MyBluetooth.isConnect()));
        if (MyMqtt.isConnect() && MyBluetooth.isConnect()) {
            return true;
        }
        return false;
    }
}
