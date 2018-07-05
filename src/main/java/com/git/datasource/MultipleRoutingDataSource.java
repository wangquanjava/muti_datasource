package com.git.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

public class MultipleRoutingDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<String>();
    private static final Map<String, String> packageDataSource = new HashMap<String, String>();


    /**
     * 当需要和数据库建立session的时候, 就会在这里获取key, 从而找到datasource对象
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String dsName = dataSourceKey.get();
//        <!--因为切面是在dao层方法级别,每次获取完,清除掉数据源标识即可-->
        dataSourceKey.remove();
        return dsName;
    }



    public static void setDataSourceKey(String dataSource) {
        dataSourceKey.set(dataSource);
    }

    public static String getPackageDataSource(String pkgName) {
        return packageDataSource.get(pkgName);
    }

    public void setPackageDataSource(Map<String, String> packageDataSource) {
        MultipleRoutingDataSource.packageDataSource.putAll(packageDataSource);
    }

}