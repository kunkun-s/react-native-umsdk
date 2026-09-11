package com.kk.rnumsdk;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.tencent.tauth.Tencent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.common.ResContainer;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.util.Map;

public class RNUMShare {
    private static Handler mSDKHandler = null;

    //发起分享/授权时都是通过参数传入当前Activity，这里不再持有Context，避免静态引用造成泄漏
    public RNUMShare() {
    }

    private static void runOnMainThread(Runnable runnable) {
        if (mSDKHandler == null){
            mSDKHandler = new Handler(Looper.getMainLooper());
        }
        mSDKHandler.postDelayed(runnable, 0);
    }



    /**
     * 配置分享平台
     * @param platforms
     */
    public static void setUMSharePlatforms(final Map<String,Map<String, String>>platforms) {
        //配置分享目标平台
        if (platforms != null && !platforms.isEmpty()){
            String provider = null;
            if( platforms.get("WX") != null ){
                Map<String,String> wxMap = platforms.get("WX");
                if (wxMap != null ){
                    if ( wxMap.get("provider") != null){
                        provider = wxMap.get("provider");
                    }
                    // 微信设置
                    if (wxMap.get("appID") != null && wxMap.get("appKey") != null){
                        PlatformConfig.setWeixin(wxMap.get("appID"),wxMap.get("appKey"));

                    }
                }

            }
            if ( platforms.get("QQ") != null) {
                Map<String,String> qqMap = platforms.get("QQ");
                if (qqMap != null){
                    if (qqMap.get("provider") != null){
                        provider = qqMap.get("provider");
                    }
                    if (qqMap.get("appID")!=null && qqMap.get("appKey")!=null){
                        // QQ设置
                        PlatformConfig.setQQZone(qqMap.get("appID"),qqMap.get("appKey"));
                    }

                }

            }
            if (provider!= null){
                PlatformConfig.setFileProvider(provider);
            }
        }

    }

    /**
     * 获取图片
     * @param url
     * @return
     */
    private UMImage getImage(Activity ma, String url){

        if (TextUtils.isEmpty(url)){
            return null;
        }else if (ma != null){
            if(url.startsWith("http")){
                return new UMImage(ma,url);
            }else if(url.startsWith("/")){
                return new UMImage(ma,url);
            }else if(url.startsWith("res")){
                return new UMImage(ma, ResContainer.getResourceId(ma,"drawable",url.replace("res/","")));
            }
        }


        return new UMImage(ma,url);
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

        String uri = newParams.getString("poster").replaceFirst("https","http");
        UMImage image = null;
        UMImage thumb = null;
        if(uri.toString().indexOf("file:///") != -1){
            File file = new File(uri.toString().replaceFirst("file:///","/"));
            image =  new UMImage(ma,file);
            thumb = new UMImage(ma,file);
        }else {
            image = new UMImage(ma,uri);
            thumb = new UMImage(ma,uri);
        }
        thumb.compressStyle = UMImage.CompressStyle.SCALE;
        image.setThumb(thumb);

        new ShareAction(ma)
                .withText("点到")
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
        //否则统一都是网址分享
        UMWeb web = new UMWeb(newParams.getString("t_url"));
        web.setTitle(newParams.getString("title"));
        web.setDescription(newParams.getString("content"));
        if (newParams.hasKey("img_path")){
            if (newParams.getString("img_path") != null){
                web.setThumb(getImage(ma,newParams.getString("img_path").replaceFirst("https","http")));
            }

        }else {
            //获取主程序的R.drawable.share文件，如果为空，则使用当前com.kk.rnumsdk包的R.drawable.share
            int nShare = RNUmsdkImpl.AppResource.getDrawableResourceId("share");
            if (nShare == 0){
                nShare = R.drawable.share;
            }
            web.setThumb(new UMImage(ma,nShare));
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

        //分享微信小程序 t_url兼容低版本的网页链接
        UMMin umMin = new UMMin(newParams.getString("t_url"));
        // 小程序消息封面图片
        umMin.setThumb(getImage(ma, newParams.getString("img_path").replaceFirst("https","http"))); //img.diandao.org ssl证书问题
        // 小程序消息title
        umMin.setTitle(newParams.getString("title"));
        // 小程序消息描述 "pages/page10007/xxxxxx"
        umMin.setDescription(newParams.getString("content"));
        //小程序页面路由路径
        umMin.setPath(newParams.getString("path"));
        // 小程序原始id,在微信平台查询
        umMin.setUserName(newParams.getString("userName"));
        new ShareAction(ma)
                .withMedia(umMin)
                .setPlatform(share_media)
                .share();

    }


    /**
     * 调起无UI 分享（不返回回调）
     * @param ma 发起分享的activity
     * @param platformType 1微信聊天 2微信朋友圈 4qq
     * @param shareType Image / MiniProgram / 其它(网页)
     * @param params 分享参数
     */
    public void shareToPlatform(Activity ma, final Integer platformType, final String shareType, final ReadableMap params){
        if (ma == null){
            return;
        }
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ReadableMap newParams = (ReadableMap)params;
                   SHARE_MEDIA share_media = platformType(platformType);

                    if ( share_media != null && params != null ){
                        if (shareType.equals("MiniProgram")){
                            shareWXMiniProgram(newParams,share_media,ma);

                        } else if ( shareType.equals("Image") ) {
                            shareImage(newParams,share_media,ma);


                        } else {
                            shareWeb(newParams,share_media,ma);

                        }
                    }
                }catch (Exception e){
                    //分享不返回回调，失败只能在这里留痕
                    Log.e("RNUMShare", "shareToPlatform failed, platformType=" + platformType + " shareType=" + shareType, e);
                }

            }
        });
    }

    /**
     * 第三方授权登录
     * @param platformType
     * @param successCallback
     */
    public void auth(Activity activity, final int  platformType, final Callback successCallback){
        if (activity == null){
            return;
        }
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                int n_p = platformType;
                if (n_p == 2 || n_p == 1){
                    n_p = 1;//1、2都是微信登录
                }
                UMShareAPI.get(activity).getPlatformInfo(activity, platformType(n_p), new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        WritableMap result = Arguments.createMap();
                        for (String key:map.keySet()){
                            result.putString(key,map.get(key));
                        }
                        if (successCallback != null) {
                            successCallback.invoke(200,result,"success");
                        }
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

    /**
     * 是否安装目标APP
     * @param platform "QQ" / "WX_LINE"(朋友圈) / "WX_SESSION"(聊天)
     */
    public void isInstall(Activity ma, String platform, Callback callback){
        SHARE_MEDIA share_media = null;
        boolean isInstall = false;
        if ( "QQ".equals(platform) ){
            Tencent.setIsPermissionGranted(true);
            share_media = SHARE_MEDIA.QQ;

        }else if ( "WX_LINE".equals(platform) ){
            share_media = SHARE_MEDIA.WEIXIN_CIRCLE;

        }else if ( "WX_SESSION".equals(platform) ){
            share_media = SHARE_MEDIA.WEIXIN;

        }
        if (share_media != null && ma != null ){
            try {

                isInstall =  UMShareAPI.get(ma).isInstall(ma,share_media);
            }catch (Exception e){
                isInstall = false;
            }

        }
        if (callback != null) {
            callback.invoke(isInstall);
        }
    };


}
