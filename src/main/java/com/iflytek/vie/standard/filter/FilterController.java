package com.iflytek.vie.standard.filter;

import com.iflytek.vie.app.api.dimension.DimensionService;
import com.iflytek.vie.app.api.filter.FilterService;
import com.iflytek.vie.app.api.model.ModelService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.common.ServiceResponse;
import com.iflytek.vie.app.pojo.dimension.Constant;
import com.iflytek.vie.app.pojo.dimension.DimensionRequest;
import com.iflytek.vie.app.pojo.dimension.DimensionResponse;
import com.iflytek.vie.app.pojo.filter.FilterRequest;
import com.iflytek.vie.app.pojo.model.ModelRequest;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.constants.DimType;
import com.iflytek.vie.pojo.DimensionDTO;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.CommonValueUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("filterController")
@RequestMapping({"/commonFilter"})
public class FilterController {
   private static final Logger logger = LoggerFactory.getLogger(FilterController.class);
   @Autowired
   private CommonService commonService;
   @Autowired
   private FilterService filterService;
   @Autowired
   private DimensionService dimensionService;
   @Autowired
   private ModelService modelService;

   @RequestMapping(
      value = {"createOrUpdateFilterService"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult createOrUpdateFilterService(FilterRequest filterRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         filterRequest.setUserId(BaseUtils.getUserId(request));
         filterRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse result = this.filterService.createOrUpdateFilterService(filterRequest);
         return ResponseResult.success(result, result.getMessage());
      } catch (VieAppServiceException var5) {
         logger.error("筛选器操作出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"queryFilter"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryFilterService(FilterRequest filterRequest, HttpServletRequest request) {
      ServiceResponse result = null;

      try {
         filterRequest.setUserId(BaseUtils.getUserId(request));
         filterRequest.setDataSource(BaseUtils.getDataSource(request));
         result = this.filterService.queryFilterService(filterRequest);
         return ResponseResult.success(result, "查询成功!");
      } catch (VieAppServiceException var5) {
         logger.error("查询筛选器出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @ResponseBody
   @RequestMapping(
      value = {"queryAllFilter"},
      method = {RequestMethod.POST}
   )
   public ResponseResult queryAllFiltersServer(FilterRequest filterRequest, HttpServletRequest request) {
      ServiceResponse result = null;

      try {
         filterRequest.setUserId(BaseUtils.getUserId(request));
         filterRequest.setDataSource(BaseUtils.getDataSource(request));
         result = this.filterService.queryAllFilterService(filterRequest);
         return ResponseResult.success(result, "查询成功!");
      } catch (VieAppServiceException var5) {
         logger.error("查询筛选器出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @ResponseBody
   @RequestMapping(
      value = {"checkFilterName"},
      method = {RequestMethod.POST}
   )
   public ResponseResult checkFilterNameServer(FilterRequest filterRequest, HttpServletRequest request) {
      ServiceResponse result = null;

      try {
         filterRequest.setUserId(BaseUtils.getUserId(request));
         filterRequest.setDataSource(BaseUtils.getDataSource(request));
         result = this.filterService.checkFilterNameService(filterRequest);
         return ResponseResult.success(result, result.getMessage());
      } catch (VieAppServiceException var5) {
         logger.error("查询筛选器出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @ResponseBody
   @RequestMapping(
      value = {"test"},
      method = {RequestMethod.POST}
   )
   public String testFilter() {
      System.out.println("hello world!");
      return "hello world!";
   }

   @ResponseBody
   @RequestMapping(
      value = {"searchDimAndModel"},
      method = {RequestMethod.POST}
   )
   public ResponseResult searchDimAndModel(HttpServletRequest request) {
      String keyword = "";
      Integer isReport = 2;

      try {
         DimensionRequest dimensionRequest = new DimensionRequest();
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         dimensionRequest.setDimensionName(keyword);
         DimensionResponse result = this.dimensionService.searchPersonalDimensionService(dimensionRequest);
         FilterRequest filterRequest = new FilterRequest();
         filterRequest.setUserId(BaseUtils.getUserId(request));
         filterRequest.setSystemId(this.commonService.getContextPath());
         filterRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse systemResult = this.filterService.queryDimandModelService(filterRequest);
         List<DimensionDTO> list = new ArrayList();
         List<HashMap<String, Object>> systemResultList = (List)systemResult.getValue();
         if (systemResultList != null && systemResultList.size() > 0) {
            Iterator var10 = systemResultList.iterator();

            while(var10.hasNext()) {
               HashMap<String, Object> map = (HashMap)var10.next();
               DimensionDTO dimensionDTO = new DimensionDTO();
               dimensionDTO.setDataType(String.valueOf(map.get("dataType")));
               dimensionDTO.setKey(String.valueOf(map.get("key")));
               dimensionDTO.setName(String.valueOf(map.get("name")));
               dimensionDTO.setType(String.valueOf(map.get("type")));
               dimensionDTO.setValue((List)map.get("valueList"));
               dimensionDTO.setDurationFlag(Integer.parseInt(String.valueOf(map.get("flag"))));
               dimensionDTO.setAnalysis(Integer.parseInt(String.valueOf(map.get("analysis"))));
               list.add(dimensionDTO);
            }
         }

         List<HashMap<String, Object>> resultList = (List)result.getValue();
         DimensionDTO model;
         if (resultList != null && resultList.size() > 0) {
            Iterator var19 = resultList.iterator();

            while(var19.hasNext()) {
               HashMap<String, Object> map = (HashMap)var19.next();
               model = new DimensionDTO();
               model.setDataType(String.valueOf(map.get("dimensionDataType")));
               model.setKey(String.valueOf(map.get("dimensionAnotherName")));
               model.setName(String.valueOf(map.get("dimensionName")));
               model.setType(String.valueOf(map.get("dimensionType")));
               model.setValue((List)map.get("dimensionValues"));
               model.setAnalysis(Integer.parseInt(String.valueOf(map.get("analysis"))));
               list.add(model);
            }
         }

         if (isReport != null) {
            Map.Entry entry;
            HashMap specDimValue;
            DimensionDTO reportModel;
            List specDimValueList;
            Iterator var25;
            if (isReport == 1) {
               reportModel = new DimensionDTO();
               reportModel.setType(DimType.timeDim.getName());
               reportModel.setDataType("string");
               reportModel.setKey("timeDim");
               reportModel.setName("时间");
               specDimValueList = new ArrayList();
               var25 = CommonValueUtils.dimTime.entrySet().iterator();

               while(var25.hasNext()) {
                  entry = (Map.Entry)var25.next();
                  specDimValue = new HashMap();
                  specDimValue.put("key", entry.getKey());
                  specDimValue.put("value", entry.getValue());
                  specDimValueList.add(specDimValue);
               }

               reportModel.setValue(specDimValueList);
               list.add(reportModel);
               reportModel = new DimensionDTO();
               reportModel.setType(DimType.offLineTagId.getName());
               reportModel.setKey(DimType.offLineTagId.getName());
               reportModel.setName("模型");
               reportModel.setDataType("string");
               specDimValueList = this.getModelInfo(request);
               reportModel.setValue(specDimValueList);
               list.add(reportModel);
               DimensionDTO kwd = new DimensionDTO();
               kwd.setType(DimType.mulEqu.getName());
               kwd.setKey(CommonValueUtils.keyword);
               kwd.setName("关键词");
               kwd.setDataType("string");
               list.add(kwd);
            } else if (isReport == 2 || isReport == 3) {
               reportModel = new DimensionDTO();
               specDimValueList = new ArrayList();
               var25 = Constant.dimTime.entrySet().iterator();

               while(var25.hasNext()) {
                  entry = (Map.Entry)var25.next();
                  specDimValue = new HashMap();
                  specDimValue.put("key", entry.getKey());
                  specDimValue.put("value", entry.getValue());
                  specDimValueList.add(specDimValue);
               }

               reportModel.setType(DimType.offLineTagId.getName());
               reportModel.setKey(DimType.offLineTagId.getName());
               reportModel.setName("模型");
               reportModel.setDataType("string");
               specDimValueList = this.getModelInfo(request);
               reportModel.setValue(specDimValueList);
               list.add(reportModel);
            }

            int flag = -1;
            if (isReport == 2 || isReport == 0) {
               for(int index = 0; index < list.size(); ++index) {
                  if ("timeFormat".equals(((DimensionDTO)list.get(index)).getKey())) {
                     flag = index;
                     break;
                  }
               }

               if (flag != -1) {
                  list.remove(flag);
               }
            }
         }

         return ResponseResult.success(list, "查询成功!");
      } catch (VieAppServiceException var16) {
         logger.error("filter:查询所有维度信息列表出错", var16);
         return ResponseResult.error(var16.getMessage());
      } catch (ViePlatformServiceException var17) {
         logger.error("filter:查询所有维度信息列表出错", var17);
         return ResponseResult.error(var17.getMessage());
      }
   }

   public List<Object> getModelInfo(HttpServletRequest request) {
      ArrayList<Object> modelDTOList = new ArrayList();

      try {
         ModelRequest modelRequest = new ModelRequest();
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         modelRequest.setModelGroupId(-1L);
         modelRequest.setType("online");
         Map<String, Object> map = this.modelService.searModelByGroupService(modelRequest);
         if (!map.containsKey("rows")) {
            return modelDTOList;
         }

         List<HashMap<String, Object>> mapList = (List)map.get("rows");
         if (mapList == null) {
            return modelDTOList;
         }

         HashMap<Long, HashMap<String, List<HashMap<String, Object>>>> hashMap = new HashMap();
         Iterator var7 = mapList.iterator();

         while(var7.hasNext()) {
            HashMap<String, Object> modelInfo = (HashMap)var7.next();
            long modelGroupId = Long.valueOf(String.valueOf(modelInfo.get("modelGroupId")));
            if (hashMap.containsKey(modelGroupId)) {
               HashMap<String, List<HashMap<String, Object>>> storedGroupHash = (HashMap)hashMap.get(modelGroupId);
               List<HashMap<String, Object>> storedList = (List)storedGroupHash.get(modelInfo.get("modelGroupName"));
               storedList.add(modelInfo);
            } else {
               List<HashMap<String, Object>> unStoredList = new ArrayList();
               unStoredList.add(modelInfo);
               HashMap<String, List<HashMap<String, Object>>> groupHash = new HashMap();
               groupHash.put(String.valueOf(modelInfo.get("modelGroupName")), unStoredList);
               hashMap.put(modelGroupId, groupHash);
            }
         }

         var7 = hashMap.entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry<Long, HashMap<String, List<HashMap<String, Object>>>> entry = (Map.Entry)var7.next();
            HashMap<String, Object> modelMap = new HashMap();
            modelMap.put("modelGroupId", entry.getKey());
            HashMap<String, List<HashMap<String, Object>>> tmp = (HashMap)entry.getValue();
            Iterator var21 = tmp.entrySet().iterator();

            while(var21.hasNext()) {
               Map.Entry<String, List<HashMap<String, Object>>> entryTmp = (Map.Entry)var21.next();
               modelMap.put("modelGroup", entryTmp.getKey());
               List<Object> specDimValueList = new ArrayList();
               Iterator var14 = ((List)entryTmp.getValue()).iterator();

               while(var14.hasNext()) {
                  HashMap<String, Object> m = (HashMap)var14.next();
                  HashMap<String, Object> specDimValue = new HashMap();
                  specDimValue.put("key", m.get("modelId"));
                  specDimValue.put("value", m.get("modelName"));
                  specDimValueList.add(specDimValue);
               }

               modelMap.put("value", specDimValueList);
            }

            if (((List)modelMap.get("value")).size() > 0) {
               modelDTOList.add(modelMap);
            }
         }
      } catch (Exception var17) {
         logger.error("filter:查询模型维度信息出错", var17);
      }

      return modelDTOList;
   }

   public void setCommonService(CommonService commonService) {
      this.commonService = commonService;
   }

   public void setFilterService(FilterService filterService) {
      this.filterService = filterService;
   }

   public void setDimensionService(DimensionService dimensionService) {
      this.dimensionService = dimensionService;
   }

   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }
}
