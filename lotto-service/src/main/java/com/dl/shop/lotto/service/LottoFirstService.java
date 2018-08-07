package com.dl.shop.lotto.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dl.base.result.BaseResult;
import com.dl.base.result.ResultGenerator;
import com.dl.lotto.dto.LottoChartDataDTO;
import com.dl.lotto.dto.LottoDTO;
import com.dl.lotto.dto.LottoDropDTO;
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.lotto.dto.LottoHeatColdDTO;
import com.dl.lotto.dto.LottoNumDTO;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.shop.lotto.dao2.LottoDropMapper;
import com.dl.shop.lotto.dao2.LottoMapper;
import com.dl.shop.lotto.model.Lotto;
import com.dl.shop.lotto.model.LottoDrop;
import com.dl.shop.lotto.utils.TermEndDateUtil;

@Service
@Transactional(value="transactionManager2")
public class LottoFirstService {
	@Resource
    private LottoMapper lottoMapper;
	@Resource
	private LottoDropMapper lottoDropMapper;
	
	  
	/**
     * 投注页数据
     */
	 public LottoFirstDTO queryFirstData(){
		 LottoFirstDTO lottoFirstDTO = null;
		 //获取最近10期的开奖
		 List<Lotto> lottos = lottoMapper.getLastNumLottos(10);
		 if(lottos.size() >= 0) {
			 Lotto lastLotto = lottos.get(0);
			 //@Todo
			 //判断该期次和当前日期是否匹配???
			 lottos.sort((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum()));
			 List<LottoNumDTO> lottoPrizeNumDTOs = lottos.parallelStream().sorted((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum())).map(item->item.lottoNumDto()).collect(Collectors.toList());
			 //
			 lottoFirstDTO = new LottoFirstDTO();
			 lottoFirstDTO.setTerm_num(lastLotto.getTermNum()+1);
			 lottoFirstDTO.setEndDate(TermEndDateUtil.getChoseEndTime());
			 lottoFirstDTO.setPrizes(lastLotto.getPrizes());
			 lottoFirstDTO.setPrizeList(lottoPrizeNumDTOs);
			 LottoDrop lottoDrop = lottoDropMapper.getLottoDropByTermNum(lastLotto.getTermNum());
			 if(lottoDrop != null) {
				 lottoFirstDTO.setPreList(Arrays.asList(lottoDrop.getPreDrop().split(",")));
				 lottoFirstDTO.setPostList(Arrays.asList(lottoDrop.getPostDrop().split(",")));
			 }
		 }
		 return lottoFirstDTO;
	 }

	/**
	 * 走势图数据
	 * @param param
	 * @return
	 */
	public LottoChartDataDTO getChartData(ChartSetupParam param) {
		LottoChartDataDTO lottoChartData = null;
		Integer count = param.getCount();
		//开奖号码
		 List<Lotto> allLottos = lottoMapper.getLastNumLottos(100);
		 List<LottoDrop> allLottoDrops = lottoDropMapper.getLastNumLottoDrops(100);
		 if(CollectionUtils.isNotEmpty(allLottos) && CollectionUtils.isNotEmpty(allLottoDrops)) {
			 Integer sort = param.getSort();
			 Integer compute = param.getCompute();
			 lottoChartData = new LottoChartDataDTO();
			 List<LottoDrop> lottoDrops = allLottoDrops;
			 List<Lotto> lottos = allLottos;
			 if(count < 100) {
				 lottoDrops = allLottoDrops.subList(0, count);
				 lottos = allLottos.subList(0, count);
			 }
			 //冷热数据
			 this.initHeatColds(allLottos, allLottoDrops.get(0), lottoChartData);
			 if(sort == 0) {//正序
				 List<LottoNumDTO> lottoNums = lottos.parallelStream().sorted((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum())).map(item->item.lottoNumDto()).collect(Collectors.toList());
				 lottoChartData.setLottoNums(lottoNums);
				 List<LottoNumDTO> preLottoNums = lottoDrops.parallelStream().sorted((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum())).map(item->item.preLottoNumDto()).collect(Collectors.toList());
				 List<LottoNumDTO> postLottoNums = lottoDrops.parallelStream().sorted((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum())).map(item->item.postLottoNumDto()).collect(Collectors.toList());
				 LottoDropDTO preLottoDrop = new LottoDropDTO();
				 preLottoDrop.setDrop(preLottoNums);
				 preLottoDrop.setAverageData(averageData);
				 preLottoDrop.setCountNum(countNum);
				 preLottoDrop.setMaxContinue(maxContinue);
				 preLottoDrop.setMaxData(maxData);
				 LottoDropDTO postLottoDrop = new LottoDropDTO();
				 postLottoDrop.setDrop(postLottoNums);
				 postLottoDrop.setAverageData(averageData);
				 postLottoDrop.setCountNum(countNum);
				 postLottoDrop.setMaxContinue(maxContinue);
				 postLottoDrop.setMaxData(maxData);
				 lottoChartData.setPreLottoDrop(preLottoDrop);
				 lottoChartData.setPostLottoDrop(postLottoDrop);
			 } else {//倒序
				 List<LottoNumDTO> lottoNums = lottos.parallelStream().sorted((item1,item2)->item2.getTermNum().compareTo(item1.getTermNum())).map(item->item.lottoNumDto()).collect(Collectors.toList());
				 lottoChartData.setLottoNums(lottoNums);
				 List<LottoNumDTO> preLottoNums = lottoDrops.parallelStream().sorted((item1,item2)->item2.getTermNum().compareTo(item1.getTermNum())).map(item->item.preLottoNumDto()).collect(Collectors.toList());
				 List<LottoNumDTO> postLottoNums = lottoDrops.parallelStream().sorted((item1,item2)->item2.getTermNum().compareTo(item1.getTermNum())).map(item->item.postLottoNumDto()).collect(Collectors.toList());
				 LottoDropDTO preLottoDrop = new LottoDropDTO();
				 preLottoDrop.setDrop(preLottoNums);
				 preLottoDrop.setAverageData(averageData);
				 preLottoDrop.setCountNum(countNum);
				 preLottoDrop.setMaxContinue(maxContinue);
				 preLottoDrop.setMaxData(maxData);
				 LottoDropDTO postLottoDrop = new LottoDropDTO();
				 postLottoDrop.setDrop(postLottoNums);
				 postLottoDrop.setAverageData(averageData);
				 postLottoDrop.setCountNum(countNum);
				 postLottoDrop.setMaxContinue(maxContinue);
				 postLottoDrop.setMaxData(maxData);
				 lottoChartData.setPreLottoDrop(preLottoDrop);
				 lottoChartData.setPostLottoDrop(postLottoDrop);
			 }
		 }
		return lottoChartData;
	}

	private void initHeatColds(List<Lotto> allLottos, LottoDrop lottoDrop, LottoChartDataDTO lottoChartData) {
		List<List<Integer>> preDatas = new ArrayList<List<Integer>>(100);
		List<List<Integer>> postDatas = new ArrayList<List<Integer>>(100);
		allLottos.forEach(item->{
			List<String> list = Arrays.asList(item.getPrizeNum().split(","));
			List<Integer> collect = list.parallelStream().map(item1->Integer.valueOf(item1)).collect(Collectors.toList());
			List<Integer> subList = collect.subList(0, 5);
			preDatas.add(subList);
			List<Integer> subList2 = collect.subList(5, 7);
			postDatas.add(subList2);
		});
		//30
		List<List<Integer>> preDatas30 = preDatas.subList(0, 30);
		List<Integer> allData30 = new ArrayList<Integer>(150);
		preDatas30.forEach(item->allData30.addAll(item));
		
		List<List<Integer>> preDatas50 = preDatas.subList(0, 50);
		
		postDatas.subList(0, 30);
		postDatas.subList(0, 50);
		List<LottoHeatColdDTO> preHeatColds = null;
		List<LottoHeatColdDTO> postHeatColds = null;
		lottoChartData.setPreHeatColds(preHeatColds);
		lottoChartData.setPostHeatColds(postHeatColds);
	}
	  
	 
}
