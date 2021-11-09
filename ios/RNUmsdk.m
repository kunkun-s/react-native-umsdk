
#import "RNUMSdk.h"
#import <UMCommon/UMCommon.h>
@implementation RNUMSdk

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

#pragma mark- 友盟
+ (void)initWithAppkey:(NSString* )umAppKey{
    [UMConfigure setLogEnabled:YES];
    [UMConfigure initWithAppkey:umAppKey channel:@"App Store"];
  
}
RCT_EXPORT_MODULE(RNUMSdkBridge)
RCT_REMAP_METHOD(umtest,test:(BOOL)status callBack:(RCTResponseSenderBlock)callBack) {
 
  
    NSString * str = @"React - call - Native - callBack - React";
    callBack(@[str]);
  
}
@end
  
