package com.sanli.mallsystem.exceptionHandle;

import com.sanli.mallsystem.enums.ResponseStatusEnum;
import com.sanli.mallsystem.exception.UserUnLoginException;
import com.sanli.mallsystem.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

import static com.sanli.mallsystem.enums.ResponseStatusEnum.PARAM_ERROR;
import static com.sanli.mallsystem.enums.ResponseStatusEnum.REQUEST_BODY_MISSING;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

// 统一服务器异常处理器
@Slf4j
@ControllerAdvice
public class ServiceExceptionHandler {

    // 运行时异常统一处理入口
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR) // 500 错误码
    @ResponseBody
    public ResponseVo processRuntimeExceptionHandle(RuntimeException e){
        log.info("processRuntimeExceptionHandle --error ===> {}",e.getMessage());
        return ResponseVo.error(ResponseStatusEnum.SERVICE_ERROR,e.getMessage());
    }

    // 用户登录状态统一异常处理
    @ExceptionHandler(UserUnLoginException.class)
    @ResponseBody
    public ResponseVo catchUserUnLoginException(UserUnLoginException e){
        log.info("catchUserUnLoginException --error ===> {}",e.getMessage());
        return ResponseVo.error(ResponseStatusEnum.NOT_LOGGED);
    }

    // 验证方法参数方法异常处理
    /**
     * 若使用@Validated验证的对象参数有误时，则抛出HttpMessageNotReadableException异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVo catchMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
        // 将所有错误参数及其错误信息都打印到控制台上
        if (result.hasErrors()){
            for (ObjectError allError : result.getAllErrors()) {
                log.info("catchMethodArgumentNotValidException --error ===> {} {}" , allError.getObjectName() , allError.getDefaultMessage());
            }
        }
        // 这样写仅返回一个错误信息
        return ResponseVo.error(PARAM_ERROR, Objects.requireNonNull(result.getFieldError()).getField()+" " +result.getFieldError().getDefaultMessage());
    }

    /**
     * 若必要的@RequestBody参数丢失则抛出HttpMessageNotReadableException异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseVo catchHttpMessageNotReadableException(HttpMessageNotReadableException e){
        log.info("catchHttpMessageNotReadableException --error ===> {}",e.getMessage());
        // 返回缺失body请求参数错误信息
        return ResponseVo.error(REQUEST_BODY_MISSING);
    }

}

