package com.dl.lotto.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("走势图数据")
@Data
public class LottoChartDataDTO {
	@ApiModelProperty(value="开奖号码")
	private List<LottoNumDTO> lottoNums;
	@ApiModelProperty(value=" 红球走势")
	private LottoDropDTO preLottoDrop;
	@ApiModelProperty(value="蓝球走势")
	private LottoDropDTO postLottoDrop;
	@ApiModelProperty(value=" 红球冷热")
	private List<LottoHeatColdDTO> preHeatColds;
	@ApiModelProperty(value=" 蓝球冷热")
	private List<LottoHeatColdDTO> postHeatColds;
	 
}
