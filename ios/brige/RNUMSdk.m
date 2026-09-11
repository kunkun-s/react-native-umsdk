
#import "RNUMSdk.h"
#import "RNUMSdkImpl.h"
#import "RNUMShare.h"
#import <UMCommon/UMCommon.h>
@implementation RNUMSdk

#if RCT_NEW_ARCH_ENABLED

#else
RCT_EXPORT_MODULE(RNUMSdkBridge)

RCT_REMAP_METHOD(preInitUMSDK, preInitUMSDK:(NSString *)appkey channel:(NSString *)channel){

}

RCT_REMAP_METHOD(initUMSDK, initUMSDK:(NSString *)appkey channel:(NSString *)channel secret:(NSString *)secret){
    [[RNUMSdkImpl sharedInstanceDelegate] initUMSDK:appkey channel:channel secret:secret];
}

RCT_REMAP_METHOD(getDeviceToken, getDeviceToken:(RCTResponseSenderBlock)callback){
    [[RNUMSdkImpl sharedInstanceDelegate] getDeviceToken:callback];
    
}
RCT_REMAP_METHOD(getNonification, getNonification:(RCTResponseSenderBlock)callback){
    [[RNUMSdkImpl sharedInstanceDelegate] getNonification:callback];
}
RCT_REMAP_METHOD(shareToPlatform, shareToPlatform:(NSString *)platformType shareType:(NSString *)shareType params:(NSDictionary *)params){
    [[RNUMSdkImpl sharedInstanceDelegate] shareToPlatform:platformType shareType:shareType params:params];
}
RCT_REMAP_METHOD(isInstall, isInstall:(NSString *)platformType callback:(RCTResponseSenderBlock)callback){
    [[RNUMSdkImpl sharedInstanceDelegate] isInstall:platformType callback:callback];

}
RCT_EXPORT_METHOD(auth:(NSString *)platformType callback:(RCTResponseSenderBlock)callback){
    [[RNUMSdkImpl sharedInstanceDelegate] auth:platformType callback:callback];
}
RCT_REMAP_METHOD(onPageStart, onPageStart:(NSString *)pageName){
    [[RNUMSdkImpl sharedInstanceDelegate] onPageStart:pageName];
}
RCT_REMAP_METHOD(onPageEnd, onPageEnd:(NSString *)pageName){
    [[RNUMSdkImpl sharedInstanceDelegate] onPageEnd:pageName];
}
RCT_REMAP_METHOD(profileSignInWithPUID, profileSignInWithPUID:(NSString *)puid){
    [[RNUMSdkImpl sharedInstanceDelegate] profileSignInWithPUID:puid];
}
RCT_REMAP_METHOD(profileSignOff, profileSignOff){
    [[RNUMSdkImpl sharedInstanceDelegate] profileSignOff];
}

#endif
- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

//- 友盟
+ (void)initWithAppkey:(NSString* )umAppKey{
    [UMConfigure initWithAppkey:umAppKey channel:@"App Store"];
  
}

@end
  
