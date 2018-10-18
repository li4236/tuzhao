package com.tuzhao.activity.navi;

import java.util.List;

/**
 * 用于解析科大讯飞语音听写服务器返回的Json数据
 *
 * 语音识别结果Json数据格式：
 * {"sn":1,"ls":true,"bg":0,"ed":0,"ws":[{"bg":0,"cw":[{"w":"今天","sc":0}]},
 * {"bg":0,"cw":[{"w":"的","sc":0}]},{"bg":0,"cw":[{"w":"天气","sc":0}]},
 * {"bg":0,"cw":[{"w":"怎么样","sc":0}]},{"bg":0,"cw":[{"w":"。","sc":0}]}]}
 */
public class DictationJsonParseUtil {

    // 解析服务器返回的语音听写结果Json格式数据的静态方法，返回值为语音的字符串
    public static String parseJsonData(String jsonDataStr) {
        String speechStr = "";
        List<DictationResult> resultList = GsonUtil.parseJsonArrayWithGson(
                jsonDataStr, DictationResult.class);

        for (int i = 0; i < resultList.size() - 1; i++) { // 这里减1是因为最后有一组作为结尾的标点符号数据，要舍去
            speechStr += resultList.get(i).toString();
        }

        return speechStr;
    }
}

// 语音听写结果类
class DictationResult {
    private String sn;
    private String ls;
    private String bg;
    private String ed;

    private List<Words> ws;

    public static class Words {
        private String bg;
        private List<Cw> cw;

        public static class Cw {
            private String w;
            private String sc;

            @Override
            public String toString() {
                return w;
            }
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (Cw cwTmp : cw) {
                result.append(cwTmp.toString());
            }
            return result.toString();
        }
    }

    public List<Words> getWs() {
        return ws;
    }

    public void setWs(List<Words> ws) {
        this.ws = ws;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Words wsTmp : ws) {
            result.append(wsTmp.toString());
        }
        return result.toString();
    }
}
