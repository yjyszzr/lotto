package com.dl.lotto.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("红蓝走势数据")
@Data
public class LottoDropDTO {
	@ApiModelProperty(value="遗漏号码")
	private List<LottoNumDTO> drop;
	@ApiModelProperty(value="平均遗漏 ")
	private LottoNumDTO averageData; 
	@ApiModelProperty(value="最大遗漏 ")
	private LottoNumDTO maxData; 
	@ApiModelProperty(value="出现次数 ")
	private LottoNumDTO countNum; 
	@ApiModelProperty(value="最大连出 ")
	private LottoNumDTO maxContinue; 
}
