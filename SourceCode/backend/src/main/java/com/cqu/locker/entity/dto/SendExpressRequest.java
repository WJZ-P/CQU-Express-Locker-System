package com.cqu.locker.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 寄件请求
 */
@Data
public class SendExpressRequest {
    
    @NotBlank(message = "快递公司不能为空")
    private String company;
    
    @NotBlank(message = "寄件人姓名不能为空")
    private String senderName;
    
    @NotBlank(message = "寄件人电话不能为空")
    private String senderPhone;
    
    @NotBlank(message = "寄件人地址不能为空")
    private String senderAddress;
    
    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;
    
    @NotBlank(message = "收件人电话不能为空")
    private String receiverPhone;
    
    @NotBlank(message = "收件人地址不能为空")
    private String receiverAddress;
    
    private String itemType;
    
    private String weight;
    
    private String remark;
}
