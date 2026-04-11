package com.iflytek.vie.standard.push;

import com.iflytek.vie.app.api.topicgroup.TopicGroupService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.topicgroup.AllCallReadRequest;
import com.iflytek.vie.app.pojo.topicgroup.BusiAndPushRequest;
import com.iflytek.vie.app.pojo.topicgroup.CallListResponse;
import com.iflytek.vie.app.pojo.topicgroup.CallReadRequest;
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

@Controller("emphasisPushController")
@RequestMapping({"/emphasisPush"})
public class EmphasisPushController {
   private static final Logger logger = LoggerFactory.getLogger(EmphasisPushController.class);
   @Autowired
   private TopicGroupService topicGroupService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/getUnReadCallCount"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getUnReadCallCount(HttpServletRequest request, BusiAndPushRequest busiAndPushRequest) {
      try {
         busiAndPushRequest.setUserId(BaseUtils.getUserId(request));
         busiAndPushRequest.setSystemId(this.commonService.getContextPath());
         busiAndPushRequest.setDataSource(BaseUtils.getDataSource(request));
         busiAndPushRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         int count = this.topicGroupService.getUnReadCallCount(busiAndPushRequest);
         return ResponseResult.success(count, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【getUnReadCallCount】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/queryPushCallList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryPushCallList(HttpServletRequest request, BusiAndPushRequest busiAndPushRequest) {
      try {
         busiAndPushRequest.setUserId(BaseUtils.getUserId(request));
         busiAndPushRequest.setSystemId(this.commonService.getContextPath());
         busiAndPushRequest.setDataSource(BaseUtils.getDataSource(request));
         busiAndPushRequest.setTimeThreshold(Integer.parseInt(LoadConfig.getConfigProperty("topicFocusPushTimeThreshold")));
         busiAndPushRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         CallListResponse response = this.topicGroupService.queryPushCallList(busiAndPushRequest);
         return ResponseResult.success(response, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【queryPushCallList】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/updateCallRead"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateCallRead(CallReadRequest callReadRequest) {
      try {
         boolean flag = this.topicGroupService.updateCallRead(callReadRequest);
         return !flag ? ResponseResult.error("保存失败!") : ResponseResult.success("保存成功!");
      } catch (VieAppServiceException var3) {
         logger.error("【updateCallRead】方法调用出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }

   @RequestMapping(
      value = {"/updateAllCallRead"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateAllCallRead(HttpServletRequest request, AllCallReadRequest allCallReadRequest) {
      try {
         allCallReadRequest.setUserId(BaseUtils.getUserId(request));
         allCallReadRequest.setSystemId(this.commonService.getContextPath());
         allCallReadRequest.setDataSource(BaseUtils.getDataSource(request));
         allCallReadRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         boolean flag = this.topicGroupService.updateAllCallRead(allCallReadRequest);
         return !flag ? ResponseResult.error("保存失败!") : ResponseResult.success("保存成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【updateAllCallRead】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/queryAllGroupDatas"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryAllGroupDatas(HttpServletRequest request, AllCallReadRequest allCallReadRequest) {
      try {
         allCallReadRequest.setUserId(BaseUtils.getUserId(request));
         allCallReadRequest.setSystemId(this.commonService.getContextPath());
         allCallReadRequest.setDataSource(BaseUtils.getDataSource(request));
         allCallReadRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         List<TopicModelDTO> list = this.topicGroupService.queryAllGroupDatas(allCallReadRequest);
         return ResponseResult.success(list, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【queryAllGroupDatas】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }
}
