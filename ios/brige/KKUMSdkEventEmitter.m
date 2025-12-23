//
//  KKUMSdkEventEmitter.m
//  RNUmsdk
//
//  Created by ddmobile on 2025/12/19.
//

// CustomEventEmitter.m
#import "KKUMSdkEventEmitter.h"

static KKUMSdkEventEmitter *sharedInstance;

@implementation KKUMSdkEventEmitter {
    BOOL _hasListeners;
}
RCT_EXPORT_MODULE(KKUMSdkEventEmitter)

// 单例模式
+ (instancetype)sharedInstance {
    if(sharedInstance == nil){
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            sharedInstance = [KKUMSdkEventEmitter new];
        });
    }
    return sharedInstance;
}
+(id)allocWithZone:(NSZone *)zone {
    if(sharedInstance == nil){
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            sharedInstance = [super allocWithZone:zone];
        });
    }
    return sharedInstance;
}
// 必须实现 supportedEvents
- (NSArray<NSString *> *)supportedEvents {
    return @[NONIFICATION_TYPE];
}

// 在监听开始时调用
- (void)startObserving {
    _hasListeners = YES;
    // 可以在这里开始准备数据
}

// 在监听停止时调用
- (void)stopObserving {
    _hasListeners = NO;
    // 清理资源
}

// 发送事件的方法
- (void)emitEvent:(NSString *)eventName body:(id)body {
    if (_hasListeners) {
        [self sendEventWithName:eventName body:body];
    }
}

// 可以从任何地方调用的便捷方法
+ (void)sendEventWithName:(NSString *)eventName body:(id)body {
    [[KKUMSdkEventEmitter sharedInstance] emitEvent:eventName body:body];
}

@end
