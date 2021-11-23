//
//  RNUMPush.m
//  RNUmsdk
//
//  Created by 坤坤 on 2021/11/19.
//

#import "RNUMPush.h"


@implementation RNUMPush

+ (void)initUpus:(NSDictionary * __nullable)launchOptions delegate:(id <UNUserNotificationCenterDelegate> )delegate{
    // Push功能配置
    UMessageRegisterEntity* entity =[[UMessageRegisterEntity alloc] init];
     entity.types =UMessageAuthorizationOptionBadge|UMessageAuthorizationOptionAlert|UMessageAuthorizationOptionSound;
    //如果你期望使用交互式(只有iOS 8.0及以上有)的通知，请参考下面注释部分的初始化代码
    if(([[[UIDevice currentDevice] systemVersion]intValue]>=8)&&([[[UIDevice currentDevice] systemVersion]intValue]<10)){
        UIMutableUserNotificationAction*action1 =[[UIMutableUserNotificationAction alloc] init];
        action1.identifier =@"action1_identifier";
        action1.title=@"打开应用";
        action1.activationMode =UIUserNotificationActivationModeForeground;//当点击的时候启动程序

        UIMutableUserNotificationAction*action2 =[[UIMutableUserNotificationAction alloc] init];//第二按钮
        action2.identifier =@"action2_identifier";
        action2.title=@"忽略";
        action2.activationMode =UIUserNotificationActivationModeBackground;//当点击的时候不启动程序，在后台处理
        action2.authenticationRequired = YES;//需要解锁才能处理，如果action.activationMode = UIUserNotificationActivationModeForeground;则这个属性被忽略；
        action2.destructive = YES;
        UIMutableUserNotificationCategory*actionCategory1 =[[UIMutableUserNotificationCategory alloc] init];
                actionCategory1.identifier =@"category1";//这组动作的唯一标示
        [actionCategory1 setActions:@[action1,action2] forContext:(UIUserNotificationActionContextDefault)];
        NSSet*categories =[NSSet setWithObjects:actionCategory1,nil];
        entity.categories=categories;
    }
    //如果要在iOS10显示交互式的通知，必须注意实现以下代码
    if([[[UIDevice currentDevice] systemVersion]intValue]>=10){
        UNNotificationAction*action1_ios10 =[UNNotificationAction actionWithIdentifier:@"action1_identifier" title:@"打开应用" options:UNNotificationActionOptionForeground];
        UNNotificationAction*action2_ios10 =[UNNotificationAction actionWithIdentifier:@"action2_identifier" title:@"忽略" options:UNNotificationActionOptionForeground];

        //UNNotificationCategoryOptionNone
        //UNNotificationCategoryOptionCustomDismissAction  清除通知被触发会走通知的代理方法
        //UNNotificationCategoryOptionAllowInCarPlay       适用于行车模式
        UNNotificationCategory*category1_ios10 =[UNNotificationCategory categoryWithIdentifier:@"category1" actions:@[action1_ios10,action2_ios10]   intentIdentifiers:@[] options:UNNotificationCategoryOptionCustomDismissAction];
        NSSet*categories =[NSSet setWithObjects:category1_ios10,nil];
         entity.categories=categories;
    }

    [UNUserNotificationCenter currentNotificationCenter].delegate = delegate;
   //友盟推送的注册方法
    [UMessage registerForRemoteNotificationsWithLaunchOptions:launchOptions Entity:entity completionHandler:^(BOOL granted, NSError * _Nullable error) {
      if (granted) {
        //点击允许
      } else {
        //点击不允许
      }
      
    }];
    
}
//iOS10新增：处理前台收到通知的代理方法
+ (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler{
  
  NSDictionary * userInfo = notification.request.content.userInfo;
  if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    //应用处于前台时的远程推送接受
    //关闭U-Push自带的弹出框
    [UMessage setAutoAlert:NO];
    //必须加这句代码
    [UMessage didReceiveRemoteNotification:userInfo];
    
//    [self pushDic:userInfo notificationName:NONIFICATION_TYPE];
    
  }else{
    //应用处于前台时的本地推送接受
  }
  //当应用处于前台时提示设置，需要哪个可以设置哪一个
  completionHandler(UNNotificationPresentationOptionSound|UNNotificationPresentationOptionBadge|UNNotificationPresentationOptionAlert);
}
//iOS10新增：处理后台点击通知的代理方法
+ (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completionHandler{
  NSDictionary * userInfo = response.notification.request.content.userInfo;
  if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    //应用处于后台时的远程推送接受
    //必须加这句代码
    [UMessage didReceiveRemoteNotification:userInfo];
    //将通知消息暂存
    [[NSUserDefaults standardUserDefaults] setObject:userInfo forKey:@"RN_UM_NONIFICATION_BACK"];
    [[NSUserDefaults standardUserDefaults] synchronize];
//    [self pushDic:userInfo notificationName:NONIFICATION_TYPE];
    
  }else{
    //应用处于后台时的本地推送接受
  }
}
//UNUserNotificationCenter deleage
+ (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(nonnull void (^)(UIBackgroundFetchResult))completionHandler{
    [UMessage setAutoAlert:NO];
    if([[[UIDevice currentDevice] systemVersion]intValue] < 10){
      [UMessage didReceiveRemoteNotification:userInfo];
//      [self pushDic:userInfo notificationName:NONIFICATION_TYPE];

      completionHandler(UIBackgroundFetchResultNewData);
    }
    //过滤掉Push的撤销功能，因为PushSDK内部已经调用的completionHandler(UIBackgroundFetchResultNewData)，
    //防止两次调用completionHandler引起崩溃
    if(![userInfo valueForKeyPath:@"aps.recall"])
    {
        completionHandler(UIBackgroundFetchResultNewData);
    }
}

RCT_EXPORT_MODULE(RNUMPush)
RCT_REMAP_METHOD(getNonification, getNonification:(RCTResponseSenderBlock)callback){
  NSUserDefaults * userdf = [NSUserDefaults standardUserDefaults];
  if ([userdf objectForKey:@"RN_UM_NONIFICATION_BACK"]) {
      //有消息返回并删除旧消息
      NSDictionary * params =  [RNUMPush creactData:[userdf objectForKey:@"RN_UM_NONIFICATION_BACK"]];
      callback(@[params]);//返回应用在后台时接受的暂存消息
      [userdf removeObjectForKey:@"RN_UM_NONIFICATION_BACK"];//删除这条推送消息
  }
}
RCT_REMAP_METHOD(getDeviceToken, getDeviceToken:(RCTResponseSenderBlock)callback){
  NSString *deviceToken = [[NSUserDefaults standardUserDefaults] objectForKey:@"deviceToken"];
  deviceToken =  deviceToken?deviceToken:@"";
  callback(@[deviceToken]);
  
}
/**
 暂存APP在后台时，用户点击的推送消息
 */
+ (void)saveDeviaceToken:(nonnull NSData *)deviceToken{
    [UMessage registerDeviceToken:deviceToken];
    // 保存deviceToken值
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString * deviceTokenStr = @"";
    if ([deviceToken isKindOfClass:[NSData class]]){
      const unsigned *tokenBytes = (const unsigned *)[deviceToken bytes];
      deviceTokenStr = [NSString stringWithFormat:@"%08x%08x%08x%08x%08x%08x%08x%08x",
                        ntohl(tokenBytes[0]), ntohl(tokenBytes[1]), ntohl(tokenBytes[2]),
                        ntohl(tokenBytes[3]), ntohl(tokenBytes[4]), ntohl(tokenBytes[5]),
                        ntohl(tokenBytes[6]), ntohl(tokenBytes[7])];
    }
    
    deviceTokenStr = deviceTokenStr?deviceTokenStr:@"";
    if (![deviceTokenStr isEqual:[defaults objectForKey:@"deviceToken"]]) {
      //这里需要更新deviceToken
      [defaults setObject:deviceTokenStr forKey:@"deviceToken"];
      NSLog(@"deviceToken = %@",[defaults valueForKey:@"deviceToken"]);
      [defaults synchronize];
    }
}
/**
 解析自定义推送消息
 */
+ (NSDictionary *)creactData:(NSDictionary *)dic{
    //解析
    NSMutableDictionary * parmas = @{}.mutableCopy;
    if ([dic isKindOfClass:[NSDictionary class]]) {
        NSDictionary * body = [dic objectForKey:@"body"];
        
        [parmas setValue:[body objectForKey:@"text"] forKey:@"text"];
        if ([dic objectForKey:@"extra"]) {
          if ([[dic objectForKey:@"extra"] objectForKey:@"title"]) {
            [parmas setValue:[[dic objectForKey:@"extra"] objectForKey:@"title"] forKey:@"title"];
          }
          if ([[dic objectForKey:@"extra"] objectForKey:@"text"]) {
            [parmas setValue:[[dic objectForKey:@"extra"] objectForKey:@"text"] forKey:@"text"];
          }
          NSData *jsonData = [NSJSONSerialization dataWithJSONObject:[dic objectForKey:@"extra"] options:NSJSONWritingPrettyPrinted error:NULL];
          
          [parmas setValue:[[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding] forKey:@"extra"];
        }
    }
  
    return parmas;
}
@end


