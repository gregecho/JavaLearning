# Spring MVC 视图渲染 #

**Spring兼容很多视图引擎例如Velocity和Freemaker。并且Spring支持基于原生JSP的方案。下面我们会了解Spring如何利用不同的视图引擎来生成视图。**

-------

## 主要内容 ##
本文首先会介绍Spring从开始处理请求到生成视图，然后我们将会实现自定义的可以自行javascript代码的renderer。

## 正文 ##
### 1. Spring如何生成视图 ###
首先，我们回忆一下Spring如何处理request，如果想要深入了解请参考[DispatcherServlet生命周期](https://github.com/gregecho/JavaLearning/blob/master/Translation/%E8%AF%91%E6%96%87-Spring-DispatcherServlet-%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F.md)。Spring首先会找到需要处理该请求的controller，如果找到controller，Spring会执行该请求对应的方法，视图渲染会通过执行该方法来进行准备。在方法执行的最后，Spring会返回要使用的视图信息。这些信息可能是字符串(视图文件名称不包含扩展名)或者其他"viewable"对象(视图或者ModelAndView)，最后Spring会将Model值与视图结合最后生成页面。

通过上面我们可以看出Spring为视图渲染做了两件事：解析抽象视图名称和渲染最终页面。视图解析器(view resolvers)决定视图渲染应该使用哪一个模板。视图解析器(view resolvers)实现了org.springframework.web.servlet.ViewResolver接口，自定义了一个resolveViewName(String viewName, Locale locale)方法。由于这个方法，Spring可以创建一个为视图渲染负责的对象。org.springframework.web.servlet.view.UrlBasedViewResolver标准视图解析器会创建新的一个新的buildView的视图实例，如下：
````java
protected AbstractUrlBasedView buildView(String viewName) throws Exception {
  AbstractUrlBasedView view = (AbstractUrlBasedView) BeanUtils.instantiateClass(getViewClass());
  /**
    * This is very important place because it informs which is the path of template view file. 
    * Suppose given configuration:
    * - prefix (specified in this view resolver configuration) : /templates/
    * - suffix (specified in this view resolver configuration) : .jsp
    * - view name (returned dynamically by controller): helloWorld
    * Below setUrl method constructs following path: /templates/helloWorld.jsp. This path represents 
    * the file to render by the view after merging all Model attributes.
    */
  view.setUrl(getPrefix() + viewName + getSuffix());
  String contentType = getContentType();
  if (contentType != null) {
    view.setContentType(contentType);
  }
  view.setRequestContextAttribute(getRequestContextAttribute());
  view.setAttributesMap(getAttributesMap());
  if (this.exposePathVariables != null) {
    view.setExposePathVariables(exposePathVariables);
  }
  return view;
}
````

可能你已经注意到了我们前面所谈到的视图实例是Spring中第二重要概念。视图实例实现了org.springframework.web.servlet.View接口。该接口定义了两个方法：
>+ getContentType返回视图类型
>+ render(Map model, HttpServletRequest request, HttpServletResponse response)方法渲染最终页面返回给用户。


原文：
> [View rendering in Spring Web MVC][1]

[1]:http://www.waitingforcode.com/spring-web-mvc/view-rendering-in-spring-web-mvc/read

