package com.dl.lotto.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.dl.base.param.EmptyParam;
import com.dl.base.result.BaseResult;
import com.dl.lotto.dto.LottoDTO;

/**
 * 大乐透接口
 * @author zhangzirong
 *
 */
@FeignClient(value="lotto-service")
public interface ISuperLottoService {
	
	/**
	 * 查询大乐透最近一期奖池
	 * @return
	 */
	@RequestMapping(path="/lotto/queryLottoLatestPrizes", method=RequestMethod.POST)
	public BaseResult<LottoDTO> queryLottoLatestPrizes(@RequestBody EmptyParam emptyParam);
	

}
