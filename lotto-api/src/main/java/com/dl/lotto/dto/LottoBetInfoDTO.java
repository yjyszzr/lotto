package com.dl.lotto.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LottoBetInfoDTO {

	@ApiModelProperty(value = "投注选项：单复式01,02,03,04,05,06|01,02,03 ；胆拖01,02$03,04,05,06|01,02$03,04 ")
	private String betInfo;
	
	@ApiModelProperty(value = "玩法:0单式，1复式，2胆拖")
	private Integer playType;
	
	@ApiModelProperty(value = "该彩票注数")
	private Integer betNum;
	
	@ApiModelProperty(value = "该张彩票金额")
	private String amount;
}
