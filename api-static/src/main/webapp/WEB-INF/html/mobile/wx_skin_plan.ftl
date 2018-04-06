<#include "/mobile/wx_share_header.ftl" />
<link href="https://static.bevol.cn/wx/css/wx_skin_plan.css" rel="stylesheet" />
<div class="main">
    <div class="header">
        <h5>快来看看我在美丽修行上传的护肤方案吧！——${result.category.categoryName!'护肤'}</h5>
        <ul id="dpuser" class="comment-answer-ul-box">
            <li class="comment-answer-left">
                <img src="${result.userinfo.headimgurl}@90p" onerror="javascript:this.src='${img}/wx/images/newyixiujie.png@90p'" alt="" class="head-box-img-url" />
            </li>
            <li class="comment-answer-right">
                <div class="span01">${result.userinfo.nickname}</div>
            <#if (result.userinfo.skinResults)??>
                <div class="head-skin">
                    <span>${result.userinfo.skinResult1}</span>
                    <span>${result.userinfo.skinResult2}</span>
                    <span>${result.userinfo.skinResult3}</span>
                    <span>${result.userinfo.skinResult4}</span>
                </div>
            </#if>
            </li>
            <div class="clear"></div>
        </ul>
    </div>
    <div class="info">
    <#list result.subCategory as category_list>
        <#assign showSubCategoryName = true />
        <#list result.list as goods_list>
            <#if category_list.id == goods_list.categoryId>
                <#if showSubCategoryName>
                <div class="info-box">
                    <h4>${category_list.categoryName}</h4>
                <ul>
                    <#assign showSubCategoryName = false />
                </#if>
                <a <#if goods_list.entityInfo.mid??> href="/product/${goods_list.entityInfo.mid}.html" </#if> alt="${goods_list.entityInfo.title!}">
                <li>
                    <div class="goods-img">
                        <p><img src="${goods_list.entityInfo.imgSrc}@80p" onerror="javascript:this.src='${img}/wx/images/default.jpg@80p'" class="img_z"></p>
                    </div>
                    <div class="goods-text">
                        <p class="p1">${goods_list.entityInfo.title!}</p>
                        <p class="p2">${goods_list.entityInfo.alias!}</p>
                    </div>
                    <div class="clear"></div>
                </li>
                </a>
            </#if>
        </#list>
        <#if !showSubCategoryName>
        </ul>
        </div>
        </#if>
    </#list>
    </div>
</div>
<#include "/mobile/wx_share_footer.ftl" />