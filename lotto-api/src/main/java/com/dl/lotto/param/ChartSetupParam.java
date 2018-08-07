package com.dl.lotto.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChartSetupParam {
	@ApiModelProperty(value="期数，30，50，100，默认100")
	private Integer count=100;
	@ApiModelProperty(value="是否显示遗漏0不显示，1显示，默认1")
	private Integer drop=1;
	@ApiModelProperty(value="是否计算统计，0不统计，1统计，默认1")
	private Integer compute=1;
	@ApiModelProperty(value="排序, 0正序1倒序，默认为0")
	private Integer sort=0;
}
