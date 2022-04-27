package com.sanli.paysystem.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Slf4j
public class Verification {

    // 参数校验并打印日志
    public static void parameterVerificationAndLogging(BindingResult result){
        if (result.hasErrors()){
            for (ObjectError allError : result.getAllErrors()) {
                log.info("error ===> {} {}" , allError.getObjectName() , allError.getDefaultMessage());
            }
        }
    }

}
