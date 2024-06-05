
Pod::Spec.new do |s|
  s.name         = "RNUmsdk"
  s.version      = "1.0.4"
  s.summary      = "RNUmsdk"
  s.description  = <<-DESC
                  RNUmsdk
                   DESC
  s.homepage     = "https://github.com/kunkun-s/react-native-umsdk.git"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNUmsdk.git", :tag => "master" }
  s.source_files  = "ios/*.{h,m}","ios/libs/share/**/SocialLibraries/*/UMSocial*Handler.h","ios/class/**/*.{h,m}","ios/libs/share/**/SocialLibraries/WeChat/WechatSDK/*.h"

  s.requires_arc = true
=begin
  推荐使用s.dependency自动引用依赖，避免手动引用导致重复。
  如果手动引用了OpenSdk，会导致与umsdk冲突，
    解决方式1.所有OpenSdk都用自动引用。
    方式2：在Podfile中pre_install方法内删除UMSDK的WechatSDK。
  同时集成Alipay也会导致UTDID重复引用。
    解决：1.将UMSDK -> thirdparties -> UTDID 和 pod 'UMCSecurityPlugins' 删除
=end
  # 或略WechatSDK 采用自动依赖，工程预留WechatSDK 是防止pod不能下载成功，做备份。
  s.exclude_files = "ios/libs/**/WeChat/WechatSDK/*",
  # 系统的依赖库
  s.framework = "CoreTelephony","SystemConfiguration","WebKit","UserNotifications"
  # 私有framework
  s.vendored_frameworks = 'ios/libs/**/*.framework'
  # 系统的.a
  s.libraries = 'z','sqlite3','c++'
  # 私有的.a
  s.vendored_libraries = 'ios/libs/**/*.a'
  # 引入资源文件
  s.resources = ['ios/libs/**/*.bundle']
  # 资源文件 将资源文件打包成bundle
  s.resource_bundles = {'RNUMSDK' => ['ios/images/*'] }
  # 依赖其他的pod库
  s.dependency "React"
  # UM错误分析升级为独立SDK，看crash数据请务必集成，可选
  s.dependency "UMAPM",'~> 1.5.3'
  #s.dependency "others"
  s.dependency 'WechatOpenSDK',"~> 1.8.7.1"
end

  
