//
//  KKUMSdkEventEmitter.h
//  RNUmsdk
//
//  Created by ddmobile on 2025/12/19.
//

#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>
NS_ASSUME_NONNULL_BEGIN
#if !defined(NONIFICATION_TYPE)
#define NONIFICATION_TYPE @"userNotificationCenter"
#endif

@interface KKUMSdkEventEmitter : RCTEventEmitter <RCTBridgeModule>

+ (instancetype)sharedInstance;
+ (void)sendEventWithName:(NSString *)eventName body:(id)body;

@end

NS_ASSUME_NONNULL_END

