package com.dl.shop.lotto.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dl.base.model.UserDeviceInfo;
import com.dl.base.result.BaseResult;
import com.dl.base.result.ResultGenerator;
import com.dl.base.util.JSONHelper;
import com.dl.base.util.MD5Util;
import com.dl.base.util.SessionUtil;
import com.dl.lottery.dto.OrderIdDTO;
import com.dl.lotto.dto.LottoBetInfoDTO;
import com.dl.lotto.dto.LottoChartDataDTO;
import com.dl.lotto.dto.LottoDTO;
import com.dl.lotto.dto.LottoDropDTO;
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.lotto.dto.LottoHeatColdDTO;
import com.dl.lotto.dto.LottoNumDTO;
import com.dl.lotto.enums.LottoResultEnum;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.lotto.param.SaveBetInfoParam;
import com.dl.member.api.ISwitchConfigService;
import com.dl.member.api.ISysConfigService;
import com.dl.member.dto.SysConfigDTO;
import com.dl.member.param.SysConfigParam;
import com.dl.order.api.IOrderService;
import com.dl.order.dto.OrderDTO;
import com.dl.order.param.SubmitOrderParam;
import com.dl.order.param.SubmitOrderParam.TicketDetail;
import com.dl.shop.lotto.dao2.LottoDropMapper;
import com.dl.shop.lotto.dao2.LottoMapper;
import com.dl.shop.lotto.model.Lotto;
import com.dl.shop.lotto.model.LottoDrop;
import com.dl.shop.lotto.utils.MathUtil;
import com.dl.shop.lotto.utils.TermDateUtil;
import com.dl.shop.payment.dto.UserBetDetailInfoDTO;
import com.dl.shop.payment.dto.UserBetPayInfoDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(value = "transactionManager2")
public class LottoService {
	@Resource
	private LottoMapper lottoMapper;
	@Resource
	private LottoDropMapper lottoDropMapper;
	@Resource
	private IOrderService iOrderService;
	
	@Resource
	private ISwitchConfigService iSwitchConfigService;
	@Resource
	private ISysConfigService iSysCfgService;
	
	public String getLatelyTerm() {
		List<Lotto> lottos = lottoMapper.getLastNumLottos(1);
		int term = lottos.get(0).getTermNum() + 1;
		return term + "";
	}

	/**
	 * 创建模拟订单
	 * @return
	 */
	public BaseResult<OrderIdDTO> createOrderBySimulate(SaveBetInfoParam param){
		//save bet info
		String orderMoneyStr = param.getOrderMoney();
		if(StringUtils.isBlank(orderMoneyStr)) {
			return ResultGenerator.genResult(LottoResultEnum.PARAM_ERROR.getCode(), LottoResultEnum.PARAM_ERROR.getMsg());
		}
		Double orderMoney = Double.valueOf(orderMoneyStr);
		List<LottoBetInfoDTO> betInfos = param.getBetInfos();
		if(CollectionUtils.isEmpty(betInfos)) {
			return ResultGenerator.genResult(LottoResultEnum.PARAM_ERROR.getCode(), LottoResultEnum.PARAM_ERROR.getMsg());
		}
		Integer ticketNum = betInfos.size();
		boolean isOk = checkBetInfo(param);
		if(!isOk) {
			return ResultGenerator.genResult(LottoResultEnum.BET_INFO_ERROR.getCode(), LottoResultEnum.BET_INFO_ERROR.getMsg());
		}
		//是否包含胆拖
		boolean isContainDantuo = false;
		if(betInfos != null) {
			for(LottoBetInfoDTO lottoBetInfo : betInfos) {
				String betsInfo = lottoBetInfo.getBetInfo();
				if(betsInfo != null && betsInfo.contains("$")) {
					isContainDantuo = true;
					break;
				}
			}
		}
		log.info("[createOrderBySimulate]" + " isContainDantuo:" + isContainDantuo);
		if(isContainDantuo) {
			return ResultGenerator.genResult(LottoResultEnum.BET_DANTUO_FORBIT.getCode(), LottoResultEnum.BET_DANTUO_FORBIT.getMsg());
		}
		int betNum = param.getBetNum();
		//投注时间
		LocalDateTime stopTime = TermDateUtil.getChoseEndDateTime();
		LocalDateTime nowTime = LocalDateTime.now();
		if(nowTime.isAfter(stopTime)) {
			return ResultGenerator.genResult(LottoResultEnum.GET_TICKET_INFO_NULL.getCode(), LottoResultEnum.GET_TICKET_INFO_NULL.getMsg());
		}
		String issue = getLatelyTerm();
		int lotteryPlayClassifyId ;
		//缓存订单支付信息
		UserBetPayInfoDTO dto = new UserBetPayInfoDTO();
		dto.setBetType("00");	//bet type 投注类型
		dto.setLotteryClassifyId(2);
		if(param.getIsAppend()==0) {
			lotteryPlayClassifyId = 9;
			dto.setPlayType("0");
		}else {
			lotteryPlayClassifyId = 10;
			dto.setPlayType("5");
		}
		dto.setLotteryPlayClassifyId(lotteryPlayClassifyId);
		dto.setBetNum(betNum);
		dto.setOrderMoney(orderMoney);
		dto.setTicketNum(ticketNum);
		dto.setTimes(param.getTimes());
		
		dto.setForecastMoney("");
		String requestFrom = "0";
		UserDeviceInfo userDevice = SessionUtil.getUserDevice();
		if(userDevice != null) {
			requestFrom = userDevice.getPlat();
		}
		dto.setRequestFrom(requestFrom);
		dto.setUserId(SessionUtil.getUserId());
		dto.setIssue(issue);
		List<UserBetDetailInfoDTO> betDetailInfos = new ArrayList<UserBetDetailInfoDTO>(betInfos.size());
		for(LottoBetInfoDTO betInfo: betInfos) {
			UserBetDetailInfoDTO dizqUserBetCellInfoDTO = new UserBetDetailInfoDTO();
			dizqUserBetCellInfoDTO.setMatchId(0);
			dizqUserBetCellInfoDTO.setChangci("");
			dizqUserBetCellInfoDTO.setIsDan(0);
			dizqUserBetCellInfoDTO.setLotteryClassifyId(2);
			dizqUserBetCellInfoDTO.setLotteryPlayClassifyId(lotteryPlayClassifyId);
			dizqUserBetCellInfoDTO.setMatchTeam("");
			dizqUserBetCellInfoDTO.setPlayCode(issue);
			dizqUserBetCellInfoDTO.setTicketData(betInfo.getBetInfo());
			dizqUserBetCellInfoDTO.setBetType("0"+betInfo.getPlayType());
			dizqUserBetCellInfoDTO.setFixedodds("");
			dizqUserBetCellInfoDTO.setMatchTime((int)TermDateUtil.getTermEndDate());
			betDetailInfos.add(dizqUserBetCellInfoDTO);
		}
		dto.setBetDetailInfos(betDetailInfos);
		String dtoJson = JSONHelper.bean2json(dto);
		String keyStr = "bet_info_" + SessionUtil.getUserId() +"_"+ System.currentTimeMillis();
		String payToken = "lotto_" + MD5Util.crypt(keyStr);
		
		log.info("[createOrderBySimulate]" + " dtoJson:" + dtoJson + "\n keyStr:" + keyStr + " payToken:" + payToken);
		log.info("=================== saveBetInfo succ====================");
		
		// order生成
		final String betType = dto.getBetType();
		List<TicketDetail> ticketDetails = betDetailInfos.stream().map(betCell -> {
			TicketDetail ticketDetail = new TicketDetail();
			ticketDetail.setMatch_id(betCell.getMatchId());
			ticketDetail.setChangci(betCell.getChangci());
			int matchTime = betCell.getMatchTime();
			if (matchTime > 0) {
				ticketDetail.setMatchTime(Date.from(Instant.ofEpochSecond(matchTime)));
			}
			ticketDetail.setMatchTeam(betCell.getMatchTeam());
			ticketDetail.setLotteryClassifyId(betCell.getLotteryClassifyId());
			ticketDetail.setLotteryPlayClassifyId(betCell.getLotteryPlayClassifyId());
			ticketDetail.setTicketData(betCell.getTicketData());
			ticketDetail.setIsDan(betCell.getIsDan());
			ticketDetail.setIssue(betCell.getPlayCode());
			ticketDetail.setFixedodds(betCell.getFixedodds());
			ticketDetail.setBetType(betCell.getBetType());
			return ticketDetail;
		}).collect(Collectors.toList());
		
		BigDecimal moneyPaid = BigDecimal.valueOf(orderMoney);
		BigDecimal ticketAmount = BigDecimal.valueOf(orderMoney);
		BigDecimal surplus = BigDecimal.ZERO;
		BigDecimal thirdPartyPaid = BigDecimal.ZERO;
		
		SubmitOrderParam submitOrderParam = new SubmitOrderParam();
		submitOrderParam.setStoreId(param.getStoreId());
		submitOrderParam.setTicketNum(dto.getTicketNum());
		submitOrderParam.setMoneyPaid(moneyPaid);
		submitOrderParam.setTicketAmount(ticketAmount);
		submitOrderParam.setSurplus(surplus);
		submitOrderParam.setThirdPartyPaid(thirdPartyPaid);
		submitOrderParam.setPayName("");
		submitOrderParam.setUserBonusId(0);
		submitOrderParam.setBonusAmount(BigDecimal.ZERO);
		submitOrderParam.setOrderFrom(dto.getRequestFrom());
		int lotteryClassifyId = dto.getLotteryClassifyId();
		String lotteryClassifyIdStr = lotteryClassifyId + "";
		submitOrderParam.setLotteryClassifyId(lotteryClassifyId);
		
		lotteryPlayClassifyId = dto.getLotteryPlayClassifyId();
		submitOrderParam.setLotteryPlayClassifyId(lotteryPlayClassifyId);
		submitOrderParam.setPassType(dto.getBetType());
		submitOrderParam.setPlayType("0" + dto.getPlayType());
		submitOrderParam.setBetNum(dto.getBetNum());
		submitOrderParam.setCathectic(dto.getTimes());
		if (lotteryPlayClassifyId != 8 && lotteryClassifyId == 1) {
			if (ticketDetails.size() > 1) {
				Optional<TicketDetail> max = ticketDetails.stream().max((detail1, detail2) -> detail1.getMatchTime().compareTo(detail2.getMatchTime()));
				submitOrderParam.setMatchTime(max.get().getMatchTime());
			} else {
				submitOrderParam.setMatchTime(ticketDetails.get(0).getMatchTime());
			}
		}
		submitOrderParam.setForecastMoney(dto.getForecastMoney());

		submitOrderParam.setIssue(dto.getIssue());
		submitOrderParam.setTicketDetails(ticketDetails);
		BaseResult<OrderDTO> createOrder = iOrderService.createOrder(submitOrderParam);
		if (createOrder.getCode() != 0) {
			log.info("订单创建失败！msg:" + createOrder.getMsg());
			return ResultGenerator.genFailResult("创建模拟订单失败");
		}
		String orderId = createOrder.getData().getOrderId().toString();
		String orderSn = createOrder.getData().getOrderSn();
		OrderIdDTO orderDTO = new OrderIdDTO();
		orderDTO.setOrderId(orderId);
		orderDTO.setOrderSn(orderSn);
		return ResultGenerator.genSuccessResult("success", orderDTO);
	}
	
	/**
	 * 投注页数据
	 */
	public LottoFirstDTO queryFirstData() {
		LottoFirstDTO lottoFirstDTO = null;
		// 获取最近10期的开奖
		List<Lotto> lottos = lottoMapper.getLastNumLottos(10);
		if (lottos.size() >= 0) {
			Lotto lastLotto = lottos.get(0);
			// @Todo
			// 判断该期次和当前日期是否匹配
			if (TermDateUtil.isLast(lastLotto.getPrizeDate())) {
				lottos.sort((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()));
				List<LottoNumDTO> lottoPrizeNumDTOs = lottos.parallelStream()
						.sorted((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()))
						.map(item -> item.lottoNumDto()).collect(Collectors.toList());
				//
				lottoFirstDTO = new LottoFirstDTO();
				lottoFirstDTO.setTerm_num(lastLotto.getTermNum() + 1);
				lottoFirstDTO.setEndDate(TermDateUtil.getChoseEndTime());
				lottoFirstDTO.setPrizes(lastLotto.getPrizes());
				lottoFirstDTO.setPrizeList(lottoPrizeNumDTOs);
				LottoDrop lottoDrop = lottoDropMapper.getLottoDropByTermNum(lastLotto.getTermNum());
				if (lottoDrop != null) {
					lottoFirstDTO.setPreList(Arrays.asList(lottoDrop.getPreDrop().split(",")));
					lottoFirstDTO.setPostList(Arrays.asList(lottoDrop.getPostDrop().split(",")));
				}
			}
		}
		//是否展示胆拖
		SysConfigParam sysCfgParam = new SysConfigParam();
		sysCfgParam.setBusinessId(54);
		BaseResult<SysConfigDTO> baseResult = iSysCfgService.querySysConfig(sysCfgParam);
		if(lottoFirstDTO != null && baseResult != null && baseResult.isSuccess()) {
			SysConfigDTO sysCfgDTO = baseResult.getData();
			int isShow = sysCfgDTO.getValue().intValue();
			lottoFirstDTO.setIsShowDragOn(isShow);
		}
		return lottoFirstDTO;
	}

	/**
	 * 走势图数据
	 * 
	 * @param param
	 * @return
	 */
	public LottoChartDataDTO getChartData(ChartSetupParam param) {
		LottoChartDataDTO lottoChartData = null;
		Integer count = param.getCount();
		Integer tab = param.getTab();
		// 开奖号码
		List<Lotto> allLottos = lottoMapper.getLastNumLottos(100);
		List<LottoDrop> allLottoDrops = lottoDropMapper.getLastNumLottoDrops(100);
		if (CollectionUtils.isNotEmpty(allLottos) && CollectionUtils.isNotEmpty(allLottoDrops)) {
			Integer sort = param.getSort();
			Integer compute = param.getCompute();
			Integer drop = param.getDrop();
			lottoChartData = new LottoChartDataDTO();
			List<LottoDrop> lottoDrops = allLottoDrops;
			List<Lotto> lottos = allLottos;
			if (count < 100) {
				lottoDrops = allLottoDrops.subList(0, count);
				lottos = allLottos.subList(0, count);
			}
			// 添加期次与截止时间
			if (tab == 0 || tab == 1) {
				Lotto lastLotto = lottos.get(0);
				if (TermDateUtil.isLast(lastLotto.getPrizeDate())) {
					int term = lastLotto.getTermNum() + 1;
					lottoChartData.setStopTime(term + "|" + TermDateUtil.getChoseEndTime());
				}
			}
			// 冷热数据
			this.initHeatColds(allLottos, allLottoDrops.get(0), lottoChartData, tab);
			if (sort == 0) {// 正序
				// 历史开奖tab
				if (tab == 0 || tab == 1) {
					List<LottoNumDTO> lottoNums = lottos.parallelStream()
							.sorted((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()))
							.map(item -> item.lottoNumDto()).collect(Collectors.toList());
					lottoChartData.setLottoNums(lottoNums);
				}
				// 红球走势tab
				if (tab == 0 || tab == 2) {
					List<LottoNumDTO> preLottoNums = lottoDrops.parallelStream()
							.sorted((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()))
							.map(item -> item.preLottoNumDto()).collect(Collectors.toList());
					// 隐藏遗漏
					if (drop == 0) {
						preLottoNums.forEach(items -> {
							List<String> list = new ArrayList<>();
							items.getNumList().forEach(item -> {
								if (item.equals("0")) {
									list.add(item);
								} else {
									list.add("");
								}
							});
							items.setNumList(list);
						});
					}
					LottoDropDTO preLottoDrop = new LottoDropDTO();
					preLottoDrop.setDrop(preLottoNums);
					lottoChartData.setPreLottoDrop(preLottoDrop);
					// 显示统计
					if (compute == 1) {
						List<LottoNumDTO> numsByCount = lottoDrops.parallelStream()
								.sorted((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()))
								.map(item -> item.preLottoNumDto()).collect(Collectors.toList());
						this.initLottoDrop(preLottoDrop, numsByCount);
					}
				}
				// 蓝球走势tab
				if (tab == 0 || tab == 3) {
					List<LottoNumDTO> postLottoNums = lottoDrops.parallelStream()
							.sorted((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()))
							.map(item -> item.postLottoNumDto()).collect(Collectors.toList());
					// 隐藏遗漏
					if (drop == 0) {
						postLottoNums.forEach(items -> {
							List<String> list = new ArrayList<>();
							items.getNumList().forEach(item -> {
								if (item.equals("0")) {
									list.add(item);
								} else {
									list.add("");
								}
							});
							items.setNumList(list);
						});
					}
					LottoDropDTO postLottoDrop = new LottoDropDTO();
					postLottoDrop.setDrop(postLottoNums);
					lottoChartData.setPostLottoDrop(postLottoDrop);
					// 显示统计
					if (compute == 1) {
						List<LottoNumDTO> numsByCount = lottoDrops.parallelStream()
								.sorted((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()))
								.map(item -> item.postLottoNumDto()).collect(Collectors.toList());
						this.initLottoDrop(postLottoDrop, numsByCount);
					}
				}
			} else {// 倒序
				if (tab == 0 || tab == 1) {
					List<LottoNumDTO> lottoNums = lottos.parallelStream()
							.sorted((item1, item2) -> item2.getTermNum().compareTo(item1.getTermNum()))
							.map(item -> item.lottoNumDto()).collect(Collectors.toList());
					lottoChartData.setLottoNums(lottoNums);
				}
				// 红球走势tab
				if (tab == 0 || tab == 2) {
					List<LottoNumDTO> preLottoNums = lottoDrops.parallelStream()
							.sorted((item1, item2) -> item2.getTermNum().compareTo(item1.getTermNum()))
							.map(item -> item.preLottoNumDto()).collect(Collectors.toList());
					// 隐藏遗漏
					if (drop == 0) {
						preLottoNums.forEach(items -> {
							List<String> list = new ArrayList<>();
							items.getNumList().forEach(item -> {
								if (item.equals("0")) {
									list.add(item);
								} else {
									list.add("");
								}
							});
							items.setNumList(list);
						});
					}
					LottoDropDTO preLottoDrop = new LottoDropDTO();
					preLottoDrop.setDrop(preLottoNums);
					lottoChartData.setPreLottoDrop(preLottoDrop);
					// 显示统计
					if (compute == 1) {
						List<LottoNumDTO> numsByCount = lottoDrops.parallelStream()
								.sorted((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()))
								.map(item -> item.preLottoNumDto()).collect(Collectors.toList());
						this.initLottoDrop(preLottoDrop, numsByCount);
					}
				}
				// 蓝球走势tab
				if (tab == 0 || tab == 3) {
					List<LottoNumDTO> postLottoNums = lottoDrops.parallelStream()
							.sorted((item1, item2) -> item2.getTermNum().compareTo(item1.getTermNum()))
							.map(item -> item.postLottoNumDto()).collect(Collectors.toList());
					// 隐藏遗漏
					if (drop == 0) {
						postLottoNums.forEach(items -> {
							List<String> list = new ArrayList<>();
							items.getNumList().forEach(item -> {
								if (item.equals("0")) {
									list.add(item);
								} else {
									list.add("");
								}
							});
							items.setNumList(list);
						});
					}
					LottoDropDTO postLottoDrop = new LottoDropDTO();
					postLottoDrop.setDrop(postLottoNums);
					lottoChartData.setPostLottoDrop(postLottoDrop);
					// 显示统计
					if (compute == 1) {
						List<LottoNumDTO> numsByCount = lottoDrops.parallelStream()
								.sorted((item1, item2) -> item1.getTermNum().compareTo(item2.getTermNum()))
								.map(item -> item.postLottoNumDto()).collect(Collectors.toList());
						this.initLottoDrop(postLottoDrop, numsByCount);
					}
				}
				// 冷热倒序
				if (tab == 4 || tab == 0) {
					Collections.reverse(lottoChartData.getPreHeatColds());// 顺序反转
				}
				if (tab == 5 || tab == 0) {
					Collections.reverse(lottoChartData.getPostHeatColds());// 顺序反转
				}
			}
		}
		return lottoChartData;
	}

	/**
	 * 冷热数据计算
	 * 
	 * @param allLottos
	 * @param lottoDrop
	 * @param lottoChartData
	 */
	private void initHeatColds(List<Lotto> allLottos, LottoDrop lottoDrop, LottoChartDataDTO lottoChartData,
			Integer tab) {
		if (tab == 4 || tab == 0) {
			List<List<Integer>> preDatas = new ArrayList<List<Integer>>(100);
			allLottos.forEach(item -> {
				List<String> list = Arrays.asList(item.getPrizeNum().split(","));
				List<Integer> collect = list.parallelStream().map(item1 -> Integer.valueOf(item1))
						.collect(Collectors.toList());
				List<Integer> subList = collect.subList(0, 5);
				preDatas.add(subList);
			});
			// 30红
			List<List<Integer>> preDatas30 = preDatas.subList(0, 30);
			List<Integer> allPreData30 = new ArrayList<Integer>(150);
			preDatas30.forEach(item -> allPreData30.addAll(item));
			// 50红
			List<List<Integer>> preDatas50 = preDatas.subList(0, 50);
			List<Integer> allPreData50 = new ArrayList<Integer>(250);
			preDatas50.forEach(item -> allPreData50.addAll(item));
			// 100红
			List<Integer> allPreData100 = new ArrayList<Integer>(500);
			preDatas.forEach(item -> allPreData100.addAll(item));
			List<LottoHeatColdDTO> preHeatColds = new ArrayList<LottoHeatColdDTO>();
			// 计算号码出现次数
			for (int i = 1; i <= 35; i++) {
				int j = i;
				Stream<Integer> stream30 = allPreData30.stream().filter(item -> {
					return item == j;
				});
				Stream<Integer> stream50 = allPreData50.stream().filter(item -> {
					return item == j;
				});
				Stream<Integer> stream100 = allPreData100.stream().filter(item -> {
					return item == j;
				});
				LottoHeatColdDTO lhcDTO = new LottoHeatColdDTO();
				lhcDTO.setNum(j);
				lhcDTO.setCountA((int) stream30.count());
				lhcDTO.setCountB((int) stream50.count());
				lhcDTO.setCountC((int) stream100.count());
				lhcDTO.setDrop(Integer.parseInt(lottoDrop.preLottoNumDto().getNumList().get(i - 1)));
				preHeatColds.add(lhcDTO);
			}
			lottoChartData.setPreHeatColds(preHeatColds);
		}
		if (tab == 5 || tab == 0) {
			List<List<Integer>> postDatas = new ArrayList<List<Integer>>(100);
			allLottos.forEach(item -> {
				List<String> list = Arrays.asList(item.getPrizeNum().split(","));
				List<Integer> collect = list.parallelStream().map(item1 -> Integer.valueOf(item1))
						.collect(Collectors.toList());
				List<Integer> subList2 = collect.subList(5, 7);
				postDatas.add(subList2);
			});
			// 30蓝
			List<List<Integer>> postDatas30 = postDatas.subList(0, 30);
			List<Integer> allPostData30 = new ArrayList<Integer>(60);
			postDatas30.forEach(item -> allPostData30.addAll(item));
			// 50蓝
			List<List<Integer>> postDatas50 = postDatas.subList(0, 50);
			List<Integer> allPostData50 = new ArrayList<Integer>(100);
			postDatas50.forEach(item -> allPostData50.addAll(item));
			// 100蓝
			List<Integer> allPostData100 = new ArrayList<Integer>(200);
			postDatas.forEach(item -> allPostData100.addAll(item));
			List<LottoHeatColdDTO> postHeatColds = new ArrayList<LottoHeatColdDTO>();
			;
			// 计算号码出现次数
			for (int i = 1; i <= 12; i++) {
				int j = i;
				Stream<Integer> stream30 = allPostData30.stream().filter(item -> {
					return item == j;
				});
				Stream<Integer> stream50 = allPostData50.stream().filter(item -> {
					return item == j;
				});
				Stream<Integer> stream100 = allPostData100.stream().filter(item -> {
					return item == j;
				});
				LottoHeatColdDTO lhcDTO = new LottoHeatColdDTO();
				lhcDTO.setNum(j);
				lhcDTO.setCountA((int) stream30.count());
				lhcDTO.setCountB((int) stream50.count());
				lhcDTO.setCountC((int) stream100.count());
				lhcDTO.setDrop(Integer.parseInt(lottoDrop.postLottoNumDto().getNumList().get(i - 1)));
				postHeatColds.add(lhcDTO);
			}
			lottoChartData.setPostHeatColds(postHeatColds);
		}
	}

	/**
	 * 遗漏统计计算
	 * 
	 * @param lottoDrop
	 * @param lottoNums
	 */
	private void initLottoDrop(LottoDropDTO lottoDrop, List<LottoNumDTO> lottoNums) {
		Map<Integer, List<String>> map = new HashMap<>();
		for (int i = 0; i < lottoNums.get(0).getNumList().size(); i++) {
			int j = i;
			List<String> numList = new ArrayList<>();
			lottoNums.forEach(item -> {
				numList.add(item.getNumList().get(j));
			});
			map.put(i + 1, numList);
		}

		List<String> averageList = new ArrayList<>();
		List<String> maxList = new ArrayList<>();
		List<String> countList = new ArrayList<>();
		List<String> continuityList = new ArrayList<>();
		map.forEach((k, v) -> {
			Integer sum = v.stream().collect(Collectors.summingInt(s -> Integer.parseInt(s))) / v.size();
			averageList.add(sum + "");// 遗漏总和
			String max = v.stream().collect(Collectors.maxBy(Comparator.comparing(s -> Integer.parseInt(s)))).get();
			maxList.add(max);// 遗漏最大
			Stream<String> count = v.stream().filter(l -> {
				return l.equals("0");
			});
			countList.add(count.count() + "");// 出现次数
			int ctt = 0;
			int ct = 0;
			for (String s : v) {
				if (s.equals("0")) {
					ct = ct + 1;
					if (ct > ctt) {
						ctt = ct;
					}
				} else {
					ct = 0;
				}
			}
			continuityList.add(ctt + "");// 最大连出
		});
		lottoDrop.setAverageData(averageList);
		lottoDrop.setMaxData(maxList);
		lottoDrop.setCountNum(countList);
		lottoDrop.setMaxContinue(continuityList);

	}

	/**
	 * 校验投注信息
	 * 
	 * @param param
	 * @return
	 */
	public boolean checkBetInfo(SaveBetInfoParam param) {
		// TODO Auto-generated method stub
		boolean isOK = true;
		List<LottoBetInfoDTO> betInfos = param.getBetInfos();
		// 是否选了号码
		if (betInfos.size() == 0) {
			return false;
		}
		int betNumSum = 0;// 订单总注数
		for (int i = 0; i < betInfos.size(); i++) {
			String info = betInfos.get(i).getBetInfo();
			int betNum = 0;// 单张票注数
			if (info.contains("$")) {
				if (betInfos.get(i).getPlayType() != 2) {
					log.error("投注内容错误。" + info);
					return false;
				}
				List<String> redDanCathectics = new ArrayList<>();
				List<String> blueDanCathectics = new ArrayList<>();
				List<String> redTuoCathectics = new ArrayList<>();
				List<String> blueTuoCathectics = new ArrayList<>();
				String[] nums = info.split("\\|");
				if (nums.length < 2) {
					log.error("投注内容错误。" + info);
					return false;
				}
				if (nums[0].contains("$")) {// 如果前区有$
					String[] preNums = nums[0].split("\\$");
					if (preNums.length < 2) {
						log.error("投注内容错误。" + info);
						return false;
					}
					redDanCathectics = Arrays.asList(preNums[0].split(","));
					redTuoCathectics = Arrays.asList(preNums[1].split(","));
				}
				if (nums[1].contains("$")) {// 如果后区有$
					String[] postNums = nums[1].split("\\$");
					if (postNums.length < 2) {
						log.error("投注内容错误。" + info);
						return false;
					}
					blueDanCathectics = Arrays.asList(postNums[0].split(","));
					blueTuoCathectics = Arrays.asList(postNums[1].split(","));
				} else {
					blueTuoCathectics = Arrays.asList(nums[1].split(","));
				}
				betNum = MathUtil.getDanTuoCathecticsCount(redTuoCathectics.size(), redDanCathectics.size(),
						blueTuoCathectics.size(), blueDanCathectics.size());
				// 单票注数校验
				if (betInfos.get(i).getBetNum() != betNum) {
					log.error("投注内容错误。");
					return false;
				}
				int betAmount = 0;
				// 单张钱校验
				if (param.getIsAppend() == 0) {
					betAmount = betNum * 2 * param.getTimes();
				} else {
					betAmount = betNum * 3 * param.getTimes();
				}
				if (Integer.parseInt(betInfos.get(i).getAmount()) != betAmount) {
					log.error("投注内容错误。" + info);
					return false;
				}
			} else {
				String[] nums = info.split("\\|");
				if (nums.length < 2) {
					log.error("投注内容错误。" + info);
					return false;
				}
				List<String> preList = Arrays.asList(nums[0].split(","));
				List<String> postList = Arrays.asList(nums[1].split(","));
				if (betInfos.get(i).getPlayType() != 1 && (preList.size() + postList.size()) > 7) {
					log.error("投注内容错误。" + info);
					return false;
				}
				if (betInfos.get(i).getPlayType() != 0 && (preList.size() + postList.size()) == 7) {
					log.error("投注内容错误。" + info);
					return false;
				}
				betNum = MathUtil.getCathecticsCount(preList.size(), postList.size());
				// 单票注数校验
				if (betInfos.get(i).getBetNum() != betNum) {
					log.error("投注内容错误。");
					return false;
				}
				int betAmount = 0;
				// 单张钱校验
				if (param.getIsAppend() == 0) {
					betAmount = betNum * 2 * param.getTimes();
				} else {
					betAmount = betNum * 3 * param.getTimes();
				}
				if (Integer.parseInt(betInfos.get(i).getAmount()) != betAmount) {
					log.error("投注内容错误。" + info);
					return false;
				}
			}
			betNumSum = betNumSum + betNum;
		}
		// 定单总注数校验
		if (param.getBetNum() != betNumSum) {
			log.error("投注内容错误。");
			return false;
		}
		int amount = 0;// 金额
		// 定单总金额校验
		if (param.getIsAppend() == 0) {
			amount = betNumSum * 2 * param.getTimes();
		} else {
			amount = betNumSum * 3 * param.getTimes();
		}
		if (Integer.parseInt(param.getOrderMoney()) != amount) {
			log.error("投注内容错误。");
			return false;
		}

		return true;
	}

	/**
	 * 查询最近一期大乐透的奖池
	 * 
	 * @return
	 */
	public BaseResult<LottoDTO> queryLottoLatestPrizes() {
		LottoDTO lottoDTO = new LottoDTO();
		List<Lotto> list = lottoMapper.getLastNumLottos(1);
		Lotto lotto = list.get(0);
		lottoDTO.setTerm_num(lotto.getPrizeNum());
		lottoDTO.setPrizes(lotto.getPrizes());
		lottoDTO.setPrize_date(lotto.getPrizeDate());
		return ResultGenerator.genSuccessResult("success", lottoDTO);
	}

	// 是否停止售卖
	@Transactional(value = "transactionManager1")
	public boolean isShutDownBet() {
		SysConfigParam sysCfgParam = new SysConfigParam();
		sysCfgParam.setBusinessId(56);
		BaseResult<SysConfigDTO> baseR = iSysCfgService.querySysConfig(sysCfgParam);
		if(baseR != null && baseR.isSuccess()) {
			SysConfigDTO sysCfgDTO = baseR.getData();
			int shutDownBetValue = sysCfgDTO.getValue().intValue();
			if(shutDownBetValue == 1) {
				return true;
			}
		}
		//判断用户是否有交易
//		UserDealActionParam param = new UserDealActionParam();
//		param.setUserId(SessionUtil.getUserId());
//		BaseResult<Integer> userDealAction = iSwitchConfigService.userDealAction(param);
//		Integer data = userDealAction.getData();
//		if (null != data && 0 == data) {
//			return true;
//		}
		List<Lotto> lottos = lottoMapper.getLastNumLottos(1);
		if (lottos.size() >= 0) {
			Lotto lastLotto = lottos.get(0);
			if (!TermDateUtil.isLast(lastLotto.getPrizeDate())) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	//1,3,6
	public boolean isShutDownV2() {
		boolean isShutDown = false;
		Calendar cal = Calendar.getInstance();
		int w = cal.get(Calendar.DAY_OF_WEEK)-1;
		log.info("w:"+w);
		if(w == 1 || w == 3 || w == 6) {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			String strTime = df.format(new Date());
			log.info(strTime);
			if(strTime.compareTo("19:00") > 0 && strTime.compareTo("24:00") < 0) {
				isShutDown = true;
			}
		}
		return isShutDown;
	}

//	public LottoService() {
//		boolean isShutDown = isShutDownV2();
//		System.out.println("isShutDown:" + isShutDown);
//		isShutDown = isShutDownBet();
//		System.out.println("isShutDown:" + isShutDown);
//	}
//	
//	public static void main(String[] args) {
//		new LottoService();
//	}
}
