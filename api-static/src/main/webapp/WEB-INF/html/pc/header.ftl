<!DOCTYPE html>
<html lang="zh-cn">
<head>
<#if staticType??>
    <#if staticType=="find">
        <#assign findClass="active">
    <#elseif staticType=="product">
        <#assign goodsClass="active">
    <#elseif staticType="composition">
        <#assign goodsClass="active">
    <#else> <!-- 行业资讯 -->
        <#assign indutryClass="active">
    </#if>
</#if>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="${description!}">
    <meta name="keywords" content="${keywords!}">
    <link rel="shortcut icon" href="${img}/www.ico.la_af49e6c91a9a793c3aa1e02f8f22f91a_30x30.ico"/>
    <title>${title!}</title>
    <base target="_blank" />
    <link rel="stylesheet" href="${css!'https://static.bevol.cn/pc/css'}/pc_style_20170221.css" />
    <meta name="robots" content="none" />
    <base target="_blank" href="https://www.bevol.cn/"/>
    <script src="${js!'https://static.bevol.cn/pc/js'}/jquery.min.js" type="text/javascript"></script>
    <script src="${js!'https://static.bevol.cn/pc/js'}/public_20170209.js" type="text/javascript"></script>
    <script src="${js!'https://static.bevol.cn/pc/js'}/jquery.cookie.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="${js!'https://static.bevol.cn/pc/js'}/base_2017010901.js"></script>

    <script type="text/javascript">
        //baidu统计
        var _hmt = _hmt || [];
        (function() {
            var hm = document.createElement("script");
            hm.src = "//hm.baidu.com/hm.js?14d9852b3b43958dbf0ebac06426979c";
            var s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>
    <script>
        //360统计
        (function(){
            var src = (document.location.protocol == "http:") ? "http://js.passport.qihucdn.com/11.0.1.js?045a03941aa457762ba1f68f18dfc14a":"https://jspassport.ssl.qhimg.com/11.0.1.js?045a03941aa457762ba1f68f18dfc14a";
            document.write('<script src="' + src + '" id="sozz"><\/script>');
        })();
    </script>
</head>
<body>

<div class="container">
    <!--头部导航-->
    <div class="main-header">
        <div class="header-logo">
            <img src="https://static.bevol.cn/pc/images/pcpicture/logo.png" alt="美丽修行">
        </div>
        <div class="main-col"></div>
        <div class="header-nav" style="display: block;">
            <ul>
                <ul>
                    <li><a data-index target="_self" href="/" alt="首页">首页</a></li>
                    <li><a data-download target="_self" href="/download.html" alt="下载APP">下载APP</a></li>
                    <li><a data-category target="_self" href="/product?v=2.0&category=6" class="${goodsClass!}" alt="化妆品分类">妆品分类</a></li>
                    <li><a data-find target="_self" href="/find" class="${findClass!}" alt="往期文章">往期文章</a></li>
                    <li><a data-contact target="_self" href="/contact.html" alt="联系我们">联系我们</a></li>
                    <li><a data-question target="_self" href="/question.html" alt="Q&A">Q&A</a></li>
                    <li>&nbsp;</li>
                    <li><a data-my target="_self" href="/my/index.html" id="myinfo" alt="登录">登录</a></li>
                    <div class="clear"></div>
                </ul>
        </div>
        <div class="clear"></div>
    </div>