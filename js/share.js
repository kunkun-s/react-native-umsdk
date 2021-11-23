
import { NativeModules } from 'react-native'

let share = null

function getShare() {
    
    if (!share) {
        share = NativeModules.RNUMShare
    }
    return share;
}
/**
 *  getShare()?.shareToPlatform(4,"Image",{title:"测试分享",poster:"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwww.2008php.com%2F09_Website_appreciate%2F10-07-11%2F1278861720_g.jpg&refer=http%3A%2F%2Fwww.2008php.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1639551315&t=c51f82fed65a7534d81b977796694be9"},(data)=>{
        console.log(data)
    })
 * @param {*} platformType 1微信聊天 2微信朋友圈 4qq 默认1
 * @param {*} shareType Image  MiniProgram 默认其他params有t_url时分享web，没有t_url但有title时分享纯文本(qq不支持纯文本，只能是网页)
 * @param {*} params { t_url:分享网页地址 title:标题 img_path:分享图 content:描述 path:小程序页面路径 userName:小程序id poster:纯图片分享}
 * @param {*} Callback 分享回调，暂时没有回调，
 */
export function shareToPlatform(platformType, shareType, params, Callback = ()=>{} ) {
    getShare()?.shareToPlatform(platformType, shareType, params, Callback)
}
