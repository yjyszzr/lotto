package com.dl.lotto.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LottoFirstDTO {
	@ApiModelProperty(value="期号")
	private Integer term_num;
	@ApiModelProperty(value=" 截止日期")
	private String endDate;
	@ApiModelProperty(value="历史中奖")
	private List<LottoPrizeNumDTO> prizeList;
	@ApiModelProperty(value="奖池")
	private String prizes;
	@ApiModelProperty(value="前区遗漏")
	private List<String> preList;
	@ApiModelProperty(value="后区遗漏")
	private List<String> postList;
}
