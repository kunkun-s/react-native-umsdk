//
//  DDWXShare.m
//  点到
//
//  Created by ddmobile on 2024/12/19.
//  Copyright © 2024 点到. All rights reserved.
//

#import "DDWXShare.h"
#import "WXApi.h"
@implementation DDWXShare

+ (void)share:(NSInteger )platformType shareType:(NSString *_Nullable)shareType params:(NSDictionary *)params completion:(void (^ __nullable)(BOOL success))completion {
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    WXMediaMessage *message = [WXMediaMessage message];
    long limitByte = 60000;
    if([shareType isEqualToString: @"MiniProgram"]){
      WXMiniProgramObject *object = [WXMiniProgramObject object];
      object.webpageUrl = [params objectForKey:@"t_url"]?[params objectForKey:@"t_url"]:@"http://www.diandao.org/";
      object.userName = [params objectForKey:@"userName"];
      object.path = [params objectForKey:@"path"];
      object.hdImageData = [NSData dataWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"defaultShare" ofType:@"jpg"]];
      object.withShareTicket = true;
      object.miniProgramType = WXMiniProgramTypeRelease;
      message.title = [params objectForKey:@"title"];
      message.description = [params objectForKey:@"content"] ;
      message.thumbData =  UIImageJPEGRepresentation([DDWXShare compressImageSize:[UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:[params objectForKey:@"img_path"]]]] toByte:limitByte], 1);
      message.mediaObject = object;
    }else if([shareType isEqualToString: @"Image"]){
      WXImageObject *imageObject = [WXImageObject object];
      NSData * imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:[params objectForKey:@"poster"]]];
      imageObject.imageData = imageData;
      
      message.thumbData =  UIImageJPEGRepresentation([DDWXShare compressImageSize:[UIImage imageWithData:imageData] toByte:limitByte], 1);
      message.mediaObject = imageObject;
      
    }else{
      //否则默认是Webpage 网页分享
      WXWebpageObject *webpageObject = [WXWebpageObject object];
      webpageObject.webpageUrl = [params objectForKey:@"t_url"]?[params objectForKey:@"t_url"]:@"http://www.diandao.org/";
      message.title = [params objectForKey:@"title"];
      message.description = [params objectForKey:@"content"] ;
      message.thumbData =  UIImageJPEGRepresentation([DDWXShare compressImageSize:[UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:[params objectForKey:@"img_path"]]]] toByte:limitByte], 1);
      message.mediaObject = webpageObject;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
      SendMessageToWXReq *req = [[SendMessageToWXReq alloc] init];
      req.bText = NO;
      req.message = message;
      req.scene = platformType == 2 ? WXSceneTimeline : WXSceneSession;
      [WXApi sendReq:req completion:completion];
    });
  });
  
}
/*!
 *  @brief 使图片压缩后刚好小于指定大小
 *
 *  @param image 当前要压缩的图 maxLength 压缩后的大小
 *
 *  @return 图片对象
 */
//图片质量压缩到某一范围内，如果后面用到多，可以抽成分类或者工具类,这里压缩递减比二分的运行时间长，二分可以限制下限。
+ (UIImage *_Nullable)compressImageSize:(UIImage * _Nullable )image toByte:(long)maxLength{
    //首先判断原图大小是否在要求内，如果满足要求则不进行压缩，over
    
    NSData *data = UIImageJPEGRepresentation(image, 1);
    //判断“压处理”的结果是否符合要求，符合要求就over
    UIImage *resultImage = [UIImage imageWithData:data];
    if (data.length < maxLength) return resultImage;
    
    //缩处理，直接用大小的比例作为缩处理的比例进行处理，因为有取整处理，所以一般是需要两次处理
    NSUInteger lastDataLength = 0;
    while (data.length > maxLength && data.length != lastDataLength) {
        lastDataLength = data.length;
        //获取处理后的尺寸
        CGFloat ratio = (CGFloat)maxLength / data.length;
        CGSize size = CGSizeMake((NSUInteger)(resultImage.size.width * sqrtf(ratio)),
                                 (NSUInteger)(resultImage.size.height * sqrtf(ratio)));
        //通过图片上下文进行处理图片
      if ( size.width && size.height) {
        UIGraphicsBeginImageContext(size);
        [resultImage drawInRect:CGRectMake(0, 0, size.width, size.height)];
        resultImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
      }
        
        //获取处理后图片的大小
        data = UIImageJPEGRepresentation(resultImage, 1);
    }
    
    return resultImage;
}
@end
