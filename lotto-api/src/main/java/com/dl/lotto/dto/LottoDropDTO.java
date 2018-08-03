package com.dl.lotto.dto;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LottoDropDTO {
	@ApiModelProperty(value="遗漏号码")
	private List<LottoNumDTO> drop;
	@ApiModelProperty(value=" 统计计算")
	private Map<String,List<String>> compute; 
}
