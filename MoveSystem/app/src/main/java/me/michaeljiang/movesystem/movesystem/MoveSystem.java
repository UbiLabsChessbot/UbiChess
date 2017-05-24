package me.michaeljiang.movesystem.movesystem;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import me.michaeljiang.movesystem.movesystem.component.ComponentSetting;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.Arm7Bot;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.MotoPosition;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.ReceiverResult;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.TransformResult;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.util.ArmReceiver;
import me.michaeljiang.movesystem.movesystem.model.MoveData;
import me.michaeljiang.movesystem.movesystem.model.Chess;
import me.michaeljiang.movesystem.movesystem.component.conveyerband.ConveyerBand;
import me.michaeljiang.movesystem.movesystem.component.conveyerband.model.ConveyerReceiver;
import me.michaeljiang.movesystem.movesystem.component.bluetooth.MyBluetooth;
import me.michaeljiang.movesystem.movesystem.component.bluetooth.model.MyBlueToothDevice;
import me.michaeljiang.movesystem.movesystem.component.bluetooth.util.BluetoothTool;
import me.michaeljiang.movesystem.movesystem.component.mqtt.MyMqtt;
import me.michaeljiang.movesystem.movesystem.setting.MoveSystemSetting;
import me.michaeljiang.movesystem.movesystem.setting.ProjectSetting;
import me.michaeljiang.movesystem.movesystem.thread.MoveSystemSend;

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
    /**Android系统相关**/
    private Context context;
    private String TAG = "MoveSystem";
    private Gson moveGson = new Gson();
    private boolean isDebug = false;

    /**MoveSystem相关**/
    MoveSystemSend moveSystemSend = null;
    MoveData armState = new MoveData();

    /**机械手相关**/
    private Arm7Bot arm7Bot = new Arm7Bot();
    private boolean isCatch = false;
    /**传送带相关**/
    private ConveyerBand conveyerBand = new ConveyerBand();

    /**MQTT相关**/
    private MyMqtt mqtt ;

    /**蓝牙相关**/
    MyBluetooth bluetooth;

    /**逻辑相关**/
    private Chess[][] chinaChess = new Chess[8][8];//col,row
    private Chess[][] chesses = new Chess[8][8];//col,row
    private Chess[][] goChess = new Chess[19][19];//col,row
    private Chess[][] internation_chess_bowl = new Chess[8][4];

    /**测试用数据**/
    //internation_chess
    MotoPosition A = new MotoPosition(new int[]{  86, 45, 99, 90, 65, 90, 75});
    MotoPosition B = new MotoPosition(new int[]{  85, 61,111, 90, 49, 90, 75});
    MotoPosition C = new MotoPosition(new int[]{  85, 81,116, 90, 44, 90, 75});
    MotoPosition D = new MotoPosition(new int[]{  84,105,116, 90, 44, 90, 75});

    MotoPosition E = new MotoPosition(new int[]{136, 71,113, 91, 43, 90, 75});
    MotoPosition F = new MotoPosition(new int[]{141, 83,117, 91, 46, 90, 75});
    MotoPosition G = new MotoPosition(new int[]{146, 95,118, 90, 42, 90, 75});
    MotoPosition H = new MotoPosition(new int[]{155,108,116, 90, 44, 90, 75});

    //internation_chess_bowl
    MotoPosition AA = new MotoPosition(new int[]{115, 33, 96, 90, 64, 90, 75});
    MotoPosition BB = new MotoPosition(new int[]{117, 52,106, 90, 58, 90, 75});
    MotoPosition CC = new MotoPosition(new int[]{120, 67,113, 90, 51, 90, 75});
    MotoPosition DD = new MotoPosition(new int[]{123, 81,118, 90, 47, 90, 75});

    MotoPosition EE = new MotoPosition(new int[]{139, 69,116, 90, 50, 90, 75});
    MotoPosition FF = new MotoPosition(new int[]{145, 80,119, 88, 44, 90, 75});
    MotoPosition GG = new MotoPosition(new int[]{150, 92,117, 90, 46, 90, 75});
    MotoPosition HH = new MotoPosition(new int[]{158, 101,118, 90, 43, 90, 75});
    //
    //
    MotoPosition reset = new MotoPosition(new int[]{90,90,65, 90, 90, 90, 70});


    /**
     * 获取Context，并进行基本的初始化
     * @return  是否初始化成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public MoveSystem(Context context){
        this.context = context;
        //格子之间相差5.1cm
        double tempPositionA = 0+35.7;//cm
        double tempPositionB = 33.75+35.7;//cm

        //格子之间相差4.4cm
        double tempPositionC = 0+14.31;//cm
        double tempPositionD = 11.25+14.31;//cm

        for(int row = 0; row < 8; row++){
            chesses[0][row] = new Chess(A,tempPositionA);
            chesses[1][row] = new Chess(B,tempPositionA);
            chesses[2][row] = new Chess(C,tempPositionA);
            chesses[3][row] = new Chess(D,tempPositionA);

            chesses[4][row] = new Chess(E,tempPositionB);
            chesses[5][row] = new Chess(F,tempPositionB);
            chesses[6][row] = new Chess(G,tempPositionB);
            chesses[7][row] = new Chess(H,tempPositionB);
            tempPositionA -= 5.1;//cm
            tempPositionB -= 5.1;//cm
        }
        for(int row = 0; row <4; row++){

            // -- Position 0 --
            internation_chess_bowl[0][row] = new Chess(AA,tempPositionC);
            internation_chess_bowl[1][row] = new Chess(BB,tempPositionC);
            internation_chess_bowl[2][row] = new Chess(CC,tempPositionC);
            internation_chess_bowl[3][row] = new Chess(DD,tempPositionC);

            // -- Position 7.5 --
            internation_chess_bowl[4][row] = new Chess(EE,tempPositionD);
            internation_chess_bowl[5][row] = new Chess(FF,tempPositionD);
            internation_chess_bowl[6][row] = new Chess(GG,tempPositionD);
            internation_chess_bowl[7][row] = new Chess(HH,tempPositionD);
            tempPositionC -=4.77;//cm
            tempPositionD -=4.77;//cm
        }

    }

    /**
     * 对MoveSysten中的Mqtt和Bluetooth进行初始化
     * @param handler  来自主类的Handle，向其他Activity传递信息
     * @return  是否初始化成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean initSystem(Handler handler){
        mqtt = new MyMqtt(handler);
        bluetooth = new MyBluetooth(context,handler);
        moveSystemSend = new MoveSystemSend(conveyerBand,arm7Bot,mqtt,bluetooth);
        moveSystemSend.start();
        return true;
    }

    /**
     * 对MoveSysten中的Mqtt和Bluetooth进行初始化
     * @return  是否初始化成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean initSystem(){
        Handler uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what== ComponentSetting.MQTT_STATE_CONNECTED){
                    mqtt.subTopic(MoveSystemSetting.TAG_RECEIVER_FROM_TRANSPORTER);
                    Log.d(TAG+":MQTT","连接成功");
                }else if(msg.what==ComponentSetting.MQTT_STATE_LOST){
                    Log.d(TAG,"连接丢失，进行重连");
                }else if(msg.what==ComponentSetting.MQTT_STATE_FAIL){
                    Log.d(TAG,"连接成功");
                }
                else if(msg.what== ComponentSetting.BLUETOOTH_LINK_SUCCESS_MESSAGE){
                    Log.d(TAG+":Bluetooth","连接成功");
                }
                else if(msg.what==ComponentSetting.BLUETOOTH_LINK_WAITING_MESSAGE){
                    Log.d(TAG,(String)msg.obj);
                }
                else if(msg.what== ComponentSetting.BLUETOOTH_LINK_ERROR_MESSAGE){
                    Log.d(TAG,(String)msg.obj);
                }
                else if(msg.what==ComponentSetting.MQTT_STATE_RECEIVE){
                    //MqttReceiver
                    Log.d(TAG,(String)msg.obj);
                    try {
                        ConveyerReceiver myReceiver = moveGson.fromJson((String)msg.obj,ConveyerReceiver.class);
                        conveyerBand.setPosition(myReceiver.getPlatformState().getPlatformPosition());
                        conveyerBand.setMove(myReceiver.getPlatformState().isMove());
                        if(myReceiver.getCode()==2){
                            moveSystemSend.setCanArmMove(true);
                        }
                        Log.d(TAG,conveyerBand.toString());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if(msg.what==ComponentSetting.BLUETOOTH_MESSAGE_RECEIVER){
                    //BluetoothReceiver
                    Bundle temp=(Bundle)msg.obj;
                    int[] receiver=temp.getIntArray("receiver");
                    byte[] ttemp=new byte[receiver.length];
                    if(receiver.length==17&&receiver[0]==(byte)0xFE){
                        for(int i=0;i<receiver.length;i++){
                            ttemp[i]=(byte)receiver[i];
                        }
                        if(isDebug)
                            Log.d(TAG,"Byte[] : "+ BluetoothTool.bytesToHexString(ttemp));
                        try{
                            ReceiverResult result =  ArmReceiver.analysisReceived(receiver);
                            arm7Bot.setMove(result.isMoveing());
                            if(isDebug)
                                Log.d(TAG,String.valueOf(result.isMoveing()));
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                super.handleMessage(msg);
            }
        };
        mqtt = new MyMqtt(uiHandler);
        bluetooth = new MyBluetooth(context,uiHandler);
        moveSystemSend = new MoveSystemSend(conveyerBand,arm7Bot,mqtt,bluetooth);
        moveSystemSend.start();
        return true;
    }

    public void dontUse(){
        moveSystemSend.clearWaiting();
        moveSystemSend.setCanArmMove(true);
    }

    /**
     * 连接蓝牙连接
     * @return  调用是否成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean connectBluetooth(){
        if(bluetooth != null){
            bluetooth.linkBluetooth();
        }
        return true;
    }

    /**
     * 打开MQTT连接
     * @return  调用是否成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean connectMqtt(){
        if(mqtt!=null)
            mqtt.connectMqtt();
        return true;
    }

    /**
     * 断开蓝牙连接
     * @return  是否成功断开连接
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean disconnectBluetooth(){
        if(bluetooth != null){
            bluetooth.disconnectBluetooth();
        }
        return true;
    }

    /**
     * 断开MQTT连接
     * @return  是否成功断开连接
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean disconnectMqtt(){
        if(mqtt!=null){
            mqtt.disConnectMqtt();
        }
        return true;
    }

    /**
     * 设置蓝牙信息
     * @param BluetoothType     蓝牙设备类型
     * @param BluetoothName     蓝牙设备名称
     * @param BluetoothAddress  蓝牙设备地址
     * @return  是否连接成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean settingBluetooth(int BluetoothType,String BluetoothName,String BluetoothAddress){
        if(bluetooth == null){
            Log.d(TAG,"请先初始化Bluetooth");
            return false;
        }
        bluetooth.BlueToothAddress = BluetoothAddress;
        bluetooth.BlueToothName = BluetoothName;
        bluetooth.BlueToothType = BluetoothType;
        return  true;
    }

    /**
     * 直接根据给予的蓝牙参数进行连接
     * @param BluetoothType 设置蓝牙类型，是否为蓝牙4.0
     * @param mDevice       自定义的设备类，包括地址和名称
     * @return  是否连接成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean settingBluetooth(int BluetoothType,MyBlueToothDevice mDevice){
        if(bluetooth == null){
            Log.d(TAG,"请先初始化Bluetooth");
            return false;
        }
        bluetooth.BlueToothAddress = mDevice.getBlueToothAddress();
        bluetooth.BlueToothName = mDevice.getBlueToothName();
        bluetooth.BlueToothType = BluetoothType;
        return  true;
    }

    /**
     * 初始化Mqtt连接的信息
     * @param Host      Mqtt连接地址
     * @param Port      Mqtt的连接端口
     * @param userId    连接的用户名ID
     * @param password  连接的密码
     * @param clientId  登录时的设备ID
     * @return
     */
    public boolean settingMqtt(String Host,String Port,String userId,String password,String clientId){
        if(mqtt == null){
            Log.d(TAG,"请先初始化MQTT");
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
     *封装API，主线程传入棋子的位置的行列
     * @param chessType 传入棋子的类型
     * @param row       传入棋子的行
     * @param col       传入棋子的列
     * @param isCatch   是否抓取棋子
     * @return      放入线程中是否成功
     * @State       Doing
     * @Version     1.0
     * @User        MichaelJiang
     */
    public boolean move(int chessType,int row,int col,boolean isCatch){
        return changeToCommand(chessType,row,col,isCatch);
    }

    /**
     *封装API，主线程传入棋子的位置A1、A2、A3这种感觉
     * @param chessType 传入棋子的类型
     * @param command   "A1"之类的回传参数
     * @param isCatch   是否抓取棋子
     * @return      是否成功
     * @State       Doing
     * @Version     1.0
     * @User        MichaelJiang
     */
    public boolean move(int chessType,String command,boolean isCatch){
        byte[] temp = command.getBytes();
        switch (chessType){
            case ProjectSetting.INTERNATIONAL_CHESS:{
                if(temp[0]>='A'&&temp[0]<='H'&&temp[1]>'0'&&temp[1]<'9'){
                    return changeToCommand(chessType,temp[0]-'A',temp[1]-'0'-1,isCatch);
                }
            }
            case ProjectSetting.CHESS_BOWL:{
                if(temp[0]>='A'&&temp[0]<='H'&&temp[1]>'0'&&temp[1]<'5'){
                    return changeToCommand(chessType,temp[0]-'A',temp[1]-'0'-1,isCatch);
                }
            }
        }
        return false;
    }

    /**
     * 让系统根据棋盘上的某一点进行指定位移
     * @param chessType 现在下棋的棋子类型
     * @param row       行
     * @param col       类
     * @return          返回移动结果
     * @State   Doing
     * @Version 0.4
     * @User    MichaelJiang
     * V0.2版本更新内容：
     * -每次移动到下一个子之前首先会回到原始位置
     * -以增加滑轨反馈
     * -目前是先移动滑轨，在移动机械手的
     *
     * V0.3版本更新内容：
     * -解决了机械手和滑轨之间协同运动的问题（对Arm7Bot和ConveyeryBand的SetMove进行了线程控制）
     * 待进行事项：
     *-滑轨发送数据不及时，导致上位机无法接受到数据
     *
     * V0.4版本更新
     * -解决了上位机无法收到数据的问题，采用的方式是延时等待2分钟，并且增加下位机发送接受到数据的次数
     * 待解决事项：
     * -部分地区抓取不稳定
     */
    public boolean changeToCommand(int chessType,int row,int col,boolean isCatch){
        //Step One      如果不在复位的位置上则抬起机械手
        MoveData stepOne = armState;
        armState = new MoveData();
        moveSystemSend.addMoveData(stepOne);

        //Step Two      回到复位的位置
        MoveData stepTwo = new MoveData();
        MotoPosition resetPosition = readArm7BotData(reset,false,this.isCatch);
        TransformResult transformResult = arm7Bot.toStandMotoByte(resetPosition);
        stepTwo.setTransformResult(transformResult);
        moveSystemSend.addMoveData(stepTwo);


        //Step Three    移动到指定位置的上方
        MoveData stepThree = new MoveData();
        Chess targetChess;
        switch (chessType){
            case ProjectSetting.INTERNATIONAL_CHESS:{
                targetChess = chesses[row][col];
                break;
            }
            case ProjectSetting.CHESS_BOWL:{
                targetChess = internation_chess_bowl[row][col];
                break;
            }
            default:{
                targetChess = null;
                break;
            }
        }

        if(targetChess == null)
            return false;

        //计算首先移动滑轨
        //如果不需要移动则返回null,发送的时候已经做过null的检测，无需在进行检查
        String conveyerBandCommand = contrastConveyerBandPosition(targetChess);
        stepThree.setConveyerBandData(conveyerBandCommand);

        //计算机械手位移
        MotoPosition prePosition = readArm7BotData(targetChess.getMotoPosition(),true,this.isCatch);
        TransformResult stepThreeCommad = arm7Bot.toStandMotoByte(prePosition);
        stepThree.setTransformResult(stepThreeCommad);
        moveSystemSend.addMoveData(stepThree);

        prePosition = readArm7BotData(targetChess.getMotoPosition(),true,isCatch);
        armState.setTransformResult(arm7Bot.toStandMotoByte(prePosition));//设置原始的位置为isCatch

        //StepFourth
        MoveData stepFourth = new MoveData();
        MotoPosition truePosition = readArm7BotData(targetChess.getMotoPosition(),false,this.isCatch);
        TransformResult stepFourthCommad =  arm7Bot.toStandMotoByte(truePosition);
        stepFourth.setTransformResult(stepFourthCommad);
        moveSystemSend.addMoveData(stepFourth);

        //StepFive
        this.isCatch = isCatch;
        MoveData stepFive = new MoveData();
        MotoPosition finishPosition = readArm7BotData(targetChess.getMotoPosition(),false,this.isCatch);
        stepFive.setTransformResult(arm7Bot.toStandMotoByte(finishPosition));
        moveSystemSend.addMoveData(stepFive);

        return true;
    }


    /**
     * 根据条件返回调整和都舵机值
     * @param data              目标的舵机值
     * @param isReadPosition    是否为准备位置
     * @param isCatch           当前状态是否在抓取
     * @return                  返回舵机值
     * @State   Doing
     * @Version 1.0
     * @User    MichaelJiang
     */
    private MotoPosition readArm7BotData(MotoPosition data,boolean isReadPosition,boolean isCatch){
        MotoPosition readData = new MotoPosition();
        readData.setMotoData(data.getAngleInts());
        //如果是抓的则设置Moto7为0
        if(isCatch)
            readData.setMoto7(0);
        else
            readData.setMoto7(70);

        //如果是预备位置，则设置Moto-15
        if(isReadPosition)
            readData.setMoto3(readData.getMoto3()-15);

        return readData;
    }

    /**
     * 对比棋子和当前位置的位置差距，组装下位机需要移动的Command
     * @param chess 目标棋子的位置
     * @return      例子"FF+1600EE"
     * @State   Doing
     * @Version 1.0
     * @User    MichaelJiang
     */
    private String contrastConveyerBandPosition(Chess chess){
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

    /**
     * 初始化整个系统
     * @return  返回初始化结果
     * @State   Doing
     * @Version 0.2
     * @User    MichaelJiang
     * V0.1
     * 待进行事项：
     * 1.滑轨的初始化还没有做
     * 2.存在多个需要返回的点（左右）
     *
     * V0.2版本更新内容：
     * -已经可以进行滑轨的初始化
     * 带解决事项
     * -还没有做右边的返回
     */
    public boolean reset(){
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

}


