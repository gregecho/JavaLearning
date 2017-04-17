# Spring DispatcherServlet 生命周期 #
> DispatcherServlet是Spring web框架中最重要的组件，这篇文章将会详细讲解DispatcherServlet。

## 主要内容 ##
> #### 前端控制器模式（front controller pattern) ####
> #### DisplatcherServlet执行过程 ####
> #### 讲解DisplatcherServlet源码 ####
> #### 自定义DisplatcherServlet ####

## 正文 ##
### 1. 前端控制器模式（front controller pattern) ###
在学习DispatcherServlet之前，我们需要了解一些它所基于的机制的理论知识。DispatcherServlet所基于的最关键的机制就是前端控制器模式。

前端控制器模式为web程序提供了一个统一的入口。该入口可以统一处理例如：资源安全，语言切换，session管理，缓存等功能。

因此，更加技术的来讲，前端控制器模式会处理所有的request请求，每个请求会被分析确定那个控制器和方法来进行处理。

5个重要的部分组成了前端控制器模式：
>+ 客户端：发送请求
>+ 控制器: 应用程序的入口，处理所有请求
>+ 分发器(dispatcher): 确定将那个试图返回到客户端
>+ 视图：需要返回到客户端的内容
>+ helper: 帮助视图或者控制器完成请求的处理

### 2. DisplatcherServlet _执行过程_ ###
从前一章节可以看到，前端控制器模式有自己的执行过程，负责处理请求并将视图返回到客户端：
>+ 客户端发送请求，请求会首先到达spring默认的控制器DisplatcherServlet。
>+ 
>+
>+    

他有着足以让你跪拜的人生经历：    
+ **14岁**参与RSS 1.0规格标准的制订。     
+ **2004**年入读**斯坦福**，之后退学。   
+ **2005**年创建[Infogami](http://infogami.org/)，之后与[Reddit](http://www.reddit.com/)合并成为其合伙人。   
+ **2010**年创立求进会（Demand Progress），积极参与禁止网络盗版法案（SOPA）活动，最终该提案**居然**被撤回。   
+ **2011**年7月19日，因被控从MIT和JSTOR下载480万篇学术论文并以免费形式上传于网络被捕。     
+ **2013**年1月自杀身亡。    

![Aaron Swartz](https://github.com/younghz/Markdown/raw/master/resource/Aaron_Swartz.jpg) 

天才都有早逝的归途（又是一位**犹太人**）。

### 3. _为什么_要使用它？ ###
+ 它是易读（_看起开舒服_）、易写（_语法简单_）、易更改**纯文本**。处处体现着**极简主义**的影子。
+ 兼容HTML，可以转换为HTML格式发布。
+ 跨平台使用。
+ 越来越多的网站支持Markdown。
+ 更方便清晰的组织你的电子邮件。（Markdown-here, Airmail）
+ 摆脱Word（我不是认真的）。
 
### 4. _怎么_使用？ ###
如果不算**扩展**，Markdown的语法绝对**简单**到让你爱不释手。

废话太多，下面正文，Markdown语法主要分为如下几大部分：
**标题**，**段落**，**区块引用**，**代码区块**，**强调**，**列表**，**分割线**，**链接**，**图片**，**反斜杠 `\`**，**符号'`'**。

#### 4.1 标题 ####
两种形式：  
1）使用`=`和`-`标记一级和二级标题。
> 一级标题   
> `=========`   
> 二级标题    
> `---------`
  
效果：
> 一级标题   
> =========   
> 二级标题
> ---------  

2）使用`#`，可表示1-6级标题。
> \# 一级标题   
> \## 二级标题   
> \### 三级标题   
> \#### 四级标题   
> \##### 五级标题   
> \###### 六级标题    

效果：
> # 一级标题   
> ## 二级标题   
> ### 三级标题   
> #### 四级标题   
> ##### 五级标题   
> ###### 六级标题 

#### 4.2 段落 ####
段落的前后要有空行，所谓的空行是指没有文字内容。若想在段内强制换行的方式是使用**两个以上**空格加上回车（引用中换行省略回车）。

#### 4.3 区块引用 ####
在段落的每行或者只在第一行使用符号`>`,还可使用多个嵌套引用，如：
> \> 区块引用  
> \>> 嵌套引用  

效果：
> 区块引用  
>> 嵌套引用 

#### 4.4 代码区块 ####
代码区块的建立是在每行加上4个空格或者一个制表符（如同写代码一样）。如    
普通段落：

void main()    
{    
    printf("Hello, Markdown.");    
}    

代码区块：

    void main()
    {
        printf("Hello, Markdown.");
    }

**注意**:需要和普通段落之间存在空行。

#### 4.5 强调 ####
在强调内容两侧分别加上`*`或者`_`，如：
> \*斜体\*，\_斜体\_    
> \*\*粗体\*\*，\_\_粗体\_\_

效果：
> *斜体*，_斜体_    
> **粗体**，__粗体__

#### 4.6 列表 ####
使用`·`、`+`、或`-`标记无序列表，如：
> \-（+\*） 第一项
> \-（+\*） 第二项
> \- （+\*）第三项

**注意**：标记后面最少有一个_空格_或_制表符_。若不在引用区块中，必须和前方段落之间存在空行。

效果：
> + 第一项
> + 第二项
> + 第三项

有序列表的标记方式是将上述的符号换成数字,并辅以`.`，如：
> 1 . 第一项   
> 2 . 第二项    
> 3 . 第三项    

效果：
> 1. 第一项
> 2. 第二项
> 3. 第三项

#### 4.7 分割线 ####
分割线最常使用就是三个或以上`*`，还可以使用`-`和`_`。

#### 4.8 链接 ####
链接可以由两种形式生成：**行内式**和**参考式**。    
**行内式**：
> \[younghz的Markdown库\]\(https:://github.com/younghz/Markdown "Markdown"\)。

效果：
> [younghz的Markdown库](https:://github.com/younghz/Markdown "Markdown")。

**参考式**：
> \[younghz的Markdown库1\]\[1\]    
> \[younghz的Markdown库2\]\[2\]    
> \[1\]:https:://github.com/younghz/Markdown "Markdown"    
> \[2\]:https:://github.com/younghz/Markdown "Markdown"    

效果：
> [younghz的Markdown库1][1]    
> [younghz的Markdown库2][2]

[1]: https:://github.com/younghz/Markdown "Markdown"
[2]: https:://github.com/younghz/Markdown "Markdown"

**注意**：上述的`[1]:https:://github.com/younghz/Markdown "Markdown"`不出现在区块中。

#### 4.9 图片 ####
添加图片的形式和链接相似，只需在链接的基础上前方加一个`！`。
#### 4.10 反斜杠`\` ####
相当于**反转义**作用。使符号成为普通符号。
#### 4.11 符号'`' ####
起到标记作用。如：
>\`ctrl+a\`

效果：
>`ctrl+a`    

#### 5. 都_谁_在用？####
Markdown的使用者：
+ GitHub
+ 简书
+ Stack Overflow
+ Apollo
+ Moodle
+ Reddit
+ 等等

#### 6. 感觉有意思？趁热打铁，推荐几个_工具_。 ####
+ **Chrome**下的stackedit插件可以离线使用，很爽。也不用担心平台受限。
在线的dillinger.io算是评价好的了，可是不能离线使用。    
+ **Windowns**下的MarkdownPad也用过，不过免费版的体验不是很好。    
+ **Mac**下的Mou是国人贡献的，口碑很好。推荐。    
+ **Linux**下的ReText不错。    

**其实在对语法了如于心的话，直接用编辑器就可以了，脑子里满满的都是格式化好的文本啊。**
我现在使用`马克飞象` + `Markdown-here`，先编辑好，然后一键格式化，挺方便。

****
**注意**：不同的Markdown解释器或工具对相应语法（扩展语法）的解释效果不尽相同，具体可参见工具的使用说明。
虽然有人想出面搞一个所谓的标准化的Markdown，[没想到还惹怒了健在的创始人John Gruber]
(http://blog.codinghorror.com/standard-markdown-is-now-common-markdown/)。
****
以上基本是所有traditonal markdown的语法。

### 其它： ###
列表的使用(非traditonal markdown)：

用`|`表示表格纵向边界，表头和表内容用`-`隔开，并可用`:`进行对齐设置，两边都有`:`则表示居中，若不加`:`则默认左对齐。

|代码库                              |链接                                |
|:------------------------------------:|------------------------------------|
|MarkDown                              |[https://github.com/younghz/Markdown](https://github.com/younghz/Markdown "Markdown")|
|moos-young                            |[https://github.com/younghz/moos-young](https://github.com/younghz/moos-young "tianchi")|

关于其它扩展语法可参见具体工具的使用说明。
