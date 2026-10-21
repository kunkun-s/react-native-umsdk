package com.kk.rnumsdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
     * @param platformType 1微信聊天 2微信朋友圈 4qq
     * @param share_media
     * @param ma
     */
    private void shareWXMiniProgram(ReadableMap newParams, int platformType, SHARE_MEDIA share_media, Activity ma){
        int miniProgramType = getMiniProgramType(newParams);
        String appId = getWXAppId();
        //友盟UMMin不支持指定小程序版本（默认就是正式版），只有指定开发/体验版且是微信平台时才直接调微信SDK
        if ((platformType == 1 || platformType == 2) && !TextUtils.isEmpty(appId) && miniProgramType != 0){
            shareWXMiniProgramDirect(newParams, platformType, appId, miniProgramType, ma);
            return;
        }

        //其他平台（QQ）没有小程序版本的概念，仍走友盟
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
     * 直接调微信SDK分享小程序，支持miniProgramType（0正式 1开发 2体验）
     */
    private void shareWXMiniProgramDirect(ReadableMap newParams, int platformType, String appId, int miniProgramType, Activity ma){
        final Context context = ma.getApplicationContext();
        final IWXAPI api = WXAPIFactory.createWXAPI(context, appId, false);
        api.registerApp(appId);

        final WXMiniProgramObject miniProgram = new WXMiniProgramObject();
        //小程序原始id,在微信平台查询
        miniProgram.userName = newParams.getString("userName");
        //小程序页面路由路径
        miniProgram.path = newParams.getString("path");
        //兼容低版本微信的网页链接
        miniProgram.webpageUrl = newParams.hasKey("t_url") && !newParams.isNull("t_url") ? newParams.getString("t_url") : "http://www.diandao.org/";
        miniProgram.withShareTicket = true;
        //小程序类型 0正式 1开发 2体验
        miniProgram.miniprogramType = miniProgramType;

        final WXMediaMessage message = new WXMediaMessage(miniProgram);
        message.title = newParams.getString("title");
        message.description = newParams.getString("content");

        final String imgPath = newParams.hasKey("img_path") && !newParams.isNull("img_path") ? newParams.getString("img_path") : null;
        //获取主程序的R.drawable.share文件，如果为空，则使用当前com.kk.rnumsdk包的R.drawable.share
        int nShare = RNUmsdkImpl.AppResource != null ? RNUmsdkImpl.AppResource.getDrawableResourceId("share") : 0;
        final int fallbackThumb = nShare != 0 ? nShare : R.drawable.share;
        final int scene = platformType == 2 ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        //缩略图需要下载压缩，放子线程处理，避免主线程网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                message.thumbData = loadMiniProgramThumb(context, imgPath, fallbackThumb);
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = String.valueOf(System.currentTimeMillis()) + "_MiniProgram";
                        req.message = message;
                        req.scene = scene;
                        if (!api.sendReq(req)){
                            Log.e("RNUMShare", "shareWXMiniProgram sendReq failed");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 微信小程序的appid，友盟配置微信时写入PlatformConfig
     */
    private String getWXAppId(){
        try {
            PlatformConfig.Platform platform = PlatformConfig.getPlatform(SHARE_MEDIA.WEIXIN);
            if (platform != null){
                return platform.getAppid();
            }
        } catch (Exception e){
            Log.w("RNUMShare", "getWXAppId failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * 小程序类型 "0"正式(默认) "1"开发 "2"体验，非法值按正式处理，兼容JS传字符串或数字
     */
    private int getMiniProgramType(ReadableMap params){
        int type = 0;
        try {
            if (params != null && params.hasKey("miniProgramType") && !params.isNull("miniProgramType")){
                ReadableType valueType = params.getType("miniProgramType");
                if (valueType == ReadableType.Number){
                    type = params.getInt("miniProgramType");
                } else if (valueType == ReadableType.String){
                    type = Integer.parseInt(params.getString("miniProgramType").trim());
                }
            }
        } catch (Exception e){
            Log.w("RNUMShare", "invalid miniProgramType, fallback to release");
        }
        if (type < 0 || type > 2){
            type = 0;
        }
        return type;
    }

    /**
     * 小程序缩略图，微信要求thumbData不能为空且不超过128K
     */
    private byte[] loadMiniProgramThumb(Context context, String imgPath, int fallbackResId){
        if (!TextUtils.isEmpty(imgPath)){
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(imgPath.replaceFirst("https","http")).openConnection(); //img.diandao.org ssl证书问题
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoInput(true);
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1){
                    out.write(buffer, 0, len);
                }
                in.close();
                conn.disconnect();

                byte[] raw = out.toByteArray();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(raw, 0, raw.length, options);
                int sampleSize = 1;
                while (options.outWidth / (sampleSize * 2) >= 320 && options.outHeight / (sampleSize * 2) >= 320){
                    sampleSize *= 2;
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = sampleSize;
                Bitmap bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length, options);
                byte[] thumb = compressThumb(bitmap, 64 * 1024);
                if (thumb != null){
                    return thumb;
                }
            } catch (Exception e){
                Log.w("RNUMShare", "load mini program thumb failed: " + e.getMessage());
            }
        }
        //图片下载失败时用本地默认图，thumbData为空会导致分享失败
        if (fallbackResId != 0){
            Bitmap fallback = BitmapFactory.decodeResource(context.getResources(), fallbackResId);
            return compressThumb(fallback, 64 * 1024);
        }
        return null;
    }

    /**
     * 压缩图片到指定大小以内（JPEG）
     */
    private byte[] compressThumb(Bitmap bitmap, int maxBytes){
        if (bitmap == null){
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap current = bitmap;
        int quality = 80;
        byte[] data = null;
        for (int i = 0; i < 8; i++){
            out.reset();
            current.compress(Bitmap.CompressFormat.JPEG, quality, out);
            data = out.toByteArray();
            if (data.length <= maxBytes){
                break;
            }
            if (quality > 40){
                quality -= 20;
            } else {
                int width = current.getWidth() / 2;
                int height = current.getHeight() / 2;
                if (width < 16 || height < 16){
                    break;
                }
                Bitmap scaled = Bitmap.createScaledBitmap(current, width, height, true);
                if (scaled != current){
                    current = scaled;
                }
                quality = 80;
            }
        }
        return data;
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
                            shareWXMiniProgram(newParams,platformType,share_media,ma);

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
