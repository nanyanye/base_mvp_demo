package cn.test.demo.base.mvp;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by dxx on 2017/11/20.
 */

public class JsonUtil {
    public static <T> T Str2JsonBean(String json, Class<T> clazz) {
        T object = null;
        try {
            object = JSON.parseObject(json, clazz);
        } catch (JSONException e) {
            Log.i("JsonUtil", "Str2JsonBean JSONException:" + e.getMessage());
        }
        return object;
    }


    public static String JsonBean2Str(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * [text={"error":"无权对此电桩充电"}]
     *
     * 需要去除 [text=  以及最后的一个 ]
     *  最终得到  {"error":"无权对此电桩充电"}
     *
     * @param errorText
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T errorServer(String errorText, Class<T> clazz) {
        if(errorText.startsWith("[text=")){
            errorText = errorText.replace("[text=", "");
        }

        if(errorText.endsWith("]")){
            errorText = errorText.replace("]", "");
        }
        return JsonUtil.Str2JsonBean(errorText, clazz);
    }


    public static JSONObject strJSONObject(String jsonStr) {
        return JSON.parseObject(jsonStr);
    }
}
