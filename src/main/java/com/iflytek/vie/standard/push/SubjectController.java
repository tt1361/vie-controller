package com.iflytek.vie.standard.push;

import com.iflytek.vie.app.api.topicgroup.TopicGroupService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.topicgroup.BusiAndPushRequest;
import com.iflytek.vie.app.pojo.topicgroup.TopicGroupRequest;
import com.iflytek.vie.app.pojo.topicgroup.TopicModelDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("subjectController")
@RequestMapping({"/subject"})
public class SubjectController {
   private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);
   @Autowired
   private TopicGroupService topicGroupService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/queryChildrenGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryChildrenGroup(HttpServletRequest request, BusiAndPushRequest busiAndPushRequest) {
      try {
         busiAndPushRequest.setUserId(BaseUtils.getUserId(request));
         busiAndPushRequest.setSystemId(this.commonService.getContextPath());
         busiAndPushRequest.setDataSource(BaseUtils.getDataSource(request));
         busiAndPushRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         List<TopicModelDTO> list = this.topicGroupService.queryChildrenGroup(busiAndPushRequest);
         return ResponseResult.success(list, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【queryChildrenGroup】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/addTopicGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addTopicGroup(TopicGroupRequest topicGroupRequest) {
      try {
         Long groupId = this.topicGroupService.addTopicGroup(topicGroupRequest);
         return ResponseResult.success(groupId, "保存成功!");
      } catch (VieAppServiceException var3) {
         logger.error("【addTopicGroup】方法调用出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }

   @RequestMapping(
      value = {"/updateTopicGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateTopicGroup(TopicGroupRequest topicGroupRequest) {
      try {
         boolean flag = this.topicGroupService.updateTopicGroup(topicGroupRequest);
         return !flag ? ResponseResult.error("保存失败!") : ResponseResult.success("保存成功!");
      } catch (VieAppServiceException var3) {
         logger.error("【updateTopicGroup】方法调用出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }

   @RequestMapping(
      value = {"/deleteTopicGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteTopicGroup(@RequestParam("id") Long id) {
      try {
         boolean flag = this.topicGroupService.deleteTopicGroup(id);
         return !flag ? ResponseResult.error("删除失败!") : ResponseResult.success("删除成功!");
      } catch (VieAppServiceException var3) {
         logger.error("【deleteTopicGroup】方法调用出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }

   @RequestMapping(
      value = {"/addGroupModels"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addGroupModels(TopicGroupRequest topicGroupRequest) {
      try {
         boolean flag = this.topicGroupService.addGroupModels(topicGroupRequest);
         return !flag ? ResponseResult.error("保存失败!") : ResponseResult.success("保存成功!");
      } catch (VieAppServiceException var3) {
         logger.error("【addGroupModels】方法调用出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }

   @RequestMapping(
      value = {"/deleteGroupModel"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteGroupModel(TopicGroupRequest topicGroupRequest) {
      try {
         boolean flag = this.topicGroupService.deleteGroupModel(topicGroupRequest);
         return !flag ? ResponseResult.error("删除失败!") : ResponseResult.success("删除成功!");
      } catch (VieAppServiceException var3) {
         logger.error("【deleteGroupModel】方法调用出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }
}
