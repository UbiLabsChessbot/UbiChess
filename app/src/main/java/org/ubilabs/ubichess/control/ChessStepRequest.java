package org.ubilabs.ubichess.control;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ChessStepRequest extends AsyncTask<String, Integer, String> {
    private static final String TAG = ChessStepRequest.class.getSimpleName();

    @Override
    protected String doInBackground(String... params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "http://192.168.3.13:8080?move=" + params[0];
        Log.e(TAG, "url: " + url);
        String ret = "Request Failed";
        try {
            HttpGet httpGet = new HttpGet(url);

            CloseableHttpResponse response = httpClient.execute(httpGet);
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
