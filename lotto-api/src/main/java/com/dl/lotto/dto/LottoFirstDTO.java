package com.dl.lotto.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("投注页面数据")
@Data
public class LottoFirstDTO {
	@ApiModelProperty(value="期号")
	private Integer term_num;
	@ApiModelProperty(value=" 截止日期")
	private String endDate;
	@ApiModelProperty(value="历史中奖")
	private List<LottoNumDTO> prizeList;
	@ApiModelProperty(value="奖池")
	private String prizes;
	@ApiModelProperty(value="前区遗漏")
	private List<String> preList;
	@ApiModelProperty(value="后区遗漏")
	private List<String> postList;
}
