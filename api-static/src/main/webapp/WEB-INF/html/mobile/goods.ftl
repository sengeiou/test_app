<#include "/mobile/header.ftl" />
<section class="page">
<#include "/mobile/search.ftl" />
    <div class="detail">
        <ul page-content>
            <div class="headling">
            <#if goods.title??>
                <h1><font size="3px;" id="title">${goods.title!}</font></h1>
                </br>
            </#if>
            <#if goods.title??>
                <#if goods.alias??>
                    <font size="2px;">${goods.alias!}</font>
                    </br>
                </#if>
            </#if>
            <#if goods.remark != goods.title>
                <#if goods.remark != "">
                    <font size="2px;">${goods.remark!}</font>
                    </br>
                </#if>
            </#if>
            </div>
            <div class="title">
            <#if (goods.image)?? && (goods.image)?length gt 0>
                <img class="imgloading" src="${img}/Goods/source/${goods.image!}@90p" onerror="javascript:this.src='${img}/wx/images/default.jpg'" alt="${goods.title!}" />
            <#else>
                <img class="imgloading" src="${img}/Goods/default.png" onerror="javascript:this.src='${img}/wx/images/default.jpg'" alt="${goods.title!}" />
            </#if>
                <div class="round"><span>
                ${goods.dataTypeStr!}
            </span></div>
            <#if goods.dataType!= 4>
                <a href="#" title="" class="record">备案文号：${goods.approval!}</a>
            </#if>
            </div>
            <div id="sub-layer" class="layer">
                <div class="sub-product">
                    <a href="#" title="" class="close-btn"></a>
                    <table>
                        <tbody>
                        <#if goods.country != "">
                        <tr>
                            <td class="td-first"><strong>生产国/地区:</strong></td>
                            <td>${goods.country!}</td>
                        </tr>
                        </#if>
                        <#if goods.company != "">
                        <tr>
                            <td class="td-first"><strong>生产企业:</strong></td>
                            <td>${goods.company!}</td>
                        </tr>
                        </#if>
                        <#if goods.companyEnglish != "">
                        <tr>
                            <td class="td-first"><strong>生产企业(英文):</strong></td>
                            <td>${goods.companyEnglish!}</td>
                        </tr>
                        </#if>
                        <#if goods.approvalDate??>
                        <tr>
                            <td class="td-first"><strong>批准日期:</strong></td>
                            <td><span id="approval_date">${goods.approvalDate!}</span></td>
                        </tr>
                        </#if>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="purple-box">
            <#if composition??>
                <div class="box">
                    <h3>安全解读</h3>
                    <div class="safe">
                        <div class="anqianxinji" style="color: #333;padding-bottom: 10px;">
                            安全星级:
                            <#list safety as safe>
                                <#if safe.unit == 1>
                                    <#assign xinji = safe.num />
                                </#if>
                            </#list>
                            <#list 1..5 as t>
                                <#if t lte xinji?eval >
                                    <img src="${img}/xiaostar.png" style="width: 14px;height: 14px; "/>
                                <#else/>
                                    <#if t gt xinji?eval && t lt (xinji?eval + 1)>
                                        <img src="${img}/xiaobanstar.png" style="width: 14px;height: 14px; "/>
                                    <#else/>
                                        <img src="${img}/xiaostargree.png" style="width: 14px;height: 14px; " />
                                    </#if>
                                </#if>
                            </#list>
                        </div>
                        <ul style="margin-bottom: 25px;">
                            <#list safety as safe>
                                <#if safe.unit != 1>
                                    <li>
                                        <span class="safe-first">${safe.displayName}<span class="safe-two">${safe.num}种</span></span>
                                        <#if safe.num?eval gt 0>
                                            <span class="safe-arrow" data-id="safe_${safe.id}"></span>
                                        <#else/>
                                            <span class="safe-arrow none" ></span>
                                        </#if>
                                    </li>
                                </#if>
                            </#list>
                        </ul>
                    </div>

                    <h3>${data.appCategoryCateText}</h3>
                    <div class="safe">
                        <ul style="margin-bottom: 25px;">
                            <#list effects as effect>
                                <#if effect.id == -1>
                                    <li>
                                            <span class="safe-first">
                                                ${effect.displayName}
                                                    <span class="safe-two">${effect.num}种</span></span>
                                        <#if effect.num?eval gt 0>
                                            <span class="safe-arrow" data-id="effect_${effect.id}"></span>
                                        <#else/>
                                            <span class="safe-arrow none" ></span>
                                        </#if>
                                    </li>
                                </#if>
                            </#list>
                            <#list effects as effect>
                                <#if effect.id != -1 && ( effect.displayType==0 || effect.displayType==1)>
                                    <li>
                                        <span class="safe-first">${effect.displayName}<span class="safe-two">${effect.num}种</span></span>
                                        <#if effect.num?eval gt 0>
                                            <span class="safe-arrow" data-id="effect_${effect.id}"></span>
                                        <#else/>
                                            <span class="safe-arrow none"  ></span>
                                        </#if>
                                    </li>
                                </#if>
                            </#list>
                        </ul>
                    </div>
                </div>
                <div id="facts-layer" class="layer" style="margin-top: 80px;"></div>
                <div id="all_composition">
                    <div class="table-facts" style="margin-top: 0;">
                        <table>
                            <tbody>
                            <tr class="first-th">
                                <th colspan="5">全成分表</th>
                            </tr>
                            <tr>
                                <td colspan="5" class="biaoqian-text">
                                    <#if (goods.cpsType)?? &&  (goods.cpsType)?has_content && goods.cpsType== 'mfj_cps'>
                                        本产品成分表顺序为产品标签顺序，和产品包装上的标签一致，成分表根据单一成分含量从多到少排序。
                                    <#else/>
                                        <#if (goods.cpsType)?? &&  (goods.cpsType)?has_content && goods.cpsType == 'def_cps_3'>
                                            本产品成分表顺序为产品备案顺序，来自产品在药监局提供的成分表，原则上和产品标签一致，但与厂家和药监局的问题而有例外情况。
                                        <#else/>
                                            本产品成分表顺序为配方备案顺序，成分表根据药监局备案的配方工艺分组排序，和含量无关，与产品包装上的顺序很多不一致。
                                        </#if>
                                    </#if>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="first-td">成份名称</td>
                                <td class="two-td"><div>安全 </div>
                                    <div>风险</div></td>
                                <td class="three-td"><div>活性</div>
                                    <div>成份</div></td>
                                <td class="four-td"><div>致痘</div>
                                    <div>风险</div></td>
                                <td class="five-td"><div>使用</div>
                                    <div>目的</div></td>
                            </tr>
                                <#assign i = 0 />
                                <#list composition as comp>

                                <tr class="<#if i % 2 != 0>odd<#else/>even</#if><#if i gte 10> hide</#if>">
                                    <td>
                                        <#if comp.updateStamp gt 0 && comp.mid!="">
                                            <a href="/composition/${comp.mid!}.html" class="main_color">
                                            ${comp.title!}
                                            </a>
                                        <#else/>
                                        ${comp.title!}
                                        </#if>
                                    </td>
                                    <td>
                                        <span class="label safe-color">${comp.safety!}</span>
                                    </td>
                                    <td>
                                        <#if comp.active == "1">
                                            <span class="label label-good">赞</span>
                                        </#if>
                                        <#if comp.active == "2">
                                            <span class="label label-good">防晒</span>
                                        </#if>
                                        <#if comp.active == "UVAB">
                                            <span class="label label-UVA"></span><span class="label label-UVB"></span>
                                        </#if>
                                        <#if comp.active == "UVA">
                                            <span class="label label-UVA"></span>
                                        </#if>
                                        <#if comp.active == "UVB">
                                            <span class="label label-UVB"></span>
                                        </#if>
                                        <#if comp.active == "">
                                            <span></span>
                                        </#if>
                                    </td>

                                    <#if comp.acneRisk == "1">
                                    <td class="label-danger">
                                    <#else/>
                                    <td>
                                    </#if>
                                </td>

                                    <td>
                                        <#if comp.useds?size gt 0>
                                                ${comp.useds[0].title}
                                            </#if>
                                    </td>
                                </tr>
                                    <#assign i = i+1 />
                                </#list>
                                <#if composition?size gt 10>
                                <tr id="yingcang">
                                    <td colspan="5">
                                        <p style=" margin-top: 15px;"><font size="3px">显示更多</font></p>
                                        <p style=" margin-top: 15px;"><a href="javascript:void(0)" onclick="show_more()"><img src="${img}/wx/images/more.png" /></a></p>
                                    </td>
                                </tr>
                                </#if>
                            </tbody>
                        </table>
                    </div>
                </div>
            <#else/>
                对不起，由于该产品备案时间较早，在国家药监局备案时未提供成分数据。 我们会逐步在产品标签数据库再次补录该产品和成分。
            </#if>
                <br>
                <br>
            </div>
        </ul>
    </div>
</section>
<#include "/mobile/footer.ftl" />
<div style="display:none">
<#list safety as safe>
    <#if safe.num?eval gt 0  && safe.unit?eval !=1 >
        <div id="safe_${safe.id}">
            <div class="table-facts" style="width: 90%;margin:0px auto;margin-top:20px; border-radius:none;boeder:1px solid red;">
                <table>
                    <tr class="even">
                        <td style="text-align:center;color:#fff;line-height:40px;height: 40px;background:#8064a2 none repeat scroll 0 0" colspan="5">
                            <font size= "3px;" weight="bold;">成份名称</font>
                            <a class="close-btn" title="" href="javascript:;" style="float:right; margin-right: 40px;"></a>
                        </td></tr><tr class="odd" style=""><td class="first-td">成份名称</td>
                    <td class="two-td">安全<br>风险</td>
                    <td class="three-td">活性<br>成份</td>
                    <td class="four-td">致痘<br>风险</td>
                    <td class="five-td">使用<br>目的</td>
                </tr>
                </table>
            </div>
            <div class="table-facts" style="width: 90%;height: 70%;overflow-y:auto;margin:30px auto; border-radius:none">
                <table>
                    <tbody style="">
                        <#assign i = 0 />
                        <#list safe.composition as comp>
                        <tr
                            <#if i%2 == 0>
                                    class="even"
                            <#else/>
                                    class="odd"
                            </#if>
                        >
                            <td class="first-td">
                                <a class="comp" href="/composition/${comp.mid!}.html" >${comp.title}</a>
                            </td>
                            <td class="two-td">
                                <span class="label safe-color">${comp.safety}</span>
                            </td>
                            <td class="three-td">
                                <#if comp.active == "1">
                                    <span class="label label-good">赞</span>
                                </#if>
                                <#if comp.active == "2">
                                    <span class="label label-good">防晒</span>
                                </#if>
                                <#if comp.active == "UVAB">
                                    <span class="label label-UVA"></span><span class="label label-UVB"></span>
                                </#if>
                                <#if comp.active == "UVA">
                                    <span class="label label-UVA"></span>
                                </#if>
                                <#if comp.active == "UVB">
                                    <span class="label label-UVB"></span>
                                </#if>
                                <#if comp.active == "">
                                    <span></span>
                                </#if>
                            </td>
                            <td class='four-td <#if comp.acneRisk == "1">label-danger</#if>'>
                            </td>
                            <td class="five-td">${comp.useds[0].title}</td>
                        </tr>
                            <#assign i = i+1 />
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </#if>
</#list>
<#list effects as effect>
    <#if effect.num?eval gt 0  && effect.unit != -1>
        <div id="effect_${effect.id}">
            <div class="table-facts" style="width: 90%;margin:0px auto;margin-top:20px; border-radius:none;boeder:1px solid red;">
                <table>
                    <tr class="even">
                        <td style="text-align:center;color:#fff;line-height:40px;height: 40px;background:#8064a2 none repeat scroll 0 0" colspan="5">
                            <font size= "3px;" weight="bold;">成份名称</font>
                            <a class="close-btn" title="" href="javascript:;" style="float:right; margin-right: 40px;"></a>
                        </td></tr><tr class="odd" style=""><td class="first-td">成份名称</td>
                    <td class="two-td">安全<br>风险</td>
                    <td class="three-td">活性<br>成份</td>
                    <td class="four-td">致痘<br>风险</td>
                    <td class="five-td">使用<br>目的</td>
                </tr>
                </table>
            </div>
            <div class="table-facts" style="width: 90%;height: 70%;overflow-y:auto;margin:30px auto; border-radius:none">
                <table>
                    <tbody style="">
                        <#assign i = 0 />
                        <#list effect.composition as comp>
                        <tr
                            <#if i%2 == 0>
                                    class="even"
                            <#else/>
                                    class="odd"
                            </#if>
                        >
                            <td class="first-td">
                                <a class="comp" href="/composition/${comp.mid!}.html" >${comp.title}</a>
                            </td>
                            <td class="two-td">
                                <span class="label safe-color">${comp.safety}</span>
                            </td>
                            <td class="three-td">
                                <#if comp.active == "1">
                                    <span class="label label-good">赞</span>
                                </#if>
                                <#if comp.active == "2">
                                    <span class="label label-good">防晒</span>
                                </#if>
                                <#if comp.active == "UVAB">
                                    <span class="label label-UVA"></span><span class="label label-UVB"></span>
                                </#if>
                                <#if comp.active == "UVA">
                                    <span class="label label-UVA"></span>
                                </#if>
                                <#if comp.active == "UVB">
                                    <span class="label label-UVB"></span>
                                </#if>
                                <#if comp.active == "">
                                    <span></span>
                                </#if>
                            </td>
                            <td class='four-td <#if comp.acneRisk == "1">label-danger</#if>'>
                            </td>
                            <td class="five-td">${comp.useds[0].title}</td>
                        </tr>
                            <#assign i = i+1 />
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </#if>
</#list>
</div>
<script type="text/javascript">

    $(".safe-arrow").click("click",function(){
        var oFactsLayer = $("#facts-layer");
        $(document).on("click", "#facts-layer .close-btn", function(){
            oFactsLayer.hide().html('');
        });
        var id = $(this).data("id");
        oFactsLayer.html($('#'+id).html()).show();
    });

    $(function(){
        var safe_color = document.getElementsByClassName("safe-color");
        for(var i=0; i< safe_color.length; i++){
            var numLength = safe_color[i].innerText.length;
            var num01 = safe_color[i].innerText;

            if(numLength == 1 && num01 < 3 || numLength > 1 && num01.split("-")[1] <= 3){
                safe_color[i].style.background = "#5cb85c";
            }else if(numLength == 1 && num01 < 7 || numLength > 1 && num01.split("-")[1] <= 7){
                safe_color[i].style.background = "#f0ad4e";
            }else if(numLength == 1 && num01 >= 7 || numLength > 1 && num01.split("-")[1] > 7){
                safe_color[i].style.background = "#c13636";
            }else{
                safe_color[i].style.background = "";
            }
        }

        var date = $("#approval_date").html().replace(/,/g, "");
        date = $.toDate(date)
        $("#approval_date").html(date);
    })

    function show_more(){
        $("#all_composition tr").removeClass("hide")
        $("#yingcang").css("display","none");
    }

    $(document).on("click",".record",function(){
        $("#sub-layer").show();
    });

    $(document).on("click", "#sub-layer .close-btn", function(){
        $("#sub-layer").hide();
    });
    $.toDate=function(d) {
        if(!d) return '';
        if((d+"").length==10) d=d*1000;
        var date = new Date(d);

        var Y = date.getFullYear() + '-';

        var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';

        var D = date.getDate() + ' ';
        return (Y+M+D);
    }
</script>
