package com.iflytek.vie.standard.demo;

import com.iflytek.vie.app.api.custom.CustomHomePageService;
import com.iflytek.vie.app.pojo.custom.CustomHomePage;
import com.iflytek.vie.app.pojo.custom.CustomHomePageRequest;
import com.iflytek.vie.pojo.ResponseResult;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("demoTest")
@RequestMapping({"/demoTest"})
public class DemoTest {
   @Autowired
   private CustomHomePageService customHomePageService;

   @RequestMapping({"queryCustomHomePageList"})
   @ResponseBody
   public ResponseResult queryCustomHomePageList() {
      CustomHomePageRequest customHomePageRequest = new CustomHomePageRequest();

      try {
         customHomePageRequest.setUserId("133");
         List<CustomHomePage> list = this.customHomePageService.queryCustomHomePageList(customHomePageRequest);
         return ResponseResult.success(list, "查询成功!");
      } catch (Exception var3) {
         return ResponseResult.error(var3.getMessage());
      }
   }
}
