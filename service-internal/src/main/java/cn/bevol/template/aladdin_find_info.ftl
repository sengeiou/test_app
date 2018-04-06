<url>
    <!-- url，url标记每条信息的开始和结束，最少出现1次 最多出现50000次 -->
    <loc>https://m.bevol.cn/find/${list.id?c}.html</loc>
    <!-- loc，该条数据的存放地址，最少出现1次 最多出现1次，类型为URL地址，最小长度1个字符	最大长度256个字符	必须符合正则表达式(https?://)(.+) -->
    <lastmod>${date}</lastmod>
    <!-- lastmod，指该条数据的最新一次更新时间，最少出现0次 最多出现1次，类型为日期或日期时间，格式为YYYY-MM-DD的日期或者格式为YYYY-MM-DDThh:mm:ss的日期时间（请注意日期与时间之间以“T”分隔） -->
    <changefreq>monthly</changefreq>
    <!-- changefreq，指该条数据的更新频率，最少出现0次 最多出现1次，类型为字符串，有效值为：always、hourly、daily、weekly、monthly、yearly、never -->
    <priority>1.0</priority>
    <!-- priority，用来指定此链接相对于其他链接的优先权比值，此值定于0.0-1.0之间，最少出现0次 最多出现1次，类型为小数，最小值为（包含）0.0	最大值为（包含）1.0 -->
    <data>
        <!-- data，最少出现1次 最多出现1次 -->
        <display>
            <!-- display，，最少出现1次 最多出现1次 -->
            <id>${list.id?c}</id>
            <!-- article_id: 【可选】文章站内的唯一标识ID，最少出现1次 最多出现1次，类型为字符串 -->
            <wap_url>https://m.bevol.cn/find/${list.id?c}.html</wap_url>
            <!-- wap_url: 【必选】文章的移动wap页面链接，最少出现1次 最多出现1次，类型为字符串 -->
            <title>${list.title}</title>
            <!-- question_title: 【必选】文章标题，最少出现1次 最多出现1次，类型为字符串，最小长度1个字符 -->
            <publish_time>${list.updateStamp}</publish_time>
            <!-- publish_time: 【必选】文章的发布日期；必须与站内文章页面上显示的文章发布日期保持一致；日期格式：YYYY-MM-DDT，最少出现1次 最多出现1次，类型为日期-->
            <provider>美丽修行</provider>
            <!-- provider:【必选】文章来源站点名称如：中关村在线、太平洋在线、闺蜜网等，最少出现1次 最多出现1次，类型为字符串 -->
            <author>
                <!-- author: 作者信息，最少出现0次 最多出现1次 -->
                <name>${list.nickname!}</name>
                <!-- name: 【必选】作者名称，最少出现1次 最多出现1次，类型为字符串 -->

                <type>达人</type>
                <!-- author_type:【必选】作者类型，例如：编辑、专家、网红、达人、普通用户；最少出现1次 最多出现1次，类型为字符串 -->

            </author>
            <thumbnail>https://img0.bevol.cn/Find/${list.image}</thumbnail>
            <content>
                <!-- content: 正文内容，将正文全文按照元素段落全部给出，最少出现1次 最多出现1次 -->
                <items>
                    <!-- items: 内容正文元素，最少出现1次 不限制最多出现次数 -->
                    <type>html</type>
                    <!-- type: 类型，这段的类型，可以是文字（text），可以是html（html），可以是图片（image），最少出现0次 最多出现1次，类型为字符串 -->
                    <data>${list.descp!}</data>
                    <!-- data: 内容，最少出现0次 最多出现1次，类型为字符串 -->
                    <image>
                        <!-- image: 正文图片，最少出现0次 最多出现1次 -->
                        <src>https://img0.bevol.cn/Find/${list.image}</src>
                        <!-- src: 图片url，，最少出现0次 最多出现1次，类型为URL地址 -->
                    </image>
                </items>
            </content>
            <category>美妆</category>
            <!-- category：【必选】文章类型，例如：导购、新闻、行情、评测、心得、试用报告、众测报告、精华点评等，最少出现1次，最多出现1次，类型为字符串 -->
            <content_type>心得</content_type>
            <ugc_info>
                <!-- ugc_info：【必选】文章UGC热度信息，最少出现1次，最多出现1次，类型为字符串 -->
                <scan_count>100000</scan_count>
                <!-- scan_count：【必选】阅读量，没有填0，最少出现1次，最多出现1次，类型为字符串 -->
                <like_count>9999</like_count>
                <!-- like_count：【必选】点赞量，没有填0，最少出现1次，最多出现1次，类型为字符串 -->
                <dislike_count>0</dislike_count>
                <!-- dislike_count：【必选】点踩量，没有填0，最少出现1次，最多出现1次，类型为字符串 -->
                <comment_count>999</comment_count>
                <!-- comment_count：【必选】评论量，没有填0，最少出现1次，最多出现1次，类型为字符串 -->
                <share_count>0</share_count>
                <!-- share_count：【必选】分享量，没有填0，最少出现1次，最多出现1次，类型为字符串 -->
                <collection_count>299</collection_count>
                <!-- collection_count：【必选】收藏量，没有填0，最少出现1次，最多出现1次，类型为字符串 -->
            </ugc_info>
            <goodsnum>0</goodsnum>
        </display>
    </data>
</url>