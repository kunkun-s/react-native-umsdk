//
//  RNUMSdk.h
//  Pods
//
//  Created by 坤坤 on 2021/11/8.
//



#ifndef RNUMSdk_h
#define RNUMSdk_h

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import "RNUMShareModel.h"
#import <UMShare/UMShare.h>

@interface RNUMShare : NSObject

+(UMShareImageObject *) shareImageObject:(NSDictionary *)dic;
+(UMShareMiniProgramObject *)shareMiniProgramObject:(NSDictionary *)dic;
+(UMShareWebpageObject *)shareWebObject:(NSDictionary *)dic;
+ (UMSocialPlatformType)platformType:(NSInteger)platform;

+(void)configUSharePlatforms:(RNUMShareModel *)params;
+(void)shareToPlatform:(NSInteger )platformType shareType:(NSString *)shareType params:(NSDictionary *)params;
+(BOOL)isInstall:(NSString *)platform;
+(void)auth:(NSInteger)platform completion:(RCTResponseSenderBlock)completion;
@end

#endif /* RNUMSdk_h */

