package cn.example.tenant.demo.common.entity;

import lombok.Data;

import java.util.List;

/**
 * 分页结果（对齐前端常见 list + total 结构）。
 */
@Data
public class PageResult<T> {

    private List<T> records;
    private long total;
    private int pageNum;
    private int pageSize;

    public static <T> PageResult<T> of(List<T> records, long total, int pageNum, int pageSize) {
        PageResult<T> p = new PageResult<>();
        p.records = records;
        p.total = total;
        p.pageNum = pageNum;
        p.pageSize = pageSize;
        return p;
    }
}
