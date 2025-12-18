package com.kk.rnumsdk;

public interface KKAppResource {
    default int getDrawableResourceId(String key){
        switch (key) {
            case "share":
                return R.drawable.share;
            default:
                return 0;
        }
    };
}
