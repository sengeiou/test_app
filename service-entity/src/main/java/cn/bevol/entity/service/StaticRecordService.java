package cn.bevol.entity.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import cn.bevol.entity.service.utils.ConfUtils;
import cn.bevol.mybatis.dao.StaticRecordMapper;
import cn.bevol.mybatis.model.GoodsExtend;
import cn.bevol.mybatis.model.StaticRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.io97.utils.db.Paged;

@Service
public class StaticRecordService {
	private static Logger logger = LoggerFactory.getLogger(StaticGoodsService.class);

	@Resource
	private StaticRecordMapper staticRecordMapper;

	@Resource
	private StaticGoodsService staticGoodsService;

	@Resource
	private GoodsExtendService goodsExtendService;



	public int insertRecord(StaticRecord record){
		record.setCreateTime(new Date().getTime()/1000);
		return staticRecordMapper.insert(record);
	}

	public int insertOrUpdate(String mid, String dataType, String dataSource, int state,String path) {
		StaticRecord record = new StaticRecord();
		record.setCreateTime(new Date().getTime()/1000);
		record.setUpdateTime(new Date().getTime()/1000);
		record.setDataSource(dataSource);
		record.setDataType(dataType);
		record.setMid(mid);
		record.setIsSeo(0);//添加操作
		record.setIsAgain(1);//修改操作
		record.setState(state);
		record.setPath(path);
		return staticRecordMapper.insertOrUpdate(record);

	}
	public int update(StaticRecord record) {

		return staticRecordMapper.update(record);
	}
	/***
	 * 分页查询
	 * @param dataType
	 * @param dataSource
	 * @param state
	 * @param page
	 * @return
	 */
	public List<StaticRecord> staticRecordByPage(String dataType,String dataSource,Integer state,Integer isSeo,Integer page,Integer pageSize ) {
		StaticRecord staticRecord = new StaticRecord();
		if (!StringUtils.isEmpty(dataType)) {
			staticRecord.setDataType(dataType);
		}
		if (!StringUtils.isEmpty(dataSource)) {
			staticRecord.setDataSource(dataSource);
		}
		if (null != state) {
			staticRecord.setState(state);
		}
		if (null != isSeo) {
			staticRecord.setIsSeo(isSeo);
		}
		Paged<StaticRecord> paged = new Paged<StaticRecord>();
		paged.setCurPage(page);
		paged.setPageSize(pageSize);
		paged.setWheres(staticRecord);
		return staticRecordMapper.staticRecordByPage(paged);
	}
	/***
	 * 没有修改过的记录
	 * @param dataType
	 * @param dataSource
	 * @param state
	 * @param page
	 * @return
	 */
	public List<StaticRecord> staticRecordByNoAgainPage(String dataType,String dataSource,Integer state,Integer page ) {
		StaticRecord staticRecord = new StaticRecord();
		if (!StringUtils.isEmpty(dataType)) {
			staticRecord.setDataType(dataType);
		}
		if (!StringUtils.isEmpty(dataSource)) {
			staticRecord.setDataSource(dataSource);
		}
		if (null != state) {
			staticRecord.setState(state);
		}
		Paged<StaticRecord> paged = new Paged<StaticRecord>();
		paged.setCurPage(page);
		paged.setWheres(staticRecord);
		return staticRecordMapper.staticRecordByAgainPage(paged);
	}
	public int getCount(String dataType,String dataSource,Integer state,Integer isSeo){
		StaticRecord staticRecord = new StaticRecord();
		if (!StringUtils.isEmpty(dataType)) {
			staticRecord.setDataType(dataType);
		}
		if (!StringUtils.isEmpty(dataSource)) {
			staticRecord.setDataSource(dataSource);
		}
		if (null != state) {
			staticRecord.setState(state);
		}
		if (null != isSeo) {
			staticRecord.setIsSeo(isSeo);
		}
		Paged<StaticRecord> paged = new Paged<StaticRecord>();
		paged.setWheres(staticRecord);
		return staticRecordMapper.selectTotal(paged);
	}
	/***
	 * 没有修改过的总条数
	 * @param dataType
	 * @param dataSource
	 * @param state
	 * @return
	 */
	public int getNoUpdateCount(String dataType,String dataSource,Integer state){
		StaticRecord staticRecord = new StaticRecord();
		if (!StringUtils.isEmpty(dataType)) {
			staticRecord.setDataType(dataType);
		}
		if (!StringUtils.isEmpty(dataSource)) {
			staticRecord.setDataSource(dataSource);
		}
		if (null != state) {
			staticRecord.setState(state);
		}
		Paged<StaticRecord> paged = new Paged<StaticRecord>();
		paged.setWheres(staticRecord);
		return staticRecordMapper.selectNoAgainTotal(paged);
	}
	/**
	 * 静态化统计
	 * @return
	 */
	public List<StaticRecord> recordTotal(){
		return   staticRecordMapper.recordTotal();
	}

	/***
	 * 批量出错后，重新从错误记录中再次批量静态化
	 * @param dataSource 类型：goods、find ...
	 */
	public List<StaticRecord> errorStatics(String dataSource){
		List<StaticRecord> list =staticRecordByNoAgainPage("pc",dataSource,-1,1);//cp和M的mid在记录中一样
		return list;
	}

	/***
	 * 每天8W批量处理
	 * @param page
	 */
	public void batch8wGoodsStatics(Integer page){
		//	List<StaticRecord> ls = staticRecordByPage("pc","goods",1,0,page,200);
		List<GoodsExtend> ls = goodsExtendService.findByPage(page);
		for (GoodsExtend record : ls) {
			staticGoodsService.goodsStatic(record.getMid());
			goodsExtendService.updateByMid(record);
		}
	}


	public void batch8wGoodsStatics(){
		final Integer everyNum = ConfUtils.staticEveryDayNum;
		ExecutorService exec =  Executors.newFixedThreadPool(1);
		exec .submit( new Runnable() {
			public void run() {
				int tatolPage = everyNum / 200 + 1;
				BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
				for (int i = 1; i <= tatolPage; i++) {
					try {
						queue.put(i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				while (true) {
					try {
						int page = 1;
						while (true) {
							try {
								page = queue.take();
								try {
									batch8wGoodsStatics(page);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
								//异常页码直接跳过  todo 后期处理异常情况
								logger.error(e.getMessage(), e.getCause());
							}

							logger.info("每天8W条。共"+tatolPage+"页;第" + page + "页成功！");
							Thread.sleep(500);
						}

					} catch (Exception e) {
						logger.error(e.getMessage(), e.getStackTrace());
					}
				}
			}
		});
		exec.shutdown();
	}





	private static void run(ExecutorService threadPool) {
		for(int i = 1; i < 5; i++) {
			final int taskID = i;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					for(int i = 1; i < 5; i++) {
						try {
							Thread.sleep(20);// 为了测试出效果，让每次任务执行都需要一定时间
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("第" + taskID + "次任务的第" + i + "次执行");
					}
				}
			});
		}
		threadPool.shutdown();// 任务执行完毕，关闭线程池
	}


	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		StaticRecordService staticRecordService = (StaticRecordService) context.getBean("staticRecordService");
		StaticRecord record = new StaticRecord();
		List<StaticRecord> ls2 = staticRecordService.recordTotal();
		//staticRecordService.insertRecord(record);
		List<StaticRecord> ls =staticRecordService.staticRecordByPage("pc","goods",1,0,1,null);
		System.out.println(ls2);
	}

}
