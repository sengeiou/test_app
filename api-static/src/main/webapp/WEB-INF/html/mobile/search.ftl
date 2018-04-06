<div class="searchs" style="margin-top: 10px;">
    <form method="get" action="/product" autocomplete="off" class="searchs-form" id="search-form">
        <div class="searchs-box">
            <select class="selectpicker" id="search-type">
                <option selected = "selected" value="/product">产品</option>
                <option value="/composition">成分</option>
            </select>
        </div>
        <input type="text" id="keywords" name="keywords" placeholder="" class="searchs-term ">
        <input type="submit" value="" class="searchs-btn">
    </form>
</div>
<!-- 切换搜索类型-->
<script>
    var keywords=decodeURI(decodeURI($.getUrlField("keywords")));
    $.loadselect();
    $("#search-type").change(function(){
        var act = $("#search-type").val();
        $("#search-form").attr('action',act);
        //请输入关键字...
        if(act.indexOf("product")!=-1) {
            //产品
            $("#keywords").attr("placeholder","搜索353489条化妆品");
        }else {
            //成分
            $("#keywords").attr("placeholder","搜索10585条化妆品成分");
        }
    }).change();

    var token_name = "__hash__";
    $("#search-form input[name='" + token_name + "']").remove();
    if(window.location.href.indexOf("composition")!=-1){
        $('#keywords').attr("value",keywords);
    }
    $("#search-form").submit(function(){
        var cccc=encodeURI($("#keywords").val());
        $("#keywords").css("color","#f1f2f3");
        $("#keywords").val(cccc);
    });
</script>