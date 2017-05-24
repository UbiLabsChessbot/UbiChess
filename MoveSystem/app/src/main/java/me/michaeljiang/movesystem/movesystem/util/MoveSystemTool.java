package me.michaeljiang.movesystem.movesystem.util;

/**
 * Created by MichaelJiang on 2017/5/15.
 */

public class MoveSystemTool {
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
}
