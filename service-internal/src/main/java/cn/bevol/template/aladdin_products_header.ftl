<?xml version="1.0" encoding="UTF-8" ?>
<urlset content_method="full">
    <!-- urlset，urlset用来标记整个文档的开头，最少出现1次 最多出现1次 -->
    <!-- 属性content_method，XML全量、增量还是删除，可选，类型为字符串，有效值为：full、inc、dec -->
    <url>
        <!-- url，url标记每条信息的开始和结束，最少出现0次 最多出现50000次 -->
        <loc>${url}</loc>
        <!-- loc，该条数据的存放地址，最少出现1次 最多出现1次，类型为URL地址，最小长度1个字符最大长度256个字符必须符合正则表达式(https?://)(.+) -->
        <lastmod>${date}</lastmod>
        <!-- lastmod，指该条数据的最新一次更新时间，最少出现0次 最多出现1次，类型为日期或日期时间，格式为YYYY-MM-DD的日期或者格式为YYYY-MM-DDThh:mm:ss的日期时间（请注意日期与时间之间以“T”分隔） -->
        <changefreq>monthly</changefreq>
        <!-- changefreq，指该条数据的更新频率，最少出现0次 最多出现1次，类型为字符串，有效值为：always、hourly、daily、weekly、monthly、yearly、never -->
        <priority>1.0</priority>
        <!-- priority，用来指定此链接相对于其他链接的优先权比值，此值定于0.0-1.0之间，最少出现0次 最多出现1次，类型为小数，最小值为（包含）0.0最大值为（包含）1.0 -->
        <data>
            <!-- data，，最少出现1次 最多出现1次 -->
            <display>
                <!-- display，，最少出现1次 最多出现1次 -->
                <product_base>
                    <!-- product_base，不同商品类型（如美妆，3C，运动户外等的主体）的本体部分，最少出现1次 最多出现多次 -->
                    <product_type>美妆</product_type>
                    <!-- product_type  商品类型，如美妆，3C，运动户外等-->
                    <mall_name>美丽修行</mall_name>
                    <!-- mall_name  商城名称-->

