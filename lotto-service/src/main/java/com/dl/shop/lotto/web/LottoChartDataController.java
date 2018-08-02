package com.dl.shop.lotto.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dl.base.result.BaseResult;
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.lotto.dto.LottoHeatColdDTO;
import com.dl.lotto.dto.LottoDropDTO;
import com.dl.lotto.param.TermNumParam;
import com.dl.shop.lotto.service.LottoDropService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/lotto")
public class LottoChartDataController {

	@Resource
	private LottoDropService lottoDropService;

	
	@ApiOperation(value = "查询走势", notes = "查询走势")
	@PostMapping("/queryChartDataByColor")
	public BaseResult<List<LottoDropDTO>> queryChartDataByColor(@RequestBody TermNumParam termNumParam) {
		return lottoDropService.queryChartDataByColor(termNumParam);
	}
	
	@ApiOperation(value = "查询冷热分析", notes = "查询冷热分析")
	@PostMapping("/queryHeatColdByColor")
	public BaseResult<List<LottoHeatColdDTO>> queryHeatColdByColor() {
		return lottoDropService.queryHeatColdByColor();
	}
	
}
