package me.michaeljiang.movesystem.movesystem.component.arm7bot.util;


/**
 * Created by Michael on 2016/11/7 0007.
 * Arm7Bot所用到的通用工具
 */

public class ArmTools {

    /***
     * 将byte数组以byte16进制字符串的形式输出
     * @param bytes     254
     * @return          0xFE
     */
    public static String bytesToHexString(byte[] bytes) {
        //将Byte数组转换成字符串
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if(hexString.equals("0")){
                hexString="00";
            }
            if(hexString.length()==1){
                hexString = "0" + hexString;
            }
            result += hexString.toUpperCase()+"  ";
        }
        return result;
    }

    /**
     * 将int类型的Byte数组依照文档转换成相应的Byte  0-10000
     * @param data
     * @return
     */
    public static byte[] motoToBytes(int[] data){
        byte[] motoData = new byte[14];
        for(int i = 0; i < 7; i++){
            motoData[2*(i)]=(byte) (data[i]/128);
            motoData[2*(i)+1]=(byte) ((byte)(data[i]%128));
        }
        return motoData;
    }

    /**
     * 将StartByte和EndByte拼接在一起
     * @param start     (byte)0xfe,(byte)0xFA
     * @param end       0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00, 0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48
     * @return          0xFE  0xFA  0x08  0x00  0x01  0x2F  0x00  0x64  0x08  0x00  0x08  0x00  0x09  0x44  0x01  0x48  0x01  0x48  0x01  0x48  0x01  0x48
     */
    public static byte[] spliceBytes(byte[] start,byte[] end){
        int maxLength = start.length+end.length;
        byte[] result = new byte[maxLength];
        int position = 0;
        for(int i = 0; i < start.length; i++){
            result[position++] = start[i];
        }

        for(int i = 0; i < end.length; i++){
            result[position++] = end[i];
        }
        return result;
    }

}
