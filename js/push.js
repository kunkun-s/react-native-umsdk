import { useEffect } from 'react'
import {NativeEventEmitter, NativeModules} from 'react-native'

let RNUMPush = null

function getRNUMPush() {
    if (!RNUMPush) {
        RNUMPush = NativeModules.RNUMPush;
    }
    return RNUMPush;
}
/**
 * 监听push 消息回调通知
 * 需要在合适的时机使用  listeener?.remove?.()
 */
export const userNotificationCenter = (callback)=>{
    const listeener = new NativeEventEmitter(getRNUMPush())?.addListener?.('userNotificationCenter', callback);
   
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
