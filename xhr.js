function xhrFormRequest(url, formData, ContentType, callback, errorback, callFullData){
    let loading = null;
    setTimeout(() => {
        vue ? loading = vue.$loading({ lock: true, spinner: 'el-icon-loading', background: 'rgba(0, 0, 0, 0)' }) : false;    
    }, 50);
	var xhr = new XMLHttpRequest();
	xhr.upload.addEventListener("progress", uploadProgress, false); //设置上传进度监控
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 ) {
            loading ? loading.close() : false;
            if( xhr.status==200 ){
                if( !xhr.responseText ){
                    console.log("none response body return",url);
                    return;
                }
                try{
                    var json = JSON.parse(xhr.responseText);
                    if( json.status!=0 )
                    {
                        if( json.rows ){
                            typeof callback=="function" && callback( json );
                            return;
                        }

                        if( errorback && typeof errorback=="function" ){
                            errorback(json)
                            return;
                        }

                        if( json.status==2 ){
                            delCookie("xt_mobile_login_token");
                            localStorage.clear();
                            sessionStorage.clear();
                            //localStorage.setItem(Back.LOGIN, location.href);
                            location.replace("/login.html");
                            return;
                        }else if( json.status==1 && json.msg.indexOf('未查询')==0 && json.msg.indexOf('排班')<0 && json.msg.indexOf('医嘱')<0 ){
                            return;
                        }

                        vue ? vue.$message({message:json.msg, type:'warning', center:true}) : false;
                    }else{
                        if( callFullData ){
                            typeof callback=="function" && callback( json );
                        }else{
                            typeof callback=="function" && callback( json.data || '' );
                        }
                    }
                }catch(e){
                    console.error('response.error',e);
                }
            }else{
                //window.alert('Network Error!');
                console.error('Network Error!');
            }
		}
	}
    xhr.open("POST", url, true);        // method,url,async
    xhr.setRequestHeader("Content-type", ContentType);
    //xhr.setRequestHeader("Origin", location.origin);
	// default content-type : multipart/form-data
    //                        application/x-www-form-urlencoded
    xhr.withCredentials = true;
	xhr.send(formData);
}

function uploadProgress(evt) {  
	if (evt.lengthComputable) {  
		var percentComplete = Math.round(evt.loaded * 100 / evt.total);
		//console.log("uploadProgress", percentComplete+"%");
	}else {  
		console.log("unable to compute");
	}
}

var xhrOnProgress = function(fun) {
    xhrOnProgress.onprogress = fun; //绑定监听
    return function() {
        //通过$.ajaxSettings.xhr();获得XMLHttpRequest对象
        var xhr = $.ajaxSettings.xhr();
        //判断监听函数是否为函数
        if (typeof xhrOnProgress.onprogress !== 'function')
            return xhr;
        //如果有监听函数并且xhr对象支持绑定时就把监听函数绑定上去
        if (xhrOnProgress.onprogress && xhr.upload) {
            xhr.upload.onprogress = xhrOnProgress.onprogress;
            console.log(xhr.upload.onprogress);
        }
        return xhr;
    }
}
