package org.ubilabs.ubichess.uitl;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImgUtils {
    private static final String TAG = ImgUtils.class.getSimpleName();

    public static File mat2PngFile(Mat mat) {
        Bitmap bmp = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        Log.e(TAG, "Transform: finish! ");

        File file = null;
        FileOutputStream fos;
        try {
            file = new File(Environment.getExternalStorageDirectory() + "/Chess/Chess" + ".png");
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
