//
//  RCTNativeUMSdk.m
//  RNUmsdk
//
//  Created by ddmobile on 2025/12/18.
//
#import "RCTNativeUMSdk.h"
#import "RNUMSdkImpl.h"
@implementation RCTNativeUMSdk
#ifdef RCT_NEW_ARCH_ENABLED
RCT_EXPORT_MODULE(RNUMSdkBridge)
//新架构
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativeUMSdkModuleSpecJSI>(params);
}
#endif


- (void)preInitUMSDK:(NSString *)appkey channel:(NSString *)channel{
    
};
- (void)initUMSDK:(NSString *)appkey channel:(NSString *)channel secret:(NSString *)secret{
    [[RNUMSdkImpl sharedInstanceDelegate] initUMSDK:appkey channel:channel secret:secret];
};
- (void)getDeviceToken:(RCTResponseSenderBlock)callback{
    [[RNUMSdkImpl sharedInstanceDelegate] getDeviceToken:callback];
};
- (void)getNonification:(RCTResponseSenderBlock)callback{
    [[RNUMSdkImpl sharedInstanceDelegate] getNonification:callback];
};
- (void)auth:(NSString *)platformType callback:(RCTResponseSenderBlock)callback{
    [[RNUMSdkImpl sharedInstanceDelegate] auth:platformType callback:callback];
};
- (void)shareToPlatform:(NSString *)platformType shareType:(NSString *)shareType params:(NSDictionary *)params callback:(RCTResponseSenderBlock)callback{
    [[RNUMSdkImpl sharedInstanceDelegate] shareToPlatform:platformType shareType:shareType params:params callback:callback];
};
- (void)isInstall:(NSString *)platformType callback:(RCTResponseSenderBlock)callback{
    [[RNUMSdkImpl sharedInstanceDelegate] isInstall:platformType callback:callback];
    
};
- (void)onPageStart:(NSString *)pageName{
    [[RNUMSdkImpl sharedInstanceDelegate] onPageStart:pageName];
};
- (void)onPageEnd:(NSString *)pageName{
    [[RNUMSdkImpl sharedInstanceDelegate] onPageEnd:pageName];

};
- (void)profileSignInWithPUID:(NSString *)puid{
    [[RNUMSdkImpl sharedInstanceDelegate] profileSignInWithPUID:puid];

};
- (void)profileSignOff{
    [[RNUMSdkImpl sharedInstanceDelegate] profileSignOff];
};



@end
