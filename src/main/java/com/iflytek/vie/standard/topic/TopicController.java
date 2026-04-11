package com.iflytek.vie.standard.topic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.vie.app.api.topic.FunnelService;
import com.iflytek.vie.app.api.topic.TopicExportService;
import com.iflytek.vie.app.api.topic.TopicService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.permission.UserBasicInfo;
import com.iflytek.vie.app.pojo.topic.AddMarkRequest;
import com.iflytek.vie.app.pojo.topic.BaseExportRequest;
import com.iflytek.vie.app.pojo.topic.CreateTopicRequest;
import com.iflytek.vie.app.pojo.topic.DeleteMarkRequest;
import com.iflytek.vie.app.pojo.topic.DeleteTopicRequest;
import com.iflytek.vie.app.pojo.topic.EditPathRequest;
import com.iflytek.vie.app.pojo.topic.EditTopicRequest;
import com.iflytek.vie.app.pojo.topic.EditTopicResponse;
import com.iflytek.vie.app.pojo.topic.ExportFunnelRequest;
import com.iflytek.vie.app.pojo.topic.ExportFunnelResponse;
import com.iflytek.vie.app.pojo.topic.FunnelCallResponse;
import com.iflytek.vie.app.pojo.topic.FunnelTableResponse;
import com.iflytek.vie.app.pojo.topic.GetPathDataRequest;
import com.iflytek.vie.app.pojo.topic.GetPathDataResponse;
import com.iflytek.vie.app.pojo.topic.GetPathValueRequest;
import com.iflytek.vie.app.pojo.topic.HotWordRankResponse;
import com.iflytek.vie.app.pojo.topic.HotWordTaskRequest;
import com.iflytek.vie.app.pojo.topic.LoadCountRequest;
import com.iflytek.vie.app.pojo.topic.MarkQueryRequest;
import com.iflytek.vie.app.pojo.topic.MarkQueryResponse;
import com.iflytek.vie.app.pojo.topic.ModelStatusRequest;
import com.iflytek.vie.app.pojo.topic.ModelStatusResponse;
import com.iflytek.vie.app.pojo.topic.TogetherPathRequest;
import com.iflytek.vie.app.pojo.topic.TopicExportResponse;
import com.iflytek.vie.app.pojo.topic.TopicPathRequest;
import com.iflytek.vie.app.pojo.topic.TopicPathResponse;
import com.iflytek.vie.app.pojo.topic.UpdateTopicDimensionRequest;
import com.iflytek.vie.app.pojo.topic.UpdateTopicNameRequest;
import com.iflytek.vie.app.pojo.topic.UpdateTopicTimeRequest;
import com.iflytek.vie.app.pojo.topicgroup.ColumnMap;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.custom.api.topic.TopicCustomService;
import com.iflytek.vie.custom.pojo.PageCustomRequest;
import com.iflytek.vie.custom.pojo.TopicInfosResponse;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.DesensitizationUtils;
import com.iflytek.vie.utils.StringUtils;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("topicController")
@RequestMapping({"/topic"})
public class TopicController {
   private static final Logger logger = LoggerFactory.getLogger(TopicController.class);
   @Autowired
   private TopicService topicService;
   @Autowired
   private TopicCustomService topicCustomService;
   @Autowired
   private CommonService commonService;
   @Autowired
   private TopicExportService topicExportService;
   @Autowired
   private FunnelService funnelService;

   @RequestMapping(
      value = {"/findAllTopics"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult findAllTopics(PageCustomRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();
      if (null == params) {
         response.setMessage("入参不正确");
         return response;
      } else {
         try {
            params.setDataSource(BaseUtils.getDataSource(request));
            params.setUserId(((UserBasicInfo)request.getSession().getAttribute("sys_user_info")).getId().toString());
            params.setAccountName(((UserBasicInfo)request.getSession().getAttribute("sys_user_info")).getAccountName());
            TopicInfosResponse result = this.topicCustomService.findAllTopicsByAuth(params);
            response.setValue(result);
            response.setSuccess(true);
            return response;
         } catch (Exception var5) {
            response.setMessage(var5.getMessage());
            return response;
         }
      }
   }

   @RequestMapping(
      value = {"/createTopic"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult createTopic(CreateTopicRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();
      if (null == params) {
         response.setMessage("入参不正确");
         return response;
      } else {
         try {
            String userName = BaseUtils.getUserName(request);
            params.setDataSource(BaseUtils.getDataSource(request));
            params.setRoleId(String.valueOf(this.commonService.getRoleIds(userName, BaseUtils.getDataSource(request)).get(0)));
            params.setLoginUserName(userName);
            params.setUserId(BaseUtils.getUserId(request));
            TopicPathResponse result = this.topicService.createTopic(params);
            response.setValue(result);
            response.setSuccess(true);
            return response;
         } catch (Exception var6) {
            logger.error("【createTopic】方法异常", var6);
            response.setMessage(var6.getMessage());
            return response;
         }
      }
   }

   @RequestMapping(
      value = {"/updateTopic"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateTopic(UpdateTopicDimensionRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();
      if (null == params) {
         response.setMessage("入参不正确");
         return response;
      } else {
         try {
            String userName = BaseUtils.getUserName(request);
            params.setLoginUserName(userName);
            params.setSystemId(this.commonService.getContextPath());
            params.setUserId(BaseUtils.getUserId(request));
            params.setDataSource(BaseUtils.getDataSource(request));
            this.topicService.updateTopic(params);
            response.setSuccess(true);
            return response;
         } catch (VieAppServiceException var5) {
            response.setMessage(var5.getMessage());
            return response;
         }
      }
   }

   @RequestMapping(
      value = {"/editTopic"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult editTopic(String topicId, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         EditTopicRequest params = new EditTopicRequest();
         params.setContext(this.commonService.getContextPath());
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setTopicId(topicId);
         params.setUserId(BaseUtils.getUserId(request));
         EditTopicResponse result = this.topicService.editTopic(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var6) {
         response.setMessage(var6.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getPathValue"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getPathValue(GetPathValueRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setSystemId(this.commonService.getContextPath());
         params.setUserId(BaseUtils.getUserId(request));
         int result = this.topicService.getPathValue(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (Exception var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getPathData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getPathData(GetPathDataRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setAccountName(BaseUtils.getUserName(request));
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setSystemId(this.commonService.getContextPath());
         params.setUserId(BaseUtils.getUserId(request));
         params.setExport(false);
         GetPathDataResponse result = this.topicService.getPathData(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/updateTopicName"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateTopicName(UpdateTopicNameRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         this.topicService.updateTopicName(params);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/updateTopicTime"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateTopicTime(UpdateTopicTimeRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         this.topicService.updateTopicTime(params);
         response.setSuccess(true);
         return response;
      } catch (Exception var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/deleteTopics"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteTopics(DeleteTopicRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         this.topicService.deleteTopics(params);
         response.setSuccess(true);
         return response;
      } catch (Exception var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/fetchPreDimension"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public ResponseResult fetchPreDimension() {
      ResponseResult response = new ResponseResult();

      try {
         String result = this.topicService.fetchPreDimension();
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var3) {
         response.setMessage(var3.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/addMark"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addMark(AddMarkRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         this.topicService.addMark(params);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/editPath"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult editPath(EditPathRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         int result = this.topicService.editPath(params);
         response.setValue(result);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/deletePath"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deletePath(TopicPathRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setSystemId(this.commonService.getContextPath());
         params.setUserId(BaseUtils.getUserId(request));
         params.setDataSource(BaseUtils.getDataSource(request));
         this.topicService.deletePath(params);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getMarkData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getMarkData(MarkQueryRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setAccountName(BaseUtils.getUserName(request));
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setUserId(BaseUtils.getUserId(request));
         params.setSystemId(this.commonService.getContextPath());
         MarkQueryResponse result = this.topicService.getMarkData(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/deleteMark"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteMark(DeleteMarkRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         this.topicService.deleteMark(params);
         response.setSuccess(true);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getLoadCount"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getLoadCount(LoadCountRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setUserId(BaseUtils.getUserId(request));
         params.setSystemId(this.commonService.getContextPath());
         int result = this.topicService.getLoadCount(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/getModelStatus"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getModelStatus(ModelStatusRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setUserId(BaseUtils.getUserId(request));
         params.setSystemId(this.commonService.getContextPath());
         List<ModelStatusResponse> result = this.topicService.getModelStatus(params);
         response.setSuccess(true);
         response.setValue(result);
         return response;
      } catch (VieAppServiceException var5) {
         response.setMessage(var5.getMessage());
         return response;
      }
   }

   @RequestMapping(
      value = {"/exportCluster"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult exportCluster(TogetherPathRequest params, HttpServletResponse response, HttpServletRequest request) {
      ResponseResult result = new ResponseResult();

      try {
         String moduleName = "聚类分析";
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setUserId(BaseUtils.getUserId(request));
         params.setSystemId(this.commonService.getContextPath());
         TopicExportResponse exportResponse = this.topicExportService.getExportDataOfCluster(params);
         logger.info("修改前：exportResponse==" + JSON.toJSONString(exportResponse));
         this.handleData(exportResponse);
         this.getExportFile(result, moduleName, exportResponse, request, response, params.getImgCode());
      } catch (Exception var7) {
         result.setMessage(var7.getMessage());
         logger.error("【getExportDataOfCluster】方法调用出错", var7);
      }

      return result;
   }

   @RequestMapping(
      value = {"/exportBase"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult exportBase(BaseExportRequest params, HttpServletResponse response, HttpServletRequest request) {
      ResponseResult result = new ResponseResult();

      try {
         String moduleName = "基础分析";
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setUserId(BaseUtils.getUserId(request));
         params.setSystemId(this.commonService.getContextPath());
         TopicExportResponse exportResponse = this.topicExportService.exportTopicBaseData(params);
         this.handleData(exportResponse);
         this.getExportFile(result, moduleName, exportResponse, request, response, params.getSvgCode());
      } catch (Exception var7) {
         result.setMessage(var7.getMessage());
         logger.error("【exportTopicBaseData】方法调用出错", var7);
      }

      return result;
   }

   private void handleData(TopicExportResponse exportResponse) {
      List<Map<String, Object>> dataList = exportResponse.getDataList();
      if (CollectionUtils.isNotEmpty(dataList)) {
         Iterator var3 = dataList.iterator();

         while(var3.hasNext()) {
            Map<String, Object> map = (Map)var3.next();
            this.handleParam(map);
         }
      }

      logger.info("修改后 ：exportResponse ==" + JSON.toJSONString(exportResponse));
   }

   private void handleParam(Map<String, Object> map) {
      String paperId = (String)map.get("paperId");
      String cardNo = (String)map.get("cardNo");
      String dnis = (String)map.get("dnis");
      String callNumber = (String)map.get("callNumber");
      String newPaperId = DesensitizationUtils.specialCharactorReplace("paperId", paperId);
      String newCardNo = DesensitizationUtils.specialCharactorReplace("cardNo", cardNo);
      String newDnis = DesensitizationUtils.specialCharactorReplace("dnis", dnis);
      String newCallNumber = DesensitizationUtils.specialCharactorReplace("callNumber", callNumber);
      map.put("paperId", newPaperId);
      map.put("cardNo", newCardNo);
      map.put("dnis", newDnis);
      map.put("callNumber", newCallNumber);
   }

   private void getExportFile(ResponseResult result, String moduleName, TopicExportResponse exportResponse, HttpServletRequest request, HttpServletResponse response, String pngCod) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
      String fileCreateTime = simpleDateFormat.format(new Date());
      Map<String, Object> pathMap = exportResponse.getTopicPathMap();
      String topicName = pathMap.get("topicName").toString();
      if (exportResponse.isPassLimit()) {
         result.setMessage("单次只能导出" + exportResponse.getLimitNum() + "条数据，请重新筛选数据后导出！");
         result.setSuccess(false);
      } else {
         String userAgent = request.getHeader("User-Agent");
         String zipName = topicName + moduleName + "-" + fileCreateTime;

         try {
            File zip = this.exportTopicData(exportResponse, moduleName, pngCod, fileCreateTime, zipName);
            zipName = zip.getName();
            if (!userAgent.contains("MSIE") && !userAgent.contains("Trident")) {
               zipName = new String(zipName.getBytes("UTF-8"), "ISO-8859-1");
            } else {
               zipName = URLEncoder.encode(zipName, "UTF-8");
            }

            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + zipName);
            response.setContentType("application/octet-stream");
            OutputStream out = response.getOutputStream();
            FileInputStream inStream = new FileInputStream(zip);
            byte[] buf = new byte[4096];

            int readLength;
            while((readLength = inStream.read(buf)) != -1) {
               out.write(buf, 0, readLength);
            }

            inStream.close();
            out.close();
            if (zip.exists()) {
               zip.delete();
            }

            result.setSuccess(true);
         } catch (Exception var18) {
            result.setMessage(var18.getMessage());
            logger.error("【getExportFile】方法异常", var18);
         }

      }
   }

   private void ZipFiles(List<File> srcfile, File zipfile) throws Exception {
      byte[] buf = new byte[1024];

      try {
         ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
         Iterator var5 = srcfile.iterator();

         while(var5.hasNext()) {
            File file = (File)var5.next();
            FileInputStream in = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(file.getName()));

            int len;
            while((len = in.read(buf)) > 0) {
               out.write(buf, 0, len);
            }

            out.closeEntry();
            in.close();
         }

         out.close();
      } catch (IOException var9) {
         logger.info("zip打包方法ZipFiles异常");
         throw new VieAppServiceException("zip打包方法ZipFiles异常", var9);
      }
   }

   private File exportTopicData(TopicExportResponse response, String moduleName, String imgCode, String fileCreateTime, String zipName) throws VieAppServiceException {
      File zip = new File(zipName + ".zip");
      List<Map<String, Object>> dataLst = response.getDataList();
      Map<String, Object> pathMap = response.getTopicPathMap();
      String topicName = pathMap.get("topicName").toString();
      XSSFWorkbook workbook = new XSSFWorkbook();
      XSSFSheet pathSheet = workbook.createSheet();
      XSSFSheet callSheet = workbook.createSheet();
      workbook.setSheetName(0, topicName + "路径信息");
      workbook.setSheetName(1, topicName + moduleName + "数据列表");
      XSSFCellStyle normalStyle = this.createCostomCellStyle(workbook, (short)32767, false, new XSSFColor(new Color(119, 170, 85)));
      XSSFCellStyle titleStyle = this.createCostomCellStyle(workbook, (short)32767, true, new XSSFColor(new Color(119, 170, 85)));
      List<ColumnMap> rowHead = new ArrayList();
      ColumnMap cmp = new ColumnMap("路径名称", "pathName");
      rowHead.add(cmp);
      cmp = new ColumnMap("路径标签", "pathType");
      rowHead.add(cmp);
      this.createPathListSheet(pathSheet, normalStyle, titleStyle, rowHead, pathMap);
      rowHead.clear();
      List<ColumnMap> callRowHead = response.getColumnList();
      this.createCallListSheet(callSheet, normalStyle, titleStyle, callRowHead, dataLst);

      try {
         String excelPath = topicName + moduleName + "数据导出-" + fileCreateTime + ".xlsx";
         FileOutputStream excleFout = new FileOutputStream(excelPath);
         workbook.write(excleFout);
         excleFout.close();
         String imgPath = topicName + moduleName + "图表导出-" + fileCreateTime + ".png";
         List<File> srcfile = new ArrayList();
         srcfile.add(new File(excelPath));
         if (StringUtils.isNotNullAndEmpry(imgCode)) {
            if (moduleName.equals("基础分析")) {
               this.convertToPng(imgCode, imgPath);
            } else {
               this.exportImgForBase64(imgCode, imgPath);
            }

            srcfile.add(new File(imgPath));
         }

         this.ZipFiles(srcfile, zip);
         Iterator var21 = srcfile.iterator();

         while(var21.hasNext()) {
            File file = (File)var21.next();
            file.delete();
         }
      } catch (Exception var23) {
         logger.error("【exportTopicData】方法异常", var23);
      }

      return zip;
   }

   private XSSFCellStyle createCostomCellStyle(XSSFWorkbook workbook, short fontColor, boolean isTitle, XSSFColor titleColor) {
      XSSFFont titleFont = workbook.createFont();
      XSSFCellStyle titleStyle = workbook.createCellStyle();
      workbook.createCellStyle();
      titleStyle.setAlignment((short)2);
      titleStyle.setVerticalAlignment((short)1);
      if (isTitle) {
         titleStyle.setFillPattern((short)1);
         titleStyle.setFillForegroundColor(titleColor);
      }

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

   private void createSheetLength(Sheet sheet, int length) {
      for(int i = 0; i < length; ++i) {
         sheet.setColumnWidth(i, 12800);
      }

   }

   private void createPathListSheet(XSSFSheet callSheet, XSSFCellStyle normalStyle, XSSFCellStyle titleStyle, List<ColumnMap> rowHead, Map<String, Object> pathMap) throws VieAppServiceException {
      List<HashMap<String, Object>> dataInfoList = (List)pathMap.get("pathData");
      String topicName = pathMap.get("topicName").toString();
      String topicPath = pathMap.get("topicPath").toString();
      XSSFRow rowHeadRow = callSheet.createRow(0);
      XSSFCell xc = rowHeadRow.createCell(0);
      xc.setCellStyle(normalStyle);
      xc.setCellType(1);
      xc.setCellValue("专题名称");
      xc = rowHeadRow.createCell(1);
      xc.setCellStyle(normalStyle);
      xc.setCellType(1);
      xc.setCellValue(topicName);
      rowHeadRow = callSheet.createRow(1);
      xc = rowHeadRow.createCell(0);
      xc.setCellStyle(normalStyle);
      xc.setCellType(1);
      xc.setCellValue("时间区间");
      xc = rowHeadRow.createCell(1);
      xc.setCellStyle(normalStyle);
      xc.setCellType(1);
      xc.setCellValue(topicPath);
      rowHeadRow = callSheet.createRow(2);
      List<String> sortString = new ArrayList();

      int rowHang;
      for(rowHang = 0; rowHang < rowHead.size(); ++rowHang) {
         ColumnMap cmap = (ColumnMap)rowHead.get(rowHang);
         sortString.add(cmap.getColumn());
         this.changeFontStyle(rowHeadRow, titleStyle, rowHang, cmap.getColumnName());
      }

      this.createSheetLength(callSheet, sortString.size());
      rowHang = 3;

      for(int i = 0; i < dataInfoList.size(); ++i) {
         XSSFRow rows = callSheet.createRow(rowHang++);
         HashMap<String, Object> dataMaps = (HashMap)dataInfoList.get(i);

         for(int j = 0; j < sortString.size(); ++j) {
            String headName = (String)sortString.get(j);
            String rowValue = dataMaps.get(headName) == null ? "" : dataMaps.get(headName).toString();
            this.changeFontStyle(rows, normalStyle, j, rowValue);
         }
      }

   }

   private void createCallListSheet(XSSFSheet callSheet, XSSFCellStyle normalStyle, XSSFCellStyle titleStyle, List<ColumnMap> rowHead, List<Map<String, Object>> dataList) throws VieAppServiceException {
      try {
         XSSFRow rowHeadRow = callSheet.createRow(0);
         List<String> sortString = new ArrayList();

         for(int i = 0; i < rowHead.size(); ++i) {
            ColumnMap cmap = (ColumnMap)rowHead.get(i);
            sortString.add(cmap.getColumn());
            this.changeFontStyle(rowHeadRow, titleStyle, i, cmap.getColumnName());
         }

         this.createSheetLength(callSheet, sortString.size());
         String dataJson = JSON.toJSON(dataList).toString();
         List<HashMap<String, Object>> dataInfoList = this.jsonToMap(dataJson);
         int rowHang = 1;

         for(int i = 0; i < dataInfoList.size(); ++i) {
            XSSFRow rows = callSheet.createRow(rowHang++);
            LinkedHashMap<String, Object> dataMaps = (LinkedHashMap)dataInfoList.get(i);

            for(int j = 0; j < sortString.size(); ++j) {
               String headName = (String)sortString.get(j);
               String rowValue = dataMaps.get(headName) == null ? "" : dataMaps.get(headName).toString();
               this.changeFontStyle(rows, normalStyle, j, rowValue);
            }
         }
      } catch (Exception var17) {
         logger.error("【createCallListSheet】方法异常", var17);
      }

   }

   private void changeFontStyle(XSSFRow row, XSSFCellStyle titleStyle, int cellIndex, String cellValue) {
      XSSFCell xc = row.createCell(cellIndex);
      xc.setCellStyle(titleStyle);
      xc.setCellValue(cellValue);
   }

   private void createColumnHead(XSSFSheet sheet, String[] columNames, XSSFCellStyle cellStyle) {
      XSSFRow row = sheet.createRow(0);

      for(int i = 0; i < columNames.length; ++i) {
         XSSFCell cell = row.createCell(i);
         cell.setCellStyle(cellStyle);
         cell.setCellValue(columNames[i]);
      }

   }

   private boolean exportImgForBase64(String imgCode, String imgPath) {
      if (imgCode == null) {
         return false;
      } else {
         imgCode = imgCode.substring(imgCode.indexOf(",") + 1, imgCode.length()).replaceAll(" ", "+");

         try {
            byte[] b = Base64.decodeBase64((new String(imgCode)).getBytes());

            for(int i = 0; i < b.length; ++i) {
               if (b[i] < 0) {
                  b[i] = (byte)(b[i] + 256);
               }
            }

            OutputStream out = new FileOutputStream(imgPath);
            out.write(b);
            out.flush();
            out.close();
            return true;
         } catch (Exception var5) {
            return false;
         }
      }
   }

   private List<HashMap<String, Object>> jsonToMap(String jsonString) throws Exception {
      List<HashMap<String, Object>> jsonList = new ArrayList();
      if (jsonString != null && !"".equals(jsonString)) {
         ObjectMapper mapper = new ObjectMapper();
         jsonList = (List)mapper.readValue(jsonString, List.class);
      } else {
         return jsonList;
      }

      return jsonList;
   }

   private void convertToPng(String svgCode, String pngFilePath) throws Exception {
      File file = new File(pngFilePath);
      FileOutputStream outputStream = null;

      try {
         file.createNewFile();
         outputStream = new FileOutputStream(file);
         this.convertToPng(svgCode, (OutputStream)outputStream);
      } catch (Exception var13) {
         throw var13;
      } finally {
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (IOException var12) {
               logger.error("【将svg字符串转换为png，输出到路径中】方法异常", var12);
            }
         }

      }

   }

   private void convertToPng(String svgCode, OutputStream outputStream) throws IOException, TranscoderException {
      try {
         byte[] bytes = svgCode.getBytes("UTF-8");
         PNGTranscoder t = new PNGTranscoder();
         TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
         TranscoderOutput output = new TranscoderOutput(outputStream);
         t.transcode(input, output);
         outputStream.flush();
      } finally {
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (IOException var12) {
               logger.error("【将svgCode转换成png文件，直接输出到流中】方法异常", var12);
            }
         }

      }

   }

   @RequestMapping(
      value = {"/exportTopicHotWordData"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult exportTopicHotWordData(HttpServletRequest request, HttpServletResponse response, HotWordTaskRequest hotWordTaskRequest) {
      ResponseResult res = new ResponseResult();
      if (hotWordTaskRequest == null) {
         res.setSuccess(false);
         res.setMessage("入参为空");
         return res;
      } else {
         ServletOutputStream out = null;
         FileInputStream fis = null;
         BufferedInputStream bis = null;
         File zipFile = null;
         String userAgent = request.getHeader("User-Agent");
         String path = System.getProperty("user.dir");

         try {
            hotWordTaskRequest.setDataSource(BaseUtils.getDataSource(request));
            hotWordTaskRequest.setUserId(BaseUtils.getUserId(request));
            hotWordTaskRequest.setSystemId(this.commonService.getContextPath());
            hotWordTaskRequest.setIfExport(true);
            File file = this.exportHotWordStat(hotWordTaskRequest, path);
            if (file != null && file.exists()) {
               String fileName = file.getName();
               zipFile = new File(file.getAbsolutePath());
               if (!userAgent.contains("MSIE") && !userAgent.contains("Trident")) {
                  fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
               } else {
                  fileName = URLEncoder.encode(fileName, "UTF-8");
               }

               response.reset();
               response.setHeader("Content-disposition", "attachment; filename=" + fileName);
               response.setContentType("application/octet-stream");
               out = response.getOutputStream();
               fis = new FileInputStream(file);
               bis = new BufferedInputStream(fis);
               byte[] b = new byte[1024];

               int size;
               while((size = bis.read(b)) != -1) {
                  out.write(b, 0, size);
               }

               res.setSuccess(true);
            }

            res.setSuccess(false);
            res.setMessage("获取数据为空，无任务文件");
         } catch (Exception var23) {
            res.setSuccess(false);
            res.setMessage(var23.getMessage());
         } finally {
            try {
               if (bis != null) {
                  bis.close();
               }

               if (fis != null) {
                  fis.close();
               }

               if (out != null) {
                  out.flush();
                  out.close();
               }

               if (zipFile != null && zipFile.exists()) {
                  zipFile.delete();
               }
            } catch (Exception var22) {
               res.setSuccess(false);
               res.setMessage(var22.getMessage());
            }

         }

         return res;
      }
   }

   private File exportHotWordStat(HotWordTaskRequest HotWordTaskRequest, String path) throws VieAppServiceException {
      Map<String, Object> map = this.topicExportService.queryHotWordStatForExport(HotWordTaskRequest);
      if (map.isEmpty()) {
         return null;
      } else {
         try {
            if (map.get("topicPath") != null) {
               Map<String, Object> pathMap = (Map)map.get("topicPath");
               String topicName = pathMap.get("topicName").toString();
               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
               String time = simpleDateFormat.format(new Date());
               List<File> excelFiles = new ArrayList();
               List<HotWordRankResponse> fVoiceList = (List)map.get("focusKwd/voice");
               List<HotWordRankResponse> fKwdList = (List)map.get("focusKwd/kwd");
               List<HotWordRankResponse> sVoiceList = (List)map.get("systemKwd/voice");
               List<HotWordRankResponse> sKwdList = (List)map.get("systemKwd/kwd");
               File focusFile = this.exportHotWordStatBytype(time, fKwdList, fVoiceList, "focusKwd", pathMap, path);
               excelFiles.add(focusFile);
               File sysFile = this.exportHotWordStatBytype(time, sKwdList, sVoiceList, "systemKwd", pathMap, path);
               excelFiles.add(sysFile);
               File zipFile = new File(path + File.separator + topicName + "热词分析-" + time + ".zip");
               if (!zipFile.exists()) {
                  zipFile.createNewFile();
               }

               if (CollectionUtils.isNotEmpty(excelFiles)) {
                  boolean var24 = false;

                  try {
                     var24 = true;
                     this.ZipFiles(excelFiles, zipFile);
                     var24 = false;
                  } catch (Exception var25) {
                     logger.info("导出专题-热词时打包异常：exportHotWordStat");
                     throw new VieAppServiceException("导出专题-热词时打包异常：exportHotWordStat", var25);
                  } finally {
                     if (var24) {
                        Iterator var19 = excelFiles.iterator();

                        while(var19.hasNext()) {
                           File file = (File)var19.next();
                           file.delete();
                        }

                     }
                  }

                  Iterator var16 = excelFiles.iterator();

                  while(var16.hasNext()) {
                     File file = (File)var16.next();
                     file.delete();
                  }
               }

               return zipFile;
            } else {
               return null;
            }
         } catch (Exception var27) {
            logger.info("导出专题-热词时异常：exportHotWordStat");
            throw new VieAppServiceException("导出专题-热词时异常：exportHotWordStat", var27);
         }
      }
   }

   private File exportHotWordStatBytype(String time, List<HotWordRankResponse> kwdList, List<HotWordRankResponse> voiceList, String type, Map<String, Object> pathMap, String path) throws IOException, VieAppServiceException {
      String[] kwdColumNames = new String[]{"排名", "关键词", "词频占比"};
      String[] voiceColumNames = new String[]{"排名", "关键词", "音频占比"};
      String[] colums = new String[]{"rn", "hotVocabulary", "rate"};
      String topicName = pathMap.get("topicName").toString();
      String excelName = "";
      if ("focusKwd".equals(type)) {
         excelName = topicName + "热词分析关注热词-" + time + ".xlsx";
      } else {
         excelName = topicName + "热词分析系统热词-" + time + ".xlsx";
      }

      FileOutputStream out = new FileOutputStream(path + File.separator + excelName);

      try {
         XSSFWorkbook workbook = new XSSFWorkbook();
         XSSFSheet pathSheet = workbook.createSheet();
         XSSFSheet kwdSheet = workbook.createSheet();
         XSSFSheet voiceSheet = workbook.createSheet();
         XSSFCellStyle normalStyle = this.createCostomCellStyle(workbook, (short)32767, false, new XSSFColor(new Color(119, 170, 85)));
         XSSFCellStyle titleStyle = this.createCostomCellStyle(workbook, (short)32767, true, new XSSFColor(new Color(119, 170, 85)));
         List<ColumnMap> rowHead = new ArrayList();
         ColumnMap cmp = new ColumnMap("路径名称", "pathName");
         rowHead.add(cmp);
         cmp = new ColumnMap("路径标签", "pathType");
         rowHead.add(cmp);
         this.createPathListSheet(pathSheet, normalStyle, titleStyle, rowHead, pathMap);
         this.fillSheetContent(kwdSheet, kwdColumNames, colums, normalStyle, titleStyle, kwdList);
         this.fillSheetContent(voiceSheet, voiceColumNames, colums, normalStyle, titleStyle, voiceList);
         workbook.setSheetName(0, topicName + "路径信息");
         workbook.setSheetName(1, topicName + "热词分析词频占比");
         workbook.setSheetName(2, topicName + "热词分析音频占比");
         workbook.write(out);
      } catch (Exception var24) {
         throw new VieAppServiceException("导出专题-热词信息时异常exportHotWordStatBytype", var24);
      } finally {
         out.flush();
         out.close();
      }

      File file = new File(path + File.separator + excelName);
      return file.exists() ? file : null;
   }

   private void fillSheetContent(XSSFSheet Sheet, String[] columNames, String[] colums, XSSFCellStyle cellStyle, XSSFCellStyle titleStyle, List<HotWordRankResponse> hotWordList) {
      this.createSheetLength(Sheet, columNames.length);
      this.createColumnHead(Sheet, columNames, titleStyle);
      int voiceRow = 1;
      if (CollectionUtils.isNotEmpty(hotWordList)) {
         Iterator var8 = hotWordList.iterator();

         while(true) {
            HotWordRankResponse hotWord;
            do {
               if (!var8.hasNext()) {
                  return;
               }

               hotWord = (HotWordRankResponse)var8.next();
            } while(hotWord == null);

            XSSFRow rowData = Sheet.createRow(voiceRow++);

            for(int j = 0; j < colums.length; ++j) {
               XSSFCell cell = rowData.createCell(j);
               cell.setCellStyle(cellStyle);
               String colum = colums[j];
               if ("rn".equals(colum)) {
                  cell.setCellValue((double)hotWord.getRn());
               }

               if ("hotVocabulary".equals(colum)) {
                  cell.setCellValue(hotWord.getHotVocabulary());
               }

               if ("rate".equals(colum)) {
                  cell.setCellValue(hotWord.getRate());
               }
            }
         }
      }
   }

   @RequestMapping(
      value = {"/exportFunnel"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult exportFunnel(ExportFunnelRequest params, HttpServletRequest request, HttpServletResponse response) {
      ResponseResult responseResult = new ResponseResult();

      try {
         params.setDataSource(BaseUtils.getDataSource(request));
         params.setUserId(BaseUtils.getUserId(request));
         params.setSystemId(this.commonService.getContextPath());
         String path = System.getProperty("user.dir");
         ExportFunnelResponse exportResponse = this.topicExportService.exportFunnel(params);
         Map<String, Object> pathMap = exportResponse.getPathMap();
         String topicName = pathMap.get("topicName").toString();
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
         String timeStamp = simpleDateFormat.format(new Date());
         String zipName = topicName + "漏斗分析-" + timeStamp;
         List<HashMap<String, Object>> conditionList = exportResponse.getConditionList();
         FunnelTableResponse funnelTableResponse = exportResponse.getFunnelTableResponse();
         List<String> columnList = null;
         List<String> numList = null;
         List<String> rateList = null;
         if (funnelTableResponse != null) {
            columnList = funnelTableResponse.getColumn();
            numList = funnelTableResponse.getNum();
            rateList = funnelTableResponse.getRate();
         }

         FunnelCallResponse funnCallResponse = exportResponse.getFunnelCallResponse();
         double totalCount = 0.0;
         List<ColumnMap> columnList2 = null;
         List<LinkedHashMap<String, Object>> list = null;
         if (funnCallResponse != null) {
            totalCount = funnCallResponse.getTotalCount();
            columnList2 = funnCallResponse.getColumns();
            list = funnCallResponse.getPreviewList().getRows();
         }

         logger.info("list==" + JSONObject.toJSONString(list));
         Map obj;
         if (null != list) {
            Iterator var22 = list.iterator();

            while(var22.hasNext()) {
               LinkedHashMap<String, Object> dataMap = (LinkedHashMap)var22.next();
               obj = (Map)dataMap.get("dataMaps");
               this.handleParam(obj);
            }
         }

         int limitNum = exportResponse.getLimitNum();
         XSSFWorkbook wb = new XSSFWorkbook();
         obj = null;
         XSSFRow row = null;
         XSSFCell cell = null;
         XSSFCellStyle cellStyle = wb.createCellStyle();
         cellStyle.setAlignment((short)2);
         XSSFCellStyle cellStyle2 = wb.createCellStyle();
         cellStyle2.setFillPattern((short)1);
         cellStyle2.setFillForegroundColor(new XSSFColor(new Color(119, 170, 85)));
         cellStyle2.setAlignment((short)2);
         String cellValue1 = "";
         String cellValue2 = "";
         if (conditionList != null) {
            if (((HashMap)conditionList.get(0)).get("type").equals("model")) {
               String modelName = String.valueOf(((HashMap)conditionList.get(0)).get("name"));
               if (String.valueOf(((HashMap)conditionList.get(0)).get("isNegate")).equals("1")) {
                  cellValue1 = "!模型";
               } else {
                  cellValue1 = "模型";
               }

               cellValue2 = modelName;
            } else if (((HashMap)conditionList.get(0)).get("name").equals("起止时间")) {
               cellValue2 = "主路径";
            } else {
               cellValue1 = String.valueOf(((HashMap)conditionList.get(0)).get("name"));
               cellValue2 = String.valueOf(((HashMap)conditionList.get(0)).get("value"));
            }
         }

         XSSFSheet pathSheet = wb.createSheet(topicName + "路径信息");
         List<ColumnMap> rowHead = new ArrayList();
         ColumnMap cmp = new ColumnMap("路径名称", "pathName");
         rowHead.add(cmp);
         cmp = new ColumnMap("路径标签", "pathType");
         rowHead.add(cmp);
         this.createPathListSheet(pathSheet, cellStyle, cellStyle2, rowHead, pathMap);
         int i;
         XSSFSheet sheet;
         if (params.getAllPathFlag() != null && params.getAllPathFlag() == 1) {
            sheet = wb.createSheet(topicName + "漏斗分析详情");
            this.createSheetLength(sheet, columnList.size() + 1);
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue("");

            for(i = 0; i < columnList.size(); ++i) {
               cell = row.createCell(i + 1);
               cell.setCellValue((String)columnList.get(i));
               cell.setCellStyle(cellStyle);
               cell.setCellStyle(cellStyle2);
            }

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("数量");
            cell.setCellStyle(cellStyle);

            for(i = 0; i < numList.size(); ++i) {
               cell = row.createCell(i + 1);
               cell.setCellValue((String)numList.get(i));
            }

            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("占比");
            cell.setCellStyle(cellStyle);

            for(i = 0; i < rateList.size(); ++i) {
               cell = row.createCell(i + 1);
               cell.setCellValue((String)rateList.get(i));
            }
         } else if (params.getAllPathFlag() != null && params.getAllPathFlag() == 0) {
            sheet = wb.createSheet(topicName + "漏斗转化率详情");
            this.createSheetLength(sheet, columnList.size() + 1);
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(cellValue1);
            cell.setCellStyle(cellStyle);
            cell.setCellStyle(cellStyle2);

            for(i = 0; i < columnList.size(); ++i) {
               cell = row.createCell(i + 1);
               cell.setCellValue((String)columnList.get(i));
               cell.setCellStyle(cellStyle2);
            }

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue(cellValue2);
            cell.setCellStyle(cellStyle);

            for(i = 0; i < numList.size(); ++i) {
               cell = row.createCell(i + 1);
               cell.setCellValue((String)numList.get(i));
            }
         }

         int j;
         Map dataMaps;
         List listMap;
         String keywords;
         Iterator var39;
         HashMap hashMap;
         if (params.getIsNumData() != null && params.getIsNumData() == 1) {
            if (totalCount > (double)limitNum) {
               responseResult.setMessage("单次只能导出" + limitNum + "条数据，请重新筛选数据后导出");
               responseResult.setSuccess(false);
               return responseResult;
            }

            sheet = wb.createSheet(topicName + "漏斗分析转化量数据导出");
            this.createSheetLength(sheet, columnList2.size());
            row = sheet.createRow(0);
            cell = row.createCell(0);
            if (cellValue2.equals("主路径")) {
               cell.setCellValue(cellValue2);
            } else {
               cell.setCellValue(cellValue1 + ":" + cellValue2);
            }

            row = sheet.createRow(1);

            for(i = 0; i < columnList2.size(); ++i) {
               cell = row.createCell(i);
               cell.setCellValue(((ColumnMap)columnList2.get(i)).getColumnName());
               cell.setCellStyle(cellStyle);
               cell.setCellStyle(cellStyle2);
            }

            for(i = 0; (double)i < totalCount; ++i) {
               row = sheet.createRow(i + 2);

               for(j = 0; j < columnList2.size(); ++j) {
                  dataMaps = (Map)((LinkedHashMap)list.get(i)).get("dataMaps");
                  listMap = (List)((LinkedHashMap)list.get(i)).get("keywordInfos");
                  keywords = "";

                  for(var39 = listMap.iterator(); var39.hasNext(); keywords = keywords + hashMap.get("content")) {
                     hashMap = (HashMap)var39.next();
                  }

                  cell = row.createCell(j);
                  cell.setCellStyle(cellStyle);
                  if ("keyword".equals(((ColumnMap)columnList2.get(j)).getColumn())) {
                     cell.setCellValue(keywords);
                  } else {
                     cell.setCellValue(String.valueOf(dataMaps.get(((ColumnMap)columnList2.get(j)).getColumn())));
                  }
               }
            }
         } else if (params.getIsNumData() != null && params.getIsNumData() == 0) {
            if (totalCount > (double)limitNum) {
               responseResult.setMessage("单次只能导出" + limitNum + "条数据，请重新筛选数据后导出");
               responseResult.setSuccess(false);
               return responseResult;
            }

            sheet = wb.createSheet(topicName + "漏斗分析流失量数据导出");
            this.createSheetLength(sheet, columnList2.size());
            row = sheet.createRow(0);
            cell = row.createCell(0);
            if (cellValue2.equals("主路径")) {
               cell.setCellValue(cellValue2);
            } else {
               cell.setCellValue(cellValue1 + ":" + cellValue2);
            }

            row = sheet.createRow(1);

            for(i = 0; i < columnList2.size(); ++i) {
               cell = row.createCell(i);
               cell.setCellValue(((ColumnMap)columnList2.get(i)).getColumnName());
               cell.setCellStyle(cellStyle);
               cell.setCellStyle(cellStyle2);
            }

            for(i = 0; (double)i < totalCount; ++i) {
               row = sheet.createRow(i + 2);

               for(j = 0; j < columnList2.size(); ++j) {
                  dataMaps = (Map)((LinkedHashMap)list.get(i)).get("dataMaps");
                  listMap = (List)((LinkedHashMap)list.get(i)).get("keywordInfos");
                  keywords = "";

                  for(var39 = listMap.iterator(); var39.hasNext(); keywords = keywords + hashMap.get("content")) {
                     hashMap = (HashMap)var39.next();
                  }

                  cell = row.createCell(j);
                  cell.setCellStyle(cellStyle);
                  if ("keyword".equals(((ColumnMap)columnList2.get(j)).getColumn())) {
                     cell.setCellValue(keywords);
                  } else {
                     cell.setCellValue(String.valueOf(dataMaps.get(((ColumnMap)columnList2.get(j)).getColumn())));
                  }
               }
            }
         }

         String moduleName = "漏斗分析";
         String excelName = topicName + moduleName + "-" + timeStamp + ".xlsx";
         String picName = topicName + moduleName + "-" + timeStamp + ".png";
         List<String> fileNames = new ArrayList();
         fileNames.add(excelName);
         FileOutputStream excelOs = new FileOutputStream(path + File.separator + excelName);
         if (!StringUtils.isNullOrEmpry(params.getSvgCode()) && params.getIsNumData() == null) {
            this.exportImgForBase64(params.getSvgCode(), path + File.separator + picName);
            fileNames.add(picName);
         }

         wb.write(excelOs);
         response.reset();
         File zip = new File(path + File.separator + zipName + ".zip");
         if (!zip.exists()) {
            zip.createNewFile();
         }

         response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipName, "UTF-8") + ".zip");
         response.setContentType("application/octet-stream");
         OutputStream out = response.getOutputStream();
         List<File> srcfile = new ArrayList();
         for(int index = 0; index < fileNames.size(); ++index) {
            srcfile.add(new File(path + File.separator + (String)fileNames.get(index)));
         }

         this.ZipFiles(srcfile, zip);
         FileInputStream inStream = new FileInputStream(zip);
         byte[] buf = new byte[4096];

         int readLength;
         while((readLength = inStream.read(buf)) != -1) {
            out.write(buf, 0, readLength);
         }

         inStream.close();
         Iterator var45 = srcfile.iterator();

         while(var45.hasNext()) {
            File file = (File)var45.next();
            file.delete();
         }

         zip.delete();
         responseResult.setSuccess(true);
         return responseResult;
      } catch (Exception var47) {
         responseResult.setSuccess(false);
         responseResult.setMessage(var47.getMessage());
         return responseResult;
      }
   }
}
