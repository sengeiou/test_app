package cn.bevol.app.dao.mapper;


import cn.bevol.app.entity.vo.CompositionName;
import cn.bevol.app.entity.dto.CompositionDTO;
import cn.bevol.app.entity.dto.DirtyCompositionDTO;
import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.entity.model.Used;
import cn.bevol.app.dao.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author ruanchen
 */
public interface CompositionOldMapper {
    Composition getById(@Param("id") Long id);

    /**
     * 查询所有成分
     *
     * @return
     */
    List<Composition> getAll();

    Composition getCompositionByPid();

    List<Composition> compositionByPage(Paged paged);

    Composition compositionById(int id);
    Composition compositionByMid(String mid);

    List<Used> getUsedsByUid(@Param("used") String used);
    List<Used> getAllUsed();

    List<Composition> findByNames(@Param("namess") List<String> namess);


    Composition findByName(@Param("name") String name);

    List<Composition> findTmpByNames(@Param("namess") List<String> namess);

    List<Composition> findByLikeNames(@Param("namess") List<String> namess);

    /**
     * 插入临时表
     *
     * @param string
     */
    int insertdirtyComposition(Composition composition);

    List<Composition> getByIds(@Param("ids") List<Long> ids);

    Composition getByMid(@Param("mid") String mid);


    List findCompositionKeyIdByPage(Paged paged);

    int selectTotal();

	Composition findCleanMarkByName(@Param("name") String name);
	Composition findTmpCleanMarkByName(@Param("name") String name);

	List<Map> getSourceCps(@Param("pager") long pager, @Param("pageSize") int pageSize);

	long SourceCpsCount();

    void addCompositionInfo(CompositionDTO compositionDTO);

    void saveCompositionInfo(CompositionDTO compositionDTO);

    void saveCompositionList(CompositionDTO compositionDTO);

    void saveCompositionMid(@Param("id") long id, @Param("mid") String mid);

    String getMidById(@Param("id") long id);

    List<CompositionName> findCompositionByIds(String[] ids);

    List<String> findFormatCps(String[] ids);

    void addDirtyComposition(DirtyCompositionDTO dirtyCompositionDTO);

    void saveDirtyComposition(DirtyCompositionDTO dirtyCompositionDTO);

    void saveDirtyCompositionList(DirtyCompositionDTO dirtyCompositionDTO);

    List<CompositionDTO> getCompositionInfoByNames(@Param("compositionArr") String[] compositionArr);

    List<CompositionDTO> getDirtyCompositionInfoByNames(@Param("compositionArr") String[] compositionArr);

	List<Integer> getSoapCps();
	//A类成分查询
	List<Integer> selectAcomposition();
	//B类成分查询
	List<Integer> selectBcomposition();
}
