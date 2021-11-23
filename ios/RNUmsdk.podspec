
Pod::Spec.new do |s|
  s.name         = "RNUmsdk"
  s.version      = "1.0.0"
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
  s.source_files  = "*.{h,m}","libs/share/**/SocialLibraries/*/UMSocial*Handler.h","class/**/*.{h,m}","libs/share/**/SocialLibraries/WeChat/WechatSDK/*.h"

  s.requires_arc = true

  # 排除那些文件 如果集成了微信支付，则WechatSDK的内容会与微信官方的OpenSdk重复。
  # s.exclude_files = "libs/**/WeChat/WechatSDK/*"
  # 系统的依赖库
  s.framework = "CoreTelephony","SystemConfiguration","WebKit","UserNotifications"
  # 私有framework
  s.vendored_frameworks = 'libs/**/*.framework'
  # 系统的.a
  s.libraries = 'z','sqlite3','c++'
  # 私有的.a
  s.vendored_libraries = 'libs/**/*.a'
  # 引入资源文件
  s.resources = ['libs/**/*.bundle']
  # 资源文件 将资源文件打包成bundle
  s.resource_bundles = {'RNUMSDK' => ['images/*'] }
  # 依赖其他的pod库
  s.dependency "React"
  # UM错误分析升级为独立SDK，看crash数据请务必集成，可选
  s.dependency "UMAPM"
  #s.dependency "others"

end

  
