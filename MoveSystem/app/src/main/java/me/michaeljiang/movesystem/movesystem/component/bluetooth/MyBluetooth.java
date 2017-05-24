package me.michaeljiang.movesystem.movesystem.component.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.michaeljiang.movesystem.movesystem.component.ComponentSetting;
import me.michaeljiang.movesystem.movesystem.component.bluetooth.model.MyBlueToothDevice;
import me.michaeljiang.movesystem.movesystem.component.bluetooth.util.BluetoothTool;


/**
 * Created by MichaelJiang on 2017/5/10.
 */

public class MyBluetooth {
    /**系统变量**/
    private String TAG = "MyBluetooth";
    private static boolean isOpen = false;
    private boolean isDebug = false;

    /**蓝牙变量**/
    public  String BlueToothName = "null";
    public  String BlueToothAddress = "98:D3:31:30:06:E9";
    public  int BlueToothType = -1;

    /**系统类**/
    private Handler fatherHandle;
    private Context mContext;
    private List<MyBlueToothDevice> mDeviceList = new ArrayList<>();

    /**蓝牙相关类**/
    private clientThread clientConnectThread = null;
    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    private readThread mreadThread = null;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /** 判断蓝牙是否打开
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public static boolean isOpen() {
        return isOpen;
    }

    /** 初始化系统，订阅相应频道的广播
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public void initBluetooth(){
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver,discoveryFilter);
        mContext.registerReceiver(mReceiver,foundFilter);
    }

    /**
     * 如果不需要操作界面，则直接使用 Context的构造函数即可
     * @param context   上下文，由于要使用到广播所以必须要传进来一个Context
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public MyBluetooth(Context context){
        mContext = context;
        initBluetooth();
        fatherHandle = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what== ComponentSetting.BLUETOOTH_LINK_SUCCESS_MESSAGE){
                    MyBluetooth.isOpen = true;
                    Log.d(TAG,"连接成功");
                }
                else if(msg.what==ComponentSetting.BLUETOOTH_LINK_WAITING_MESSAGE){
                    Log.d(TAG,(String)msg.obj);
                }
                else if(msg.what== ComponentSetting.BLUETOOTH_LINK_ERROR_MESSAGE){
                    MyBluetooth.isOpen = false;
                    Log.d(TAG,(String)msg.obj);
                }
                else if(msg.what==ComponentSetting.BLUETOOTH_MESSAGE_RECEIVER){
                    Bundle temp=(Bundle)msg.obj;
                    int[] receiver=temp.getIntArray("receiver");
                    byte[] ttemp=new byte[receiver.length];
                    for(int i=0;i<receiver.length;i++){
                        ttemp[i]=(byte)receiver[i];
                    }
                    Log.d(TAG,"Byte[] : "+ BluetoothTool.bytesToHexString(ttemp));
                    Log.d(TAG,"String : "+new String(ttemp));
                }
                super.handleMessage(msg);
            }
        };

    }

    /**
     * 如果不需要操作界面，则直接使用 Context的构造函数即可
     * @param context       上下文，由于要使用到广播所以必须要传进来一个Context
     * @param fatherHandle  如果需要和主线程有交互，需要在这里定义
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public MyBluetooth(Context context,Handler fatherHandle){
        mContext = context;
        initBluetooth();
        this.fatherHandle = fatherHandle;
    }

    /**
     * 获取曾配对的蓝牙设备
     * @return  一个设备的List或者null
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public List getPaireDevice(){
        List<MyBlueToothDevice> paireList = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                MyBlueToothDevice temp = new MyBlueToothDevice();
                temp.setBlueToothName(device.getName());
                temp.setBlueToothAddress(device.getAddress());
            }
            return paireList;
        } else {
            return null;
        }
    }

    /**
     * 设置需要连接的蓝牙信息与连接类型
     * @param BlueToothType     是否为蓝牙4.0
     * @param BlueToothName     蓝牙的名称（可以为空）
     * @param BlueToothAddress  蓝牙连接的唯一地址，不能为空
     * @return  返回设置成功与否
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean setBluetoothSetting(int BlueToothType,String BlueToothName,String BlueToothAddress){
        this.BlueToothType = BlueToothType;
        this.BlueToothAddress = BlueToothAddress;
        this.BlueToothAddress = BlueToothAddress;
        return true;
    }

    /**
     * 判断手机蓝牙是否打开
     * @return  打开则返回true
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean isPhoneBluetoothOPen(){
        return mBluetoothAdapter.enable();
    }

    /**
     * 启动默认的蓝牙连接，自动从BluetoothSetting中读取蓝牙信息
     * @return 函数调用是否成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean linkBluetooth(){
        if(this.BlueToothAddress.equals("null")){
            Log.d(TAG,"蓝牙地址为空，请重试");
            return false;
        }
        if(this.isOpen)
        {
            Log.d(TAG,"连接已经打开，可以通信。如果要再建立连接，请先断开！");
            return false;
        }
        else{
            String address = this.BlueToothAddress;
            if(!address.equals("null"))
            {
                device = mBluetoothAdapter.getRemoteDevice(address);
                clientConnectThread = new clientThread();
                clientConnectThread.start();
                this.isOpen = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 直接根据给予的蓝牙参数进行连接
     * @param BlueToothType     蓝牙设备类型
     * @param BlueToothName     蓝牙设备名称
     * @param BlueToothAddress  蓝牙设备地址
     * @return  是否连接成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean linkBluetooth(int BlueToothType,String BlueToothName,String BlueToothAddress){
        if(BlueToothAddress.equals("null")){
            Log.d(TAG,"蓝牙地址为空，请重试");
            return false;
        }
        if(this.isOpen)
        {
            Log.d(TAG,"连接已经打开，可以通信。如果要再建立连接，请先断开！");
            return false;
        }
        else{
            String address = BlueToothAddress;
            if(!address.equals("null"))
            {
                device = mBluetoothAdapter.getRemoteDevice(address);
                clientConnectThread = new clientThread();
                clientConnectThread.start();
                this.isOpen = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 直接根据给予的蓝牙参数进行连接
     * @param blueToothDevice     自定义蓝牙类型
     * @return  是否连接成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean linkBluetooth(MyBlueToothDevice blueToothDevice){
        if(blueToothDevice.getBlueToothAddress().equals("null")){
            Log.d(TAG,"蓝牙地址为空，请重试");
            return false;
        }
        if(this.isOpen)
        {
            Log.d(TAG,"连接已经打开，可以通信。如果要再建立连接，请先断开！");
            return false;
        }
        else{
            String address = blueToothDevice.getBlueToothAddress();
            if(!address.equals("null"))
            {
                device = mBluetoothAdapter.getRemoteDevice(address);
                clientConnectThread = new clientThread();
                clientConnectThread.start();
                this.isOpen = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 断开与蓝牙的连接
     * @return  是否连接成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public void disconnectBluetooth(){
        shutdownClient();
        this.isOpen = false;
    }

    /**
     * 向蓝牙发送信息
     * @param msg  发送信息的内容
     * @return  是否发送成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean sendMessage(byte[] msg){
        return this.sendMessageHandle(msg);
    }

    /**
     * 向蓝牙发送信息
     * @param msg  发送信息的内容
     * @return  是否发送成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public boolean sendMessage(String msg){
        return this.sendMessageHandle(msg.getBytes());
    }

    /**
     * 当不用蓝牙设备是可以直接调用
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public void onDestroy() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        disconnectBluetooth();
        mContext.unregisterReceiver(mReceiver);
    }

    /**HC-05 蓝牙连接相关函数，属于基类内容，勿动**/
    /* 发送数据线程 */
    private boolean sendMessageHandle(byte[] msg) {
        if (socket == null)
        {
            return false;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(msg);
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /* 读取数据线程 */
    public class readThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;
            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (true) {
                try {
                    if( (bytes = mmInStream.read(buffer)) > 0 )
                    {
                        int[] buf_data = new int[bytes];
                        for(int i=0; i<bytes; i++)
                        {
                            buf_data[i] = buffer[i];
                        }
                        Bundle data=new Bundle();
                        data.putIntArray("receiver",buf_data);
                        Message msg = new Message();
                        msg.obj = data;
                        msg.what = ComponentSetting.BLUETOOTH_MESSAGE_RECEIVER;
                        fatherHandle.sendMessage(msg);
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    /* 开启客户端线程 */
    public class clientThread extends Thread {
        public void run() {
            try {
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                //连接
                Message msg2 = new Message();
                msg2.obj = "请稍候，正在连接服务器:"+BlueToothAddress;
                msg2.what = ComponentSetting.BLUETOOTH_LINK_WAITING_MESSAGE;
                fatherHandle.sendMessage(msg2);
                socket.connect();

                Message msg = new Message();
                msg.obj = "已经连接上服务端！可以发送信息。";
                msg.what = ComponentSetting.BLUETOOTH_LINK_SUCCESS_MESSAGE;
                fatherHandle.sendMessage(msg);
                isOpen = true;
                //启动接受数据
                mreadThread = new readThread();
                mreadThread.start();
            }
            catch (IOException e)
            {
                Log.e("connect", "", e);
                Message msg = new Message();
                msg.obj = "连接服务端异常！断开连接重新试一试。";
                msg.what = ComponentSetting.BLUETOOTH_LINK_ERROR_MESSAGE;
                fatherHandle.sendMessage(msg);
                isOpen = false;
            }
        }
    };

    /* 停止客户端连接 */
    private void shutdownClient() {
        new Thread() {
            public void run() {
                if(clientConnectThread!=null)
                {
                    clientConnectThread.interrupt();
                    clientConnectThread= null;
                }
                if(mreadThread != null)
                {
                    mreadThread.interrupt();
                    mreadThread = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket = null;
                    isOpen = false;
                }
            };
        }.start();
    }

    /**蓝牙搜索相关函数**/
    public void searchDevices(){
        if(mBluetoothAdapter.isDiscovering())
        {
            //如果已经在搜索了，就取消搜索
            mBluetoothAdapter.cancelDiscovery();
        }
        else{
            mDeviceList.clear();
            mBluetoothAdapter.startDiscovery();
        }
    }

    /**
     * 设置广播监听，当搜索到设备后自动加入到List中
     * 本次扫描结束后，自动输出扫描到的蓝牙设备信息
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                MyBlueToothDevice temp = new MyBlueToothDevice();
                temp.setBlueToothAddress(device.getAddress());
                temp.setBlueToothName(device.getName());
                mDeviceList.add(temp);
                Log.d("HH", temp.toString());
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mDeviceList.size() == 0) {
                    Log.d("HH", "没有发现蓝牙设备");
                } else {
                    for (int i = 0; i < mDeviceList.size(); i++) {
                        Log.d("HH", mDeviceList.get(i).toString());
                    }
                }
            }
        }
    };

}
