package com.iflytek.vie.standard.push;

import com.iflytek.vie.app.api.topicgroup.TopicGroupService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.topicgroup.BusiAndPushRequest;
import com.iflytek.vie.app.pojo.topicgroup.CallCounTrendResponse;
import com.iflytek.vie.app.pojo.topicgroup.CallListResponse;
import com.iflytek.vie.app.pojo.topicgroup.TopicModelDTO;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.config.LoadConfig;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("businessOverviewController")
@RequestMapping({"/businessOverview"})
public class BusinessOverviewController {
   private static final Logger logger = LoggerFactory.getLogger(BusinessOverviewController.class);
   @Autowired
   private TopicGroupService topicGroupService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/queryBusiOverview"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryBusiOverview(HttpServletRequest request, BusiAndPushRequest busiAndPushRequest) {
      try {
         busiAndPushRequest.setUserId(BaseUtils.getUserId(request));
         busiAndPushRequest.setSystemId(this.commonService.getContextPath());
         busiAndPushRequest.setDataSource(BaseUtils.getDataSource(request));
         busiAndPushRequest.setTimeThreshold(Integer.parseInt(LoadConfig.getConfigProperty("topicFocusPushTimeThreshold")));
         busiAndPushRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         List<TopicModelDTO> list = this.topicGroupService.queryBusiOverview(busiAndPushRequest);
         return ResponseResult.success(list, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【queryBusiOverview】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getCallCountTrend"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getCallCountTrend(HttpServletRequest request, BusiAndPushRequest busiAndPushRequest) {
      try {
         busiAndPushRequest.setDataSource(BaseUtils.getDataSource(request));
         CallCounTrendResponse response = this.topicGroupService.getCallCountTrend(busiAndPushRequest);
         return ResponseResult.success(response, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【getCallCountTrend】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/queryCallList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryCallList(HttpServletRequest request, BusiAndPushRequest busiAndPushRequest) {
      try {
         busiAndPushRequest.setDataSource(BaseUtils.getDataSource(request));
         busiAndPushRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         CallListResponse response = this.topicGroupService.queryCallList(busiAndPushRequest);
         return ResponseResult.success(response, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【queryCallList】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }
}
