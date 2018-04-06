<?xml version="1.0" encoding="UTF-8"?>
<sitemapindex>
    <#list urls as url>
        <sitemap>
            <loc>${url}</loc>
            <lastmod>${updateDate}</lastmod>
        </sitemap>
    </#list>
</sitemapindex>