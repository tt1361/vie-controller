package com.iflytek.vie.standard.custom;

import com.iflytek.vie.app.api.custom.CustomHomePageService;
import com.iflytek.vie.app.pojo.custom.CustomHomePage;
import com.iflytek.vie.app.pojo.custom.CustomHomePageRequest;
import com.iflytek.vie.app.pojo.custom.HomePageRelateModule;
import com.iflytek.vie.app.pojo.custom.PageRelateModuleRequest;
import com.iflytek.vie.app.pojo.custom.PageRelateModuleResponse;
import com.iflytek.vie.config.LoadConfig;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.StringUtils;
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

@Controller("customHomePageController")
@RequestMapping({"/customHomePage"})
public class CustomHomePageController {
   private static final Logger logger = LoggerFactory.getLogger(CustomHomePageController.class);
   @Autowired
   private CustomHomePageService customHomePageService;

   @RequestMapping(
      value = {"saveCustomHomePage"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult saveCustomHomePage(CustomHomePageRequest customHomePageRequest, HttpServletRequest request) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            customHomePageRequest.setDataSource(dataSource);
            customHomePageRequest.setUserId(BaseUtils.getUserId(request));
            customHomePageRequest.setMaxNum(Integer.parseInt(LoadConfig.getConfigProperty("maxPageRelateModuleNum")));
            Long pageId = this.customHomePageService.saveCustomHomePage(customHomePageRequest);
            return ResponseResult.success(pageId, "保存成功!");
         }
      } catch (Exception var5) {
         logger.error("【增加自定义首页】出现异常", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"updateCustomHomePage"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updateCustomHomePage(CustomHomePageRequest customHomePageRequest, HttpServletRequest request) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            customHomePageRequest.setDataSource(dataSource);
            customHomePageRequest.setUserId(BaseUtils.getUserId(request));
            boolean result = this.customHomePageService.updateCustomHomePage(customHomePageRequest);
            return !result ? ResponseResult.error("保存失败!") : ResponseResult.success("保存成功!");
         }
      } catch (Exception var5) {
         logger.error("【更新自定义首页】出现异常", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"deleteCustomHomePage"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteCustomHomePage(@RequestParam("pageId") Long pageId, @RequestParam(value = "pageName",required = false) String pageName, HttpServletRequest request) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            CustomHomePageRequest cr = new CustomHomePageRequest();
            cr.setDataSource(dataSource);
            cr.setPageId(pageId);
            boolean result = this.customHomePageService.deleteCustomHomePage(cr);
            return !result ? ResponseResult.error("删除失败!") : ResponseResult.success("删除成功!");
         }
      } catch (Exception var7) {
         logger.error("【删除自定义首页】出现异常", var7);
         return ResponseResult.error(var7.getMessage());
      }
   }

   @RequestMapping(
      value = {"queryCustomHomePageList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryCustomHomePageList(HttpServletRequest request) {
      CustomHomePageRequest customHomePageRequest = new CustomHomePageRequest();

      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            customHomePageRequest.setDataSource(dataSource);
            customHomePageRequest.setUserId(BaseUtils.getUserId(request));
            List<CustomHomePage> result = this.customHomePageService.queryCustomHomePageList(customHomePageRequest);
            return ResponseResult.success(result, "查询成功!");
         }
      } catch (Exception var5) {
         logger.error("【自定义首页列表查询】出现异常", var5);
         return ResponseResult.error(var5.getMessage());
      }
   }

   @RequestMapping(
      value = {"savePageRelateModule"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult savePageRelateModule(HttpServletRequest request, PageRelateModuleRequest pageRelateModuleRequest) {
      try {
         pageRelateModuleRequest.setUserId(BaseUtils.getUserId(request));
         pageRelateModuleRequest.setDataSource(BaseUtils.getDataSource(request));
         pageRelateModuleRequest.setMaxNum(Integer.parseInt(LoadConfig.getConfigProperty("maxPageRelateModuleNum")));
         Long id = this.customHomePageService.savePageRelateModule(pageRelateModuleRequest);
         return id == null ? ResponseResult.error("操作失败,所选图表已被删除!") : ResponseResult.success(id, "保存成功!");
      } catch (Exception var4) {
         logger.error("【保存发送到自定义首页的图表信息】出现异常", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"updatePageRelateModule"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updatePageRelateModule(HttpServletRequest request, PageRelateModuleRequest pageRelateModuleRequest) {
      try {
         pageRelateModuleRequest.setUserId(BaseUtils.getUserId(request));
         pageRelateModuleRequest.setDataSource(BaseUtils.getDataSource(request));
         pageRelateModuleRequest.setMaxNum(Integer.parseInt(LoadConfig.getConfigProperty("maxPageRelateModuleNum")));
         boolean result = this.customHomePageService.updatePageRelateModule(pageRelateModuleRequest);
         return !result ? ResponseResult.error("保存失败!") : ResponseResult.success("保存成功!");
      } catch (Exception var4) {
         logger.error("【更新发送到自定制首页的模块（报表、专题）结论】出现异常", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"deletePageRelateModule"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deletePageRelateModule(HttpServletRequest request, @RequestParam("id") Long id) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            HomePageRelateModule hm = new HomePageRelateModule();
            hm.setId(id);
            hm.setDataSource(dataSource);
            boolean result = this.customHomePageService.deletePageRelateModule(hm);
            return !result ? ResponseResult.error("删除失败!") : ResponseResult.success("删除成功!");
         }
      } catch (Exception var6) {
         logger.error("【删除发送到自定制首页的模块（报表、专题）结论】出现异常", var6);
         return ResponseResult.error(var6.getMessage());
      }
   }

   @RequestMapping(
      value = {"deleteModuleFromHomePage"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteModuleFromHomePage(HttpServletRequest request, PageRelateModuleRequest pageRelateModuleRequest) {
      try {
         pageRelateModuleRequest.setDataSource(BaseUtils.getDataSource(request));
         this.customHomePageService.deleteModuleFromHomePage(pageRelateModuleRequest);
         return ResponseResult.success("删除成功!");
      } catch (Exception var4) {
         logger.error("【删除专题或专题路径时，从自定义首页删除与此专题有关的模块信息】出现异常", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }

   @RequestMapping(
      value = {"queryPageRelateModuleList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryPageRelateModuleList(HttpServletRequest request, @RequestParam("pageId") Long pageId) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            CustomHomePageRequest cpr = new CustomHomePageRequest();
            cpr.setDataSource(dataSource);
            cpr.setPageId(pageId);
            PageRelateModuleResponse result = this.customHomePageService.queryPageRelateModuleList(cpr);
            return ResponseResult.success(result, "查询成功!");
         }
      } catch (Exception var6) {
         logger.error("【查询自定义首页关联的模块列表信息】出现异常", var6);
         return ResponseResult.error(var6.getMessage());
      }
   }

   @RequestMapping(
      value = {"getModuleInfoById"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getModuleInfoById(HttpServletRequest request, @RequestParam("id") Long id) {
      try {
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            return ResponseResult.error("数据源为空");
         } else {
            HomePageRelateModule hm = new HomePageRelateModule();
            hm.setId(id);
            hm.setDataSource(dataSource);
            HomePageRelateModule result = this.customHomePageService.getModuleInfoById(hm);
            return ResponseResult.success(result, "查询成功!");
         }
      } catch (Exception var6) {
         logger.error("【根据首页关联的模块主键ID查询此模块的详细信息】出现异常", var6);
         return ResponseResult.error(var6.getMessage());
      }
   }

   @RequestMapping(
      value = {"queryPageIdByModuleInfo"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryPageIdByModuleInfo(HttpServletRequest request, PageRelateModuleRequest pageRelateModuleRequest) {
      try {
         pageRelateModuleRequest.setDataSource(BaseUtils.getDataSource(request));
         pageRelateModuleRequest.setUserId(BaseUtils.getUserId(request));
         Long pageId = this.customHomePageService.queryPageIdByModuleInfo(pageRelateModuleRequest);
         return ResponseResult.success(pageId, "查询成功!");
      } catch (Exception var4) {
         logger.error("【根据模块信息查询自定义首页ID】出现异常", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }
}
