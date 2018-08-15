package com.dl.shop.lotto.web;

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
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.lotto.enums.LottoResultEnum;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.lotto.param.SaveBetInfoParam;
import com.dl.member.api.IUserBonusService;
import com.dl.member.api.IUserService;
import com.dl.shop.lotto.core.ProjectConstant;
import com.dl.shop.lotto.service.LottoService;
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
		/*//用户信息
		StrParam strParam = new StrParam();
		BaseResult<UserDTO> userInfoExceptPassRst = userService.userInfoExceptPass(strParam);
		if(userInfoExceptPassRst.getCode() != 0 || null == userInfoExceptPassRst.getData()) {
			return ResultGenerator.genResult(LottoResultEnum.OPTION_ERROR.getCode(), LottoResultEnum.OPTION_ERROR.getMsg());
		}*/
		boolean isOk = lottoFirstService.checkBetInfo(param);
		if(!isOk) {
			return ResultGenerator.genResult(LottoResultEnum.BET_INFO_ERROR.getCode(), LottoResultEnum.BET_INFO_ERROR.getMsg());
		}
		Double maxBetAmount = 0.0;
		if(maxBetAmount >= 20000) {
			return ResultGenerator.genResult(LottoResultEnum.BET_MONEY_LIMIT.getCode(), LottoResultEnum.BET_MONEY_LIMIT.getMsg());
		}
		int betNum = param.getBetNum();
		if(betNum >= 10000 || betNum < 0) {
			return ResultGenerator.genResult(LottoResultEnum.BET_NUMBER_LIMIT.getCode(), LottoResultEnum.BET_NUMBER_LIMIT.getMsg());
		}
	/*	String betMoney = betInfo.getMoney();
		Double orderMoney = Double.valueOf(betMoney);
		Double minBetMoney = lotteryMatchService.getMinBetMoney();
		if(orderMoney < minBetMoney) {
			return ResultGenerator.genResult(LottoResultEnum.BET_MATCH_WC.getCode(), "最低投注"+minBetMoney.intValue()+"元!");
		}
		int canBetMoney = lotteryMatchService.canBetMoney();
		if(orderMoney > canBetMoney) {
			return ResultGenerator.genResult(LottoResultEnum.BET_MATCH_STOP.getCode(), LottoResultEnum.BET_MATCH_STOP.getMsg());
		}*/
		String issue = "";
		List<UserBetDetailInfoDTO> betDetailInfos = new ArrayList<UserBetDetailInfoDTO>(betInfos.size());
		for(LottoBetInfoDTO betInfo: betInfos) {
			UserBetDetailInfoDTO dizqUserBetCellInfoDTO = new UserBetDetailInfoDTO();
			dizqUserBetCellInfoDTO.setMatchId(0);
			dizqUserBetCellInfoDTO.setChangci("");
			dizqUserBetCellInfoDTO.setIsDan(0);
			dizqUserBetCellInfoDTO.setLotteryClassifyId(param.getLotteryClassifyId());
			dizqUserBetCellInfoDTO.setLotteryPlayClassifyId(param.getLotteryPlayClassifyId());
			dizqUserBetCellInfoDTO.setMatchTeam("");
			dizqUserBetCellInfoDTO.setPlayCode("");
			dizqUserBetCellInfoDTO.setTicketData(betInfo.getBetInfo());
			dizqUserBetCellInfoDTO.setPlayType(betInfo.getPlayType());
			betDetailInfos.add(dizqUserBetCellInfoDTO);
		}
		//缓存订单支付信息
		UserBetPayInfoDTO dto = new UserBetPayInfoDTO();
		dto.setBetNum(betNum);
		dto.setOrderMoney(orderMoney);
		dto.setTicketNum(ticketNum);
//		dto.setBonusAmount(bonusAmount);
//		dto.setBonusId(bonusId);
//		dto.setSurplus(surplus);
//		String forecastMoney = betInfo.getMinBonus() + "~" + betInfo.getMaxBonus();
//		dto.setThirdPartyPaid(thirdPartyPaid);
		dto.setForecastMoney("");
		String requestFrom = "0";
		UserDeviceInfo userDevice = SessionUtil.getUserDevice();
		if(userDevice != null) {
			requestFrom = userDevice.getPlat();
		}
		dto.setRequestFrom(requestFrom);
		dto.setUserId(SessionUtil.getUserId());
		dto.setBetDetailInfos(betDetailInfos);
		dto.setIssue(issue);
		String dtoJson = JSONHelper.bean2json(dto);
		String keyStr = "bet_info_" + SessionUtil.getUserId() +"_"+ System.currentTimeMillis();
		String payToken = "lotto_" + MD5Util.crypt(keyStr);
		stringRedisTemplate.opsForValue().set(payToken, dtoJson, ProjectConstant.BET_INFO_EXPIRE_TIME, TimeUnit.MINUTES);
		return ResultGenerator.genSuccessResult("success", payToken);
	}
}
