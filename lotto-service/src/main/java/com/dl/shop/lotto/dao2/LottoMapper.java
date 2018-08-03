package com.dl.shop.lotto.dao2;

import java.util.List;

import com.dl.base.mapper.Mapper;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.shop.lotto.model.LottoDrop;
import com.dl.shop.lotto.model.Lotto;

public interface LottoMapper extends Mapper<Lotto> {
	/**
	 * 历史中奖（10条）
	 * @return
	 */
	public List<Lotto> queryHistoryPrizeData();
	
	/**
	 * 最近期次
	 * @return
	 */
	public Lotto queryLatelyPrizeData();
}