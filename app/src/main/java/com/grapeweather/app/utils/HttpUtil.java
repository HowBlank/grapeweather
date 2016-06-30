package com.grapeweather.app.utils;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fancheng on 2016/6/21.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallBackListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream in = null;
                BufferedReader bf = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(10000);
                    in = connection.getInputStream();
                    bf = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    StringBuilder response = new StringBuilder();
                    while ((line = bf.readLine())!= null){
                        response.append(line);
                    }
                    if(listener != null){
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if(listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if(in != null){
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(bf!=null){
                        try {
                            bf.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection != null){
                        connection.disconnect();
                    }

                }
            }
        }).start();

    }
}
