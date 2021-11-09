//
//  RNUMShareModule.m
//  RNUmsdk
//
//  Created by 坤坤 on 2021/11/8.
//

#import "RNUMShareModel.h"


@implementation PlatformMode

- (void)setValue:(id)value forUndefinedKey:(NSString *)key{

}
@end

@implementation RNUMShareModel

- (id)init{

    self = [super init];
    if (self) {
        
        _wxModel = [[PlatformMode alloc]init];
        _qqModel = [[PlatformMode alloc]init];
    }
    return self;
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key{
    if ([key isEqualToString:@"qq"]) {

        [_qqModel setValuesForKeysWithDictionary:value];
        
    }
    if ([key isEqualToString:@"wx"]) {
       
        [_wxModel setValuesForKeysWithDictionary:value];
    }
}

@end
