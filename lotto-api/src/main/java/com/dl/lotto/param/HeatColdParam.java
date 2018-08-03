package com.dl.lotto.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class HeatColdParam {
	@ApiModelProperty(value="0红球/1篮球")
	private Integer color;
	@ApiModelProperty(value="排序")
	private Integer order;
}
