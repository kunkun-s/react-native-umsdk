//
//  RNUMSdk.h
//  Pods
//
//  Created by 坤坤 on 2021/11/8.
//



#ifndef RNUMSdk_h
#define RNUMSdk_h

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif
#import <Foundation/Foundation.h>

#import "RNUMShareModel.h"

@interface RNUMShare : NSObject<RCTBridgeModule>


+(void)configUSharePlatforms:(RNUMShareModel *)params;
@end

#endif /* RNUMSdk_h */

