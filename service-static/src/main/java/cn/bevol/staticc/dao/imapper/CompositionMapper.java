package cn.bevol.staticc.dao.imapper;

import cn.bevol.staticc.model.entity.Composition;
import com.io97.utils.db.Paged;

import java.util.List;
public interface CompositionMapper {

	 
	 Composition compositionById(int id);

	int selectTotal();
	
	Composition compositionByMid(String mid);

	List<Composition> selectTotal(Paged<Composition> paged);

	List<Composition> findCompositionByIds(String[] ids);
}
