<div class="page_head">
    <img src="https://img${n}.bevol.cn/article/${id}/${header_image}@80p" class="img_z">
</div>
<div class="page_info_main01" style="position: relative; left: 0px; top: 0px;">
    <div class="page_main_title">${title}</div>
    <div style="font-size: 12px;margin: 10px 0 0 0;color: #979797;">
        <span id="hitNum">${hitNum!"0"}</span>阅读 | <span id="commentNum">${commentNum!"0"}</span>评论
    </div>
    <div class="page_line"></div>
    <div class="page_lists_box">
        <ul>
            <#if tag?size gt 0 >
                <#list tag as vo>
                    <li>${vo}</li>
                </#list>
            </#if>
        </ul>
        <div class="clear"></div>
    </div>
</div>
${descp}