package com.iflytek.vie.standard.call;

import com.alibaba.fastjson.JSON;
import com.iflytek.vie.app.api.call.CallFilterService;
import com.iflytek.vie.app.api.datadrill.DataDrillService;
import com.iflytek.vie.app.pojo.call.CallFilter;
import com.iflytek.vie.app.pojo.call.CallFilterRequest;
import com.iflytek.vie.app.pojo.call.VoiceCallRequest;
import com.iflytek.vie.app.pojo.call.VoiceCallResponse;
import com.iflytek.vie.app.pojo.datadrill.DataDetailRequest;
import com.iflytek.vie.app.pojo.datadrill.DataDetailResponse;
import com.iflytek.vie.base.CommonService;
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

@Controller("callFilterController")
@RequestMapping({"/callFilter"})
public class CallFilterController {
   @Autowired
   private CommonService commonService;
   @Autowired
   private CallFilterService callFilterService;
   @Autowired
   private DataDrillService dataDrillService;
   private static final Logger logger = LoggerFactory.getLogger(CallFilterController.class);

   @RequestMapping(
      value = {"/queryCallList"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryCallList(HttpServletRequest request, String filterParams) {
      ResponseResult response = new ResponseResult();

      try {
         DataDetailRequest dataDetailRequest = (DataDetailRequest)JSON.parseObject(filterParams, DataDetailRequest.class);
         dataDetailRequest.setDataSource(BaseUtils.getDataSource(request));
         dataDetailRequest.setSystemId(this.commonService.getContextPath());
         dataDetailRequest.setUserId(BaseUtils.getUserId(request));
         DataDetailResponse dataDetailResponse = this.dataDrillService.queryVoiceList(dataDetailRequest);
         response.setSuccess(true);
         response.setValue(dataDetailResponse);
         return response;
      } catch (Exception var6) {
         response.setMessage(var6.getMessage());
         logger.info("queryCallList 方法出现异常", var6);
         return response;
      }
   }

   @RequestMapping(
      value = {"/queryDimensions"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryDimensionList(HttpServletRequest request, int listType) {
      ResponseResult response = new ResponseResult();

      try {
         String userId = BaseUtils.getUserId(request);
         CallFilterRequest callFilterRequest = new CallFilterRequest();
         callFilterRequest.setUserId(userId);
         callFilterRequest.setListType(listType);
         callFilterRequest.setDataSource(BaseUtils.getDataSource(request));
         String userDimension = this.callFilterService.getDimensionByUserId(callFilterRequest);
         response.setSuccess(true);
         response.setValue(userDimension);
         return response;
      } catch (Exception var7) {
         response.setMessage(var7.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/queryAllFilters"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryAllFilters(HttpServletRequest request, int listType) {
      ResponseResult response = new ResponseResult();

      try {
         CallFilterRequest callFilterRequest = new CallFilterRequest();
         String userId = BaseUtils.getUserId(request);
         callFilterRequest.setUserId(userId);
         callFilterRequest.setListType(listType);
         callFilterRequest.setDataSource(BaseUtils.getDataSource(request));
         List<CallFilter> filters = this.callFilterService.getFilterListByUserId(callFilterRequest);
         response.setSuccess(true);
         response.setValue(filters);
         return response;
      } catch (Exception var7) {
         response.setMessage(var7.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/querySingleFilter"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult querySingleFilters(HttpServletRequest request, int id) {
      ResponseResult response = new ResponseResult();

      try {
         CallFilterRequest callFilterRequest = new CallFilterRequest();
         callFilterRequest.setId(id);
         callFilterRequest.setDataSource(BaseUtils.getDataSource(request));
         CallFilter filter = this.callFilterService.getSingleFilterById(callFilterRequest);
         response.setSuccess(true);
         response.setValue(filter);
         return response;
      } catch (Exception var6) {
         response.setMessage(var6.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/saveFiltersOrDimension"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveFiltersOrDimension(HttpServletRequest request, CallFilterRequest callFilterRequest) {
      ResponseResult response = new ResponseResult();

      try {
         callFilterRequest.setUserId(BaseUtils.getUserId(request));
         callFilterRequest.setDataSource(BaseUtils.getDataSource(request));
         this.callFilterService.saveFilters(callFilterRequest);
         response.setSuccess(true);
         response.setValue(true);
         return response;
      } catch (Exception var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/deleteSingleFilter"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteSingleFilter(HttpServletRequest request, int id) {
      ResponseResult response = new ResponseResult();

      try {
         CallFilterRequest callFilterRequest = new CallFilterRequest();
         callFilterRequest.setId(id);
         callFilterRequest.setDataSource(BaseUtils.getDataSource(request));
         this.callFilterService.deleteFilters(callFilterRequest);
         response.setSuccess(true);
         response.setValue(true);
         return response;
      } catch (Exception var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/queryVoiceCallList"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryVoiceCallList(HttpServletRequest request, VoiceCallRequest voiceCallRequest) {
      try {
         voiceCallRequest.setDataSource(BaseUtils.getDataSource(request));
         voiceCallRequest.setUserId(BaseUtils.getUserId(request));
         VoiceCallResponse voiceCallResponse = this.callFilterService.queryVoiceCallList(voiceCallRequest);
         return ResponseResult.success(voiceCallResponse, "查询成功");
      } catch (Exception var4) {
         return ResponseResult.error(var4.getMessage());
      }
   }
}
