import { NativeModules } from 'react-native'

let umsdk = null

function getMyReactBridgeManager() {
    
    if (!umsdk) {
        umsdk = NativeModules.RNUMSdkBridge
    }
    return umsdk;
}
/**
 * 正式注册umsdk，必须在用户协议同意之后 暂时仅Android
 */
export function initUMSDK() {
    NativeModules.MyReactBridgeManager?.initUMSDK?.();
}
/**
 * 微信第三方登陆 wx qq
 * @param {} platformType  1微信聊天 2微信朋友圈 4qq 默认1
 * @param {*} callback  data,result
 */
export function auth( platformType, callback) {
    NativeModules.RNUMShare.auth(platformType, callback);
}
/**
 * 用户账户统计，配合onProfileSignOff
 * 账号统计，这里账户尽量不要使用手机号，或者是用户token，可以使用用户uid。因为这个用户信息会保存在UM第三番平台
 *
 * @param params { userID provider}
 */
export function onProfileSignIn(params) {
    getMyReactBridgeManager()?.onProfileSignIn?.(params);
}
/**
 * 账户统计退出登录时调用（退出账户，不是退出APP）
 */
export function onProfileSignOff() {
    getMyReactBridgeManager()?.onProfileSignOff?.();
}
/**
 * 是否自动统计页面，默认手动
 * @param status 默认flase
 */
export function setPageAuto(status) {
    getMyReactBridgeManager()?.setPageAuto?.(status);

}
/**
 * 手动采集页面
 * 页面打开时调用onPageState（viewName，true）
 * 页面关闭时调用用onPageState（viewName，false）
 * @param viewName 页面名称（需要在um后台提前部署好）
 * @param status 当前页面是关闭还是打开
 */
export function onPageState(viewName, status) {
    getMyReactBridgeManager()?.onPageState?.(viewName, status);
}
/**
 * 自定义事件统计
 * @param eventID 事件ID （这个ID需要提前在um自定义事件后台创建）
 * @param params 这个事件自定义参数（例如加入描述、事件触发时间、或者统计一个网络请求事件的参数等）
 * 为方便使用者理解及使用，可通过显示名称进行重命名（支持中文），进入【应用设置-事件-编辑】进行操作
 * 参数上线是256个
 * 请不要将事件属性key以"_"开头。"_"开头的key属于SDK保留字
 * id、ts、du、ds、duration、pn、token、device_name、device_model 、device_brand、country、city、channel、province、appkey、app_version、access、launch、pre_app_version、terminate、no_first_pay、is_newpayer、first_pay_at、first_pay_level、first_pay_source、first_pay_user_level、first_pay_version、type、是保留字段，不能作为event id 及key的名称
 */
export function onEventObject(eventID, params) {
    getMyReactBridgeManager()?.onEventObject?.(eventID, params);

}