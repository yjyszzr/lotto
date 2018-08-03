package com.dl.lotto.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChartSetupParam {
	@ApiModelProperty(value="期数")
	private Integer count;
	@ApiModelProperty(value="0红球/1篮球")
	private Integer color;
	@ApiModelProperty(value="是否显示遗漏")
	private Integer drop;
	@ApiModelProperty(value="是否计算统计")
	private Integer compute;
	@ApiModelProperty(value="排序")
	private Integer orderBY;
}
