package com.machloop.fpc;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DslParse {

  public static void main(String[] args) {

    // String param = "(((a=1) AND (b!=2)) OR ((a2=1) AND (b2!=2)) )OR (c >=3) AND (d < 4)";
    String param = "(a=1) AND (b=2) OR (( (c>2) AND (d=3)  )  AND ( (c>2) AND (d=3)  )  )";

    SingleNode singleNode = dsl2Tree(param);
    System.out.println(Tree2Dsl(singleNode, true));
  }

  /** 示例
   *     将DSL字符串  (((a=1) AND (b!=2)) OR ((a2=1) AND (b2!=2)) ) OR (c >=3) AND (d < 4) 解析为树形结构
   *  level 1                                  root
   *  level 2                     node                                        OR       c>=3        AND  d < 4
   *  level 3        node                OR               node
   *  level 4 a=1    AND     b!=2                   a2=1    AND      b2!=2
   *
   */
  public static SingleNode dsl2Tree(String param) {
    // 保存数据结构
    // 括号栈
    LinkedList<Character> bracketStack = new LinkedList<>();
    // 整体层级信息
    HashMap<Integer, List<SingleNode>> levelMap = new HashMap<>();
    // 层级父节点信息
    HashMap<Integer, FatherInfo> fatherMap = new HashMap<>();

    // 初始化
    SingleNode currentNode = new SingleNode();
    int currentLevel = 1;
    List<SingleNode> levelList = levelMap.getOrDefault(currentLevel, new ArrayList<>());
    levelList.add(currentNode);
    levelMap.put(currentLevel, levelList);
    bracketStack.add('(');

    SingleNode root = new SingleNode();
    fatherMap.put(1, new FatherInfo(root, false));
    currentNode.father = root;
    root.sons.add(currentNode);

    for (int i = currentLevel + 1; i <= 5; i++) {
      fatherMap.put(i, new FatherInfo(null, false));
    }

    String value = "";

    // 上一次识别到的括号以这个为同级判断
    Character lastBracket = '(';

    for (int i = 1; i < param.length(); i++) {
      char currentCharacter = param.charAt(i);
      Character topBracket = bracketStack.peekLast();
      switch (currentCharacter) {
        case '(':
          bracketStack.add(currentCharacter);
          if (lastBracket == '(') {
            // 需要到下一级
            currentLevel++;
            SingleNode fatherNode = currentNode;
            currentNode = new SingleNode();
            currentNode.father = fatherNode;
            fatherNode.sons.add(currentNode);
            fatherMap.put(currentLevel, new FatherInfo(fatherNode, true));

            List<SingleNode> tempList = levelMap.getOrDefault(currentLevel, new ArrayList<>());
            tempList.add(currentNode);
            levelMap.put(currentLevel, tempList);
          } else {
            // 同级
            currentNode = new SingleNode();
            currentNode.father = fatherMap.get(currentLevel).currFather;
            // 顶层父为null
            if (fatherMap.get(currentLevel).currFather != null) {
              fatherMap.get(currentLevel).currFather.sons.add(currentNode);
            }
            List<SingleNode> tempList = levelMap.getOrDefault(currentLevel, new ArrayList<>());
            tempList.add(currentNode);
            levelMap.put(currentLevel, tempList);
          }
          lastBracket = currentCharacter;
          break;
        case ')':
          if (StringUtils.isNotBlank(value)) {
            currentNode.value = value;
          }
          value = "";

          if (lastBracket == ')') {
            fatherMap.put(currentLevel, new FatherInfo(null, false));
            currentLevel--;
          }

          lastBracket = currentCharacter;
          break;
        case 'A':
          fatherMap.get(currentLevel).currFather.sons.add(new SingleNodeRelation("AND"));
          i = i + 3;
          break;
        case 'O':
          fatherMap.get(currentLevel).currFather.sons.add(new SingleNodeRelation("OR"));
          i = i + 2;
          break;
        default:
          // 遇到值
          if (currentCharacter != ' ') {
            value += currentCharacter;
          }
          break;
      }
    }

    return root;
  }

  /**
   * 
   * @param root
   * @param outermostLayer 是否为最外层 最外层不再加括号
   * @return
   */
  public static String Tree2Dsl(SingleNode root, boolean outermostLayer) {
    String result = "";
    if (root == null) {
      return result;
    }

    if (root.sons.size() == 0) {
      return "(" + root.value + ")";
    }
    if (!outermostLayer) {
      result += "(";
    }

    for (SingleNode node : root.sons) {
      if (node instanceof SingleNodeRelation) {
        result += " " + ((SingleNodeRelation) node).relation + " ";
      } else {
        result += " " + Tree2Dsl(node, false) + " ";
      }
    }

    if (!outermostLayer) {
      result += ")";
    }

    return result;
  }
}

class SingleNode {
  public List<SingleNode> sons = new ArrayList<>();
  public SingleNode father;
  public String value;
}

class SingleNodeRelation extends SingleNode {
  public String relation;

  public SingleNodeRelation(String relation) {
    this.relation = relation;
  }
}

class FatherInfo {
  public SingleNode currFather;
  public boolean isContinue;

  public FatherInfo(SingleNode currFather, boolean isContinue) {
    this.currFather = currFather;
    this.isContinue = isContinue;
  }
}
