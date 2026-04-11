package com.iflytek.vie.standard.topic;

import com.iflytek.vie.app.api.topic.FunnelService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.topic.FunnelCallRequest;
import com.iflytek.vie.app.pojo.topic.FunnelCallResponse;
import com.iflytek.vie.app.pojo.topic.FunnelChartResponse;
import com.iflytek.vie.app.pojo.topic.FunnelTableResponse;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("funnelController")
@RequestMapping({"/funnel"})
public class FunnelController {
   private static final Logger logger = LoggerFactory.getLogger(FunnelController.class);
   @Autowired
   private FunnelService funnelService;
   @Autowired
   private CommonService CommonService;

   @RequestMapping(
      value = {"/getTotalRate"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTotalRate(HttpServletRequest request, FunnelCallRequest funnelCallRequest) {
      try {
         funnelCallRequest.setUserId(BaseUtils.getUserId(request));
         funnelCallRequest.setSystemId(this.CommonService.getContextPath());
         funnelCallRequest.setDataSource(BaseUtils.getDataSource(request));
         String totalRate = this.funnelService.getTotalRate(funnelCallRequest);
         return ResponseResult.success(totalRate, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【getTotalRate】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getFunnelChart"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getFunnelChart(HttpServletRequest request, FunnelCallRequest funnelCallRequest) {
      try {
         funnelCallRequest.setUserId(BaseUtils.getUserId(request));
         funnelCallRequest.setSystemId(this.CommonService.getContextPath());
         funnelCallRequest.setDataSource(BaseUtils.getDataSource(request));
         FunnelChartResponse Response = this.funnelService.getFunnelChart(funnelCallRequest);
         return ResponseResult.success(Response, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【getFunnelChart】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getFunnelTable"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getFunnelTable(HttpServletRequest request, FunnelCallRequest funnelCallRequest) {
      try {
         funnelCallRequest.setUserId(BaseUtils.getUserId(request));
         funnelCallRequest.setSystemId(this.CommonService.getContextPath());
         funnelCallRequest.setDataSource(BaseUtils.getDataSource(request));
         FunnelTableResponse Response = this.funnelService.getFunnelTable(funnelCallRequest);
         return ResponseResult.success(Response, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【getFunnelTable】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getFunnelList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getFunnelList(HttpServletRequest request, FunnelCallRequest funnelCallRequest) {
      try {
         funnelCallRequest.setUserId(BaseUtils.getUserId(request));
         funnelCallRequest.setSystemId(this.CommonService.getContextPath());
         funnelCallRequest.setDataSource(BaseUtils.getDataSource(request));
         funnelCallRequest.setRoleId(this.CommonService.getRoleIds(BaseUtils.getUserName(request), BaseUtils.getDataSource(request)));
         FunnelCallResponse Response = this.funnelService.getFunnelList(funnelCallRequest);
         return ResponseResult.success(Response, "查询成功!");
      } catch (Exception var4) {
         logger.error("【getFunnelList】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }
}
