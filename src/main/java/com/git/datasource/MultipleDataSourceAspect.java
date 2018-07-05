package com.git.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 切mapper层和@DataSource注解
 * 取出注解中的key, 放入ThreadLocal, 提供给下游使用
 */
@Component
@Aspect
// 一定要比@Transactional之前确定数据源
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MultipleDataSourceAspect {

    private static final Logger LOG = LoggerFactory.getLogger(MultipleDataSourceAspect.class);


    @Around("@annotation(com.git.datasource.DataSource) || execution(public * com.git.dao.mapper.*.*(..))")
    public Object doAround(ProceedingJoinPoint jp) throws Throwable {
        Signature signature = jp.getSignature();

        String dataSourceKey = getDataSourceKey(signature);

        if (StringUtils.hasText(dataSourceKey)) {
            MultipleRoutingDataSource.setDataSourceKey(dataSourceKey);
        }

        Object jpVal = jp.proceed();

        return jpVal;


    }

    public String getDataSourceKey(Signature signature) {
        if (signature == null) return null;

        if (signature instanceof MethodSignature) {

            //检测方法级注解
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            if (method.isAnnotationPresent(DataSource.class)) {
                return dsSettingInMethod(method);
            }

            //类级注解
            Class declaringClazz = method.getDeclaringClass();
            if (declaringClazz.isAnnotationPresent(DataSource.class)) {
                try {
                    return dsSettingInConstructor(declaringClazz);
                } catch (Exception e) {
                    LOG.error("获取构造方法的DataSource注解失败", e);
                }
            }

            //包级注解,为了配置方便，包注解和类以及方法注解方式不同
            Package pkg = declaringClazz.getPackage();
            return dsSettingInPackage(pkg);

        }

        return null;
    }

    private String dsSettingInMethod(Method method) {
        DataSource dataSource = method.getAnnotation(DataSource.class);
        return dataSource.value();
    }

    private String dsSettingInConstructor(Class clazz) {
        DataSource dataSource = (DataSource) clazz.getAnnotation(DataSource.class);
        return dataSource.value();
    }

    private String dsSettingInPackage(Package pkg) {
        return MultipleRoutingDataSource.getPackageDataSource(pkg.getName());
    }


}