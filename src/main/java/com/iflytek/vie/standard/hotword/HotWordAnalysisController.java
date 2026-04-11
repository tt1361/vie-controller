package com.iflytek.vie.standard.hotword;

import com.iflytek.vie.app.api.dimension.HotWordService;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.auth.AuthorizeInfo;
import com.iflytek.vie.app.pojo.dimension.HotWordRankRequest;
import com.iflytek.vie.app.pojo.dimension.HotWordRankResponse;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("hotWordAnalysisController")
@RequestMapping({"/hotWordAnalysis"})
public class HotWordAnalysisController {
   private static final Logger logger = LoggerFactory.getLogger(HotWordAnalysisController.class);
   @Autowired
   private HotWordService hotWordService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"/getHotWordRank"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getHotWordRankService(HttpServletRequest request, HotWordRankRequest hotWordRankRequest) {
      try {
         hotWordRankRequest.setAutInfo(new AuthorizeInfo(BaseUtils.getUserId(request), this.commonService.getContextPath(), BaseUtils.getDataSource(request)));
         hotWordRankRequest.setDataSource(BaseUtils.getDataSource(request));
         hotWordRankRequest.setDataTypeDimension(BaseUtils.getDataTypeDimension(request));
         List<HotWordRankResponse> list = this.hotWordService.getHotWordRankService(hotWordRankRequest);
         return ResponseResult.success(list, "查询成功!");
      } catch (ViePlatformServiceException var4) {
         logger.error("【getHotWordRankService】方法调用出错", var4);
         return ResponseResult.error(var4.getMessage());
      }
   }
}
