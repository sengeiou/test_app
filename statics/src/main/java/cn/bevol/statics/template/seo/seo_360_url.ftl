<?xml version="1.0" encoding="utf-8"?>
<urlset>
<#list urls as url>
    <url>
        <loc>${url}</loc>
        <lastmod>${updateDate}</lastmod>
        <changefreq>${frequency!'weekly'}</changefreq>
        <priority>${priority!'0.8'}</priority>
    </url>
</#list>
</urlset>