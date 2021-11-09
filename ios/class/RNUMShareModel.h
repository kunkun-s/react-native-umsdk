//
//  RNUMshareModule.h
//  Pods
//
//  Created by 坤坤 on 2021/11/8.
//

#ifndef RNUMshareModel_h
#define RNUMshareModel_h

#import <Foundation/Foundation.h>

@interface PlatformMode : NSObject


@property (nonatomic,copy) NSString* appKey;
@property (nonatomic,copy) NSString* appSecret;
@property (nonatomic,copy) NSString* universalLink;

@end


@interface RNUMShareModel : NSObject


@property (nonatomic,strong) PlatformMode * wxModel;
@property (nonatomic,strong) PlatformMode * qqModel;

@end

#endif /* RNUMshareModule_h */
