# Spring 设计模式 #

> 设计模式有助于遵循良好的编程实践。Spring作为最流行的web框架之一也是用到了一些设计模式。
--------------------------

> 本系列的前5篇文章将会介绍spring框架中用到的设计模式。首先，我们会介绍Spring中用到的4种设计模式：解释器模式，建造者模式，工厂方法模式以及抽象工厂模式。最后，我们会实现一个简单的Spring框架。

## 主要内容 ##
> #### Spring设计模式-解释器模式（interpreter） ####
> #### 剖析Spring默认拦截器 ####
> #### 自定义拦截器 ####

## 正文 ##
### 1. Spring设计模式-解释器模式（interpreter） ###

解释器模式具体请参考[解释器模式][2]。

Spring主要在Spring表达式语言（Spring Expression Language）中使用解释器模式。SpEL语言表达是会被Spring框架的org.springframework.expression.ExpressionParser解析和执行。该实现会将SpEL表达式字符串转换为org.springframework.expression.Expression实例。上下文组件实现了org.springframework.expression.EvaluationContext接口，比如：StandardEvaluationContext。

SpEL示例代码如下所示：
````java
Writer writer = new Writer();
writer.setName("Writer's name");
StandardEvaluationContext modifierContext = new StandardEvaluationContext(subscriberContext);
modifierContext.setVariable("name", "Overriden writer's name");
parser.parseExpression("name = #name").getValue(modifierContext);
System.out.println("writer's name is : " + writer.getName());
````




### 2. Spring设计模式-建造者模式（builder） ###

### 3. Spring设计模式-工厂方法模式（factory method） ###
工厂方法模式具体请参考[工厂方法模式][3]。


````java
public interface HandlerInterceptor {

	/**
	 * Intercept the execution of a handler. Called after HandlerMapping determined
	 * an appropriate handler object, but before HandlerAdapter invokes the handler.
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * of any number of interceptors, with the handler itself at the end.
	 * With this method, each interceptor can decide to abort the execution chain,
	 * typically sending a HTTP error or writing a custom response.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return {@code true} if the execution chain should proceed with the
	 * next interceptor or the handler itself. Else, DispatcherServlet assumes
	 * that this interceptor has already dealt with the response itself.
	 * @throws Exception in case of errors
	 */
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	    throws Exception;

	/**
	 * Intercept the execution of a handler. Called after HandlerAdapter actually
	 * invoked the handler, but before the DispatcherServlet renders the view.
	 * Can expose additional model objects to the view via the given ModelAndView.
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * of any number of interceptors, with the handler itself at the end.
	 * With this method, each interceptor can post-process an execution,
	 * getting applied in inverse order of the execution chain.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started async
	 * execution, for type and/or instance examination
	 * @param modelAndView the {@code ModelAndView} that the handler returned
	 * (can also be {@code null})
	 * @throws Exception in case of errors
	 */
	void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception;

	/**
	 * Callback after completion of request processing, that is, after rendering
	 * the view. Will be called on any outcome of handler execution, thus allows
	 * for proper resource cleanup.
	 * <p>Note: Will only be called if this interceptor's {@code preHandle}
	 * method has successfully completed and returned {@code true}!
	 * <p>As with the {@code postHandle} method, the method will be invoked on each
	 * interceptor in the chain in reverse order, so the first interceptor will be
	 * the last to be invoked.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started async
	 * execution, for type and/or instance examination
	 * @param ex exception thrown on handler execution, if any
	 * @throws Exception in case of errors
	 */
	void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception;

}
````


#### 1.2 拦截器(interceptros)与过滤器(filters)区别 ####

如果拦截器与servlet中过滤器功能类似，为什么spring要重写Java默认的方案？拦截器与过滤器最主要的区别是scope(上下文)。过滤器仅能在servlet容器内使用，而拦截器可以在Spring容器中使用，而不局限于web环境。

正如前面所介绍的，Spring拦截器可以在请求处理之前及之后调用，并且也可以在视图render给用户之后调用。而过滤器只能在reponse返回到用户之前调用。

补充：
>+ 拦截器是基于java的反射机制的，而过滤器是基于函数回调。
>+ 拦截器不依赖与servlet容器，过滤器依赖与servlet容器。
>+ 拦截器只能对action请求起作用，而过滤器则可以对几乎所有的请求起作用。
>+ 拦截器可以访问action上下文、值栈里的对象，而过滤器不能访问。
>+ 在action的生命周期中，拦截器可以多次被调用，而过滤器只能在容器初始化时被调用一次。

### 2. 剖析Spring默认拦截器 ###
Spring中有两个默认的拦截器org.springframework.web.servlet.i18n.LocaleChangeInterceptor 及org.springframework.web.servlet.theme.ThemeChangeInterceptor.   



原文：
> [Handler interceptors in Spring][1]

[1]:http://www.waitingforcode.com/spring-framework/handler-interceptors-in-spring/read
[2]:https://github.com/gregecho/JavaDesignPattern/blob/master/Document/Design_Pattern_Part4_Behavioral_Pattern_Interpreter_Pattern.md
[3]:https://github.com/gregecho/JavaDesignPattern/blob/master/Document/Design_Pattern_Part2_Creational_Pattern_Factory_Pattern.md
