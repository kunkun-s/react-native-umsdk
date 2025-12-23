//
//  RNUMSdkImpl.m
//  RNUmsdk
//
//  Created by ddmobile on 2025/12/19.
//

#import "RNUMSdkImpl.h"
#import <UMCommon/UMCommon.h>
#import <UMCommon/MobClick.h>
#import "RNUMPush.h"
#import "RNUMShare.h"
@implementation RNUMSdkImpl

static RNUMSdkImpl *sharedInstance = nil;

+ (instancetype)sharedInstanceDelegate{
  
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        sharedInstance = [[super allocWithZone:NULL] init];
        // 可以在这里进行初始化配置
    });
    return sharedInstance;
}
- (void)preInitUMSDK:(NSString *)appkey channel:(NSString *)channel{
    
};
- (void)initUMSDK:(NSString *)appkey channel:(NSString *)channel secret:(NSString *)secret{
    [UMConfigure initWithAppkey:appkey channel:channel];
    
};
- (void)getDeviceToken:(RCTResponseSenderBlock)callback{
    NSString *deviceToken = [RNUMPush getDeviceToken];
    callback(@[deviceToken]);
};
- (void)getNonification:(RCTResponseSenderBlock)callback{
    NSDictionary * params = [RNUMPush getNonification];
    if (params&& params.count >0 ) {
        callback(@[params]);//返回应用在后台时接受的暂存消息
    }
};
- (void)auth:(NSString *)platformType callback:(RCTResponseSenderBlock)callback{
    
    [RNUMShare auth:[platformType integerValue] completion:callback];
};
- (void)shareToPlatform:(NSString *)platformType shareType:(NSString *)shareType params:(NSDictionary *)params callback:(RCTResponseSenderBlock)callback{
    [RNUMShare shareToPlatform:[platformType integerValue] shareType:shareType params:params completion:callback];
};
- (void)isInstall:(NSString *)platformType callback:(RCTResponseSenderBlock)callback{
    BOOL isInstall = [RNUMShare isInstall:platformType];
    if (callback) {
        callback( @[@(isInstall)]) ;
    }
};
//页面打开
- (void)onPageStart:(NSString *)pageName{
    [MobClick beginLogPageView:pageName];

};
//页面关闭
- (void)onPageEnd:(NSString *)pageName{
    [MobClick endLogPageView:pageName];

};
//账户统计
- (void)profileSignInWithPUID:(NSString *)puid{
    [MobClick profileSignInWithPUID:puid];
};
//账户退出
- (void)profileSignOff{
    [MobClick profileSignOff];
};
@end
