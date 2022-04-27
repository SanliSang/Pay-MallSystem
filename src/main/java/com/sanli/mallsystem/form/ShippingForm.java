package com.sanli.mallsystem.form;

import com.sanli.mallsystem.pojo.Shipping;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;

@Data
public class ShippingForm {
    @NotBlank
    private String receiverName;
    @NotBlank
    private String receiverPhone;
    @NotBlank
    private String receiverMobile;
    @NotBlank
    private String receiverProvince;
    @NotBlank
    private String receiverCity;
    @NotBlank
    private String receiverDistrict;
    @NotBlank
    private String receiverAddress;
    @NotBlank
    private String receiverZip;

    public static Shipping toShipping(ShippingForm form){
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form,shipping);
        return shipping;
    }
}
