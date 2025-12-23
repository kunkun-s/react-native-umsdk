//
//  RCTNativeUMSdk.h
//  Pods
//
//  Created by ddmobile on 2025/12/18.
//

#import <React/RCTEventEmitter.h>
#import "RNUMPush.h"

#ifdef RCT_NEW_ARCH_ENABLED
#import <NativeUMSdkModuleSpec/NativeUMSdkModuleSpec.h>
#endif

NS_ASSUME_NONNULL_BEGIN

#ifdef RCT_NEW_ARCH_ENABLED
//新架构
@interface RCTNativeUMSdk : NSObject<NativeUMSdkModuleSpec>
#else
//旧架构
@interface RCTNativeUMSdk : NSObject
#endif

@end

NS_ASSUME_NONNULL_END
