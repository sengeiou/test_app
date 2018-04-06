<#include "/mobile/header.ftl" />
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
        <div class="sbp">
            <table cellspacing="0" cellpadding="0" width="100%" class="composition_list">
                <tbody>
                <tr>
                    <th class="title" colspan="2">成分详解</th>
                </tr>
                <tr>
                    <td width="20%">中文名：</td>
                    <td><h1>${composition.title!}</h1></td>
                </tr>
                <tr class="title">
                    <td class="first">英文名(INCI)：</td>
                    <td>${composition.english!}</td>
                </tr>
                <#if composition.cas != "0">
                    <tr class="title">
                        <td class="first">CAS号：</td>
                        <td>${composition.cas!}</td>
                    </tr>
                </#if>
                <#if useds?size gt 0>
                    <tr class="title">
                        <td class="first">使用目的：</td>
                        <td>
                            <#list useds as used>
                                ${used.title}
                            </#list>
                        </td>
                    </tr>
                </#if>
                <#if composition.otherTitle !="">
                    <tr class="title">
                        <td class="first">其他名称：</td>
                        <td>${composition.otherTitle!}</td>
                    </tr>
                </#if>
                <#if composition.remark !="">
                    <tr class="title">
                        <td class="first">成分概述：</td>
                        <td>${composition.remark}</td>
                    </tr>
                </#if>
                </tbody>
            </table>
            <br>
            <br>
            <table cellspacing="0" cellpadding="0" width="100%">
                <tbody>
                <tr>
                    <th class="title" colspan="2">安全提示</th>
                </tr>
                <#if composition.acneRisk != "0">
                    <tr class="title odd">
                        <td class="first">致痘风险</td>
                        <td class="label-danger" style="background-color: #d8d3e0;"></td>
                    </tr>
                </#if>
                <tr class="title even">
                    <td class="first">安全风险</td>
                    <td class="label safe-color">${composition.safety}</td>
                </tr>
                </tbody>
            </table>
            <br>
            <br>
        </div>
        <div page-content>
            <p style="background: #C9A1EF;color: #fff;text-align: center;padding: 10px 0;">含有该成分的产品</p>
            <#if items?size gt 0 >
                <div class="img-txtlist">
                    <ul>
                        <#list items as good>
                            <li>
                                <div class="img">
                                    <a href="/product/${good.mgoods_id}.html">
                                        <#if good.image??>
                                            <img onerror="javascript:this.src='${img}/wx/images/default.jpg'" src="${img}/Goods/source/${good.image}@90p" alt="${good.title}">
                                            <#else/>
                                            <img onerror="javascript:this.src='${img}/wx/images/default.jpg'" src="${img}/wx/images/default.jpg@90p" alt="${good.title}">
                                        </#if>
                                    </a>
                                </div>
                                <div class="text">
                                    <h4><a href="/product/${good.mgoods_id}.html">${good.title}</a></h4>
                                    <p class="exp">
				                    <span class="lable-info">
                                        <#if good.data_type == "1">
                                            进口备案
                                        </#if>
                                        <#if good.data_type == "2">
                                            国产备案
                                        </#if>
                                        <#if good.data_type == "3">
                                            国产备案
                                        </#if>
                                        <#if good.data_type == "4">
                                            产品标签
                                        </#if>
									 </span>
                                    </p>
                                </div>
                            </li>
                            <#if good_index + 1 gte 3><#break/></#if>
                        </#list>
                    </ul>
                </div>
            </#if>
        </div>
    </div>
</div>
<#include "/mobile/footer.ftl" />
<script>
    $(function() {
        var safe_color = document.getElementsByClassName("safe-color");
        for (var i = 0; i < safe_color.length; i++) {
            var numLength = safe_color[i].innerText.length;
            var num01 = safe_color[i].innerText;

            if (numLength == 1 && num01 < 3 || numLength > 1 && num01.split("-")[1] <= 3) {
                safe_color[i].style.background = "#5cb85c";
            } else if (numLength == 1 && num01 < 7 || numLength > 1 && num01.split("-")[1] <= 7) {
                safe_color[i].style.background = "#f0ad4e";
            } else if (numLength == 1 && num01 >= 7 || numLength > 1 && num01.split("-")[1] > 7) {
                safe_color[i].style.background = "#c13636";
            } else {
                safe_color[i].style.background = "";
            }
        }
    })
</script>