package com.iflytek.vie.standard.topic;

import com.iflytek.vie.app.api.dimension.HotWordService;
import com.iflytek.vie.app.api.topic.TopicHotWordTaskService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.topic.HotWordRankResponse;
import com.iflytek.vie.app.pojo.topic.HotWordTaskRequest;
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

@Controller("hotWordController")
@RequestMapping({"/hotWord"})
public class HotWordController {
   private static final Logger logger = LoggerFactory.getLogger(HotWordController.class);
   @Autowired
   private HotWordService hotWordService;
   @Autowired
   private TopicHotWordTaskService topicHotWordTaskService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/addHotWordTask"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addHotWordTask(HttpServletRequest request, HotWordTaskRequest hotWordTaskRequest) {
      try {
         hotWordTaskRequest.setDataSource(BaseUtils.getDataSource(request));
         hotWordTaskRequest.setUserId(BaseUtils.getUserId(request));
         hotWordTaskRequest.setSystemId(this.commonService.getContextPath());
         long taskId = this.hotWordService.addHotWordTask(hotWordTaskRequest);
         return ResponseResult.success(taskId, "保存成功!");
      } catch (ViePlatformServiceException var5) {
         logger.error("【addHotWordTask】方法调用错误", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getHotWordTaskStatus"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getHotWordTaskStatus(HttpServletRequest request, HotWordTaskRequest hotWordTaskRequest) {
      try {
         hotWordTaskRequest.setDataSource(BaseUtils.getDataSource(request));
         hotWordTaskRequest.setUserId(BaseUtils.getUserId(request));
         hotWordTaskRequest.setSystemId(this.commonService.getContextPath());
         int status = this.hotWordService.getHotWordTaskStatus(hotWordTaskRequest);
         return ResponseResult.success(status, "查询成功!");
      } catch (ViePlatformServiceException var4) {
         logger.error("【getHotWordTaskStatus】方法调用错误", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getHotWordLastFlushTime"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getHotWordLastFlushTime(HttpServletRequest request, HotWordTaskRequest hotWordTaskRequest) {
      try {
         hotWordTaskRequest.setDataSource(BaseUtils.getDataSource(request));
         hotWordTaskRequest.setUserId(BaseUtils.getUserId(request));
         hotWordTaskRequest.setSystemId(this.commonService.getContextPath());
         String time = this.hotWordService.getHotWordLastFlushTime(hotWordTaskRequest);
         return ResponseResult.success(time, "查询成功!");
      } catch (ViePlatformServiceException var4) {
         logger.error("【getHotWordLastFlushTime】方法调用错误", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/queryHotWordStat"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryHotWordStat(HttpServletRequest request, HotWordTaskRequest hotWordTaskRequest) {
      try {
         hotWordTaskRequest.setDataSource(BaseUtils.getDataSource(request));
         hotWordTaskRequest.setUserId(BaseUtils.getUserId(request));
         hotWordTaskRequest.setSystemId(this.commonService.getContextPath());
         hotWordTaskRequest.setIfExport(false);
         List<HotWordRankResponse> response = this.topicHotWordTaskService.queryHotWordStat(hotWordTaskRequest);
         return ResponseResult.success(response, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【queryHotWordStat】方法调用错误", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }
}
