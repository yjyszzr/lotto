package com.dl.lotto.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TermNumParam {
	@ApiModelProperty(value="期数")
	private Integer count;
	@ApiModelProperty(value="0红球/1篮球")
	private Integer color;
}
