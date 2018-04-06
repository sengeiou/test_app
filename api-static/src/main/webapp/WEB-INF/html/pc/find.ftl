<#include "/pc/header.ftl" />
<link rel="stylesheet" href="${css!'https://static.bevol.cn/pc/css'}/article_info.css">
<script src="${js!'https://static.bevol.cn/pc/js'}/article_info.js" type="text/javascript"></script>

<!--面包屑导航-->
<div class="crumbs_nav">
	<div class="crumbs_nav_text"><a href="/">首页</a> / <a href="/find">往期文章</a> / 文章详情</div>
</div>

<!--产品内容-->
<div class="main-cosmetics-class">
	<div class="cosmetics-info-left main-padding-4">
		<div class="cosmetics-info-title padding15 margin-bottom-50">
			<!--文章内容BEGIN-->
			<div class="page_info_main01">
				<h1 class="page_main_title">${data.title!}</h1>
				<div class="page_line"></div>
				<div class="page_lists_box">
					<ul>
						<volist name="info.tag" id="vo">
							<li>${data.tag!}</li>
						</volist>
					</ul>
					<div class="clear"></div>
				</div>
			</div>
			${data.descp!}
			<!--文章内容END-->
		</div>
	</div>
    <#include "/pc/sidebar.ftl" />

</div>
<#include "/pc/footer.ftl" />