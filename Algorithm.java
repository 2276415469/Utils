package com.machloop.fpc;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.machloop.alpha.common.Constants;
import com.machloop.alpha.common.util.CsvUtils;
import com.machloop.alpha.common.util.DateUtils;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;


public class Algorithm {
 volatile static   int flag = 0; 

  public static void main(String[] args) throws FileNotFoundException {

  }


  public static int number(String param) {
    int result = 0;
    double jin = 26d;
    for (int i = param.length() - 1, j = 0; i >= 0; i--, j++) {
      char cur = param.charAt(i);
      result += (cur - 'a' + 1) * Math.pow(jin, j);
    }

    return result;
  }

  public static String replaceResult(String input, String target, String replaceContent) {
    // 正则表达式
    String regex = target + "\\s*([=!=<>]=?)\\s*(.*)\\)";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(input);

    if (matcher.find()) {
      // 提取操作符
      String operator = matcher.group(1);
      // 提取操作符到右括号之间的内容
      String content = matcher.group(2);
      replaceContent = replaceContent.replaceAll("operator", operator);
      replaceContent = replaceContent.replaceAll("content", content);

      System.out.println("操作符: " + operator);
      System.out.println("内容: " + content);
    } else {
      return "";
    }

    return replaceContent;
  }

  public static String binarySum(String param1, String param2) {
    String result = "";
    String max = "", min = "";
    if (param1.length() > param2.length()) {
      max = param1;
      min = param2;
    } else {
      max = param2;
      min = param1;
    }
    for (int i = 0; i < max.length() - min.length(); i++) {
      min = "0" + min;
    }
    int jin = 0;
    for (int i = max.length() - 1; i >= 0; i--) {
      char c = max.charAt(i);
      char c2 = min.charAt(i);

      int i1 = jin + c - '0' + c2 - '0';
      switch (i1) {
        case 0:
          result = "0" + result;
          jin = 0;
          break;
        case 1:
          result = "1" + result;
          jin = 0;
          break;
        case 2:
          result = "0" + result;
          jin = 1;
          break;
        case 3:
          result = "1" + result;
          jin = 1;
          break;
      }
    }
    if (jin == 1) {
      result = "1" + result;
    }

    return result;
  }


  // 一个数字在一个排序集合中第一次和最后一次出现的下角标
  public static List<Integer> seekFirstAndLastIndex(int[] param, int target) {
    List<Integer> result = new ArrayList<>();
    int left = 0;
    int right = param.length - 1;
    int seekIndex = -1;
    while (left < right) {
      int mid = (left + right) / 2;
      if (param[mid] == target) {
        seekIndex = mid;
        break;
      }
      if (param[mid] < target) {
        left = mid + 1;
      } else {
        right = mid - 1;
      }
    }
    if (param[left] == target) {
      seekIndex = left;
    }

    if (seekIndex == -1) {
      result.add(-1);
      result.add(-1);
      return result;
    } else {
      int first = seekIndex;
      int last = seekIndex;
      while (first - 1 >= 0 && param[first - 1] == target) {
        first--;
      }
      while (last + 1 <= param.length - 1 && param[last + 1] == target) {
        last++;
      }
      result.add(first);
      result.add(last);
    }

    return result;

  }

  public static Integer seekPeak(int[] param) {

    int left = 0;
    int right = param.length - 1;
    while (left < right) {
      int mid = (left + right) / 2;
      if (param[mid] > param[mid + 1]) {
        right = mid;
      } else {
        left = mid + 1;
      }
    }

    return param[left];

  }


  // 去重list中的list 重复返回true
  // 如果使用set进行去重 [1,2] [1,2]会去重 [1,2] [2,1]不会去重 [1,2][1,2,2]也不会去重 明显无法满足顺序无关性
  public static boolean isDuplicate(List<List<Integer>> param, List<Integer> target) {
    for (List<Integer> integers : param) {
      // 判断单个list和list是否相等 所以结果在这一层
      boolean result = true;
      for (Integer integer : integers) {
        if (target.contains(integer)) {

        } else {
          // 不重复
          result = false;
        }
      }
      // 是否完全相等
      if (result == true) {
        return true;
      }
    }
    return false;

  }

  // 优化点？排序再取值 我们可以直接计算一下时间复杂度
  // 先排序 首先排序 就需要nlog2n 取值过程需要x3 负优化
  // 此方法 nx3 O(n)
  public static void threeNumberSum(List<Integer> param, int target, List<Integer> choose,
      List<List<Integer>> result) {
    if (choose.size() > 3) {
      return;
    }
    if (choose.size() == 3 && sum(choose) == target) {
      result.add(choose);
    }

    for (int i = 0; i < param.size(); i++) {
      List<Integer> nextParam = new ArrayList<>();
      nextParam.addAll(param);
      List<Integer> nextChoose = new ArrayList<>();
      nextChoose.addAll(choose);

      Integer integer = param.get(i);
      nextChoose.add(integer);
      nextParam.remove(integer);

      threeNumberSum(nextParam, target, nextChoose, result);
    }
  }

  public static int sum(List<Integer> choose) {
    return choose.stream().mapToInt(Integer::intValue).sum();
  }

  // 括号生成
  public static void bracketCreate(int n, int leftLimt, int rightLimt, String choose,
      List<String> result) {
    if (choose.length() == 2 * n) {
      result.add(choose);
      return;
    }
    if (leftLimt > rightLimt) {
      if (leftLimt == n) {
        bracketCreate(n, leftLimt, rightLimt + 1, choose + ")", result);
      } else {
        bracketCreate(n, leftLimt + 1, rightLimt, choose + "(", result);
        bracketCreate(n, leftLimt, rightLimt + 1, choose + ")", result);
      }
    } else {
      bracketCreate(n, leftLimt + 1, rightLimt, choose + "(", result);
    }
  }


  // 前面写过 不过之前那个是返回长度
  public static List<Integer> minSubArrayLen(int target, int[] nums) {
    List<Integer> result = new ArrayList<>();
    int legth = 999;
    if (nums == null) {
      return result;
    }
    if (nums.length == 1) {
      result.add(nums[0]);
      return result;
    }
    List<Integer> param = new ArrayList<>();
    for (int num : nums) {
      param.add(num);
    }

    int left = 0, right = 1;
    List<Integer> temp = new ArrayList<>();
    temp.add(nums[0]);
    while (right <= nums.length) {
      temp = param.subList(left, right);
      if (temp.stream().mapToInt(Integer::intValue).sum() >= target) {
        if (temp.size() < legth) {
          result.clear();
          result.addAll(temp);
          legth = temp.size();
        }
        left++;
      } else {
        right++;
      }
    }

    return result;
  }


  public static boolean isSubString(String param, String target) {
    int index = 0;
    for (int i = 0; i < param.length(); i++) {
      if (param.charAt(i) == target.charAt(index)) {
        index++;
      }
      if (index == target.length()) {
        return true;
      }
    }

    return false;
  }


  // 罗马字符转数字
  public static Integer romanNumeral(String param) {
    HashMap<Character, Integer> hashMap = new HashMap<>();
    hashMap.put('I', 1);
    hashMap.put('V', 5);
    hashMap.put('X', 10);
    if (param == null) {
      return 0;
    }
    if (param.length() == 1) {
      return hashMap.get(param.charAt(0));
    }
    int result = 0;
    int left = 0, right = 1;
    while (right < param.length()) {
      if (hashMap.get(param.charAt(left)) < hashMap.get(param.charAt(right))) {
        result -= hashMap.get(param.charAt(left));
      } else {
        result += hashMap.get(param.charAt(left));
      }
      left++;
      right++;
    }
    result += hashMap.get(param.charAt(left));
    return result;


  }

  public static void printThreadPoolInfo(ThreadPoolExecutor threadPoolExecutor) {

    System.out.printf(
        "ActiveCount:%s,getCorePoolSize:%s,getPoolSize:%s,getCompletedTaskCount:%s,getQueue:%s,getTaskCount:%s",
        threadPoolExecutor.getActiveCount(), threadPoolExecutor.getCorePoolSize(),
        threadPoolExecutor.getPoolSize(), threadPoolExecutor.getCompletedTaskCount(),
        threadPoolExecutor.getQueue().size(), threadPoolExecutor.getTaskCount());
    System.out.println();
  }

  // 字符串去重 o(1)的那个是入参为数组可以直接操作index替换 特殊处理0 1 是经常使用的技巧
  public static String DeDuplicateData(String param) {
    String result = "";
    if (param == null) {
      return result;
    }
    if (param.length() == 1) {
      return param;
    }

    int right = 1;
    LinkedList<Character> list = new LinkedList<>();
    list.add(param.charAt(0));

    while (right < param.length()) {
      Character now = list.peekLast();
      if (now == param.charAt(right)) {
        right++;
      } else {
        list.add(param.charAt(right));
        right++;

      }
    }

    for (Character character : list) {
      result += character;
    }

    return result;
  }


  // 最长回文字串 其实找子串应该是只有这一种暴力法 取巧的都是因为子串本身符合了一些特性可以利用
  public static String maxPalindromeSubString(String param) {
    if (param == null || "".equals(param)) {
      return "";
    }
    int max = 0;
    String result = "";

    for (int i = 0; i < param.length(); i++) {
      for (int j = i; j <= param.length(); j++) {
        String substring = param.substring(i, j);
        if (isPalindrome(substring)) {
          if (substring.length() > max) {
            max = substring.length();
            result = substring;
          }
        }
      }
    }

    return result;
  }


  public static boolean isPalindrome(String param) {
    int left = 0;
    int right = param.length() - 1;
    while (left < right) {
      if (param.charAt(left) == param.charAt(right)) {
        left++;
        right--;
      } else {
        return false;
      }
    }

    return true;
  }


  // 求根 取整
  public static int sqrt(int param) {
    if (param < 2) {
      return param;
    }
    int left = 2;
    int right = param;
    int mid = 0;
    while (left <= right) {
      mid = left + (right - left) / 2;
      long temp = mid * mid;
      if (temp == param) {
        return mid;
      }

      if (temp > param) {
        right = mid - 1;
      } else {
        left = mid + 1;
      }
    }

    return mid;

  }

  // 带精度求根
  public static double sqrtPrecision(int num) {

    double left = 0;
    double right = num;
    while (left < right) {
      double middle = (left + right) / 2.0;
      if (Math.abs(middle * middle - num) <= 0.000001) {
        return middle;
      }
      if (middle * middle < num) {
        left = middle;
        // 除一个比最终结果更小的数 自然在最终结果的右边了
        right = num / middle;
      } else if (middle * middle > num) {
        right = middle;
        // 除一个比最终结果更大的数 自然在最终结果的左边了
        left = num / middle;
      }
    }
    return left;
  }


  // 以下两个方法为将一个数据按奇偶数分为两部分 都是基于交换 一个是变种快排 一个双指针
  public static void parityClassification2(long[] param) {
    if (param == null) {
      return;
    }
    int left = 0;
    int right = param.length - 1;
    while (left < right) {
      if (param[left] % 2 == 1) {
        left++;
      } else {
        if (param[right] % 2 == 0) {
          right--;
        } else {
          long temp = param[left];
          param[left] = param[right];
          param[right] = temp;
        }
      }
    }

  }

  public static void parityClassification(long[] param) {
    if (param == null) {
      return;
    }
    Long temp = param[0];
    int left = 0;
    int right = param.length - 1;
    while (left < right) {
      while (param[right] % 2 == 0 && left < right) {
        right--;
      }

      param[left] = param[right];
      left++;

      while (param[left] % 2 == 1 && left < right) {
        left++;
      }

      param[right] = param[left];
      right--;
    }

    param[left] = temp;

  }

  public static void leafNodePathSum(TreeNode root, String path, List<String> result) {
    if (root == null) {
      return;
    }

    path += root.val + "";

    if (root.left == null && root.right == null) {
      result.add(path);
      return;
    }

    leafNodePathSum(root.left, path, result);
    leafNodePathSum(root.right, path, result);
  }


  // 多线程交替输出
  public static void multithreadedAlternateOutput() {
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>());
    threadPoolExecutor.submit(() -> {
      int i = 0;
      int j = 0;
      while (j < 100) {
        if (flag == 0) {
          System.out.println(i);
          i = i + 2;
          flag = 1;
          j++;
        }
      }
    });

    threadPoolExecutor.submit(() -> {
      int i = 1;
      int j = 0;
      while (j < 100) {
        if (flag == 1) {
          System.out.println(i);
          i = i + 2;
          flag = 0;
          j++;
        }
      }
    });
  }

  // 两棵树是否为同一颗 一次性写出来和答案一样
  public static boolean sameTree(TreeNode param1, TreeNode param2) {
    boolean result = true;

    if (param1 == null && param2 == null) {
      return result;
    }

    if (param1 == null || param2 == null) {
      return false;
    }
    if (param1.val != param2.val) {
      return false;
    }

    boolean leftResult = sameTree(param1.left, param2.left);
    boolean rightResult = sameTree(param1.right, param2.right);

    return leftResult & rightResult;
  }

  // 根据前序 中序还原树
  public static TreeNode revivificationTree(List<Integer> qian, List<Integer> zhong) {
    TreeNode result = new TreeNode();
    if (qian.size() == 0) {
      return null;
    }

    Integer root = qian.get(0);
    result.val = root;

    int midIndex = zhong.indexOf(root);
    List<Integer> zhongLeft = zhong.subList(0, midIndex);
    List<Integer> zhongRight = zhong.subList(midIndex + 1, zhong.size());

    List<Integer> qianLeft = qian.subList(1, zhongLeft.size() + 1);
    List<Integer> qianRight = qian.subList(zhongLeft.size() + 1, qian.size());

    result.left = revivificationTree(qianLeft, zhongLeft);
    result.right = revivificationTree(qianRight, zhongRight);

    return result;
    // TreeNode tree = createTree();
    // printTree(tree,2);
    //
    // int[] qian = {12, 3, 1, 2, 9, 5, 8, 22, 33};
    // int[] zhong = {1, 3, 2, 12, 5, 9, 33, 22, 8};
    // ArrayList<Integer> qianL = new ArrayList<>();
    // for (int i : qian) {
    // qianL.add(i);
    // }
    // ArrayList<Integer> zhongL = new ArrayList<>();
    // for (int i : zhong) {
    // zhongL.add(i);
    // }
    // TreeNode huanyuan = huanyuan(qianL, zhongL);
    // printTree(huanyuan,2);
  }


  public static String simplifyUrl(String url) {

    String result = "";
    LinkedList<String> linkedList = new LinkedList<>();
    url = url.replace("\\\\", "\\");

    String[] split = url.split("\\\\");
    for (String s : split) {
      linkedList.add(s);
    }

    LinkedList<String> linkedList2 = new LinkedList<>();
    while (linkedList.size() != 0) {
      String ding = linkedList.pollFirst();
      switch (ding) {
        case ".":
          break;
        case "..":
          linkedList2.pollLast();

          break;
        default:
          if (!"".equals(ding)) {
            linkedList2.add(ding);
          }
      }
    }

    for (String s : linkedList2) {
      result += "\\" + s;
    }

    System.out.println(result);
    return result;
  }

  // 异构字符串分组
  public static Map<String, List<String>> isomerismStringGroup(String[] param) {
    String[] paramTest = {"rec", "rce", "erc", "abc", "cba"};
    HashMap<String, List<String>> result = new HashMap<>();
    for (String s : param) {
      char[] charArray = s.toCharArray();
      Arrays.sort(charArray);
      String string = String.valueOf(charArray);

      if (!result.containsKey(string)) {
        ArrayList<String> list = new ArrayList<>();
        list.add(s);
        result.put(string, list);
      } else {
        result.get(string).add(s);
      }
    }
    return result;
  }


  // 找一个数组中总和大于等于target的最短子数组
  public static int minSubArray(int[] param, int target) {
    int result = 999999;
    int left = 0;
    int right = 0;
    while (right < param.length) {
      if (sumBetweenLR(param, left, right) >= target) {
        result = Math.min(result, right - left + 1);
        left++;
      } else {
        right++;
      }
    }


    return result;
  }

  public static int sumBetweenLR(int[] param, int leftIndex, int rightIndex) {
    int result = 0;
    for (int i = leftIndex; i <= rightIndex; i++) {
      result += param[i];
    }
    return result;
  }


  // 做的优化

  // 跳跃游戏 跳到最后一格的最少步数 参数中的数字代表可以跳的距离
  public static int jump(int[] param) {
    int[] dp = new int[param.length];
    dp[0] = 0;
    for (int i = 1; i < param.length; i++) {
      ArrayList<Integer> list1 = new ArrayList<>();
      for (int j = i - 1; j >= 0; j--) {
        int stepSize = i - j;
        if (param[j] >= stepSize) {
          list1.add(dp[j] + 1);
        }
      }

      Integer integer = list1.stream().min(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
          return o1 - o2;
        }
      }).get();

      dp[i] = integer;

    }
    System.out.println(dp[dp.length - 1]);
    return dp[dp.length - 1];
  }

  // 无序 不重复 有正负数组 找最长连续长度 不允许排序 （找最长连续序列）
  public static int longestContinuous(int[] param) {
    int result = 0;
    if (param == null) {
      return result;
    }

    HashSet<Integer> paramSet = new HashSet<>();
    for (int i : param) {
      paramSet.add(i);
    }

    HashSet<Integer> doneSet = new HashSet<>();
    int temp = 1;
    for (Integer single : paramSet) {
      if (doneSet.contains(single)) {
        continue;
      }
      doneSet.add(single);

      Integer left = single - 1;
      Integer right = single + 1;
      while (paramSet.contains(left)) {
        doneSet.add(left);
        left--;
        temp++;
      }

      while (paramSet.contains(right)) {
        doneSet.add(right);
        right++;
        temp++;
      }
      result = Math.max(temp, result);
      temp = 1;
    }
    return result;
  }


  // dp矩阵中每一新格子 遍历前面所有的 比较所有能达到的+1跳以后 取最小值

  // 饭转数组
  public static int peaksAndTroughs(int[] param) {
    int result = 0;
    if (param == null || param.length == 0) {
      return result;
    }
    if (param.length == 1) {
      return param[0] > 0 ? param[0] : result;
    }

    for (int i = 1; i < param.length; i++) {
      if (param[i] > param[i - 1]) {
        result += param[i] - param[i - 1];
      }

    }

    System.out.println(result);
    return result;
  }


  // 轮转数组
  public static void lunzhuan(int[] param, int k) {
    k = k % param.length;

    if (k == 0) {
      printArray(param);
      return;
    }
    int start = param.length - k;
    int end = start - 1;

    while (true) {
      System.out.println(param[start]);
      start++;
      if (start == param.length) {
        start = 0;
      }
      if (start == end) {
        break;
      }
    }
    System.out.println(param[end]);
  }


  public static void printArray(int[] param) {
    for (int i : param) {
      System.out.print(i + "|");
    }
    System.out.println();
  }

  // n(1)删除重复数据
  public static int DeDuplicateData(int[] param) {
    if (param == null) {
      return 0;
    }
    if (param.length <= 2) {
      return param.length;
    }

    printArray(param);

    int slow = 0;
    int now = 1;
    for (int i = 1; i < param.length; i++) {
      if (param[slow] == param[i]) {
        if (now < 2) {
          slow++;
          now++;
          param[slow] = param[i];
        } else {
          now++;
        }
      } else {
        now = 1;
        slow++;
        param[slow] = param[i];
      }
    }

    printArray(param);
    return slow + 1;
  }


  // 快慢指针判断是否有环 关键在于用快慢成比例关系 来简化一些空间和时间 比如找链表中间值 倒数第K个节点
  public static boolean slowQuickHasCicyle(ListNode node) {
    if (node == null || node.next == null) {
      return false;
    }
    ListNode slow = node.next;
    ListNode quick = node.next.next;
    while (slow != null && quick != null) {
      if (slow == quick) {
        return true;
      }
      slow = slow.next;
      quick = quick.next.next;
    }
    return false;

  }

  /**
   *     1
   *    2  3
   *   4 5 6 7
  **/
  public static void heapSort(int[] param) {
    // 找到第一个最大的非叶子节点 从右往左 从下往上 构造大顶堆
    for (int i = param.length / 2 - 1; i >= 0; i--) {
      adjustHeap(param, i, param.length - 1);
    }
    // 堆顶和最后一个交换 再调整 直到循环结束
    for (int i = param.length - 1; i >= 0; i--) {
      int temp = param[0];
      param[0] = param[i];
      param[i] = temp;
      adjustHeap(param, 0, i - 1);
    }

  }

  // 根据堆顶数值 和左右范围 重新调整为大顶堆
  public static void adjustHeap(int[] param, int leftIndex, int rightIndex) {
    int target = param[leftIndex];
    // 用for循环可以有效避免边界问题 同时第一次就取下一次 避免了for内的边界问题
    for (int i = leftIndex * 2 + 1; i <= rightIndex; i = i * 2 + 1) {
      // i在循环中 指向最大值

      if (i + 1 <= rightIndex && param[i + 1] > param[i]) {
        i = i + 1;
      }
      // 大的子节点上浮 目标数值下沉 至于满足条件
      if (param[i] > target) {
        param[leftIndex] = param[i];
        leftIndex = i;
      } else {
        break;
      }

    }

    param[leftIndex] = target;
  }

  /**
   *    100
   *   88    5
   * 77 3  4   3
   * @param param
   * @return
   */


  public static int parseInt(String param) {
    int result = 0;
    int jin = 0;
    for (int i = param.length() - 1; i >= 0; i--) {
      result += (param.charAt(i) - '0') * Math.pow(10, jin);
      jin++;
    }
    return result;
  }


  // 计算岛屿的数量 1为岛屿 0为海
  public static int numberOfIslands(String[][] grid) {
    int result = 0;
    int oneMax = grid.length;
    int twoMax = grid[0].length;
    for (int i = 0; i < oneMax; i++) {
      for (int j = 0; j < twoMax; j++) {
        if (grid[i][j].equals("1")) {
          traversalSingleIsland(grid, i, j, oneMax, twoMax);
          result++;
        }
      }
    }

    return result;
  }

  public static void traversalSingleIsland(String[][] grid, int oneIndex, int twoIndex, int oneMax,
      int twoMax) {
    if (oneIndex >= oneMax || oneIndex < 0 || twoIndex >= twoMax || twoIndex < 0) {
      return;
    }
    if ("2".equals(grid[oneIndex][twoIndex]) || "0".equals(grid[oneIndex][twoIndex])) {
      return;
    }
    grid[oneIndex][twoIndex] = "2";
    traversalSingleIsland(grid, oneIndex - 1, twoIndex, oneMax, twoMax);
    traversalSingleIsland(grid, oneIndex + 1, twoIndex, oneMax, twoMax);
    traversalSingleIsland(grid, oneIndex, twoIndex - 1, oneMax, twoMax);
    traversalSingleIsland(grid, oneIndex, twoIndex + 1, oneMax, twoMax);

  }

  public static ListNode deleteNFromBottomNode(ListNode head, int n) {
    ListNode loop = head;
    LinkedList<ListNode> list = new LinkedList<>();
    while (head != null) {
      list.add(head);

      head = head.next;
    }

    list.remove(list.size() - n);

    ListNode loop2 = new ListNode();
    ListNode result = loop2;
    for (ListNode node : list) {
      loop2.next = node;
      loop2 = node;
    }
    // 预防删除的是最后一个
    loop2.next = null;

    result = result.next;
    return result;
  }


  // 数组是否能够被分为等值的k部分
  public boolean canPartitionKSubsets(int[] nums, int k) {
    // 筛选不符合条件的情况1
    if (nums.length < k) {
      return false;
    }
    // 计算和
    int sum = 0;
    for (int num : nums) {
      sum += num;
    }
    // 筛选不符合条件的情况2
    if (sum % k != 0) {
      return false;
    }
    // 获得目标值
    int target = sum / k;
    int used = 0; // 使用位图技巧
    return backtrack(k, 0, nums, 0, used, target);
  }

  HashMap<Integer, Boolean> memo = new HashMap<>();

  boolean backtrack(int k, int bucket, int[] nums, int start, int used, int target) {
    // backcase
    if (k == 0) {
      return true;
    }

    // 换下一个集合装
    if (bucket == target) {
      boolean res = backtrack(k - 1, 0, nums, 0, used, target);
      memo.put(used, res);
      return res;
    }

    // 减枝
    if (memo.containsKey(used)) {
      return memo.get(used);
    }

    for (int i = start; i < nums.length; i++) {
      // 元素已经被选中过了
      if (((used >> i) & 1) == 1) { // 判断第i为是否是1
        continue;
      }
      if (nums[i] + bucket > target) {
        continue;
      }

      // 排除所有不选的情况，这会需要选择
      used |= 1 << i; // 将第i为设为1
      bucket += nums[i];
      // 双重循环
      if (backtrack(k, bucket, nums, i + 1, used, target)) { // ==for循环
        return true;
      }
      // 撤销选择
      used ^= 1 << i; // 将第i为设为0
      bucket -= nums[i];
    }
    return false;
  }

  public static void LevelPrintOddEven(LinkedList<TreeNode> param, int height) {
    if (param == null || param.size() == 0) {
      return;
    }
    if (height % 2 == 0) {
      for (int i = param.size() - 1; i >= 0; i--) {
        System.out.print(param.get(i).val + " ");
      }
    } else {
      for (int i = 0; i < param.size(); i++) {
        System.out.print(param.get(i).val + " ");
      }
    }
    LinkedList<TreeNode> nextParam = new LinkedList<>();
    while (param.size() != 0) {
      TreeNode first = param.pollFirst();
      if (first.left != null) {
        nextParam.add(first.left);
      }
      if (first.right != null) {
        nextParam.add(first.right);
      }
    }
    LevelPrintOddEven(nextParam, height + 1);

  }

  public static void swap(int[] arr, int a, int b) {
    int temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }

  // Find k numbers that sum to m
  public static List<List<Integer>> kNumber2SumM(List<Integer> param, int k, int m) {

    List<List<Integer>> result = new ArrayList<>();

    if (k == 1) {
      if (param.contains(m)) {
        List<Integer> temp = new ArrayList<>();
        temp.add(m);
        result.add(temp);
        return result;
      }
    }

    for (int i = 0; i < param.size(); i++) {
      List<Integer> nextParam = new ArrayList<>();
      nextParam.addAll(param);
      nextParam.remove(i);
      List<List<Integer>> lists = kNumber2SumM(nextParam, k - 1, m - param.get(i));
      for (List<Integer> list : lists) {
        list.add(param.get(i));
        // 不加这个判断就是一个全排列的结果
        if (!hasList(result, list)) {
          result.add(list);
        }
      }
    }

    return result;
  }

  // 根据值判断二维集合中是否有某一个一维集合
  public static boolean hasList(List<List<Integer>> param, List<Integer> list) {
    for (List<Integer> single : param) {

      boolean equal = true;
      for (Integer singleInteger : single) {
        if (list.contains(singleInteger)) {

        } else {
          equal = false;
        }
      }

      if (equal == true) {
        return true;
      } else {
        return false;
      }
    }

    return false;
  }


  public static List<Integer> fromTopToBottomAndLeftToRightprint(LinkedList<TreeNode> queue,
      List<Integer> result) {
    if (queue.size() == 0) {
      return result;
    }
    LinkedList<TreeNode> nextQueue = new LinkedList<>();
    while (queue.size() != 0) {
      TreeNode treeNode = queue.pollFirst();
      result.add(treeNode.val);
      if (treeNode.left != null) {

        nextQueue.add(treeNode.left);
      }
      if (treeNode.right != null) {
        nextQueue.add(treeNode.right);

      }

    }

    return fromTopToBottomAndLeftToRightprint(nextQueue, result);
  }


  // 左右翻转
  public static TreeNode flipBinaryTree(TreeNode root) {
    if (root == null) {
      return null;
    }
    TreeNode temp = root.left;
    root.left = root.right;
    root.right = temp;
    flipBinaryTree(root.left);
    flipBinaryTree(root.right);

    return root;
  }

  // 根据前序 中序还原一棵树
  public static TreeNode restoreTree(List<Integer> preorder, List<Integer> midorder) {
    if (CollectionUtils.isEmpty(preorder)) {
      return null;
    }
    Integer integer = preorder.get(0);
    int index = midorder.indexOf(integer);
    List<Integer> midLeft = midorder.subList(0, index);
    List<Integer> midRight = midorder.subList(index + 1, midorder.size());

    List<Integer> leftnext = preorder.subList(1, midLeft.size() + 1);
    List<Integer> rightnext = preorder.subList(1 + midLeft.size(), preorder.size());

    TreeNode root = new TreeNode(integer);
    root.left = restoreTree(leftnext, midLeft);
    root.right = restoreTree(rightnext, midRight);
    return root;
  }


  // 根据数据特点选择方法 这里关键的是 找到第一个正数和代价之间的关系
  public static int longestContinuousSubarray(int[] param) {
    int result = 0;
    int[] dp = new int[param.length];
    dp[0] = param[0];
    result = dp[0];
    for (int i = 1; i < param.length; i++) {
      dp[i] = Math.max(dp[i - 1] + param[i], param[i]);
      result = Math.max(dp[i], result);
    }

    return result;
  }

  public static Tuple2<String, Integer> longestNotRepeatSubstring(String param) {
    int leftIndex = 0;
    int rightIndex = 0;
    int maxLength = 0;
    String result = "";
    LinkedList<Character> queue = new LinkedList<>();
    while (rightIndex < param.length()) {
      char c = param.charAt(rightIndex);
      if (!queue.contains(c)) {
        queue.add(c);
        rightIndex++;
        if (queue.size() > maxLength) {
          maxLength = queue.size();
          result = param.substring(leftIndex, rightIndex);
        }
      } else {
        queue.pollFirst();
        leftIndex++;
      }
    }

    return Tuples.of(result, maxLength);
  }


  // 字典序算法
  public static String nextPermutation(List<Integer> param) {
    // 需要交换的左索引 第一个右比左大的位置
    int redIndex = 0;
    String result = "";
    for (int i = param.size() - 1; i >= 1; i--) {
      if (param.get(i - 1) < param.get(i)) {
        redIndex = i - 1;
        break;
      }
    }
    // 截取右部分取最小值 避免不相邻
    List<Integer> integers = param.subList(redIndex + 1, param.size());
    Integer min = integers.stream().min(new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
        return o1 - o2;
      }
    }).get();
    int changeIndex = param.indexOf(min);
    // 交换
    Integer temp = param.get(redIndex);
    param.set(redIndex, param.get(changeIndex));
    param.set(changeIndex, temp);
    // 切分 重排序
    List<Integer> before = param.subList(0, redIndex + 1);
    List<Integer> after = param.subList(redIndex + 1, param.size());
    after.sort(new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
        return o1 - o2;
      }
    });

    before.addAll(after);
    for (Integer integer : before) {
      result += integer + "";
    }

    return result;
  }


  public static boolean isCompleteBinaryTree(TreeNode root) {
    LinkedList<TreeNode> next = new LinkedList<>();
    next.add(root);
    LinkedList<TreeNode> result = new LinkedList<>();
    levelPrintByQueue(next, result, true);
    for (int i = 0; i < result.size() - 1; i++) {
      if (result.get(i) == null && result.get(i + 1) != null) {
        return false;
      }
    }
    return true;
  }

  public static void exeLevelPrintByQueue(TreeNode node) {
    LinkedList<TreeNode> current = new LinkedList<>();
    current.add(node);
    List<TreeNode> result = new ArrayList<>();
    levelPrintByQueue(current, result, false);
    System.out.println(result);
  }

  public static void levelPrintByQueue(LinkedList<TreeNode> current, List<TreeNode> result,
      boolean isContainNullNode) {
    if (current.isEmpty()) {
      return;
    }
    LinkedList<TreeNode> next = new LinkedList<>();

    while (CollectionUtils.isNotEmpty(current)) {
      TreeNode treeNode = current.pollFirst();
      if (treeNode != null) {
        result.add(treeNode);
        next.add(treeNode.left);
        next.add(treeNode.right);
      }
      // 这里是为了把null节点也加进来
      if (isContainNullNode) {
        if (treeNode == null) {
          result.add(treeNode);
        }
      }
    }
    levelPrintByQueue(next, result, isContainNullNode);
  }


  /**
   * 根节点到叶子节点的路径和 这里用递归返回值为子树和大小更好 null返回0
   * 我又写成了回溯
   * @param root
   * @param result 结果
   * @param path 当时的路径
   */
  public static void depthFirstTraversalLeafNodeSum(TreeNode root, List<Integer> result,
      List<String> path) {
    path.add(root.val + "");
    List<String> nextPathParam = new ArrayList<>();
    nextPathParam.addAll(path);

    if (root.left == null && root.right == null) {
      String temp = "";
      for (String s : nextPathParam) {
        temp += s;
      }
      result.add(Integer.parseInt(temp));
      return;
    }

    if (root.left != null) {
      depthFirstTraversalLeafNodeSum(root.left, result, nextPathParam);
    }

    List<String> nextPathParam2 = new ArrayList<>();
    nextPathParam2.addAll(path);

    if (root.right != null) {
      depthFirstTraversalLeafNodeSum(root.right, result, nextPathParam2);
    }

  }


  // param=cc2[abc]3[cd]ef
  public static String stringDecode(String param) {
    String result = "";
    for (int i = 0; i < param.length(); i++) {
      char c = param.charAt(i);
      if (c - '0' <= 9) {
        int loop = c - '0';
        String loopString = "";
        int index = i + 2;
        while (true) {
          if (param.charAt(index) == ']') {
            break;
          }
          loopString += param.charAt(index);
          index++;
        }

        for (int j = 0; j < loop; j++) {
          result += loopString;
        }
        i = index;
      } else {
        result = result + c;
      }
    }
    return result;
  }


  // 反转单词
  public static String flipString(String param) {
    String result = "";
    int lastPoint = param.length();
    for (int i = param.length() - 1; i >= 0; i--) {
      char current = param.charAt(i);
      if (current == ' ') {
        String substring = param.substring(i + 1, lastPoint);
        result += substring + " ";
        lastPoint = i;
      }
    }
    result += param.substring(0, lastPoint);

    return result;
  }


  /**
   * 最长连续序列 0n复杂度 hash方法和排序循环方法
   */
  public static int longestContinuousSequence2(int[] params) {
    if (params == null || params.length == 0) {
      return 0;
    }
    int result = 1;
    HashSet<Integer> set = new HashSet<>();
    for (int i = 0; i < params.length; i++) {
      set.add(params[i]);
    }
    for (int i = 0; i < params.length; i++) {
      if (!set.contains(params[i] - 1)) {
        int temp = 1;
        int tempLength = 1;
        while (true) {
          if (set.contains(params[i] + temp)) {
            tempLength++;
            temp++;
            result = Math.max(result, tempLength);
          } else {
            break;
          }
        }
      }
    }

    return result;
  }

  public static int longestContinuousSequence1(int[] params) {
    if (params == null || params.length == 0) {
      return 0;
    }
    int result = 1;
    Arrays.sort(params);
    int tempLength = 1;
    for (int i = 0; i < params.length - 1; i++) {
      if (params[i] + 1 == params[i + 1]) {
        tempLength++;
        result = Math.max(result, tempLength);
      } else {
        tempLength = 1;
      }
    }

    return result;
  }


  // 1234 -> 4321
  public static void queue2Stack(int[] param) {
    int result = 0;
    LinkedList<Integer> queue = new LinkedList<>();
    LinkedList<Integer> queue2 = new LinkedList<>();
    for (int i = 0; i < param.length; i++) {
      queue.add(param[i]);
    }

    while (result != param.length) {
      while (queue.size() != 0) {
        if (queue.size() == 1) {
          System.out.println(queue.pollFirst());
          result++;
        } else {
          queue2.add(queue.pollFirst());
        }
      }

      if (result == param.length) {
        break;
      }

      while (queue2.size() != 0) {
        if (queue2.size() == 1) {
          System.out.println(queue2.pollFirst());
          result++;
        } else {
          queue.add(queue2.pollFirst());
        }
      }

    }

  }


  // 1234 ->1234
  public static void stack2Queue(int[] param) {
    LinkedList<Integer> list1 = new LinkedList<>();
    LinkedList<Integer> list2 = new LinkedList<>();
    for (int i = 0; i < param.length; i++) {
      list1.add(param[i]);
    }

    while (!list1.isEmpty()) {
      list2.add(list1.pollLast());
    }

    while (!list2.isEmpty()) {
      System.out.println(list2.pollLast());
    }
  }


  // 路径和
  public static boolean pathSum(TreeNode root, int targetValue, int currentValue) {
    if (root == null) {
      return false;
    }
    currentValue += root.val;
    if (currentValue == targetValue) {
      return true;
    }
    if (currentValue < targetValue) {
      boolean left = pathSum(root.left, targetValue, currentValue);
      boolean right = pathSum(root.right, targetValue, currentValue);
      return left | right;
    }

    return false;
  }


  // 二叉搜索树中两节点的公共祖先
  public static TreeNode commonAncestor(TreeNode root, int key1, int key2) {
    int max = Math.max(key1, key2);
    int min = Math.min(key1, key2);

    if (root.val == key1 || root.val == key2) {
      return root;
    }
    if (min < root.val && max > root.val) {
      return root;
    }
    if (max < root.val) {
      return commonAncestor(root.left, key1, key2);
    }

    if (min > root.val) {
      return commonAncestor(root.right, key1, key2);
    }

    return new TreeNode(-1);
  }


  public static boolean isSymmetry(TreeNode root) {
    return isSymmetry(root.left, root.right);
  }

  public static boolean isSymmetry(TreeNode left, TreeNode right) {
    if (left == null && right == null) {
      return true;
    }
    if (left == null && right != null) {
      return false;
    }
    if (left != null && right == null) {
      return false;
    }
    if (left.val != right.val) {
      return false;
    }

    boolean b = isSymmetry(left.left, right.right);
    boolean a = isSymmetry(left.right, right.left);
    return a & b;
  }


  // 数的最大直径
  public static int diameterOfBinaryTree(TreeNode root) {
    if (root == null) {
      return 0;
    }
    int leftHeight = getTreeHeight(root.left);
    int rightHeight = getTreeHeight(root.right);
    int ownDiameter = leftHeight + rightHeight;
    int leftDiameter = diameterOfBinaryTree(root.left);
    int rightDiameter = diameterOfBinaryTree(root.right);

    int max = Math.max(ownDiameter, leftDiameter);
    int max2 = Math.max(max, rightDiameter);
    return max2;
  }


  public static int getTreeHeight(TreeNode root) {
    if (root == null) return 0;
    int leftHeight = getTreeHeight(root.left);
    int rightHeight = getTreeHeight(root.right);
    return Math.max(leftHeight, rightHeight) + 1;
  }


  public static boolean isSearchTree(TreeNode tree, int fValue, int trend) {
    // isSearchTree(tree,0,3)
    if (tree == null) {
      return true;
    }

    switch (trend) {
      case 3:
        break;
      case 1:
        if (tree.val > fValue) {
          return false;
        }
        break;
      case 2:
        if (tree.val < fValue) {
          return false;
        }
        break;
    }
    // 搜索树必由搜索树组成
    boolean leftResult = isSearchTree(tree.left, tree.val, 1);
    boolean rightResult = isSearchTree(tree.right, tree.val, 2);
    return leftResult & rightResult;
  }

  public static void treeRightView(TreeNode tree) {
    Map<Integer, List<Integer>> result = new LinkedHashMap<>();

    levelPrint(tree, result, 0);

    for (Map.Entry<Integer, List<Integer>> integerListEntry : result.entrySet()) {
      LinkedList<Integer> value = (LinkedList) integerListEntry.getValue();
      System.out.println(value.getLast());
    }

  }


  /**
   * 寻找第k大的数
   */
  public static int seekKNumber(int[] param, int k, int leftIndex, int rightIndex) {
    if (leftIndex > rightIndex) {
      return -1;
    }
    // call
    int endIndex = quickOnce(param, leftIndex, rightIndex);
    if (endIndex == k) {
      return param[endIndex];
    }

    if (endIndex < k) {
      return seekKNumber(param, k, endIndex + 1, rightIndex);
    }

    if (endIndex > k) {
      return seekKNumber(param, k, leftIndex, endIndex - 1);
    }
    return -1;
  }

  public static int quickOnce(int[] param, int leftIndex, int rightIndex) {
    int hangValue = param[leftIndex];

    int dynamicLeftIndex = leftIndex;
    int dynamicrightIndex = rightIndex;

    while (dynamicrightIndex > dynamicLeftIndex) {
      if (param[dynamicrightIndex] >= hangValue && dynamicrightIndex > dynamicLeftIndex) {
        dynamicrightIndex--;
        continue;
      } else {

        param[dynamicLeftIndex] = param[dynamicrightIndex];
        dynamicLeftIndex++;
      }

      if (param[dynamicLeftIndex] <= hangValue && dynamicrightIndex > dynamicLeftIndex) {
        dynamicLeftIndex++;
        continue;
      } else {
        param[dynamicrightIndex] = param[dynamicLeftIndex];
        dynamicrightIndex--;
      }

    }

    param[dynamicLeftIndex] = hangValue;
    return dynamicLeftIndex;
  }


  // three number sum
  public static boolean hasSumThreeNumber(int target, List<Integer> param) {
    for (int i = 0; i < param.size(); i++) {
      int i1 = target - param.get(i);

      List<Integer> list = new ArrayList<>();
      list.addAll(param);
      list.remove(param.get(i));

      if (hasSumTwoNumber(i1, list)) {
        return true;
      }

    }

    return false;
  }

  // two number sum Backtracking plus recursion
  public static boolean hasSumTwoNumber(int target, List<Integer> param) {
    for (int i = 0; i < param.size(); i++) {
      int i1 = target - param.get(i);

      List<Integer> list = new ArrayList<>();
      list.addAll(param);
      list.remove(param.get(i));

      int[] array = list.stream().mapToInt(Integer::intValue).toArray();

      if (twoF(array, i1, 0, array.length - 1) != -1) {
        return true;
      }

    }

    return false;
  }


  /**
   *  String add function
   * @param param1
   * @param param2
   * @return
   */
  public static String addString(String param1, String param2) {
    if (param1 == null || param1.equals("")) {
      return param2;
    }
    if (param2 == null || param2.equals("")) {
      return param1;
    }
    if (param1.length() >= param2.length()) {
      int length = param1.length() - param2.length();
      String bu = "";
      for (int i = 0; i < length; i++) {
        bu += "0";
      }
      param2 = bu + param2;
    } else {
      int length = param2.length() - param1.length();
      String bu = "";
      for (int i = 0; i < length; i++) {
        bu += "0";
      }
      param1 = bu + param1;
    }

    int jin = 0;
    String result = "";
    for (int i = param1.length() - 1; i >= 0; i--) {
      int i1 = param1.charAt(i) - '0';
      int i2 = param2.charAt(i) - '0';
      int i3 = i1 + i2 + jin;
      jin = i3 / 10;
      int i4 = i3 % 10;
      result = i4 + result;
    }
    return result;

  }

  /**
   *  judgment has annulus
   * @param param
   */
  public void judgmentAnnulus(ListNode param) {
    LinkedList<ListNode> linkedList = new LinkedList<>();
    while (param != null) {
      if (linkedList.contains(param)) {
        System.out.println(" you");
        int i = linkedList.indexOf(param);
        System.out.println("index:" + i);
        break;
      } else {
        System.out.println(" wu");
      }
      linkedList.add(param);
      param = param.next;

    }
  }

  public static String underlineToHump(String param) {
    if (param == null || "".equals(param.trim())) {
      return "";
    }
    param = param.toLowerCase();
    StringBuilder sb = new StringBuilder("");
    if (!param.contains("_")) {
      return param;
    }
    String arrays[] = param.split("");
    for (int i = 0; i < arrays.length; i++) {
      String s = arrays[i];
      if (s.equals("_")) {
        int nextStep = i + 1;
        if (nextStep <= arrays.length) {
          String next = arrays[nextStep];
          next = next.toUpperCase();
          arrays[nextStep] = next;
        }
      } else {
        sb.append(s);
      }
    }
    return sb.toString();
  }


  /**
   * reversal link by range 不如用list直接存 拼接一下
   * @param param
   * @param left 索引值
   * @param right
   * @return
   */
  public static ListNode reversalLink(ListNode param, int left, int right) {

    if (param == null || param.next == null) {
      return param;
    }

    if (left < 0) {
      left = 0;
    }

    int max = -1;
    ListNode maxPoint = param;
    while (maxPoint != null) {
      max++;
      maxPoint = maxPoint.next;
    }
    if (right > max) {
      right = max;
    }


    int currentPoint = 0;
    ListNode loop = param;
    // cut three parts
    ListNode leftLast = null;
    ListNode midLeft = null;
    ListNode midRight = null;
    ListNode rightFirst = null;

    if (left == 0) {
      midLeft = param;
      leftLast = null;
    }

    while (loop != null) {

      if (currentPoint == left - 1) {
        leftLast = loop;
        midLeft = loop.next;
      }

      if (currentPoint == right) {
        midRight = loop;
        rightFirst = loop.next;
      }

      loop = loop.next;
      currentPoint++;
    }

    // head insert
    // cut tail
    midRight.next = null;
    ListNode vnode = new ListNode();

    ListNode loop2 = midLeft;
    while (loop2 != null) {
      ListNode temp = loop2.next;
      loop2.next = vnode;
      vnode = loop2;
      loop2 = temp;
    }

    // process boundary value
    if (leftLast != null) {

      leftLast.next = vnode;
      midLeft.next = rightFirst;
      return param;
    } else {
      midLeft.next = rightFirst;
      return vnode;
    }
  }

  public static void printLink(ListNode param) {
    while (param != null) {
      System.out.print(param.val + "|");
      param = param.next;
    }
    System.out.println();
  }


  public static ListNode reversalLink(ListNode param) {
    if (param == null) {
      return new ListNode();
    }
    LinkedList<Integer> stack = new LinkedList<>();

    ListNode loop = param;
    while (loop != null) {

      stack.add(loop.val);
      loop = loop.next;
    }

    ListNode result = null;
    loop = result;
    while (!stack.isEmpty()) {
      Integer nodeVal = stack.pollLast();
      ListNode node = new ListNode(nodeVal);
      if (result == null) {
        result = node;
        loop = node;
        continue;
      }

      loop.next = node;
      loop = node;

    }

    return result;
  }


  public static int[][] rotationMatrix(int[][] params) {
    if (params == null || params.length == 0) {
      return new int[0][];
    }

    int[][] result = new int[params.length][params[0].length];

    for (int i = 0; i < params.length; i++) {
      int[] line = params[i];
      for (int j = 0; j < line.length; j++) {
        result[j][params[0].length - 1 - i] = params[i][j];
      }
    }

    return result;
  }


  // 二维查找 参数有序
  public static boolean twoDimensionalSearch(int[][] params, int target) {

    for (int i = 0; i < params.length; i++) {
      int[] line = params[i];
      if (line[line.length - 1] >= target) {
        int index = twoF(line, target, 0, line.length - 1);
        if (index != -1) {
          return true;
        } else {
          return false;
        }
      }
    }

    return false;
    // 还可以二维转一维 直接二分
  }

  // 归并排序 先递归左 再递归右 假设都已经有序 那么就是两部分合并 终结条件为 left<right
  public static void mergeSort(int[] params, int leftIndex, int rightIndex) {
    if (leftIndex < rightIndex) {
      int midIndex = (leftIndex + rightIndex) / 2;
      mergeSort(params, leftIndex, midIndex);
      // +1 如果放在上面就会无限
      mergeSort(params, midIndex + 1, rightIndex);

      mergeOrderlyArray(params, leftIndex, midIndex, rightIndex);
    }
  }

  public static void mergeOrderlyArray(int[] params, int leftIndex, int midIndex, int rightIndex) {
    int[] ints = new int[rightIndex - leftIndex + 1];
    int i = leftIndex;
    // 和分离的逻辑一致 中 属于左
    int j = midIndex + 1;
    int k = 0;
    while (i <= midIndex && j <= rightIndex) {
      if (params[i] > params[j]) {
        ints[k++] = params[j++];
      } else {
        ints[k++] = params[i++];
      }
    }
    // 只会有一个有剩余
    while (i <= midIndex) {
      ints[k++] = params[i++];
    }

    while (j <= rightIndex) {
      ints[k++] = params[j++];
    }
    // 重新把数据拷贝进去
    k = 0;
    for (int l = leftIndex; l <= rightIndex; l++) {
      params[l] = ints[k++];
    }

  }


  // deep first基本是树 scope first基本是图 在树上表现为level
  public static void scopePrintTree(List<TreeNode> loopResult, List<List<Integer>> finalResult) {
    if (loopResult.size() == 0) {
      for (List<Integer> integers : finalResult) {
        System.out.println(integers.toString());
      }
      return;
    }

    List<Integer> singleResult = new ArrayList<>();
    List<TreeNode> nextLoop = new ArrayList<>();

    for (TreeNode treeNode : loopResult) {
      singleResult.add(treeNode.val);
      if (treeNode.left != null) {
        nextLoop.add(treeNode.left);
      }
      if (treeNode.right != null) {
        nextLoop.add(treeNode.right);
      }
    }

    finalResult.add(singleResult);

    scopePrintTree(nextLoop, finalResult);
  }

  // 从个位开始排到最后 按照当前位放桶 然后循环桶更新param即下次循环原数据 直至结束
  public static int[] radixSort(int[] params) {
    // 最大位长度为循环次数
    int asInt = Arrays.stream(params).max().getAsInt();
    int length = (asInt + "").length();

    // int[][] ints = new int[10][params.length];
    // init linked比Array好操作 不用维护index
    LinkedList[] linkedLists = new LinkedList[10];
    for (int i = 0; i < 10; i++) {
      linkedLists[i] = new LinkedList<Integer>();
    }

    for (int i = 0; i < length; i++) {

      Double pow = Math.pow(10, i);
      int i1 = pow.intValue();
      // classify
      for (int j = 0; j < params.length; j++) {
        int remainder = -1;
        if (i == 0) {
          remainder = params[j] % 10;
        } else {
          remainder = (params[j] / i1) % 10;
        }
        linkedLists[remainder].add(params[j]);
      }

      for (LinkedList linkedList : linkedLists) {

        System.out.print(linkedList.toString() + "|");
      }

      // collect
      int nextIndex = 0;
      for (int j = 0; j < 10; j++) {
        LinkedList<Integer> linkedList = linkedLists[j];
        while (linkedList.size() != 0) {
          int i2 = linkedList.pollFirst();
          params[nextIndex] = i2;
          nextIndex++;
        }
      }

      for (int param : params) {
        System.out.print(param + " ");

      }

      System.out.println("--------------------");

    }

    return params;

  }

  // print by queue or stack
  public static void printQueue(int[] params) {
    LinkedList<Integer> linkedList = new LinkedList<>();
    // queue
    // for (int i = 0; i < params.length; i++) {
    // if (linkedList.size() <3){
    // linkedList.addLast(params[i]);
    // }else {
    // linkedList.pollFirst();
    // linkedList.addLast(params[i]);
    // }
    // System.out.println(linkedList.toString());
    // }
    // stack
    for (int i = 0; i < params.length; i++) {
      if (linkedList.size() < 3) {
        linkedList.addLast(params[i]);
      } else {
        linkedList.pollLast();
        linkedList.addLast(params[i]);
      }
      System.out.println(linkedList.toString());
    }

  }

  // create double link implement by head insert and tail insert
  public static ListNode createDoubleHeadLink(int[] params) {
    ListNode result = new ListNode();
    if (params == null || params.length == 0) {
      return null;
    }
    result.val = params[0];
    ListNode loop = result;
    // tail insert
    for (int i = 1; i < params.length; i++) {
      ListNode temp = new ListNode();
      temp.val = params[i];

      loop.next = temp;
      temp.before = loop;

      loop = temp;
    }

    // head insert
    // for (int i = 1; i < params.length; i++) {
    // ListNode temp = new ListNode();
    // temp.val = params[i];
    //
    // temp.next = loop;
    // loop.before = temp;
    //
    // loop = temp;
    // }

    return result;


  }


  // 插入单向有序递增link
  public static ListNode insertOrderLink(ListNode root, ListNode key) {

    if (root == null) {
      return key;
    }
    int i = 0;

    // 虚构一个最前面的指针用于做插入 用于解决需要插入时 找不到前一个节点
    ListNode loop1 = new ListNode();
    loop1.next = root;
    ListNode loop2 = root;

    while (loop2 != null) {
      // 递增必须是小于时插入 防止同值错误
      if (loop2.val > key.val) {
        // 特殊处理头指针
        if (i == 0) {
          loop1.val = key.val;
          return loop1;
        }
        // 正常处理
        loop1.next = key;
        key.next = loop2;
        return root;
      }
      // 循环
      loop1 = loop1.next;
      loop2 = loop2.next;
      i++;
    }

    // 如果是最大值
    loop1.next = key;
    return root;
  }


  // 创建link
  public static ListNode createLinkBackInterpolation(int[] params, boolean flag) {
    ListNode result = new ListNode();
    if (params.length == 0) {
      return result;
    }
    result.val = params[0];

    ListNode loop = result;
    for (int i = 1; i < params.length; i++) {
      ListNode temp = new ListNode();
      temp.val = params[i];
      loop.next = temp;// 后插法
      loop = temp;
    }
    // 是否循环
    if (flag) {
      loop.next = result;
    }

    // for (int i = 1; i < params.length; i++) {
    // ListNode temp = new ListNode();
    // temp.val = params[i];
    // temp.next = loop;// 前插
    // loop = temp;
    // }

    return result;
  }


  // 合并区间 要善于利用while 还需要会处理特殊区间 能统一的逻辑 也要合并
  public static void foo(List<List<Integer>> param) {
    List<List<Integer>> result = new ArrayList<>();
    // 有无触发过合并
    boolean needRecursion = false;

    int i = 0;
    while (i < param.size()) {
      if (i == param.size() - 1) {
        result.add(param.get(i));
        i++;
        continue;
      }

      if (isIntersection(param.get(i), param.get(i + 1))) {
        List<Integer> integers = mergeInterval(param.get(i), param.get(i + 1));
        result.add(integers);
        i = i + 2;
        needRecursion = true;
        continue;
      }

      result.add(param.get(i));
      i++;
    }

    if (needRecursion) {
      foo(result);
    } else {
      System.out.println(result.toString());
    }
  }


  public static boolean isIntersection(List<Integer> param1, List<Integer> param2) {
    if (param1.get(1) >= param2.get(0)) {
      return true;
    }
    return false;
  }

  public static List<Integer> mergeInterval(List<Integer> param1, List<Integer> param2) {
    List<Integer> list = new ArrayList<>();
    list.add(param1.get(0));
    list.add(param2.get(1));
    return list;
  }


  // 无重复的最长子串长度 滑动窗口 左右维护
  public static int noRepeatZi(String param) {
    if (param == null || param.equals("")) {
      return 0;
    }
    HashMap<Character, Integer> map = new HashMap<>();
    int left = 0;
    int right = 0;
    int maxInt = 0;
    String result = "";
    while (right < param.length()) {
      char rightChar = param.charAt(right);
      right++;
      map.put(rightChar, map.getOrDefault(rightChar, 0) + 1);

      while (map.get(rightChar) > 1) {
        char leftChar = param.charAt(left);
        left++;
        map.put(leftChar, map.getOrDefault(leftChar, 0) - 1);
      }

      maxInt = Math.max(maxInt, right - left);
      // 如果要看结果是什么样子 就用这个
      if (right - left > maxInt) {
        result = param.substring(left, right);
      }
    }

    return maxInt;
  }


  public static TreeNode createTree() {
    /**
     *     12
     *    3   9
     *  1 2  5   8
     *         22
     *        33
    */
    TreeNode one = new TreeNode(1);
    TreeNode two = new TreeNode(2);
    TreeNode three = new TreeNode(3, one, two);

    TreeNode one2 = new TreeNode(5);

    TreeNode one4 = new TreeNode(33);
    TreeNode one3 = new TreeNode(22, one4, null);

    TreeNode two2 = new TreeNode(8, one3, null);
    TreeNode three2 = new TreeNode(9, one2, two2);


    TreeNode root = new TreeNode(12, three, three2);
    return root;
  }

  public static TreeNode createSearchTree() {
    /**
     *     12
     *    2     16
     *  1 3  15   18
     *           17
     */
    TreeNode one = new TreeNode(1);
    TreeNode two = new TreeNode(3);
    TreeNode three = new TreeNode(2, one, two);

    TreeNode one2 = new TreeNode(15);

    TreeNode one3 = new TreeNode(17);

    TreeNode two2 = new TreeNode(18, one3, null);
    TreeNode three2 = new TreeNode(16, one2, two2);


    TreeNode root = new TreeNode(12, three, three2);
    return root;
  }

  // 层次遍历 就是带上高度递归
  public static void levelPrint(TreeNode root, Map<Integer, List<Integer>> result, int height) {
    if (root == null) {
      return;
    }

    List<Integer> list = result.getOrDefault(height, new LinkedList<>());
    list.add(root.val);
    result.put(height, list);

    levelPrint(root.left, result, height + 1);
    levelPrint(root.right, result, height + 1);
  }

  // 如何遍历 取决于你的输出和递归的代码顺序
  public static void printTree(TreeNode root, int direction) {
    if (root == null) {
      return;
    }
    switch (direction) {
      case 1:
        System.out.println(root.val);
        printTree(root.left, 1);
        printTree(root.right, 1);
        break;
      case 2:
        printTree(root.left, 2);
        System.out.println(root.val);
        printTree(root.right, 2);
        break;
      case 3:
        printTree(root.left, 3);
        printTree(root.right, 3);
        System.out.println(root.val);
        break;
    }

  }


  // 新的在前 旧的在后 一个结果 一个记录先后顺序 大小为2
  public static void lru(List<String> inputStringQueue) {

    LinkedList<String> linkedList = new LinkedList<>();
    List<String> result = new ArrayList<>();
    for (String param : inputStringQueue) {
      if (result.size() <= 2) {
        result.add(param);
        linkedList.addFirst(param);
      } else {
        if (linkedList.contains(param)) {
          linkedList.remove(param);
          linkedList.addFirst(param);
        } else {
          String last = linkedList.pollLast();
          linkedList.addFirst(param);

          int changeIndex = result.indexOf(last);
          result.set(changeIndex, param);
        }
      }
    }

    System.out.println(result);

  }

  // 全排列 回溯
  public static void quanpailie(List<String> param, List<String> choose, List<String> result,
      int height) {
    if (param.size() == 0) {
      System.out.println(choose);
    }
    for (int i = 0; i < param.size(); i++) {
      String s = param.get(i);
      choose.add(s);

      List<String> list = new ArrayList<>();
      list.addAll(param);
      list.remove(s);

      quanpailie(list, choose, result, height + 1);

      choose.remove(s);
    }
  }

  // 二分找索引位置
  public static int twoF(int[] params, int key, int leftIndex, int rightIndex) {
    if (leftIndex > rightIndex) {
      return -1;
    }
    if (params.length == 0) {
      return -1;
    }
    // 递增
    // 靠前
    int midIndex = (leftIndex + rightIndex) / 2;
    if (params[midIndex] == key) {
      return midIndex;
    }
    if (params[midIndex] > key) {
      return twoF(params, key, 0, midIndex - 1);
    }
    if (params[midIndex] < key) {
      return twoF(params, key, midIndex + 1, rightIndex);
    }

    return -1;
  }

  // 快排 我们默认就是取第一个为空 从最后一个开始处理 以左为边界划分递归
  public static void quick(int[] params, int leftIndex, int rightIndex) {
    if (leftIndex >= rightIndex) {
      return;
    }

    int leftPoint = leftIndex;
    int rightPoint = rightIndex;

    int changeNumber = params[leftIndex];
    int direction = 0;

    while (leftPoint < rightPoint) {
      if (direction == 0) {

        if (params[rightPoint] >= changeNumber) {
          rightPoint--;
          continue;
        } else {
          params[leftPoint] = params[rightPoint];
          leftPoint++;
          direction = 1;
          continue;
        }
      }

      if (direction == 1) {

        if (params[leftPoint] <= changeNumber) {
          leftPoint++;
          continue;
        } else {
          params[rightPoint] = params[leftPoint];
          rightPoint--;
          direction = 0;
          continue;
        }
      }
    }
    params[leftPoint] = changeNumber;

    quick(params, 0, leftPoint - 1);
    quick(params, leftPoint + 1, rightIndex);
  }


  @Override
  public boolean equals(Object obj) {
    ZonedDateTime dateTime = ZonedDateTime.parse("1933-12-10T03:30:20+08:00",
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
    Date from = Date.from(dateTime.toInstant());
    // System.out.println(dateTime.toInstant());
    new ConcurrentHashMap<>();

    Calendar c = Calendar.getInstance();
    c.setTime(from);
    c.add(Calendar.DAY_OF_MONTH, 1);
    // System.out.println(c.toInstant());


    LocalDateTime localDateTime = LocalDateTime.ofInstant(c.toInstant(),
        ZoneId.of("Asia/Shanghai"));
    // System.out.println(localDateTime);
    ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Shanghai"));

    Date date = new Date(System.currentTimeMillis());
    Calendar instance = Calendar.getInstance();
    instance.setTime(date);
    instance.set(Calendar.SECOND, 0);

    long time = 10992979279872l;

    long l = time / 1024l / 1024l / 1024l;

    HashMap<Object, Object> map = new HashMap<>();
    map.put("a", 376655.2d);

    long a1 = MapUtils.getLongValue(map, "a");
    // System.out.println(a1);

    String json = """

        """;
    // JSONObject jsonObject = JSONObject.parseObject(json);

    Map<String, Object> result = new HashMap<>();
    result.put("a", 123);

    String jsonString = JSON.toJSONString(result);
    JSONObject jsonObject = JSONObject.parseObject(jsonString);
    return true;
  }
}


class TreeNode {
  int val;
  TreeNode left;
  TreeNode right;

  TreeNode() {
  }

  TreeNode(int val) {
    this.val = val;
  }

  TreeNode(int val, TreeNode left, TreeNode right) {
    this.val = val;
    this.left = left;
    this.right = right;
  }
}

class ListNode {
  int val;
  ListNode next;
  ListNode before;

  ListNode() {
  }

  ListNode(int val) {
    this.val = val;
  }

  ListNode(int val, ListNode next) {
    this.val = val;
    this.next = next;
  }
}

