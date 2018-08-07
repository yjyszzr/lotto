package com.dl.shop.lotto.dao2;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dl.base.mapper.Mapper;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.lotto.param.HeatColdParam;
import com.dl.shop.lotto.model.LottoDrop;

public interface LottoDropMapper extends Mapper<LottoDrop> {
	
	/**
	 * 获取指定其次的遗漏数据
	 * @param termNum
	 * @return
	 */
	public LottoDrop getLottoDropByTermNum(@Param("termNum")Integer termNum);
	/**
	 * 获取指定数据的数据
	 * @param count
	 * @return
	 */
	public List<LottoDrop> getLastNumLottoDrops(@Param("count")Integer count);
}