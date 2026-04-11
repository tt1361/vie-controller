package com.iflytek.vie.standard.model;

import com.iflytek.vie.app.api.model.ModelGroupService;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.ServiceResponse;
import com.iflytek.vie.app.pojo.common.TreeJson;
import com.iflytek.vie.app.pojo.model.ModelGroup;
import com.iflytek.vie.app.pojo.model.ModelGroupRequest;
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

@Controller("modelGroupController")
@RequestMapping({"modelGroup"})
public class ModelGroupController {
   private final Logger logger = LoggerFactory.getLogger(ModelGroupController.class);
   @Autowired
   private ModelGroupService modelGroupService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"queryGroupList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryGroupList(HttpServletRequest request) {
      try {
         ModelGroupRequest modelGroupRequest = new ModelGroupRequest();
         modelGroupRequest.setUserId(BaseUtils.getUserId(request));
         modelGroupRequest.setSystemId(this.commonService.getContextPath());
         modelGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelGroupService.searchAllModelGroupsService(modelGroupRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "查询成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (ViePlatformServiceException var4) {
         this.logger.error("查询所有模型组出错", var4.getMessage());
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"queryGroupTree"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryGroupTree(HttpServletRequest request, ModelGroupRequest modelGroupRequest) throws ViePlatformServiceException {
      try {
         modelGroupRequest.setUserId(BaseUtils.getUserId(request));
         modelGroupRequest.setSystemId(this.commonService.getContextPath());
         modelGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         List<TreeJson> groupTree = this.modelGroupService.searchModelGroupService(modelGroupRequest);
         return ResponseResult.success(groupTree, "查询成功!");
      } catch (ViePlatformServiceException var4) {
         this.logger.error("查询所有模型组树形出错", var4.getMessage());
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"saveModelGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveModelGroup(ModelGroup modelGroup, HttpServletRequest request) throws ViePlatformServiceException {
      try {
         ModelGroupRequest modelGroupRequest = new ModelGroupRequest();
         modelGroupRequest.setModelGroup(modelGroup);
         modelGroupRequest.setUserId(BaseUtils.getUserId(request));
         modelGroupRequest.setSystemId(this.commonService.getContextPath());
         modelGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse;
         if (modelGroup.getGroupId() == -1L) {
            serviceResponse = this.modelGroupService.addModelGroupService(modelGroupRequest);
         } else {
            serviceResponse = this.modelGroupService.updateModelGroupService(modelGroupRequest);
         }

         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "保存成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (ViePlatformServiceException var5) {
         this.logger.error("保存模型组树形出错", var5.getMessage());
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"deleteModelGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteModelGroup(ModelGroupRequest modelGroupRequest, HttpServletRequest request) throws ViePlatformServiceException {
      try {
         modelGroupRequest.setUserId(BaseUtils.getUserId(request));
         modelGroupRequest.setSystemId(this.commonService.getContextPath());
         modelGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelGroupService.deleteModelGroupService(modelGroupRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "删除成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (ViePlatformServiceException var4) {
         this.logger.error("删除模型组树形出错", var4.getMessage());
         return ResponseResult.error(var4.getMessage());
      }
   }
}
