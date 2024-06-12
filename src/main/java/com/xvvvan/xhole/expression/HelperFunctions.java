package com.xvvvan.xhole.expression;


import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.*;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperFunctions {
    Map<String,AbstractFunction> functionMap = new HashMap<>();

    public Map<String, AbstractFunction> getFunctionMap() {
        return functionMap;
    }

    public HelperFunctions(){
//        "add",
        AviatorEvaluator.addFunction(new AddFunction());
//        "len",
        AviatorEvaluator.addFunction(new Len());
//        "toupper",
        AviatorEvaluator.addFunction(new ToUpper());
//        "tolower",
        AviatorEvaluator.addFunction(new ToLower());
//        "replace",
        AviatorEvaluator.addFunction(new Replace());
//        "trim",
//        AviatorEvaluator.addFunction(new Trim());
////        "trimleft",
//        AviatorEvaluator.addFunction(new TrimLeft());
////        "trimright",
//        AviatorEvaluator.addFunction(new TrimRight());
////        "trimspace",
//        AviatorEvaluator.addFunction(new TrimSpace());
////        "trimprefix",
//        AviatorEvaluator.addFunction(new TrimPrefix());
////        "trimsuffix",
//        AviatorEvaluator.addFunction(new TrimSuffix());
//        "base64",
        AviatorEvaluator.addFunction(new MyBase64());
//        "base64_decode",
        AviatorEvaluator.addFunction(new MyBase64Decode());
//        "md5",
        AviatorEvaluator.addFunction(new Md5());
//        "sha265",
        AviatorEvaluator.addFunction(new Sha256());
//        "contains",
        AviatorEvaluator.addFunction(new Contains());
//        "regex",
        AviatorEvaluator.addFunction(new Regex());
//        http
        AviatorEvaluator.addFunction(new AddHttp());
        AviatorEvaluator.addFunction(new AddHttps());
        AviatorEvaluator.addFunction(new AddDomain());
        AviatorEvaluator.addFunction(new AddCert());
        AviatorEvaluator.addFunction(new ToSet());
        AviatorEvaluator.addFunction(new GetDomain());
        AviatorEvaluator.addFunction(new GetIp());
        AviatorEvaluator.addFunction(new AddToC());
        AviatorEvaluator.addFunction(new AddIp());


    }
    class AddFunction extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env,
                                  AviatorObject arg1, AviatorObject arg2) {
            Number left = FunctionUtils.getNumberValue(arg1, env);
            Number right = FunctionUtils.getNumberValue(arg2, env);
            return new AviatorDouble(left.doubleValue() + right.doubleValue());
        }
        public String getName() {
            return "add";
        }
    }
    class Len extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue = FunctionUtils.getStringValue(arg1, env);
            return new AviatorBigInt(stringValue.length());
        }

        public String getName() {
            return "len";
        }
    }
    class ToUpper extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue = FunctionUtils.getStringValue(arg1, env);

            return new AviatorString(stringValue.toUpperCase());
        }

        public String getName() {
            return "toupper";
        }
    }
    class ToLower extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue = FunctionUtils.getStringValue(arg1, env);
            return new AviatorString(stringValue.toLowerCase());
        }
        public String getName() {
            return "tolower";
        }
    }
    class Replace extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String stringValue2 = FunctionUtils.getStringValue(arg2, env);
            String stringValue3 = FunctionUtils.getStringValue(arg3, env);
            return new AviatorString(stringValue1.replaceAll(stringValue2, stringValue3));
        }
        public String getName() {
            return "replace";
        }
    }
//    class Trim extends AbstractFunction {
//        @Override
//        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
//            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
//            return new AviatorString(StringUtils.trim(stringValue1));
//        }
//        public String getName() {
//            return "trim";
//        }
//    }
//    class TrimLeft extends AbstractFunction {
//        @Override
//        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
//            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
//            String stringValue2 = FunctionUtils.getStringValue(arg2, env);
//            return new AviatorString(StringUtils.substringBeforeLast(stringValue1,stringValue2));
//        }
//        public String getName() {
//            return "trimleft";
//        }
//    }
//    class TrimRight extends AbstractFunction {
//        @Override
//        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
//            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
//            String stringValue2 = FunctionUtils.getStringValue(arg2, env);
//            return new AviatorString(StringUtils.substringAfterLast(stringValue1,stringValue2));
//        }
//        public String getName() {
//            return "trimright";
//        }
//    }
//    class TrimSpace extends AbstractFunction {
//        @Override
//        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
//            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
//            return new AviatorString(StringUtils.trim(stringValue1));
//        }
//        public String getName() {
//            return "trimspace";
//        }
//    }
//    class TrimPrefix extends AbstractFunction {
//        @Override
//        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
//            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
//            String stringValue2 = FunctionUtils.getStringValue(arg2, env);
//            return new AviatorString(StringUtils.substringAfter(stringValue1,stringValue2));
//        }
//        public String getName() {
//            return "trimprefix";
//        }
//    }
//    class TrimSuffix extends AbstractFunction {
//        @Override
//        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
//            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
//            String stringValue2 = FunctionUtils.getStringValue(arg2, env);
//            return new AviatorString(StringUtils.substringBefore(stringValue1,stringValue2));
//        }
//        public String getName() {
//            return "trimsuffix";
//        }
//    }
    class MyBase64 extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            return new AviatorString(Base64.getEncoder().encodeToString(stringValue1.getBytes(StandardCharsets.UTF_8)));
        }
        public String getName() {
            return "base64";
        }
    }
    class MyBase64Decode extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String s = null;
            try {
                s = new String(Base64.getDecoder().decode(stringValue1), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return new AviatorString(s);
        }
        public String getName() {
            return "base64_decode";
        }
    }
    class Md5 extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            byte[] md5s = null;
            try {
                md5s = MessageDigest.getInstance("md5").digest(stringValue1.getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            String md5code = new BigInteger(1, md5s).toString(16);// 16进制数字
            for (int i = 0; i < 32 - md5code.length(); i++) {
                md5code = "0"+md5code;
            }
            return new AviatorString(md5code);
        }
        public String getName() {
            return "md5";
        }
    }
    class Sha256 extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            MessageDigest messageDigest;
            String encdeStr = "";
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(stringValue1.getBytes(StandardCharsets.UTF_8));
                encdeStr = byte2Hex(messageDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return new AviatorString(encdeStr);
        }
        public String getName() {
            return "sha256";
        }
        /**
         * 将byte转为16进制
         *
         * @param bytes
         * @return
         */
        private String byte2Hex(byte[] bytes) {
            StringBuffer stringBuffer = new StringBuffer();
            String temp = null;
            for (int i = 0; i < bytes.length; i++) {
                temp = Integer.toHexString(bytes[i] & 0xFF);
                if (temp.length() == 1) {
                    // 1得到一位的进行补0操作
                    stringBuffer.append("0");
                }
                stringBuffer.append(temp);
            }
            return stringBuffer.toString();
        }

    }
    class Contains extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1,AviatorObject arg2) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String stringValue2 = FunctionUtils.getStringValue(arg2, env);
            boolean contains =stringValue1.contains(stringValue2);
            AviatorBoolean aviatorBoolean = AviatorBoolean.valueOf(contains);
            return aviatorBoolean;
        }
        public String getName() {
            return "contains";
        }
    }

    class Regex extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1,AviatorObject arg2) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String stringValue2 = FunctionUtils.getStringValue(arg2, env);
            Pattern compile = Pattern.compile(stringValue1);
            Matcher matcher = compile.matcher(stringValue2);
            AviatorBoolean aviatorBoolean = AviatorBoolean.valueOf(matcher.find());
            return aviatorBoolean;
        }
        public String getName() {
            return "regex";
        }
    }
    class AddHttp extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String[] split = stringValue1.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append("http://"+s+"\n");
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "http";
        }
    }
    class AddHttps extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String[] split = stringValue1.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append("https://"+s+"\n");
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "https";
        }
    }
    class AddDomain extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String[] split = stringValue1.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append("domain="+s+"\n");
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "addd";
        }
    }
    class AddCert extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String[] split = stringValue1.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append("cert="+s+"\n");
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "addc";
        }
    }
    class AddToC extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String[] split = stringValue1.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append(s+"/24"+"\n");
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "toc";
        }
    }
    class ToSet extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String[] split = stringValue1.split("\n");
            StringBuilder sb = new StringBuilder();
            HashSet<String> set = new HashSet<>();
            set.addAll(Arrays.asList(split));
            for (String s : set) {
                sb.append(s+"\n");
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "set";
        }
    }
    class GetDomain extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String[] split = stringValue1.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                try {
                    URL url = new URL(s);
                    String host = url.getHost();
                    host = host.startsWith("www.") ? host.substring(4) : host;
                    sb.append(host+"\n");
                }catch (Exception e){
                    System.out.println(e);
                }
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "getdomain";
        }
    }
    class GetIp extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String regex = "[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}";
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            Pattern compile = Pattern.compile(regex);
            Matcher matcher = compile.matcher(stringValue1);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                sb.append(matcher.group(0)+"\n");
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "getip";
        }
    }
    class AddIp extends AbstractFunction {

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
            String stringValue1 = FunctionUtils.getStringValue(arg1, env);
            String[] split = stringValue1.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append("ip="+s+"\n");
            }
            String trim = sb.toString().trim();
            return new AviatorString(trim);
        }
        public String getName() {
            return "addip";
        }
    }
}
