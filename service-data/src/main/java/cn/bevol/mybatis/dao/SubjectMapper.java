package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.Subject;
import cn.bevol.mybatis.model.SubjectList;
import com.io97.utils.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by mysens on 17-7-6.
 */
public interface SubjectMapper {
    int selectTotal(@Param("pid") Integer pid);


    List<Subject> subjectByPage(@Param("pagedBegin") Integer pagedBegin, @Param("pageSize") Integer pageSize, @Param("pid") Integer pid);

    int selectListTotal();


    List<SubjectList> subjectListByPage(Paged<SubjectList> paged);

    SubjectList findSubjectListById(Integer id);
}
