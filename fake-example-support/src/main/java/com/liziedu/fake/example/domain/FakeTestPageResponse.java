package com.liziedu.fake.example.domain;

import java.util.List;

public class FakeTestPageResponse<T> {

    private List<T> list;

    private int page;

    private int pagesize;

    private long total;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "FakeTestPageResponse{" +
                "list=" + list +
                ", page=" + page +
                ", pagesize=" + pagesize +
                ", total=" + total +
                '}';
    }
}
