package com.iflytek.vie.standard.datasource;

import com.iflytek.vie.app.api.dataSource.DataSourceService;
import com.iflytek.vie.app.api.permission.DataAuthService;
import com.iflytek.vie.app.api.permission.FunctionAuthService;
import com.iflytek.vie.app.api.permission.UserService;
import com.iflytek.vie.app.exception.PermissionServiceException;
import com.iflytek.vie.app.pojo.permission.ContextRequest;
import com.iflytek.vie.app.pojo.permission.DataResourceAuth;
import com.iflytek.vie.app.pojo.permission.DataSourceResponse;
import com.iflytek.vie.app.pojo.permission.FunctionAuthResponse;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.StringUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("datasourceController")
@RequestMapping({"/datasource"})
public class DatasourceController {
   private static final Logger logger = LoggerFactory.getLogger(DatasourceController.class);
   @Autowired
   private CommonService commonService;
   @Autowired
   private UserService userService;
   @Autowired
   private FunctionAuthService functionAuthService;
   @Autowired
   private DataAuthService dataAuthService;
   @Autowired
   private DataSourceService dataSourceService;

   @RequestMapping(
      value = {"/getUserDataSourceList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getUserDataSourceList(HttpServletRequest request) {
      String userId = BaseUtils.getUserId(request);
      String contextPath = this.commonService.getContextPath();

      try {
         List<DataSourceResponse> result = this.userService.getUserAllBusiness(contextPath, userId);
         if (StringUtils.isNotNullAndEmpry(BaseUtils.getDataSource(request)) && result != null && result.size() > 0) {
            Iterator var5 = result.iterator();

            while(var5.hasNext()) {
               DataSourceResponse response = (DataSourceResponse)var5.next();
               if (response.getBusinessCode().equals(BaseUtils.getDataSource(request))) {
                  response.setType("1");
               } else {
                  response.setType("0");
               }
            }
         }

         return ResponseResult.success(result, "查询成功!");
      } catch (PermissionServiceException var7) {
         logger.error("获取用户在系统下拥有的业务出错", var7);
         return ResponseResult.error("获取用户在系统下拥有的业务服务内部错误!");
      }
   }

   @RequestMapping(
      value = {"/changeUserDataSource"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult changeUserDataSource(HttpServletRequest request, String dataSource, String dimensionName) {
      try {
         List<String> dataSourceList = this.dataSourceService.getDataSource();
         if (dataSourceList != null && dataSourceList.size() > 0 && !dataSourceList.contains(dataSource)) {
            return ResponseResult.error("该数据源不存在");
         }

         HttpSession session = request.getSession();
         session.setAttribute("user_system_datasource", dataSource);
         logger.info("changeUserDataSource中数据源dataSource: " + dataSource);
         session.setAttribute("dataTypeDimension", dimensionName);
         logger.info("changeUserDataSource中维度dimensionName: " + dimensionName);
         ContextRequest resourceParams = new ContextRequest();
         resourceParams.setUserId(Long.valueOf(BaseUtils.getUserId(request)));
         resourceParams.setContextPath(this.commonService.getContextPath());
         resourceParams.setDataSource(dataSource);
         Map<String, FunctionAuthResponse> resourceAuths = this.functionAuthService.getFuncAuths(resourceParams);
         session.setAttribute("sys_function_info", resourceAuths);
         Map<String, DataResourceAuth> dataAuths = this.dataAuthService.getDataAuths(resourceParams);
         session.setAttribute("sys_data_info", dataAuths);
      } catch (PermissionServiceException var9) {
         return ResponseResult.error("服务内部错误!");
      }

      return ResponseResult.success("保存成功!");
   }
}
