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
 * 正式注册umsdk，必须在用户协议同意之后 暂时仅Android
 */
export function initUMSDK() {
    getMyReactBridgeManager?.()?.initUMSDK?.();
}
/**
 * 微信第三方登陆 wx qq
 * @param {} platformType  1微信聊天 2微信朋友圈 4qq 默认1
 * @param {*} callback  data,result
 */
export function auth( platformType, callback) {
    getMyReactBridgeManager?.().auth(platformType, callback);
}
/**
 * 用户账户统计，配合onProfileSignOff
 * 账号统计，这里账户尽量不要使用手机号，或者是用户token，可以使用用户uid。因为这个用户信息会保存在UM第三番平台
 *
 * @param params { userID provider}
 */
export function profileSignInWithPUID(puid) {
    getMyReactBridgeManager()?.profileSignInWithPUID?.(puid);
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
    getMyReactBridgeManager()?.onPageStart?.(viewName);
}
export function onPageEnd(viewName) {
    getMyReactBridgeManager()?.onPageEnd?.(viewName);
}