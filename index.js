
import { NativeModules } from 'react-native';
import * as _push from './js/push';
import * as _Share from './js/share';
import * as _UMSdk from './js/UMSdk';

export const RNUmsdk = NativeModules.RNUmsdk;

export const Push = _push;
export const Share = _Share;
export const UMSdk = _UMSdk;

