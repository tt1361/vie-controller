package com.iflytek.vie.standard.player;

import com.iflytek.vie.app.api.datadrill.DataDrillService;
import com.iflytek.vie.app.api.dataquery.DataQueryService;
import com.iflytek.vie.app.api.player.PlayerService;
import com.iflytek.vie.app.api.player.server.wave.objects.InitialiseWaveFormat;
import com.iflytek.vie.app.exception.VieAppServiceException;
import com.iflytek.vie.app.exception.ViePlatformServiceException;
import com.iflytek.vie.app.pojo.datadrill.DataDetailRequest;
import com.iflytek.vie.app.pojo.datadrill.TaskDetailsResponse;
import com.iflytek.vie.app.pojo.dataquery.DataFilter;
import com.iflytek.vie.app.pojo.dataquery.DataQueryRequest;
import com.iflytek.vie.app.pojo.dataquery.PlayAudio;
import com.iflytek.vie.app.pojo.dataquery.VoiceDataRequest;
import com.iflytek.vie.app.pojo.dataquery.VoiceDataResponse;
import com.iflytek.vie.app.pojo.player.ContactKwContext;
import com.iflytek.vie.app.pojo.player.ContantKwRequest;
import com.iflytek.vie.app.pojo.player.ModelKeyWordRequest;
import com.iflytek.vie.app.pojo.player.PlayerDataRequest;
import com.iflytek.vie.app.pojo.player.RuleInfo;
import com.iflytek.vie.base.CommonService;
import com.iflytek.vie.config.LoadConfig;
import com.iflytek.vie.constants.IndexConstants;
import com.iflytek.vie.custom.api.player.PlayerCustomService;
import com.iflytek.vie.pojo.ResponseResult;
import com.iflytek.vie.utils.BaseUtils;
import com.iflytek.vie.utils.PropertyPlaceholderConfigurerUtils;
import com.iflytek.vie.utils.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("playerController")
@RequestMapping({"/player"})
public class PlayerController {
   private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);
   @Autowired
   private DataQueryService dataQueryService;
   @Autowired
   private PlayerService playerService;
   @Autowired
   private PlayerCustomService playerCustomService;
   @Autowired
   private DataDrillService dataDrillService;
   @Autowired
   private CommonService commonService;

   @RequestMapping(
      value = {"getVoiceList"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getVoiceList(HttpServletRequest request, @RequestParam(value = "callId",required = true) String ID, @RequestParam(value = "dataSource",required = true) String dataSourceParam, @RequestParam(value = "token",required = false) String token) {
      ResponseResult result = null;
      String userName = BaseUtils.getUserName(request);
      logger.info("用户： " + userName + "  调听录音ID： " + ID);
      if (dataSourceParam.equals("vie-flynull")) {
         int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
         if (insightType == 1) {
            result = this.getVoiceList_ByTask(ID, request, dataSourceParam);
            return result;
         } else {
            result = this.getVoiceList_ByVoice(ID, request, dataSourceParam);
            return result;
         }
      } else if (StringUtils.isNullOrEmpry(token)) {
         return ResponseResult.error("免密登录需要第三方token", 1);
      } else {
         boolean flag = false;
         MultiValueMap requestBody = new LinkedMultiValueMap();
         requestBody.add("token", token);
         String url = (String)PropertyPlaceholderConfigurerUtils.getAllUserData().get("apiTokenCheckUrl");
         ResponseEntity response = null;

         try {
            Map map = new HashMap();
            map.put("token", token);
            flag = Boolean.parseBoolean(HttpClientUtilsTwo.sendGet(url, map));
            logger.info("方式二获取值：{}", flag);
         } catch (Exception var12) {
            return ResponseResult.error("请求第三方服务失败，链接不上", 1);
         }

         if (!flag) {
            return ResponseResult.error("第三方提供的token失效", 1);
         } else {
            int insightType = IndexConstants.getInsightType(dataSourceParam);
            if (insightType == 1) {
               result = this.getVoiceList_ByTask(ID, request, dataSourceParam);
               return result;
            } else {
               result = this.getVoiceList_ByVoice(ID, request, dataSourceParam);
               return result;
            }
         }
      }
   }

   public ResponseResult getVoiceList_ByVoice(String callId, HttpServletRequest request, String dataSourceParam) {
      logger.info("===========getVoiceList_ByVoice方法被调用========开始==");
      VoiceDataRequest voiceDataRequest = new VoiceDataRequest();

      try {
         if (dataSourceParam.equals("vie-flynull")) {
            voiceDataRequest.setDataSource(BaseUtils.getDataSource(request));
         } else {
            voiceDataRequest.setDataSource(dataSourceParam);
         }

         String selVoiceColumn = LoadConfig.getConfigProperty("selVoiceColumn");
         if (selVoiceColumn != null && !selVoiceColumn.contains("listenUrl")) {
            selVoiceColumn = selVoiceColumn + ",listenUrl";
         }
         voiceDataRequest.setSelVoiceColumn(selVoiceColumn);
         voiceDataRequest.setCallId(callId);
         VoiceDataResponse voiceDataResponse = this.dataQueryService.getVoiceList_ByVoice(voiceDataRequest);
         logger.info("===========getVoiceList_ByVoice方法被调用========结束");
         return ResponseResult.success(voiceDataResponse, "查询成功!");
      } catch (Exception var7) {
         logger.error("【getVoiceList】方法调用错误", var7);
         return ResponseResult.error(var7.getMessage());
      }
   }

   public ResponseResult getVoiceList_ByTask(String taskId, HttpServletRequest request, String dataSourceParam) {
      logger.info("===========getVoiceList_ByTask方法被调用========开始");
      VoiceDataRequest voiceDataRequest = new VoiceDataRequest();

      try {
         if (dataSourceParam.equals("vie-flynull")) {
            voiceDataRequest.setDataSource(BaseUtils.getDataSource(request));
         } else {
            voiceDataRequest.setDataSource(dataSourceParam);
         }

         String selVoiceColumn = LoadConfig.getConfigProperty("selVoiceColumn");
         if (selVoiceColumn != null && !selVoiceColumn.contains("listenUrl")) {
            selVoiceColumn = selVoiceColumn + ",listenUrl";
         }
         voiceDataRequest.setSelVoiceColumn(selVoiceColumn);
         voiceDataRequest.setTaskId(taskId);
         VoiceDataResponse voiceDataResponse = this.dataQueryService.getVoiceList_ByTask(voiceDataRequest);
         logger.info("===========getVoiceList_ByTask方法被调用========结束");
         return ResponseResult.success(voiceDataResponse, "查询成功!");
      } catch (Exception var7) {
         logger.error("【getVoiceList】方法调用错误", var7);
         return ResponseResult.error(var7.getMessage());
      }
   }

   @RequestMapping(
      value = {"getFullAudioInfo"},
      method = {RequestMethod.POST, RequestMethod.GET}
   )
   @ResponseBody
   public void getFullAudioInfo(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "callId",required = true) String callId, @RequestParam(value = "taskId",required = false) String taskId, @RequestParam(value = "dataSource",required = false) String jspDataSource, @RequestParam(value = "token",required = false) String token) {
      if (!String.valueOf(jspDataSource).equals("") && String.valueOf(jspDataSource) != null && !String.valueOf(jspDataSource).equals("null") && !String.valueOf(jspDataSource).equals("vie-flynull")) {
         if (StringUtils.isNullOrEmpry(token)) {
            this.handleErrorApiTokenCheck(response, "免密登录需要第三方token");
         }

         boolean flag = false;
         MultiValueMap requestBody = new LinkedMultiValueMap();
         requestBody.add("token", token);
         String url = (String)PropertyPlaceholderConfigurerUtils.getAllUserData().get("apiTokenCheckUrl");
         ResponseEntity responseApi = null;

         try {
            Map map = new HashMap();
            map.put("token", token);
            flag = Boolean.parseBoolean(HttpClientUtilsTwo.sendGet(url, map));
            logger.info("方式二获取值：{}", flag);
         } catch (Exception var12) {
            this.handleErrorApiTokenCheck(response, "请求第三方服务失败，链接不上");
         }

         if (!flag) {
            this.handleErrorApiTokenCheck(response, "第三方提供的token失效");
         }

         int insightType = IndexConstants.getInsightType(jspDataSource);
         if (insightType == 1) {
            this.getFullAudioInfo_ByTask_forJSP(request, response, callId, taskId, jspDataSource);
         } else {
            this.getFullAudioInfo_ByVoice_forJSP(request, response, callId, jspDataSource);
         }
      } else {
         int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
         if (insightType == 1) {
            this.getFullAudioInfo_ByTask(request, response, callId, taskId);
         } else {
            this.getFullAudioInfo_ByVoice(request, response, callId);
         }
      }

   }

   private void handleErrorApiTokenCheck(HttpServletResponse response, String errmsg) {
      PrintWriter out = null;

      try {
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/xml;charset=utf-8");
         response.setHeader("Cache-Control", "no-cache");
         out = response.getWriter();
         out.write("");
         logger.info("远程调用失败:" + errmsg);
      } catch (Exception var8) {
         logger.error("【handleErrorApiTokenCheck】方法调用错误", var8);
      } finally {
         if (out != null) {
            out.flush();
            out.close();
         }

      }

   }

   public void getFullAudioInfo_ByVoice(HttpServletRequest request, HttpServletResponse response, String callId) {
      logger.info("===========getFullAudioInfo_ByVoice方法被调用========开始");
      PrintWriter out = null;
      String speechUrl = "";
      DataQueryRequest dataQueryRequest = new DataQueryRequest();
      speechUrl = request.getRequestURL().toString();
      speechUrl = speechUrl.substring(0, speechUrl.lastIndexOf("/"));
      speechUrl = speechUrl + "/getSlicedStreamData";
      dataQueryRequest.setSpeechUrl(speechUrl);
      dataQueryRequest.setCallId(callId);
      dataQueryRequest.setDataSource(BaseUtils.getDataSource(request));
      String result = "";

      try {
         result = this.dataQueryService.getSilverlightPlayText_ByVoice(dataQueryRequest);
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/xml;charset=utf-8");
         response.setHeader("Cache-Control", "no-cache");
         out = response.getWriter();
         out.write(result);
         logger.info("===========getFullAudioInfo_ByVoice方法被调用========结束");
      } catch (Exception var12) {
         logger.error("【getFullAudioInfo】方法调用错误", var12);
      } finally {
         if (out != null) {
            out.flush();
            out.close();
         }

      }

   }

   public void getFullAudioInfo_ByVoice_forJSP(HttpServletRequest request, HttpServletResponse response, String callId, String dataSource) {
      logger.info("===========getFullAudioInfo_ByVoice方法被调用========开始");
      PrintWriter out = null;
      String speechUrl = "";
      DataQueryRequest dataQueryRequest = new DataQueryRequest();
      speechUrl = request.getRequestURL().toString();
      speechUrl = speechUrl.substring(0, speechUrl.lastIndexOf("/"));
      speechUrl = speechUrl + "/getSlicedStreamData";
      dataQueryRequest.setSpeechUrl(speechUrl);
      dataQueryRequest.setCallId(callId);
      dataQueryRequest.setDataSource(dataSource);
      String result = "";

      try {
         result = this.dataQueryService.getSilverlightPlayText_ByVoice(dataQueryRequest);
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/xml;charset=utf-8");
         response.setHeader("Cache-Control", "no-cache");
         out = response.getWriter();
         out.write(result);
         logger.info("===========getFullAudioInfo_ByVoice方法被调用========结束");
      } catch (Exception var13) {
         logger.error("【getFullAudioInfo】方法调用错误", var13);
      } finally {
         if (out != null) {
            out.flush();
            out.close();
         }

      }

   }

   public void getFullAudioInfo_ByTask(HttpServletRequest request, HttpServletResponse response, String callId, String taskId) {
      logger.info("===========getFullAudioInfo_ByTask方法被调用========开始");
      PrintWriter out = null;
      String speechUrl = "";
      DataQueryRequest dataQueryRequest = new DataQueryRequest();
      speechUrl = request.getRequestURL().toString();
      speechUrl = speechUrl.substring(0, speechUrl.lastIndexOf("/"));
      speechUrl = speechUrl + "/getSlicedStreamData";
      dataQueryRequest.setSpeechUrl(speechUrl);
      dataQueryRequest.setCallId(callId);
      dataQueryRequest.setTaskId(taskId);
      dataQueryRequest.setDataSource(BaseUtils.getDataSource(request));
      String result = "";

      try {
         result = this.dataQueryService.getSilverlightPlayText_ByTask(dataQueryRequest);
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/xml;charset=utf-8");
         response.setHeader("Cache-Control", "no-cache");
         out = response.getWriter();
         out.write(result);
         logger.info("===========getFullAudioInfo_ByTask方法被调用========结束");
      } catch (Exception var13) {
         logger.error("【getFullAudioInfo】方法调用错误", var13);
      } finally {
         if (out != null) {
            out.flush();
            out.close();
         }

      }

   }

   public void getFullAudioInfo_ByTask_forJSP(HttpServletRequest request, HttpServletResponse response, String callId, String taskId, String dataSource) {
      logger.info("===========getFullAudioInfo_ByTask方法被调用========开始");
      PrintWriter out = null;
      String speechUrl = "";
      DataQueryRequest dataQueryRequest = new DataQueryRequest();
      speechUrl = request.getRequestURL().toString();
      speechUrl = speechUrl.substring(0, speechUrl.lastIndexOf("/"));
      speechUrl = speechUrl + "/getSlicedStreamData";
      dataQueryRequest.setSpeechUrl(speechUrl);
      dataQueryRequest.setCallId(callId);
      dataQueryRequest.setTaskId(taskId);
      dataQueryRequest.setDataSource(dataSource);
      String result = "";

      try {
         result = this.dataQueryService.getSilverlightPlayText_ByTask(dataQueryRequest);
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/xml;charset=utf-8");
         response.setHeader("Cache-Control", "no-cache");
         out = response.getWriter();
         out.write(result);
         logger.info("===========getFullAudioInfo_ByTask方法被调用========结束");
      } catch (Exception var14) {
         logger.error("【getFullAudioInfo】方法调用错误", var14);
      } finally {
         if (out != null) {
            out.flush();
            out.close();
         }

      }

   }

   @RequestMapping(
      value = {"getSlicedStreamData"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public void getSlicedStreamData(HttpServletResponse response, @RequestParam(value = "voiceUrl",required = true) String voiceUrl, @RequestParam(value = "offset",required = true) int offset, @RequestParam(value = "macTag",required = true) String macTag, @RequestParam(value = "count",required = true) int count, @RequestParam(value = "begin",required = false) Integer begin) {
      logger.info("===========getSlicedStreamData方法被调用========开始");
      OutputStream out = null;
      DataQueryRequest dataQueryRequest = new DataQueryRequest();
      dataQueryRequest.setVoicePath(voiceUrl);
      if (begin != null) {
         dataQueryRequest.setOffset(begin + offset);
      } else {
         dataQueryRequest.setOffset(offset);
      }

      dataQueryRequest.setMacTag(macTag);
      dataQueryRequest.setCount(count);
      try {
         out = response.getOutputStream();
         byte[] result = this.dataQueryService.getSlicedStreamData(dataQueryRequest);
         response.setContentType("application/octet-stream");
         out.write(result);
         logger.info("===========getSlicedStreamData方法被调用========结束");
      } catch (Exception var19) {
         logger.error("【getSlicedStreamData】方法调用错误", var19);
      } finally {
         if (out != null) {
            try {
               out.flush();
               out.close();
            } catch (IOException var18) {
               logger.error("关闭流错误", var18);
            }
         }

      }

   }

   @RequestMapping(
      value = {"queryAudioKeyWord"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryAudioKeyWord(HttpServletRequest request, @RequestParam(value = "voiceId",required = true) String voiceId, @RequestParam(value = "isLoad",required = true) String isLoad, @RequestParam(value = "taskId",required = false) String taskId, @RequestParam(value = "dataSource",required = true) String dataSourceParam, @RequestParam(value = "token",required = false) String token) {
      ResponseResult result = null;
      if (dataSourceParam.equals("vie-flynull")) {
         int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
         if (insightType == 1) {
            result = this.queryAudioKeyWord_ByTask(request, voiceId, isLoad, taskId, dataSourceParam);
            return result;
         } else {
            result = this.queryAudioKeyWord_ByVoice(request, voiceId, isLoad, dataSourceParam);
            return result;
         }
      } else if (StringUtils.isNullOrEmpry(token)) {
         return ResponseResult.error("免密登录需要第三方token", 1);
      } else {
         boolean flag = false;
         MultiValueMap requestBody = new LinkedMultiValueMap();
         requestBody.add("token", token);
         String url = (String)PropertyPlaceholderConfigurerUtils.getAllUserData().get("apiTokenCheckUrl");
         ResponseEntity response = null;

         try {
            Map map = new HashMap();
            map.put("token", token);
            flag = Boolean.parseBoolean(HttpClientUtilsTwo.sendGet(url, map));
         } catch (Exception var13) {
            return ResponseResult.error("请求第三方服务失败，链接不上", 1);
         }

         if (!flag) {
            return ResponseResult.error("第三方提供的token失效", 1);
         } else {
            int insightType = IndexConstants.getInsightType(dataSourceParam);
            if (insightType == 1) {
               result = this.queryAudioKeyWord_ByTask(request, voiceId, isLoad, taskId, dataSourceParam);
               return result;
            } else {
               result = this.queryAudioKeyWord_ByVoice(request, voiceId, isLoad, dataSourceParam);
               return result;
            }
         }
      }
   }

   public ResponseResult queryAudioKeyWord_ByVoice(HttpServletRequest request, String voiceId, String isLoad, String dataSourceParam) {
      logger.info("===========queryAudioKeyWord_ByVoice方法被调用========开始");
      ModelKeyWordRequest modelKeyWordRequest = new ModelKeyWordRequest();

      try {
         String userId = BaseUtils.getUserId(request);
         if (StringUtils.isNotNullAndEmpry(userId)) {
            modelKeyWordRequest.setUserId(userId);
         } else {
            modelKeyWordRequest.setUserId("-1");
         }

         modelKeyWordRequest.setCallId(voiceId);
         if (dataSourceParam.equals("vie-flynull")) {
            modelKeyWordRequest.setDataSource(BaseUtils.getDataSource(request));
         } else {
            modelKeyWordRequest.setDataSource(dataSourceParam);
         }

         modelKeyWordRequest.setIsLoad(1);
         List list = this.playerCustomService.getOnlineModelKeyWordService_ByVoice_SpdCustom(modelKeyWordRequest);
         Map result = this.handleKeyWordResponse(list);
         logger.info("===========queryAudioKeyWord_ByVoice方法被调用========结束");
         return ResponseResult.success(result, "查询成功!");
      } catch (Exception var9) {
         logger.error("【queryAudioKeyWord】方法调用错误", var9);
         return ResponseResult.error(var9.getMessage());
      }
   }

   public ResponseResult queryAudioKeyWord_ByTask(HttpServletRequest request, String voiceId, String isLoad, String taskId, String dataSourceParam) {
      logger.info("===========queryAudioKeyWord_ByTask方法被调用========开始");
      ModelKeyWordRequest modelKeyWordRequest = new ModelKeyWordRequest();

      try {
         String userId = BaseUtils.getUserId(request);
         if (StringUtils.isNotNullAndEmpry(userId)) {
            modelKeyWordRequest.setUserId(userId);
         } else {
            modelKeyWordRequest.setUserId("-1");
         }

         modelKeyWordRequest.setCallId(voiceId);
         modelKeyWordRequest.setTaskId(taskId);
         if (dataSourceParam.equals("vie-flynull")) {
            modelKeyWordRequest.setDataSource(BaseUtils.getDataSource(request));
         } else {
            modelKeyWordRequest.setDataSource(dataSourceParam);
         }

         modelKeyWordRequest.setIsLoad(1);
         List list = this.playerCustomService.getOnlineModelKeyWordService_ByTask_SpdCustom(modelKeyWordRequest);
         Map result = this.handleKeyWordResponse(list);
         logger.info("===========queryAudioKeyWord_ByTask方法被调用========结束");
         return ResponseResult.success(result, "查询成功!");
      } catch (Exception var10) {
         logger.error("【queryAudioKeyWord】方法调用错误", var10);
         return ResponseResult.error(var10.getMessage());
      }
   }

   @RequestMapping(
      value = {"getContactKwContext"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getContactKwContext(HttpServletRequest request, @RequestParam(value = "voiceId",required = true) String callId, @RequestParam(value = "begin",required = true) int begin, @RequestParam(value = "callId",required = false) String taskId) {
      ResponseResult result = null;
      int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
      if (insightType == 1) {
         result = this.getContactKwContext_ByTask(request, callId, begin, taskId);
         return result;
      } else {
         result = this.getContactKwContext_ByVoice(request, callId, begin);
         return result;
      }
   }

   public ResponseResult getContactKwContext_ByVoice(HttpServletRequest request, String callId, int begin) {
      logger.info("===========getContactKwContext_ByVoice方法被调用========开始");

      try {
         List queryList = new ArrayList();
         queryList.add("timePosition");
         queryList.add("duration");
         queryList.add("contentOrigin");
         LinkedHashMap map = this.getAudioBaseInfo_ByVoice(callId, queryList, request, "vie-flynull");
         String timePosition = String.valueOf(map.get("timePosition"));
         String duration = String.valueOf(map.get("duration"));
         String contentOrigin = String.valueOf(map.get("contentOrigin"));
         ContantKwRequest contantKwRequest = new ContantKwRequest();
         contantKwRequest.setTimePosition(timePosition);
         contantKwRequest.setDuration(duration);
         contantKwRequest.setContentOrigin(contentOrigin);
         contantKwRequest.setBegin(begin);
         ContactKwContext result = this.playerService.getContantTaskKwContext(contantKwRequest);
         logger.info("===========getContactKwContext_ByVoice方法被调用========结束");
         return ResponseResult.success(result, "查询成功!");
      } catch (Exception var11) {
         logger.error("【getContactKwContext】方法调用错误", var11);
         return ResponseResult.error(var11.getMessage());
      }
   }

   public ResponseResult getContactKwContext_ByTask(HttpServletRequest request, String callId, int begin, String taskId) {
      logger.info("===========getContactKwContext_ByTask方法被调用========开始");

      try {
         List queryList = new ArrayList();
         queryList.add("timePosition");
         queryList.add("childDuration");
         queryList.add("childContentLength");
         queryList.add("contentOrigin");
         queryList.add("voiceId");
         LinkedHashMap map = this.getAudioBaseInfo_ByTask(taskId, queryList, request, "vie-flynull");
         String[] SplitedTimePosition = String.valueOf(map.get("timePosition")).split("\\$");
         String[] SplitedContentOrigin = String.valueOf(map.get("contentOrigin")).split("\\$");
         String[] SplitedVoiceId = String.valueOf(map.get("voiceId")).split("\\$");
         ArrayList child_fields = (ArrayList)map.get("child_fields");
         int VoiceIdCorrspondTaskIdPosition = -1;

         for(int i = 0; i < SplitedVoiceId.length; ++i) {
            if (callId.equals(String.valueOf(SplitedVoiceId[i]))) {
               VoiceIdCorrspondTaskIdPosition = i;
               break;
            }
         }

         if (VoiceIdCorrspondTaskIdPosition == -1) {
            logger.error("在taskId为" + taskId + "的任务下没有找到录音号为" + callId + "的录音。");
            throw new ViePlatformServiceException("在对应的taskId下没有找到对应的录音。");
         } else {
            String voice_timePosition = SplitedTimePosition[VoiceIdCorrspondTaskIdPosition];
            String voice_ContentOrigin = SplitedContentOrigin[VoiceIdCorrspondTaskIdPosition];
            String voice_Duration = String.valueOf(((HashMap)child_fields.get(VoiceIdCorrspondTaskIdPosition)).get("childDuration"));
            String timePosition = String.valueOf(voice_timePosition);
            String duration = String.valueOf(voice_Duration);
            String contentOrigin = String.valueOf(voice_ContentOrigin);
            ContantKwRequest contantKwRequest = new ContantKwRequest();
            contantKwRequest.setTimePosition(timePosition);
            contantKwRequest.setDuration(duration);
            contantKwRequest.setContentOrigin(contentOrigin);
            int redundantTime = 0;

            for(int i = 0; i < VoiceIdCorrspondTaskIdPosition; ++i) {
               redundantTime += Integer.parseInt(String.valueOf(((HashMap)child_fields.get(i)).get("childContentLength")));
            }

            contantKwRequest.setBegin(begin - redundantTime);
            ContactKwContext result = this.playerService.getContantTaskKwContext(contantKwRequest);
            logger.info("===========getContactKwContext_ByTask方法被调用========结束");
            return ResponseResult.success(result, "查询成功!");
         }
      } catch (Exception var21) {
         logger.error("【getContactKwContext】方法调用错误", var21);
         return ResponseResult.error(var21.getMessage());
      }
   }

   @RequestMapping(
      value = {"getAudioClipsInfo"},
      method = {RequestMethod.POST, RequestMethod.GET}
   )
   @ResponseBody
   public void getAudioClipsInfo(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "voiceId",required = true) String voiceId, @RequestParam(value = "begin",required = true) int begin, @RequestParam(value = "end",required = true) int end, @RequestParam(value = "duration",required = true) int duration, @RequestParam(value = "channel",required = true) int channel, @RequestParam(value = "callId",required = false) String taskId) {
      int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
      if (insightType == 1) {
         this.getAudioClipsInfo_ByTask(request, response, voiceId, begin, end, duration, channel, taskId);
      } else {
         this.getAudioClipsInfo_ByVoice(request, response, voiceId, begin, end, duration, channel);
      }

   }

   public void getAudioClipsInfo_ByVoice(HttpServletRequest request, HttpServletResponse response, String voiceId, int begin, int end, int duration, int channel) {
      logger.info("===========getAudioClipsInfo_ByVoice方法被调用========开始");
      PrintWriter out = null;

      try {
         List queryList = new ArrayList();
         queryList.add("voiceUri");
         queryList.add("machineId");
         LinkedHashMap map = this.getAudioBaseInfo_ByVoice(voiceId, queryList, request, "vie-flynull");
         String voiceUrl = String.valueOf(map.get("voiceUri"));
         String machineTag = String.valueOf(map.get("machineId"));
         DataQueryRequest dataQueryRequest = new DataQueryRequest();
         dataQueryRequest.setCallId(voiceId);
         dataQueryRequest.setBegin(begin);
         dataQueryRequest.setEnd(end);
         dataQueryRequest.setDuration(duration);
         dataQueryRequest.setChannel(channel);
         dataQueryRequest.setVoicePath(voiceUrl);
         dataQueryRequest.setMacTag(machineTag);
         String speechUrl = request.getRequestURL().toString();
         speechUrl = speechUrl.substring(0, speechUrl.lastIndexOf("/"));
         speechUrl = speechUrl + "/getSlicedStreamData";
         dataQueryRequest.setSpeechUrl(speechUrl);
         String result = this.dataQueryService.getUcSilverlightPlayText(dataQueryRequest);
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/xml;charset=utf-8");
         response.setHeader("Cache-Control", "no-cache");
         out = response.getWriter();
         out.write(result);
         out.flush();
         out.close();
         logger.info("===========getAudioClipsInfo_ByVoice方法被调用========结束");
      } catch (Exception var19) {
         logger.error("【getAudioClipsInfo】方法调用错误", var19);
      } finally {
         if (out != null) {
            out.flush();
            out.close();
         }

      }

   }

   public void getAudioClipsInfo_ByTask(HttpServletRequest request, HttpServletResponse response, String voiceId, int begin, int end, int duration, int channel, String taskId) {
      logger.info("===========getAudioClipsInfo_ByTask方法被调用========开始");
      PrintWriter out = null;

      try {
         List queryList = new ArrayList();
         queryList.add("childVoiceUri");
         queryList.add("machineId");
         queryList.add("voiceId");
         LinkedHashMap map = this.getAudioBaseInfo_ByTask(taskId, queryList, request, "vie-flynull");
         int CallIdCorrespondToTaskPosition = -1;
         String[] SplitedVoiceId = map.get("voiceId").toString().split("\\$");

         for(int i = 0; i < SplitedVoiceId.length; ++i) {
            if (voiceId.equals(String.valueOf(SplitedVoiceId[i]))) {
               CallIdCorrespondToTaskPosition = i;
               break;
            }
         }

         if (CallIdCorrespondToTaskPosition == -1) {
            logger.error("在任务号为" + taskId + "的任务下没有找到录音流水号为" + voiceId + "的录音");
            throw new ViePlatformServiceException("在任务号为" + taskId + "的任务下没有找到录音流水号为" + voiceId + "的录音");
         }

         new ArrayList();
         ArrayList child_fields = (ArrayList)map.get("child_fields");
         String voiceUrl = String.valueOf(((HashMap)child_fields.get(CallIdCorrespondToTaskPosition)).get("childVoiceUri").toString());
         String machineTag = String.valueOf(map.get("machineId"));
         DataQueryRequest dataQueryRequest = new DataQueryRequest();
         dataQueryRequest.setCallId(voiceId);
         dataQueryRequest.setTaskId(taskId);
         dataQueryRequest.setBegin(begin);
         dataQueryRequest.setEnd(end);
         dataQueryRequest.setDuration(duration);
         dataQueryRequest.setChannel(channel);
         dataQueryRequest.setVoicePath(voiceUrl);
         dataQueryRequest.setMacTag(machineTag);
         String speechUrl = request.getRequestURL().toString();
         speechUrl = speechUrl.substring(0, speechUrl.lastIndexOf("/"));
         speechUrl = speechUrl + "/getSlicedStreamData";
         dataQueryRequest.setSpeechUrl(speechUrl);
         String result = this.dataQueryService.getUcSilverlightPlayText(dataQueryRequest);
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/xml;charset=utf-8");
         response.setHeader("Cache-Control", "no-cache");
         out = response.getWriter();
         out.write(result);
         out.flush();
         out.close();
         logger.info("===========getAudioClipsInfo_ByTask方法被调用========结束");
      } catch (Exception var23) {
         logger.error("【getAudioClipsInfo】方法调用错误", var23);
      } finally {
         if (out != null) {
            out.flush();
            out.close();
         }

      }

   }

   @RequestMapping(
      value = {"getWavFormat"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getWavFormat(HttpServletRequest request, @RequestParam(value = "voiceId",required = true) String voiceId, @RequestParam(value = "callId",required = false) String taskId, @RequestParam(value = "dataSource",required = true) String dataSourceParam, @RequestParam(value = "token",required = false) String token) {
      ResponseResult result = null;
      int insightType;
      if (dataSourceParam.equals("vie-flynull")) {
         insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
      } else {
         if (StringUtils.isNullOrEmpry(token)) {
            return ResponseResult.error("免密登录需要第三方token", 1);
         }

         boolean flag = false;
         MultiValueMap requestBody = new LinkedMultiValueMap();
         requestBody.add("token", token);
         String url = (String)PropertyPlaceholderConfigurerUtils.getAllUserData().get("apiTokenCheckUrl");
         ResponseEntity response = null;

         try {
            Map map = new HashMap();
            map.put("token", token);
            flag = Boolean.parseBoolean(HttpClientUtilsTwo.sendGet(url, map));
         } catch (Exception var13) {
            return ResponseResult.error("请求第三方服务失败，链接不上", 1);
         }

         if (!flag) {
            return ResponseResult.error("第三方提供的token失效", 1);
         }

         insightType = IndexConstants.getInsightType(dataSourceParam);
      }

      if (insightType == 1) {
         result = this.getWavFormat_ByTask(request, voiceId, taskId);
         return result;
      } else {
         result = this.getWavFormat_ByVoice(request, voiceId, dataSourceParam);
         return result;
      }
   }

   public ResponseResult getWavFormat_ByVoice(HttpServletRequest request, String voiceId, String dataSource) {
      logger.info("===========getWavFormat_ByVoice方法被调用========开始");

      try {
         List queryList = new ArrayList();
         queryList.add("voiceUri");
         queryList.add("machineId");
         LinkedHashMap map = this.getAudioBaseInfo_ByVoice(voiceId, queryList, request, dataSource);
         String voiceUrl = map.get("voiceUri") == null ? "" : String.valueOf(map.get("voiceUri"));
         String macTag = map.get("machineId") == null ? "" : String.valueOf(map.get("machineId"));
         if (!StringUtils.isNullOrEmpry(voiceUrl) && !StringUtils.isNullOrEmpry(macTag)) {
            PlayerDataRequest playerDataRequest = new PlayerDataRequest();
            playerDataRequest.setVoiceUrl(voiceUrl);
            playerDataRequest.setMacTag(macTag);
            InitialiseWaveFormat initialiseWaveFormat = this.playerService.getVoiceFormatService(playerDataRequest);
            Map resultMap = new HashMap();
            this.groupResult(resultMap, initialiseWaveFormat);
            logger.info("===========getWavFormat_ByVoice方法被调用========结束");
            return ResponseResult.success(resultMap, "查询成功!");
         } else {
            return ResponseResult.error("获取音频地址或机器标识失败!");
         }
      } catch (Exception var11) {
         logger.error("【getVoiceFormat】方法调用错误", var11);
         return ResponseResult.error(var11.getMessage());
      }
   }

   public ResponseResult getWavFormat_ByTask(HttpServletRequest request, String voiceId, String taskId) {
      logger.info("===========getWavFormat_ByTask方法被调用========开始");

      try {
         List queryList = new ArrayList();
         queryList.add("childVoiceUri");
         queryList.add("machineId");
         queryList.add("childVoiceId");
         LinkedHashMap map = this.getAudioBaseInfo_ByTask(taskId, queryList, request, "vie-flynull");
         String voiceUrl = "";
         String macTag = map.get("machineId") == null ? "" : String.valueOf(map.get("machineId"));
         Object child_fields = map.get("child_fields");
         ArrayList child_fields_ArrayList = (ArrayList)child_fields;

         for(int i = 0; i < child_fields_ArrayList.size(); ++i) {
            String childVoiceIdStr = String.valueOf(((HashMap)child_fields_ArrayList.get(i)).get("childVoiceId"));
            if (voiceId.equals(childVoiceIdStr)) {
               voiceUrl = String.valueOf(((HashMap)child_fields_ArrayList.get(i)).get("childVoiceUri"));
               break;
            }
         }

         if (!StringUtils.isNullOrEmpry(voiceUrl) && !StringUtils.isNullOrEmpry(macTag)) {
            PlayerDataRequest playerDataRequest = new PlayerDataRequest();
            playerDataRequest.setVoiceUrl(voiceUrl);
            playerDataRequest.setMacTag(macTag);
            InitialiseWaveFormat initialiseWaveFormat = this.playerService.getVoiceFormatService(playerDataRequest);
            Map resultMap = new HashMap();
            this.groupResult(resultMap, initialiseWaveFormat);
            logger.info("===========getWavFormat_ByTask方法被调用========结束");
            return ResponseResult.success(resultMap, "查询成功!");
         } else {
            return ResponseResult.error("获取音频地址或机器标识失败!");
         }
      } catch (Exception var13) {
         logger.error("【getVoiceFormat】方法调用错误", var13);
         return ResponseResult.error(var13.getMessage());
      }
   }

   @RequestMapping(
      value = {"getSpeechInfo"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getSpeechInfo(HttpServletRequest request, @RequestParam(value = "callId",required = true) String taskId, @RequestParam(value = "voiceId",required = false) String voiceId, @RequestParam(value = "dataSource",required = true) String dataSourceParam, @RequestParam(value = "token",required = false) String token) {
      ResponseResult result = null;
      if (dataSourceParam.equals("vie-flynull")) {
         int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
         if (insightType == 1) {
            result = this.getSpeechInfo_ByTask(request, taskId, voiceId, dataSourceParam);
            return result;
         } else {
            result = this.getSpeechInfo_ByVoice(request, voiceId, dataSourceParam);
            return result;
         }
      } else if (StringUtils.isNullOrEmpry(token)) {
         return ResponseResult.error("免密登录需要第三方token", 1);
      } else {
         boolean flag = false;
         MultiValueMap requestBody = new LinkedMultiValueMap();
         requestBody.add("token", token);
         String url = (String)PropertyPlaceholderConfigurerUtils.getAllUserData().get("apiTokenCheckUrl");
         ResponseEntity response = null;

         try {
            Map map = new HashMap();
            map.put("token", token);
            flag = Boolean.parseBoolean(HttpClientUtilsTwo.sendGet(url, map));
         } catch (Exception var12) {
            return ResponseResult.error("请求第三方服务失败，链接不上", 1);
         }

         if (!flag) {
            return ResponseResult.error("第三方提供的token失效", 1);
         } else {
            int insightType = IndexConstants.getInsightType(dataSourceParam);
            if (insightType == 1) {
               result = this.getSpeechInfo_ByTask(request, taskId, voiceId, dataSourceParam);
               return result;
            } else {
               result = this.getSpeechInfo_ByVoice(request, voiceId, dataSourceParam);
               return result;
            }
         }
      }
   }

   public ResponseResult getSpeechInfo_ByVoice(HttpServletRequest request, String voiceId, String dataSourceParam) {
      logger.info("===========getSpeechInfo_ByVoice方法被调用========开始");

      try {
         List queryList = new ArrayList();
         queryList.add("voiceUri");
         queryList.add("machineId");
         queryList.add("listenUrl");
         LinkedHashMap map = this.getAudioBaseInfo_ByVoice(voiceId, queryList, request, dataSourceParam);
         String voiceUrl = map.get("voiceUri") == null ? "" : String.valueOf(map.get("voiceUri"));
         String macTag = map.get("machineId") == null ? "" : String.valueOf(map.get("machineId"));
         String listenUrl = map.get("listenUrl") == null ? "" : String.valueOf(map.get("listenUrl"));
         if (!StringUtils.isNullOrEmpry(voiceUrl) && !StringUtils.isNullOrEmpry(macTag)) {
            Map resultMap = new HashMap();
            resultMap.put("voicePath", voiceUrl);
            resultMap.put("macTag", macTag);
            resultMap.put("listenUrl", listenUrl);
            logger.info("===========getSpeechInfo_ByVoice方法被调用========结束");
            return ResponseResult.success(resultMap, "查询成功!");
         } else {
            return ResponseResult.error("获取音频地址或机器标识失败!");
         }
      } catch (Exception var9) {
         logger.error("【getSpeechInfo】方法调用错误", var9);
         return ResponseResult.error(var9.getMessage());
      }
   }

   public ResponseResult getSpeechInfo_ByTask(HttpServletRequest request, String taskId, String voiceId, String dataSourceParam) {
      logger.info("===========getSpeechInfo_ByTask方法被调用========开始");

      try {
         List queryList = new ArrayList();
         queryList.add("childVoiceUri");
         queryList.add("machineId");
         queryList.add("childVoiceId");
         queryList.add("listenUrl");
         LinkedHashMap map = this.getAudioBaseInfo_ByTask(taskId, queryList, request, dataSourceParam);
         String voiceUrl = "";
         String macTag = map.get("machineId") == null ? "" : String.valueOf(map.get("machineId"));
         String listenUrl = "";
         Object child_fields = map.get("child_fields");
         ArrayList child_fields_ArrayList = (ArrayList)child_fields;

         for(int i = 0; i < child_fields_ArrayList.size(); ++i) {
            String childVoiceIdStr = String.valueOf(((HashMap)child_fields_ArrayList.get(i)).get("childVoiceId"));
            if (voiceId.equals(childVoiceIdStr)) {
               voiceUrl = String.valueOf(((HashMap)child_fields_ArrayList.get(i)).get("childVoiceUri"));
               if (((HashMap)child_fields_ArrayList.get(i)).get("listenUrl") != null) {
                  listenUrl = String.valueOf(((HashMap)child_fields_ArrayList.get(i)).get("listenUrl"));
               }
               break;
            }
         }

         if (!StringUtils.isNullOrEmpry(voiceUrl) && !StringUtils.isNullOrEmpry(macTag)) {
            Map resultMap = new HashMap();
            resultMap.put("voicePath", voiceUrl);
            resultMap.put("macTag", macTag);
            resultMap.put("listenUrl", listenUrl);
            logger.info("===========getSpeechInfo_ByTask方法被调用========结束");
            return ResponseResult.success(resultMap, "查询成功!");
         } else {
            return ResponseResult.error("获取音频地址或机器标识失败!");
         }
      } catch (Exception var13) {
         logger.error("【getSpeechInfo_ByTask】方法调用错误", var13);
         return ResponseResult.error(var13.getMessage());
      }
   }

   @RequestMapping(
      value = {"getGramData"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public Object getGramData(HttpServletRequest request, String voicePath, String macTag, String dataSource, @RequestParam(value = "listenUrl", required = false) String listenUrl, @RequestParam(value = "durationMs", required = false) Long durationMs) {
      logger.info("===========getGramData方法被调用========开始");

      try {
         String voiceGramAction = request.getHeader("VoiceGram-Action");
         String voiceGramSampleRange = request.getHeader("VoiceGram-Sample-Range");
         String voiceGramChannel = request.getHeader("VoiceGram-Channel");
         String voiceGramBlockSize = request.getHeader("VoiceGram-Block-Size");
         DataQueryRequest dataQueryRequest = new DataQueryRequest();
         dataQueryRequest.setVoicePath(voicePath);
         dataQueryRequest.setMacTag(macTag);
         dataQueryRequest.setVoiceGramAction(voiceGramAction);
         dataQueryRequest.setVoiceGramSampleRange(voiceGramSampleRange);
         dataQueryRequest.setVoiceGramChannel(voiceGramChannel);
         dataQueryRequest.setVoiceGramBlockSize(voiceGramBlockSize);
         if (dataSource != null && dataSource.equals("vie-flynull")) {
            dataQueryRequest.setDataSource(BaseUtils.getDataSource(request));
         } else if (dataSource != null) {
            dataQueryRequest.setDataSource(dataSource);
         }

         if ("02".equals(voicePath) && listenUrl != null && !listenUrl.isEmpty()) {
            long safeDurationMs = durationMs == null ? 0L : Math.max(durationMs, 0L);
            dataQueryRequest.setMacTag("__NEW_RECORDING__" + safeDurationMs + "__LISTEN_URL__" + listenUrl);
         }

         Object result = this.dataQueryService.getGramData(dataQueryRequest);
         logger.info("===========getGramData方法被调用========结束");
         return result;
      } catch (Exception var11) {
         logger.error("【getGramData】方法调用错误", var11);
         return ResponseResult.error(var11.getMessage());
      }
   }

   @RequestMapping(
      value = {"play"},
      method = {RequestMethod.GET, RequestMethod.POST}
   )
   @ResponseBody
   public void play(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "voicePath",required = true) String voicePath, @RequestParam(value = "macTag",required = true) String macTag, @RequestParam(value = "listenUrl",required = false) String listenUrl) {
      logger.info("===========play方法被调用========开始");

      try {
         // 判断是新录音还是老录音
         if ("02".equals(voicePath) && listenUrl != null && !listenUrl.isEmpty()) {
            String range = request.getHeader("Range");
            URL url = new URL(listenUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            try {
               conn.setRequestMethod("GET");
               conn.setConnectTimeout(5000);
               conn.setReadTimeout(30000);
               if (!StringUtils.isNullOrEmpry(range)) {
                  conn.setRequestProperty("Range", range);
               }

               int status = conn.getResponseCode();
               String contentType = conn.getContentType();
               String contentLength = conn.getHeaderField("Content-Length");
               String contentRange = conn.getHeaderField("Content-Range");
               String acceptRanges = conn.getHeaderField("Accept-Ranges");
               response.setContentType(StringUtils.isNullOrEmpry(contentType) ? "audio/mpeg" : contentType);
               if (!StringUtils.isNullOrEmpry(contentLength)) {
                  response.setHeader("Content-Length", contentLength);
               }

               if (!StringUtils.isNullOrEmpry(contentRange)) {
                  response.setHeader("Content-Range", contentRange);
               }

               if (!StringUtils.isNullOrEmpry(acceptRanges)) {
                  response.setHeader("Accept-Ranges", acceptRanges);
               } else {
                  response.setHeader("Accept-Ranges", "bytes");
               }

               response.setStatus(status > 0 ? status : 200);
               InputStream inputStream = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
               if (inputStream == null) {
                  throw new IOException("listenUrl 未返回可读取的音频流");
               }

               try {
                  OutputStream outputStream = response.getOutputStream();
                  byte[] buffer = new byte[4096];

                  int bytesRead;
                  while((bytesRead = inputStream.read(buffer)) != -1) {
                     outputStream.write(buffer, 0, bytesRead);
                  }

                  outputStream.flush();
               } finally {
                  inputStream.close();
               }

               logger.info("新录音通过listenUrl播放成功, status={}, range={}", status, range);
            } finally {
               conn.disconnect();
            }
         } else {
            // 老录音：原有逻辑
            String range = request.getHeader("Range");
            int count = Integer.parseInt(LoadConfig.getConfigProperty("blockSize"));
            DataQueryRequest dataQueryRequest = new DataQueryRequest();
            dataQueryRequest.setVoicePath(voicePath);
            dataQueryRequest.setMacTag(macTag);
            dataQueryRequest.setRange(range);
            dataQueryRequest.setCount(count);
            PlayAudio result = this.dataQueryService.getPlayData(dataQueryRequest);
            response.setContentType("audio/wav");
            response.addHeader("Content-Length", String.valueOf(result.getPlayBytes().length));
            response.addHeader("Accept-Ranges", "bytes");
            response.addHeader("Content-Range", result.getContentRange());
            response.setStatus(206);
            response.getOutputStream().write(result.getPlayBytes());
            response.getOutputStream().flush();
            logger.info("老录音通过原有方式播放成功");
         }
      } catch (Exception var17) {
         logger.error("【play】方法调用错误", var17);
      } finally {
         try {
            response.getOutputStream().close();
         } catch (IOException var16) {
            logger.error("【play】方法关闭流错误", var16);
         }

      }

   }

   @RequestMapping(
      value = {"queryFullAudioInfo"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult queryFullAudioInfo(HttpServletRequest request, @RequestParam(value = "callId",required = true) String ID, @RequestParam(value = "dataSource",required = true) String dataSourceParam, @RequestParam(value = "token",required = false) String token) {
      ResponseResult result = null;
      if (dataSourceParam.equals("vie-flynull")) {
         int insightType = IndexConstants.getInsightType(BaseUtils.getDataSource(request));
         if (insightType == 1) {
            result = this.queryFullAudioInfo_ByTask(ID, request, dataSourceParam);
            return result;
         } else {
            result = this.queryFullAudioInfo_ByVoice(request, ID, dataSourceParam);
            return result;
         }
      } else if (StringUtils.isNullOrEmpry(token)) {
         return ResponseResult.error("免密登录需要第三方token", 1);
      } else {
         boolean flag = false;
         MultiValueMap requestBody = new LinkedMultiValueMap();
         requestBody.add("token", token);
         String url = (String)PropertyPlaceholderConfigurerUtils.getAllUserData().get("apiTokenCheckUrl");
         ResponseEntity response = null;

         try {
            Map map = new HashMap();
            map.put("token", token);
            flag = Boolean.parseBoolean(HttpClientUtilsTwo.sendGet(url, map));
         } catch (Exception var11) {
            return ResponseResult.error("请求第三方服务失败，链接不上", 1);
         }

         if (!flag) {
            return ResponseResult.error("第三方提供的token失效", 1);
         } else {
            int insightType = IndexConstants.getInsightType(dataSourceParam);
            if (insightType == 1) {
               result = this.queryFullAudioInfo_ByTask(ID, request, dataSourceParam);
               return result;
            } else {
               result = this.queryFullAudioInfo_ByVoice(request, ID, dataSourceParam);
               return result;
            }
         }
      }
   }

   public ResponseResult queryFullAudioInfo_ByVoice(HttpServletRequest request, String callId, String dataSourceParam) {
      try {
         List queryList = new ArrayList();
         queryList.add("timePosition");
         queryList.add("channelSeq");
         queryList.add("contentOrigin");
         LinkedHashMap map = this.getAudioBaseInfo_ByVoice(callId, queryList, request, dataSourceParam);
         String timePosition = String.valueOf(map.get("timePosition"));
         String channelSeq = String.valueOf(map.get("channelSeq"));
         String contentOrigin = String.valueOf(map.get("contentOrigin"));
         PlayerDataRequest playerDataRequest = new PlayerDataRequest();
         playerDataRequest.setTimePosition(timePosition);
         playerDataRequest.setChannelSeq(channelSeq);
         playerDataRequest.setContentOrigin(contentOrigin);
         List result = this.playerService.getFullTextInfoService_ByVoice(playerDataRequest);
         return result != null && result.size() != 0 ? ResponseResult.success(result, "查询成功!") : ResponseResult.error("查询不到对话文本信息");
      } catch (Exception var11) {
         logger.error("【queryFullAudioInfo】方法调用错误", var11);
         return ResponseResult.error(var11.getMessage());
      }
   }

   public ResponseResult queryFullAudioInfo_ByTask(String taskId, HttpServletRequest request, String dataSourceParam) {
      logger.info("===========queryFullAudioInfo_ByTask方法被调用========开始");

      try {
         List queryList = new ArrayList();
         queryList.add("timePosition");
         queryList.add("channelSeq");
         queryList.add("contentOrigin");
         queryList.add("childVoiceId");
         queryList.add("childTimeFormat");
         LinkedHashMap map = this.getAudioBaseInfo_ByTask(taskId, queryList, request, dataSourceParam);
         String timePosition = String.valueOf(map.get("timePosition"));
         String channelSeq = String.valueOf(map.get("channelSeq"));
         String contentOrigin = String.valueOf(map.get("contentOrigin"));
         Object child_fields = map.get("child_fields");
         ArrayList child_fields_ArrayList = (ArrayList)child_fields;
         PlayerDataRequest playerDataRequest = new PlayerDataRequest();
         playerDataRequest.setChildVoiceIdList(child_fields_ArrayList);
         playerDataRequest.setTimePosition(timePosition);
         playerDataRequest.setChannelSeq(channelSeq);
         playerDataRequest.setContentOrigin(contentOrigin);
         List result = this.playerService.getFullTextInfoService_ByTask(playerDataRequest);
         if (result != null && result.size() != 0) {
            logger.info("===========queryFullAudioInfo_ByTask方法被调用========结束");
            return ResponseResult.success(result, "查询成功!");
         } else {
            return ResponseResult.error("查询不到对话文本信息");
         }
      } catch (Exception var13) {
         logger.error("【queryFullAudioInfo】方法调用错误", var13);
         return ResponseResult.error(var13.getMessage());
      }
   }

   @RequestMapping(
      value = {"getDetailsOfTask"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public ResponseResult getDetailsOfTask(HttpServletRequest request, @RequestParam(value = "callId",required = true) String callId, @RequestParam(value = "dataSource",required = true) String dataSourceParam) {
      ResponseResult response = new ResponseResult();

      try {
         List queryList = new ArrayList();
         queryList.add("childVoiceId");
         queryList.add("childDuration");
         queryList.add("childSilenceLong");
         queryList.add("childTimeFormat");
         queryList.add("childVoiceUri");
         queryList.add("childMachineId");
         DataDetailRequest dataDetailRequest = new DataDetailRequest();
         dataDetailRequest.setTaskID(callId);
         dataDetailRequest.setColumns(queryList);
         if (dataSourceParam.equals("vie-flynull")) {
            dataDetailRequest.setDataSource(BaseUtils.getDataSource(request));
         } else {
            dataDetailRequest.setDataSource(dataSourceParam);
         }

         dataDetailRequest.setSystemId(this.commonService.getContextPath());
         dataDetailRequest.setUserId(BaseUtils.getUserId(request));
         TaskDetailsResponse taskDetailsResponse = this.playerService.getDetailsOfTaskByTaskID(dataDetailRequest);
         return taskDetailsResponse == null ? ResponseResult.error("查不到任务录音信息。") : ResponseResult.success(taskDetailsResponse, "查询成功!");
      } catch (Exception var8) {
         response.setMessage(var8.getMessage());
         logger.error("调用getDetailsOfTask方法出错");
         return ResponseResult.error(var8.getMessage());
      }
   }

   private Map<String, Object> handleKeyWordResponse(List<RuleInfo> ruleInfoList) throws Exception {
      Map result = new HashMap();
      LinkedHashMap rowsHead = new LinkedHashMap();
      List rows = new ArrayList();

      try {
         rowsHead.put("id", "序号");
         rowsHead.put("ruleName", "模型名称");
         rowsHead.put("ruleInfo", "关键词");
         if (ruleInfoList != null && ruleInfoList.size() > 0) {
            for(int i = 0; i < ruleInfoList.size(); ++i) {
               RuleInfo ruleInfo = (RuleInfo)ruleInfoList.get(i);
               LinkedHashMap data = new LinkedHashMap();
               data.put("index", i + 1);
               data.put("ruleName", ruleInfo.getRuleName());
               data.put("ruleInfo", ruleInfo.getHitKw());
               rows.add(data);
            }

            result.put("rows", rows);
            result.put("rowsHead", rowsHead);
            result.put("totalSize", ruleInfoList.size());
         }

         return result;
      } catch (Exception var8) {
         logger.error("[handleKeyWordResponse]方法处理失败", var8);
         throw new Exception("服务内部错误", var8);
      }
   }

   private void groupResult(Map<String, Object> resultMap, InitialiseWaveFormat initialiseWaveFormat) {
      if (initialiseWaveFormat != null) {
         resultMap.put("bitspersample", initialiseWaveFormat.getBitsPerSample());
         resultMap.put("channels", initialiseWaveFormat.getChannels());
         resultMap.put("samplerate", initialiseWaveFormat.getSampleRate());
         resultMap.put("audioformat", initialiseWaveFormat.getFormatEncoding());
         resultMap.put("blockalign", initialiseWaveFormat.getBlockAlign());
         resultMap.put("sampleCount", initialiseWaveFormat.getSampleCount());
      }

   }

   private LinkedHashMap<String, Object> getAudioBaseInfo_ByVoice(String voiceId, List<String> queryList, HttpServletRequest request, String dataSourceParam) throws VieAppServiceException {
      LinkedHashMap result = null;
      DataFilter dataFilter = new DataFilter();
      dataFilter.setVoiceId(voiceId);
      dataFilter.setQueryList(queryList);
      if (dataSourceParam.equals("vie-flynull")) {
         dataFilter.setDataSource(BaseUtils.getDataSource(request));
      } else {
         dataFilter.setDataSource(dataSourceParam);
      }

      try {
         result = this.dataQueryService.getAudioBaseInfo_ByVoice(dataFilter);
         return result;
      } catch (VieAppServiceException var8) {
         logger.error("【getAudioBaseInfo】方法调用错误", var8);
         throw new VieAppServiceException(var8.getMessage());
      }
   }

   private LinkedHashMap<String, Object> getAudioBaseInfo_ByTask(String taskId, List<String> queryList, HttpServletRequest request, String dataSourceParam) throws VieAppServiceException {
      LinkedHashMap result = null;
      DataFilter dataFilter = new DataFilter();
      dataFilter.setTaskId(taskId);
      dataFilter.setQueryList(queryList);
      if (dataSourceParam.equals("vie-flynull")) {
         dataFilter.setDataSource(BaseUtils.getDataSource(request));
      } else {
         dataFilter.setDataSource(dataSourceParam);
      }

      try {
         result = this.dataQueryService.getAudioBaseInfo_ByTask(dataFilter);
         return result;
      } catch (VieAppServiceException var8) {
         logger.error("【getAudioBaseInfo】方法调用错误", var8);
         throw new VieAppServiceException(var8.getMessage());
      }
   }
}
