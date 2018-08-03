package com.dl.lotto.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LottoNumDTO {
	 
	@ApiModelProperty(value=" 期号")
	private String termNum;
	@ApiModelProperty(value=" 号码")
	private List<String> numList;
	 
	 
}
