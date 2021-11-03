
Pod::Spec.new do |s|
  s.name         = "RNUmsdk"
  s.version      = "1.0.0"
  s.summary      = "RNUmsdk"
  s.description  = <<-DESC
                  RNUmsdk
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNUmsdk.git", :tag => "master" }
  s.source_files  = "RNUmsdk/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  