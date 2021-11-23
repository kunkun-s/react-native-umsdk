import { useEffect } from 'react'
import {NativeEventEmitter, NativeModules} from 'react-native'

let DDNativeEventEmitter = null;
let RNUMPush = null
function getNEE(params) {
    if (!DDNativeEventEmitter) {
        
        DDNativeEventEmitter =  new NativeEventEmitter(NativeModules.MyReactBridgeManager);

        // DDNativeEventEmitter =  new NativeEventEmitter();
    }
    return DDNativeEventEmitter
}
function getRNUMPush() {
    if (RNUMPush) {
        RNUMPush = NativeModules.RNUMPush;
    }
    return RNUMPush;
}
/**
 * 监听push 消息回调通知
 * 需要在合适的时机使用  listeener?.remove?.()
 */
export const userNotificationCenter = (callback)=>{
    const listeener = getNEE()?.addListener?.('userNotificationCenter', callback);
    // useEffect(()=>{
    //     return ()=>{
    //         listeener?.remove?.()
    //     }
    // },[listeener])
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
    getRNUMPush()?.getNonification?.((deviceToken)=>{
        callback?.(deviceToken)
    })
}
