package com.sanli.paysystem.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sanli.paysystem.enums.ResponseStatusEnum;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@JsonInclude(value = NON_NULL) // 当该对象作为json对象返回时，若存在空字段则不返回该字段
public class ResponseVo<T> {
    private int status; // 0表示成功
    private String message;
    private T data;

    private ResponseVo(int status , String message) {
        this.status = status;
        this.message = message;
    }

    private ResponseVo(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseVo<T> success(){
        return new ResponseVo<>(ResponseStatusEnum.SUCCESS.getStatus(),ResponseStatusEnum.SUCCESS.getDesc());
    }

    public static <T> ResponseVo<T> success(String message){
        return new ResponseVo<>(ResponseStatusEnum.SUCCESS.getStatus(),message);
    }

    public static <T> ResponseVo<T> success(ResponseStatusEnum statusEnum){
        return new ResponseVo<>(statusEnum.getStatus(),statusEnum.getDesc());
    }
    public static <T> ResponseVo<T> success(T data){
        return new ResponseVo<>(ResponseStatusEnum.SUCCESS.getStatus(),ResponseStatusEnum.SUCCESS.getDesc(),data);
    }

    public static <T> ResponseVo<T> success(ResponseStatusEnum statusEnum , String message){
        return new ResponseVo<>(statusEnum.getStatus(),message);
    }
    public static <T> com.sanli.paysystem.vo.ResponseVo<T> success(ResponseStatusEnum statusEnum , T data){
        return new ResponseVo<>(statusEnum.getStatus(),statusEnum.getDesc(),data);
    }

    public static <T> ResponseVo<T> success(ResponseStatusEnum statusEnum , String message , T data){
        return new ResponseVo<>(statusEnum.getStatus() , message , data);
    }

    public static <T> ResponseVo<T> error(){
        return new ResponseVo<>(ResponseStatusEnum.FAIL.getStatus(),ResponseStatusEnum.FAIL.getDesc());
    }

    public static <T> ResponseVo<T> error(String message){
        return new ResponseVo<>(ResponseStatusEnum.FAIL.getStatus(),message);
    }

    public static <T> ResponseVo<T> error(ResponseStatusEnum statusEnum){
        return new ResponseVo<>(statusEnum.getStatus(),statusEnum.getDesc());
    }

    public static <T> ResponseVo<T> error(ResponseStatusEnum statusEnum , String message){
        return new ResponseVo<>(statusEnum.getStatus(),message);
    }

    // 可能存在多个失败条件失败原因卸载data（Map方式）
    public static <T> ResponseVo<T> error(BindingResult result){
        Map<String, String> results = new HashMap<>();
        for (ObjectError allError : result.getAllErrors()) {
            results.put(allError.getObjectName(),allError.getDefaultMessage());
        }
        return new ResponseVo(ResponseStatusEnum.FAIL.getStatus(),ResponseStatusEnum.FAIL.getDesc(),results);
    }
}
