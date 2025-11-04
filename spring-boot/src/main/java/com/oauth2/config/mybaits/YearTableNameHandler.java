package com.oauth2.config.mybaits;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author huangzhao
 * @date 2025/10/13
 */
public class YearTableNameHandler implements TableNameHandler {

    private final static List<String> TABLE_NAME_LIST = List.of("");

    // 使用ThreadLocal来安全地传递当前线程的年份参数
    private static final ThreadLocal<String> YEAR_THREAD_LOCAL = new ThreadLocal<>();

    // 设置当前线程需要使用的年份
    public static void setYear(String year) {
        if (StringUtils.isEmpty(year)) {
            return;
        }
        YEAR_THREAD_LOCAL.set(year);
    }

    @Override
    public String dynamicTableName(String sql, String tableName) {
        if (TABLE_NAME_LIST.contains(tableName)) {
            String year = YEAR_THREAD_LOCAL.get();
            if (StringUtils.isNotEmpty(year)) {
                try {
                    return tableName + "_" + year;
                } finally {
                    YEAR_THREAD_LOCAL.remove();
                }
            }
        }
        return tableName;
    }
}
