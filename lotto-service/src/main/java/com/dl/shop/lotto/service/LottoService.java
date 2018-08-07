package com.dl.shop.lotto.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
				 LottoDropDTO postLottoDrop = new LottoDropDTO();
				 postLottoDrop.setDrop(postLottoNums);
				 lottoChartData.setPreLottoDrop(preLottoDrop);
				 lottoChartData.setPostLottoDrop(postLottoDrop);
				 if(compute == 0) {
					 List<LottoNumDTO> preLottoNums100 = allLottoDrops.parallelStream().sorted((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum())).map(item->item.preLottoNumDto()).collect(Collectors.toList());
					 List<LottoNumDTO> postLottoNums100 = allLottoDrops.parallelStream().sorted((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum())).map(item->item.postLottoNumDto()).collect(Collectors.toList());
					 initLottoDrop(preLottoDrop,preLottoNums100);
					 initLottoDrop(postLottoDrop,postLottoNums100);
				 }
			 } else {//倒序
				 List<LottoNumDTO> lottoNums = lottos.parallelStream().sorted((item1,item2)->item2.getTermNum().compareTo(item1.getTermNum())).map(item->item.lottoNumDto()).collect(Collectors.toList());
				 lottoChartData.setLottoNums(lottoNums);
				 List<LottoNumDTO> preLottoNums = lottoDrops.parallelStream().sorted((item1,item2)->item2.getTermNum().compareTo(item1.getTermNum())).map(item->item.preLottoNumDto()).collect(Collectors.toList());
				 List<LottoNumDTO> postLottoNums = lottoDrops.parallelStream().sorted((item1,item2)->item2.getTermNum().compareTo(item1.getTermNum())).map(item->item.postLottoNumDto()).collect(Collectors.toList());
				 LottoDropDTO preLottoDrop = new LottoDropDTO();
				 preLottoDrop.setDrop(preLottoNums);
				 LottoDropDTO postLottoDrop = new LottoDropDTO();
				 postLottoDrop.setDrop(postLottoNums);
				 lottoChartData.setPreLottoDrop(preLottoDrop);
				 lottoChartData.setPostLottoDrop(postLottoDrop);
				 if(compute == 0) {
					 List<LottoNumDTO> preLottoNums100 = allLottoDrops.parallelStream().sorted((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum())).map(item->item.preLottoNumDto()).collect(Collectors.toList());
					 List<LottoNumDTO> postLottoNums100 = allLottoDrops.parallelStream().sorted((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum())).map(item->item.postLottoNumDto()).collect(Collectors.toList());
					 initLottoDrop(preLottoDrop,preLottoNums100);
					 initLottoDrop(postLottoDrop,postLottoNums100);
				 }
				 //冷热倒序
				 Collections.reverse(lottoChartData.getPreHeatColds());//顺序反转
				 Collections.reverse(lottoChartData.getPostHeatColds());//顺序反转
			 }
		 }
		return lottoChartData;
	}
	
	/**
	 * 冷热数据计算
	 * @param allLottos
	 * @param lottoDrop
	 * @param lottoChartData
	 */
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
		//30红
		List<List<Integer>> preDatas30 = preDatas.subList(0, 30);
		List<Integer> allPreData30 = new ArrayList<Integer>(150);
		preDatas30.forEach(item->allPreData30.addAll(item));
		//50红
		List<List<Integer>> preDatas50 = preDatas.subList(0, 50);
		List<Integer> allPreData50 = new ArrayList<Integer>(250);
		preDatas50.forEach(item->allPreData50.addAll(item));
		//100红
		List<Integer> allPreData100 = new ArrayList<Integer>(500);
		preDatas.forEach(item->allPreData100.addAll(item));
		List<LottoHeatColdDTO> preHeatColds = new ArrayList<LottoHeatColdDTO>();
		//计算号码出现次数
		for (int i = 1; i <= 35; i++) {
			int j = i;
			Stream<Integer> stream30 = allPreData30.stream().filter(item->{return item==j;});
			Stream<Integer> stream50 = allPreData50.stream().filter(item->{return item==j;});
			Stream<Integer> stream100 = allPreData100.stream().filter(item->{return item==j;});
			LottoHeatColdDTO lhcDTO = new LottoHeatColdDTO();
			lhcDTO.setNum(j);
			lhcDTO.setCountA((int) stream30.count());
			lhcDTO.setCountB((int) stream50.count());
			lhcDTO.setCountC((int) stream100.count());
			lhcDTO.setDrop(Integer.parseInt(lottoDrop.preLottoNumDto().getNumList().get(i-1)));
			preHeatColds.add(lhcDTO);
		}
		//30蓝
		List<List<Integer>> postDatas30 = postDatas.subList(0, 30);
		List<Integer> allPostData30 = new ArrayList<Integer>(60);
		postDatas30.forEach(item->allPostData30.addAll(item));
		//50蓝
		List<List<Integer>> postDatas50 = postDatas.subList(0, 50);
		List<Integer> allPostData50 = new ArrayList<Integer>(100);
		postDatas50.forEach(item->allPostData50.addAll(item));
		//100蓝
		List<Integer> allPostData100 = new ArrayList<Integer>(200);
		postDatas.forEach(item->allPostData100.addAll(item));
		List<LottoHeatColdDTO> postHeatColds = new ArrayList<LottoHeatColdDTO>();;
		//计算号码出现次数
		for (int i = 1; i <= 12; i++) {
			int j = i;
			Stream<Integer> stream30 = allPostData30.stream().filter(item->{return item==j;});
			Stream<Integer> stream50 = allPostData50.stream().filter(item->{return item==j;});
			Stream<Integer> stream100 = allPostData100.stream().filter(item->{return item==j;});
			LottoHeatColdDTO lhcDTO = new LottoHeatColdDTO();
			lhcDTO.setNum(j);
			lhcDTO.setCountA((int) stream30.count());
			lhcDTO.setCountB((int) stream50.count());
			lhcDTO.setCountC((int) stream100.count());
			lhcDTO.setDrop(Integer.parseInt(lottoDrop.postLottoNumDto().getNumList().get(i-1)));
			postHeatColds.add(lhcDTO);
		}
		lottoChartData.setPreHeatColds(preHeatColds);
		lottoChartData.setPostHeatColds(postHeatColds);
	}
	
	/**
	 *遗漏统计计算   
	 * @param lottoDrop
	 * @param lottoNums
	 */
	private void initLottoDrop(LottoDropDTO lottoDrop,List<LottoNumDTO> lottoNums) {
		Map<Integer,List<String>> map = new HashMap<>();
		for (int i = 0; i < lottoNums.get(0).getNumList().size(); i++) {
			int j = i;
			List<String> numList = new ArrayList<>();
			lottoNums.forEach(item->{numList.add(item.getNumList().get(j));});
			map.put(i+1, numList);
		}
		
		List<String> averageList = new ArrayList<>();
		List<String> maxList = new ArrayList<>();
		List<String> countList = new ArrayList<>();
		List<String> continuityList =  new ArrayList<>();
		map.forEach((k,v)->{
			Integer sum = v.stream().collect(Collectors.summingInt(s-> Integer.parseInt(s)))/v.size();
			averageList.add(sum+"");//遗漏总和
			String max = v.stream().collect(Collectors.maxBy(Comparator.comparing(s-> Integer.parseInt(s)))).get();
			maxList.add(max);//遗漏最大
			Stream<String> count = v.stream().filter(l->{return l.equals("0");});
			countList.add(count.count()+"");//出现次数
			 int ctt = 0;
			 int ct = 0;
			 for(String s : v) {
				 if(s.equals("0")) {
					 ct = ct +1;
					 if(ct>ctt) {
						 ctt = ct;
					 }
				 }else {
					 ct = 0;
				 }
			 }
			 continuityList.add(ctt+"");//最大连出
		});
		lottoDrop.setAverageData(averageList);
		lottoDrop.setMaxData(maxList);
		lottoDrop.setCountNum(countList);
		lottoDrop.setMaxContinue(continuityList);
		
	}
}
