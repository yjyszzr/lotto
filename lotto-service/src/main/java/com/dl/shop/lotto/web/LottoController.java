package com.dl.shop.lotto.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dl.base.model.UserDeviceInfo;
import com.dl.base.param.EmptyParam;
import com.dl.base.result.BaseResult;
import com.dl.base.result.ResultGenerator;
import com.dl.base.util.JSONHelper;
import com.dl.base.util.MD5Util;
import com.dl.base.util.SessionUtil;
import com.dl.lotto.dto.LottoBetInfoDTO;
import com.dl.lotto.dto.LottoChartDataDTO;
import com.dl.lotto.dto.LottoDTO;
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.lotto.enums.LottoResultEnum;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.lotto.param.SaveBetInfoParam;
import com.dl.member.api.IUserBonusService;
import com.dl.member.api.IUserService;
import com.dl.shop.lotto.core.ProjectConstant;
import com.dl.shop.lotto.service.LottoService;
import com.dl.shop.lotto.utils.TermDateUtil;
import com.dl.shop.payment.dto.UserBetDetailInfoDTO;
import com.dl.shop.payment.dto.UserBetPayInfoDTO;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/lotto")
public class LottoController {

	@Resource
	private LottoService lottoFirstService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private IUserBonusService userBonusService;
	@Resource
	private IUserService userService;

	
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
	
	@ApiOperation(value = "投注确认", notes = "投注确认")
	@PostMapping("/saveBetInfo")
	public BaseResult<String> saveBetInfo(@RequestBody SaveBetInfoParam param) {
//		if(lottoFirstService.isShutDownBet()) {
//			return ResultGenerator.genResult(LottoResultEnum.GET_TICKET_INFO_NULL.getCode(), LottoResultEnum.GET_TICKET_INFO_NULL.getMsg());
//		}
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
		
		boolean isOk = lottoFirstService.checkBetInfo(param);
		if(!isOk) {
			return ResultGenerator.genResult(LottoResultEnum.BET_INFO_ERROR.getCode(), LottoResultEnum.BET_INFO_ERROR.getMsg());
		}
//		Double maxBetAmount = 0.0;
//		if(maxBetAmount >= 20000) {
//			return ResultGenerator.genResult(LottoResultEnum.BET_MONEY_LIMIT.getCode(), LottoResultEnum.BET_MONEY_LIMIT.getMsg());
//		}
		int betNum = param.getBetNum();
//		if(betNum >= 10000 || betNum < 0) {
//			return ResultGenerator.genResult(LottoResultEnum.BET_NUMBER_LIMIT.getCode(), LottoResultEnum.BET_NUMBER_LIMIT.getMsg());
//		}
		//投注时间
		LocalDateTime stopTime = TermDateUtil.getChoseEndDateTime();
		LocalDateTime nowTime = LocalDateTime.now();
		if(nowTime.isAfter(stopTime)) {
			return ResultGenerator.genResult(LottoResultEnum.GET_TICKET_INFO_NULL.getCode(), LottoResultEnum.GET_TICKET_INFO_NULL.getMsg());
		}
		
		String issue = lottoFirstService.getLatelyTerm();
		int lotteryPlayClassifyId ;
		//缓存订单支付信息
		UserBetPayInfoDTO dto = new UserBetPayInfoDTO();
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
		stringRedisTemplate.opsForValue().set(payToken, dtoJson, ProjectConstant.BET_INFO_EXPIRE_TIME, TimeUnit.MINUTES);
		return ResultGenerator.genSuccessResult("success", payToken);
	}
	
	
	@ApiOperation(value = "查询大乐透最近一期奖池", notes = "查询大乐透最近一期奖池")
	@RequestMapping(path="/queryLottoLatestPrizes", method=RequestMethod.POST)
	public BaseResult<LottoDTO> queryLottoLatestPrizes(@RequestBody EmptyParam emptyParam){
		return lottoFirstService.queryLottoLatestPrizes();
	}
}
