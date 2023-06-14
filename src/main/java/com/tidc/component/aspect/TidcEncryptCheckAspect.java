package com.tidc.component.aspect;

import com.tidc.component.annotate.TidcEncryptCheck;
import com.tidc.entity.BaseEntity;
import com.tidc.handler.ResultData;
import com.tidc.handler.emun.ReturnCode;
import com.tidc.utils.TidcEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 校验加密字段注解切面类
 */
@Component
@Aspect
@Slf4j
public class TidcEncryptCheckAspect {


    @Pointcut("@annotation(encryptCheck)") // 确定切点,以便拦截所有带有此注解的方法
    public void annotatedMethod(TidcEncryptCheck encryptCheck) {
    }

    @Around(value = "annotatedMethod(encryptCheck)", argNames = "joinPoint, encryptCheck")
    public Object checkEncryption(ProceedingJoinPoint joinPoint, TidcEncryptCheck encryptCheck) throws Throwable {

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null && arg.getClass().equals(encryptCheck.value())) {
                String key = TidcEncryptUtil.formatStr((BaseEntity) arg);
                if (!TidcEncryptUtil.makeAndCheckEncryptionIsEquals(key, ((BaseEntity) arg).getEncryption())) {
                    log.warn("密钥比对错误 -> 后端生成密钥: {} , 前端传递密钥: {}", TidcEncryptUtil.md5(key), ((BaseEntity) arg).getEncryption());
                    return ResultData.fail(ReturnCode.ENCRYPT_ERROR.getCode(), ReturnCode.ENCRYPT_ERROR.getMessage());
                }
            }
        }
        return joinPoint.proceed();

    }

}
