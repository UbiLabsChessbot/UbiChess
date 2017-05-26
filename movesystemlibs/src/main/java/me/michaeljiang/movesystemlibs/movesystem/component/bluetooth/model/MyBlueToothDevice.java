package me.michaeljiang.movesystemlibs.movesystem.component.bluetooth.model;

/**
 * Created by MichaelJiang on 2017/5/10.
 */

public class MyBlueToothDevice {
    private  String BlueToothName = "null";
    private  String BlueToothAddress = "null";

    public String getBlueToothName() {
        return BlueToothName;
    }

    public void setBlueToothName(String blueToothName) {
        BlueToothName = blueToothName;
    }

    public String getBlueToothAddress() {
        return BlueToothAddress;
    }

    public void setBlueToothAddress(String blueToothAddress) {
        BlueToothAddress = blueToothAddress;
    }

    public String toString(){
        return "BluetoothName ; "+BlueToothName+"     BluetoothAddress  :  "+BlueToothAddress;
    }

}
