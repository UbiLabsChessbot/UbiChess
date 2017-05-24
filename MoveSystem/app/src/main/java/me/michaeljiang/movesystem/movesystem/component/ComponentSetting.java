package me.michaeljiang.movesystem.movesystem.component;

/**
 * Created by MichaelJiang on 2017/5/20.
 */

public class ComponentSetting {
    /**MqttSetting**/
    public final static int MQTT_STATE_CONNECTED=1;
    public final static int MQTT_STATE_LOST=2;
    public final static int MQTT_STATE_FAIL=3;
    public final static int MQTT_STATE_RECEIVE=4;

    /**BluetoothSetting**/
    public static final int BLUETOOTH_TYPE_HC05 = 5;
    public static final int BLUETOOTH_TYPE_4 = 6;
    public static final int BLUETOOTH_LINK_ERROR_MESSAGE = 7;
    public static final int BLUETOOTH_LINK_SUCCESS_MESSAGE = 8;
    public static final int BLUETOOTH_LINK_WAITING_MESSAGE = 9;
    public static final int BLUETOOTH_MESSAGE_RECEIVER = 10;

}
