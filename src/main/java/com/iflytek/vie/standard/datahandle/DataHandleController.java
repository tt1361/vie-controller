package com.iflytek.vie.standard.datahandle;

import com.iflytek.vie.app.api.datahandle.DataHandleRequest;
import com.iflytek.vie.app.api.datahandle.DataHandleStatusService;
import com.iflytek.vie.app.api.datahandle.TaskfileHandleInfo;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("DataHandleController")
@RequestMapping({"/dataHandle"})
public class DataHandleController {
   @Autowired
   private DataHandleStatusService dataHandleStatusService;
   @Autowired
   private CommonService commonService;
   private static final Logger logger = LoggerFactory.getLogger(DataHandleController.class);

   @RequestMapping(
      value = {"/queryDataHandleInfo"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryDataHandleStatus(HttpServletRequest request, DataHandleRequest dataHandleRequest) {
      ResponseResult response = new ResponseResult();
      if (this.checkRequest(dataHandleRequest)) {
         try {
            Map<String, Object> result = new HashMap();
            dataHandleRequest.setDataSource(BaseUtils.getDataSource(request));
            TaskfileHandleInfo TaskfileHandleInfo = this.dataHandleStatusService.getHandleInfoBytime(dataHandleRequest);
            result.put("totalNumInfo", TaskfileHandleInfo);
            Map<String, Object> infoMap = this.dataHandleStatusService.getDetailInfoMap(dataHandleRequest);
            result.put("handleInfo", infoMap);
            response.setSuccess(true);
            response.setValue(result);
         } catch (Exception var7) {
            response.setSuccess(false);
            response.setMessage(var7.getMessage());
         }
      } else {
         response.setSuccess(false);
         response.setMessage("查询数据处理情况错误：参数错误");
      }

      return response;
   }

   private boolean checkRequest(DataHandleRequest request) {
      return request != null && request.getPageNum() > 0 && request.getPageSize() > 0 && !StringUtils.isBlank(request.getHandleTime());
   }
}
