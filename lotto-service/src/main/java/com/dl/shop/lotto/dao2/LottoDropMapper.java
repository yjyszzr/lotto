package com.dl.shop.lotto.dao2;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dl.base.mapper.Mapper;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.lotto.param.HeatColdParam;
import com.dl.shop.lotto.model.LottoDrop;

public interface LottoDropMapper extends Mapper<LottoDrop> {
	
	/**
	 * 查询前/后区遗漏数据
	 * @param termNumParam
	 * @return
	 */
	public List<LottoDrop> queryChartDataByColor(ChartSetupParam termNumParam);
	
	/**
	 * 查询前/后区冷热数据
	 * @param termNumParam
	 * @return
	 */
	public List<LottoDrop> queryHeatColdByColor(HeatColdParam setupParam);
	
	/**
	 * 最近其次遗漏数据
	 * @param termNumParam
	 * @return
	 */
	public LottoDrop queryLatelyDataByColor();
	/**
	 * 获取指定其次的遗漏数据
	 * @param termNum
	 * @return
	 */
	public LottoDrop getLottoDropByTermNum(@Param("termNum")Integer termNum);
}