package cn.bevol.util.db;


import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Paged<T> {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private int total;
    private int curPage = 1;
    private int totalPage = 0;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private List<T> result;
    @Deprecated
    private T wheres;
    @Deprecated
    private List<OrderBy> orders = new LinkedList<OrderBy>();

    public Paged() {

    }

    public Paged(Paged paged) {
        this(paged.getTotal(), paged.getCurPage(), paged.getPageSize(), null);
    }

    public Paged(Paged source, Converter converter) {
        this(source.getTotal(), source.getCurPage(), source.getPageSize(), null);
        List<T> re = new ArrayList<T>(pageSize);
        for (Object o : source.getResult()) {
            Object n = converter.convert(o);
            re.add((T) n);
        }
        this.result = re;
    }

    public Paged(int total, int curPage, int pageSize, List<T> result) {
        this.total = total;
        this.curPage = curPage;
        this.pageSize = pageSize;
        this.result = result;
        init();
    }

    public static interface Convert<U> {
        <T> T convert(U u);
    }

    public void init() {
        initTotalPage();
    }

    public void initTotalPage() {
        totalPage = (total - 1) / pageSize + 1;
    }

    public int getTotalPage() {
        if (0 == totalPage) {
            initTotalPage();
        }
        return totalPage;
    }

    public void setCurPage(int curPage) {
        if (curPage <= 1)
            this.curPage = 1;
        else
            this.curPage = curPage;
    }

    public int getPagedBegin() {
    		return (curPage - 1) * pageSize;
    	}

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    @Deprecated
    public T getWheres() {
        return wheres;
    }

    @Deprecated
    public void setWheres(T wheres) {
        this.wheres = wheres;
    }

    @Deprecated
    public String getOrderBy() {
        if (this.orders.size() > 0) {
            StringBuilder result = new StringBuilder();
            for (OrderBy order : orders) {
                result.append(order.getOrderBy()).append(",");
            }
            if (result.length() > 1) {
                result.deleteCharAt(result.length() - 1);
                return result.toString();
            }
        }
        return null;
    }

    @Deprecated
    public void addOrderBy(String field, String orderType) {
        this.orders.add(new OrderBy(field, orderType));
    }

    @Deprecated
    public static class OrderBy {

        private String field;//排序的字段
        private String orderType;

        public OrderBy(String field, String orderType) {
            this.field = field;
            this.orderType = orderType;
        }

        public OrderBy(String field) {
            this.field = field;
        }

        public OrderBy(String field, Type type) {
            this.field = field;
            if (type == Type.ASC) return;
            this.orderType = type.str;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public String getOrderBy() {
            return this.field + " " + this.orderType;
        }

        public static enum Type {
            DESC("DESC"),
            ASC("ASC");
            private String str;

            Type(String str) {
                this.str = str;
            }
        }
    }
}
