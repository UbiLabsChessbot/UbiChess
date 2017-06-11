package org.ubilabs.ubichess.control;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ChessTypeRequest extends AsyncTask<File, Integer, String> {
    private static final String TAG = ChessTypeRequest.class.getSimpleName();

    @Override
    protected String doInBackground(File... params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://192.168.2.52:5000/upload";
        String ret = "No Data";
        try {
            HttpPost httppost = new HttpPost(url);

            FileBody bin = new FileBody(params[0]);

            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).build();

            httppost.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(httppost);
            try {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    ret = EntityUtils.toString(resEntity, "utf-8");
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    //在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPostExecute(String result) {

    }

    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
    }
}
