import { NativeModules } from 'react-native'
import RNUMPushArch from '../specs/NativeUMSdkModule'

let umsdk = null

function getMyReactBridgeManager() {
    
    if (!umsdk) {
        umsdk = RNUMPushArch||NativeModules.RNUMSdkBridge
    }
    return umsdk;
}
/**
 * 是否安装了目标APP
 * 注意：isInstall 的平台编码与 auth/shareToPlatform 不同，原生两端认的是
 * "QQ" / "WX_LINE"(朋友圈) / "WX_SESSION"(聊天)，这里把 4/2/1 也映射过去，避免误传。
 * @param {*} platformType "QQ"|"WX_LINE"|"WX_SESSION"，或 4|2|1
 * @param {*} callback (isInstall:boolean)=>void
 */
export function isInstall(platformType, callback){
    //对象取值会把数字key转成字符串，因此 1 和 '1' 都能命中
    const INSTALL_PLATFORM_MAP = {
        1: 'WX_SESSION',
        2: 'WX_LINE',
        4: 'QQ',
    };
    const platform = INSTALL_PLATFORM_MAP[platformType] || platformType;
    return getMyReactBridgeManager?.()?.isInstall?.(String(platform), callback);
}

/**
 * 正式注册umsdk，必须在用户协议同意之后 暂时仅Android
 *
 * @param {*} appkey  友盟appkey
 * @param {*} channel 渠道
 * @param {*} secret  Android专用，iOS传空字符串
 */
export function initUMSDK(appkey, channel, secret = '') {
    if (!appkey || !channel) {
        console.warn('[RNUmsdk] initUMSDK 需要 appkey 和 channel，已忽略本次调用');
        return;
    }
    getMyReactBridgeManager?.()?.initUMSDK?.(String(appkey), String(channel), String(secret));
}
/**
 * 微信第三方登陆 wx qq
 * @param {} platformType  1微信聊天 2微信朋友圈 4qq 默认1
 * @param {*} callback  data,result
 */
export function auth( platformType, callback) {
    getMyReactBridgeManager?.()?.auth?.(String(platformType), callback);
}
/**
 * 用户账户统计，配合onProfileSignOff
 * 账号统计，这里账户尽量不要使用手机号，或者是用户token，可以使用用户uid。因为这个用户信息会保存在UM第三番平台
 *
 * @param params { userID provider}
 */
export function profileSignInWithPUID(puid) {
    getMyReactBridgeManager()?.profileSignInWithPUID?.(String(puid));
}
/**
 * 账户统计退出登录时调用（退出账户，不是退出APP）
 */
export function profileSignOff() {
    getMyReactBridgeManager()?.profileSignOff?.();
}

/**
 * 手动采集页面
 */
export function onPageStart(viewName) {
    getMyReactBridgeManager()?.onPageStart?.(String(viewName));
}
export function onPageEnd(viewName) {
    getMyReactBridgeManager()?.onPageEnd?.(String(viewName));
}