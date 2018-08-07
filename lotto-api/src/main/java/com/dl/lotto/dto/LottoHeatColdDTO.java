package com.dl.lotto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("冷热数据")
@Data
public class LottoHeatColdDTO {
	@ApiModelProperty(value="号码")
	private Integer num;
	@ApiModelProperty(value="30期次数")
	private Integer countA; 
	@ApiModelProperty(value="50期次数")
	private Integer countB; 
	@ApiModelProperty(value="100期次数")
	private Integer CountC; 
	@ApiModelProperty(value="遗漏")
	private Integer drop; 
}
