package com.tidc.utils;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tidc.entity.BaseEntity;
import com.tidc.handler.emun.ReturnCode;
import com.tidc.handler.exception.ServiceException;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 加密工具类
 */
public class TidcEncryptUtil {

    /**
     * 格式化为符合加密的字符串
     *
     * @param obj 传输对象
     * @return 格式化字符串
     */
    public static <T extends BaseEntity> String formatStr(T obj) {
        JSONObject json = JSONUtil.parseObj(obj);
        Console.log(obj);
        if (StrUtil.isEmpty(obj.getEncryption()) || StrUtil.isEmpty(obj.getTimestamp())) {
            throw new ServiceException(ReturnCode.FIELD_NAME_ERROR_OR_VALUE_IS_NULL.getCode(),
                    ReturnCode.FIELD_NAME_ERROR_OR_VALUE_IS_NULL.getMessage());
        }
        StringBuilder key = new StringBuilder();
        for (String fieldName : json.keySet()) {
            if ("encryption".equals(fieldName)) continue;
            key.append(fieldName).append("=").append(json.get(fieldName).toString()).append("&");
        }
        key.deleteCharAt(key.length() - 1);
        return key.toString();
    }

    /**
     * 根据传入字符串来创建自定义MD5加密字段,并与传入的密钥进行比较
     *
     * @param str        符合格式的字符串
     * @param encryption 密钥(同种加密方式的加密字段)
     * @return 比较结果是否相等
     */
    public static boolean makeAndCheckEncryptionIsEquals(String str, String encryption) {
        String md5 = md5(str);
        return md5.equals(encryption);
    }

    /**
     * MD5加密,需要字符串格式为: x=???&y=???
     *
     * @param s 需要加密的字符串
     * @return 加密字符串
     */
    public static String md5(String s) {
        s = sSort(s);
        s = DigestUtils.md5DigestAsHex(s.getBytes());
        s = s.toUpperCase();
        List<String> d = new ArrayList<>();
        d.add(String.valueOf(s.charAt(0)));
        for (int i = 1; i < s.length(); i++) {
            int u = (d.get(i - 1).charAt(0) + s.charAt(i)) % 26 + 65;
            d.add(String.valueOf((char) u));
        }
        StringBuilder result = new StringBuilder();
        for (String str : d) {
            result.append(str);
        }
        return result.toString();
    }

    private static String sSort(String s) {
        String[] y = s.split("&");
        Arrays.sort(y, (a, b) -> {
            String a1 = a.substring(0, a.indexOf("="));
            String b1 = b.substring(0, b.indexOf("="));
            for (int i = 0; i < Math.max(a1.length(), b1.length()); i++) {
                if (i >= a1.length()) {
                    return -1;
                } else if (i >= b1.length()) {
                    return 1;
                }
                char q = a1.charAt(i), w = b1.charAt(i);
                if (q < w) {
                    return -1;
                } else if (q > w) {
                    return 1;
                }
            }
            return 0;
        });
        return String.join("", y) + "tidcNice";
    }

}
