<product_sub_info>
    <!-- product_sub_info  美妆下面的二级分类信息主体 至少出现一次或多次-->
    <product_sub_type>${product.categoryCateName}</product_sub_type>
    <!-- product_sub_type  美妆下面的二级分类 出现一次-->
    <common_info>
        <!-- common_info，商品的通用基础信息，最少出现1次 最多出现1次 -->
        <product_name_cn>${product.goods.title}</product_name_cn>
        <!-- prodcuct_name_cn，商品名称（中文） 最少出现1次 最多出现1次，类型为字符串 -->
        <product_name_en>${product.goods.alias}</product_name_en>
        <!-- prodcuct_name_en 商品名称（英文） 最多出现1次，类型为字符串 -->
        <catogery_tag>${product.categoryCateName}</catogery_tag>
        <!-- catogery_tag, 分类标签,最少出现1次 最多出现1次，类型为字符串 -->
        <product_cover_pic></product_cover_pic>
        <!-- product_cover_pic, 商品的封面图片 最多出现一次-->
        <pic_info>
            <!-- pic_info: 不同类型的图片  不限制最多出现次数 如室内实拍图，界面图 -->
            <pic_info_tag>${product.goods.title}</pic_info_tag>
            <pic_num>1</pic_num>
            <!-- pic_list 一个类型下面的图片集合 最少出现1次 不限制最多出现次数-->
            <pic_list>
                <img>${product.goods.imageSrc}</img>
                <imgurl>${product.goods.imageSrc}</imgurl>
                <desc>${product.goods.title}</desc>
            </pic_list>
        </pic_info>
        <product_price>${product.goods.sellPrice}</product_price>
        <!-- product_price 商品价格 最少出现1次 最多出现1次 -->
        <product_source>${product.goods.country}</product_source>
        <!-- product_source, 发源地/产地 最多出现1次，类型为字符串, 榜单发布时间 -->
    </common_info>
    <special_info>
        <product_weight>${product.goods.weight!}</product_weight>
        <!-- product_weight 商品毛重 最多出现1次，类型为字符串 -->
        <product_specification>${product.goods.volume!}</product_specification>
        <!-- product_specification 商品规格 最多出现1次，类型为字符串 -->
        <#list product.composition as component>
            <component_base_info>
                <!-- component_base_info, 成分的基本信息，下面为成分的各种基本信息字段，以角鲨烷为例,最多出现多次-->
                <component>${component.title}</component>
                <!-- component, 成分,最多出现1次，类型为字符串-->
                <component_efficacy>${component.usedTitle}</component_efficacy>
                <!-- component_safety_index, 成分安全指数,最多出现1次，类型为字符串-->
                <component_safety_risk_value>${component.active}</component_safety_risk_value>
                <!-- component_safety_risk_value, 成分安全风险值,最多出现1次，-->
                <contained_active_ingredient>${component.usedTitle}</contained_active_ingredient>
                <!-- contained_active_ingredient, 成分所含活性成分,最多出现1次，-->
                <poxvirus_risk>${component.acneRisk}</poxvirus_risk>
                <!-- poxvirus_risk, 成分有无致痘风险,最多出现1次，-->
            </component_base_info>
        </#list>
        <product_filing_information>${product.goods.approval}</product_filing_information>
        <!-- product_filing_information, 产品备案信息,最多出现1次，-->
        <product_safety_star>${product.goods.score_star}</product_safety_star>
        <!-- product_safety_star, 产品安全星级,最多出现1次，-->
    </special_info>
    <#if product.comments?size gt 0>
        <comment_info>
            <!-- comment_info, 评论信息,最多出现1次，类型为字符串-->
            <spu_name>${product.goods.title}</spu_name>
            <!-- spu_name, 评论关联的spu_name,最多出现1次，类型为字符串-->
            <#list product.comments as comment>
                <comment>
                    <!-- comment, 单条评论的信息，可以出现多次-->
                    <user_name>${comment.userInfo.nickname}</user_name>
                    <!-- user_name, 评论者的用户名，最少出现一次，最多出现一次-->
                    <date>${comment.updateStamp}</date>
                    <!-- date, 评论时间，最多出现一次-->
                    <content>${comment.content}</content>
                    <!-- content, 单条评论的类容，最少出现一次，最多出现一次-->
                    <ext_info>
                        <!-- ext_info, 单条评论里面的扩展字段，没有可以不填，可以出现多次-->
                        <row>
                            <name></name>
                            <value></value>
                        </row>
                    </ext_info>
                </comment>
            </#list>
        </comment_info>
    </#if>

</product_sub_info>
