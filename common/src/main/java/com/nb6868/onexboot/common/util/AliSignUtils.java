package com.nb6868.onexboot.common.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * 阿里签名工具类
 * 支持: 阿里云、钉钉
 *
 * @author zhangchaoxu@gmail.com
 */
@Slf4j
public class AliSignUtils {

    /**
     * 特殊urlEncode
     */
    @SneakyThrows
    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("~", "%7E")
                .replace("/", "%2F");
    }

    /**
     * 加密
     *
     * @param data 明文
     * @param key 密钥
     * @param algorithm 算法 如:HmacSHA1/HmacSHA256
     * @return 密文
     */
    public static String signature(String data, String key, String algorithm) {
        try {
            // 加密
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance(algorithm);
            mac.init(new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8.name()), algorithm));
            byte[] signData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8.name()));
            // base64
            String base64 = java.util.Base64.getEncoder().encodeToString(signData);
            // 最后urlEncode
            return urlEncode(base64);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            e.printStackTrace();
            log.error("ParamParseUtils", e);
            return null;
        }
    }

    /**
     * 参数转为字符串
     * @param params 参数
     * @return 拼接后的内容
     */
    public static String paramToQueryString(Map<String, String> params) {
        // 参数KEY排序
        TreeMap<String, String> sortParas = new TreeMap<>(params);
        // 构造待签名的字符串
        StringJoiner stringJoiner = new StringJoiner("&");
        for (String key : sortParas.keySet()) {
            stringJoiner.add(urlEncode(key) + "=" + urlEncode(params.get(key)));
        }
        return stringJoiner.toString();
    }

}
