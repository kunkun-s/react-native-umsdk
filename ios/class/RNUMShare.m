//
//  RNUMShare.m
//  RNUmsdk
//
//  Created by 坤坤 on 2021/11/8.
//

#import <UMShare/UMShare.h>
#import "RNUMShare.h"

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
        
        if (params.wxModel && params.wxModel.appKey) {
            PlatformMode * wxModel = params.wxModel;
          
//            [UMSocialGlobal shareInstance].universalLinkDic = @{@(UMSocialPlatformType_WechatSession):params.wxModel.universalLink};
            [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_WechatSession appKey:wxModel.appKey appSecret:wxModel.appSecret redirectURL:nil];
            [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_WechatTimeLine appKey:wxModel.appKey appSecret:wxModel.appSecret redirectURL:nil];
        }
        if (params.qqModel && params.qqModel.appKey) {
            PlatformMode * qqModel = params.qqModel;
//            [UMSocialGlobal shareInstance].universalLinkDic = @{@(UMSocialPlatformType_QQ):params.qqModel.universalLink};
            [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_QQ appKey:qqModel.appKey appSecret:qqModel.appSecret redirectURL:nil];
        }
    }
}


- (UMSocialPlatformType)platformType:(NSInteger)platform
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
- (id)getImage:(NSString *)imageName{
//    NSString * imageName = [dic objectForKey:@"poster"] ? [dic objectForKey:@"poster"] : [dic objectForKey:@"img_path"] ? [dic objectForKey:@"img_path"] : nil;
    UIImage * image = nil;
    if (imageName == nil) {
        return nil;
    }
    if ([imageName hasPrefix:@"http"]) {
        image = [UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:imageName]]];
    } else {
      if ([imageName hasPrefix:@"/"]) {
          //本地图片路径
          image = [UIImage imageWithContentsOfFile:imageName];
      } else {
          //通过图片名字获取主工程的本地图片，mainBundle而非是当前RNUMSDK.bundle
          image = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:imageName ofType:nil]];
      }
    }
    return image;
}
/**图片*/
-(UMShareImageObject *) shareImageObject:(NSDictionary *)dic{
    UMShareImageObject *shareObject = [[UMShareImageObject alloc] init];
    NSString * imageName = [dic objectForKey:@"poster"] ? [dic objectForKey:@"poster"] : [dic objectForKey:@"img_path"] ? [dic objectForKey:@"img_path"] : nil;
    UIImage * image = [self getImage:imageName];
   
    //如果有缩略图，则设置缩略图本地
    shareObject.thumbImage = image;
    [shareObject setShareImage:image];

    return shareObject;
}
/**小程序*/
-(UMShareMiniProgramObject *)shareMiniProgramObject:(NSDictionary *)dic{
  //这里只能是小程序
    UIImage * image = [self getImage:[dic objectForKey:@"img_path"]?[dic objectForKey:@"img_path"]:[UIImage imageNamed:@"defaultShare"]];
  UMShareMiniProgramObject *shareObject = [UMShareMiniProgramObject shareObjectWithTitle:[dic objectForKey:@"title"] descr:[dic objectForKey:@"content"] thumImage:image];
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
-(UMShareWebpageObject *)shareWebObject:(NSDictionary *)dic{
  // 获取技师图片和 要发的话（字符串）
  UIImage * upImage = [self getImage:[dic objectForKey:@"img_path"]];
 
  //创建网页内容对象
  UMShareWebpageObject *shareObject = [UMShareWebpageObject shareObjectWithTitle:[dic objectForKey:@"title"] descr:[dic objectForKey:@"content"] thumImage:upImage];
  //设置网页地址
  shareObject.webpageUrl = [dic objectForKey:@"t_url"]?[dic objectForKey:@"t_url"]:@"http://www.diandao.org/";
  
  return shareObject;
}

RCT_EXPORT_MODULE(RNUMShare)

//不带UI直接分享 shareType: MiniProgram、Image、web
/**
    params : {
        t_url
        img_path
 title
 content
 }
 */
RCT_REMAP_METHOD(shareToPlatform, shareToPlatform:(NSInteger )platformType shareType:(NSString *)shareType params:(NSDictionary *)params completion:(RCTResponseSenderBlock)callBack){
  
  UMSocialMessageObject *messageObject = [UMSocialMessageObject messageObject];
  
  if([shareType isEqualToString: @"MiniProgram"]){
    messageObject.shareObject = [self shareMiniProgramObject:params];
    
  }else if([shareType isEqualToString: @"Image"]){
    messageObject.shareObject = [self shareImageObject:params];
    
  }else if([params objectForKey:@"t_url"]){
    //否则默认是Webpage 网页分享
    messageObject.shareObject = [self shareWebObject:params];
    
  } else {
    //默认为纯文字分享
    messageObject.text = [params objectForKey:@"title"] ? [params objectForKey:@"title"] : @"分享默认文案";
  }
  
  //调用分享接口
  [[UMSocialManager defaultManager] shareToPlatform:[self platformType:platformType] messageObject:messageObject currentViewController:nil completion:^(id data, NSError *error) {

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
          callBack(@[@(stcode), msg]);
      } else {
          callBack(@[@200, @"share success"]);
        
      }
    }
  }];
}

RCT_REMAP_METHOD(isInstall, isInstall:(NSString *)platform completion:(RCTResponseSenderBlock)callBack){
  
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
  
  
  if (callBack) {
      callBack( @[@(isInstall)]) ;
  }
}


RCT_EXPORT_METHOD(auth:(NSInteger)platform completion:(RCTResponseSenderBlock)completion)
{
  UMSocialPlatformType plf = [self platformType:platform];
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

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}
+ (BOOL)requiresMainQueueSetup {
  return YES;
}
@end
