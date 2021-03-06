package com.whf.messagerelayer.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.whf.messagerelayer.confing.Constant;
import com.whf.messagerelayer.utils.EmailRelayerManager;
import com.whf.messagerelayer.utils.NativeDataManager;

import java.util.Set;

public class SmsService extends IntentService {

    private NativeDataManager mNativeDataManager;

    public SmsService() {
        super("SmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNativeDataManager = new NativeDataManager(this);

        String mobile = intent.getStringExtra(Constant.EXTRA_MESSAGE_MOBILE);
        String content = intent.getStringExtra(Constant.EXTRA_MESSAGE_CONTENT);
        Set<String> keySet = mNativeDataManager.getKeywordSet();

        //没有配置转发规则，转发所有
        if (keySet.size() == 0) {
            relayMessage(content, mobile);
        } else {
            // 仅支持关键字规则
            for (String key : keySet) {
                if (content.contains(key)) {
                    relayMessage(content, mobile);
                    return;
                }
            }
        }
    }

    private void relayMessage(String content, String mobile) {
        // 仅支持邮件转发
        if (mNativeDataManager.getEmailRelay()) {
            EmailRelayerManager.relayEmail(mNativeDataManager, content, mobile);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
