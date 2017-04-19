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
>+ DisplatcherServlet使用请求映射来确定具体处理该request的controller。 _org.springframework.web.servlet.HandlerMapping_会返回一个 _org.springframework.web.servlet.HandlerExecutionChain_的实例。该实例包含了一组可以在controller调用前或调用后执行的拦截器。关于spring拦截器可以参考[Spring拦截器](http://www.waitingforcode.com/spring-framework/spring-dispatcherservlet-lifecycle/read#)
>+ 系统会执行根据配置文件找到的前拦截器及controller。controller处理完请求后，DisplatcherServlet会执行所有的后拦截器。最后DisplatcherServlet会接收到controller返回的ModelAndView实例。
>+ DisplatcherServlet将接收到的视图名称发送给视图解析器，视图解析器将决定客户端将看到的具体视图。
>+ 最后视图将以response的形式发挥到客户端。   

### 3. 什么是DisplatcherServlet ###   
### 3.1 策略初始化(Strategies initialization) ###
DispatcherServlet类位于org.springframework.web.servlet包中，并且实现了该包中FrameworkServlet抽象类。包含了一些私有字段例如，解析器（本地化，视图，异常和文件上传），handler映射（handler mapping）以及handler适配器。DispatcherServlet中关键的方法是initStrategies，该方法在onRefresh方法中调用。onRefresh方法在FrameworkServlet类中的initServletBean和initWebApplicationContext中调用。initServletBean产生了一个application级别的上下文以及所有提供的策略。每一个策略代表了可以被DispatcherServlet用来处理请求的对象。

````java
/**
 * Initialize the strategy objects that this servlet uses.
 * May be overridden in subclasses in order to initialize further strategy objects.
 */
protected void initStrategies(ApplicationContext context) {
    initMultipartResolver(context);
    initLocaleResolver(context);
    initThemeResolver(context);
    initHandlerMappings(context);
    initHandlerAdapters(context);
    initHandlerExceptionResolvers(context);
    initRequestToViewNameTranslator(context);
    initViewResolvers(context);
    initFlashMapManager(context);
}
````
**注意：** 如果一个strategy不存在，会抛出NoSuchBeanDefinitionException并且会使用默认的strategy来代替。如果默认的strategy在DispatcherServler.properties文件中没有定义，会抛出BeanInitializationException异常。
````java
/**
 * Return the default strategy object for the given strategy interface.
 * The default implementation delegates to {@link #getDefaultStrategies},
 * expecting a single object in the list.
 * @param context the current WebApplicationContext
 * @param strategyInterface the strategy interface
 * @return the corresponding strategy object
 * @see #getDefaultStrategies
 */
protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
    List<T> strategies = getDefaultStrategies(context, strategyInterface);
    if (strategies.size() != 1) {
        throw new BeanInitializationException(
                "DispatcherServlet needs exactly 1 strategy for interface [" + strategyInterface.getName() + "]");
    }
    return strategies.get(0);
}
````
### 3.2 请求处理(request handling) ###
DispatcherServlet继承关系如图所示：
![DispatcherServlet继承关系](/images/DispatcherServlet.jpg)
HttpServlet是一个处理不同请求类型的抽象类，可以处理：doGet (GET request), doPost (POST), doPut (PUT), doDelete (DELETE), doTrace (TRACE), doHead (HEAD), doOptions (OPTIONS)。FrameworkServlet重写了这些方法，将所有的请求分发到processRequest(HttpServletRequest request, HttpServletResponse response)方法。processRequest是protected和final级别的方法，它构造了LocaleContext 和 ServletRequestAttributes对象，这两个对象都可以从RequestContextHolder对象访问。
````java
@Override
protected final void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
 
    processRequest(request, response);
}
 
/**
 * Process this request, publishing an event regardless of the outcome.
 * The actual event handling is performed by the abstract
 * {@link #doService} template method.
 */
protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
 
    long startTime = System.currentTimeMillis();
    Throwable failureCause = null;
 
    LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
    LocaleContext localeContext = buildLocaleContext(request);
 
    RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
    ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);
 
    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
    asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());
 
    initContextHolders(request, localeContext, requestAttributes);
 
    try {
        doService(request, response);
    }
    catch (ServletException ex) {
        failureCause = ex;
        throw ex;
    }
    catch (IOException ex) {
        failureCause = ex;
        throw ex;
    }
    catch (Throwable ex) {
        failureCause = ex;
        throw new NestedServletException("Request processing failed", ex);
    }
 
    finally {
        resetContextHolders(request, previousLocaleContext, previousAttributes);
        if (requestAttributes != null) {
            requestAttributes.requestCompleted();
        }
 
        if (logger.isDebugEnabled()) {
            if (failureCause != null) {
                this.logger.debug("Could not complete request", failureCause);
            }
            else {
                if (asyncManager.isConcurrentHandlingStarted()) {
                    logger.debug("Leaving response open for concurrent processing");
                }
                else {
                    this.logger.debug("Successfully completed request");
                }
            }
        }
 
        publishRequestHandledEvent(request, startTime, failureCause);
    }
}
````
从processRequest的代码可以看出，在调用了initContextHolders方法后调用了doService方法。doService方法在请求中加入了额外的信息(flash maps, context information),然后调用protected void doDispatch(HttpServletRequest request, HttpServletResponse response)方法。

The most important part of doDispatch method is handler retrieving. doDispatch calls getHandler() method which analyzes processed requests and returns HandlerExecutionChain instance. This instance contains handler mapping and interceptors. The next thing done by DispatcherServlet is applying pre-handler interceptors (applyPreHandle()). If at least one of them returns false, the request processing stops. Otherwise, the servlet uses handler adapter associated to handler mapping to generate the view object.

doDispatch调用getHandler()方法分析请求并返回相应的处理器实例(HandlerExecutionChain)。处理器实例包含handler映射及拦截器。接着DispatcherServlet会调用已经注册的拦截器。如果任何一个拦截器返回false，那停止请求处理。如果拦截器调用成功，处理器会根据映射关系生成对应的view对象。
````java
/**
 * Process the actual dispatching to the handler.
 * The handler will be obtained by applying the servlet's HandlerMappings in order.
 * The HandlerAdapter will be obtained by querying the servlet's installed HandlerAdapters
 * to find the first that supports the handler class.
 * All HTTP methods are handled by this method. It's up to HandlerAdapters or handlers
 * themselves to decide which methods are acceptable.
 * @param request current HTTP request
 * @param response current HTTP response
 * @throws Exception in case of any kind of processing failure
 */
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    boolean multipartRequestParsed = false;
 
    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
 
    try {
        ModelAndView mv = null;
        Exception dispatchException = null;
 
        try {
            processedRequest = checkMultipart(request);
            multipartRequestParsed = processedRequest != request;
 
            // Determine handler for the current request.
            // 获取处理该请求的处理器
            mappedHandler = getHandler(processedRequest);
            if (mappedHandler == null || mappedHandler.getHandler() == null) {
                noHandlerFound(processedRequest, response);
                return;
            }
 
            // Determine handler adapter for the current request.
            // 确定该请求的适配器
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
 
            // Process last-modified header, if supported by the handler.
            String method = request.getMethod();
            boolean isGet = "GET".equals(method);
            if (isGet || "HEAD".equals(method)) {
                long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                if (logger.isDebugEnabled()) {
                    String requestUri = urlPathHelper.getRequestUri(request);
                    logger.debug("Last-Modified value for [" + requestUri + "] is: " + lastModified);
                }
                if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                    return;
                }
            }
 
            if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                return;
            }
 
            try {
                // Actually invoke the handler.
                mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
            }
            finally {
                if (asyncManager.isConcurrentHandlingStarted()) {
                    return;
                }
            }
 
            applyDefaultViewName(request, mv);
            mappedHandler.applyPostHandle(processedRequest, response, mv);
        }
        catch (Exception ex) {
            dispatchException = ex;
        }
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
    }
    catch (Exception ex) {
        triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
    }
    catch (Error err) {
        triggerAfterCompletionWithError(processedRequest, response, mappedHandler, err);
    }
    finally {
        if (asyncManager.isConcurrentHandlingStarted()) {
            // Instead of postHandle and afterCompletion
            mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
            return;
        }
        // Clean up any resources used by a multipart request.
        if (multipartRequestParsed) {
            cleanupMultipart(processedRequest);
        }
    }
}
````

### 3.3 视图解析(View resolving) ###
在获取到ModelAndView实例之后，doDispatch方法调用private void applyDefaultViewName(HttpServletRequest request, ModelAndView mv)方法来获取对应的视图名称。

最后会执行所有注册的后拦截器方法。

### 3.4 视图渲染(view rendering) ###
至此，servlet知道哪一个视图需要被渲染，视图将通过processDispatchResult方法进行最后的处理-视图渲染。

首先，processDispatchResult会检查传入的参数没有异常，如果有任何异常将会只想到错误页面。如果没有异常，该方法会检查ModelAndView实例，如果ModelAndView实例不是null，方法会调用render方法protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response)。

render方法会根据定义的视图策略寻找对应的视图类实例，视图类实例负责展示的内容。
````java
/**
 * Handle the result of handler selection and handler invocation, which is
 * either a ModelAndView or an Exception to be resolved to a ModelAndView.
 */
private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
        HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
 
    boolean errorView = false;
 
    if (exception != null) {
        if (exception instanceof ModelAndViewDefiningException) {
            logger.debug("ModelAndViewDefiningException encountered", exception);
            mv = ((ModelAndViewDefiningException) exception).getModelAndView();
        }
        else {
            Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
            mv = processHandlerException(request, response, handler, exception);
            errorView = (mv != null);
        }
    }
 
    // Did the handler return a view to render?
    if (mv != null && !mv.wasCleared()) {
        render(mv, request, response);
        if (errorView) {
            WebUtils.clearErrorRequestAttributes(request);
        }
    }
    else {
        if (logger.isDebugEnabled()) {
            logger.debug("Null ModelAndView returned to DispatcherServlet with name '" + getServletName() +
                    "': assuming HandlerAdapter completed request handling");
        }
    }
 
    if (WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
        // Concurrent handling started during a forward
        return;
    }
 
    if (mappedHandler != null) {
        mappedHandler.triggerAfterCompletion(request, response, null);
    }
}
 
/**
 * Render the given ModelAndView.
 * This is the last stage in handling a request. It may involve resolving the view by name.
 * @param mv the ModelAndView to render
 * @param request current HTTP servlet request
 * @param response current HTTP servlet response
 * @throws ServletException if view is missing or cannot be resolved
 * @throws Exception if there's a problem rendering the view
 */
protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
    // Determine locale for request and apply it to the response.
    Locale locale = this.localeResolver.resolveLocale(request);
    response.setLocale(locale);
 
    View view;
    if (mv.isReference()) {
        // We need to resolve the view name.
        view = resolveViewName(mv.getViewName(), mv.getModelInternal(), locale, request);
        if (view == null) {
            throw new ServletException(
                    "Could not resolve view with name '" + mv.getViewName() + "' in servlet with name '" +
                            getServletName() + "'");
        }
    }
    else {
        // No need to lookup: the ModelAndView object contains the actual View object.
        view = mv.getView();
        if (view == null) {
            throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
                    "View object in servlet with name '" + getServletName() + "'");
        }
    }
 
    // Delegate to the View object for rendering.
    if (logger.isDebugEnabled()) {
        logger.debug("Rendering view [" + view + "] in DispatcherServlet with name '" + getServletName() + "'");
    }
    try {
        view.render(mv.getModelInternal(), request, response);
    }
    catch (Exception ex) {
        if (logger.isDebugEnabled()) {
            logger.debug("Error rendering view [" + view + "] in DispatcherServlet with name '"
                    + getServletName() + "'", ex);
        }
        throw ex;
    }
}
````

### 4. 自定义DisplatcherServlet ###


原文：
> [Spring DispatcherServlet lifecycle][1]

[1]:http://www.waitingforcode.com/spring-framework/spring-dispatcherservlet-lifecycle/read

参考：
> [SpringMVC DispatcherServlet初始化过程][1]    

[1]: http://blog.csdn.net/tiantiandjava/article/details/47663853

### TODO
* [ ] 适配器模式在spring MVC中的运用--- HandlerAdapter
