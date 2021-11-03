using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Umsdk.RNUmsdk
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNUmsdkModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNUmsdkModule"/>.
        /// </summary>
        internal RNUmsdkModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNUmsdk";
            }
        }
    }
}
