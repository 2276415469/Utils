# Utils
三个文件，对应json处理，自定义语言处理、算法回顾。

DslParse文件，为领域特定语言解析器。
你应该在有如下需求的时候使用它

1、你需要自定义一套过滤规则，同时你的规则很复杂，会需要层层嵌套。

2、你并不是这一套DSL的唯一受益者，或者说你有时会面向toB进行一些对接。

3、你有语法解析需求，但是现在框架或者轮子都太过沉重，你需要一个简单轻便的解析器，这个文件可以作为你很好的基础底盘。

举例：

   将DSL字符串  (((a=1) AND (b!=2)) OR ((a2=1) AND (b2!=2)) ) OR (c >=3) AND (d < 4) 解析为树形结构
   
   level 1                                  root
   
   level 2                     node                                        OR       c>=3        AND  d < 4
   
   level 3        node                OR               node
   
   level 4 a=1    AND     b!=2                   a2=1    AND      b2!=2

JsonUtils文件为json串操作文件，帮助你快速操作json。

他提供如下使用功能

根据两个list生成你需要的复杂结构

{
    "a":{
        "b":[]
    },
    "c":"",
    "d":[
        {},{
            "e":{}
        }
    ]
}

List<String> paramList = Lists.newArrayList("a", "a.b","c", "d", "d.[1]#e.");

List<String> paramTypeList = Lists.newArrayList(JsonUtils.JSON_OBJEC, JsonUtils.JSON_ARRAY,JsonUtils.JSON_String, JsonUtils.JSON_ARRAY, JsonUtils.JSON_OBJEC);

存取特定位置，支持准确匹配，模糊匹配，空值默认值设置。类型转换。结构修改（任意增加删除节点），按照类型获取结果。
如
1、赋值a.b位置为字符串

2、在d的数组中增加对象

3、设置a.f的类型如String，如不存在会自动添加，如果没有初始值，使用默认值

4、任意转换获取结果只需调用时增加类型后缀

5、需要设置key为G的值但是不知道结构在哪里，可以直接赋值，方法会自动找到key所在位置并赋值

Algorithm文件为纯算法文件

为避免大家还要去登录特定网站，或者自己找寻联系题目，所创建。

里面包括了基本上所有的类型题，解题方式，不夸张地说这个文件过一遍，OC应该是轻轻松松的，因为这个文件中的题目本就在面试难度之上。

