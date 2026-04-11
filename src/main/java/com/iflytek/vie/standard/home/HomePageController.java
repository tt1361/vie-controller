package com.iflytek.vie.standard.home;

import com.alibaba.fastjson.JSON;
import com.iflytek.vie.app.api.custom.CustomHomePageService;
import com.iflytek.vie.app.api.datadrill.DataDrillService;
import com.iflytek.vie.app.api.datamining.ClusterService;
import com.iflytek.vie.app.api.dimension.DimensionService;
import com.iflytek.vie.app.api.model.ModelApplyService;
import com.iflytek.vie.app.api.model.ModelGroupService;
import com.iflytek.vie.app.api.model.ModelService;
import com.iflytek.vie.app.api.permission.DataAuthService;
import com.iflytek.vie.app.api.permission.FunctionAuthService;
import com.iflytek.vie.app.exception.PermissionServiceException;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.ServiceResponse;
import com.iflytek.vie.app.pojo.auth.AuthorizeInfo;
import com.iflytek.vie.app.pojo.common.ColumnInfo;
import com.iflytek.vie.app.pojo.common.ColumnMap;
import com.iflytek.vie.app.pojo.common.PagerResponse;
import com.iflytek.vie.app.pojo.custom.CallTrendDimBeanDTO;
import com.iflytek.vie.app.pojo.custom.CallTrendDimRequest;
import com.iflytek.vie.app.pojo.custom.CallTrendDimResponse;
import com.iflytek.vie.app.pojo.custom.CustomHomePage;
import com.iflytek.vie.app.pojo.custom.CustomHomePageRequest;
import com.iflytek.vie.app.pojo.custom.HomeTableDataRequest;
import com.iflytek.vie.app.pojo.datadrill.DataDetailRequest;
import com.iflytek.vie.app.pojo.datadrill.DataDetailResponse;
import com.iflytek.vie.app.pojo.datamining.ClusterRequest;
import com.iflytek.vie.app.pojo.datamining.ClusterResponse;
import com.iflytek.vie.app.pojo.datamining.ClusterTableRequest;
import com.iflytek.vie.app.pojo.dimension.AllDimensionRequest;
import com.iflytek.vie.app.pojo.dimension.DimensionConfig;
import com.iflytek.vie.app.pojo.dimension.DimensionRequest;
import com.iflytek.vie.app.pojo.dimension.DimensionResponse;
import com.iflytek.vie.app.pojo.model.ModelGroupRequest;
import com.iflytek.vie.app.pojo.model.ModelRequest;
import com.iflytek.vie.app.pojo.permission.ContextRequest;
import com.iflytek.vie.app.pojo.permission.DataResourceAuth;
import com.iflytek.vie.app.pojo.permission.DimensionAuth;
import com.iflytek.vie.app.pojo.permission.FunctionAuthResponse;
import com.iflytek.vie.app.pojo.topicgroup.DataInfo;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.constants.IndexConstants;
import com.iflytek.vie.pojo.MenuBean;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("homePageController")
@RequestMapping({"/home"})
public class HomePageController {
   private static final Logger logger = LoggerFactory.getLogger(HomePageController.class);
   @Autowired
   private FunctionAuthService functionAuthService;
   @Autowired
   private DataAuthService dataAuthService;
   @Autowired
   private CommonService commonService;
   @Autowired
   private CustomHomePageService customHomePageService;
   @Autowired
   private ClusterService clusterService;
   @Autowired
   private ModelGroupService modelGroupService;
   @Autowired
   private ModelService modelService;
   @Autowired
   private DimensionService dimensionService;
   @Autowired
   private DataDrillService dataDrillService;
   @Autowired
   private ModelApplyService modelApplyService;
   private final DecimalFormat df = new DecimalFormat("0.00");

   @RequestMapping(
      value = {"/getMenu"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public ResponseResult getMenu(HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         ContextRequest params = new ContextRequest();
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            response.setMessage("数据源为空");
            return response;
         } else {
            params.setDataSource(dataSource);
            params.setContextPath(this.commonService.getContextPath());
            params.setUserId(Long.valueOf(BaseUtils.getUserId(request)));
            Map<String, FunctionAuthResponse> map = this.functionAuthService.getFuncAuths(params);
            if (null != map && map.size() != 0) {
               List<MenuBean> menuList = this.packMenu(map);
               CustomHomePageRequest customHomePageRequest = new CustomHomePageRequest();
               customHomePageRequest.setUserId(BaseUtils.getUserId(request));
               customHomePageRequest.setDataSource(dataSource);
               List<CustomHomePage> customPageList = this.customHomePageService.queryCustomHomePageList(customHomePageRequest);
               if (customPageList != null && customPageList.size() > 0) {
                  this.setCustomPageListToMenu(menuList, customPageList);
               }

               response.setValue(menuList);
               response.setSuccess(true);
            } else {
               response.setMessage("该用户下无菜单配置");
            }

            return response;
         }
      } catch (Exception var9) {
         logger.info(var9.toString());
         response.setMessage(var9.getMessage());
         return response;
      }
   }

   private List<MenuBean> packMenu(Map<String, FunctionAuthResponse> map) throws Exception {
      Map<Long, MenuBean> menuMap = new LinkedHashMap();
      Iterator var3 = map.entrySet().iterator();

      Map.Entry entry;
      String s;
      FunctionAuthResponse f;
      while(var3.hasNext()) {
         entry = (Map.Entry)var3.next();
         s = JSON.toJSONString(entry.getValue());
         f = (FunctionAuthResponse)JSON.parseObject(s, FunctionAuthResponse.class);
         if (f.getHierarchy() == 1) {
            try {
               MenuBean menu = new MenuBean();
               BeanUtils.copyProperties(menu, f);
               menuMap.put(f.getResourceId(), menu);
            } catch (InvocationTargetException | IllegalAccessException var13) {
               throw new Exception("BeanUtils.copyProperties()方法异常", var13);
            }
         }
      }

      var3 = map.entrySet().iterator();

      while(var3.hasNext()) {
         entry = (Map.Entry)var3.next();
         s = JSON.toJSONString(entry.getValue());
         f = (FunctionAuthResponse)JSON.parseObject(s, FunctionAuthResponse.class);
         if (f.getHierarchy() == 2) {
            try {
               long parentId = f.getParentId();
               MenuBean menu = new MenuBean();
               BeanUtils.copyProperties(menu, f);
               MenuBean fatherMenu = (MenuBean)menuMap.get(parentId);
               List<MenuBean> childMenus = fatherMenu.getChildRes();
               if (null == childMenus) {
                  childMenus = new ArrayList();
               }

               ((List)childMenus).add(menu);
               fatherMenu.setChildRes((List)childMenus);
            } catch (InvocationTargetException | IllegalAccessException var12) {
               throw new Exception("BeanUtils.copyProperties()方法异常", var12);
            }
         }
      }

      return new ArrayList(menuMap.values());
   }

   public void setCustomPageListToMenu(List<MenuBean> menuBeanList, List<CustomHomePage> customPageList) throws Exception {
      try {
         if (menuBeanList != null && menuBeanList.size() > 0) {
            MenuBean menuBean = null;

            for(int m = 0; m < menuBeanList.size(); ++m) {
               if ("#/index".equals(((MenuBean)menuBeanList.get(m)).getView())) {
                  menuBean = (MenuBean)menuBeanList.get(m);
                  break;
               }
            }

            if (menuBean != null) {
               List<MenuBean> childRes = menuBean.getChildRes();
               if (childRes != null && childRes.size() > 0) {
                  List<MenuBean> customList = new ArrayList();
                  CustomHomePage tempPage = null;
                  List<String> optActionList = new ArrayList();
                  optActionList.add("query");

                  int systemPageIndex;
                  for(systemPageIndex = 0; systemPageIndex < customPageList.size(); ++systemPageIndex) {
                     tempPage = (CustomHomePage)customPageList.get(systemPageIndex);
                     MenuBean customPage = new MenuBean();
                     customPage.setChildRes((List)null);
                     customPage.setHierarchy(2);
                     customPage.setIcon("");
                     customPage.setIsDisplay(1);
                     customPage.setLink("/custom");
                     customPage.setOptAction(optActionList);
                     customPage.setParentId(menuBean.getResourceId());
                     customPage.setResourceId((long)tempPage.getId().intValue());
                     customPage.setResourceName(tempPage.getPageName());
                     customPage.setView("#/index/custom/" + tempPage.getId());
                     customList.add(customPage);
                  }

                  systemPageIndex = this.getSystemPageIndex(childRes);
                  childRes.addAll(systemPageIndex + 1, customList);
               }
            }
         }

      } catch (Exception var10) {
         throw new Exception("setCustomPageListToMenu方法异常", var10);
      }
   }

   private int getSystemPageIndex(List<MenuBean> childRes) {
      int offset = 0;
      MenuBean resource = null;

      for(int i = 0; i < childRes.size(); ++i) {
         resource = (MenuBean)childRes.get(i);
         if ("#/index/system".equals(resource.getView())) {
            offset = i;
            break;
         }
      }

      return offset;
   }

   @RequestMapping(
      value = {"/getCenters"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public ResponseResult getCenters(HttpServletRequest request) throws ViePlatformServiceException {
      ResponseResult response = new ResponseResult();

      try {
         ContextRequest params = new ContextRequest();
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            response.setMessage("数据源为空");
            return response;
         } else {
            params.setDataSource(dataSource);
            params.setContextPath(this.commonService.getContextPath());
            params.setUserId(Long.valueOf(BaseUtils.getUserId(request)));
            Map<String, DataResourceAuth> dataMap = this.dataAuthService.getDataAuths(params);
            if (null == dataMap) {
               response.setMessage("没有数据权限");
               return response;
            } else {
               HashMap<String, Set<String>> hashMap = this.getMapDataAuth(dataMap, dataSource);
               String dataTypeDimension = BaseUtils.getDataTypeDimension(request);
               response.setValue(hashMap.get(dataTypeDimension));
               response.setSuccess(true);
               return response;
            }
         }
      } catch (PermissionServiceException var8) {
         var8.printStackTrace();
         response.setMessage(var8.getMessage());
         return response;
      }
   }

   private HashMap<String, Set<String>> getMapDataAuth(Map<String, DataResourceAuth> data, String dataSource) {
      HashMap<String, Set<String>> hashMap = new HashMap();
      Iterator var4 = data.keySet().iterator();

      label45:
      while(var4.hasNext()) {
         String ck = (String)var4.next();
         Iterator var6 = data.keySet().iterator();

         while(true) {
            DataResourceAuth d;
            do {
               do {
                  String key;
                  do {
                     if (!var6.hasNext()) {
                        continue label45;
                     }

                     key = (String)var6.next();
                  } while(!ck.equals(dataSource));

                  d = (DataResourceAuth)data.get(key);
               } while(d == null);
            } while(null == d.getDataDimension());

            List<DimensionAuth> dimensionAuths = d.getDataDimension();

            DimensionAuth dimensionAuth;
            HashSet values;
            for(Iterator var10 = dimensionAuths.iterator(); var10.hasNext(); hashMap.put(dimensionAuth.getEnglishName(), values)) {
               dimensionAuth = (DimensionAuth)var10.next();
               values = new HashSet();
               values.addAll(dimensionAuth.getValue());
               if (hashMap.containsKey(dimensionAuth.getEnglishName())) {
                  values.addAll((Collection)hashMap.get(dimensionAuth.getEnglishName()));
               }
            }
         }
      }

      return hashMap;
   }

   @RequestMapping(
      value = {"/getCallTimeTrendDimList"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public ResponseResult getCallTimeTrendDimList() {
      ResponseResult response = new ResponseResult();

      try {
         CallTrendDimBeanDTO result = this.customHomePageService.getCallTrendDimDTO();
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var3) {
         var3.printStackTrace();
         response.setMessage(var3.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getCallTimeTrendByDim"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getCallTimeTrendByDim(CallTrendDimRequest trendDimRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         trendDimRequest.setSystemId(this.commonService.getContextPath());
         trendDimRequest.setUserId(BaseUtils.getUserId(request));
         trendDimRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            response.setMessage("数据源为空");
            return response;
         } else {
            trendDimRequest.setDataSource(dataSource);
            CallTrendDimResponse result = this.customHomePageService.getCallTimeTrendByDim(trendDimRequest);
            response.setSuccess(true);
            response.setValue(result);
            return response;
         }
      } catch (VieAppServiceException var6) {
         response.setMessage(var6.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/fetchHotViewData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult fetchHotViewData(ClusterRequest clusterRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         new AuthorizeInfo();
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            response.setMessage("数据源为空");
            return response;
         } else {
            clusterRequest.setDataSource(dataSource);
            clusterRequest.setSystemId(this.commonService.getContextPath());
            clusterRequest.setUserId(BaseUtils.getUserId(request));
            clusterRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
            clusterRequest.setDataSource(dataSource);
            List<ClusterResponse> result = this.clusterService.getClusterDataService(clusterRequest);
            response.setSuccess(true);
            response.setValue(result);
            return response;
         }
      } catch (ViePlatformServiceException var7) {
         response.setMessage(var7.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getModelByModelGroupId"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getModelByModelGroupId(ModelGroupRequest modelGroupRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         modelGroupRequest.setSystemId(this.commonService.getContextPath());
         modelGroupRequest.setUserId(BaseUtils.getUserId(request));
         modelGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         modelGroupRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         ServiceResponse serviceResponse = this.modelGroupService.searchModelByGroupId(modelGroupRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "查询成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (ViePlatformServiceException var5) {
         logger.error("根据模型组和模型时间查询模型出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getModelAccuracy"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getModelAccuracy(ModelRequest ModelRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         ModelRequest.setSystemId(this.commonService.getContextPath());
         ModelRequest.setUserId(BaseUtils.getUserId(request));
         ModelRequest.setDataSource(BaseUtils.getDataSource(request));
         ModelRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         ServiceResponse serviceResponse = this.modelService.getModelAccuracy(ModelRequest);
         return serviceResponse.isSuccessful() ? ResponseResult.success(serviceResponse.getValue(), "查询成功!") : ResponseResult.error(serviceResponse.getMessage());
      } catch (ViePlatformServiceException var5) {
         logger.error("获取模型检出数出错", var5);
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"getTableData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTableData(HttpServletRequest request, DataDetailRequest dataDetailRequest, @RequestParam("selectTime") String selectTime, @RequestParam("clusterId") Integer clusterId, @RequestParam("pageNum") String pageNum, @RequestParam("pageSize") String pageSize, @RequestParam("sortColumn") String sortColumn, @RequestParam("sortType") String sortType, @RequestParam("searchDimension") String searchDimension, @RequestParam("centerFlag") Integer centerFlag, @RequestParam(value = "selectCenter",required = false) String selectCenter) {
      try {
         logger.info("首页聚类钻取通话列表controler层getTableData开始响应");
         HomeTableDataRequest homeTableDataRequest = new HomeTableDataRequest();
         homeTableDataRequest.setPageNum(Integer.valueOf(pageNum));
         homeTableDataRequest.setPageSize(Integer.valueOf(pageSize));
         homeTableDataRequest.setSortColumn(sortColumn);
         homeTableDataRequest.setSortType(sortType);
         homeTableDataRequest.setIfLone("0");
         homeTableDataRequest.setDataType(2);
         if (centerFlag != null && centerFlag == 0) {
            selectCenter = "all";
         }

         Map<String, Object> retMap = new HashMap();
         ClusterTableRequest clusterTableRequest = new ClusterTableRequest();
         centerFlag = centerFlag == null ? 0 : centerFlag;
         clusterTableRequest.setCenterFlag(centerFlag);
         clusterTableRequest.setClusterId(clusterId);
         clusterTableRequest.setSelectCenter(selectCenter);
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            clusterTableRequest.setDataSource(dataSource);
            clusterTableRequest.setSelectTime(selectTime);
            String voiceIds = this.clusterService.getClusterTableDataService(clusterTableRequest);
            if (StringUtils.isNullOrEmpry(voiceIds)) {
               return ResponseResult.success(retMap, "getClusterTableDataService方法无结果返回");
            } else {
               homeTableDataRequest.setVoiceId(voiceIds);
               homeTableDataRequest.setDataSource(dataSource);
               homeTableDataRequest.setUserId(BaseUtils.getUserId(request));
               homeTableDataRequest.setSystemId(this.commonService.getContextPath());
               homeTableDataRequest.setDimDay(selectTime);
               ColumnInfo columnInfo = this.getColumnMaps(searchDimension, dataSource);
               List<String> selCols = columnInfo.getColumns();
               List<ColumnMap> showCols = new ArrayList();
               HashMap<String, String> dimensionMap = this.getDimensionMap(request);
               Iterator var21 = selCols.iterator();

               while(var21.hasNext()) {
                  String colFiled = (String)var21.next();
                  showCols.add(new ColumnMap((String)dimensionMap.get(colFiled), colFiled));
               }

               showCols.add(new ColumnMap("关键词", "keyword"));
               showCols.add(new ColumnMap("模型名称", "offLineTagName"));
               selCols.add("offLineTagInfo");
               selCols.add("offLineTagId");
               selCols.add("offLineTagName");
               homeTableDataRequest.setSearchColumns(selCols);
               logger.info(JSON.toJSONString(homeTableDataRequest));
               DataDetailResponse dataDetailResponse = this.customHomePageService.getTableData(homeTableDataRequest);
               logger.info(JSON.toJSONString(dataDetailResponse));
               List<LinkedHashMap<String, Object>> values = dataDetailResponse.getValues();
               DimensionRequest dimensionRequest = new DimensionRequest();
               dimensionRequest.setDataSource(homeTableDataRequest.getDataSource());
               LinkedHashMap<String, DimensionConfig> dimensionList = this.dimensionService.getDimensionMap(dimensionRequest);
               List<DataInfo> dataInfos = new ArrayList();
               if (values != null && values.size() > 0) {
                  Iterator var26 = values.iterator();

                  while(var26.hasNext()) {
                     LinkedHashMap<String, Object> map = (LinkedHashMap)var26.next();
                     String id = "";
                     map.put("id", map.get("taskId"));
                     id = map.get("id") == null ? "" : String.valueOf(map.get("id"));
                     DataInfo dataInfo = new DataInfo(id, 0, 0);
                     String column;
                     if (map.containsKey("offLineTagId") || map.containsKey("offLineTagInfo") || map.containsKey("offLineTagName")) {
                        List<Map<String, Object>> list = new ArrayList();
                        if (map.containsKey("offLineTagName")) {
                           column = String.valueOf(map.get("offLineTagName") == null ? "" : map.get("offLineTagName"));
                           map.put("offLineTagId", column);
                        } else {
                           map.put("offLineTagId", (Object)null);
                        }

                        if (map.containsKey("offLineTagInfo")) {
                           if (map.get("offLineTagInfo") != null && !StringUtils.isNullOrEmpry(String.valueOf(map.get("offLineTagInfo")))) {
                              JSONArray jsonArray = JSONArray.fromObject(String.valueOf(map.get("offLineTagInfo")));
                              if (jsonArray != null && jsonArray.size() > 0) {
                                 Iterator var33 = jsonArray.iterator();

                                 while(var33.hasNext()) {
                                    Map<String, Object> tempWord = (Map)var33.next();
                                    int type = Integer.parseInt(tempWord.get("type").toString());
                                    if (type == 0) {
                                       Map<String, Object> hms = new HashMap();
                                       hms.put("word", tempWord.get("content"));
                                       hms.put("begin", tempWord.get("beginTime"));
                                       hms.put("end", tempWord.get("endTime"));
                                       hms.put("machineId", tempWord.get("machineId"));
                                       hms.put("voiceId", tempWord.get("voiceId"));
                                       hms.put("voiceUri", tempWord.get("voiceUri"));
                                       if (!list.contains(hms)) {
                                          list.add(hms);
                                       }
                                    }
                                 }
                              }
                           }

                           map.put("keywordInfos", list);
                        } else {
                           map.put("keywordInfos", (Object)null);
                        }

                        dataInfo.setKeywordInfos(list);
                     }

                     Iterator var41 = selCols.iterator();

                     while(var41.hasNext()) {
                        column = (String)var41.next();
                        if (map.containsKey(column) && dimensionList.containsKey(column)) {
                           DimensionConfig dimensionConfig = (DimensionConfig)dimensionList.get(column);
                           if (dimensionConfig != null) {
                              int flag = dimensionConfig.getFlag();
                              if (flag == 1) {
                                 Long resultDuration = 0L;
                                 if (map.get(column) != null) {
                                    resultDuration = this.toSecond(Long.valueOf(String.valueOf(map.get(column))));
                                 }

                                 map.put(column, resultDuration);
                              } else if (flag == 2 && map.get(column) != null) {
                                 map.put(column, this.toSecondTwo(map.get(column).toString()));
                              }
                           } else {
                              logger.error("isDurationDimsion 找不到维度" + column);
                           }
                        }
                     }

                     dataInfo.setDataMaps(map);
                     dataInfos.add(dataInfo);
                  }
               }

               PagerResponse<DataInfo> pagerResponse = new PagerResponse();
               pagerResponse.setPageNum(dataDetailResponse.getPageNow());
               pagerResponse.setPageSize(dataDetailResponse.getPageSize());
               pagerResponse.setTotalPages(dataDetailResponse.getTotalPage());
               pagerResponse.setTotalRows((long)dataDetailResponse.getTotalSize());
               pagerResponse.setRows(dataInfos);
               retMap.put("previewList", pagerResponse);
               retMap.put("columns", showCols);
               logger.info("首页聚类钻取通话列表controler层getTableData响应成功");
               return ResponseResult.success(retMap, "查询成功!");
            }
         }
      } catch (Exception var37) {
         logger.error("首页聚类钻取通话列表controler层getTableData出错", var37);
         return ResponseResult.error(var37.getMessage());
      }
   }

   public long toSecond(long millSecond) {
      return millSecond == 0L ? 0L : (long)Math.round((float)millSecond / 1000.0F);
   }

   public String toSecondTwo(String millSecond) {
      if (millSecond == null) {
         return "0";
      } else {
         try {
            return this.df.format((double)(this.df.parse(millSecond).floatValue() / 1000.0F));
         } catch (ParseException var3) {
            var3.printStackTrace();
            return "0";
         }
      }
   }

   private ColumnInfo getColumnMaps(String searchDimension, String dataSource) {
      ColumnInfo columnInfo = new ColumnInfo();
      columnInfo.getColumnMaps().add(new ColumnMap("通话时长（秒）", "duration"));
      int insightType = IndexConstants.getInsightType(dataSource);
      if (insightType == 1) {
         columnInfo.getColumnMaps().add(new ColumnMap("录音编号", "taskId"));
         columnInfo.getColumns().add("taskId");
      } else {
         columnInfo.getColumnMaps().add(new ColumnMap("录音编号", "voiceId"));
         columnInfo.getColumns().add("voiceId");
      }

      columnInfo.getColumns().add("duration");
      if (searchDimension != null && !"".equals(searchDimension)) {
         List<Object> list = (List)JSON.parseObject(searchDimension, List.class);
         if (list != null && list.size() != 0) {
            for(int i = 0; i < list.size(); ++i) {
               Map<String, String> map = (Map)JSON.parseObject(String.valueOf(list.get(i)), Map.class);
               if (!((String)map.get("column")).equals("taskId") && !((String)map.get("column")).equals("voiceId") && !((String)map.get("column")).equals("duration")) {
                  ColumnMap columnMap = new ColumnMap((String)map.get("columnName"), (String)map.get("column"));
                  columnInfo.getColumnMaps().add(columnMap);
                  columnInfo.getColumns().add(columnMap.getColumn());
               }
            }
         }
      }

      columnInfo.getColumnMaps().add(new ColumnMap("匹配规则", "keyword"));
      columnInfo.setColumnsStr(columnInfo.getColumns().toString());
      return columnInfo;
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
         logger.error("查询所有维度出錯", var12);
         throw var12;
      }
   }
}
