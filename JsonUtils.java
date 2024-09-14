package com.machloop.vfpc.common.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {

  public static final String JSON_OBJEC = "JSONObject";
  public static final String JSON_ARRAY = "JSONArray";
  public static final String JSON_String = "String";
  public static final String JSON_Integer = "Integer";
  public static final String JSON_Long = "Long";
  public static final String JSON_Float = "Float";

  /**
   *  从任意结构json对象获取key对应的值 不能重名
   * @param param 已经JSONObject.parseObject的对象
   * @param targetKey 需要取值的key
   * @return 目标key value 如果不存在 返回值为空MAP
   */
  public static Map<String, Object> jsonGetWithType(Object param, String targetKey) {
    Map<String, Object> result = new HashMap<>();
    if (param instanceof JSONObject) {
      JSONObject jsonObject = (JSONObject) param;
      if (jsonObject.containsKey(targetKey)) {
        Object o = jsonObject.get(targetKey);
        result.put(o.getClass().getSimpleName(), o);
        return result;
      } else {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
          Map<String, Object> map = jsonGetWithType(entry.getValue(), targetKey);
          if (!map.isEmpty()) {
            result.putAll(map);
            return result;
          }
        }
      }

    } else if (param instanceof JSONArray) {
      JSONArray jsonArray = (JSONArray) param;
      for (int i = 0; i < jsonArray.size(); i++) {
        Object a = jsonArray.get(i);
        if (a instanceof JSONObject) {
          JSONObject jsonObject = (JSONObject) a;
          if (jsonObject.containsKey(targetKey)) {
            Object o = jsonObject.get(targetKey);
            result.put(o.getClass().getSimpleName(), o);
            return result;
          } else {
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
              Map<String, Object> map = jsonGetWithType(entry.getValue(), targetKey);
              if (!map.isEmpty()) {
                result.putAll(map);
                return result;
              }
            }
          }

        }
      }
    }

    return result;
  }

  /**
   *  带默认值返回
   * @param param
   * @param targetKey
   * @param defaultValue
   * @return
   */
  public static Object jsonGetOrDefault(Object param, String targetKey, Object defaultValue) {
    Object result = jsonGet(param, targetKey);
    return result == null ? defaultValue : result;
  }

  /**
   *  重载方法 直接返回结果(不带类型)
   * @param param
   * @param targetKey
   * @return 不存在返回null
   */
  public static Object jsonGet(Object param, String targetKey) {
    Map<String, Object> queryResult = jsonGetWithType(param, targetKey);
    return queryResult.isEmpty() ? null : new ArrayList<>(queryResult.values()).get(0);
  }

  /**
   *
   * @param param
   * @param targetKey key3如果重名 应该传值key1.key2.key3
   * @return
   */
  public static String jsonGetString(Object param, String targetKey) {
    if (targetKey.contains(".")) {
      return (String) jsonGetByUrl(param, targetKey);
    }
    return (String) jsonGet(param, targetKey);
  }

  public static Integer jsonGetInteger(Object param, String targetKey) {
    Number number = initNumber();
    if (targetKey.contains(".")) {
      number = (Number) jsonGetByUrl(param, targetKey);
    } else {
      number = (Number) jsonGet(param, targetKey);
    }
    return number != null ? number.intValue() : initNumber().intValue();
  }

  // 增加判空处理 和 数字初始化
  public static Long jsonGetLong(Object param, String targetKey) {
    Number number = initNumber();
    if (targetKey.contains(".")) {
      number = (Number) jsonGetByUrl(param, targetKey);
    } else {
      number = (Number) jsonGet(param, targetKey);
    }
    return number != null ? number.longValue() : initNumber().longValue();
  }

  public static Float jsonGetFloat(Object param, String targetKey) {
    Number number = initNumber();
    if (targetKey.contains(".")) {
      number = (Number) jsonGetByUrl(param, targetKey);
    } else {
      number = (Number) jsonGet(param, targetKey);
    }
    return number != null ? number.floatValue() : initNumber().floatValue();
  }

  public static Date jsonGetDate(Object param, String targetKey, String dateFormat) {
    return new Date(System.currentTimeMillis());
    // TODO 之后实现
  }

  public static JSONObject jsonGetObject(Object param, String targetKey) {
    JSONObject result = null;
    if (targetKey.contains(".")) {
      result = (JSONObject) jsonGetByUrl(param, targetKey);
    } else {
      result = (JSONObject) jsonGet(param, targetKey);
    }

    return result == null ? new JSONObject() : result;
  }

  /**
   *  不存在 返回空数组 可以for循环
   * @param param
   * @param targetKey
   * @return
   */
  public static JSONArray jsonGetArray(Object param, String targetKey) {
    JSONArray result = null;
    if (targetKey.contains(".")) {
      result = (JSONArray) jsonGetByUrl(param, targetKey);
    } else {
      result = (JSONArray) jsonGet(param, targetKey);
    }
    return result == null ? new JSONArray() : result;
  }

  /**
   * 返回精准取值结果
   * @param param JSONObject对象参数
   * @param targetKeyUrl a.b.c abc中除最后一位 不允许出现JSONArray
   * @return 不存在返回为null
   */
  public static Object jsonGetByUrl(Object param, String targetKeyUrl) {
    Object result = param;
    String[] urlSplit = targetKeyUrl.split("\\.");
    for (String key : urlSplit) {
      result = ((JSONObject) result).get(key);
      if (result == null) {
        return null;
      }
    }
    return result;
  }

  /**
   * 设置json对象中key对应的值 支持直接的类型覆盖 不能重名
   * @param param 已经JSONObject.parseObject的对象
   * @param targetKey 需要赋值的key
   * @param targetValue 最终值
   * @return true 修改成功 false 修改失败
   */
  public static boolean jsonSet(Object param, String targetKey, Object targetValue) {
    boolean result = false;
    if (param instanceof JSONObject) {
      JSONObject jsonObject = (JSONObject) param;
      if (jsonObject.containsKey(targetKey)) {
        jsonObject.put(targetKey, targetValue);
        return true;
      } else {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
          boolean setResult = jsonSet(entry.getValue(), targetKey, targetValue);
          if (setResult) {
            return true;
          }
        }
      }

    } else if (param instanceof JSONArray) {
      JSONArray jsonArray = (JSONArray) param;
      for (int i = 0; i < jsonArray.size(); i++) {
        Object a = jsonArray.get(i);
        if (a instanceof JSONObject) {
          JSONObject jsonObject = (JSONObject) a;
          if (jsonObject.containsKey(targetKey)) {
            jsonObject.put(targetKey, targetValue);
            return true;
          } else {
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
              boolean setResult = jsonSet(entry.getValue(), targetKey, targetValue);
              if (setResult) {
                return true;
              }
            }
          }
        }
      }
    }

    return result;
  }

  /**
   *  根据url准确赋值 如果没有key会创建
   * @param param JSONObject参数对象
   * @param url 路径 policies.monitor.[0]#host-metric.cpu-util
   * @param value 目标值
   * @return
   */
  public static boolean jsonPrecisionSetByUrl(Object param, String url, Object value) {
    int i = url.indexOf(".");
    int i1 = url.indexOf("#");
    if (i == -1 && i1 == -1) {
      // end floor
      String regex = "\\[(\\d)+\\]";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(url);
      if (matcher.find()) {
        JSONArray jsonArray = (JSONArray) param;
        String group = matcher.group(1);
        jsonArray.set(Integer.parseInt(group), value);
      } else {
        JSONObject jsonObject = (JSONObject) param;
        jsonObject.put(url, value);
      }
      return true;
    }

    int splitIndex = 0;
    if (i1 < i && i1 != -1) {
      splitIndex = i1;
    } else {
      splitIndex = i;
    }

    String key = url.substring(0, splitIndex);

    String regex = "\\[(\\d)+\\]";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(key);
    if (matcher.find()) {
      JSONArray jsonArray = (JSONArray) param;
      String group = matcher.group(1);
      Object nextParam = jsonArray.get(Integer.parseInt(group));
      String nextUrl = url.substring(splitIndex + 1, url.length());
      return jsonPrecisionSetByUrl(nextParam, nextUrl, value);

    } else {
      JSONObject jsonObject = (JSONObject) param;
      Object nextParam = jsonObject.get(key);
      String nextUrl = url.substring(splitIndex + 1, url.length());
      return jsonPrecisionSetByUrl(nextParam, nextUrl, value);
    }
  }

  /**
   *   包装jsonSet函数 处理null为该类型默认值 方便引擎端处理
   */
  public static boolean jsonSetNull2Default(Object param, String targetKey, Object targetValue,
      String type) {
    if (targetValue == null) {
      switch (type) {
        case JSON_Integer ->
        {
          return jsonSet(param, targetKey, 0);
        }
        case JSON_Long ->
        {
          return jsonSet(param, targetKey, 0l);
        }
        case JSON_String ->
        {
          return jsonSet(param, targetKey, "");
        }
        case JSON_Float ->
        {
          return jsonSet(param, targetKey, 0.0f);
        }
      }
    }
    return jsonSet(param, targetKey, targetValue);
  }

  /**
   *  根据list生成对应结构json对象
   * @param paramList
   * "collect_type_specimen.circleCollect.endTime"
   * @param paramTypeList
   * 从String jsonObject jsonArray 中取值
   * @return
   * {"collect_type_specimen":{"circleCollect":{"endTime":{}}}
   */
  public static JSONObject init(List<String> paramList, List<String> paramTypeList) {
    JSONObject result = new JSONObject();

    for (int i = 0; i < paramList.size(); i++) {
      String keyLocationString = paramList.get(i);
      List<String> keyLocationList = Arrays.stream(keyLocationString.split("\\.")).toList();

      String initType = paramTypeList.get(i);
      jsonNodeAdd(result, keyLocationList, initType, false, 0);
    }

    return result;
  }

  /**
   * 根据路径添加所需类型到对应路径 不支持添加节点在array中
   * @param root json串根节点
   * @param keyLocationList 路径 如 key1.key2.key3
   * @param initType 如 JSON_OBJEC | JSON_ARRAY
   * @param isNew 初始值为false 递归标识是否为新建节点
   * @param layers 递归层数
   */
  public static void jsonNodeAdd(JSONObject root, List<String> keyLocationList, String initType,
      boolean isNew, int layers) {
    if (root == null || keyLocationList == null || keyLocationList.size() == layers) {
      return;
    }
    String key = keyLocationList.get(layers);
    if (isNew == false && (!root.containsKey(key))) {
      isNew = true;
    }

    if (isNew) {
      JSONObject newNode = new JSONObject();
      root.put(key, newNode);
      layers++;
      if (keyLocationList.size() == layers) {
        Object finalInitType = switch (initType) {
          case JSON_OBJEC -> new JSONObject();
          case JSON_ARRAY -> new JSONArray();
          default -> "";
        };
        root.put(key, finalInitType);
      }
      jsonNodeAdd(newNode, keyLocationList, initType, isNew, layers);
    } else {
      JSONObject currentNode = (JSONObject) root.get(key);
      layers++;
      jsonNodeAdd(currentNode, keyLocationList, initType, isNew, layers);
    }
  }

  public static Number initNumber() {
    Number number = new Number() {
      @Override
      public int intValue() {
        return 0;
      }

      @Override
      public long longValue() {
        return 0l;
      }

      @Override
      public float floatValue() {
        return 0.0f;
      }

      @Override
      public double doubleValue() {
        return 0.0d;
      }
    };
    return number;
  }
}

