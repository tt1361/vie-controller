package com.iflytek.vie.standard.topic;

import com.iflytek.vie.app.api.topic.TopicService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.topic.ClusterInfo;
import com.iflytek.vie.app.pojo.topic.CreateClusterRequest;
import com.iflytek.vie.app.pojo.topic.GetClusterStatusRequest;
import com.iflytek.vie.app.pojo.topic.HotViewRequest;
import com.iflytek.vie.app.pojo.topic.MarkQueryResponse;
import com.iflytek.vie.app.pojo.topic.TogetherPathRequest;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("clusterController")
@RequestMapping({"/cluster"})
public class ClusterController {
   @Autowired
   private TopicService topicService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/getClusterStatus"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getClusterStatus(GetClusterStatusRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setSystemId(this.commonService.getContextPath());
         params.setUserId(BaseUtils.getUserId(request));
         int result = this.topicService.getClusterStatus(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (Exception var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getClusterInfoFromPia"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getClusterInfoFromPia(GetClusterStatusRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setSystemId(this.commonService.getContextPath());
         params.setUserId(BaseUtils.getUserId(request));
         List<ClusterInfo> result = this.topicService.getClusterInfoFromPia(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getClusterInfoFromPath"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getClusterInfoFromPath(GetClusterStatusRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setSystemId(this.commonService.getContextPath());
         params.setUserId(BaseUtils.getUserId(request));
         List<ClusterInfo> result = this.topicService.getClusterInfoFromPath(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getHotviewById"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getHotviewById(HotViewRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         String result = this.topicService.getHotviewById(params);
         response.setSuccess(true);
         response.setValue("{" + result + "}");
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/createCluster"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult createCluster(CreateClusterRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setSystemId(this.commonService.getContextPath());
         params.setUserId(BaseUtils.getUserId(request));
         int result = this.topicService.createCluster(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/flushClusterTime"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult flushClusterTime(GetClusterStatusRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setSystemId(this.commonService.getContextPath());
         params.setUserId(BaseUtils.getUserId(request));
         String result = this.topicService.flushClusterTime(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getTogatherData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTogatherData(TogetherPathRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setAccountName(BaseUtils.getUserName(request));
         params.setUserId(BaseUtils.getUserId(request));
         params.setSystemId(this.commonService.getContextPath());
         params.setExport(false);
         MarkQueryResponse result = this.topicService.getTogatherData(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }
}
