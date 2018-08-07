package com.dl.shop.lotto.web;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dl.base.param.EmptyParam;
import com.dl.base.result.BaseResult;
import com.dl.base.result.ResultGenerator;
import com.dl.lotto.dto.LottoChartDataDTO;
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.lotto.enums.LottoResultEnum;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.shop.lotto.service.LottoFirstService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/lotto")
public class LottoFirstController {

	@Resource
	private LottoFirstService lottoFirstService;

	
	@ApiOperation(value = "选号投注页数据", notes = "选号投注页数据")
	@PostMapping("/getTicketInfo")
	public BaseResult<LottoFirstDTO> getTicketInfo(@RequestBody EmptyParam emprt) {
		LottoFirstDTO queryFirstData = lottoFirstService.queryFirstData();
		if(queryFirstData != null) {
			return ResultGenerator.genSuccessResult("", queryFirstData);
		}
		return ResultGenerator.genResult(LottoResultEnum.GET_TICKET_INFO_NULL.getCode(), LottoResultEnum.GET_TICKET_INFO_NULL.getMsg());
	}
	
	@ApiOperation(value = "走势图数据", notes = "走势图数据")
	@PostMapping("/getChartData")
	public BaseResult<LottoChartDataDTO> getChartData(@RequestBody ChartSetupParam param) {
		LottoChartDataDTO lottoChartData = lottoFirstService.getChartData(param);
		if(lottoChartData != null) {
			return ResultGenerator.genSuccessResult("", lottoChartData);
		}
		return ResultGenerator.genResult(LottoResultEnum.GET_CHART_DATA_NULL.getCode(), LottoResultEnum.GET_CHART_DATA_NULL.getMsg());
	}
	
}
