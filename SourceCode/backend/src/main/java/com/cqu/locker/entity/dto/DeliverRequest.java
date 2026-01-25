package com.cqu.locker.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 快递员投递请求
 */
@Data
public class DeliverRequest {
    
    @NotBlank(message = "快递柜ID不能为空")
    private String lockerId;
    
    @NotBlank(message = "格口大小不能为空")
    private String compartmentSize;
    
    @NotBlank(message = "运单号不能为空")
    private String trackingNo;
    
    @NotBlank(message = "收件人手机号不能为空")
    private String receiverPhone;
    
    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;
}