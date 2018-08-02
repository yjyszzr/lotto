package com.dl.lotto.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LottoDTO {
	@ApiModelProperty(value="期号")
	private String term_num;
	@ApiModelProperty(value=" 开奖日期")
	private String prize_date;
	@ApiModelProperty(value=" 中奖号码")
	private List<String> numList;
	@ApiModelProperty(value=" 奖池")
	private String prizes;
	@ApiModelProperty(value=" 创建时间")
	private String create_time;
	 
}
