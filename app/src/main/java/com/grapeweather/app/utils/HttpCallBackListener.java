package com.grapeweather.app.utils;

/**
 * Created by fancheng on 2016/6/21.
 */
public interface HttpCallBackListener {
    void onFinish(String response);

    void onError(Exception e);
}
