    <url>
        <!-- url，url标记每条信息的开始和结束，最少出现0次 最多出现50000次 -->
        <loc>https://m.bevol.cn/find/${list.id?c}.html</loc>
        <!-- loc，该条数据的存放地址，最少出现1次 最多出现1次，类型为URL地址，最小长度1个字符	最大长度256个字符	必须符合正则表达式(https?://)(.+) -->
        <lastmod>${date}</lastmod>
        <!-- lastmod，指该条数据的最新一次更新时间，最少出现0次 最多出现1次，类型为日期或日期时间，格式为YYYY-MM-DD的日期或者格式为YYYY-MM-DDThh:mm:ss的日期时间（请注意日期与时间之间以“T”分隔） -->
        <changefreq>monthly</changefreq>
        <!-- changefreq，指该条数据的更新频率，最少出现0次 最多出现1次，类型为字符串，有效值为：always、hourly、daily、weekly、monthly、yearly、never -->
        <priority>1.0</priority>
        <!-- priority，用来指定此链接相对于其他链接的优先权比值，此值定于0.0-1.0之间，最少出现0次 最多出现1次，类型为小数，最小值为（包含）0.0	最大值为（包含）1.0 -->
        <data>
            <!-- data，，最少出现1次 最多出现1次 -->
            <display>
                <!-- display，，最少出现1次 最多出现1次 -->
                <workId>${list.id?c}</workId>
                <!-- workId: 文章id，文章站内的唯一标识ID，最少出现1次 最多出现1次，类型为字符串 -->
                <url>https://www.bevol.cn/find/${list.id?c}.html</url>
                <!-- url: pc页面链接，，最少出现0次 最多出现1次，类型为URL地址 -->
                <wapUrl>https://m.bevol.cn/find/${list.id?c}.html</wapUrl>
                <!-- wapUrl: 移动wap页面链接，请同时将链接填写至 loc 字段中，最少出现1次 最多出现1次，类型为URL地址 -->
                <originalUrl>https://m.bevol.cn/find/${list.id?c}.html</originalUrl>
                <!-- originalUrl: 文章原始url，如果文章为转载文章，此处填原文章url，如果为原创文章，此处填和wapUrl字段一样的链接，最少出现1次 最多出现1次，类型为URL地址 -->
                <headline>${list.title}</headline>
                <!-- headline: 文章标题，，最少出现1次 最多出现1次，类型为字符串 -->
                <datePublished>${list.updateStamp}</datePublished>
                <!-- datePublished:  文章的发布日期，必须与页面展示的日期一致，最少出现1次 最多出现1次，类型为日期，格式为YYYY-MM-DD -->
                <provider>
                    <!-- provider: 站点名称，，最少出现1次 最多出现1次 -->
                    <brand>美丽修行</brand>
                    <!-- brand，，最少出现1次 最多出现1次，类型为字符串 -->
                </provider>
                <author>
                    <!-- author: 作者信息，，最少出现1次 最多出现1次 -->
                    <name>${list.nickname!}</name>
                    <!-- name: 作者名称，，最少出现1次 最多出现1次，类型为字符串 -->
                    <tag>网红达人</tag>
                    <!-- tag: 作者类型，，最少出现1次 最多出现1次，类型为字符串，有效值为：编辑、专家、自媒体、网红达人、普通用户 -->
                    <fansCount>123</fansCount>
                    <!-- fansCount: 作者粉丝数，，最少出现1次 最多出现1次，类型为整数 -->
                    <articleCount>${list.authorTotalNum?c!}</articleCount>
                    <!-- articleCount: 作者发布的文章数量，，最少出现1次 最多出现1次，类型为整数 -->
                </author>
                <thumbnailUrl>https://img0.bevol.cn/Find/${list.image}</thumbnailUrl>
                <!-- thumbnailUrl: 封面图片url，高清大图，宽不小于400px，高不小于267px，不能带水印，可以提交多张，最少出现1次 最多出现10次，类型为URL地址 -->
                <paragraph>
                    <!-- paragraph: 正文段落，将全文按元素段落依次提交，一篇文章如有多个段落则提交多个paragraph，最少出现1次 不限制最多出现次数 -->
                    <contentType>text</contentType>
                    <!-- contentType: 段落内容类型，可以是文字（text），可以是图片（image），最少出现1次 最多出现1次，类型为字符串 -->
                    <text>${list.descp!}</text>
                    <!-- text: 段落正文内容，，最少出现0次 最多出现1次，类型为字符串 -->
                </paragraph>
                <category>美妆个护</category>
                <!-- category: 文章涉及的商品领域，，最少出现1次 最多出现1次，类型为字符串，有效值为：手机数码、电脑办公、家用电器、美妆个护、家居家装、母婴玩具、服装服饰、运动户外、鞋靴箱包、珠宝钟表、图书音像、宠物用品、汽车用品、食品饮料、医药保健、计生情趣、其他 -->
                <genre>评测</genre>
                <!-- genre: 文章类型，，最少出现1次 最多出现1次，类型为字符串，有效值为：评测、对比、图赏、开箱、试用、种草、空瓶、试色、晒单、心得、导购、教程、科普、穿搭 -->
                <keywords>续航给力</keywords>
                <!-- keywords: 文章标签，文章标签，如有多个标签，可提交多个keywords字段，最少出现1次 不限制最多出现次数，类型为字符串 -->
                <scanCount>10000</scanCount>
                <!-- scanCount: 阅读量，，最少出现1次 最多出现1次，类型为整数 -->
                <thumbupCount>999</thumbupCount>
                <!-- thumbupCount: 点赞量，，最少出现1次 最多出现1次，类型为整数 -->
                <thumbdownCount>0</thumbdownCount>
                <!-- thumbdownCount: 点踩量，，最少出现1次 最多出现1次，类型为整数 -->
                <commentCount>99</commentCount>
                <!-- commentCount: 评论量，，最少出现1次 最多出现1次，类型为整数 -->
                <shareCount>999</shareCount>
                <!-- shareCount: 分享量，，最少出现1次 最多出现1次，类型为整数 -->
                <collectCount>999</collectCount>
                <!-- collectCount: 收藏量，，最少出现1次 最多出现1次，类型为整数 -->
            </display>
        </data>
    </url>
