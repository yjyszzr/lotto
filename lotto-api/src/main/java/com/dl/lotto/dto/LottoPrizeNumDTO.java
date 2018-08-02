package com.dl.lotto.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LottoPrizeNumDTO {
	 
	@ApiModelProperty(value=" 期号")
	private String termNum;
	@ApiModelProperty(value=" 中奖号码")
	private List<String> numList;
	 
	 
}
