//
//  RNUMSdkImpl.h
//  RNUmsdk
//
//  Created by ddmobile on 2025/12/19.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
NS_ASSUME_NONNULL_BEGIN

@interface RNUMSdkImpl : NSObject
+ (instancetype)sharedInstanceDelegate;

- (void)preInitUMSDK:(NSString *)appkey channel:(NSString *)channel;
- (void)initUMSDK:(NSString *)appkey channel:(NSString *)channel secret:(NSString *)secret;
- (void)getDeviceToken:(RCTResponseSenderBlock)callback;
- (void)getNonification:(RCTResponseSenderBlock)callback;
- (void)auth:(NSString *)platformType callback:(RCTResponseSenderBlock)callback;
- (void)shareToPlatform:(NSString *)platformType shareType:(NSString *)shareType params:(NSDictionary *)params;
- (void)isInstall:(NSString *)platformType callback:(RCTResponseSenderBlock)callback;
- (void)onPageStart:(NSString *)pageName;
- (void)onPageEnd:(NSString *)pageName;
- (void)profileSignInWithPUID:(NSString *)puid;
- (void)profileSignOff;

@end

NS_ASSUME_NONNULL_END
