package cn.bevol.controller;

import cn.bevol.internal.service.InternalCommentService;
import cn.bevol.model.entityAction.Comment;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class BackCommentController {

    @Resource
    private InternalCommentService internalCommentService;

    /**
     * 获取评论列表
     * @param entity
     * @param rows
     * @param page
     * @param sortField
     * @param order
     * @param entityId
     * @param hidden
     * @param pid
     * @param keyword
     * @param keywordType
     * @param startTimeStamp
     * @param endTimeStamp
     * @return
     */
    @RequestMapping(value = "back/comment/list", method = RequestMethod.POST)
    @ResponseBody
    public ReturnListData<Comment> getCommentList(@RequestParam String entity,
                                                  @RequestParam(required = false, defaultValue = "10") Integer rows,
                                                  @RequestParam(required = false, defaultValue = "1") Integer page,
                                                  @RequestParam(required = false, defaultValue = "id") String sortField,
                                                  @RequestParam(required = false, defaultValue = "-1") Integer order,
                                                  @RequestParam(required = false) Integer entityId,
                                                  @RequestParam(required = false) Integer userId,
                                                  @RequestParam(required = false) Integer hidden,
                                                  @RequestParam(required = false) Integer isEssence,
                                                  @RequestParam(required = false) Integer pid,
                                                  @RequestParam(required = false) Integer mainId,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) Integer keywordType,
                                                  @RequestParam(required = false) String label,
                                                  @RequestParam(required = false) String suggestion,
                                                  @RequestParam(required = false) Integer startTimeStamp,
                                                  @RequestParam(required = false) Integer endTimeStamp){
        return internalCommentService.getCommentList(entity, rows, page, sortField, order, entityId, userId, hidden, isEssence, pid, mainId, keyword, keywordType, label, suggestion, startTimeStamp, endTimeStamp);
    }

    /**
     * 更改评论详情
     * @param entity
     * @param ids
     * @param likeNum
     * @param isEssence
     * @param hidden
     * @param deleted
     * @return
     */
    @RequestMapping(value = "back/comment/save", method = RequestMethod.POST)
    @ResponseBody
    public ReturnData updateCommentInfo(@RequestParam String entity,
                                        @RequestParam String ids,
                                        @RequestParam(required = false) Long likeNum,
                                        @RequestParam(required = false) Integer isEssence,
                                        @RequestParam(required = false) Integer hidden,
                                        @RequestParam(required = false) Integer deleted){
        return internalCommentService.updateCommentInfo(entity, ids, likeNum, isEssence, hidden, deleted);
    }

    /**
     * 获取评论详情
     * @param entity
     * @param id
     * @return
     */
    @RequestMapping(value = "back/comment/info", method = RequestMethod.POST)
    @ResponseBody
    public ReturnData getCommentInfo(@RequestParam String entity, @RequestParam Integer id){
        return internalCommentService.getCommentInfo(entity, id);
    }

}
