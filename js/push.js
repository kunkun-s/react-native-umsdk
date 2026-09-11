import {NativeEventEmitter, NativeModules} from 'react-native'
import RNUMPushArch from '../specs/NativeUMSdkModule'
let RNUMPush = null

function getRNUMPush() {
    if (!RNUMPush) {
        RNUMPush = RNUMPushArch||NativeModules.RNUMSdkBridge;
    }
    return RNUMPush;
}
/**
 * 监听push 消息回调通知
 * 需要在合适的时机使用  listeener?.remove?.()
 */
export const userNotificationCenter = (callback)=>{

    const listeener = new NativeEventEmitter( NativeModules?.KKUMSdkEventEmitter)?.addListener?.('userNotificationCenter', (pushData)=>{

        let n_pushData = {};
        if (Object.prototype.toString.call(pushData) == '[object Object]') {
            n_pushData = Object.assign({}, pushData)
        }

        //原生两端下发的 extra 都是 JSON 字符串，这里统一解析成对象再回调
        if (Object.prototype.toString.call(n_pushData?.extra) == '[object String]' && n_pushData.extra.length > 0) {
            try {
                n_pushData['extra'] = JSON.parse(n_pushData.extra);
            } catch (e) {
                n_pushData['extra'] = {};
            }
        }
        if (`${n_pushData?.extra?.hide}` == "1" || Object.keys(n_pushData)?.length === 0) {// APP内不显示弹窗
            return;
        }else{
            callback(n_pushData)
        }
    });

    return listeener
}
/**
 * 获取ios APP 在后台时点击的推送消息
 * @param {*} callback 
 */
export const getNonification = (callback)=>{
    getRNUMPush()?.getNonification?.((data)=>{
        callback?.(data)
    })
}

/**
 * 获取ios APP 在后台时点击的推送消息
 * @param {*} callback 
 */
export const getDeviceToken = (callback)=>{

    getRNUMPush()?.getDeviceToken?.((deviceToken)=>{
        callback?.(deviceToken)
    })
}
