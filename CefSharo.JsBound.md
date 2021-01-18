# WPF CefSharp 内核JS交互绑定

```
using CefSharp;
using Microsoft.Web.WebView2.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace HIS.common.winEntity
{
    public class JsBoundObject
    {
        protected IWebBrowser webBrowser;
        protected Window Owner;
        private Action<string> jsAction;

        /**
         * 供JS调用的方法名必须小心，调用时javascript会将调用方法名转小写，导致无法找到方法
         */

        public JsBoundObject(IWebBrowser webBrowser, Window owner, Action<string> jsAction = null)
        {
            this.webBrowser = webBrowser;
            this.Owner = owner;
            this.jsAction = jsAction;
        }

        [JavascriptIgnore]
        public void RunCommand(string jsCommand)
        {
            try
            {
                this.webBrowser.GetMainFrame().ExecuteJavaScriptAsync(jsCommand);
            }
            catch (Exception ex)
            {
                LogUtil.Error( string.Format("JsBoundObject.RunCommand:{0}:{1}", ex.Message, jsCommand));
            }
        }

        [JavascriptIgnore]
        public void RunCallback(string jsCommand,Action<bool,object> callback)
        {
            try
            {
                Task<JavascriptResponse> task = this.webBrowser.GetMainFrame().EvaluateScriptAsync(jsCommand);
                task.ContinueWith(t =>
                {
                    if (!t.IsFaulted)
                    {
                        var response = t.Result;
                        if (response.Success)
                        {
                            callback(true, response.Result);
                        }
                        else
                        {
                            callback(false, response.Message);
                        }
                    }
                });
            }
            catch (Exception ex)
            {
                LogUtil.Error(string.Format("JsBoundObject.RunCallback:{0}:{1}", ex.Message, jsCommand));
            }
        }

        public void Jsalert(string str)
        {
            Console.WriteLine("alert: {0}", str);
        }

        public void Jslog(params object[] o)
        {
            Console.WriteLine(o);
        }

        public void JslogOut()
        {
            this.Owner.Close();
        }

        public async Task<string> ajax(string uri, object data)
        {
            if (data!=null)
            {
                if (data.GetType() == typeof(string))
                {
                    return await HttpUtil.PostFormAsyncString(HIS.common.config.ConfigUtil.Host + uri, data.ToString());
                }
                else
                {
                    try
                    {
                        var json = Newtonsoft.Json.Linq.JObject.FromObject(data);
                        List<string> list = new List<string>();
                        foreach (var key in json.Properties())
                        {
                            list.Add($"{key.Name}={key.Value?.ToString() ?? ""}");
                        }
                        return await HttpUtil.PostFormAsyncString(HIS.common.config.ConfigUtil.Host + uri, string.Join("&", list));
                    }
                    catch (Exception)
                    {
                        return "{success:false, message:'参数错误'}";
                    }
                }
            }
            else
            {
                return await HttpUtil.PostFormAsyncString(HIS.common.config.ConfigUtil.Host + uri, "");
            }
        }

        public void close()
        {
            this.Owner?.Close();
        }

        public string dictname(string dict, string value)
        {
            if (string.IsNullOrEmpty(dict) || string.IsNullOrEmpty(value))
            {
                return "";
            }
            return HIS.common.系统接口.字典接口.GetName(dict, value);
        }

        public void action(string data)
        {
            if (this.jsAction!=null) {
                this.jsAction(data);
            }
        }

        /**
         * CefSharp.BindObjectAsync("webkit").then(function (sharp) {
         *     webkit.ajax('uploadPatientCheck/uploadPatientCheck/queryHospitalizedRegisterList.web', {name:'aaa'}).then(function (res) {
         *     
         *     });
         * });
         */
    }
}

```
