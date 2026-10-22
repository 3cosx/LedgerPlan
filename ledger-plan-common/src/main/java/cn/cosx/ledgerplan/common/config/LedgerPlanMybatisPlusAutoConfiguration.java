package cn.cosx.ledgerplan.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@AutoConfiguration
public class LedgerPlanMybatisPlusAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public MetaObjectHandler ledgerPlanMetaObjectHandler() {
        return new LedgerPlanMetaObjectHandler();
    }

    static class LedgerPlanMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            LocalDateTime now = LocalDateTime.now();
            strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
            strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
            strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
            strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
            fillUser(metaObject, "createUser");
            fillUser(metaObject, "updateUser");
            fillUser(metaObject, "createBy");
            fillUser(metaObject, "updateBy");
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            LocalDateTime now = LocalDateTime.now();
            strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
            strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, now);
            fillUser(metaObject, "updateUser");
            fillUser(metaObject, "updateBy");
        }

        private void fillUser(MetaObject metaObject, String fieldName) {
            if (!metaObject.hasSetter(fieldName)) {
                return;
            }
            Object loginId;
            try {
                loginId = StpUtil.getLoginIdDefaultNull();
            } catch (RuntimeException ignored) {
                // 无 Web/Sa-Token 上下文时（例如定时任务、单元测试）跳过用户字段填充。
                return;
            }
            if (loginId == null) {
                return;
            }
            Class<?> setterType = metaObject.getSetterType(fieldName);
            if (setterType == Long.class || setterType == long.class) {
                try {
                    loginId = Long.valueOf(loginId.toString());
                } catch (NumberFormatException ignored) {
                    return;
                }
            } else if (setterType != Object.class && !setterType.isInstance(loginId)) {
                loginId = loginId.toString();
            }
            setFieldValByName(fieldName, loginId, metaObject);
        }
    }
}
