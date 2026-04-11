package com.iflytek.vie.standard.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.vie.app.api.datadrill.DataDrillService;
import com.iflytek.vie.app.api.dimension.DimensionService;
import com.iflytek.vie.app.api.model.ModelAccuracyService;
import com.iflytek.vie.app.api.model.ModelApplyService;
import com.iflytek.vie.app.api.model.ModelFragmentService;
import com.iflytek.vie.app.api.model.ModelGroupService;
import com.iflytek.vie.app.api.model.ModelService;
import com.iflytek.vie.app.api.report.ReportChartViewService;
import com.iflytek.vie.app.api.utils.DateUtils;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.ServiceResponse;
import com.iflytek.vie.app.pojo.auth.AuthorizeInfo;
import com.iflytek.vie.app.pojo.common.ColumnInfo;
import com.iflytek.vie.app.pojo.common.ColumnMap;
import com.iflytek.vie.app.pojo.common.PagerResponse;
import com.iflytek.vie.app.pojo.datadrill.AnalyseField;
import com.iflytek.vie.app.pojo.datadrill.DataDetailRequest;
import com.iflytek.vie.app.pojo.datadrill.DataDetailResponse;
import com.iflytek.vie.app.pojo.datadrill.DataDrillRequest;
import com.iflytek.vie.app.pojo.datadrill.DrillFilter;
import com.iflytek.vie.app.pojo.datadrill.FilterField;
import com.iflytek.vie.app.pojo.datadrill.FilterRuleEnum;
import com.iflytek.vie.app.pojo.dimension.AllDimensionRequest;
import com.iflytek.vie.app.pojo.dimension.DimensionConfig;
import com.iflytek.vie.app.pojo.dimension.DimensionRequest;
import com.iflytek.vie.app.pojo.dimension.DimensionResponse;
import com.iflytek.vie.app.pojo.model.ModelColumnDataRequest;
import com.iflytek.vie.app.pojo.model.ModelDetailDTO;
import com.iflytek.vie.app.pojo.model.ModelFragment;
import com.iflytek.vie.app.pojo.model.ModelGroup;
import com.iflytek.vie.app.pojo.model.ModelGroupRequest;
import com.iflytek.vie.app.pojo.model.ModelInfo;
import com.iflytek.vie.app.pojo.model.ModelRequest;
import com.iflytek.vie.app.pojo.model.ModelResponse;
import com.iflytek.vie.app.pojo.model.ModelTableDataRequest;
import com.iflytek.vie.app.pojo.model.ModelTagRequest;
import com.iflytek.vie.app.pojo.model.ModelUpRequest;
import com.iflytek.vie.app.pojo.model.ResultTable;
import com.iflytek.vie.app.pojo.model.VoiceCommentRequest;
import com.iflytek.vie.app.pojo.model.VoiceMarkRequest;
import com.iflytek.vie.app.pojo.topicgroup.DataInfo;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.config.LoadConfig;
import com.iflytek.vie.constants.IndexConstants;
import com.iflytek.vie.pojo.MarkDataRequest;
import com.iflytek.vie.pojo.ModelRule;
import com.iflytek.vie.pojo.PropertyText;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.pojo.SilenceText;
import com.iflytek.vie.pojo.TagType;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.DesensitizationUtils;
import com.iflytek.vie.utils.JSONUtils;
import com.iflytek.vie.utils.StringUtils;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller("modelController")
@RequestMapping({"model"})
public class ModelController {
   private final Logger logger = LoggerFactory.getLogger(ModelController.class);
   @Autowired
   private CommonService commonService;
   @Autowired
   private ModelService modelService;
   @Autowired
   private ModelApplyService modelApplyService;
   @Autowired
   private ModelFragmentService modelFragmentService;
   @Autowired
   private ModelAccuracyService modelAccuracyService;
   @Autowired
   private ModelGroupService modelGroupService;
   @Autowired
   private DataDrillService dataDrillService;
   @Autowired
   private DimensionService dimensionService;
   @Autowired
   private ReportChartViewService reportChartViewService;
   protected ObjectMapper mapper = new ObjectMapper();
   private DecimalFormat df = new DecimalFormat("0.00");

   @RequestMapping({"queryModels"})
   @ResponseBody
   public ResponseResult queryModels(HttpServletRequest request, ModelRequest modelRequest) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         Map<String, Object> result = this.modelService.searModelByGroupService(modelRequest);
         return ResponseResult.success(result, "查询成功!");
      } catch (ViePlatformServiceException var4) {
         this.logger.error("查询所有模型信息列表出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getModelStatus"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getModelStatus(HttpServletRequest request, ModelRequest modelRequest) {
      modelRequest.setDataSource(BaseUtils.getDataSource(request));

      try {
         HashMap<String, Object> result = this.modelService.getModelStatusService(modelRequest);
         return ResponseResult.success(result, "查询成功!");
      } catch (ViePlatformServiceException var4) {
         this.logger.error("获取模型状态出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getModelOnlineProgress"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public Object getModelOnlineProgress(HttpServletRequest request, ModelRequest modelRequest) {
      ServiceResponse serviceResponse = new ServiceResponse();

      try {
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         serviceResponse = this.modelService.getModelOnlineProgress(modelRequest);
         Object jsonObject = JSON.toJSON(serviceResponse);
         return jsonObject;
      } catch (ViePlatformServiceException var7) {
         this.logger.error("获取模型状态出错", var7);
         serviceResponse.setMessage(var7.getMessage());
         Object jsonObject = JSON.toJSON(serviceResponse);
         return jsonObject;
      }
   }

   @RequestMapping(
      value = {"getPreviewId"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getPreviewId() {
      try {
         return ResponseResult.success(DateUtils.getCurrentTime(), "查询成功!");
      } catch (Exception var2) {
         this.logger.error("获取页面的Id出错", var2);
         return ResponseResult.error(var2.getMessage());
      }
   }

   @RequestMapping(
      value = {"checkModelName"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult checkModelName(HttpServletRequest request, ModelRequest modelRequest) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.searchModelCountByName(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "查询成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("查询模型名称是否重名出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getModelById"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getModelById(HttpServletRequest request, ModelRequest modelRequest) {
      modelRequest.setDataSource(BaseUtils.getDataSource(request));

      try {
         ServiceResponse serviceResponse = this.modelService.searchModelInfoService(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "查询成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("查询模型信息出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getTagDimension"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTagDimension(HttpServletRequest request, @RequestParam("searchType") Integer searchType) {
      ModelRequest modelRequest = new ModelRequest();
      modelRequest.setSearchType(searchType);
      modelRequest.setDataSource(BaseUtils.getDataSource(request));

      try {
         ServiceResponse serviceResponse = this.modelService.searchTagDimension(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "查询成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var5) {
         this.logger.error("查询静音标签出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"isHaveSilence"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult isHaveSilence() {
      try {
         int isHaveSilence = 0;
         int searchType = Integer.parseInt(LoadConfig.getConfigProperty("searchType"));
         if (searchType == 1) {
            isHaveSilence = 1;
         }

         return ResponseResult.success(Integer.valueOf(isHaveSilence), "查询成功!");
      } catch (Exception var3) {
         this.logger.error("判断是否是静音出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }

   @RequestMapping(
      value = {"isOrderModel"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult isOrderModel(HttpServletRequest request, ModelRequest modelRequest) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.isOrderModel(modelRequest);
         String isOrderModel = String.valueOf(serviceResponse.getValue());
         return ResponseResult.success(isOrderModel, "查询成功!");
      } catch (Exception var5) {
         this.logger.error("判断是否是顺序模型出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"onlineModel"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult onlineModel(ModelRequest modelRequest, HttpServletRequest request) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.onlineModelService(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), serviceResponse.getMessage()) : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("模型上线出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"offlineModel"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult offlineModel(ModelRequest modelRequest, HttpServletRequest request) {
      modelRequest.setDataSource(BaseUtils.getDataSource(request));

      try {
         ServiceResponse serviceResponse = this.modelService.offlineModelService(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "操作成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("模型上线出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"setModelUp"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult setModelUp(ModelUpRequest modelUpRequest, HttpServletRequest request) {
      modelUpRequest.setDataSource(BaseUtils.getDataSource(request));

      try {
         boolean result = this.modelApplyService.setModelUp(modelUpRequest);
         return result ? ResponseResult.success("操作成功!") : ResponseResult.error("操作失败!");
      } catch (Exception var4) {
         this.logger.error("模型上线出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getModelByGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getModelByGroup(HttpServletRequest request, ModelRequest modelRequest) {
      modelRequest.setDataSource(BaseUtils.getDataSource(request));

      try {
         Map<String, Object> map = this.modelService.searModelByGroupService(modelRequest);
         return ResponseResult.success(map, "操作成功!");
      } catch (Exception var4) {
         this.logger.error("查询所有模型出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"queryOnLineModelsByGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryOnLineModelsByGroup(HttpServletRequest request, ModelRequest modelRequest) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ModelResponse modelResponse = this.modelAccuracyService.getOnLineModelsService(modelRequest);
         return modelResponse.isSuccess() ? ResponseResult.success(modelResponse.getValue(), modelResponse.getMessage()) : ResponseResult.error(modelResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("获取专题中所有上线模型出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"queryOnlineModelByGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryOnlineModelByGroup(HttpServletRequest request) {
      try {
         ModelGroupRequest modelGroupRequest = new ModelGroupRequest();
         modelGroupRequest.setUserId(BaseUtils.getUserId(request));
         modelGroupRequest.setSystemId(this.commonService.getContextPath());
         modelGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelGroupService.searchOnlineModelGroupsService(modelGroupRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), serviceResponse.getMessage()) : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("查询所有的上线模型组信息出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"deleteModel"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteModel(ModelRequest modelRequest, HttpServletRequest request) {
      modelRequest.setDataSource(BaseUtils.getDataSource(request));

      try {
         ServiceResponse serviceResponse = this.modelService.deleteModelsService(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "删除成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("模型上线出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getModelOnLineDate"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getModelOnLineDate() {
      try {
         Hashtable<String, String> retmap = new Hashtable();
         retmap.put("endTime", DateUtils.getDateFormat(new Date(), "yyyy-MM-dd"));
         Date startDate = DateUtils.getDaybeforeOrAfter(new Date(), -6);
         retmap.put("startTime", DateUtils.parseDateToString(startDate));
         return ResponseResult.success(retmap, "删除成功!");
      } catch (Exception var3) {
         this.logger.error("根据模型id获取上线时间获取上线时间出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }

   @RequestMapping(
      value = {"addModelFragment"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addModelFragment(HttpServletRequest request, ModelFragment modelFragment) {
      try {
         ModelRequest modelRequest = new ModelRequest();
         modelRequest.setPriviewId(modelFragment.getPreviewId());
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setModelFragment(modelFragment);
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelFragmentService.addModelFragmentService(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "保存成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var5) {
         this.logger.error("新增模型片段出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"previewModel"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult previewModel(HttpServletRequest request, ModelRequest modelRequest, @RequestParam(value = "fragmentContent",required = false) String fragmentContent, @RequestParam(value = "ruleType",required = false) String ruleType, @RequestParam(value = "fragmentNum",required = false) String fragmentNum, @RequestParam(value = "channel",required = false) String channel, @RequestParam(value = "isTag",required = false) String isTag, @RequestParam(value = "fragmentId",required = false) String fragmentId, @RequestParam(value = "tagContent",required = false) String tagContent, @RequestParam(value = "tagText",required = false) String tagText, @RequestParam(value = "remark",required = false) String remark) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         modelRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
         ServiceResponse serviceResponse;
         if (StringUtils.isNullOrEmpry(modelRequest.getModelFragmentRelation())) {
            ModelFragment modelFragment = new ModelFragment();
            modelFragment.setChannel(Integer.parseInt(channel));
            modelFragment.setRuleType(Integer.parseInt(ruleType));
            modelFragment.setRemark(remark);
            modelFragment.setFragmentNum(Integer.parseInt(fragmentNum));
            modelFragment.setIsTag(Integer.parseInt(isTag));
            modelFragment.setTagContent(tagContent);
            modelFragment.setTagText(tagText);
            modelFragment.setFragmentContent(fragmentContent);
            modelFragment.setFragmentId(Long.valueOf(fragmentId));
            modelRequest.setModelFragment(modelFragment);
            this.logger.info(JSON.toJSONString(modelRequest));
            serviceResponse = this.modelService.fragmentPreviewService(modelRequest);
         } else {
            List<ModelFragment> fragments = new ArrayList();
            ModelFragment modelFragment = (ModelFragment)JSON.parseObject(modelRequest.getModelFragmentRelation(), ModelFragment.class);
            fragments.add(modelFragment);
            modelRequest.setModelFragmentRelation((String)null);
            List<Map<String, Object>> list = (List)JSON.parseObject(modelRequest.getModelFragments(), List.class);
            if (list != null && list.size() > 0) {
               Iterator var16 = list.iterator();

               while(var16.hasNext()) {
                  Map<String, Object> fragmentMap = (Map)var16.next();
                  ModelFragment mf = new ModelFragment();
                  mf.setFragmentContent(String.valueOf(fragmentMap.get("fragmentContent")));
                  mf.setFragmentId((long)Integer.parseInt(String.valueOf(fragmentMap.get("fragmentId"))));
                  mf.setIsTag(Integer.parseInt(String.valueOf(fragmentMap.get("isTag"))));
                  mf.setFragmentNum(Integer.parseInt(String.valueOf(fragmentMap.get("fragmentNum"))));
                  mf.setChannel(Integer.parseInt(String.valueOf(fragmentMap.get("channel"))));
                  mf.setTagContent(fragmentMap.get("tagContent").toString());
                  mf.setRuleType(Integer.parseInt(String.valueOf(fragmentMap.get("ruleType"))));
                  fragments.add(mf);
               }
            }

            modelRequest.setFragments(fragments);
            modelRequest.setModelFragments((String)null);
            System.out.println(JSON.toJSONString(modelRequest));
            serviceResponse = this.modelService.modelPreviewService(modelRequest);
         }

         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), serviceResponse.getMessage()) : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var19) {
         this.logger.error("模型预览/片段预览出错", var19);
         return ResponseResult.error(var19.getMessage());
      }
   }

   @RequestMapping(
      value = {"previewFragment"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult previewFragment(HttpServletRequest request, ModelRequest modelRequest, @RequestParam(value = "fragmentContent",required = false) String fragmentContent, @RequestParam(value = "ruleType",required = false) String ruleType, @RequestParam(value = "fragmentNum",required = false) String fragmentNum, @RequestParam(value = "channel",required = false) String channel, @RequestParam(value = "isTag",required = false) String isTag, @RequestParam(value = "fragmentId",required = false) String fragmentId, @RequestParam(value = "tagContent",required = false) String tagContent, @RequestParam(value = "tagText",required = false) String tagText, @RequestParam(value = "remark",required = false) String remark) {
      try {
         ModelFragment modelFragment = (ModelFragment)JSONUtils.toBean(modelRequest.getModelFragments(), ModelFragment.class);
         modelRequest.setModelFragment(modelFragment);
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.fragmentPreviewService(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getMessage(), serviceResponse.getMessage()) : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var14) {
         this.logger.error("模型预览/片段预览出错", var14);
         return ResponseResult.error(var14.getMessage());
      }
   }

   @RequestMapping(
      value = {"getHitCount"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getHitCount(HttpServletRequest request, ModelRequest modelRequest) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.fragmentCountService(modelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "操作成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("查询片段命中数出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"previewMarks"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult previewMarks(HttpServletRequest request, ModelRequest modelRequest, @RequestParam(value = "fragmentContent",required = false) String fragmentContent, @RequestParam(value = "ruleType",required = false) String ruleType, @RequestParam(value = "fragmentNum",required = false) String fragmentNum, @RequestParam(value = "channel",required = false) String channel, @RequestParam(value = "isTag",required = false) String isTag, @RequestParam(value = "fragmentId",required = false) String fragmentId, @RequestParam(value = "tagContent",required = false) String tagContent, @RequestParam(value = "tagText",required = false) String tagText, @RequestParam(value = "remark",required = false) String remark) {
      try {
         ModelFragment modelFragment;
         if (StringUtils.isNullOrEmpry(modelRequest.getModelFragmentRelation())) {
            modelFragment = new ModelFragment();
            modelFragment.setChannel(Integer.parseInt(channel));
            modelFragment.setRuleType(Integer.parseInt(ruleType));
            modelFragment.setRemark(remark);
            modelFragment.setFragmentNum(Integer.parseInt(fragmentNum));
            modelFragment.setIsTag(Integer.parseInt(isTag));
            modelFragment.setTagContent(tagContent);
            modelFragment.setTagText(tagText);
            modelFragment.setFragmentContent(fragmentContent);
            modelFragment.setFragmentId(Long.valueOf(fragmentId));
            modelRequest.setModelFragment(modelFragment);
         } else {
            modelFragment = (ModelFragment)JSON.parseObject(modelRequest.getModelFragmentRelation(), ModelFragment.class);
            modelRequest.setModelFragment(modelFragment);
         }

         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         modelRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
         this.logger.error(JSON.toJSONString(modelRequest));
         ModelResponse modelResponse = this.modelAccuracyService.markPreviewService(modelRequest);
         return modelResponse.isSuccess() ? ResponseResult.success(modelResponse.getValue(), modelResponse.getMessage()) : ResponseResult.error(modelResponse.getMessage());
      } catch (Exception var13) {
         this.logger.error("模型标记预览出错", var13);
         return ResponseResult.error(var13.getMessage());
      }
   }

   @RequestMapping(
      value = {"getMarkCount"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getMarkCount(HttpServletRequest request, ModelRequest modelRequest) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ModelResponse modelResponse = this.modelAccuracyService.getMarkCount(modelRequest);
         return modelResponse.isSuccess() ? ResponseResult.success(modelResponse.getValue(), modelResponse.getMessage()) : ResponseResult.error(modelResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("模型标记预览出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getTagProperty"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTagProperty(HttpServletRequest request, ModelTagRequest modelTagRequest) {
      try {
         modelTagRequest.setUserId(BaseUtils.getUserId(request));
         modelTagRequest.setSystemId(this.commonService.getContextPath());
         modelTagRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.searchTagProperty(modelTagRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), serviceResponse.getMessage()) : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("查询标签属性出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getTagOperation"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTagOperation(HttpServletRequest request, ModelTagRequest modelTagRequest) {
      try {
         modelTagRequest.setUserId(BaseUtils.getUserId(request));
         modelTagRequest.setSystemId(this.commonService.getContextPath());
         modelTagRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.searchTagOperation(modelTagRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), serviceResponse.getMessage()) : ResponseResult.error(serviceResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("查询属性操作出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"getTelephoneMarkState"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTelephoneMarkState(HttpServletRequest request, ModelRequest modelRequest) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ModelResponse modelResponse = this.modelAccuracyService.getTelephoneMarkState(modelRequest);
         return modelResponse.isSuccess() ? ResponseResult.success(modelResponse.getValue(), modelResponse.getMessage()) : ResponseResult.error(modelResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("查询录音标记结果出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping({"dealMarkData"})
   @ResponseBody
   public ResponseResult dealMarkData(HttpServletRequest request, ModelRequest modelRequest) {
      try {
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ModelResponse modelResponse = this.modelAccuracyService.getTelephoneMarkState(modelRequest);
         return modelResponse.isSuccess() ? ResponseResult.success(modelResponse.getMessage(), "查询成功!") : ResponseResult.error(modelResponse.getMessage());
      } catch (Exception var4) {
         this.logger.error("查询录音标记结果出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"dealMarkData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult dealMarkData(HttpServletRequest request, MarkDataRequest markDataRequest) {
      int yesExist = markDataRequest.getYesExist();
      int yesNotExist = markDataRequest.getYesNotExist();
      int noExist = markDataRequest.getNoExist();
      int noNotExist = markDataRequest.getNoNotExist();
      int markState = markDataRequest.getMarkState();
      int lastMarkState = markDataRequest.getLastMarkState();
      float percent = 0.0F;
      if (markState == 1) {
         if (lastMarkState == 1) {
            --yesExist;
         } else if (lastMarkState == 2) {
            --yesNotExist;
         } else if (lastMarkState == 3) {
            --noExist;
         } else if (lastMarkState == 4) {
            --noNotExist;
         }

         ++yesExist;
      } else if (markState == 2) {
         if (lastMarkState == 1) {
            --yesExist;
         } else if (lastMarkState == 2) {
            --yesNotExist;
         } else if (lastMarkState == 3) {
            --noExist;
         } else if (lastMarkState == 4) {
            --noNotExist;
         }

         ++yesNotExist;
      } else if (markState == 3) {
         if (lastMarkState == 1) {
            --yesExist;
         } else if (lastMarkState == 2) {
            --yesNotExist;
         } else if (lastMarkState == 3) {
            --noExist;
         } else if (lastMarkState == 4) {
            --noNotExist;
         }

         ++noExist;
      } else {
         if (markState != 4) {
            return ResponseResult.error("操作失败!");
         }

         if (lastMarkState == 1) {
            --yesExist;
         } else if (lastMarkState == 2) {
            --yesNotExist;
         } else if (lastMarkState == 3) {
            --noExist;
         } else if (lastMarkState == 4) {
            --noNotExist;
         }

         ++noNotExist;
      }

      int total = yesExist + noExist;
      percent = (float)yesExist / (float)total * 100.0F;
      percent = (float)Math.round(percent * 100.0F) / 100.0F;
      HashMap<String, Object> result = new HashMap();
      result.put("yExist", yesExist);
      result.put("yNoExist", yesNotExist);
      result.put("nExist", noExist);
      result.put("nNoExist", noNotExist);
      result.put("accuracy", percent);
      return ResponseResult.success(result, "操作成功!");
   }

   @RequestMapping(
      value = {"voiceMark"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult voiceMark(HttpServletRequest request, VoiceMarkRequest voiceMarkRequest) {
      try {
         AuthorizeInfo authorizeInfo = new AuthorizeInfo();
         authorizeInfo.setUserId(BaseUtils.getUserId(request));
         authorizeInfo.setSystemId(this.commonService.getContextPath());
         authorizeInfo.setDataSource(BaseUtils.getDataSource(request));
         voiceMarkRequest.setAuthorizeInfo(authorizeInfo);
         voiceMarkRequest.setDataSource(BaseUtils.getDataSource(request));
         boolean result = this.modelApplyService.addVoiceMark(voiceMarkRequest);
         return result ? ResponseResult.success("操作成功!") : ResponseResult.error("操作失败!");
      } catch (Exception var5) {
         this.logger.error("人工测听标注出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"voiceRemark"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult voiceRemark(HttpServletRequest request, VoiceCommentRequest voiceCommentRequest) {
      try {
         AuthorizeInfo authorizeInfo = new AuthorizeInfo();
         authorizeInfo.setUserId(BaseUtils.getUserId(request));
         authorizeInfo.setSystemId(this.commonService.getContextPath());
         authorizeInfo.setDataSource(BaseUtils.getDataSource(request));
         voiceCommentRequest.setAuthorizeInfo(authorizeInfo);
         voiceCommentRequest.setDataSource(BaseUtils.getDataSource(request));
         boolean result = this.modelApplyService.addFragmentVoiceComment(voiceCommentRequest);
         return result ? ResponseResult.success("操作成功!") : ResponseResult.error("操作失败!");
      } catch (Exception var5) {
         this.logger.error("片段录音备注出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping({"queryCondition"})
   @ResponseBody
   public ResponseResult queryCondition(HttpServletRequest request, ModelRequest modelRequest) {
      modelRequest.setDataSource(BaseUtils.getDataSource(request));

      try {
         Map<String, Object> result = this.modelService.searConditionByModelId(modelRequest);
         return ResponseResult.success(result, "查询成功!");
      } catch (ViePlatformServiceException var4) {
         this.logger.error("查询模型预上线筛选条件出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"saveCondition"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveCondition(ModelRequest modelRequest, HttpServletRequest request, @RequestParam("modelAccuracy") String modelAccuracy, @RequestParam("channel") String channel, @RequestParam(value = "modelComment",required = false) String modelComment) {
      try {
         if (modelRequest.getModelFragments().toString().equals("[]") && modelRequest.getSilenceRule().toString().equals("[]")) {
            return ResponseResult.error("请添加规则信息!");
         } else {
            if (modelRequest.getModelFragments().toString().equals("[]") && !modelRequest.getSilenceRule().toString().equals("[]") && null != JSONObject.parseObject(modelRequest.getModelFragmentRelation()).get("fragmentContent")) {
               String fc = JSONObject.parseObject(modelRequest.getModelFragmentRelation()).get("fragmentContent").toString();
               if (!"".equals(fc)) {
                  return ResponseResult.error("请添加规则信息!");
               }
            }

            modelRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
            ModelInfo modelInfo = new ModelInfo();
            modelInfo.setModelId(modelRequest.getModelId());
            modelInfo.setModelName(modelRequest.getModelName());
            modelInfo.setModelAccuracy(Float.parseFloat(modelAccuracy));
            modelInfo.setChannel(Integer.parseInt(channel));
            modelInfo.setModelType(1);
            modelInfo.setModelComment(modelComment);
            modelRequest.setModelInfo(modelInfo);
            ModelFragment modelFragment = (ModelFragment)JSON.parseObject(modelRequest.getModelFragmentRelation(), ModelFragment.class);
            modelRequest.setModelFragment(modelFragment);
            modelRequest.setUserId(BaseUtils.getUserId(request));
            modelRequest.setSystemId(this.commonService.getContextPath());
            modelRequest.setDataSource(BaseUtils.getDataSource(request));
            ServiceResponse serviceResponse = null;
            ServiceResponse serviceResponse2 = null;
            if (modelRequest.getModelId() == -1L) {
               serviceResponse = this.modelService.addModelService(modelRequest);
               if (serviceResponse == null || !serviceResponse.isSuccessful()) {
                  return ResponseResult.error(serviceResponse == null ? "新增模型出错" : String.valueOf(serviceResponse.getMessage()));
               }

               Long l = Long.valueOf(String.valueOf(serviceResponse.getValue()));
               serviceResponse2 = this.modelService.saveCondition(modelRequest, l);
            } else {
               serviceResponse = this.modelService.updateModelService(modelRequest);
               serviceResponse.setValue(modelRequest.getModelId());
               serviceResponse2 = this.modelService.saveCondition(modelRequest, modelRequest.getModelId());
            }

            if (serviceResponse.isSuccessful() && serviceResponse2.isSuccessful()) {
               return ResponseResult.success(serviceResponse.getValue(), "保存成功!");
            } else {
               this.logger.error("调用保存模型返回false, 错误信息：" + serviceResponse.getValue());
               return ResponseResult.error(String.valueOf(serviceResponse.getMessage()));
            }
         }
      } catch (Exception var11) {
         this.logger.error("保存模型和预上线筛选条件", var11);
         return ResponseResult.error(var11.getMessage());
      }
   }

   @RequestMapping(
      value = {"saveModel"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveModel(ModelRequest modelRequest, HttpServletRequest request, @RequestParam("modelAccuracy") String modelAccuracy, @RequestParam("channel") String channel, @RequestParam(value = "modelComment",required = false) String modelComment) {
      try {
         modelRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
         ModelInfo modelInfo = new ModelInfo();
         modelInfo.setModelId(modelRequest.getModelId());
         modelInfo.setModelName(modelRequest.getModelName());
         modelInfo.setModelAccuracy(Float.parseFloat(modelAccuracy));
         modelInfo.setChannel(Integer.parseInt(channel));
         modelInfo.setModelType(1);
         modelInfo.setModelComment(modelComment);
         modelRequest.setModelInfo(modelInfo);
         ModelFragment modelFragment = (ModelFragment)JSON.parseObject(modelRequest.getModelFragmentRelation(), ModelFragment.class);
         modelRequest.setModelFragment(modelFragment);
         modelRequest.setUserId(BaseUtils.getUserId(request));
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = null;
         if (modelRequest.getModelId() > 0L) {
            serviceResponse = this.modelService.updateModelService(modelRequest);
            serviceResponse.setValue(modelRequest.getModelId());
         } else {
            serviceResponse = this.modelService.addModelService(modelRequest);
         }

         if (serviceResponse.isSuccessful()) {
            return ResponseResult.success(serviceResponse.getValue(), serviceResponse.getMessage());
         } else {
            this.logger.error("调用保存模型返回false, 错误信息：" + serviceResponse.getValue());
            return ResponseResult.error(String.valueOf(serviceResponse.getMessage()));
         }
      } catch (Exception var9) {
         this.logger.error("保存模型出错", var9);
         return ResponseResult.error(var9.getMessage());
      }
   }

   @RequestMapping(
      value = {"clearVoiceMark"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult clearVoiceMark(HttpServletRequest request, VoiceMarkRequest voiceMarkRequest) {
      try {
         AuthorizeInfo authorizeInfo = new AuthorizeInfo();
         authorizeInfo.setUserId(BaseUtils.getUserId(request));
         authorizeInfo.setSystemId(this.commonService.getContextPath());
         authorizeInfo.setDataSource(BaseUtils.getDataSource(request));
         voiceMarkRequest.setAuthorizeInfo(authorizeInfo);
         voiceMarkRequest.setDataSource(BaseUtils.getDataSource(request));
         boolean result = this.modelApplyService.clearVoiceMark(voiceMarkRequest);
         return result ? ResponseResult.success("操作成功!") : ResponseResult.error("操作失败!");
      } catch (Exception var5) {
         this.logger.error("清除片段人工测听标注出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"getTableData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTableData(HttpServletRequest request, DataDetailRequest dataDetailRequest, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime, @RequestParam(value = "modelId",required = false) String modelId, @RequestParam("pageNum") String pageNum, @RequestParam("pageSize") String pageSize, @RequestParam("sortColumn") String sortColumn, @RequestParam("sortType") String sortType, @RequestParam("searchDimension") String searchDimension, @RequestParam(value = "dataType",required = false) Integer dataType, @RequestParam(value = "batchId",required = false) String batchId, @RequestParam(value = "ifLone",required = false) Integer ifLone) {
      try {
         this.logger.info("模型通话列表controler层getTableData方法开始响应");
         int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
         ModelTableDataRequest modelTableDataRequest = new ModelTableDataRequest();
         modelTableDataRequest.setStartTime(startTime);
         modelTableDataRequest.setEndTime(endTime);
         modelTableDataRequest.setModelId(modelId);
         modelTableDataRequest.setPageNum(Integer.valueOf(pageNum));
         modelTableDataRequest.setPageSize(Integer.valueOf(pageSize));
         modelTableDataRequest.setSortColumn(sortColumn);
         modelTableDataRequest.setSortType(sortType);
         modelTableDataRequest.setIfLone("0");
         modelTableDataRequest.setBatchId(batchId);
         modelTableDataRequest.setDataType(2);
         modelTableDataRequest.setDataSource(BaseUtils.getDataSource(request));
         modelTableDataRequest.setUserId(BaseUtils.getUserId(request));
         modelTableDataRequest.setSystemId(this.commonService.getContextPath());
         ColumnInfo columnInfo = this.getColumnMaps(searchDimension);
         List<String> selCols = columnInfo.getColumns();
         List<ColumnMap> showCols = new ArrayList();
         HashMap<String, String> dimensionMap = this.getDimensionMap(request);
         Iterator var20 = selCols.iterator();

         while(true) {
            while(var20.hasNext()) {
               String colFiled = (String)var20.next();
               if (1 == insightType && "id".equals(colFiled)) {
                  colFiled = "taskId";
                  showCols.add(new ColumnMap((String)dimensionMap.get(colFiled), "id"));
               } else {
                  showCols.add(new ColumnMap((String)dimensionMap.get(colFiled), colFiled));
               }
            }

            showCols.add(new ColumnMap("关键词", "keyword"));
            selCols.add("offLineTagInfo");
            modelTableDataRequest.setSearchColumns(selCols);
            this.logger.info(JSON.toJSONString(modelTableDataRequest));
            DataDetailResponse dataDetailResponse = this.modelApplyService.getTableData(modelTableDataRequest);
            List<DataInfo> listDatas = this.detailResultProcess(dataDetailResponse.getValues(), modelId, request);
            this.logger.info(JSON.toJSONString(listDatas));
            PagerResponse<DataInfo> pagerResponse = new PagerResponse();
            pagerResponse.setPageNum(dataDetailResponse.getPageNow());
            pagerResponse.setPageSize(dataDetailResponse.getPageSize());
            pagerResponse.setTotalPages(dataDetailResponse.getTotalPage());
            pagerResponse.setTotalRows((long)dataDetailResponse.getTotalSize());
            pagerResponse.setRows(listDatas);
            Map<String, Object> retMap = new HashMap();
            retMap.put("previewList", pagerResponse);
            retMap.put("columns", showCols);
            this.logger.info("模型通话列表controler层getTableData方法响应成功");
            return ResponseResult.success(retMap, "查询成功!");
         }
      } catch (Exception var24) {
         this.logger.error("模型通话记录查询列表数据出错", var24);
         return ResponseResult.error(var24.getMessage());
      }
   }

   private ColumnInfo getColumnMaps(String searchDimension) {
      ColumnInfo columnInfo = new ColumnInfo();
      if (searchDimension != null && !"".equals(searchDimension)) {
         List<Object> list = (List)JSON.parseObject(searchDimension, List.class);
         if (list != null && list.size() != 0) {
            for(int i = 0; i < list.size(); ++i) {
               Map<String, String> map = (Map)JSON.parseObject(String.valueOf(list.get(i)), Map.class);
               ColumnMap columnMap = new ColumnMap((String)map.get("columnName"), (String)map.get("column"));
               columnInfo.getColumnMaps().add(columnMap);
               columnInfo.getColumns().add(columnMap.getColumn());
            }
         }
      }

      columnInfo.getColumnMaps().add(new ColumnMap("匹配规则", "keyword"));
      columnInfo.getColumnMaps().add(new ColumnMap("模型", "modelName"));
      columnInfo.getColumnMaps().add(new ColumnMap("人工测听标记", "mark"));
      columnInfo.setColumnsStr(columnInfo.getColumns().toString());
      return columnInfo;
   }

   public List<DataInfo> detailResultProcess(List<LinkedHashMap<String, Object>> dataMaps, String modelId, HttpServletRequest request) throws Exception {
      int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
      if (dataMaps != null && dataMaps.size() != 0) {
         List<DataInfo> dataInfos = new ArrayList();
         DimensionRequest dimensionRequest = new DimensionRequest();
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         LinkedHashMap<String, DimensionConfig> dimenMap = this.dimensionService.getDimensionMap(dimensionRequest);
         List<String> durationList = new ArrayList();
         Iterator var11 = dimenMap.entrySet().iterator();

         while(true) {
            Map.Entry entry;
            do {
               if (!var11.hasNext()) {
                  var11 = dataMaps.iterator();

                  label64:
                  while(var11.hasNext()) {
                     LinkedHashMap<String, Object> result = (LinkedHashMap)var11.next();
                     List<HashMap<String, Object>> modelList = new ArrayList();
                     HashMap<String, Object> lhm = new HashMap();
                     lhm.put("offLineTagId", result.get("offLineTagId"));
                     lhm.put("offLineTagInfo", result.get("offLineTagInfo"));
                     modelList.add(lhm);
                     List<Map<String, Object>> keyWordInfo = this.getKeyWord(modelList, modelId);
                     String id;
                     if (1 == insightType) {
                        id = String.valueOf(result.get("id"));
                     } else {
                        id = String.valueOf(result.get("voiceId"));
                     }

                     Iterator var16 = result.entrySet().iterator();

                     while(true) {
                        while(true) {
                           while(true) {
                              Map.Entry durationEntry;
                              do {
                                 if (!var16.hasNext()) {
                                    DataInfo dataInfo = new DataInfo(id, 0, 0);
                                    dataInfo.setKeywordInfos(keyWordInfo);
                                    dataInfo.setDataMaps(result);
                                    dataInfos.add(dataInfo);
                                    continue label64;
                                 }

                                 durationEntry = (Map.Entry)var16.next();
                              } while(!durationList.contains(durationEntry.getKey()));

                              String durationKey = String.valueOf(durationEntry.getKey());
                              if (result.get(durationKey) != null) {
                                 if (!"n0avgSpeed".equals(durationKey) && !"n1avgSpeed".equals(durationKey)) {
                                    result.put(durationKey, Math.round((float)Long.parseLong(String.valueOf(result.get(durationKey))) / 1000.0F));
                                 } else {
                                    String value = String.valueOf(result.get(durationKey));
                                    if (value != null && !"".equals(value) && !"0".equals(value)) {
                                       String aa = this.df.format((double)(this.df.parse(value).floatValue() / 1000.0F));
                                       result.put(durationKey, aa);
                                    } else {
                                       result.put(durationKey, 0);
                                    }
                                 }
                              } else {
                                 result.put(durationKey, 0);
                              }
                           }
                        }
                     }
                  }

                  return dataInfos;
               }

               entry = (Map.Entry)var11.next();
            } while(((DimensionConfig)entry.getValue()).getFlag() != 1 && ((DimensionConfig)entry.getValue()).getFlag() != 2);

            durationList.add(((DimensionConfig)entry.getValue()).getIndexField());
         }
      } else {
         return null;
      }
   }

   private List<Map<String, Object>> getKeyWord(List<HashMap<String, Object>> keyWordInfo, String modelId) throws Exception {
      List<Map<String, Object>> list = new ArrayList();
      if (keyWordInfo != null && keyWordInfo.size() > 0) {
         ObjectMapper mapper = new ObjectMapper();
         Iterator var5 = keyWordInfo.iterator();

         while(true) {
            HashMap hm;
            do {
               String offLineId;
               do {
                  do {
                     if (!var5.hasNext()) {
                        return list;
                     }

                     hm = (HashMap)var5.next();
                     offLineId = String.valueOf(hm.get("offLineTagId"));
                  } while(!modelId.equals(offLineId));
               } while(hm.get("offLineTagInfo") == null);
            } while(hm.get("offLineTagInfo").equals("[]"));

            List<HashMap<String, Object>> wordList = (List)mapper.readValue(hm.get("offLineTagInfo").toString(), List.class);
            Iterator var9 = wordList.iterator();

            while(var9.hasNext()) {
               HashMap<String, Object> hmss = (HashMap)var9.next();
               int type = Integer.parseInt(hmss.get("type").toString());
               if (type == 0) {
                  Map<String, Object> hms = new HashMap();
                  hms.put("word", hmss.get("content"));
                  hms.put("begin", hmss.get("beginTime"));
                  hms.put("end", hmss.get("endTime"));
                  hms.put("voiceId", hmss.get("voiceId"));
                  if (!list.contains(hms)) {
                     list.add(hms);
                  }
               }
            }
         }
      } else {
         return list;
      }
   }

   public long toSecond(long millSecond) {
      return millSecond == 0L ? 0L : (long)Math.round((float)millSecond / 1000.0F);
   }

   private HashMap<String, String> getDimensionMap(HttpServletRequest request) throws Exception {
      HashMap<String, String> dimMap = new HashMap();

      try {
         DimensionRequest dimensionRequest = new DimensionRequest();
         dimensionRequest.setUserId(BaseUtils.getUserId(request));
         dimensionRequest.setSystemId(this.commonService.getContextPath());
         dimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse result = this.dimensionService.searchPersonalDimensionService(dimensionRequest);
         AllDimensionRequest allDimensionRequest = new AllDimensionRequest();
         AuthorizeInfo authorizeInfo = new AuthorizeInfo();
         authorizeInfo.setUserId(BaseUtils.getUserId(request));
         authorizeInfo.setSystemId(this.commonService.getContextPath());
         allDimensionRequest.setAuthorizeInfo(authorizeInfo);
         allDimensionRequest.setDataSource(BaseUtils.getDataSource(request));
         DimensionResponse systemResult = this.dimensionService.getTopicDimensionService(allDimensionRequest);
         List<HashMap<String, Object>> systemResultList = (List)systemResult.getValue();
         if (systemResultList != null && systemResultList.size() > 0) {
            Iterator var9 = systemResultList.iterator();

            while(var9.hasNext()) {
               HashMap<String, Object> map = (HashMap)var9.next();
               dimMap.put(String.valueOf(map.get("key")), String.valueOf(map.get("name")));
            }
         }

         List<HashMap<String, Object>> resultList = (List)result.getValue();
         if (resultList != null && resultList.size() > 0) {
            Iterator var13 = resultList.iterator();

            while(var13.hasNext()) {
               HashMap<String, Object> map = (HashMap)var13.next();
               dimMap.put(String.valueOf(map.get("dimensionAnotherName")), String.valueOf(map.get("dimensionName")));
            }
         }

         return dimMap;
      } catch (Exception var12) {
         this.logger.error("查询所有维度出錯", var12);
         throw var12;
      }
   }

   @ResponseBody
   @RequestMapping(
      value = {"exportPreviewModel"},
      method = {RequestMethod.POST}
   )
   public void exportPreviewModel(HttpServletRequest request, HttpServletResponse response, ModelRequest markModelRequest, @RequestParam(value = "fragmentContent",required = false) String fragmentContent, @RequestParam(value = "ruleType",required = false) String ruleType, @RequestParam(value = "fragmentNum",required = false) String fragmentNum, @RequestParam(value = "channel",required = false) String channel, @RequestParam(value = "isTag",required = false) String isTag, @RequestParam(value = "fragmentId",required = false) String fragmentId, @RequestParam(value = "tagContent",required = false) String tagContent, @RequestParam(value = "tagText",required = false) String tagText, @RequestParam(value = "remark",required = false) String remark, @RequestParam(value = "modelName",required = false) String modelName) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
      String excelName = "模型_" + (StringUtils.isNullOrEmpry(modelName) ? "null" : modelName) + "-预览数据-" + sdf.format(new Date()) + ".xlsx";
      ServletOutputStream ouputStream = null;
      ModelResponse markPreviewResponse = null;
      ServiceResponse serviceResponse = null;

      try {
         response.reset();
         response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelName, "UTF-8"));
         response.setContentType("application/vnd.ms-excel");
         XSSFWorkbook workbook = new XSSFWorkbook();
         XSSFSheet callSheet = workbook.createSheet();
         XSSFSheet markPreview = workbook.createSheet();
         workbook.setSheetName(0, "匹配通话");
         workbook.setSheetName(1, "人工测听标记");
         String userId = BaseUtils.getUserId(request);
         int pageNum = 1;
         int pageSize = LoadConfig.getConfigProperty("exportPreviewResult") == null ? 10000 : Integer.parseInt(LoadConfig.getConfigProperty("exportPreviewResult"));
         markModelRequest.setDataSource(BaseUtils.getDataSource(request));
         Long markStartTime = System.currentTimeMillis();
         markPreviewResponse = this.getMarkPreviewModel(request, markModelRequest, userId, channel, ruleType, remark, fragmentNum, isTag, tagContent, tagText, fragmentContent, fragmentId, pageNum, pageSize);
         Long markEndTime = System.currentTimeMillis();
         this.logger.info("模型标记结果查询耗时===" + (markEndTime - markStartTime));
         serviceResponse = this.getPreviewModel(request, markModelRequest, userId, channel, ruleType, remark, fragmentNum, isTag, tagContent, tagText, fragmentContent, fragmentId, pageNum, pageSize);
         Long previewEndTime = System.currentTimeMillis();
         this.logger.info("模型预览结果查询耗时===" + (previewEndTime - markEndTime));
         XSSFCellStyle titleStyle = this.createCostomCellStyle(workbook, (short)32767);
         XSSFCellStyle redStyle = this.createCostomCellStyle(workbook, (short)10);
         List rowHead = null;
         if (serviceResponse != null && serviceResponse.isSuccessful()) {
            HashMap<String, Object> retMap = (HashMap)serviceResponse.getValue();
            List<HashMap> rowHeadColumns = (List)retMap.get("columns");
            ResultTable resultTable = (ResultTable)retMap.get("previewList");
            List<HashMap> previewRows = (List)resultTable.getRows();
            Iterator var34 = previewRows.iterator();

            while(var34.hasNext()) {
               HashMap map = (HashMap)var34.next();
               HashMap dataMaps = (HashMap)map.get("dataMaps");
               String paperId = (String)dataMaps.get("paperId");
               String cardNo = (String)dataMaps.get("cardNo");
               String dnis = (String)dataMaps.get("dnis");
               String callNumber = (String)dataMaps.get("callNumber");
               String newPaperId = DesensitizationUtils.specialCharactorReplace("callNumber", callNumber);
               String maskedPaperId = DesensitizationUtils.specialCharactorReplace("paperId", paperId);
               String maskedCardNo = DesensitizationUtils.specialCharactorReplace("cardNo", cardNo);
               String maskedDnis = DesensitizationUtils.specialCharactorReplace("dnis", dnis);
               dataMaps.put("paperId", maskedPaperId);
               dataMaps.put("cardNo", maskedCardNo);
               dataMaps.put("dnis", maskedDnis);
               dataMaps.put("callNumber", newPaperId);
            }

            this.logger.info("dataInfoList1==" + JSON.toJSONString(previewRows));
            this.createCallListSheet(callSheet, titleStyle, rowHeadColumns, previewRows);
         }

         if (markPreviewResponse != null && markPreviewResponse.isSuccess) {
            ResultTable resultTable = new ResultTable();
            HashMap<String, Object> retMap = (HashMap)markPreviewResponse.getValue();
            HashMap<String, Object> preMap = (HashMap)retMap.get("previewList");
            resultTable.setPageNum((Integer)preMap.get("pageNum"));
            resultTable.setPageSize((Integer)preMap.get("pageSize"));
            resultTable.setSortColumn(preMap.get("sortColumn") == null ? "" : preMap.get("sortColumn").toString());
            resultTable.setSortMethod(preMap.get("sortMethod") == null ? "" : preMap.get("sortMethod").toString());
            resultTable.setTotalPages((Integer)preMap.get("totalPages"));
            resultTable.setTotalRows((Integer)preMap.get("totalRows"));
            resultTable.setRows(preMap.get("rows"));
            rowHead = (List)retMap.get("columns");
            List<DataInfo> dataInfoList = new ArrayList();
            List alist = (ArrayList)resultTable.getRows();

            for(int k = 0; k < alist.size(); ++k) {
               DataInfo dataInfo = new DataInfo();
               HashMap<String, Object> map = (HashMap)alist.get(k);
               dataInfo.setId(null == map.get("id") ? "" : map.get("id").toString());
               dataInfo.setMark(null == map.get("mark") ? 0 : (Integer)map.get("mark"));
               dataInfo.setIsExist(null == map.get("isExist") ? 0 : (Integer)map.get("isExist"));
               dataInfo.setRemark(null == map.get("remark") ? "" : map.get("remark").toString());
               dataInfo.setRemarked(null == map.get("remarked") ? 0 : (Integer)map.get("remarked"));
               dataInfo.setDataMarkId(null == map.get("dataMarkId") ? 0L : (Long)map.get("dataMarkId"));
               dataInfo.setDataRemarkId(null == map.get("dataRemarkId") ? 0L : (Long)map.get("dataRemarkId"));
               dataInfo.setFragmentId(null == map.get("fragmentId") ? 0L : (Long)map.get("fragmentId"));
               dataInfo.setKeywordInfos((List)map.get("keywordInfos"));
               LinkedHashMap<String, Object> maps = (LinkedHashMap)map.get("dataMaps");
               String paperId = (String)maps.get("paperId");
               String cardNo = (String)maps.get("cardNo");
               String dnis = (String)maps.get("dnis");
               String callNumber = (String)maps.get("callNumber");
               String newPaperId = DesensitizationUtils.specialCharactorReplace("paperId", paperId);
               String newCardNo = DesensitizationUtils.specialCharactorReplace("cardNo", cardNo);
               String newDnis = DesensitizationUtils.specialCharactorReplace("dnis", dnis);
               String newCallNumber = DesensitizationUtils.specialCharactorReplace("callNumber", callNumber);
               maps.put("paperId", newPaperId);
               maps.put("cardNo", newCardNo);
               maps.put("dnis", newDnis);
               maps.put("callNumber", newCallNumber);
               dataInfo.setDataMaps(maps);
               dataInfoList.add(dataInfo);
            }

            this.logger.info("dataInfoList2==" + JSON.toJSONString(dataInfoList));
            this.createmarkPreviewSheet(markPreview, titleStyle, redStyle, rowHead, dataInfoList);
         }

         Long end = System.currentTimeMillis();
         this.logger.info("模型标记预览导出结果写入Excel耗时==" + (end - previewEndTime));
         ouputStream = response.getOutputStream();
         workbook.write(ouputStream);
         ouputStream.flush();
         ouputStream.close();
      } catch (Exception var48) {
         this.logger.error("导出模型规则出错", var48);
      }

   }

   private XSSFCellStyle createCostomCellStyle(XSSFWorkbook workbook, short fontColor) {
      XSSFFont titleFont = workbook.createFont();
      XSSFCellStyle titleStyle = workbook.createCellStyle();
      workbook.createCellStyle();
      titleStyle.setAlignment((short)2);
      titleFont.setBoldweight((short)400);
      titleFont.setColor(fontColor);
      titleFont.setFontName("宋体");
      titleFont.setFontHeightInPoints((short)11);
      titleStyle.setFont(titleFont);
      titleStyle.setBorderBottom((short)1);
      titleStyle.setBorderTop((short)1);
      titleStyle.setBorderRight((short)1);
      titleStyle.setBorderLeft((short)1);
      return titleStyle;
   }

   private void createCallListSheet(XSSFSheet callSheet, XSSFCellStyle titleStyle, List<HashMap> rowHead, List<HashMap> dataInfoList) {
      XSSFRow rowHeadRow = callSheet.createRow(0);
      List<String> sortString = new ArrayList();

      int rowHang;
      for(rowHang = 0; rowHang < rowHead.size(); ++rowHang) {
         HashMap cmap = (HashMap)rowHead.get(rowHang);
         sortString.add(String.valueOf(cmap.get("column")));
         this.changeFontStyle(rowHeadRow, titleStyle, rowHang, String.valueOf(cmap.get("columnName")));
      }

      this.createSheetLength(callSheet, sortString.size());
      rowHang = 1;

      for(int i = 0; i < dataInfoList.size(); ++i) {
         XSSFRow rows = callSheet.createRow(rowHang++);
         LinkedHashMap<String, Object> dataMaps = (LinkedHashMap)((HashMap)dataInfoList.get(i)).get("dataMaps");
         List<Map<String, Object>> keywordInfos = (List)((HashMap)dataInfoList.get(i)).get("keywordInfos");

         for(int j = 0; j < sortString.size(); ++j) {
            String headName = (String)sortString.get(j);
            String rowValue = "";
            label70:
            switch (headName) {
               case "keyword":
                  if (null == keywordInfos) {
                     break;
                  }

                  int m = 0;

                  while(true) {
                     if (m >= keywordInfos.size()) {
                        break label70;
                     }

                     rowValue = rowValue + ((Map)keywordInfos.get(m)).get("word");
                     if (m < keywordInfos.size() - 1) {
                        rowValue = rowValue + " ";
                     }

                     ++m;
                  }
               case "mark":
                  rowValue = Integer.parseInt(String.valueOf(((HashMap)dataInfoList.get(i)).get("mark"))) == 0 ? "" : (Integer.parseInt(String.valueOf(((HashMap)dataInfoList.get(i)).get("mark"))) == 1 ? "Y" : "N");
                  break;
               case "remark":
                  rowValue = ((HashMap)dataInfoList.get(i)).get("remark") == null ? "" : String.valueOf(((HashMap)dataInfoList.get(i)).get("remark"));
                  break;
               default:
                  rowValue = dataMaps.get(headName) == null ? "" : dataMaps.get(headName).toString();
            }

            this.changeFontStyle(rows, titleStyle, j, rowValue);
         }
      }

   }

   private void createmarkPreviewSheet(XSSFSheet callSheet, XSSFCellStyle titleStyle, XSSFCellStyle otherStyle, List<com.iflytek.vie.app.pojo.topicgroup.ColumnMap> rowHead, List<DataInfo> dataInfoList) {
      XSSFRow rowHeadRow = callSheet.createRow(0);
      List<String> sortString = new ArrayList();

      int rowHang;
      for(rowHang = 0; rowHang < rowHead.size(); ++rowHang) {
         com.iflytek.vie.app.pojo.topicgroup.ColumnMap cmap = (com.iflytek.vie.app.pojo.topicgroup.ColumnMap)rowHead.get(rowHang);
         sortString.add(cmap.getColumn());
         this.changeFontStyle(rowHeadRow, titleStyle, rowHang, cmap.getColumnName());
      }

      this.createSheetLength(callSheet, sortString.size());
      rowHang = 1;

      for(int i = 0; i < dataInfoList.size(); ++i) {
         XSSFRow rows = callSheet.createRow(rowHang++);
         LinkedHashMap<String, Object> dataMaps = ((DataInfo)dataInfoList.get(i)).getDataMaps();
         List<Map<String, Object>> keywordInfos = ((DataInfo)dataInfoList.get(i)).getKeywordInfos();

         for(int j = 0; j < sortString.size(); ++j) {
            String headName = (String)sortString.get(j);
            int isExist = ((DataInfo)dataInfoList.get(i)).getIsExist() == 0 ? -1 : 1;
            String rowValue = "";
            boolean isMark = false;
            label79:
            switch (headName) {
               case "keyword":
                  if (null == keywordInfos) {
                     break;
                  }

                  int m = 0;

                  while(true) {
                     if (m >= keywordInfos.size()) {
                        break label79;
                     }

                     rowValue = rowValue + ((Map)keywordInfos.get(m)).get("word");
                     if (m < keywordInfos.size() - 1) {
                        rowValue = rowValue + " ";
                     }

                     ++m;
                  }
               case "mark":
                  rowValue = ((DataInfo)dataInfoList.get(i)).getMark() == 0 ? "" : (((DataInfo)dataInfoList.get(i)).getMark() == 1 ? "Y" : "N");
                  isMark = true;
                  break;
               case "remark":
                  rowValue = StringUtils.isNullOrEmpry(((DataInfo)dataInfoList.get(i)).getRemark()) ? "" : ((DataInfo)dataInfoList.get(i)).getRemark();
                  break;
               default:
                  rowValue = dataMaps.get(headName) == null ? "" : dataMaps.get(headName).toString();
            }

            if (isMark && isExist != ((DataInfo)dataInfoList.get(i)).getMark()) {
               this.changeFontStyle(rows, otherStyle, j, rowValue);
            } else {
               this.changeFontStyle(rows, titleStyle, j, rowValue);
            }
         }
      }

   }

   private ServiceResponse getPreviewModel(HttpServletRequest request, ModelRequest markModelRequest, String userId, String channel, String ruleType, String remark, String fragmentNum, String isTag, String tagContent, String tagText, String fragmentContent, String fragmentId, int pageNum, int pageSize) {
      ServiceResponse serviceResponse = null;

      try {
         ModelRequest previewModelRequest = new ModelRequest();
         previewModelRequest.setIsExport(1);
         previewModelRequest.setSilenceText(markModelRequest.getSilenceText());
         previewModelRequest.setSilenceRule(markModelRequest.getSilenceRule());
         previewModelRequest.setModelDimension(markModelRequest.getModelDimension());
         previewModelRequest.setModelId(markModelRequest.getModelId());
         previewModelRequest.setUserId(userId);
         previewModelRequest.setPageNum(pageNum);
         previewModelRequest.setPageSize(pageSize);
         previewModelRequest.setSystemId(this.commonService.getContextPath());
         previewModelRequest.setDataSource(BaseUtils.getDataSource(request));
         previewModelRequest.setColumns(markModelRequest.getColumns());
         previewModelRequest.setFilter(markModelRequest.getFilter());
         previewModelRequest.setFilterRuleId(markModelRequest.getFilterRuleId());
         previewModelRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
         if (StringUtils.isNullOrEmpry(markModelRequest.getModelFragmentRelation())) {
            ModelFragment modelFragment = new ModelFragment();
            modelFragment.setChannel(Integer.parseInt(channel));
            modelFragment.setRuleType(Integer.parseInt(ruleType));
            modelFragment.setRemark(remark);
            modelFragment.setFragmentNum(Integer.parseInt(fragmentNum));
            modelFragment.setIsTag(Integer.parseInt(isTag));
            modelFragment.setTagContent(tagContent);
            modelFragment.setTagText(tagText);
            modelFragment.setFragmentContent(fragmentContent);
            modelFragment.setFragmentId(Long.valueOf(fragmentId));
            previewModelRequest.setModelFragment(modelFragment);
            this.logger.info(JSON.toJSONString(previewModelRequest));
            serviceResponse = this.modelService.fragmentPreviewService(previewModelRequest);
         } else {
            List<ModelFragment> fragments = new ArrayList();
            ModelFragment modelFragment = (ModelFragment)JSON.parseObject(markModelRequest.getModelFragmentRelation(), ModelFragment.class);
            fragments.add(modelFragment);
            previewModelRequest.setModelFragmentRelation((String)null);
            List<Map<String, Object>> list = (List)JSON.parseObject(markModelRequest.getModelFragments(), List.class);
            if (list != null && list.size() > 0) {
               Iterator var20 = list.iterator();

               while(var20.hasNext()) {
                  Map<String, Object> fragmentMap = (Map)var20.next();
                  ModelFragment mf = new ModelFragment();
                  mf.setFragmentContent(String.valueOf(fragmentMap.get("fragmentContent")));
                  mf.setFragmentId((long)Integer.parseInt(String.valueOf(fragmentMap.get("fragmentId"))));
                  mf.setIsTag(Integer.parseInt(String.valueOf(fragmentMap.get("isTag"))));
                  mf.setFragmentNum(Integer.parseInt(String.valueOf(fragmentMap.get("fragmentNum"))));
                  mf.setChannel(Integer.parseInt(String.valueOf(fragmentMap.get("channel"))));
                  mf.setTagContent(fragmentMap.get("tagContent").toString());
                  mf.setRuleType(Integer.parseInt(String.valueOf(fragmentMap.get("ruleType"))));
                  fragments.add(mf);
               }
            }

            previewModelRequest.setFragments(fragments);
            previewModelRequest.setModelFragments((String)null);
            System.out.println(JSON.toJSONString(previewModelRequest));
            serviceResponse = this.modelService.modelPreviewService(previewModelRequest);
         }
      } catch (ViePlatformServiceException var23) {
         this.logger.error("模型预览出错", var23);
      }

      return serviceResponse;
   }

   private ModelResponse getMarkPreviewModel(HttpServletRequest request, ModelRequest modelRequest, String userId, String channel, String ruleType, String remark, String fragmentNum, String isTag, String tagContent, String tagText, String fragmentContent, String fragmentId, int pageNum, int pageSize) {
      ModelResponse markPreviewResponse = null;

      try {
         ModelFragment modelFragment;
         if (StringUtils.isNullOrEmpry(modelRequest.getModelFragmentRelation())) {
            modelFragment = new ModelFragment();
            modelFragment.setChannel(Integer.parseInt(channel));
            modelFragment.setRuleType(Integer.parseInt(ruleType));
            modelFragment.setRemark(remark);
            modelFragment.setFragmentNum(Integer.parseInt(fragmentNum));
            modelFragment.setIsTag(Integer.parseInt(isTag));
            modelFragment.setTagContent(tagContent);
            modelFragment.setTagText(tagText);
            modelFragment.setFragmentContent(fragmentContent);
            modelFragment.setFragmentId(Long.valueOf(fragmentId));
            modelRequest.setModelFragment(modelFragment);
         } else {
            modelFragment = (ModelFragment)JSON.parseObject(modelRequest.getModelFragmentRelation(), ModelFragment.class);
            modelRequest.setModelFragment(modelFragment);
         }

         modelRequest.setUserId(userId);
         modelRequest.setPageNum(pageNum);
         modelRequest.setPageSize(pageSize);
         modelRequest.setSystemId(this.commonService.getContextPath());
         modelRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
         this.logger.error(JSON.toJSONString(modelRequest));
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         markPreviewResponse = this.modelAccuracyService.markPreviewService(modelRequest);
      } catch (VieAppServiceException var17) {
         this.logger.error("模型标记预览出错", var17);
      }

      return markPreviewResponse;
   }

   @RequestMapping(
      value = {"/exportRule"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public void exportRule(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "fragmentContent",required = false) String fragmentContent, @RequestParam(value = "modelFragments",required = false) String modelFragments, @RequestParam(value = "silenceText",required = false) String sileneceText) {
      try {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
         String excelName = "模型规则_" + sdf.format(new Date()) + ".xlsx";
         response.reset();
         response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelName, "UTF-8"));
         response.setContentType("application/vnd.ms-excel");
         XSSFWorkbook workbook = new XSSFWorkbook();
         XSSFSheet sheet = workbook.createSheet();
         XSSFSheet sheet1 = workbook.createSheet();
         XSSFRow rowData;
         if (StringUtils.isNotNullAndEmpry(modelFragments) && StringUtils.isNotNullAndEmpry(fragmentContent)) {
            List<HashMap<String, Object>> jsonList = (List)this.mapper.readValue(modelFragments, List.class);
            HashMap<Integer, ModelRule> ruleMap = new HashMap();
            Iterator var41 = jsonList.iterator();

            while(var41.hasNext()) {
               HashMap<String, Object> jsonMap = (HashMap)var41.next();
               ModelRule modelRule = (ModelRule)JSONUtils.toBean(jsonMap, ModelRule.class);
               ruleMap.put(modelRule.getFragmentNum(), modelRule);
            }

            Object[] objects = ruleMap.keySet().toArray();
            Arrays.sort(objects);
            rowData = null;
            XSSFFont fontOne = workbook.createFont();
            XSSFFont fontOne1 = workbook.createFont();
            XSSFFont fontOne2 = workbook.createFont();
            XSSFFont fontTwo = workbook.createFont();
            XSSFFont fontThree = workbook.createFont();
            XSSFFont fontFour = workbook.createFont();
            fontOne.setBoldweight((short)700);
            XSSFCellStyle hcsOne = workbook.createCellStyle();
            hcsOne.setAlignment((short)2);
            fontOne.setFontName("宋体");
            fontOne.setFontHeightInPoints((short)11);
            hcsOne.setFont(fontOne);
            XSSFCellStyle hcsOne2 = workbook.createCellStyle();
            hcsOne2.setAlignment((short)2);
            fontOne2.setFontName("宋体");
            fontOne2.setFontHeightInPoints((short)11);
            hcsOne2.setFont(fontOne2);
            XSSFCellStyle hcsOne1 = workbook.createCellStyle();
            hcsOne1.setAlignment((short)2);
            fontOne1.setBoldweight((short)700);
            fontOne1.setFontName("宋体");
            fontOne1.setFontHeightInPoints((short)11);
            hcsOne1.setFont(fontOne);
            hcsOne1.setBorderBottom((short)1);
            hcsOne1.setBorderTop((short)1);
            hcsOne1.setBorderRight((short)1);
            hcsOne1.setBorderLeft((short)1);
            XSSFCellStyle hcsTwo = workbook.createCellStyle();
            hcsTwo.setAlignment((short)2);
            fontTwo.setFontName("宋体");
            fontTwo.setFontHeightInPoints((short)11);
            hcsTwo.setFont(fontTwo);
            hcsTwo.setBorderBottom((short)1);
            hcsTwo.setBorderTop((short)1);
            hcsTwo.setBorderRight((short)1);
            hcsTwo.setBorderLeft((short)1);
            XSSFCellStyle hcsThree = workbook.createCellStyle();
            hcsThree.setAlignment((short)1);
            fontThree.setFontName("宋体");
            fontThree.setFontHeightInPoints((short)11);
            hcsThree.setFont(fontThree);
            hcsThree.setBorderBottom((short)1);
            hcsThree.setBorderTop((short)1);
            hcsThree.setBorderRight((short)1);
            hcsThree.setBorderLeft((short)1);
            XSSFCellStyle hcsFour = workbook.createCellStyle();
            hcsFour.setAlignment((short)3);
            fontFour.setFontName("宋体");
            fontFour.setFontHeightInPoints((short)11);
            fontFour.setColor(new XSSFColor(new Color(245, 245, 245)));
            hcsFour.setFont(fontFour);
            hcsFour.setBorderBottom((short)1);
            hcsFour.setBorderTop((short)1);
            hcsFour.setBorderRight((short)1);
            hcsFour.setBorderLeft((short)1);
            XSSFCellStyle hcsFive = workbook.createCellStyle();
            hcsThree.setAlignment((short)1);
            fontThree.setFontName("宋体");
            fontThree.setFontHeightInPoints((short)11);
            hcsFive.setFont(fontThree);
            hcsFive.setBorderBottom((short)1);
            hcsFive.setBorderTop((short)1);
            hcsFive.setBorderRight((short)1);
            hcsFive.setBorderLeft((short)1);
            hcsFive.setLocked(true);
            int count = ruleMap.size();
            sheet.setColumnWidth(0, 2304);
            sheet.setColumnWidth(1, 14592);
            sheet.setColumnWidth(2, 2560);
            sheet.setColumnWidth(3, 4608);
            sheet.setColumnWidth(4, 256);
            int k = 0;

            label62:
            while(true) {
               XSSFCell channelCell;
               XSSFCell numCell;
               XSSFCell ruleCell;
               XSSFCell remarkCell;
               if (k >= 4) {
                  k = 0;

                  while(true) {
                     if (k >= count) {
                        break label62;
                     }

                     ModelRule mr = (ModelRule)ruleMap.get(objects[k]);
                     String ch = "";
                     switch (mr.getChannel()) {
                        case 0:
                           ch = "坐席";
                           break;
                        case 1:
                           ch = "客户";
                           break;
                        case 2:
                           ch = "全部";
                     }

                     if (!StringUtils.isNullOrEmpry(mr.getCondRule())) {
                        ch = "-";
                     }

                     rowData = sheet.createRow(k + 4);
                     channelCell = rowData.createCell(2);
                     channelCell.setCellStyle(hcsThree);
                     channelCell.setCellValue(ch);
                     numCell = rowData.createCell(0);
                     numCell.setCellStyle(hcsTwo);
                     numCell.setCellValue((double)(Integer)objects[k]);
                     ruleCell = rowData.createCell(1);
                     ruleCell.setCellValue(mr.getFragmentContent());
                     if (StringUtils.isNullOrEmpry(mr.getCondRule())) {
                        ruleCell.setCellStyle(hcsThree);
                     }

                     ruleCell.setCellStyle(hcsFive);
                     remarkCell = rowData.createCell(3);
                     if (StringUtils.isNullOrEmpry(mr.getRemark())) {
                        mr.setRemark("");
                     }

                     remarkCell.setCellValue(mr.getRemark());
                     remarkCell.setCellStyle(hcsThree);
                     XSSFCell condCell = rowData.createCell(4);
                     condCell.setCellValue(mr.getCondRule());
                     condCell.setCellStyle(hcsFour);
                     ++k;
                  }
               }

               rowData = sheet.createRow(k);
               XSSFCell cell = rowData.createCell(0);
               if (k == 0) {
                  sheet.addMergedRegion(new CellRangeAddress(k, 0, 0, 4));
                  cell.setCellValue("规则组合");
                  cell.setCellStyle(hcsOne);
               } else if (k == 2) {
                  cell.setCellStyle(hcsOne);
                  sheet.addMergedRegion(new CellRangeAddress(k, 2, 0, 4));
                  cell.setCellValue("规则片段");
               } else if (k == 3) {
                  XSSFCell cell1 = rowData.createCell(0);
                  cell1.setCellStyle(hcsOne1);
                  cell1.setCellValue("序号");
                  channelCell = rowData.createCell(1);
                  channelCell.setCellStyle(hcsOne1);
                  channelCell.setCellValue("规则");
                  numCell = rowData.createCell(2);
                  numCell.setCellStyle(hcsOne1);
                  numCell.setCellValue("声道");
                  ruleCell = rowData.createCell(3);
                  ruleCell.setCellStyle(hcsOne1);
                  ruleCell.setCellValue("备注");
                  remarkCell = rowData.createCell(4);
                  remarkCell.setCellStyle(hcsOne1);
                  remarkCell.setCellValue("");
               } else {
                  cell.setCellStyle(hcsOne2);
                  sheet.addMergedRegion(new CellRangeAddress(k, 1, 0, 4));
                  cell.setCellValue(fragmentContent);
               }

               ++k;
            }
         } else {
            XSSFFont fontOne = workbook.createFont();
            fontOne.setBoldweight((short)700);
            XSSFCellStyle hcsOne = workbook.createCellStyle();
            hcsOne.setAlignment((short)2);
            fontOne.setFontName("宋体");
            fontOne.setFontHeightInPoints((short)11);
            hcsOne.setFont(fontOne);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            rowData = sheet.createRow(0);
            XSSFCell titleCell = rowData.createCell(0);
            titleCell.setCellValue("规则组合");
            titleCell.setCellStyle(hcsOne);
         }

         this.createSheetLength(sheet1, 7);
         this.exportSilenceRule(sileneceText, 0, workbook, sheet1);
         ServletOutputStream ouputStream = response.getOutputStream();
         workbook.write(ouputStream);
         ouputStream.flush();
         ouputStream.close();
      } catch (Exception var37) {
         this.logger.error("导出模型规则出错", var37);
      }

   }

   private void createColumnHead(XSSFSheet sheet, String[] columNames, XSSFCellStyle cellStyle) {
      XSSFRow row = sheet.createRow(0);

      for(int i = 0; i < columNames.length; ++i) {
         XSSFCell cell = row.createCell(i);
         cell.setCellStyle(cellStyle);
         cell.setCellValue(columNames[i]);
      }

   }

   @RequestMapping(
      value = {"/exportExcleMore"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public void exportExcleMore(@RequestParam(value = "modelIds",required = false) String modelIds, ModelRequest modelRequest2, HttpServletResponse response, HttpServletRequest request) {
      try {
         modelRequest2.setPageNum(1);
         modelRequest2.setPageSize(10000);
         modelRequest2.setSystemId(this.commonService.getContextPath());
         modelRequest2.setUserId(BaseUtils.getUserId(request));
         modelRequest2.setDataSource(BaseUtils.getDataSource(request));
         String[] modelArr = modelIds.split(",");
         List<Long> modelIdList = new ArrayList();

         for(int i = 0; i < modelArr.length; ++i) {
            modelIdList.add(Long.valueOf(modelArr[i]));
         }

         Map<String, Object> result = this.modelService.searExportByModelId(modelRequest2, modelIdList);
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
         String time = simpleDateFormat.format(new Date());
         String excelName = "批量模型列表-" + time + ".xlsx";
         response.reset();
         response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelName, "UTF-8"));
         response.setContentType("application/vnd.ms-excel");
         XSSFWorkbook workbook = new XSSFWorkbook();
         XSSFSheet sheet = workbook.createSheet();
         XSSFCellStyle cellStyle = this.createCostomCellStyle(workbook, (short)32767);
         String[] columNames = new String[]{"模型名称", "状态", "新建时间", "上线时间字段", "上线数据范围", "准确率", "检出数", "上线进度", "模型备注", "最后修改人", "最后编辑时间"};
         String[] colums = new String[]{"modelName", "modelStatus", "createTime", "screeningRule", "screeningRule", "modelAccuracy", "modelCount", "onlineProgress", "modelComment", "modifierName", "updateTime"};
         this.createSheetLength(sheet, columNames.length);
         this.createColumnHead(sheet, columNames, cellStyle);
         List<HashMap<String, Object>> mapList = (List)result.get("rows");
         int hangRow = 1;
         if (mapList != null && mapList.size() > 0) {
            for(int i = 0; i < mapList.size(); ++i) {
               XSSFRow rowData = sheet.createRow(hangRow++);
               HashMap<String, Object> hashMap = (HashMap)mapList.get(i);
               String dataRange = "";

               for(int j = 0; j < colums.length; ++j) {
                  XSSFCell cell = rowData.createCell(j);
                  cell.setCellStyle(cellStyle);
                  String value = String.valueOf(hashMap.get(colums[j]));
                  switch (j) {
                     case 1:
                        int status = Integer.parseInt(value);
                        if (-4 == status) {
                           cell.setCellValue("上线失败");
                        } else if (-3 == status) {
                           cell.setCellValue("已下线");
                        } else if (-2 == status) {
                           cell.setCellValue("优化中");
                        } else if (-1 == status) {
                           cell.setCellValue("已上线");
                        } else if (0 == status) {
                           cell.setCellValue("上线中");
                        }
                        break;
                     case 2:
                     default:
                        cell.setCellValue(value);
                        break;
                     case 3:
                        String timeValue = "";
                        if (StringUtils.isNotNullAndEmpry(value) && !"[]".equals(value)) {
                           JSONArray jsonArray = JSON.parseArray(value);
                           if (jsonArray != null) {
                              Iterator iterator = jsonArray.iterator();

                              label103:
                              while(true) {
                                 while(true) {
                                    if (!iterator.hasNext()) {
                                       break label103;
                                    }

                                    JSONObject jsonObject = (JSONObject)iterator.next();
                                    JSONArray valueArray = (JSONArray)jsonObject.get("value");
                                    String name = (String)jsonObject.get("name");
                                    if ("timestamp".equals(jsonObject.get("key"))) {
                                       timeValue = valueArray.getString(0);
                                       String isOnlineStatus = String.valueOf(hashMap.get(colums[1]));
                                       if ("0".equals(isOnlineStatus) || "-1".equals(isOnlineStatus)) {
                                          String time2 = valueArray.getString(1);
                                          dataRange = dataRange + "起止时间:" + timeValue;
                                          if (StringUtils.isNullOrEmpry(time2)) {
                                             dataRange = dataRange + "~至今";
                                          } else {
                                             dataRange = dataRange + "~" + time2;
                                          }
                                       }

                                       dataRange = dataRange + "\n";
                                    } else {
                                       if (jsonObject.get("exclude").equals(true)) {
                                          dataRange = dataRange + name + ":!(";
                                       } else {
                                          dataRange = dataRange + name + ":";
                                       }

                                       for(int k = 0; k < valueArray.size(); ++k) {
                                          dataRange = dataRange + valueArray.get(k);
                                          if (k < valueArray.size() - 1) {
                                             dataRange = dataRange + ",";
                                          }
                                       }

                                       if (jsonObject.get("exclude").equals(true)) {
                                          dataRange = dataRange + ")\n";
                                       } else {
                                          dataRange = dataRange + "\n";
                                       }
                                    }
                                 }
                              }
                           }
                        }

                        cell.setCellValue(timeValue);
                        break;
                     case 4:
                        cell.setCellValue(dataRange);
                  }
               }
            }
         }

         ServletOutputStream out = response.getOutputStream();
         workbook.write(out);
         out.flush();
         out.close();
      } catch (Exception var34) {
         this.logger.error("模型列表页导出多个模型并压缩出错", var34);
      }

   }

   @RequestMapping(
      value = {"/exportExcleAll"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public void exportExcleAll(ModelRequest modelRequest2, HttpServletResponse response, HttpServletRequest request) {
      try {
         modelRequest2.setPageNum(1);
         modelRequest2.setPageSize(10000);
         modelRequest2.setSystemId(this.commonService.getContextPath());
         modelRequest2.setUserId(BaseUtils.getUserId(request));
         modelRequest2.setDataSource(BaseUtils.getDataSource(request));
         Map<String, Object> result = this.modelService.searModelByGroupService(modelRequest2);
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
         String time = simpleDateFormat.format(new Date());
         String excelName = "全量模型列表-" + time + ".xlsx";
         response.reset();
         response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelName, "UTF-8"));
         response.setContentType("application/vnd.ms-excel");
         XSSFWorkbook workbook = new XSSFWorkbook();
         XSSFSheet sheet = workbook.createSheet();
         XSSFCellStyle cellStyle = this.createCostomCellStyle(workbook, (short)32767);
         String[] columNames = new String[]{"模型名称", "状态", "新建时间", "上线时间字段", "上线数据范围", "准确率", "检出数", "上线进度", "模型备注", "最后修改人", "最后编辑时间"};
         String[] colums = new String[]{"modelName", "modelStatus", "createTime", "screeningRule", "screeningRule", "modelAccuracy", "modelCount", "onlineProgress", "modelComment", "modifierName", "updateTime"};
         this.createSheetLength(sheet, columNames.length);
         this.createColumnHead(sheet, columNames, cellStyle);
         List<HashMap<String, Object>> mapList = (List)result.get("rows");
         int hangRow = 1;
         if (mapList != null && mapList.size() > 0) {
            for(int i = 0; i < mapList.size(); ++i) {
               XSSFRow rowData = sheet.createRow(hangRow++);
               HashMap<String, Object> hashMap = (HashMap)mapList.get(i);
               String dataRange = "";

               for(int j = 0; j < colums.length; ++j) {
                  XSSFCell cell = rowData.createCell(j);
                  cell.setCellStyle(cellStyle);
                  String value = String.valueOf(hashMap.get(colums[j]));
                  switch (j) {
                     case 1:
                        int status = Integer.parseInt(value);
                        if (-4 == status) {
                           cell.setCellValue("上线失败");
                        } else if (-3 == status) {
                           cell.setCellValue("已下线");
                        } else if (-2 == status) {
                           cell.setCellValue("优化中");
                        } else if (-1 == status) {
                           cell.setCellValue("已上线");
                        } else if (0 == status) {
                           cell.setCellValue("上线中");
                        }
                        break;
                     case 2:
                     default:
                        cell.setCellValue(value);
                        break;
                     case 3:
                        String timeValue = "";
                        if (StringUtils.isNotNullAndEmpry(value) && !"[]".equals(value)) {
                           JSONArray jsonArray = JSON.parseArray(value);
                           if (jsonArray != null) {
                              Iterator iterator = jsonArray.iterator();

                              label93:
                              while(true) {
                                 while(true) {
                                    if (!iterator.hasNext()) {
                                       break label93;
                                    }

                                    JSONObject jsonObject = (JSONObject)iterator.next();
                                    JSONArray valueArray = (JSONArray)jsonObject.get("value");
                                    String name = (String)jsonObject.get("name");
                                    if ("timestamp".equals(jsonObject.get("key"))) {
                                       String isOnlineStatus = String.valueOf(hashMap.get(colums[1]));
                                       timeValue = valueArray.getString(0);
                                       if ("0".equals(isOnlineStatus) || "-1".equals(isOnlineStatus)) {
                                          String time2 = valueArray.getString(1);
                                          dataRange = dataRange + "起止时间:" + timeValue;
                                          if (StringUtils.isNullOrEmpry(time2)) {
                                             dataRange = dataRange + "~至今";
                                          } else {
                                             dataRange = dataRange + "~" + time2;
                                          }
                                       }

                                       dataRange = dataRange + "\n";
                                    } else {
                                       dataRange = dataRange + name + ":";

                                       for(int k = 0; k < valueArray.size(); ++k) {
                                          dataRange = dataRange + valueArray.get(k);
                                          if (k < valueArray.size() - 1) {
                                             dataRange = dataRange + ",";
                                          }
                                       }

                                       dataRange = dataRange + "\n";
                                    }
                                 }
                              }
                           }
                        }

                        cell.setCellValue(timeValue);
                        break;
                     case 4:
                        cell.setCellValue(dataRange);
                  }
               }
            }
         }

         ServletOutputStream out = response.getOutputStream();
         workbook.write(out);
         out.flush();
         out.close();
      } catch (Exception var31) {
         this.logger.error("模型列表页导出所有模型并压缩出错", var31);
      }

   }

   public void exportSilenceRule(String sileneceText, int count, XSSFWorkbook workbook, XSSFSheet sheet) {
      XSSFFont titleFont = workbook.createFont();
      XSSFCellStyle titleStyle = workbook.createCellStyle();
      titleStyle.setAlignment((short)2);
      titleFont.setBoldweight((short)700);
      titleFont.setFontName("宋体");
      titleFont.setFontHeightInPoints((short)11);
      titleStyle.setFont(titleFont);
      titleStyle.setBorderBottom((short)1);
      titleStyle.setBorderTop((short)1);
      titleStyle.setBorderRight((short)1);
      titleStyle.setBorderLeft((short)1);
      XSSFRow rowData1 = sheet.createRow(count);
      this.changeFontStyle(rowData1, titleStyle, 0, "序号");
      this.changeFontStyle(rowData1, titleStyle, 1, "对象");
      this.changeFontStyle(rowData1, titleStyle, 2, "类型");
      this.changeFontStyle(rowData1, titleStyle, 3, "属性");
      this.changeFontStyle(rowData1, titleStyle, 4, "依赖对象");
      this.changeFontStyle(rowData1, titleStyle, 5, "逻辑关系");
      this.changeFontStyle(rowData1, titleStyle, 6, "数值");
      if (!StringUtils.isNullOrEmpry(sileneceText)) {
         LinkedHashMap<String, SilenceText> ruleNameMap = new LinkedHashMap();
         JSONObject jsonObject = JSON.parseObject(sileneceText);
         JSONArray jsonArray = jsonObject.getJSONArray("condition");
         Iterator<Object> textIterator = jsonArray.iterator();

         while(textIterator.hasNext()) {
            JSONObject condition = JSONObject.parseObject(String.valueOf(textIterator.next()));
            SilenceText silenceText1 = new SilenceText();
            String ruleId = condition.get("id") + "";
            silenceText1.setId(ruleId);
            ruleNameMap.put(ruleId, silenceText1);
            silenceText1.setName(condition.get("name") + "");
            silenceText1.setDimensionCode(condition.get("dimensionCode") + "");
            silenceText1.setDimensionName(condition.get("dimensionName") + "");
            silenceText1.setOptions(this.createProperties(condition.get("options") + ""));
         }

         XSSFFont silenceFont = workbook.createFont();
         XSSFCellStyle silenceStyle = workbook.createCellStyle();
         silenceFont.setFontName("宋体");
         silenceStyle.setFont(silenceFont);
         silenceStyle.setBorderBottom((short)1);
         silenceStyle.setBorderTop((short)1);
         silenceStyle.setBorderRight((short)1);
         silenceStyle.setBorderLeft((short)1);
         Iterator iterator = ruleNameMap.keySet().iterator();
         int startHeng = 1 + count;

         for(int currentRule = 1; iterator.hasNext(); ++currentRule) {
            String ruleId = iterator.next() + "";
            SilenceText st = (SilenceText)ruleNameMap.get(ruleId);
            List<PropertyText> proterties = st.getOptions();
            int ruleSize = proterties.size();

            for(int i = 0; i < proterties.size(); ++i) {
               PropertyText pt = (PropertyText)proterties.get(i);
               XSSFRow eachRule = sheet.createRow(startHeng + i);
               this.changeFontStyle(eachRule, silenceStyle, 0, currentRule + "");
               if (i == 0) {
                  XSSFCell xc1 = eachRule.createCell(1);
                  sheet.addMergedRegion(new CellRangeAddress(startHeng, ruleSize + startHeng - 1, 1, 1));
                  xc1.setCellStyle(titleStyle);
                  xc1.setCellValue(st.getName());
                  XSSFCell xc2 = eachRule.createCell(2);
                  sheet.addMergedRegion(new CellRangeAddress(startHeng, ruleSize + startHeng - 1, 2, 2));
                  xc2.setCellStyle(titleStyle);
                  xc2.setCellValue(st.getDimensionName());
               }

               this.changeFontStyle(eachRule, silenceStyle, 3, pt.getPropertyName());
               String relativeName = StringUtils.isNullOrEmpry(pt.getRelativeName()) ? "无" : pt.getRelativeName();
               this.changeFontStyle(eachRule, silenceStyle, 4, relativeName);
               this.changeFontStyle(eachRule, silenceStyle, 5, pt.getOperationName());
               this.changeFontStyle(eachRule, silenceStyle, 6, pt.getInputValue() + "");
            }

            startHeng += ruleSize;
         }

      }
   }

   private List<PropertyText> createProperties(String options) {
      List<PropertyText> propertyTextList = new ArrayList();
      JSONArray jsonArray = JSONArray.parseArray(options);
      Iterator<Object> textIterator = jsonArray.iterator();

      while(textIterator.hasNext()) {
         JSONObject option = JSONObject.parseObject(textIterator.next() + "");
         PropertyText property = new PropertyText();
         propertyTextList.add(property);
         String propertyName = option.getString("propertyName");
         property.setPropertyName(propertyName);
         if (propertyName.contains("声道")) {
            property.setOperationName(option.getString("equOperation"));
            property.setInputValue(option.getString("operationName"));
            property.setOperationCode(option.getString("equOperationCode"));
         } else {
            property.setOperationName(option.getString("operationName"));
            property.setOperationCode(option.getString("operationCode"));
            property.setInputValue(option.get("inputValue"));
         }

         property.setRelativeName(option.getString("reletionName"));
         property.setRelativeCode(option.getString("relativeobject"));
         property.setPropertyCode(option.getString("propertyCode"));
         property.setFlag(option.getInteger("flag"));
         property.setIsDepend(option.getInteger("isDepend"));
      }

      return propertyTextList;
   }

   private void changeFontStyle(XSSFRow row, XSSFCellStyle titleStyle, int cellIndex, String cellValue) {
      XSSFCell xc = row.createCell(cellIndex);
      xc.setCellStyle(titleStyle);
      xc.setCellValue(cellValue);
   }

   @RequestMapping({"exportSingleRule"})
   @ResponseBody
   public ResponseResult exportSingleRule(HttpServletRequest request, HttpServletResponse response, ModelRequest modelRequest) {
      try {
         modelRequest.setSystemId(BaseUtils.getUserId(request));
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.searchModelInfoService(modelRequest);
         ModelDetailDTO modelDetailDTO = (ModelDetailDTO)serviceResponse.getValue();
         List<ModelFragment> modelFragments = modelDetailDTO.getModelFragments();
         HashMap<Integer, ModelRule> ruleMap = new HashMap();
         String fragmentContent = modelDetailDTO.getModelFragmentRelation().getFragmentContent();
         Iterator var9 = modelFragments.iterator();

         while(var9.hasNext()) {
            ModelFragment mf = (ModelFragment)var9.next();
            if (mf.getRuleType() != 2) {
               ModelRule modelRule = new ModelRule();
               modelRule.setChannel(mf.getChannel());
               modelRule.setFragmentContent(mf.getFragmentContent());
               modelRule.setFragmentNum(mf.getFragmentNum());
               modelRule.setRemark(mf.getRemark());
               modelRule.setCondRule("");
               ruleMap.put(modelRule.getFragmentNum(), modelRule);
            }
         }

         Object[] objects = ruleMap.keySet().toArray();
         Arrays.sort(objects);
         String excelName = modelDetailDTO.getModelName() + ".xlsx";
         String zipName = modelDetailDTO.getModelName();
         File zip = new File(zipName + ".zip");
         List<String> fileNames = new ArrayList();
         fileNames.add(excelName);
         FileOutputStream o = new FileOutputStream(excelName);
         response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipName, "UTF-8") + ".zip");
         XSSFWorkbook workbook = new XSSFWorkbook();
         XSSFSheet sheet = null;
         XSSFRow rowData = null;
         XSSFFont fontOne = workbook.createFont();
         XSSFFont fontOne1 = workbook.createFont();
         XSSFFont fontOne2 = workbook.createFont();
         XSSFFont fontTwo = workbook.createFont();
         XSSFFont fontThree = workbook.createFont();
         XSSFFont fontFour = workbook.createFont();
         fontOne.setBoldweight((short)700);
         XSSFCellStyle hcsOne = workbook.createCellStyle();
         hcsOne.setAlignment((short)2);
         fontOne.setFontName("宋体");
         fontOne.setFontHeightInPoints((short)11);
         hcsOne.setFont(fontOne);
         XSSFCellStyle hcsOne2 = workbook.createCellStyle();
         hcsOne2.setAlignment((short)2);
         fontOne2.setFontName("宋体");
         fontOne2.setFontHeightInPoints((short)11);
         hcsOne2.setFont(fontOne2);
         XSSFCellStyle hcsOne1 = workbook.createCellStyle();
         hcsOne1.setAlignment((short)2);
         fontOne1.setBoldweight((short)700);
         fontOne1.setFontName("宋体");
         fontOne1.setFontHeightInPoints((short)11);
         hcsOne1.setFont(fontOne);
         hcsOne1.setBorderBottom((short)1);
         hcsOne1.setBorderTop((short)1);
         hcsOne1.setBorderRight((short)1);
         hcsOne1.setBorderLeft((short)1);
         XSSFCellStyle hcsTwo = workbook.createCellStyle();
         hcsTwo.setAlignment((short)2);
         fontTwo.setFontName("宋体");
         fontTwo.setFontHeightInPoints((short)11);
         hcsTwo.setFont(fontTwo);
         hcsTwo.setBorderBottom((short)1);
         hcsTwo.setBorderTop((short)1);
         hcsTwo.setBorderRight((short)1);
         hcsTwo.setBorderLeft((short)1);
         XSSFCellStyle hcsThree = workbook.createCellStyle();
         hcsThree.setAlignment((short)1);
         fontThree.setFontName("宋体");
         fontThree.setFontHeightInPoints((short)11);
         hcsThree.setFont(fontThree);
         hcsThree.setBorderBottom((short)1);
         hcsThree.setBorderTop((short)1);
         hcsThree.setBorderRight((short)1);
         hcsThree.setBorderLeft((short)1);
         XSSFCellStyle hcsFour = workbook.createCellStyle();
         hcsFour.setAlignment((short)3);
         fontFour.setFontName("宋体");
         fontFour.setFontHeightInPoints((short)11);
         fontFour.setColor(new XSSFColor(new Color(245, 245, 245)));
         hcsFour.setFont(fontFour);
         hcsFour.setBorderBottom((short)1);
         hcsFour.setBorderTop((short)1);
         hcsFour.setBorderRight((short)1);
         hcsFour.setBorderLeft((short)1);
         XSSFCellStyle hcsFive = workbook.createCellStyle();
         hcsThree.setAlignment((short)1);
         fontThree.setFontName("宋体");
         fontThree.setFontHeightInPoints((short)11);
         hcsFive.setFont(fontThree);
         hcsFive.setBorderBottom((short)1);
         hcsFive.setBorderTop((short)1);
         hcsFive.setBorderRight((short)1);
         hcsFive.setBorderLeft((short)1);
         hcsFive.setLocked(true);
         int count = ruleMap.size();
         sheet = workbook.createSheet();
         sheet.setColumnWidth(0, 3840);
         sheet.setColumnWidth(1, 2304);
         sheet.setColumnWidth(2, 2560);
         sheet.setColumnWidth(4, 2304);
         sheet.setColumnWidth(5, 2304);
         sheet.setColumnWidth(6, 2304);

         int i;
         XSSFCell cell2;
         XSSFCell cell3;
         XSSFCell cell4;
         XSSFCell cell5;
         for(i = 0; i < 9; ++i) {
            rowData = sheet.createRow(i);
            XSSFCell cell = rowData.createCell(0);
            XSSFCell cell1;
            if (i == 0) {
               cell1 = rowData.createCell(0);
               cell1.setCellValue("模型名称");
               cell1.setCellStyle(hcsOne);
               cell2 = rowData.createCell(1);
               sheet.addMergedRegion(new CellRangeAddress(i, 0, 1, 4));
               cell2.setCellValue(modelDetailDTO.getModelName());
               cell2.setCellStyle(hcsOne2);
            } else if (i == 1) {
               cell1 = rowData.createCell(0);
               cell1.setCellStyle(hcsOne);
               cell1.setCellValue("所属模型组");
               cell2 = rowData.createCell(1);
               sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 1, 4));
               cell2.setCellValue(modelDetailDTO.getGroupName());
               cell2.setCellStyle(hcsOne2);
            } else if (i == 2) {
               cell1 = rowData.createCell(0);
               cell1.setCellStyle(hcsOne);
               cell1.setCellValue("备注");
               cell2 = rowData.createCell(1);
               sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 1, 4));
               cell2.setCellValue(modelDetailDTO.getModelComment());
               cell2.setCellStyle(hcsOne2);
            } else if (i == 4) {
               cell1 = rowData.createCell(0);
               cell1.setCellStyle(hcsOne);
               cell1.setCellValue("规则组合");
               cell2 = rowData.createCell(1);
               sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 1, 4));
               cell2.setCellValue(fragmentContent);
               cell2.setCellStyle(hcsOne2);
            } else if (i == 6) {
               cell.setCellStyle(hcsOne);
               cell.setCellValue("模型规则");
            } else if (i == 7) {
               cell1 = rowData.createCell(0);
               cell1.setCellStyle(hcsOne1);
               cell1.setCellValue("序号");
               cell2 = rowData.createCell(1);
               cell2.setCellStyle(hcsOne1);
               cell2.setCellValue("规则");
               cell3 = rowData.createCell(2);
               cell3.setCellStyle(hcsOne1);
               cell3.setCellValue("声道");
               cell4 = rowData.createCell(3);
               cell4.setCellStyle(hcsOne1);
               cell4.setCellValue("备注");
               cell5 = rowData.createCell(4);
               cell5.setCellStyle(hcsOne1);
               cell5.setCellValue("");
            }
         }

         for(i = 0; i < count; ++i) {
            ModelRule mr = (ModelRule)ruleMap.get(objects[i]);
            String ch = "";
            switch (mr.getChannel()) {
               case 0:
                  ch = "坐席";
                  break;
               case 1:
                  ch = "客户";
                  break;
               case 2:
                  ch = "全部";
            }

            if (!StringUtils.isNullOrEmpry(mr.getCondRule())) {
               ch = "-";
            }

            rowData = sheet.createRow(i + 8);
            cell2 = rowData.createCell(2);
            cell2.setCellStyle(hcsThree);
            cell2.setCellValue(ch);
            cell3 = rowData.createCell(0);
            cell3.setCellStyle(hcsTwo);
            cell3.setCellValue((double)(Integer)objects[i]);
            cell4 = rowData.createCell(1);
            cell4.setCellValue(mr.getFragmentContent());
            if (StringUtils.isNullOrEmpry(mr.getCondRule())) {
               cell4.setCellStyle(hcsThree);
            }

            cell4.setCellStyle(hcsFive);
            cell5 = rowData.createCell(3);
            if (StringUtils.isNullOrEmpry(mr.getRemark())) {
               mr.setRemark("");
            }

            cell5.setCellValue(mr.getRemark());
            cell5.setCellStyle(hcsThree);
            XSSFCell condCell = rowData.createCell(4);
            condCell.setCellValue(mr.getCondRule());
            condCell.setCellStyle(hcsFour);
         }

         String sileneceText = modelDetailDTO.getSilenceText();
         XSSFSheet sheet1 = workbook.createSheet();
         this.createSheetLength(sheet1, 7);
         this.exportSilenceRule(sileneceText, 0, workbook, sheet1);
         workbook.write(o);
         OutputStream out = response.getOutputStream();
         File[] srcfile = new File[fileNames.size()];
         int fileIndex = 0;

         for(int n1 = fileNames.size(); fileIndex < n1; ++fileIndex) {
            srcfile[fileIndex] = new File((String)fileNames.get(fileIndex));
         }

         this.ZipFiles(srcfile, zip);
         FileInputStream inStream = new FileInputStream(zip);
         byte[] buf = new byte[4096];

         int readLength;
         while((readLength = inStream.read(buf)) != -1) {
            out.write(buf, 0, readLength);
         }

         inStream.close();

         for(int index = 0; index < srcfile.length; ++index) {
            srcfile[index].delete();
         }

         zip.delete();
         return ResponseResult.success("操作成功!");
      } catch (Exception var40) {
         this.logger.error("模型列表页导出单个模型出错", var40);
         return ResponseResult.error(var40.getMessage());
      }
   }

   @RequestMapping(
      value = {"/exportMoreRule"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public void exportMoreRule(@RequestParam("modelId") String modelIds, HttpServletResponse response, HttpServletRequest request) {
      try {
         String[] modelArr = modelIds.split(",");
         List<Long> modelIdList = new ArrayList();

         for(int i = 0; i < modelArr.length; ++i) {
            modelIdList.add(Long.valueOf(modelArr[i]));
         }

         String zipName = "模型批量导出";
         response.reset();
         File zip = new File(zipName + ".zip");
         response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipName, "UTF-8") + ".zip");
         response.setContentType("application/octet-stream");
         List<String> fileNames = new ArrayList();
         ModelRequest modelRequest = new ModelRequest();
         modelRequest.setModelIds(modelIdList);
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelService.searchModelInfoList(modelRequest);
         Map<Long, Map<String, Object>> resultMap = (Map)serviceResponse.getValue();
         Iterator var12 = modelIdList.iterator();

         while(true) {
            Long modelId;
            do {
               if (!var12.hasNext()) {
                  OutputStream out = response.getOutputStream();
                  File[] srcfile = new File[fileNames.size()];
                  int fileIndex = 0;

                  for(int n1 = fileNames.size(); fileIndex < n1; ++fileIndex) {
                     srcfile[fileIndex] = new File((String)fileNames.get(fileIndex));
                  }

                  this.ZipFiles(srcfile, zip);
                  FileInputStream inStream = new FileInputStream(zip);
                  byte[] buf = new byte[4096];

                  int readLength;
                  while((readLength = inStream.read(buf)) != -1) {
                     out.write(buf, 0, readLength);
                  }

                  inStream.close();

                  for(int index = 0; index < srcfile.length; ++index) {
                     srcfile[index].delete();
                  }

                  zip.delete();
                  return;
               }

               modelId = (Long)var12.next();
            } while(!resultMap.containsKey(modelId));

            Map<String, Object> map = (Map)resultMap.get(modelId);
            ModelInfo model = (ModelInfo)map.get("modelInfo");
            List<ModelFragment> modelFragments = (List)map.get("modelFragments");
            ModelGroup modelGroup = (ModelGroup)map.get("modelGroup");
            String file = model.getModelName() + "." + System.currentTimeMillis() + ".xlsx";
            fileNames.add(file);
            FileOutputStream o = new FileOutputStream(file);
            HashMap<Integer, ModelRule> ruleMap = new HashMap();
            String fragmentContent = "";
            Iterator var22 = modelFragments.iterator();

            ModelRule modelRule;
            while(var22.hasNext()) {
               ModelFragment mf = (ModelFragment)var22.next();
               if (mf.getRuleType() != 2) {
                  modelRule = new ModelRule();
                  modelRule.setChannel(mf.getChannel());
                  modelRule.setFragmentContent(mf.getFragmentContent());
                  modelRule.setFragmentNum(mf.getFragmentNum());
                  modelRule.setRemark(mf.getRemark());
                  modelRule.setCondRule("");
                  ruleMap.put(modelRule.getFragmentNum(), modelRule);
               } else {
                  fragmentContent = mf.getFragmentContent();
               }
            }

            Object[] objects = ruleMap.keySet().toArray();
            Arrays.sort(objects);
            XSSFWorkbook workbook = new XSSFWorkbook();
            modelRule = null;
            XSSFRow rowData = null;
            XSSFFont fontOne = workbook.createFont();
            XSSFFont fontOne1 = workbook.createFont();
            XSSFFont fontOne2 = workbook.createFont();
            XSSFFont fontTwo = workbook.createFont();
            XSSFFont fontThree = workbook.createFont();
            XSSFFont fontFour = workbook.createFont();
            fontOne.setBoldweight((short)700);
            XSSFCellStyle hcsOne = workbook.createCellStyle();
            hcsOne.setAlignment((short)2);
            fontOne.setFontName("宋体");
            fontOne.setFontHeightInPoints((short)11);
            hcsOne.setFont(fontOne);
            XSSFCellStyle hcsOne2 = workbook.createCellStyle();
            hcsOne2.setAlignment((short)2);
            fontOne2.setFontName("宋体");
            fontOne2.setFontHeightInPoints((short)11);
            hcsOne2.setFont(fontOne2);
            XSSFCellStyle hcsOne1 = workbook.createCellStyle();
            hcsOne1.setAlignment((short)2);
            fontOne1.setBoldweight((short)700);
            fontOne1.setFontName("宋体");
            fontOne1.setFontHeightInPoints((short)11);
            hcsOne1.setFont(fontOne);
            hcsOne1.setBorderBottom((short)1);
            hcsOne1.setBorderTop((short)1);
            hcsOne1.setBorderRight((short)1);
            hcsOne1.setBorderLeft((short)1);
            XSSFCellStyle hcsTwo = workbook.createCellStyle();
            hcsTwo.setAlignment((short)2);
            fontTwo.setFontName("宋体");
            fontTwo.setFontHeightInPoints((short)11);
            hcsTwo.setFont(fontTwo);
            hcsTwo.setBorderBottom((short)1);
            hcsTwo.setBorderTop((short)1);
            hcsTwo.setBorderRight((short)1);
            hcsTwo.setBorderLeft((short)1);
            XSSFCellStyle hcsThree = workbook.createCellStyle();
            hcsThree.setAlignment((short)1);
            fontThree.setFontName("宋体");
            fontThree.setFontHeightInPoints((short)11);
            hcsThree.setFont(fontThree);
            hcsThree.setBorderBottom((short)1);
            hcsThree.setBorderTop((short)1);
            hcsThree.setBorderRight((short)1);
            hcsThree.setBorderLeft((short)1);
            XSSFCellStyle hcsFour = workbook.createCellStyle();
            hcsFour.setAlignment((short)3);
            fontFour.setFontName("宋体");
            fontFour.setFontHeightInPoints((short)11);
            fontFour.setColor(new XSSFColor(new Color(245, 245, 245)));
            hcsFour.setFont(fontFour);
            hcsFour.setBorderBottom((short)1);
            hcsFour.setBorderTop((short)1);
            hcsFour.setBorderRight((short)1);
            hcsFour.setBorderLeft((short)1);
            XSSFCellStyle hcsFive = workbook.createCellStyle();
            hcsThree.setAlignment((short)1);
            fontThree.setFontName("宋体");
            fontThree.setFontHeightInPoints((short)11);
            hcsFive.setFont(fontThree);
            hcsFive.setBorderBottom((short)1);
            hcsFive.setBorderTop((short)1);
            hcsFive.setBorderRight((short)1);
            hcsFive.setBorderLeft((short)1);
            hcsFive.setLocked(true);
            int count = ruleMap.size();
            XSSFSheet sheet = workbook.createSheet();
            sheet.setColumnWidth(0, 3840);
            sheet.setColumnWidth(1, 2304);
            sheet.setColumnWidth(2, 2560);
            sheet.setColumnWidth(4, 2304);
            sheet.setColumnWidth(5, 2304);
            sheet.setColumnWidth(6, 2304);

            int i;
            XSSFCell cell2;
            XSSFCell cell3;
            XSSFCell cell4;
            XSSFCell cell5;
            for(i = 0; i < 9; ++i) {
               rowData = sheet.createRow(i);
               XSSFCell cell = rowData.createCell(0);
               XSSFCell cell1;
               if (i == 0) {
                  cell1 = rowData.createCell(0);
                  cell1.setCellValue("模型名称");
                  cell1.setCellStyle(hcsOne);
                  cell2 = rowData.createCell(1);
                  sheet.addMergedRegion(new CellRangeAddress(i, 0, 1, 4));
                  cell2.setCellValue(model.getModelName());
                  cell2.setCellStyle(hcsOne2);
               } else if (i == 1) {
                  cell1 = rowData.createCell(0);
                  cell1.setCellStyle(hcsOne);
                  cell1.setCellValue("所属模型组");
                  cell2 = rowData.createCell(1);
                  sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 1, 4));
                  cell2.setCellValue(modelGroup.getGroupName());
                  cell2.setCellStyle(hcsOne2);
               } else if (i == 2) {
                  cell1 = rowData.createCell(0);
                  cell1.setCellStyle(hcsOne);
                  cell1.setCellValue("备注");
                  cell2 = rowData.createCell(1);
                  sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 1, 4));
                  cell2.setCellValue(model.getModelComment());
                  cell2.setCellStyle(hcsOne2);
               } else if (i == 4) {
                  cell1 = rowData.createCell(0);
                  cell1.setCellStyle(hcsOne);
                  cell1.setCellValue("规则组合");
                  cell2 = rowData.createCell(1);
                  sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 1, 4));
                  cell2.setCellValue(fragmentContent);
                  cell2.setCellStyle(hcsOne2);
               } else if (i == 6) {
                  cell.setCellStyle(hcsOne);
                  cell.setCellValue("模型规则");
               } else if (i == 7) {
                  cell1 = rowData.createCell(0);
                  cell1.setCellStyle(hcsOne1);
                  cell1.setCellValue("序号");
                  cell2 = rowData.createCell(1);
                  cell2.setCellStyle(hcsOne1);
                  cell2.setCellValue("规则");
                  cell3 = rowData.createCell(2);
                  cell3.setCellStyle(hcsOne1);
                  cell3.setCellValue("声道");
                  cell4 = rowData.createCell(3);
                  cell4.setCellStyle(hcsOne1);
                  cell4.setCellValue("备注");
                  cell5 = rowData.createCell(4);
                  cell5.setCellStyle(hcsOne1);
                  cell5.setCellValue("");
               }
            }

            for(i = 0; i < count; ++i) {
               ModelRule mr = (ModelRule)ruleMap.get(objects[i]);
               String ch = "";
               switch (mr.getChannel()) {
                  case 0:
                     ch = "坐席";
                     break;
                  case 1:
                     ch = "客户";
                     break;
                  case 2:
                     ch = "全部";
               }

               if (!StringUtils.isNullOrEmpry(mr.getCondRule())) {
                  ch = "-";
               }

               rowData = sheet.createRow(i + 8);
               cell2 = rowData.createCell(2);
               cell2.setCellStyle(hcsThree);
               cell2.setCellValue(ch);
               cell3 = rowData.createCell(0);
               cell3.setCellStyle(hcsTwo);
               cell3.setCellValue((double)(Integer)objects[i]);
               cell4 = rowData.createCell(1);
               cell4.setCellValue(mr.getFragmentContent());
               if (StringUtils.isNullOrEmpry(mr.getCondRule())) {
                  cell4.setCellStyle(hcsThree);
               }

               cell4.setCellStyle(hcsFive);
               cell5 = rowData.createCell(3);
               if (StringUtils.isNullOrEmpry(mr.getRemark())) {
                  mr.setRemark("");
               }

               cell5.setCellValue(mr.getRemark());
               cell5.setCellStyle(hcsThree);
               XSSFCell condCell = rowData.createCell(4);
               condCell.setCellValue(mr.getCondRule());
               condCell.setCellStyle(hcsFour);
            }

            String sileneceText = model.getSilenceText();
            XSSFSheet sheet1 = workbook.createSheet();
            this.createSheetLength(sheet1, 7);
            this.exportSilenceRule(sileneceText, 0, workbook, sheet1);
            workbook.write(o);
         }
      } catch (Exception var48) {
         this.logger.error("模型列表页导出多个模型并压缩出错", var48);
      }
   }

   private void createSheetLength(Sheet sheet1, int length) {
      for(int i = 0; i < length; ++i) {
         sheet1.setColumnWidth(i, 2560);
      }

   }

   private void ZipFiles(File[] srcfile, File zipfile) throws Exception {
      byte[] buf = new byte[1024];

      try {
         ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));

         for(int i = 0; i < srcfile.length; ++i) {
            FileInputStream in = new FileInputStream(srcfile[i]);
            out.putNextEntry(new ZipEntry(this.getRealFileName(srcfile[i].getName())));

            int len;
            while((len = in.read(buf)) > 0) {
               out.write(buf, 0, len);
            }

            out.closeEntry();
            in.close();
         }

         out.close();
      } catch (IOException var8) {
         throw var8;
      }
   }

   private String getRealFileName(String name) {
      String realName = null;
      if (StringUtils.isNotNullAndEmpry(name)) {
         realName = name.substring(0, name.indexOf(".")) + ".xlsx";
      }

      return realName;
   }

   @RequestMapping(
      value = {"importRule"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult importRule(HttpServletRequest request) {
      this.logger.info("importRule is begin");

      try {
         String searchType = LoadConfig.getConfigProperty("searchType");
         String fileDir = request.getSession().getServletContext().getRealPath("/uploadFiles");
         File fileTemp = new File(fileDir);
         if (fileTemp.listFiles().length == 0) {
            return ResponseResult.error("文件被删除！");
         } else {
            File file = fileTemp.listFiles()[0];
            InputStream input = new FileInputStream(fileDir + "/" + file.getName());
            Workbook wb = null;
            Sheet sheet = null;
            if (file.getName().endsWith("xls")) {
               wb = new HSSFWorkbook(input);
               sheet = ((Workbook)wb).getSheetAt(0);
            } else if (file.getName().endsWith("xlsx")) {
               wb = new XSSFWorkbook(input);
               sheet = ((Workbook)wb).getSheetAt(0);
            }

            this.logger.info("文件类型校验通过");
            HashMap<Object, Object> hm = new HashMap();
            List<HashMap<Object, Object>> ruleList = new ArrayList();
            Iterator rows;
            if (sheet != null && sheet.getRow(0) != null && sheet.getRow(1) != null) {
               rows = sheet.rowIterator();
               int i = 0;
               int preNum = 0;
               boolean templateType = false;
               int tempFlag = 0;
               boolean isOrderStatus = false;

               for(boolean startReadModelRule = false; rows.hasNext(); ++i) {
                  Row row = (Row)rows.next();
                  Iterator<Cell> cells = row.cellIterator();
                  Cell cell1;
                  String cellValue1;
                  if (i == 0) {
                     if (cells.hasNext()) {
                        cell1 = (Cell)cells.next();
                        cellValue1 = this.judgeCellType(cell1);
                        if (StringUtils.isNullOrEmpry(cellValue1)) {
                           return ResponseResult.error("导入模板有误！");
                        }

                        if (!"规则组合".equals(cellValue1.trim())) {
                           templateType = true;
                        }

                        hm.put("fragmentContent", cellValue1);
                     }
                  } else if (i == 1 && !templateType) {
                     if (cells.hasNext()) {
                        cell1 = (Cell)cells.next();
                        cellValue1 = this.judgeCellType(cell1);
                        if (StringUtils.isNullOrEmpry(cellValue1)) {
                           return ResponseResult.error("规则组合不能为空！");
                        }

                        if (cellValue1.contains("@")) {
                           isOrderStatus = true;
                        }

                        hm.put("fragmentContent", cellValue1);
                     }
                  } else {
                     for(; cells.hasNext(); ++tempFlag) {
                        if (!startReadModelRule) {
                           cell1 = (Cell)cells.next();
                           cellValue1 = this.judgeCellType(cell1);
                           if ("规则组合".equals(cellValue1) && templateType) {
                              if (cells.hasNext()) {
                                 Cell cell2 = (Cell)cells.next();
                                 String cellValue2 = this.judgeCellType(cell2);
                                 if (StringUtils.isNullOrEmpry(cellValue2)) {
                                    return ResponseResult.error("规则组合不能为空！");
                                 }

                                 hm.put("fragmentContent", cellValue2);
                              }
                              break;
                           }

                           if ("序号".equals(cellValue1)) {
                              startReadModelRule = true;
                           }
                           break;
                        }

                        if (isOrderStatus && tempFlag >= 5) {
                           return ResponseResult.error("顺序模型导入片段不可超过5个！");
                        }

                        HashMap<Object, Object> rulehm = new HashMap();
                        Cell numCell = (Cell)cells.next();
                        String numCellValue = this.judgeCellType(numCell);

                        try {
                           rulehm.put("fragmentNum", Integer.parseInt(String.valueOf(numCellValue)) - preNum);
                           if (Integer.parseInt(numCellValue) < 1) {
                              return ResponseResult.error("EXCEL第" + (i + 1) + "行序号不能小于1！");
                           }
                        } catch (Exception var37) {
                           return ResponseResult.error("EXCEL第" + (i + 1) + "行序号不能为其他类型！");
                        }

                        Cell ruleCell = (Cell)cells.next();
                        String ruleCellValue = this.judgeCellType(ruleCell);
                        rulehm.put("fragmentContent", ruleCellValue);
                        Cell channelCell = (Cell)cells.next();
                        int ch = -1;
                        String channelCellValue = this.judgeCellType(channelCell);
                        channelCellValue = channelCellValue.replace(" ", "");
                        boolean flag = false;
                        switch (channelCellValue) {
                           case "全部":
                              ch = 2;
                              break;
                           case "坐席":
                              ch = 0;
                              break;
                           case "客户":
                              ch = 1;
                              break;
                           case "-":
                              ch = 2;
                              flag = true;
                        }

                        if (ch == -1) {
                           return ResponseResult.error("EXCEL第" + (i + 1) + "行声道类型错误！");
                        }

                        rulehm.put("channel", Integer.valueOf(ch));
                        Cell remarkCell = (Cell)cells.next();
                        String remarkCellValue = this.judgeCellType(remarkCell);
                        rulehm.put("remark", remarkCellValue);
                        Cell condCell = null;

                        try {
                           if (cells.hasNext()) {
                              condCell = (Cell)cells.next();
                           }
                        } catch (Exception var35) {
                           return ResponseResult.error("EXCEL缺少参数列！");
                        }

                        String condCellValue = this.judgeCellType(condCell);
                        rulehm.put("condRule", condCellValue);
                        if (StringUtils.isNullOrEmpry(channelCellValue) || StringUtils.isNullOrEmpry(ruleCellValue) || StringUtils.isNullOrEmpry(numCellValue)) {
                           return ResponseResult.error("EXCEL第" + (i + 1) + "行存在空值！");
                        }

                        int ist = 0;
                        if (!StringUtils.isNullOrEmpry(condCellValue)) {
                           ist = 1;
                        }

                        if (!flag && ist == 1) {
                           return ResponseResult.error("EXCEL第" + (i + 1) + "行声道设置有误！");
                        }

                        if (flag && ist == 0) {
                           return ResponseResult.error("EXCEL第" + (i + 1) + "行声道设置有误！");
                        }

                        Long fragId = this.addModelFragment(request, ruleCellValue, numCellValue, ch, ist, condCellValue, "", remarkCellValue);
                        rulehm.put("fragmentId", fragId);
                        rulehm.put("isTag", Integer.valueOf(ist));
                        if (searchType.equals("1") && !StringUtils.isNullOrEmpry(condCellValue)) {
                           return ResponseResult.error("该分词模式下,不得导入条件规则！");
                        }

                        if (rulehm.size() != 0) {
                           ruleList.add(rulehm);
                        }
                     }
                  }
               }

               hm.put("modelFragments", ruleList);
            }

            hm.put("modelFragments", ruleList);
            rows = null;

            try {
               Sheet sheet1 = ((Workbook)wb).getSheetAt(1);
               if (sheet1 != null && sheet1.getRow(0) != null && sheet1.getRow(1) != null) {
                  String[] silenceStr = this.getImportSilenceRule(request, sheet1);
                  hm.put("silenceText", silenceStr[0]);
                  String error = silenceStr[1];
                  hm.put("silenceSuccess", StringUtils.isNullOrEmpry(error));
                  hm.put("silenceErrorReason", error);
               } else {
                  HashMap<String, Object> silenTextMap = new HashMap();
                  List conditions = new ArrayList();
                  silenTextMap.put("mentId", (int)(Math.random() * 1000.0) + 1000);
                  silenTextMap.put("condition", conditions);
                  String silenceText = JSON.toJSONString(silenTextMap);
                  hm.put("silenceText", silenceText);
                  hm.put("silenceSuccess", true);
                  hm.put("silenceErrorReason", "");
               }
            } catch (Exception var36) {
               this.logger.error("导入静音格式有误!", var36);
               HashMap<String, Object> silenTextMap = new HashMap();
               List conditions = new ArrayList();
               silenTextMap.put("mentId", (int)(Math.random() * 1000.0) + 1000);
               silenTextMap.put("condition", conditions);
               String silenceText = JSON.toJSONString(silenTextMap);
               hm.put("silenceText", silenceText);
               hm.put("silenceSuccess", false);
               hm.put("silenceErrorReason", "导入静音格式有误!");
            }

            File tFile = new File(fileDir + "\\" + file.getName());
            input.close();
            tFile.delete();
            return ResponseResult.success(hm, "删除成功!");
         }
      } catch (Exception var38) {
         this.logger.error("导入的数据格式有误！", var38);
         return ResponseResult.error("导入的数据格式有误！");
      }
   }

   private String[] getImportSilenceRule(HttpServletRequest request, Sheet sheet1) {
      ModelRequest modelRequest = new ModelRequest();
      modelRequest.setDataSource(BaseUtils.getDataSource(request));
      String silenceText = "";
      String[] importSilenceRuleResult = new String[2];

      try {
         ServiceResponse propertiesResponse = this.modelService.searchSilenceProperties(modelRequest);
         ServiceResponse operationResponse = this.modelService.searchSilenceOperation(modelRequest);
         HashMap<String, HashMap<String, Object>> propertiesMap = new HashMap();
         HashMap<String, HashMap<String, Object>> operationsMap = new HashMap();
         if (propertiesResponse.isSuccessful() && operationResponse.isSuccessful()) {
            propertiesMap = (HashMap)propertiesResponse.getValue();
            operationsMap = (HashMap)operationResponse.getValue();
         }

         Iterator<Row> silenceRows = sheet1.rowIterator();
         LinkedHashMap<String, Object> silenTextMap = new LinkedHashMap();
         List<LinkedHashMap<String, Object>> conditions = new ArrayList();
         silenceRows.next();
         int lastCellValue = Integer.parseInt(this.judgeCellType(sheet1.getRow(1).getCell(0)));
         int curRow = 1;
         int firstRow = 1;

         ArrayList xlsxRowList;
         for(xlsxRowList = new ArrayList(); silenceRows.hasNext(); ++curRow) {
            Row row = (Row)silenceRows.next();
            int curIndex = Integer.parseInt(this.judgeCellType(row.getCell(0)));
            if (curIndex != lastCellValue) {
               int[] currentRow = new int[]{firstRow, curRow};
               xlsxRowList.add(currentRow);
               firstRow = curRow;
            }

            lastCellValue = curIndex;
         }

         int[] currentRow = new int[]{firstRow, curRow};
         xlsxRowList.add(currentRow);
         HashMap<String, String> objToDateTime = new HashMap();

         for(int m = 0; m < xlsxRowList.size(); ++m) {
            int[] crArray = (int[])xlsxRowList.get(m);
            this.changeObjectToTime(sheet1, objToDateTime, crArray[0], m);
         }

         String errorReason = "";

         for(int j = 0; j < xlsxRowList.size(); ++j) {
            int[] crArray = (int[])xlsxRowList.get(j);
            LinkedHashMap<String, Object> conditionMap = this.readXlsxRule(sheet1, crArray[0], crArray[1], propertiesMap, operationsMap, objToDateTime);
            if (Boolean.parseBoolean(conditionMap.get("isError").toString())) {
               errorReason = errorReason + conditionMap.get("errorReason").toString();
            } else {
               conditionMap.remove("isError");
               conditions.add(conditionMap);
            }
         }

         silenTextMap.put("mentId", (int)(Math.random() * 1000.0) + 1000);
         silenTextMap.put("condition", conditions);
         silenceText = JSON.toJSONString(silenTextMap);
         this.logger.debug("silenceText is :" + silenceText);
         importSilenceRuleResult[0] = silenceText;
         importSilenceRuleResult[1] = errorReason;
         return importSilenceRuleResult;
      } catch (Exception var23) {
         this.logger.error("静音规则格式有误！", var23);
         return null;
      }
   }

   private void changeObjectToTime(Sheet sheet, HashMap<String, String> objToDateTime, int i, int i1) {
      String obj1 = this.judgeCellType(sheet.getRow(i).getCell(1));
      objToDateTime.put(obj1, (new Date()).getTime() + (long)(i1 * 1000) + "");
   }

   public LinkedHashMap<String, Object> readXlsxRule(Sheet sheet, int firstRow, int endRow, HashMap<String, HashMap<String, Object>> propertiesMap, HashMap<String, HashMap<String, Object>> operationsMap, HashMap<String, String> objToDateTime) {
      LinkedHashMap<String, Object> everySilence = new LinkedHashMap();
      List<HashMap<String, Object>> optList = new ArrayList();
      String errorReason = "";

      for(int es = firstRow; es < endRow; ++es) {
         Row row = sheet.getRow(es);
         String obj1 = "";
         if (es == firstRow) {
            obj1 = this.judgeCellType(sheet.getRow(es).getCell(1));
            String obj2 = this.judgeCellType(sheet.getRow(es).getCell(2));
            everySilence.put("name", obj1);
            everySilence.put("id", objToDateTime.get(obj1));
            everySilence.put("dimensionName", obj2);
            String dimension = TagType.getDimensionCode(obj2);
            if (StringUtils.isNullOrEmpry(dimension)) {
               errorReason = errorReason + obj1 + "中" + dimension + "类型不存在;\n";
            }

            everySilence.put("dimensionCode", dimension);
         }

         String[] objString = this.readXlsxCell(row, 3, 6);
         HashMap<String, Object> options = new HashMap();
         String proStr = objString[0];
         String relStr = objString[1];
         String opeStr = objString[2];
         String inputValue = objString[3];
         HashMap<String, Object> propertyMap = (HashMap)propertiesMap.get(proStr);
         HashMap<String, Object> operationMap = (HashMap)operationsMap.get(opeStr);
         if (propertyMap == null || operationMap == null) {
            if (propertyMap == null) {
               everySilence.put("isError", true);
               errorReason = errorReason + obj1 + "中" + proStr + "静音属性不存在;\n";
            }

            if (operationMap == null) {
               everySilence.put("isError", true);
               errorReason = errorReason + obj1 + "中" + opeStr + "静音逻辑关系不存在";
            }

            everySilence.put("errorReason", errorReason);
            return everySilence;
         }

         everySilence.put("isError", false);
         options.put("propertyName", proStr);
         options.put("propertyCode", propertyMap.get("propertyCode"));
         if (proStr.contains("声道")) {
            options.put("operationName", inputValue);
            options.put("operationCode", this.changeInputValue(inputValue));
            options.put("equOperation", opeStr);
            options.put("equOperationCode", operationMap.get("operationCode"));
         } else {
            options.put("operationName", opeStr);
            options.put("operationCode", operationMap.get("operationCode"));
            options.put("inputValue", inputValue);
         }

         options.put("flag", propertyMap.get("flag"));
         options.put("isDepend", propertyMap.get("isDepend"));
         String relativeobject = "";
         String reletionName = "";
         if (!"无".equals(relStr)) {
            reletionName = relStr;
            relativeobject = (String)objToDateTime.get(relStr);
         }

         options.put("relativeobject", relativeobject);
         options.put("reletionName", reletionName);
         optList.add(options);
      }

      everySilence.put("options", optList);
      return everySilence;
   }

   private String changeInputValue(String inputValue) {
      switch (inputValue) {
         case "坐席":
            return "n0";
         case "客户":
            return "n1";
         default:
            return inputValue;
      }
   }

   private String[] readXlsxCell(Row row, int fc, int ec) {
      int objLength = ec - fc + 1;
      String[] objString = new String[objLength];

      for(int i = 0; i < objLength; ++i) {
         objString[i] = this.judgeAnotherCellType(row.getCell(fc));
         ++fc;
      }

      return objString;
   }

   private String judgeAnotherCellType(Cell cell) {
      String cellValue = "";
      DecimalFormat df = new DecimalFormat("0.00");
      if (cell != null) {
         switch (cell.getCellType()) {
            case 0:
               cellValue = df.format(cell.getNumericCellValue()).toString();
               break;
            case 1:
               cellValue = cell.getRichStringCellValue().getString().trim();
               break;
            case 2:
               cellValue = cell.getCellFormula();
               break;
            case 3:
            default:
               cellValue = "";
               break;
            case 4:
               cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
         }
      }

      return cellValue;
   }

   public long addModelFragment(HttpServletRequest request, String fragContent, String fragNum, int fragChannel, int isTag, String tgText, String tgContent, String fragRemark) throws Exception {
      try {
         Date date = new Date();
         ModelFragment modelFragment = new ModelFragment();
         modelFragment.setModelId(-1L);
         modelFragment.setCreateTime(DateUtils.getDateFormat(date, "yyyy-MM-dd HH:mm:ss"));
         modelFragment.setCreateTimestamp(date.getTime());
         modelFragment.setRuleType(2);
         modelFragment.setFragmentContent(fragContent);
         modelFragment.setPreviewId(DateUtils.getCurrentTime());
         modelFragment.setFragmentNum(Integer.parseInt(fragNum));
         modelFragment.setChannel(fragChannel);
         modelFragment.setIsTag(isTag);
         modelFragment.setTagContent(tgContent);
         modelFragment.setTagText(tgText);
         modelFragment.setRemark(fragRemark);
         ModelRequest modelRequest = new ModelRequest();
         modelRequest.setPriviewId(modelFragment.getPreviewId());
         modelRequest.setModelFragment(modelFragment);
         modelRequest.setDataSource(BaseUtils.getDataSource(request));
         ServiceResponse serviceResponse = this.modelFragmentService.addModelFragmentService(modelRequest);
         if (serviceResponse.isSuccessful()) {
            return (Long)serviceResponse.getValue();
         } else {
            throw new Exception("调用服务化接口添加导入模型规则出错");
         }
      } catch (Exception var13) {
         this.logger.error("添加导入模型规则出错", var13);
         throw var13;
      }
   }

   private String judgeCellType(Cell cell) {
      String cellValue = "";
      DecimalFormat df = new DecimalFormat("#");
      if (cell != null) {
         switch (cell.getCellType()) {
            case 0:
               cellValue = df.format(cell.getNumericCellValue()).toString();
               break;
            case 1:
               cellValue = cell.getRichStringCellValue().getString().trim();
               break;
            case 2:
               cellValue = cell.getCellFormula();
               break;
            case 3:
            default:
               cellValue = "";
               break;
            case 4:
               cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
         }
      }

      return cellValue;
   }

   @RequestMapping(
      value = {"uploadFile"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult uploadFile(HttpServletRequest request, @RequestParam("uploadify") MultipartFile uploadify) {
      try {
         this.logger.info("uploadFile is begin");
         String path = request.getSession().getServletContext().getRealPath("/uploadFiles");
         this.delAllFile(path);
         File dir = new File(path);
         if (!dir.exists()) {
            dir.mkdir();
         }

         String newFileName = null;
         long now = (new Date()).getTime();
         String uploadifyFileName = uploadify.getOriginalFilename();
         if (!uploadifyFileName.substring(0, 5).equals("模型规则_")) {
            this.logger.error("导入格式有误！");
            return ResponseResult.error("导入格式有误！");
         } else {
            this.logger.info("导入格式正确");
            int index = uploadifyFileName.lastIndexOf(".");
            if (index != -1) {
               newFileName = now + UUID.randomUUID().toString() + uploadifyFileName.substring(index);
            } else {
               newFileName = Long.toString(now);
            }

            this.logger.info(path + System.getProperty("file.separator") + newFileName);

            try {
               File uploadedFile = new File(path + System.getProperty("file.separator") + newFileName);
               byte[] bytes = uploadify.getBytes();
               FileCopyUtils.copy(bytes, uploadedFile);
            } catch (FileNotFoundException var12) {
               this.logger.error("没有找到文件");
            } catch (IOException var13) {
               this.logger.error("文件读写出错");
            }

            this.logger.info("uploadFile is complete");
            return ResponseResult.success("操作成功!");
         }
      } catch (Exception var14) {
         this.logger.error("导入格式有误！");
         return ResponseResult.error("导入格式有误！");
      }
   }

   public boolean delAllFile(String path) throws Exception {
      boolean flag = false;

      try {
         File file = new File(path);
         if (!file.exists()) {
            return flag;
         } else if (!file.isDirectory()) {
            return flag;
         } else {
            String[] tempList = file.list();
            File temp = null;

            for(int i = 0; i < tempList.length; ++i) {
               if (path.endsWith(File.separator)) {
                  temp = new File(path + tempList[i]);
               } else {
                  temp = new File(path + File.separator + tempList[i]);
               }

               if (temp.isFile()) {
                  temp.delete();
               }

               if (temp.isDirectory()) {
                  this.delAllFile(path + "/" + tempList[i]);
                  flag = true;
               }
            }

            return flag;
         }
      } catch (Exception var7) {
         this.logger.error("清空uploadFiles中的所有文件及其文件夹出错", var7);
         throw var7;
      }
   }

   @ResponseBody
   @RequestMapping(
      value = {"fetchOnSwitch"},
      method = {RequestMethod.POST}
   )
   public ResponseResult fetchDimensionSwitch() {
      String dimensionOpen = "on";
      String openStr = "onOpen";
      HashMap<String, Integer> result = new HashMap();

      try {
         dimensionOpen = LoadConfig.getConfigProperty("withDimensionOnline");
         this.logger.info("获取到参数withDimensionOnline:" + dimensionOpen);
      } catch (Exception var5) {
         this.logger.error("获取withDimensionOnline 配置项出错！" + ExceptionUtils.getFullStackTrace(var5));
      }

      result.put(openStr, 1);
      if (dimensionOpen != null && dimensionOpen.equalsIgnoreCase("off")) {
         result.put(openStr, 0);
      }

      return ResponseResult.success(result, "操作成功!");
   }

   @RequestMapping(
      value = {"getColumData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getColumData(HttpServletRequest request, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestParam("modelId") String modelId, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime, @RequestParam("isDetail") int isDetail) {
      try {
         this.logger.info("模型通话列表controler层getColumData方法开始响应");
         ModelColumnDataRequest modelColumnDataRequest = new ModelColumnDataRequest();
         modelColumnDataRequest.setStartTime(startTime);
         modelColumnDataRequest.setEndTime(endTime);
         modelColumnDataRequest.setModelId(modelId);
         modelColumnDataRequest.setIfLone("0");
         modelColumnDataRequest.setBatchId("");
         modelColumnDataRequest.setDataType(2);
         modelColumnDataRequest.setPageNum(pageNum);
         if (isDetail == 0) {
            pageSize = -1;
         }

         modelColumnDataRequest.setPageSize(pageSize);
         modelColumnDataRequest.setDataSource(BaseUtils.getDataSource(request));
         modelColumnDataRequest.setUserId(BaseUtils.getUserId(request));
         modelColumnDataRequest.setSystemId(this.commonService.getContextPath());
         this.logger.info(JSON.toJSONString(modelColumnDataRequest));
         DataDetailResponse dataDetailResponse = this.modelApplyService.getColumnData(modelColumnDataRequest);
         this.logger.info(JSON.toJSONString(dataDetailResponse));
         List<LinkedHashMap<String, Object>> valueList = dataDetailResponse.getValues();
         List<Map<String, Object>> values = new ArrayList();
         Iterator var12 = valueList.iterator();

         while(var12.hasNext()) {
            Map<String, Object> map = (Map)var12.next();
            ((List)values).add(map);
         }

         if (isDetail == 0) {
            values = this.reportChartViewService.dealLineColumData((List)values);
         }

         List<String> xDatas = new ArrayList();
         List<Object> yDatas = new ArrayList();
         Iterator var14 = ((List)values).iterator();

         while(var14.hasNext()) {
            Map<String, Object> map = (Map)var14.next();
            String xData = map.get("year") + String.valueOf(map.get("month")) + map.get("day");
            xDatas.add(xData);
            if (map.get("sumCallNum") == null) {
               yDatas.add(map.get("sumCallNum"));
            } else {
               yDatas.add(Double.valueOf(String.valueOf(map.get("sumCallNum"))));
            }
         }

         HashMap<String, Object> resultMap = new HashMap();
         resultMap.put("xData", xDatas);
         resultMap.put("yData", yDatas);
         HashMap<String, Object> pageMap = new HashMap();
         pageMap.put("pageNum", dataDetailResponse.getPageNow());
         pageMap.put("pageSize", dataDetailResponse.getPageSize());
         pageMap.put("totalPages", dataDetailResponse.getTotalPage());
         pageMap.put("totalRows", dataDetailResponse.getTotalSize());
         resultMap.put("pageInfo", pageMap);
         this.logger.info("模型通话列表controler层getColumData方法开始响应");
         return ResponseResult.success(resultMap, "查询成功!");
      } catch (Exception var17) {
         this.logger.error("获取模型通话量出错！", var17);
         return ResponseResult.error(var17.getMessage());
      }
   }

   public DataDrillRequest getRequest(String startTime, String endTime, String userID, String systemName, String orderField, String orderType) throws Exception {
      try {
         DataDrillRequest dataDrillRequest = new DataDrillRequest();
         dataDrillRequest.setUserId(userID);
         dataDrillRequest.setSystemId(systemName);
         dataDrillRequest.addAnalyses(new AnalyseField("callNumSum", "呼叫量", "0"));
         DrillFilter filter = new DrillFilter();
         filter.setExpression(" (a) ");
         Map<String, FilterField> filterFields = new HashMap();
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         filterFields.put("a", new FilterField("timestamp", FilterRuleEnum.BOTH_RANGE.getValue(), new Object[]{sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime()}));
         dataDrillRequest.setOrderField(orderField);
         dataDrillRequest.setOrderType(orderType);
         filter.setFilterFields(filterFields);
         dataDrillRequest.setFilter(filter);
         this.logger.info(JSON.toJSONString(dataDrillRequest));
         return dataDrillRequest;
      } catch (Exception var11) {
         throw var11;
      }
   }
}
