package com.iflytek.vie.standard.login;

import com.iflytek.vie.app.api.permission.DataAuthService;
import com.iflytek.vie.app.api.permission.FunctionAuthService;
import com.iflytek.vie.app.api.permission.LogService;
import com.iflytek.vie.app.api.permission.UserService;
import com.iflytek.vie.app.api.utils.DateUtils;
import com.iflytek.vie.app.pojo.permission.LoginRequest;
import com.iflytek.vie.app.pojo.permission.ResponseStatus;
import com.iflytek.vie.app.pojo.permission.UpdateLoginLog;
import com.iflytek.vie.app.pojo.permission.UpdatePwdRequest;
import com.iflytek.vie.app.pojo.permission.UserBasicInfo;
import com.iflytek.vie.config.LoadConfig;
import com.iflytek.vie.constants.IndexConstants;
import com.iflytek.vie.custom.api.permission.UserCustomService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.DomainVerificationUtils;
import com.iflytek.vie.utils.StringUtils;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("loginController")
public class LoginController {
   @Autowired
   private UserService userService;
   @Autowired
   private UserCustomService userCustomService;
   @Autowired
   private FunctionAuthService functionAuthService;
   @Autowired
   private DataAuthService dataAuthService;
   @Autowired
   private LogService logService;
   private static final String ALGORITHM = "AES/GCM/NoPadding";
   private static final int IV_LENGTH = 16;

   public static String decrypt(String encryptedText) throws Exception {
      String key = "1234567890123456";
      String iv = "1234567890123456";
      byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
      byte[] keyBytes = key.getBytes("UTF-8");
      byte[] ivBytes = iv.getBytes("UTF-8");
      SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
      IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(2, secretKey, ivParameterSpec);
      byte[] original = cipher.doFinal(encryptedBytes);
      return new String(original, "UTF-8");
   }

   @RequestMapping(
      value = {"dataSource"},
      method = {RequestMethod.POST, RequestMethod.GET}
   )
   public String dataSource(HttpServletRequest request, ModelMap map) {
      String userName = BaseUtils.getUserName(request);
      if (userName == null) {
         return "logout";
      } else {
         map.put("systemDate", DateUtils.getDateFormat(new Date(), "yyyy-MM-dd"));
         map.put("userName", userName);
         int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
         map.put("insightType", insightType);
         return "index";
      }
   }

   @RequestMapping(
      value = {"loginIn"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult checkLogin(LoginRequest params, HttpServletRequest request) {
      if (null != params && null != params.getAccountName() && !"".equals(params.getAccountName()) && null != params.getPassword() && !"".equals(params.getPassword())) {
         try {
            String accountName = new String(decrypt(params.getAccountName()));
            String password = new String(decrypt(params.getPassword()));
            params.setAccountName(accountName);
            params.setPassword(password);
            UserBasicInfo result = null;
            String domain_Account_Login = LoadConfig.getConfigProperty("domain_account_login");
            if (StringUtils.isNotNullAndEmpry(domain_Account_Login) && domain_Account_Login.trim().equals("0")) {
               int res = DomainVerificationUtils.authenticates(accountName, password);
               if (res != 0) {
                  if (res == -1) {
                     return ResponseResult.error("域账户验证失败，密码错误");
                  }

                  return ResponseResult.error("域账户验证失败，其他异常");
               }

               result = this.userCustomService.getUserBasicInfoByUserName(accountName);
               if (null == result) {
                  return ResponseResult.error("权限系统里面不存在此账户");
               }
            } else {
               result = this.userService.checkLogin(params);
               if (null == result) {
                  return ResponseResult.error("用户名或密码不正确");
               }
            }

            HttpSession session = request.getSession(false);
            if (session != null) {
               session.invalidate();
            }

            HttpSession session1 = request.getSession(true);
            session1.setAttribute("sys_user_info", result);
            return ResponseResult.success(result, "查询成功!");
         } catch (Exception var9) {
            return ResponseResult.error("服务内部错误!");
         }
      } else {
         return ResponseResult.error("入参不正确");
      }
   }

   @RequestMapping({"logout"})
   public String logout(HttpServletRequest request) {
      HttpSession session = request.getSession();

      try {
         if (session != null) {
            UpdateLoginLog loginLog = new UpdateLoginLog();
            loginLog.setLogoutTime(DateUtils.toDateStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
            loginLog.setSessionId(session.getId());
            this.logService.updateLoginLog(loginLog);
            session.removeAttribute("sys_user_info");
            session.removeAttribute("user_system_datasource");
            session.invalidate();
         }

         return "login";
      } catch (Exception var4) {
         return "login";
      }
   }

   @RequestMapping({"nosession"})
   @ResponseBody
   public Map<String, Object> nosession(HttpServletRequest request) {
      Map<String, Object> result = new HashMap();
      result.put("success", false);
      result.put("message", "当前用户未登录或者超时，请重新登录!");
      result.put("value", "login.html");
      result.put("errorCode", "SessionOut");
      return result;
   }

   public static void main(String[] args) throws Exception {
      String asdadawdaw222 = decrypt("asdadawdaw222");
      String asdfgh12345678 = decrypt("asdfgh12345678");
      System.out.println(asdadawdaw222);
      System.out.println(asdfgh12345678);
   }

   @RequestMapping(
      value = {"login/updatePwd"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult updatePwd(UpdatePwdRequest params, HttpServletRequest request) {
      ResponseResult response = new ResponseResult();
      if (null != params && null != params.getNewPassword() && !"".equals(params.getNewPassword()) && null != params.getOldPassword() && !"".equals(params.getOldPassword())) {
         try {
            params.setNewPassword(decrypt(params.getNewPassword()));
            params.setOldPassword(decrypt(params.getOldPassword()));
            params.setUserId(Long.valueOf(BaseUtils.getUserId(request)));
            ResponseStatus status = this.userService.updatePwd(params);
            if (status.isStatus()) {
               response.setSuccess(true);
            }

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
}
