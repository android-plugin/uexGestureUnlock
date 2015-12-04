package org.zywx.wbpalmstar.plugin.uexgestureunlock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.fragment.GestureCreateFragment;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.fragment.GestureVerifyFragment;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ConfigGestureVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.CreateGestureVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ResultEventVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ResultFailedVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ResultIsGestureSetVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ResultVerifyVO;

public class EUExGestureUnlock extends EUExBase {

    private static final String BUNDLE_DATA = "data";
    private static final int MSG_IS_GESTURE_CODE_SET = 1;
    private static final int MSG_RESET_GESTURE_CODE = 2;
    private static final int MSG_CONFIG = 3;
    private static final int MSG_VERIFY = 4;
    private static final int MSG_CREATE = 5;
    private static final int MSG_CANCEL = 6;
    private static final String PRES_KEY = "uexGestureUnlock_data_code";
    private static final String PRES_DATA_GESTURE = "uexGestureUnlock_data_code";
    private static final String TAG_GESTURE_VERIFY = "uexGestureUnlock_verify";
    private static final String TAG_GESTURE_CREATE = "uexGestureUnlock_create";
    private GestureVerifyFragment mGestureVerifyFragment;
    private GestureCreateFragment mGestureCreateFragment;
    private SharedPreferences mPres;
    private ConfigGestureVO mData;

    public EUExGestureUnlock(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
        mPres = mContext.getSharedPreferences(PRES_KEY, Activity.MODE_PRIVATE);
    }

    @Override
    protected boolean clean() {
        return false;
    }


    public void isGestureCodeSet(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_IS_GESTURE_CODE_SET;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void isGestureCodeSetMsg() {
        boolean isGestureCodeSet = !TextUtils.isEmpty(getGestureData());
        ResultIsGestureSetVO result = new ResultIsGestureSetVO();
        result.setResult(isGestureCodeSet);
        callBackPluginJs(JsConst.CALLBACK_IS_GESTURE_CODE_SET, DataHelper.gson.toJson(result));
    }

    public void resetGestureCode(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_RESET_GESTURE_CODE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void resetGestureCodeMsg() {
        mPres.edit().remove(PRES_DATA_GESTURE).commit();
    }

    public void config(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CONFIG;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void configMsg(String[] params) {
        String json = params[0];
        if (TextUtils.isEmpty(json)) return;
        mData = DataHelper.gson.fromJson(json, ConfigGestureVO.class);
        if (!TextUtils.isEmpty(mData.getBackgroundImage())){
            String path = BUtility.makeRealPath(
                    BUtility.makeUrl(mBrwView.getCurrentUrl(), mData.getBackgroundImage()),
                    mBrwView.getCurrentWidget().m_widgetPath,
                    mBrwView.getCurrentWidget().m_wgtType);
            mData.setBackgroundImage(path);
        }
        if (!TextUtils.isEmpty(mData.getIconImage())){
            String path = BUtility.makeRealPath(
                    BUtility.makeUrl(mBrwView.getCurrentUrl(), mData.getIconImage()),
                    mBrwView.getCurrentWidget().m_widgetPath,
                    mBrwView.getCurrentWidget().m_wgtType);
            mData.setIconImage(path);
        }
    }

    public void verify(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_VERIFY;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void verifyMsg() {
        String gestureCode = getGestureData();
        if (TextUtils.isEmpty(gestureCode)){
            ResultFailedVO result = new ResultFailedVO();
            result.setErrorCode(JsConst.ERROR_CODE_NONE_GESTURE);
            result.setErrorString(EUExUtil
                    .getString("plugin_uexGestureUnlock_errorCodeNoneGesture"));
            callBackVerify(result);
            return;
        }
        openVerifyGestureLayout(false, gestureCode);

    }

    public void create(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CREATE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void createMsg(String[] params) {
        boolean isNeedVerifyBeforeCreate = true;
        if (params != null && params.length > 0) {
            CreateGestureVO dataVO = DataHelper.gson.fromJson(params[0], CreateGestureVO.class);
            isNeedVerifyBeforeCreate = dataVO.isNeedVerifyBeforeCreate();
        }
        if (isNeedVerifyBeforeCreate && !TextUtils.isEmpty(getGestureData())){
            openVerifyGestureLayout(true, getGestureData());
        }else{
            openCreateGestureLayout();
        }
    }

    private void openVerifyGestureLayout(boolean isHasCreate, String gestureCode) {
        if (mGestureVerifyFragment != null){
            closeVerifyFragment();
        }
        mGestureVerifyFragment = new GestureVerifyFragment();
        mGestureVerifyFragment.setGestureVerifyListener(mGestureVerifyListener);
        mGestureVerifyFragment.setGestureCodeData(gestureCode);
        mGestureVerifyFragment.setData(mData);
        mGestureVerifyFragment.setIsHasCreate(isHasCreate);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addFragmentToCurrentWindow(mGestureVerifyFragment, lp, TAG_GESTURE_VERIFY);
    }

    private void openCreateGestureLayout() {
        if (mGestureVerifyFragment != null){
            closeCreateFragment();
        }
        mGestureCreateFragment = new GestureCreateFragment();
        mGestureCreateFragment.setGestureCreateListener(mGestureCreateListener);
        mGestureCreateFragment.setData(mData);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addFragmentToCurrentWindow(mGestureCreateFragment, lp, TAG_GESTURE_CREATE);
    }

    public void cancel(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CANCEL;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void cancelMsg() {
        if (mGestureVerifyFragment != null){
            closeVerifyFragment();
            ResultFailedVO result =new ResultFailedVO();
            result.setIsFinished(false);
            result.setErrorCode(JsConst.ERROR_CODE_CANCEL_OUTSIDE);
            result.setErrorString(EUExUtil
                    .getString("plugin_uexGestureUnlock_errorCodeCancelOutside"));
            callBackVerify(result);
        }
        if (mGestureCreateFragment != null){
            closeCreateFragment();
            ResultFailedVO result =new ResultFailedVO();
            result.setIsFinished(false);
            result.setErrorCode(JsConst.ERROR_CODE_CANCEL_OUTSIDE);
            result.setErrorString(EUExUtil
                    .getString("plugin_uexGestureUnlock_errorCodeCancelOutside"));
            callBackCreate(result);
        }
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case MSG_IS_GESTURE_CODE_SET:
                isGestureCodeSetMsg();
                break;
            case MSG_RESET_GESTURE_CODE:
                resetGestureCodeMsg();
                break;
            case MSG_CONFIG:
                configMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_VERIFY:
                verifyMsg();
                break;
            case MSG_CREATE:
                createMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CANCEL:
                cancelMsg();
                break;
            default:
                super.onHandleMessage(message);
        }
    }

    private void callBackPluginJs(String methodName, String jsonData){
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }

    private GestureVerifyListener mGestureVerifyListener = new GestureVerifyListener() {
        @Override
        public void onVerifyResult(ResultVerifyVO result) {
            callBackVerify(result);
        }

        @Override
        public void closeLayout() {
            closeVerifyFragment();
        }

        @Override
        public void onStartCreate() {
            openCreateGestureLayout();
        }

        @Override
        public void onCancel() {
            ResultFailedVO result = new ResultFailedVO();
            result.setIsFinished(false);
            result.setErrorCode(JsConst.ERROR_CODE_CANCEL_VERIFY);
            result.setErrorString(EUExUtil
                    .getString("plugin_uexGestureUnlock_errorCodeCancelVerify"));
            callBackVerify(result);

            callBackEvent(JsConst.EVENT_CANCEL_VERIFY);
            this.closeLayout();
        }

        @Override
        public void onEventOccur(int eventCode) {
            callBackEvent(eventCode);
        }
    };

    private void callBackEvent(int code){
        ResultEventVO result = new ResultEventVO();
        result.setEventCode(code);
        callBackPluginJs(JsConst.ON_EVENT_OCCUR, DataHelper.gson.toJson(result));
    }

    private void callBackVerify(ResultVerifyVO result) {
        callBackPluginJs(JsConst.CALLBACK_VERIFY, DataHelper.gson.toJson(result));
    }
    private void callBackCreate(ResultFailedVO result) {
        callBackPluginJs(JsConst.CALLBACK_CREATE, DataHelper.gson.toJson(result));
    }

    private GestureCreateListener mGestureCreateListener = new GestureCreateListener() {
        @Override
        public void onCreateFinished(String gestureCode) {
            ResultVerifyVO result;
            if (!TextUtils.isEmpty(gestureCode)){
                setGestureData(gestureCode);
                result = new ResultVerifyVO();
                result.setIsFinished(true);
            }else {
                result = new ResultFailedVO();
                result.setIsFinished(false);
            }
            callBackPluginJs(JsConst.CALLBACK_CREATE, DataHelper.gson.toJson(result));
            closeCreateFragment();
        }

        @Override
        public void onCreateFailed(ResultVerifyVO result) {

        }

        @Override
        public void closeLayout() {
            closeCreateFragment();
        }

        @Override
        public void onCancel() {
            ResultFailedVO result = new ResultFailedVO();
            result.setIsFinished(false);
            result.setErrorCode(JsConst.ERROR_CODE_CANCEL_CREATE);
            result.setErrorString(EUExUtil
                    .getString("plugin_uexGestureUnlock_errorCodeCancelCreate"));
            callBackCreate(result);
            callBackEvent(JsConst.EVENT_CANCEL_CREATE);
            this.closeLayout();
        }

        @Override
        public void onEventOccur(int eventCode) {
            callBackEvent(eventCode);
        }
    };

    public interface GestureVerifyListener{
        public void onVerifyResult(ResultVerifyVO result);
        public void closeLayout();
        public void onStartCreate();
        public void onCancel();
        public void onEventOccur(int eventCode);
    }

    public interface GestureCreateListener{
        public void onCreateFinished(String gestureCode);
        public void onCreateFailed(ResultVerifyVO result);
        public void closeLayout();
        public void onCancel();
        public void onEventOccur(int eventCode);
    }

    private String getGestureData(){
        return mPres.getString(PRES_DATA_GESTURE, "");
    }

    private void setGestureData(String code){
        SharedPreferences.Editor editor = mPres.edit();
        editor.putString(PRES_DATA_GESTURE, code);
        editor.commit();
    }

    private void closeCreateFragment() {
        if (mGestureCreateFragment != null){
            removeFragmentFromWindow(mGestureCreateFragment);
            mGestureCreateFragment = null;
        }
    }

    private void closeVerifyFragment() {
        if (mGestureVerifyFragment != null){
            removeFragmentFromWindow(mGestureVerifyFragment);
            mGestureVerifyFragment = null;
        }
    }
}
