package com.dl.shop.lotto.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dl.base.result.BaseResult;
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.shop.lotto.service.LottoFirstService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/lotto")
public class LottoFirstController {

	@Resource
	private LottoFirstService lottoFirstService;

	
	@ApiOperation(value = "首页信息", notes = "首页信息")
	@PostMapping("/queryFirstData")
	public BaseResult<List<LottoFirstDTO>> queryFirstData() {
		return lottoFirstService.queryFirstData();
	}
	
}
