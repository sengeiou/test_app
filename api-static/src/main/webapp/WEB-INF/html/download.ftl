<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>正在跳转。。。</title>
<script src="${js!'https://static.bevol.cn/pc/js'}/jquery.min.js" type="text/javascript"></script><script type="text/javascript">
//判断访问终端
var browser={
    versions:function(){
        var u = navigator.userAgent, app = navigator.appVersion;
        return {
            trident: u.indexOf('Trident') > -1, //IE内核
            presto: u.indexOf('Presto') > -1, //opera内核
            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,//火狐内核
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
            android: u.indexOf('Android') > -1 || u.indexOf('Adr') > -1, //android终端
            iPhone: u.indexOf('iPhone') > -1 , //是否为iPhone或者QQHD浏览器
            iPad: u.indexOf('iPad') > -1, //是否iPad
            webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部
            weixin: u.indexOf('MicroMessenger') > -1, //是否微信 （2015-01-22新增）
            qq: u.match(/\sQQ/i) == " qq" //是否QQ
        };
    }(),
    language:(navigator.browserLanguage || navigator.language).toLowerCase()
}
</script>
<script type="text/javascript">


</script>
<script type="text/javascript">
//    var curWwwPath=window.document.location.href;
//    //获取主机地址之后的目录如：/Tmall/index.jsp
//   var pathName=window.document.location.pathname;
//   var pos=curWwwPath.indexOf(pathName);
//
//   //获取主机地址，如： http://localhost:8080
//   var localhostPaht=curWwwPath.substring(0,pos);
   var url ="/static/download/add";
   var dataSource ="" ;
   var dataSource2 ="";
    //判断是微信
    if(browser.versions.weixin){
	 dataSource = "weixin";

    }
    //判断是否QQ
    if(browser.versions.qq){
	 dataSource = "qq";
    }
    //判断是否webApp
    if(browser.versions.webApp){
        dataSource = "webApp";
    }
    //判断是否pc端
    if(browser.versions.trident||browser.versions.gecko||browser.versions.presto){
        dataSource = "pc";

    }

    if(browser.versions.android){
        dataSource2="android";
    }
    if(browser.versions.ios || browser.versions.iPad ||browser.versions.iPhone ){
        dataSource2="ios"
    }
    var qrcodeId = "${qrcodeId!}";
    var qrcodeName = "${qrcodeName!}";
    var androidUrl = "${androidUrl!}";
    var iosUrl = "${iosUrl!}";
    console.log("url:"+url+";qrcodeId:"+qrcodeId+";dataSource:"+dataSource);
    $.ajax({
        url : url,
        type:'post',
        xhrFields:{withCredentials: true},
        data : {
            qrcodeId:qrcodeId,
            qrcodeName:qrcodeName,
            dataSource:dataSource,
            dataSource2:dataSource2
        },
        success : function(data) {
           // if(browser.versions.ios ||browser.versions.iPhone||browser.versions.iPad){
               //iosurl = "https://itunes.apple.com/app/id1089854728"
             //   window.location.href =iosUrl;
           // }else{
                // androidurl ="http://a.app.qq.com/o/simple.jsp?pkgname=cn.bevol.p#opened";
                window.location.href =androidUrl;
          //  }
        },
        async:false
    });

</script>
</script>
</head>
<body>

</body>
</html>