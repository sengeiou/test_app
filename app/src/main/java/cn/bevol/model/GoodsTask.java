package cn.bevol.model;

import cn.bevol.util.DateUtils;

/**
 * 产品计算任务调度监控结构
 * @author hl
 *
 */
public class GoodsTask {
	
	//当前任务名称
	String taskName;
	
	//开始执行时间
	long starttime= DateUtils.nowInMillis()/1000;
	//现存执行状态
	boolean status;
    //当前执行的sql语句
	String sql;
    //每次获取数据条数
    int  rows= 1000;
    //一次性提交的数据数量
    int pbatch=5;
    //sql开始下标		
    int  start=0;
    //总记录数
	int  total=0;
	//处理完成记录数
    int finished=0;
    //已经执行时间
    long exectime=DateUtils.nowInMillis()/1000;
    //剩余页数
    int page=0;
    //总页数
    int allPage=0;
    
    public GoodsTask(String taskName) {
    	this.taskName=taskName;
    }
    
    public void init() {
    	this.starttime=DateUtils.nowInMillis()/1000;
        //当前执行的sql语句
    	this.sql=null;
        //每次获取数据条数
    	this.rows= 1000;
        //一次性提交的数据数量
    	this.pbatch=5;
        //sql开始下标		
    	this.start=0;
        //总记录数
    	this.total=0;
    	//处理完成记录数
    	this.finished=0;
        //已经执行时间
    	this.exectime=DateUtils.nowInMillis()/1000;
        //剩余页数
    	this.page=0;
        //总页数
    	this.allPage=0;
    	this.status=true;

    }
    
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public long getStarttime() {
		return starttime;
	}
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		if(rows>0)
			this.rows = rows;
	}
	public int getPbatch() {
		return pbatch;
	}
	public void setPbatch(int pbatch) {
		if(pbatch>0)
		this.pbatch = pbatch;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		if(start>0)
		this.start = start;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getFinished() {
		return finished;
	}
	public void setFinished(int finished) {
		this.finished = finished;
	}
	public long getExectime() {
		return exectime;
	}
	public void setExectime(long exectime) {
		this.exectime = exectime;
	}
	public int getPage() {
		int ctal=start+rows;
		page=ctal%rows==0?ctal/rows:((ctal/rows)+1);
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getAllPage() {
		allPage=total%rows==0?total/rows:((total/rows)+1);
		return allPage;
	}
	public void setAllPage(int allPage) {
		this.allPage = allPage;
	}
    
    
}
