package com.iflytek.vie.standard.association;

import com.iflytek.vie.app.api.tools.WordAssociationService;
import com.iflytek.vie.app.pojo.auth.AuthorizeInfo;
import com.iflytek.vie.app.pojo.dimension.WordAssociationResponse;
import com.iflytek.vie.base.CommonService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("associationController")
@RequestMapping({"association"})
public class AssociationController {
   private final Logger logger = LoggerFactory.getLogger(AssociationController.class);
   @Autowired
   private WordAssociationService wordAssociationService;
   @Autowired
   private CommonService commonService;

   @RequestMapping({"queryWordAssociation"})
   @ResponseBody
   public ResponseResult queryWordAssociation(HttpServletRequest request, @RequestParam(value = "word",required = false) String word, @RequestParam(value = "wordNumber",required = false) String wordNumber) {
      try {
         AuthorizeInfo authorizeInfo = new AuthorizeInfo();
         authorizeInfo.setUserId(BaseUtils.getUserId(request));
         authorizeInfo.setDataSource(BaseUtils.getDataSource(request));
         authorizeInfo.setSystemId(this.commonService.getContextPath());
         int wordCount = Integer.parseInt(LoadConfig.getConfigProperty("wordAssociationCount"));
         if (!StringUtils.isNullOrEmpry(wordNumber)) {
            wordCount = Integer.parseInt(wordNumber);
         }

         List<WordAssociationResponse> wordAssociationResponse = this.wordAssociationService.getWordAssiociationService(word, wordCount, authorizeInfo);
         return ResponseResult.success(wordAssociationResponse, "查询成功!");
      } catch (Exception var7) {
         this.logger.error("词语联想出错", var7);
         return ResponseResult.error(var7.getMessage());
      }
   }
}
