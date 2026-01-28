//
//  DDWXShare.h
//  点到
//
//  Created by ddmobile on 2024/12/19.
//  Copyright © 2024 点到. All rights reserved.
//
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
@interface DDWXShare : NSObject

+ (void)share:(NSInteger )platformType shareType:(NSString *_Nullable)shareType params:(NSDictionary *_Nullable)params completion:(void (^ __nullable)(BOOL success))completion;
+ (UIImage *_Nullable)compressImageSize:(UIImage * _Nullable )image toByte:(NSUInteger)maxLength;
@end
