package com.iflytek.vie.standard.topic;

import com.iflytek.vie.app.api.topic.TopicService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.topic.DimensionRequest;
import com.iflytek.vie.app.pojo.topic.PathDimension;
import com.iflytek.vie.app.pojo.topic.PathInfoRequest;
import com.iflytek.vie.app.pojo.topic.TopicPathRequest;
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

@Controller("pathController")
@RequestMapping({"/path"})
public class PathController {
   @Autowired
   private TopicService topicService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/searchPathDim"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult searchPathDim(TopicPathRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         List<PathDimension> result = this.topicService.searchPathDim(params);
         response.setValue(result);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/addPathDim"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addPathDim(DimensionRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         int result = this.topicService.addPathDim(params);
         response.setValue(result);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/delPathDim"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deletePathDim(DimensionRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         this.topicService.deletePathDim(params);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/editBatchPath"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult editBatchPath(PathInfoRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         this.topicService.editBatchPath(params);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }
}
