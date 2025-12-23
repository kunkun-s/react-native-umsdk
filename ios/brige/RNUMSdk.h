
#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

#if RCT_NEW_ARCH_ENABLED
//新架构
@interface RNUMSdk : NSObject
#else

//旧架构
@interface RNUMSdk : NSObject <RCTBridgeModule>

+ (void)initWithAppkey:(NSString* )umAppKey;
#endif
@end
  
