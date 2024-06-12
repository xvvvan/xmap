package com.xvvvan.xhole.ui.util;

import com.alibaba.excel.util.StringUtils;
import com.xvvvan.xhole.Types;
import com.xvvvan.xhole.expression.Marker;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Replacer {
//    replacer 替换函数
    List<String> oldnew;
    public Replacer(Map<String,String> values){
        if(values.values().size()%2 ==1){
            return;
        }
        List<String> replacerItems = new ArrayList<>();
        for (String k : values.keySet()) {
            String v = values.get(k);
            replacerItems.add(String.format("{{%s}}",k));
            replacerItems.add(String.format("%s",v));
        }
        oldnew = replacerItems;
    }

    public static String replace(String template, Map<String, Object> values) {
        Map<String, Object> valuesMap = new HashMap<String, Object>(values.size());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            valuesMap.put(entry.getKey(), Types.toString(entry.getValue()));
        }

        String replaced = executeStringStd(template, Marker.ParenthesisOpen, Marker.ParenthesisClose, valuesMap);
        String finalValue = executeStringStd(replaced, Marker.General, Marker.General, valuesMap);
        return finalValue;
    }

    public String replace(String s){

        for (int i = 0; i < oldnew.size(); i+=2) {
            s = s.replaceAll(escapeExprSpecialWord(oldnew.get(i)),oldnew.get(i+1));
        }
        return s;
    }

    public Integer writeString(OutputStreamWriter w,String s){
        return 0;
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
//    private static final ByteBufferPool byteBufferPool = new ByteBufferPool();

    public static String executeStringStd(String template, String startTag, String endTag, Map<String, Object> m) {
        return executeFuncString(template, startTag, endTag, (w, tag) ->

                keepUnknownTagFunc(w, startTag, endTag, tag, m)
        );
    }

    public static String executeFuncString(String template, String startTag, String endTag, TagFunc f) {
        String s;
        s = executeFuncStringWithErr(template, startTag, endTag, f);
        return s;
    }

    public static String executeFuncStringWithErr(String template, String startTag, String endTag, TagFunc f){
        byte[] byteTemplate = template.getBytes();
        int n = indexOf(byteTemplate, startTag.getBytes(), 0);
        if (n < 0) {
            return template;
        }

        ByteBuffer bb = ByteBuffer.allocate(2048);
        int len;
        try {

            len = executeFunc(byteTemplate, startTag.getBytes(), endTag.getBytes(), bb, f);
        } catch (IOException e) {
            return "";
        }
        String s = new String(bb.array(), bb.arrayOffset(), len);
        return s;
    }

    public static int executeFunc(byte[] s, byte[] a, byte[] b, ByteBuffer w, TagFunc f) throws IOException {
        int nn = 0;
        int ni;
        for (;;) {
            int n = indexOf(s, a, 0);
            if (n < 0) {
                break;
            }
            ni = write(w, s, 0, n);
            nn += ni;

            s = copyOfRange(s, n + a.length, s.length);
            n = indexOf(s, b, 0);
            if (n < 0) {
                ni = write(w, a, 0, a.length);
                nn += ni;
                break;
            }
            //获取被包裹的标签
            String tag = new String(copyOfRange(s, 0, n));
            ni = f.tagFunc(w, tag);
            nn += ni;
            String s2 = new String(s);
            s = copyOfRange(s, n + b.length, s.length);
            String s1 = new String(s);

        }
        String s2 = new String(w.array());
        ni = write(w, s, 0, s.length);
        String s1 = new String(w.array());
        nn += ni;

        return nn;
    }

    public static int write(ByteBuffer w, byte[] buf, int off, int len) throws IOException {
        w.put(buf, off, len);
        return len;
    }
//    这段代码的作用是将original数组中从from索引开始的newLength个元素（或源数组剩余元素个数，如果剩余元素个数小于newLength）复制到目标数组copy中。复制到copy数组时，从目标数组的起始位置（索引为0）开始放置复制的元素。
    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0) throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength));
        return copy;
    }

    public static int indexOf(byte[] source, byte[] target, int fromIndex) {
        if (target.length == 0) {
            return 0;
        }
        int max = source.length - target.length;
        for (int i = fromIndex; i <= max; i++) {
            int j = i;
            int k = 0;
            while (k < target.length && source[j] == target[k]) {
                j++;
                k++;
            }
            if (k == target.length) {
                return i;
            }
        }
        return -1;
    }

    public static int len(byte[] bytes) {
        return bytes.length;
    }

    public interface TagFunc {
        int tagFunc(ByteBuffer w, String tag) throws IOException;
    }
//    替换其中{{host}}类似的
    public static int keepUnknownTagFunc(ByteBuffer w, String startTag, String endTag, String tag, Map<String, Object> m) throws IOException {
        int ni;
        byte[] startTagBytes = startTag.getBytes();
        byte[] tagBytes = tag.getBytes();
        byte[] endTagBytes = endTag.getBytes();

        int i = tag.lastIndexOf(startTag);
        if (i >= 0) {
            ni = write(w, startTagBytes, 0, startTagBytes.length);
            if (ni < 0) {
                return 0;
            }
            ni = write(w, tagBytes, 0, i);
            if (ni < 0) {
                return 0;
            }
            tag = tag.substring(i + startTag.length());
            tagBytes = tag.getBytes();
        }

        Object v = m.get(tag);
        if (!m.containsKey(tag)) {
            ni = write(w, startTagBytes, 0, startTagBytes.length);
            ni = write(w, tagBytes, 0, tagBytes.length);
            ni = write(w, endTagBytes, 0, endTagBytes.length);
            return startTagBytes.length + tagBytes.length + endTagBytes.length;
        }

        if (v == null) {
            return 0;
        }
//        写入map中获取的值
        if (v instanceof byte[]) {
            return write(w, (byte[]) v, 0, ((byte[]) v).length);
        } else if (v instanceof String) {
            return write(w, ((String) v).getBytes(), 0, ((String) v).length());
        } else if (v instanceof TagFunc) {
            return ((TagFunc) v).tagFunc(w, String.valueOf(tagBytes));
        }
        throw new RuntimeException(String.format("tag=%s contains unexpected value type=%s. Expected byte[], string or TagFunc",
                tag, v.getClass().getName()));
    }
    public static String replaceOne(String template, String key, Object value) {
        String data = replaceOneWithMarkers(template, key, value, Marker.ParenthesisOpen, Marker.ParenthesisClose);
        return replaceOneWithMarkers(data, key, value, Marker.General, Marker.General);
    }

    private static String replaceOneWithMarkers(String template, String key, Object value, String openMarker, String closeMarker) {
        return template.replace(openMarker + key + closeMarker, Types.toString(value));
    }
}
