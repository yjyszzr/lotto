package com.dl.lotto.dto;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LottoHeatColdDTO {
	@ApiModelProperty(value="号码")
	private Integer num;
	@ApiModelProperty(value="30期次数")
	private String countA; 
	@ApiModelProperty(value="50期次数")
	private String countB; 
	@ApiModelProperty(value="100期次数")
	private String CountC; 
	@ApiModelProperty(value="遗漏")
	private String drop; 
}
