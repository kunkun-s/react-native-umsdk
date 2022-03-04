//
//  RNUMPush.h
//  Pods
//
//  Created by 坤坤 on 2021/11/19.
//

#ifndef RNUMPush_h
#define RNUMPush_h

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#else
#import <React/RCTBridgeModule.h>
#endif
#import <Foundation/Foundation.h>
#import <UMPush/UMessage.h>
#import <React/RCTEventEmitter.h>

@interface RNUMPush : RCTEventEmitter <RCTBridgeModule>

@property (nonatomic,assign) bool hasListeners;
+(RNUMPush * _Nullable )shareRNUMPush;
+ (void)initUpus:(NSDictionary * __nullable)launchOptions delegate:(id <UNUserNotificationCenterDelegate> _Nullable )delegate;
+ (void)userNotificationCenter:(UNUserNotificationCenter *_Nonnull)center willPresentNotification:(UNNotification *_Nullable)notification withCompletionHandler:(void (^_Nonnull)(UNNotificationPresentationOptions))completionHandler;
+ (void)userNotificationCenter:(UNUserNotificationCenter*_Nonnull)center didReceiveNotificationResponse:(UNNotificationResponse*_Nullable)response withCompletionHandler:(void(^_Nonnull)(void))completionHandler;
+ (void)application:(nullable UIApplication *)application didReceiveRemoteNotification:(NSDictionary * _Nonnull)userInfo fetchCompletionHandler:(nonnull void (^)(UIBackgroundFetchResult))completionHandler;
+ (nullable NSDictionary *)creactData:(nullable NSDictionary *)dic;
+ (void)saveDeviaceToken:(nonnull NSData *)deviceToken;
+ (void)sendEventWithName:(NSString *)name body:(id)body;
@end

#endif /* RNUMPush_h */
