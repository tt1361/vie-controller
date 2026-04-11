package com.iflytek.vie.standard.dimension;

import com.alibaba.fastjson.JSON;
import com.iflytek.vie.app.api.dimension.DimensionService;
import com.iflytek.vie.app.api.model.ModelService;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.auth.AuthorizeInfo;
import com.iflytek.vie.app.pojo.dimension.AllDimensionRequest;
import com.iflytek.vie.app.pojo.dimension.Constant;
import com.iflytek.vie.app.pojo.dimension.DimensionPersonalValue;
import com.iflytek.vie.app.pojo.dimension.DimensionRequest;
import com.iflytek.vie.app.pojo.dimension.DimensionResponse;
import com.iflytek.vie.app.pojo.model.ModelRequest;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.constants.DimType;
import com.iflytek.vie.pojo.AddDimensionRequest;
import com.iflytek.vie.pojo.DimensionDTO;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.pojo.SaveDimensionRequest;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.CommonValueUtils;
import com.iflytek.vie.utils.StringUtils;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Controller("dimensionController")
@RequestMapping({"dimension"})
public class DimensionController {
   private static final Logger logger = LoggerFactory.getLogger(DimensionController.class);
   @Autowired
   private DimensionService dimensionService;
   @Autowired
   private CommonService commonService;
   @Autowired
   private ModelService modelService;

   @RequestMapping(
      value = {"/searchAllDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult searchAllDimension(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.searchPersonalDimensionService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (ViePlatformServiceException var5) {
         logger.error("查询自定义维度信息列表出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/searchDimensionById"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult searchDimensionById(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.searchPersonalDimensionByIdService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (ViePlatformServiceException var5) {
         logger.error("查询自定义维度信息列表出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/updatePersonalDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updatePersonalDimension(AddDimensionRequest dimension, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         DimensionRequest dimensionRequest = new DimensionRequest();
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         BeanUtils.copyProperties(dimensionRequest, dimension);
         List<DimensionPersonalValue> dimensionPersonalValues = JSON.parseArray(dimension.getDimensionValues(), DimensionPersonalValue.class);
         dimensionRequest.setDimensionPersonalValues(dimensionPersonalValues);
         DimensionResponse result = this.dimensionService.updatePersonalDimensionService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (Exception var7) {
         logger.error("更新自定义维度信息列表出错", var7);
         response.setMessage(var7.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/deletePersonalDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deletePersonalDimension(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.deletePersonalDimensionService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (ViePlatformServiceException var5) {
         logger.error("删除自定义维度信息出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/addPersonalDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addPersonalDimension(AddDimensionRequest dimension, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         DimensionRequest dimensionRequest = new DimensionRequest();
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         BeanUtils.copyProperties(dimensionRequest, dimension);
         List<DimensionPersonalValue> dimensionPersonalValues = JSON.parseArray(dimension.getDimensionValues(), DimensionPersonalValue.class);
         dimensionRequest.setDimensionPersonalValues(dimensionPersonalValues);
         DimensionResponse result = this.dimensionService.addPersonalDimensionService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (Exception var7) {
         logger.error("添加自定义维度信息出错", var7);
         response.setMessage(var7.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/updateSystemDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateSystemDimension(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            logger.error("数据源为空");
            response.setMessage("数据源为空");
            return response;
         } else {
            dimensionRequest.setDataSource(dataSource);
            DimensionResponse result = this.dimensionService.updateSystemDimensionService(dimensionRequest);
            if (result.isSuccessful()) {
               response.setSuccess(true);
               response.setValue(result.getValue());
               return response;
            } else {
               logger.error(result.getMessage());
               response.setMessage(result.getMessage());
               return response;
            }
         }
      } catch (ViePlatformServiceException var6) {
         logger.error("更新系统维度信息出错", var6);
         response.setMessage(var6.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/searchDim"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult searchDim(@RequestParam(value = "keyword",required = false) String keyword, @RequestParam(value = "isReport",required = false) Integer isReport, HttpServletRequest request) {
      try {
         DimensionRequest dimensionRequest = new DimensionRequest();
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         dimensionRequest.setDimensionName(keyword);
         DimensionResponse result = this.dimensionService.searchPersonalDimensionService(dimensionRequest);
         AllDimensionRequest allDimensionRequest = new AllDimensionRequest();
         AuthorizeInfo authorizeInfo = new AuthorizeInfo();
         authorizeInfo.setUserId(BaseUtils.getUserId(request));
         authorizeInfo.setSystemId(this.commonService.getContextPath());
         allDimensionRequest.setAuthorizeInfo(authorizeInfo);
         allDimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         allDimensionRequest.setKeyWord(keyword);
         DimensionResponse systemResult = this.dimensionService.getTopicDimensionService(allDimensionRequest);
         List<DimensionDTO> list = new ArrayList();
         List<HashMap<String, Object>> systemResultList = (List)systemResult.getValue();
         if (systemResultList != null && systemResultList.size() > 0) {
            Iterator var11 = systemResultList.iterator();

            while(var11.hasNext()) {
               HashMap<String, Object> map = (HashMap)var11.next();
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
               new ArrayList();
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
               new ArrayList();
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
      } catch (ViePlatformServiceException var17) {
         logger.error("查询所有维度信息列表出错", var17);
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
         logger.error("查询模型维度信息出错", var17);
      }

      return modelDTOList;
   }

   @RequestMapping(
      value = {"/getSystemDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getSystemDimension(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            logger.error("数据源为空");
            response.setMessage("数据源为空");
            return response;
         } else {
            dimensionRequest.setDataSource(dataSource);
            DimensionResponse result = this.dimensionService.searchAllDimensionsService(dimensionRequest);
            if (result.isSuccessful()) {
               response.setSuccess(true);
               response.setValue(result.getValue());
               return response;
            } else {
               logger.error(result.getMessage());
               response.setMessage(result.getMessage());
               return response;
            }
         }
      } catch (ViePlatformServiceException var6) {
         logger.error("获取系统维度信息出错", var6);
         response.setMessage(var6.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/importPersonalDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult importPersonalDimension(@RequestParam("uploadify") CommonsMultipartFile files, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         DimensionRequest dimensionRequest = new DimensionRequest();
         CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
         if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            MultipartFile file = multiRequest.getFile("uploadify");
            if (file == null) {
               logger.error("上传文件为空");
               response.setMessage("上传文件为空");
               return response;
            }

            String path = request.getSession().getServletContext().getRealPath("upload");
            String myFileName = file.getOriginalFilename();
            File localFile = new File(path, myFileName);
            if (!localFile.exists()) {
               localFile.mkdirs();
            }

            file.transferTo(localFile);
            dimensionRequest.setImportFile(localFile);
            dimensionRequest.setUserId(BaseUtils.getUserId(request));
            dimensionRequest.setSystemId(this.commonService.getContextPath());
            dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
            DimensionResponse result = this.dimensionService.importPersonalDimensionService(dimensionRequest);
            if (!result.isSuccessful()) {
               logger.error(result.getMessage());
               response.setMessage(result.getMessage());
               localFile.delete();
               return response;
            }

            response.setSuccess(true);
            response.setValue(result.getValue());
            localFile.delete();
         }

         return response;
      } catch (Exception var12) {
         logger.error("导入自定义维度信息出错", var12);
         response.setMessage(var12.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getDimensionValueMessage"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getDimensionValueMessage(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.searchDimensionMessageService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (ViePlatformServiceException var5) {
         logger.error("导入自定义维度信息出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/searchDimensionTask"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult searchDimensionTask(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.searchDimensionTaskService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (ViePlatformServiceException var5) {
         logger.error("导入自定义维度信息出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/exportDimensionExcel"},
      method = {RequestMethod.POST}
   )
   public void exportDimensionExcel(HttpServletResponse response) {
      try {
         String excelName = "维度导入.xlsx";
         String[] headers = new String[]{"序号", "维度条件（已知维度筛选条件）", "自定义维度名称", "值（string、int）"};
         String[] rowValue = new String[]{"1", "duration>10 & area=合肥,淮北 & timestamp>2016-05-04", "技能等级", "1.1"};
         response.reset();
         response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelName, "UTF-8"));
         response.setContentType("application/vnd.ms-excel");
         XSSFWorkbook workbook = new XSSFWorkbook();
         XSSFSheet sheet = workbook.createSheet();
         sheet.setDefaultColumnWidth(20);
         XSSFRow row = sheet.createRow(0);

         int j;
         XSSFCell cell;
         for(j = 0; j < headers.length; ++j) {
            cell = row.createCell(j);
            cell.setCellValue(headers[j]);
         }

         row = sheet.createRow(1);

         for(j = 0; j < rowValue.length; ++j) {
            cell = row.createCell(j);
            cell.setCellType(1);
            cell.setCellValue(rowValue[j]);
         }

         ServletOutputStream ouputStream = response.getOutputStream();
         workbook.write(ouputStream);
         ouputStream.flush();
         ouputStream.close();
      } catch (Exception var10) {
         logger.error("导出维度异常", var10);
      }

   }

   @RequestMapping(
      value = {"/getMulselDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getMulselDimension(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.searchMulDimensionService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (ViePlatformServiceException var5) {
         logger.error("查询枚举型自定义维度出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getTelephonDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTelephonDimensio(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.searchTelephoneDimensionService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (ViePlatformServiceException var5) {
         logger.error("查询录音维度信息出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/saveSelectDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveSelectDimension(SaveDimensionRequest dimension, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         List<Long> dimensionIds = new ArrayList();
         String dimensionIdList = dimension.getDimensionIdList() == null ? "" : dimension.getDimensionIdList();
         if (!"".equals(dimensionIdList)) {
            String[] dimensionArray = dimension.getDimensionIdList().split(",");
            String[] var7 = dimensionArray;
            int var8 = dimensionArray.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               String str = var7[var9];
               dimensionIds.add(Long.valueOf(str));
            }
         }

         DimensionRequest dimensionRequest = new DimensionRequest();
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         dimensionRequest.setDimensionIds(dimensionIds);
         BeanUtils.copyProperties(dimensionRequest, dimension);
         DimensionResponse result = this.dimensionService.saveSelectDimensionService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (Exception var11) {
         logger.error("保存选择的维度出错", var11);
         response.setMessage(var11.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/addSelectDimensionToEs"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addDimensionToEs(DimensionRequest dimensionRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.addDimensionToEsService(dimensionRequest);
         if (result.isSuccessful()) {
            response.setSuccess(true);
            response.setValue(result.getValue());
            return response;
         } else {
            logger.error(result.getMessage());
            response.setMessage(result.getMessage());
            return response;
         }
      } catch (ViePlatformServiceException var5) {
         logger.error("查询录音维度信息出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }
}
