package com.iflytek.vie.standard.report;

import com.iflytek.vie.app.api.report.ReportDownloadService;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.pojo.common.PagerResponse;
import com.iflytek.vie.app.pojo.report.ReportDeleteRequest;
import com.iflytek.vie.app.pojo.report.ReportDownload;
import com.iflytek.vie.app.pojo.report.ReportDownloadRequest;
import com.iflytek.vie.app.pojo.report.ReportRequest;
import com.iflytek.vie.app.pojo.report.ReportTemplate;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.custom.api.report.ReportDownloadCustomService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
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

@Controller("reportDownloadController")
@RequestMapping({"/reportDownload"})
public class ReportDownloadController {
   private static final Logger logger = LoggerFactory.getLogger(ReportDownloadController.class);
   @Autowired
   private ReportDownloadService reportDownloadService;
   @Autowired
   private ReportDownloadCustomService reportDownloadCustomService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/queryDownloadReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryDownloadReport(HttpServletRequest request, ReportDownloadRequest reportDownloadRequest) {
      try {
         reportDownloadRequest.setUserId(BaseUtils.getUserId(request));
         reportDownloadRequest.setSystemId(this.commonService.getContextPath());
         reportDownloadRequest.setDataSource(BaseUtils.getDataSource(request));
         PagerResponse<ReportDownload> response = this.reportDownloadService.queryDownloadReport(reportDownloadRequest);
         return ResponseResult.success(response, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【queryDownloadReport】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/getDownloadReportStatus"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getDownloadReportStatus(@RequestParam("reportDownloadId") int reportDownloadId) {
      try {
         ReportRequest reportRequest = new ReportRequest();
         reportRequest.setReportId(reportDownloadId);
         int status = this.reportDownloadService.getDownloadReportStatus(reportRequest);
         return ResponseResult.success(status, "查询成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【getDownloadReportStatus】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"/saveDownloadReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveDownloadReport(HttpServletRequest request, ReportTemplate reportTemplate) {
      try {
         StringBuffer fullAddr = request.getRequestURL();
         String portAddr = request.getServletPath();
         StringBuffer baseAddr = fullAddr.delete(fullAddr.length() - portAddr.length(), fullAddr.length());
         reportTemplate.setUserId(BaseUtils.getUserId(request));
         reportTemplate.setSystemId(this.commonService.getContextPath());
         reportTemplate.setDataSource(BaseUtils.getDataSource(request));
         reportTemplate.setDownloadCtx(baseAddr.toString());
         reportTemplate.setDataSource(BaseUtils.getDataSource(request));
         this.reportDownloadCustomService.saveDownloadReport(reportTemplate);
         return ResponseResult.success("查询成功!");
      } catch (VieAppServiceException var6) {
         logger.error("【saveDownloadReport】方法调用出错", var6);
         return ResponseResult.error(var6.getMessage());
      }
   }

   @RequestMapping(
      value = {"/saveDownloadFile"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveDownLoadFile(@RequestParam(value = "reportId",required = false) int reportId, HttpServletResponse response, HttpServletRequest request) {
      BufferedInputStream bis = null;
      BufferedOutputStream bos = null;
      OutputStream fos = null;
      InputStream fis = null;

      try {
         ReportRequest reportRequest = new ReportRequest();
         reportRequest.setDataSource(BaseUtils.getDataSource(request));
         reportRequest.setReportId(reportId);
         ReportDownload reportDownload = this.reportDownloadService.getReportDownloadById(reportRequest);
         if (reportDownload != null) {
            File downFiles = new File(reportDownload.getTureFileDir());
            if (!downFiles.exists()) {
               logger.error("文件不存在");
               return ResponseResult.error("文件不存在");
            }

            fos = response.getOutputStream();
            response.reset();
            bos = new BufferedOutputStream(fos);
            String encodedfileName = reportDownload.getDownloadName() + ".xlsx";
            encodedfileName = reportDownload.getDownloadName() + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(encodedfileName, "UTF-8"));
            response.setHeader("Content-Length", String.valueOf(downFiles.length()));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            byte[] buffer = new byte[8192];
            fis = new FileInputStream(downFiles);
            bis = new BufferedInputStream(fis);

            int byteRead;
            while((byteRead = bis.read(buffer, 0, 8192)) != -1) {
               bos.write(buffer, 0, byteRead);
            }

            bos.flush();
            fis.close();
            bis.close();
            fos.close();
            bos.close();
         }

         return ResponseResult.success("操作成功!");
      } catch (VieAppServiceException var14) {
         logger.error("【saveDownLoadFile】方法调用出错", var14);
         return ResponseResult.error("操作失败!");
      } catch (FileNotFoundException var15) {
         logger.error("没有找到文件", var15);
         return ResponseResult.error("操作失败!");
      } catch (IOException var16) {
         logger.error("文件读写异常", var16);
         return ResponseResult.error("操作失败!");
      }
   }

   @RequestMapping(
      value = {"/deleteDownLoadReport"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteDownLoadReport(@RequestParam("reportDownloadIds") String reportDownloadIds, HttpServletRequest request) {
      try {
         ReportDeleteRequest reportDeleteRequest = new ReportDeleteRequest();
         reportDeleteRequest.setDataSource(BaseUtils.getDataSource(request));
         reportDeleteRequest.setReportDownloadIds(reportDownloadIds);
         this.reportDownloadService.deleteDownLoadReport(reportDeleteRequest);
         return ResponseResult.success("删除成功!");
      } catch (VieAppServiceException var4) {
         logger.error("【deleteDownLoadReport】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }
}
