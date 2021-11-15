package com.kk.RNUMSDK;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Switch;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.common.ResContainer;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMWeb;

import java.util.Map;

public class RNUMShare extends ReactContextBaseJavaModule {
    private static Handler mainHandler = new Handler(Looper.getMainLooper());


    public RNUMShare(ReactApplicationContext reactContext) {
        super(reactContext);
//        this.reactContext = reactContext;
    }

    /**
     * 配置分享平台
     * @param platforms
     */
    public static void setUMSharePlatforms(final Map<String,Map>platforms) {
        //配置分享目标平台
        if (platforms != null && !platforms.isEmpty()){

            if( platforms.get("WX") != null ){
                Map<String,String> wxMap = platforms.get("WX");
                // 微信设置
                PlatformConfig.setWeixin(wxMap.get("appID"),wxMap.get("appKey"));
                PlatformConfig.setWXFileProvider(wxMap.get("provider"));
            }
            if ( platforms.get("QQ") != null) {
                Map<String,String> qqMap = platforms.get("QQ");
                // QQ设置
                PlatformConfig.setQQZone(qqMap.get("appID"),qqMap.get("appKey"));
                PlatformConfig.setQQFileProvider(qqMap.get("provider"));

            }
        }

    }

    /**
     * 获取图片
     * @param url
     * @return
     */
    private UMImage getImage(String url){
        if (TextUtils.isEmpty(url)){
            return null;
        }else if(url.startsWith("http")){
            return new UMImage(getCurrentActivity(),url);
        }else if(url.startsWith("/")){
            return new UMImage(getCurrentActivity(),url);
        }else if(url.startsWith("res")){
            return new UMImage(getCurrentActivity(), ResContainer.getResourceId(getCurrentActivity(),"drawable",url.replace("res/","")));
        }else {
            return new UMImage(getCurrentActivity(),url);
        }
    }

    /**
     * 映射 UMSDK 的分享平台
     * 避免UM官方改变分享平台参数、Android、Web、iOS等多端实现后端控制分享平台数据一致。
     * @param platformType
     * @return
     */
    private SHARE_MEDIA platformType(final Integer platformType){
        SHARE_MEDIA share_media = SHARE_MEDIA.WEIXIN;
        if (2 == platformType){
            //朋友圈
            share_media = SHARE_MEDIA.WEIXIN_CIRCLE;
        }else if (4 == platformType){
            //QQ
            share_media = SHARE_MEDIA.QQ;
        }else if (1 == platformType){
            //微信聊天窗口
            share_media = SHARE_MEDIA.WEIXIN;
        }
        return share_media;
    }

    /**
     * 纯图片分享
     * @param newParams
     * @param share_media
     * @param ma
     */
    private void shareImage(ReadableMap newParams, SHARE_MEDIA share_media, Activity ma){
        UMImage image = new UMImage(ma,newParams.getString("poster"));//网络图
        UMImage thumb =  new UMImage(ma, newParams.getString("poster"));//缩略图
        thumb.compressStyle = UMImage.CompressStyle.SCALE;
        image.setThumb(thumb);

        new ShareAction(ma)
                .withMedia(image)
                .setPlatform(share_media)
                .share();
    }

    /**
     * 网页分享
     * @param newParams 参数
     * @param share_media 平台
     * @param ma 那个activity发起，
     */
    private void shareWeb(ReadableMap newParams, SHARE_MEDIA share_media, Activity ma){
        String urlStr = "http://www.diandao.org";
        String title = "点到上门按摩服务！";
        String content = "小伙伴快来按摩吧！";
        if (newParams.hasKey("t_url") && newParams.getString("t_url") != null && !"".equals(newParams.getString("t_url"))){
            urlStr = newParams.getString("t_url");
        }
        if (newParams.hasKey("title") && newParams.getString("title") != null && !"".equals(newParams.getString("title"))){
            title = newParams.getString("title");
        }
        if (newParams.hasKey("content") && newParams.getString("content") != null && !"".equals(newParams.getString("content"))){
            content = newParams.getString("content");
        }
        UMWeb web = new UMWeb(urlStr);
        web.setTitle(title);
        web.setDescription(content);
        if (newParams.hasKey("img_path")){
            web.setThumb(getImage(newParams.getString("img_path")));
        }else {
            web.setThumb(new UMImage(ma, R.drawable.share));
        }
        new ShareAction(ma)
                .withMedia(web)
                .setPlatform(share_media)
                .share();
    }

    /**
     * 微信小程序
     * @param newParams
     * @param share_media
     * @param ma
     */
    private void shareWXMiniProgram(ReadableMap newParams, SHARE_MEDIA share_media, Activity ma){
        UMWeb web = new UMWeb(newParams.getString("t_url"));
        web.setTitle(newParams.getString("title"));
        web.setDescription(newParams.getString("content"));
        if (newParams.hasKey("img_path")){
            web.setThumb(getImage(newParams.getString("img_path")));
        }else {
            web.setThumb(new UMImage(ma, R.drawable.share));
        }
        new ShareAction(ma)
                .withMedia(web)
                .setPlatform(share_media)
                .share();
    }

    /**
     * 分享纯文本，qq不支持纯文本
     * @param newParams
     * @param share_media
     * @param ma
     */
    private void shareText(ReadableMap newParams, SHARE_MEDIA share_media, Activity ma){


        if (share_media == SHARE_MEDIA.QQ){
            shareWeb(newParams, share_media, ma);
        } else {
            String text = "分享默认文案";
            if (newParams.getString("title") != null && !"".equals(newParams.getString("title"))){
                text = newParams.getString("title");
            }
            new ShareAction(ma)
                    .withText(text)
                    .setPlatform(share_media)
                    .share();
        }

    }
    @Override
    public String getName() {
        return "RNUMShare";
    }

    /**
     * 调起无UI 分享
     * @param platformType
     * @param shareType
     * @param params
     * @param successCallback
     */
    @ReactMethod
    public void shareToPlatform(final Integer platformType, final String shareType, final ReadableMap params, final Callback successCallback){

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ReadableMap newParams = (ReadableMap)params;
                    SHARE_MEDIA share_media = platformType(platformType);

                    /**
                     * ma
                     * 调起分享的Activity
                     * 如果需要回调，则需要将回调方法实现在Activity ma中,(例如这里的ma为mainActivity，则回调就卸载mainActivity中)
                     * 这里没有使用mainActivity，因为不需要回调
                     */
                    Activity ma = getCurrentActivity();
                    if ( share_media != null && params != null ){
                        if (shareType.equals("MiniProgram") && share_media == SHARE_MEDIA.WEIXIN){
//                                if (successCallback != null){
//                                    successCallback.invoke(101,"WX");
//                                }
                            //分享微信小程序
                           shareWXMiniProgram(newParams, share_media, ma);

                        } else if (shareType.equals("Image")){
                            //分享图片
//                                if (successCallback !=null){
//                                    successCallback.invoke(101,"WXC");
//                                }
                            shareImage(newParams, share_media, ma);
                        } else if (newParams.getString("t_url") != null && !"".equals(newParams.getString("t_url"))){
//                                if (successCallback != null){
//                                    successCallback.invoke(101,"QQ");
//                                }
                            //否则统一都是网址分享
                          shareWeb(newParams, share_media, ma);
                        } else {
                            //qq不支持纯文本
                            shareText(newParams, share_media, ma);
                            String text = "分享给大家！";
                            if (newParams.getString("title") != null && !"".equals(newParams.getString("title"))){
                                text = newParams.getString("title");
                            }
                            new ShareAction(ma)
                                    .withText(text)
                                    .setPlatform(share_media)
                                    .share();
                        }
                    }
                }catch (Exception e){

                }

            }
        });

    }

    /**
     * 第三方授权登录
     * @param platformType
     * @param successCallback
     */
    @ReactMethod
    public void auth(final int  platformType, final Callback successCallback){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UMShareAPI.get(getCurrentActivity()).getPlatformInfo(getCurrentActivity(), platformType(platformType), new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        WritableMap result = Arguments.createMap();
                        for (String key:map.keySet()){
                            result.putString(key,map.get(key));
                            Log.e("todoremove","key="+key+"   value"+map.get(key).toString());
                        }
                        successCallback.invoke(200,result,"success");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        WritableMap result = Arguments.createMap();
                        successCallback.invoke(1,result,throwable.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        WritableMap result = Arguments.createMap();
                        successCallback.invoke(2,result,"cancel");
                    }
                });
            }
        });

    }

}
