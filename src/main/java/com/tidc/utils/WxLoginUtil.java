package com.tidc.utils;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.tidc.handler.exception.ServiceException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信登录工具类
 *
 * @author 宋佳豪
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "wx")
public class WxLoginUtil {

    private String appid;
    private String secret;
    private String wxUrl;
    private static final String GRANT_TYPE = "authorization_code";

    /**
     * 根据传入的code请求到用户的 session_key 和 openId <br>
     * 通过 wx.login 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程 <br>
     * 请求地址 <br>
     * GET <a href="https://api.weixin.qq.com/sns/jscode2session">https://api.weixin.qq.com/sns/jscode2session</a>? <br>
     * appid=APPID& <br>
     * secret=SECRET& <br>
     * js_code=CODE& <br>
     * grant_type=authorization_code
     *
     * @param code 临时登录凭证
     * @return 包含 session_key 和 openId 的字符串
     */
    public String getLoginRes(String code) {
        String url = wxUrl + "?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=" + GRANT_TYPE;
        return HttpRequest.get(url).timeout(3000).execute().body();
    }

    /**
     * 传入code获取openid
     *
     * @param code 临时登录凭证
     * @return openid
     */
    public String getOpenId(String code) {
        String res = getLoginRes(code);
        Console.log(res);
        String openid = null;
        if (res != null && JSONUtil.parseObj(res).containsKey("openid")) {
            openid = JSONUtil.parseObj(res).get("openid").toString();
        }
        if (ObjectUtil.isEmpty(openid)) {
            throw new ServiceException(40029, "code无效,可能是前后端Appid、secret不一致");
        }
        return openid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getWxUrl() {
        return wxUrl;
    }

    public void setWxUrl(String wxUrl) {
        this.wxUrl = wxUrl;
    }

}
