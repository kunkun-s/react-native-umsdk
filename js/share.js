
import { NativeModules } from 'react-native'
import RNUMPushArch from '../specs/NativeUMSdkModule'

let share = null

function getShare() {
    
    if (!share) {
        share = RNUMPushArch||NativeModules.RNUMSdkBridge
    }
    return share;
}
/**
 *  分享不返回回调，调用后不需要等待结果
 *  getShare()?.shareToPlatform(4,"Image",{title:"测试分享",poster:"https://xxx.jpg"})
 * @param {*} platformType 1微信聊天 2微信朋友圈 4qq 默认1
 * @param {*} shareType Image  MiniProgram 默认其他params有t_url时分享web，没有t_url但有title时分享纯文本(qq不支持纯文本，只能是网页)
 * @param {*} params { t_url:分享网页地址 title:标题 img_path:分享图 content:描述 path:小程序页面路径 userName:小程序id poster:纯图片分享}
 */
export function shareToPlatform(platformType, shareType, params) {
    getShare()?.shareToPlatform(String(platformType),String(shareType) , params)
}
