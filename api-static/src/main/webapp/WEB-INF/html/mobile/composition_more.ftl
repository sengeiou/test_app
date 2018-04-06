<!DOCTYPE html>
<html lang="zh-cn">

<head>
    <meta charset="utf-8">
    <title>${title_more!}</title>
    <meta name="description" content="美丽修行网移动站点为您提供48万种以上化妆品、护肤品、保养品品牌的成分权威数据查询，提供中国最全最权威的化妆品与成分数据查询服务，包括洁面、化妆水、精华、乳霜、眼霜、面膜、防晒、洗护等产品查询，彩妆教程，美妆心得，护肤指南，最资深的专家用户为您提供权威的产品点评，中国最全的INCI查询、cosdna查询类网站，微信关注美丽修行和下载APP随时随地科学安全的保护您的皮肤。">
    <meta name="keywords" content="${keywords_more!}">

    <meta name="format-detection" content="telephone=no">
    <meta name="viewport" content="width=device-width, maximum-scale=1, user-scalable=0">

    <!-- styles -->
    <link rel="stylesheet" type="text/css" href="${css}/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="${css}/global_2017011001.css">
    <!-- javascript -->
    <script type="text/javascript" src="${js}/jquery.min.js"></script>
    <script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
    <script type="text/javascript" src="${js}/base_2017010901.js"></script>


    <script type="text/javascript" src="${js}/bootstrap.min.js"></script>
    <script type="text/javascript" src="${js}/bootstrap-select.min.js"></script>
    <script type="text/javascript" src="${js}/jquery.uniform.min.js"></script>
    <script type="text/javascript" src="${js}/common.js"></script>
    <script>
        //baidu统计
        var _hmt = _hmt || [];
        (function() {
            var hm = document.createElement("script");
            hm.src = "//hm.baidu.com/hm.js?10d4a1d4aceb938e0505be5df867b453";
            var s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>

    <script type='text/javascript'>
        var _vds = _vds || [];
        window._vds = _vds;
        (function(){
            _vds.push(['setAccountId', 'bc5f7463b64b67bf']);
            (function() {
                var vds = document.createElement('script');
                vds.type='text/javascript';
                vds.async = true;
                vds.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'dn-growing.qbox.me/vds.js';
                var s = document.getElementsByTagName('script')[0];
                s.parentNode.insertBefore(vds, s);
            })();
        })();
    </script>

</head>

<body>
<link rel="stylesheet" href="${js}/laypage/skin/laypage.css" />
<style>
    .title {
        text-align: center;
    }

    .data-list {
        margin: 10px;
    }

    table {
        border: none;
        text-align: center;
    }

    table tr {
        height: 30px;
        line-height: 20px;
    }

    table tr:nth-child(odd) {
        background: #fff;
    }

    table tr:nth-child(even) {
        background: #f8f5fa;
    }

    table td {
        border-bottom: 1px solid #fff;
        border-right: 1px solid #fff;
        color: #7858a0;
        vertical-align: middle;
        padding: 5px;

    }

    table td a {
        color: #7858a0;
    }

    table .pull-tl {
        text-align: left;
    }
</style>

<div class="main">
    <div class="container product-detail">
        <br>
        <div page-content>
            <p style="background: #C9A1EF;color: #fff;text-align: center;padding: 10px 0;">含有该成分的产品</p>
            <div class="img-txtlist">
                <ul>
                    <#list items as item>
                        <li>
                            <div class="img">
                                <a href="/product/${item.mgoods_id}.html">
                                    <#if item.image??>
                                        <img onerror="javascript:this.src='${img}/wx/images/default.jpg'" src="${img}/Goods/source/${item.image}" alt="${item.title}">
                                        <#else/>
                                        <img onerror="javascript:this.src='${img}/wx/images/default.jpg'" src="${img}/wx/images/default.jpg" alt="${item.title}">
                                    </#if>
                                </a>
                            </div>
                            <div class="text">
                                <h4><a href="/product/${item.mgoods_id}.html">${item.title}</a></h4>
                                <p class="exp">
				                    <span class="lable-info">
										<#if item.data_type == "1">
                                            进口备案
                                        </#if>
                                        <#if item.data_type == "2">
                                            国产备案
                                        </#if>
                                        <#if item.data_type == "3">
                                            国产备案
                                        </#if>
                                        <#if item.data_type == "4">
                                            产品标签
                                        </#if>
									 </span>
                                </p>
                            </div>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
        <div id="page11" style="float:right;width:100%;text-align:right;"></div>
        <div id="view7" class="page-links"></div>
    </div>
</div>
<#include "/footer.html" />
<script type="text/plain" page-tpl id="page-tpl">
	<p style="background: #C9A1EF;color: #fff;text-align: center;padding: 10px 0;">含有该成分的产品</p>
		<%if(items !="") {%>
            <div class="img-txtlist">
                <ul>
		            <%for(var i=0;i<data.items.length;i++) { var item=data.items[i];%>
						<li>
				            <div class="img">
				                <a href="/product/<%=item.mgoods_id%>.html">
				                <!-- /Uploads/Goods/source/201507 -->
									<%if(item.image) {%>
				                    <img onerror="javascript:this.src='/Web/Tpl/Mobile/default/Public/images/default.jpg'" src="https://img0.bevol.cn/Goods/source/<%=item.image%>@80p" alt="<%=item.title%>">
				               		<%}else {%>
									   <img onerror="javascript:this.src='/Web/Tpl/Mobile/default/Public/images/default.jpg'" src="https://img0.bevol.cn/Goods/default.png" alt="<%=item.title%>">
									<%}%>
								 </a>
				            </div>
				            <div class="text">
				                <h4><a href="/product/<%=item.mgoods_id%>.html"><%=item.title%></a></h4>
				                <p class="exp">
				                    <span class="lable-info">
										<%if(item.data_type){%>
											<%if(item.data_type==1) {%>
							  	    				<%='进口备案'%>
							  	    			<%} else if(item.data_type==2) {%>
													<%='国产备案'%>
							  	    			<%} else if(item.data_type==3) {%>
													<%='国产备案'%>
							  	    			<%} else if(item.data_type==4) {%>
													<%='产品标签'%>
							  	    			<%}%>
							  	    		<%}else{%>
							  	    			暂待
							  	    		<%}%>
									 </span>
				                </p>
				            </div>
				        </li>
					<%}%>
                </ul>
            </div>
        <%}%>
<input type="hidden" type-hit="goods" data-title="<%=title%>" data-id="<%=id%>"  />
</script>
<script type="text/javascript" src="${js}/laypage/laypage.js"></script>
<script type="text/javascript" src="${js}/arttmpl.js?v1.0.2"></script>
<script>
    var p="p";
    var page=$.getUrlField(p);
    page = page?page:1;
    var url2="https://api.bevol.cn/search/composition/goodslists?compositionid=${(composition.id)?c}&p="+page;
    $.ajax({
        url:url2,
        data:{},
        dataType:"jsonp",
        jsonp: "callback",
        success:function(data) {
            var cont= template("page-tpl",data);
            $("[page-content]").html(cont);
            var pageto;
            if(data.data.total/20<1){
                pageto=1;
            }else{
                pageto=Math.ceil(data.data.total/20);
            }
            $('#view7').html(data.data.total+'条，第'+page+'页，一共有：'+pageto+'页');
            //$.wxfx();
            laypage({
                cont: $("#page11"),
                pages: pageto, //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
                prev: '<', //若不显示，设置false即可
                next: '>' ,//若不显示，设置false即可
                curr: page || 1, //当前页
                jump: function(obj, first){ //触发分页后的回调
                    if(!first){ //点击跳页触发函数自身，并传递当前页：obj.curr
                        window.location.href="/composition/goods/${composition.mid}.html?p="+obj.curr;
                    }
                }
            });

        }
    });
</script>