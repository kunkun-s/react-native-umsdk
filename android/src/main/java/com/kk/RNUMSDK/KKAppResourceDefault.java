package com.kk.rnumsdk;

//在主程序中新建AppResourceDefault 并集成KKAppResource 实现getDrawableResourceId 然后传递给RNUmsdkimpl
public class  KKAppResourceDefault implements KKAppResource{
    public KKAppResourceDefault(){

    }
    @Override
    public int getDrawableResourceId(String key) {
        // 根据key返回对应的drawable资源ID
        switch (key) {
            case "share":
                return R.drawable.share;
            default:
                return 0;
        }
    }

}
