package com.dl.shop.lotto.dao2;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dl.base.mapper.Mapper;
import com.dl.shop.lotto.model.Lotto;

public interface LottoMapper extends Mapper<Lotto> {
	/**
	 * 历史中奖
	 * @return
	 */
	public List<Lotto> getLastNumLottos(@Param("count")Integer count);
	
	
}