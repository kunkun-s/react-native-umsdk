//
//  RNUMShare.m
//  RNUmsdk
//
//  Created by 坤坤 on 2021/11/8.
//

#import <UMShare/UMShare.h>
#import "RNUMShare.h"
#import "DDWXShare.h"
@implementation RNUMShare

/**配置分享平台*/
+(void)configUSharePlatforms:(RNUMShareModel *)params{
    
    if (params) {
        
        NSString * wx_ul = @"";
        if (params.wxModel){
            wx_ul = params.wxModel.universalLink;
        };
        NSString * qq_ul = @"";
        if (params.qqModel) {
            qq_ul = params.qqModel.universalLink;
        };
        //配置第三方化平台前必须先设置universalLinkDic，可以一次性设置全部的，也可以逐个设置
        [UMSocialGlobal shareInstance].universalLinkDic = @{
            @(UMSocialPlatformType_WechatSession): wx_ul,
            @(UMSocialPlatformType_QQ): qq_ul
        };
        
        //分享
        if (params.wxModel && params.wxModel.appKey) {
            PlatformMode * wxModel = params.wxModel;
            [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_WechatSession appKey:wxModel.appKey appSecret:wxModel.appSecret redirectURL:nil];
            [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_WechatTimeLine appKey:wxModel.appKey appSecret:wxModel.appSecret redirectURL:nil];
        }
        if (params.qqModel && params.qqModel.appKey) {
            PlatformMode * qqModel = params.qqModel;
            [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_QQ appKey:qqModel.appKey appSecret:qqModel.appSecret redirectURL:nil];
        }
    }
}


+ (UMSocialPlatformType)platformType:(NSInteger)platform
{
    switch (platform) {
      
        case 1: // wechat
          return UMSocialPlatformType_WechatSession;
        case 2: // wechat
          return UMSocialPlatformType_WechatTimeLine;
        case 3:
          return UMSocialPlatformType_WechatFavorite;
        case 4:
          return UMSocialPlatformType_QQ;
        case 5:
          return UMSocialPlatformType_Qzone;
       
        default:
          return UMSocialPlatformType_WechatSession;
      }
}

/**图片*/
+(UMShareImageObject *) shareImageObject:(NSDictionary *)dic{
    UMShareImageObject *shareObject = [[UMShareImageObject alloc] init];
    //  UIImage * image = [UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:[dic objectForKey:@"poster"]]]];
    NSData * imgeData =[NSData dataWithContentsOfURL:[NSURL URLWithString:[dic objectForKey:@"poster"]]];
    //如果有缩略图，则设置缩略图本地
    shareObject.thumbImage = UIImageJPEGRepresentation([DDWXShare compressImageSize:[UIImage imageWithData:imgeData] toByte:60000], 1);
    shareObject.shareImage = imgeData;

    return shareObject;
}
/**小程序*/
+(UMShareMiniProgramObject *)shareMiniProgramObject:(NSDictionary *)dic{
  //这里只能是小程序
    UMShareMiniProgramObject *shareObject = [UMShareMiniProgramObject shareObjectWithTitle:[dic objectForKey:@"title"] descr:[dic objectForKey:@"content"] thumImage:[dic objectForKey:@"img_path"]?[dic objectForKey:@"img_path"]:[UIImage imageNamed:@"defaultShare"]];

  shareObject.webpageUrl = [dic objectForKey:@"t_url"]?[dic objectForKey:@"t_url"]:@"http://www.diandao.org/";
  shareObject.userName = [dic objectForKey:@"userName"];
  shareObject.path = [dic objectForKey:@"path"];
//  shareObject.hdImageData = [NSData dataWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"defaultShare" ofType:@"jpg"]];
    NSURL *bundleURL = [[NSBundle mainBundle] URLForResource:@"RNUMSDK" withExtension:@"bundle"];
    if (bundleURL) {
        NSBundle *bundle = [NSBundle bundleWithURL:bundleURL];
        shareObject.hdImageData = [NSData dataWithContentsOfFile:[bundle pathForResource:@"defaultShare" ofType:@"jpg"]];
    }
  shareObject.miniProgramType = UShareWXMiniProgramTypeRelease;
  
  return shareObject;
     
}
/**网页分享*/
+(UMShareWebpageObject *)shareWebObject:(NSDictionary *)dic{
    // 获取技师图片和 要发的话（字符串）
    UIImage * upImage;
    if ([dic objectForKey:@"img_path"]) {
        NSURL *iconUrl = [NSURL URLWithString:[dic objectForKey:@"img_path"]];
        upImage = [UIImage imageWithData:[NSData dataWithContentsOfURL:iconUrl]];

    }
    if (!upImage) {
        upImage = [UIImage imageNamed:@"share"];
    }
    //创建网页内容对象
    UMShareWebpageObject *shareObject = [UMShareWebpageObject shareObjectWithTitle:[dic objectForKey:@"title"] descr:[dic objectForKey:@"content"] thumImage:upImage];
    //设置网页地址
    shareObject.webpageUrl = [dic objectForKey:@"t_url"]?[dic objectForKey:@"t_url"]:@"http://www.diandao.org/";

    return shareObject;
}

//不带UI直接分享 shareType: MiniProgram、Image、web
/**
    params : {
        t_url
        img_path
 title
 content
 }
 */
+(void)shareToPlatform:(NSInteger )platformType shareType:(NSString *)shareType params:(NSDictionary *)params completion:(RCTResponseSenderBlock)completion{
    __block RCTResponseSenderBlock _completion = completion;
    void (^callBack)(BOOL success)  = ^(BOOL success){
        if (success) {
          _completion(@[@200, @"share success"]);
          _completion = nil;
        }else{
          _completion(@[@-1, @"share failed"]);
          _completion = nil;
        }
      };
    if (platformType == 1 || platformType == 2) {
       //友盟6.10.13调用微信有问题，改为直接调用微信api
       [DDWXShare share:platformType shareType:shareType params:params completion:callBack];
    } else {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            UMSocialMessageObject *messageObject = [UMSocialMessageObject messageObject];
            messageObject.text = @"点到分享";
            if([shareType isEqualToString: @"MiniProgram"]){
              messageObject.shareObject = [RNUMShare shareMiniProgramObject:params];
              
            }else if([shareType isEqualToString: @"Image"]){
              messageObject.shareObject = [RNUMShare shareImageObject:params];
              
            }else{
              //否则默认是Webpage 网页分享
              messageObject.shareObject = [RNUMShare shareWebObject:params];
              
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                //调用分享接口
                [[UMSocialManager defaultManager] shareToPlatform:[RNUMShare platformType:platformType] messageObject:messageObject currentViewController:nil completion:^(id data, NSError *error) {

                  if (callBack) {
                    if (error) {
                      NSString *msg = error.userInfo[@"NSLocalizedFailureReason"];
                      if (!msg) {
                        msg = error.userInfo[@"message"];
                      }if (!msg) {
                        msg = @"share failed";
                      }
                      NSInteger stcode =error.code;
                      if(stcode == 2009){
                        stcode = -1;
                      }
                        completion(@[@(stcode), msg]);
                    } else {
                        completion(@[@200, @"share success"]);
                      
                    }
                  }
                }];
            });
            
        });

    }
  

}
+(BOOL)isInstall:(NSString *)platform{
    UMSocialPlatformType platformType = -1111;
    BOOL isInstall = NO;
    if ([platform isEqualToString:@"QQ"]) {
      platformType = UMSocialPlatformType_QQ;

    }else if ([platform isEqualToString:@"WX_LINE"]){
      platformType = UMSocialPlatformType_WechatTimeLine;

    }else if([platform isEqualToString:@"WX_SESSION"]){
      platformType = UMSocialPlatformType_WechatSession;
    }
    if (platformType != -1111) {
      isInstall = [[UMSocialManager defaultManager]isInstall:platformType] ? YES : NO;
    }
    return isInstall;
}


+(void)auth:(NSInteger)platform completion:(RCTResponseSenderBlock)completion{
    UMSocialPlatformType plf = [RNUMShare platformType:(platform == 2||platform == 1)?1:platform];
  if (plf == UMSocialPlatformType_UnKnown) {
    if (completion) {
      completion(@[@(UMSocialPlatformType_UnKnown), @"invalid platform"]);
      return;
    }
  }
  
  [[UMSocialManager defaultManager] getUserInfoWithPlatform:plf currentViewController:nil completion:^(id result, NSError *error) {
    if (completion) {
      if (error) {
        NSString *msg = error.userInfo[@"NSLocalizedFailureReason"];
        if (!msg) {
          msg = error.userInfo[@"message"];
        }if (!msg) {
          msg = @"share failed";
        }
        NSInteger stCode = error.code;
        if(stCode == 2009){
          stCode = -1;
        }
        completion(@[@(stCode), @{}, msg]);
      } else {
        UMSocialUserInfoResponse *authInfo = result;
        
        NSMutableDictionary *retDict = [NSMutableDictionary dictionaryWithCapacity:8];
        retDict[@"uid"] = authInfo.uid;
        retDict[@"openid"] = authInfo.openid;
        retDict[@"unionid"] = authInfo.unionId;
        retDict[@"accessToken"] = authInfo.accessToken;
        retDict[@"refreshToken"] = authInfo.refreshToken;
        retDict[@"expiration"] = authInfo.expiration;
        
        retDict[@"name"] = authInfo.name;
        retDict[@"iconurl"] = authInfo.iconurl;
        retDict[@"gender"] = authInfo.unionGender;
        
        NSDictionary *originInfo = authInfo.originalResponse;
        retDict[@"city"] = originInfo[@"city"];
        retDict[@"province"] = originInfo[@"province"];
        retDict[@"country"] = originInfo[@"country"];
        retDict[@"headimgurl"] = originInfo[@"headimgurl"];
        completion(@[@200, retDict, @""]);
      }
    }
  }];
  
}

+ (BOOL)requiresMainQueueSetup {
  return YES;
}
@end
