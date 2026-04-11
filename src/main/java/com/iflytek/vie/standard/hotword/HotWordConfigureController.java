package com.iflytek.vie.standard.hotword;

import com.iflytek.vie.app.api.dimension.HotWordService;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.auth.AuthorizeInfo;
import com.iflytek.vie.app.pojo.dimension.FocusOrExcludeRequest;
import com.iflytek.vie.app.pojo.dimension.HotWordHistoryRequest;
import com.iflytek.vie.app.pojo.dimension.HotWordHistoryResponse;
import com.iflytek.vie.app.pojo.dimension.HotWordRequest;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.StringUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("hotWordConfigureController")
@RequestMapping({"/hotWordConfigure"})
public class HotWordConfigureController {
   @Autowired
   private HotWordService hotWordService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/addHotWord"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult addHotWord(HotWordRequest request, HttpServletRequest httpServletRequest) {
      ResponseResult response = new ResponseResult();
      if (null == request) {
         response.setMessage("入参不正确");
         return response;
      } else {
         try {
            request.setAuthInfo(new AuthorizeInfo("", "", BaseUtils.getDataSource(httpServletRequest)));
            FocusOrExcludeRequest params = new FocusOrExcludeRequest();
            BeanUtils.copyProperties(params, request);
            params.setDataSource(BaseUtils.getDataSource(httpServletRequest));
            boolean isBlack = request.isBlack();
            boolean flag;
            if (isBlack) {
               flag = this.hotWordService.addExcludeHotWordService(params);
               response.setValue(flag);
            } else {
               flag = this.hotWordService.addFocusHotWordService(params);
               response.setValue(flag);
            }

            response.setSuccess(true);
            return response;
         } catch (Exception var7) {
            response.setMessage(var7.getMessage());
            return response;
         }
      }
   }

   @RequestMapping(
      value = {"/deleteWord"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult deleteHotWord(HotWordRequest params, HttpServletRequest httpServletRequest) {
      ResponseResult response = new ResponseResult();
      if (null != params && null != params.getWord() && !"".equals(params.getWord())) {
         try {
            params.setAuthInfo(new AuthorizeInfo("", "", BaseUtils.getDataSource(httpServletRequest)));
            params.setDataSource(BaseUtils.getDataSource(httpServletRequest));
            boolean flag = this.hotWordService.deleteHotWordService(params);
            response.setValue(flag);
            response.setSuccess(true);
            return response;
         } catch (Exception var5) {
            response.setMessage(var5.getMessage());
            return response;
         }
      } else {
         response.setMessage("入参不正确");
         return response;
      }
   }

   @RequestMapping(
      value = {"/searchWord"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult searchWord(HotWordRequest params, HttpServletRequest httpServletRequest) {
      ResponseResult response = new ResponseResult();
      if (null == params) {
         response.setMessage("入参不正确");
         return response;
      } else {
         try {
            params.setAuthInfo(new AuthorizeInfo("", "", BaseUtils.getDataSource(httpServletRequest)));
            params.setDataSource(BaseUtils.getDataSource(httpServletRequest));
            List<String> result = this.hotWordService.getHotWordService(params);
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
      value = {"/fetchHistoryWord"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult fetchHistoryWord(HotWordHistoryRequest hotWordHistoryRequest, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();

      try {
         AuthorizeInfo authorizeInfo = new AuthorizeInfo();
         String dataSource = BaseUtils.getDataSource(request);
         if (StringUtils.isNullOrEmpry(dataSource)) {
            response.setMessage("数据源为空");
            return response;
         } else {
            authorizeInfo.setDataSource(dataSource);
            authorizeInfo.setUserId(BaseUtils.getUserId(request));
            authorizeInfo.setSystemId(this.commonService.getContextPath());
            hotWordHistoryRequest.setAutInfo(authorizeInfo);
            hotWordHistoryRequest.setDataSource(dataSource);
            hotWordHistoryRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
            List<HotWordHistoryResponse> result = this.hotWordService.getHistoryHotWordService(hotWordHistoryRequest);
            response.setValue(result);
            response.setSuccess(true);
            return response;
         }
      } catch (ViePlatformServiceException var7) {
         response.setMessage(var7.getMessage());
         return response;
      }
   }
}
