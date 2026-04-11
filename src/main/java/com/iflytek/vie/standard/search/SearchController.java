package com.iflytek.vie.standard.search;

import com.iflytek.vie.app.api.esdata.EsDataService;
import com.iflytek.vie.app.api.search.SearchService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.common.ColumnMap;
import com.iflytek.vie.app.pojo.search.ESSearchRequest;
import com.iflytek.vie.app.pojo.search.ESSearchResponse;
import com.iflytek.vie.app.pojo.search.SearchKeyWordRequest;
import com.iflytek.vie.app.pojo.search.SearchKeyWordResponse;
import com.iflytek.vie.app.pojo.search.SearchRequest;
import com.iflytek.vie.app.pojo.search.SearchResultResponse;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.config.LoadConfig;
import com.iflytek.vie.constants.IndexConstants;
import com.iflytek.vie.custom.api.search.SearchCustomService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.ExportExcel;
import com.iflytek.vie.utils.StringUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("searchController")
@RequestMapping({"/search"})
public class SearchController {
   private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
   @Autowired
   private SearchService searchService;
   @Autowired
   private SearchCustomService searchCustomService;
   @Autowired
   private CommonService commonService;
   @Autowired
   private EsDataService esDataService;

   @RequestMapping(
      value = {"queryTextSearchList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryTextSearchList(SearchRequest searchRequest, HttpServletRequest request) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            searchRequest.setDataSource(dataSource);
            searchRequest.setSystemId(LoadConfig.getConfigProperty("systemName"));
            searchRequest.setUserId(BaseUtils.getUserId(request));
            searchRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
            searchRequest.setSelColMap(this.commonService.getSelColMap());
            SearchResultResponse result = this.searchService.queryTextSearchList(searchRequest);
            if (result != null) {
               result.setMaxExportNum(Long.parseLong(LoadConfig.getConfigProperty("maxExportNum")));
            }

            return ResponseResult.success(result, "查询成功!");
         }
      } catch (Exception var5) {
         logger.error("【文本搜索】出现异常", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"queryTableSearchList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryTableSearchList(SearchRequest searchRequest, HttpServletRequest request) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            searchRequest.setDataSource(dataSource);
            searchRequest.setSystemId(LoadConfig.getConfigProperty("systemName"));
            searchRequest.setUserId(BaseUtils.getUserId(request));
            searchRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
            SearchResultResponse result = this.searchService.queryTableSearchList(searchRequest);
            if (result != null) {
               result.setMaxExportNum(Long.parseLong(LoadConfig.getConfigProperty("maxExportNum")));
            }

            return ResponseResult.success(result, "查询成功!");
         }
      } catch (Exception var5) {
         logger.error("【列表搜索】出现异常", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"queryModelKeyWord"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryModelKeyWord(HttpServletRequest request, SearchKeyWordRequest searchKeyWordRequest) {
      try {
         searchKeyWordRequest.setDataSource(BaseUtils.getDataSource(request));
         searchKeyWordRequest.setUserId(BaseUtils.getUserId(request));
         SearchKeyWordResponse result = this.searchService.queryModelKeyWord(searchKeyWordRequest);
         return ResponseResult.success(result, "查询成功!");
      } catch (Exception var4) {
         logger.error("【模型关键字获取】出现异常", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"exportSearchText"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult exportSearchText(HttpServletRequest request, HttpServletResponse response, SearchRequest searchRequest) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            searchRequest.setDataSource(dataSource);
            Long queryStart = System.currentTimeMillis();
            searchRequest.setSystemId(LoadConfig.getConfigProperty("systemName"));
            searchRequest.setUserId(BaseUtils.getUserId(request));
            searchRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
            searchRequest.setMaxExportNum(Long.parseLong(LoadConfig.getConfigProperty("maxExportNum")));
            searchRequest.setSelColMap(this.commonService.getSelColMap());
            SearchResultResponse result = this.searchService.exportSearchText(searchRequest);
            Long queryEnd = System.currentTimeMillis();
            logger.info("导出搜索文本查询数据用时：" + (queryEnd - queryStart));
            List<ColumnMap> showCols = new ArrayList();
            int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
            if (insightType == 1) {
               showCols.add(new ColumnMap("任务号", "contact_id"));
            } else {
               showCols.add(new ColumnMap("流水号", "contact_id"));
            }

            showCols.add(new ColumnMap("通话开始时间", "timeFormat"));
            showCols.add(new ColumnMap("通话时长", "duration"));
            showCols.add(new ColumnMap("文本内容", "content"));
            this.dataExport(result.getRows(), response, showCols, searchRequest.getContent());
            Long exportEnd = System.currentTimeMillis();
            logger.info("导出搜索文本写入数据用时：" + (exportEnd - queryEnd));
            logger.info("成功导出数据" + result.getTotal() + "条");
            return ResponseResult.success("操作成功!");
         }
      } catch (Exception var11) {
         logger.error("【导出搜索文本】出现异常", var11);
         return ResponseResult.error(var11.getMessage());
      }
   }

   @RequestMapping(
      value = {"exportSearchTable"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult exportSearchTable(HttpServletRequest request, HttpServletResponse response, SearchRequest searchRequest) {
      try {
         Long queryStart = System.currentTimeMillis();
         searchRequest.setDataSource(BaseUtils.getDataSource(request));
         searchRequest.setSystemId(LoadConfig.getConfigProperty("systemName"));
         searchRequest.setUserId(BaseUtils.getUserId(request));
         searchRequest.setSearchType(Integer.parseInt(LoadConfig.getConfigProperty("searchType")));
         searchRequest.setMaxExportNum(Long.parseLong(LoadConfig.getConfigProperty("maxExportNum")));
         SearchResultResponse result = this.searchCustomService.exportSearchTable(searchRequest);
         Long queryEnd = System.currentTimeMillis();
         logger.info("导出搜索列表查询数据用时：" + (queryEnd - queryStart));
         this.dataExport(result.getRows(), response, result.getColumns(), searchRequest.getContent());
         Long exportEnd = System.currentTimeMillis();
         logger.info("导出搜索列表写入数据用时：" + (exportEnd - queryEnd));
         logger.info("成功导出数据" + result.getTotal() + "条");
         return ResponseResult.success("操作成功!");
      } catch (Exception var8) {
         logger.error("【导出搜索列表】出现异常", var8);
         return ResponseResult.error(var8.getMessage());
      }
   }

   public String dataExport(List<LinkedHashMap<String, Object>> dataEx, HttpServletResponse response, List<ColumnMap> headers, String content) throws UnsupportedEncodingException {
      SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
      String now = format.format(new Date());
      String exportFileName = "搜索导出_" + content + "_" + now + ".xlsx";
      exportFileName = new String(exportFileName.getBytes("gbk"), "iso-8859-1");
      response.setHeader("Content-Disposition", "attachment;filename=".concat(exportFileName));
      response.setHeader("Connection", "close");
      response.setHeader("Content-Type", "application/vnd.ms-excel");

      try {
         OutputStream out = response.getOutputStream();
         ExportExcel.exportExcel(exportFileName, headers, dataEx, out);
         out.close();
      } catch (FileNotFoundException var9) {
         logger.error("【输出流导出Excel】出现异常", var9);
      } catch (IOException var10) {
         logger.error("【输出流导出Excel】出现异常", var10);
      }

      return exportFileName;
   }

   @RequestMapping(
      value = {"getEsDetailData"},
      method = {RequestMethod.POST, RequestMethod.GET}
   )
   @ResponseBody
   public ResponseResult getEsDetailData(HttpServletRequest request, ESSearchRequest esSearchRequest, @RequestParam("startTime") Long startTime, @RequestParam("endTime") Long endTime, @RequestParam("pageNum") Long pageNum, @RequestParam("pageSize") Long pageSize) {
      logger.info("getEsDetailData(controler层) 方法开始响应");
      ResponseResult result = new ResponseResult();

      try {
         esSearchRequest.setPageNum(pageNum);
         esSearchRequest.setPageSize(pageSize);
         esSearchRequest.setStartTime(startTime);
         esSearchRequest.setEndTime(endTime);
         esSearchRequest.setDataSource(BaseUtils.getDataSource(request));
         ESSearchResponse esSearchResponse = this.esDataService.getEsDetailData(esSearchRequest);
         result.setMessage("响应成功");
         result.setValue(esSearchResponse);
         result.setSuccess(true);
      } catch (VieAppServiceException var9) {
         logger.error(var9.getMessage(), var9);
         result.setSuccess(false);
         result.setMessage(var9.getMessage());
      }

      logger.info("getEsDetailData(controler层) 方法响应成功");
      return result;
   }

   @RequestMapping(
      value = {"getEsModelData"},
      method = {RequestMethod.POST, RequestMethod.GET}
   )
   @ResponseBody
   public ResponseResult getEsModelData(HttpServletRequest request, ESSearchRequest esSearchRequest, @RequestParam("id") String id, @RequestParam("pageNum") Long pageNum, @RequestParam("pageSize") Long pageSize) {
      logger.info("getEsModelData(controler层) 方法开始响应");
      ResponseResult result = new ResponseResult();

      try {
         esSearchRequest.setId(id);
         esSearchRequest.setPageNum(pageNum);
         esSearchRequest.setPageSize(pageSize);
         esSearchRequest.setDataSource(BaseUtils.getDataSource(request));
         ESSearchResponse esSearchResponse = this.esDataService.getEsModelData(esSearchRequest);
         result.setMessage("响应成功");
         result.setValue(esSearchResponse);
         result.setSuccess(true);
      } catch (VieAppServiceException var8) {
         logger.error(var8.getMessage(), var8);
         result.setSuccess(false);
         result.setMessage(var8.getMessage());
      }

      logger.info("getEsModelData(controler层) 方法响应成功");
      return result;
   }
}
