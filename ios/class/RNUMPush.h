//
//  RNUMPush.h
//  Pods
//
//  Created by 坤坤 on 2021/11/19.
//

#ifndef RNUMPush_h
#define RNUMPush_h

#import <Foundation/Foundation.h>
#import <UMPush/UMessage.h>

#if !defined(NONIFICATION_TYPE)
#define NONIFICATION_TYPE @"userNotificationCenter"
#endif

@interface RNUMPush : NSObject

@property (nonatomic,assign) bool hasListeners;

+(RNUMPush * _Nullable )shareRNUMPush;
+ (void)initUpus:(NSDictionary * __nullable)launchOptions delegate:(id <UNUserNotificationCenterDelegate> _Nullable )delegate;
+ (void)userNotificationCenter:(UNUserNotificationCenter *_Nonnull)center willPresentNotification:(UNNotification *_Nullable)notification withCompletionHandler:(void (^_Nonnull)(UNNotificationPresentationOptions))completionHandler;
+ (void)userNotificationCenter:(UNUserNotificationCenter*_Nonnull)center didReceiveNotificationResponse:(UNNotificationResponse*_Nullable)response withCompletionHandler:(void(^_Nonnull)(void))completionHandler;
+ (void)application:(nullable UIApplication *)application didReceiveRemoteNotification:(NSDictionary * _Nonnull)userInfo fetchCompletionHandler:(nonnull void (^)(UIBackgroundFetchResult))completionHandler;
+ (nullable NSDictionary *)creactData:(nullable NSDictionary *)dic;
+ (NSDictionary *_Nullable)getNonification;
+ (NSString *_Nullable)getDeviceToken;
+ (void)saveDeviaceToken:(nonnull NSData *)deviceToken;
+ (void)sendEventWithName:(nonnull NSString *)name body:(id _Nullable )body;
@end

#endif /* RNUMPush_h */
