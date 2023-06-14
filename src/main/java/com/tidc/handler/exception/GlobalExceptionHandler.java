package com.tidc.handler.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import com.tidc.handler.ResultData;
import com.tidc.handler.emun.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author 宋佳豪
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResultData<String> exceptionHandler(Exception e) {
        // 打印异常信息
        log.error("出现异常 -> {}", e.getMessage(), e);
        if (e instanceof HttpMessageNotReadableException) {
            return ResultData.fail(ReturnCode.ERROR.getCode(), "请求体不能为空");
        }
        return ResultData.fail(ReturnCode.ERROR.getCode(), ReturnCode.ERROR.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResultData<String> IOExceptionHandler(IOException e) {
        log.error("异常信息: {}", e.getMessage(), e);
        return ResultData.fail(ReturnCode.ERROR.getCode(), "上传失败");
    }

    @ExceptionHandler(NotRoleException.class)
    public ResultData<String> NotRoleExceptionHandler(NotRoleException e) {
        log.error("出现异常 -> {}", e.getMessage(), e);
        return ResultData.fail(ReturnCode.NOT_FOUND.getCode(), "无权限," + e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public ResultData<String> NoLoginExceptionHandler(Exception e) {
        log.error("出现异常 -> {}", e.getMessage(), e);
        return ResultData.fail(ReturnCode.FAIL.getCode(), e.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResultData<String> ServiceExceptionHandler(ServiceException e) {
        log.error("出现异常 -> {}", e.getMessage(), e);
        return ResultData.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RegisterException.class)
    public ResultData<String> registerExceptionHandler(RegisterException e) {
        log.error("注册失败", e);
        return ResultData.fail(ReturnCode.REGISTER_FAIL.getCode(), ReturnCode.REGISTER_FAIL.getMessage());
    }

    @ExceptionHandler(UserNotExistException.class)
    public ResultData<String> userNotExistExceptionHandler(UserNotExistException e) {
        log.error("用户不存在", e);
        return ResultData.fail(ReturnCode.USER_NOT_EXIST.getCode(), ReturnCode.USER_NOT_EXIST.getMessage());
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResultData<String> redisConnectionFailureExceptionHandler(RedisConnectionFailureException e) {
        log.error("Redis连接失败,请开启Redis! {}", e.getMessage(), e);
        return ResultData.fail(ReturnCode.ERROR.getCode(), ReturnCode.ERROR.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultData<String> MaxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException e) {
        log.error("上传文件大小超过限制: {}", e.getMessage());
        return ResultData.fail(ReturnCode.UPLOAD_FILE_MAX_EXCEED.getCode(), ReturnCode.UPLOAD_FILE_MAX_EXCEED.getMessage());
    }

    /**
     * 处理Validator校验框架返回的异常错误信息
     *
     * @param e 异常
     * @return 截取关键异常信息后的提示对象
     */
    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ResultData<String> validatedExceptionHandler(Exception e) {

        ResultData<String> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.FAIL.getCode());
        resultData.setTimestamp(System.currentTimeMillis());

        if (e instanceof MethodArgumentNotValidException) { // application/x-www-form-urlencoded
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            resultData.setMessage(ex.getBindingResult().getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("; "))
            );
        } else if (e instanceof ConstraintViolationException) { // application/json
            ConstraintViolationException ex = (ConstraintViolationException) e;
            resultData.setMessage(ex.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "))
            );
        } else if (e instanceof BindException) { // application/x-www-form-urlencoded
            BindException ex = (BindException) e;
            resultData.setMessage(ex.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("; "))
            );
        }

        log.warn("参数校验异常 -> {}", resultData.getMessage());

        return resultData;
    }

}
