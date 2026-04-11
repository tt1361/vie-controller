package com.iflytek.vie.standard.report;

import com.alibaba.fastjson.JSON;
import com.iflytek.vie.app.api.report.ReportChartViewService;
import com.iflytek.vie.app.api.report.ReportGroupService;
import com.iflytek.vie.app.api.report.ReportService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.auth.AuthorizeInfo;
import com.iflytek.vie.app.pojo.common.PagerResponse;
import com.iflytek.vie.app.pojo.report.ReportConfig;
import com.iflytek.vie.app.pojo.report.ReportDeleteRequest;
import com.iflytek.vie.app.pojo.report.ReportGroup;
import com.iflytek.vie.app.pojo.report.ReportGroupRequest;
import com.iflytek.vie.app.pojo.report.ReportGroupResponse;
import com.iflytek.vie.app.pojo.report.ReportRequest;
import com.iflytek.vie.app.pojo.report.ReportSimpleRequest;
import com.iflytek.vie.app.pojo.report.ReportTableDataRequest;
import com.iflytek.vie.app.pojo.report.ReportTopRequest;
import com.iflytek.vie.app.pojo.report.ReportUsualRequest;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("reportController")
@RequestMapping({"/report"})
public class ReportController {
   private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
   final Base64 base64 = new Base64();
   @Autowired
   private ReportService reportService;
   @Autowired
   private ReportGroupService reportGroupService;
   @Autowired
   private ReportChartViewService reportChartViewService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/queryReportGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryReportGroup(HttpServletRequest request) {
      try {
         AuthorizeInfo authorizeInfo = new AuthorizeInfo();
         authorizeInfo.setUserId(BaseUtils.getUserId(request));
         authorizeInfo.setSystemId(this.commonService.getContextPath());
         authorizeInfo.setDataSource(BaseUtils.getDataSource(request));
         List<ReportGroupResponse> list = this.reportGroupService.queryReportGroup(authorizeInfo);
         return ResponseResult.success(list, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("获取所有报表组出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getReportGroupById"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getReportGroupById(@RequestParam("reportGroupId") int reportGroupId, HttpServletRequest request) {
      try {
         ReportGroupRequest reportGroupRequest = new ReportGroupRequest();
         reportGroupRequest.setReportGroupId(reportGroupId);
         reportGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         ReportGroup reportGroupInfo = this.reportGroupService.getReportGroupById(reportGroupRequest);
         return ResponseResult.success(reportGroupInfo, "查询成功!");
      } catch (VieAppServiceException var5) {
         logger.error("根据报表组id查询报表组信息出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"/addReportGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addReportGroup(ReportGroupRequest reportGroupRequest, HttpServletRequest request) {
      try {
         reportGroupRequest.setUserId(BaseUtils.getUserId(request));
         reportGroupRequest.setUserName(BaseUtils.getUserName(request));
         reportGroupRequest.setSystemId(this.commonService.getContextPath());
         reportGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         this.reportGroupService.addReportGroup(reportGroupRequest);
         return ResponseResult.success("保存成功!");
      } catch (VieAppServiceException var4) {
         logger.error("新建报表组出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/deleteReportGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteReportGroup(ReportGroupRequest reportGroupRequest, HttpServletRequest request) {
      try {
         reportGroupRequest.setUserId(BaseUtils.getUserId(request));
         reportGroupRequest.setSystemId(this.commonService.getContextPath());
         reportGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         this.reportGroupService.deleteReportGroup(reportGroupRequest);
         return ResponseResult.success("删除成功!");
      } catch (VieAppServiceException var4) {
         logger.error("删除报表组出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/updateReportGroup"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateReportGroup(ReportGroupRequest reportGroupRequest, HttpServletRequest request) {
      try {
         reportGroupRequest.setUserId(BaseUtils.getUserId(request));
         reportGroupRequest.setSystemId(this.commonService.getContextPath());
         reportGroupRequest.setUserName(BaseUtils.getUserName(request));
         reportGroupRequest.setDataSource(BaseUtils.getDataSource(request));
         this.reportGroupService.updateReportGroup(reportGroupRequest);
         return ResponseResult.success("保存成功!");
      } catch (VieAppServiceException var4) {
         logger.error("编辑报表组出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/queryCommonReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryCommonReport(HttpServletRequest request, ReportSimpleRequest reportSimpleRequest) {
      try {
         reportSimpleRequest.setUserId(BaseUtils.getUserId(request));
         reportSimpleRequest.setSystemId(this.commonService.getContextPath());
         reportSimpleRequest.setUserName(BaseUtils.getUserName(request));
         reportSimpleRequest.setDataSource(BaseUtils.getDataSource(request));
         PagerResponse<HashMap<String, Object>> response = this.reportService.queryCommonReport(reportSimpleRequest);
         return ResponseResult.success(response, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("分页获取常用报表列表出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/deleteCommonReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteCommonReport(HttpServletRequest request, @RequestParam("reportIds") String reportIds) {
      try {
         ReportDeleteRequest reportDeleteRequest = new ReportDeleteRequest();
         reportDeleteRequest.setReportIds(reportIds);
         reportDeleteRequest.setDataSource(BaseUtils.getDataSource(request));
         this.reportService.deleteCommonReport(reportDeleteRequest);
         return ResponseResult.success("删除成功!");
      } catch (VieAppServiceException var4) {
         logger.error("删除常用报表出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/queryReportList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryReportList(HttpServletRequest request, ReportSimpleRequest reportSimpleRequest) {
      try {
         reportSimpleRequest.setUserId(BaseUtils.getUserId(request));
         reportSimpleRequest.setSystemId(this.commonService.getContextPath());
         reportSimpleRequest.setDataSource(BaseUtils.getDataSource(request));
         PagerResponse<ReportConfig> response = this.reportService.queryReportList(reportSimpleRequest);
         List<HashMap<String, Object>> list = new ArrayList();
         HashMap<String, Object> result = new HashMap();
         if (response != null) {
            if (response.getRows() != null && response.getRows().size() > 0) {
               Iterator var6 = response.getRows().iterator();

               while(var6.hasNext()) {
                  ReportConfig reportConfig = (ReportConfig)var6.next();
                  HashMap<String, Object> map = new HashMap();
                  map.put("createTime", reportConfig.getCreateTime());
                  map.put("createUser", reportConfig.getCreateUser());
                  map.put("id", reportConfig.getId());
                  map.put("isGeneral", reportConfig.getIsGeneral());
                  map.put("isShare", reportConfig.getIsShare());
                  map.put("isUp", reportConfig.getIsUp());
                  map.put("name", reportConfig.getName());
                  map.put("updateTime", reportConfig.getUpdateTime());
                  map.put("updateUser", reportConfig.getUpdateUser());
                  list.add(map);
               }
            }

            result.put("pageNum", response.getPageNum());
            result.put("pageSize", response.getPageSize());
            result.put("totalPages", response.getTotalPages());
            result.put("totalRows", response.getTotalRows());
            result.put("rows", list);
         }

         return ResponseResult.success(result, "查询成功!");
      } catch (VieAppServiceException var9) {
         logger.error("分页获取所有报表列表出错", var9);
         return ResponseResult.error(var9.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getReportById"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getReportById(HttpServletRequest request, ReportRequest reportRequest) {
      try {
         reportRequest.setUserId(BaseUtils.getUserId(request));
         reportRequest.setSystemId(this.commonService.getContextPath());
         reportRequest.setDataSource(BaseUtils.getDataSource(request));
         String detailInfo = this.reportService.getReportById(reportRequest);
         return ResponseResult.success(detailInfo, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("根据报表id查询报表详细信息出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/saveReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveReport(ReportRequest reportRequest, HttpServletRequest request) {
      try {
         reportRequest.setUserId(BaseUtils.getUserId(request));
         reportRequest.setSystemId(this.commonService.getContextPath());
         reportRequest.setCreateUser(BaseUtils.getUserName(request));
         reportRequest.setDataSource(BaseUtils.getDataSource(request));
         int reportId = this.reportService.saveReport(reportRequest);
         return ResponseResult.success(reportId, "保存成功!");
      } catch (VieAppServiceException var4) {
         logger.error("保存报表出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/setUsualReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult setUsualReport(ReportUsualRequest reportUsualRequest, HttpServletRequest request) {
      try {
         reportUsualRequest.setUserId(BaseUtils.getUserId(request));
         reportUsualRequest.setSystemId(this.commonService.getContextPath());
         reportUsualRequest.setDataSource(BaseUtils.getDataSource(request));
         this.reportService.setUsualReport(reportUsualRequest);
         return ResponseResult.success("保存成功!");
      } catch (VieAppServiceException var4) {
         logger.error("添加/取消常用报表出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/deleteReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteReport(@RequestParam("id") String id, @RequestParam(value = "reportName",required = false) String reportName, HttpServletRequest request) {
      try {
         ReportDeleteRequest reportDeleteRequest = new ReportDeleteRequest();
         reportDeleteRequest.setDataSource(BaseUtils.getDataSource(request));
         reportDeleteRequest.setId(id);
         reportDeleteRequest.setReportName(reportName);
         this.reportService.deleteReport(reportDeleteRequest);
         return ResponseResult.success("删除成功!");
      } catch (VieAppServiceException var5) {
         logger.error("删除报表出错", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"/setUpReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult setUpReport(ReportTopRequest reportTopRequest, HttpServletRequest request) {
      try {
         reportTopRequest.setUserId(BaseUtils.getUserId(request));
         reportTopRequest.setSystemId(this.commonService.getContextPath());
         reportTopRequest.setDataSource(BaseUtils.getDataSource(request));
         this.reportService.setUpReport(reportTopRequest);
         return ResponseResult.success("操作成功!");
      } catch (VieAppServiceException var4) {
         logger.error("置顶/取消置顶报表出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getTableData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getTableData(ReportTableDataRequest reportTableDataRequest, HttpServletRequest request) throws IOException, ClassNotFoundException {
      try {
         String newParam = this.handleParam(reportTableDataRequest.getTableParams());
         reportTableDataRequest.setTableParams(newParam);
         logger.info("newParam==" + newParam);
         reportTableDataRequest.setUserId(BaseUtils.getUserId(request));
         reportTableDataRequest.setSystemId(this.commonService.getContextPath());
         reportTableDataRequest.setDataSource(BaseUtils.getDataSource(request));
         HashMap<String, Object> response = this.reportChartViewService.getTableData(reportTableDataRequest);
         List rows = (List)response.get("rows");
         List dist = this.handleData(rows);
         response.put("rowsCopy", dist);
         logger.info("response--" + JSON.toJSONString(response));
         return ResponseResult.success(response, "查询成功!");
      } catch (VieAppServiceException var7) {
         logger.error("获取报表表格数据出错", var7);
         return ResponseResult.error(var7.getMessage());
      }
   }

   private String handleParam(String tableParams) throws IOException {
      Map jsonParam = JSON.parseObject(tableParams);
      List<Map> conditions = (List)jsonParam.get("condition");
      Iterator var4 = conditions.iterator();

      while(true) {
         Map condition;
         String field;
         do {
            if (!var4.hasNext()) {
               return JSON.toJSONString(jsonParam);
            }

            condition = (Map)var4.next();
            field = (String)condition.get("filed");
         } while(!field.equals("callNumber") && !field.equals("cardNo") && !field.equals("dnis") && !field.equals("paperId"));

         List<String> values = (List)condition.get("value");
         List<String> newValues = new ArrayList();
         Iterator var9 = values.iterator();

         while(var9.hasNext()) {
            String value = (String)var9.next();
            String newValue = new String(this.base64.decode(value), "UTF-8");
            newValues.add(newValue);
         }

         condition.put("value", newValues);
      }
   }

   private List handleData(List<HashMap> rows) throws IOException, ClassNotFoundException {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(byteOut);
      out.writeObject(rows);
      ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
      ObjectInputStream in = new ObjectInputStream(byteIn);
      List<HashMap> dest = (List)in.readObject();
      Iterator var7 = dest.iterator();

      while(var7.hasNext()) {
         HashMap<String, Object> row = (HashMap)var7.next();
         String newPaperId = null;
         String newCard = null;
         String newDnis = null;
         String newCallNumber = null;
         if (null != (String)row.get("paperId")) {
            newPaperId = this.base64.encodeToString(((String)row.get("paperId")).getBytes("UTF-8"));
         }

         if (null != (String)row.get("cardNo")) {
            newCard = this.base64.encodeToString(((String)row.get("cardNo")).getBytes("UTF-8"));
         }

         if (null != (String)row.get("dnis")) {
            newDnis = this.base64.encodeToString(((String)row.get("dnis")).getBytes("UTF-8"));
         }

         if (null != (String)row.get("callNumber")) {
            newCallNumber = this.base64.encodeToString(((String)row.get("callNumber")).getBytes("UTF-8"));
         }

         row.put("paperId", newPaperId);
         row.put("cardNo", newCard);
         row.put("dnis", newDnis);
         row.put("callNumber", newCallNumber);
      }

      return dest;
   }

   @RequestMapping(
      value = {"/getLineColumData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getLineColumData(ReportTableDataRequest reportTableDataRequest, HttpServletRequest request) {
      try {
         reportTableDataRequest.setUserId(BaseUtils.getUserId(request));
         reportTableDataRequest.setSystemId(this.commonService.getContextPath());
         reportTableDataRequest.setDataSource(BaseUtils.getDataSource(request));
         List<Map<String, Object>> list = this.reportChartViewService.getLineColumData(reportTableDataRequest);
         return ResponseResult.success(list, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("获取报表柱折图数据出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getPieData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getPieData(ReportTableDataRequest reportTableDataRequest, HttpServletRequest request) {
      try {
         reportTableDataRequest.setUserId(BaseUtils.getUserId(request));
         reportTableDataRequest.setSystemId(this.commonService.getContextPath());
         reportTableDataRequest.setDataSource(BaseUtils.getDataSource(request));
         Map<String, Object> result = this.reportChartViewService.getPieData(reportTableDataRequest);
         return ResponseResult.success(result, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("获取报表饼图数据出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/checkReportName"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult checkReportName(HttpServletRequest request, ReportSimpleRequest reportSimpleRequest) {
      try {
         reportSimpleRequest.setUserName(BaseUtils.getUserName(request));
         reportSimpleRequest.setSystemId(this.commonService.getContextPath());
         reportSimpleRequest.setDataSource(BaseUtils.getDataSource(request));
         reportSimpleRequest.setUserId(BaseUtils.getUserId(request));
         boolean flag = this.reportService.checkReportName(reportSimpleRequest);
         return !flag ? ResponseResult.error("报表名称" + reportSimpleRequest.getReportName() + "已存在！") : ResponseResult.success("查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("验证报表名称是否已存在出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/checkExpress"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult checkExpress(@RequestParam("expression") String expression) {
      try {
         this.reportService.checkExpress(expression);
         return ResponseResult.success("查询成功!");
      } catch (VieAppServiceException var3) {
         logger.error("验证报表显示字段计算项表达式出错", var3);
         return ResponseResult.error(var3.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getComputerField"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getComputerField() {
      try {
         String computerFiled = this.reportService.getComputerField();
         return ResponseResult.success(computerFiled, "查询成功!");
      } catch (VieAppServiceException var2) {
         logger.error("生成计算项唯一field出错", var2);
         return ResponseResult.error(var2.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getAllMeasure"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getAllMeasure() {
      try {
         return ResponseResult.success(this.reportService.getMeasureList(), "查询成功!");
      } catch (Exception var2) {
         logger.error("查询所有指标出错", var2);
         return ResponseResult.error("查询所有指标出错");
      }
   }
}
