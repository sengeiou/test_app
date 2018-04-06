package cn.bevol.internal.dao.mapper;

import cn.bevol.internal.entity.model.Subject;
import cn.bevol.internal.entity.model.SubjectList;
import cn.bevol.internal.dao.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by mysens on 17-7-6.
 */
public interface SubjectOldMapper {
    int selectTotal(@Param("pid") Integer pid);


    List<Subject> subjectByPage(@Param("pagedBegin") Integer pagedBegin, @Param("pageSize") Integer pageSize, @Param("pid") Integer pid);

    int selectListTotal();


    List<SubjectList> subjectListByPage(Paged<SubjectList> paged);

    SubjectList findSubjectListById(Integer id);
}
