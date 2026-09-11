import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

/**
 *   "codegenConfig": {
    "name": "NativeDDVerifySpec",
    "type": "modules",
    "jsSrcsDir": "specs", //这个目录下可以有多个NativeDDverify文件
    "android": {
      "javaPackageName": "com.dddverify"
    }
  },
 */
export interface Spec extends TurboModule {

    //预初始化
    preInitUMSDK(appkey:string, channel:string,): void;
    //正式初始化
    initUMSDK(appkey:string, channel:string, secret:string): void;
    //获取devicetoken
    getDeviceToken(callback:(deviceToken?:string)=>void): void;
    //获取通知栏未读消息，仅iOS
    getNonification(callback:(data:Object)=>void):void;
    
    //三方登录
    auth(platformType:string, callback:(code:number,result:string,message:string)=>void):void;
    //分享（不需要回调，分享结果不返回JS）
    shareToPlatform(platformType:string, shareType:string, params:Object):void;
    //检测是否安装
    isInstall(platformType:string, callback:(status:boolean)=>void):void;
    
    //页面统计
    onPageStart(pageName:string):void;
    onPageEnd(pageName:string):void;
    //登录统计
    profileSignInWithPUID(puid:string):void;
    profileSignOff():void;
}

// 此模块支持新旧版本，因此使用get兼容 而非getEnforcing
export default TurboModuleRegistry.get<Spec>('RNUMSdkBridge') as Spec|null;