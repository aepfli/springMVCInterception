# Spring MVC 

## Introduction

[SpringMVC](https://spring.io/guides/gs/serving-web-content/) is a powerful tool, and often used for displaying content in the Spring world. A lot of people use it, and write a lot of boilerplate code for handling exceptions or extending the information within the Model. This little Article should elaborate possible entry points, and mechanisms to keep Controller clean and remove as much boilerplatecode as possible.

### General

As the name "SpringMVC" already suggests, this framework is an implementation of the [Model-View-Controller-Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) from "Spring". It enables the segregation of business- and view-logic and the data. It is widely used, and fully integrated within the [Spring IoC](https://platform.spring.io/platform/).

SpringMVC is a good example of the [Open-Closed-Principle](https://en.wikipedia.org/wiki/Open/closed_principle), which allows you to easily add functionality or extend it. But strictly forbid modification. Based on this design aspect, SpringMVC offers a lot of entry points to modify the desired behavior. 

### Goal

A lot of people are actually using SpringMVC within their projects, but seldomly it is used to its full potential. A lot of projects handle exceptions within the controllers or do repetitive null checks, just to ensure the output is correct.

```
@Controller
@RequestMapping("/*")
public class MvcController {

    @Autowired
    Facade facade;

    @RequestMapping(value = "createUser", method = RequestMethod.GET)
    @ResponseBody
    public RESTDataWrapperDTO createUser(@RequestBody RESTCreateUserDTO createUserDTO) throws IOException {
        RESTDataWrapperDTO dataWrapper = new RESTDataWrapperDTO();
        try {
            RESTUserDTO dto = facade.create(createUserDTO);
            dataWrapper.setData(dto);
            dataWrapper.setSuccess(true);
        } catch (Exception e) {
            RESTErrorDetailsDTO errorDetails = new RESTErrorDetailsDTO();
            errorDetails.addError("ERROR", e.getMessage());
            dataWrapper.setErrorDetails(errorDetails);
            dataWrapper.setSuccess(false);
        }
        return dataWrapper;
    }

    @RequestMapping(value = "deleteUser", method = RequestMethod.POST)
    @ResponseBody
    public RESTDataWrapperDTO deleteUser(@RequestBody RESTUserDTO user) throws IOException {
        RESTDataWrapperDTO dataWrapper = new RESTDataWrapperDTO();
        try {
            boolean deleted = facade.delete(user.getUid());
            dataWrapper.setData(null);
            dataWrapper.setSuccess(deleted);
        } catch (Exception e) {
            RESTErrorDetailsDTO errorDetails = new RESTErrorDetailsDTO();
            errorDetails.addError("ERROR", e.getMessage());
            dataWrapper.setErrorDetails(errorDetails);
            dataWrapper.setSuccess(false);
        }
        return dataWrapper;
    }
}
```

SpringMVC (and the Servlet) offer mechanisms to intercept your request and response. This allows us to modify the output as we like it to be. Depending on the mechanism you choose you have different possibilities, but not all solutions cover the same use cases.

## Interception

### Overview of Mechanisms in small comparison

In this Atricle we will talk about three possible mechanisms to interfere with our request/response. They all have different characteristics and could be used for different behaviors. I will not dig deep into the configuration part, as Spring itself offers different ways to configure its ApplicationContext, eg. XML, Annotations, Classes. Therefore i will keep the configuration simple and generic, so it can be easier applied to all of the mentioned ways.

#### Filter

The [Filter](https://tomcat.apache.org/tomcat-5.5-doc/servletapi/javax/servlet/Filter.html) (javax.servlet.Filter) is - as you can see on the namespace - actually not a SpringMVC specific implementation. The Filter part of java's [Servlet API](https://tomcat.apache.org/tomcat-5.5-doc/servletapi/index.html) and allows basic interference of incoming and outgoing calls.

A Filter is the most powerful interception tool, as it is the only one which allows you the exchange the whole response and request object. Filters are called for every requests/response-pairs, so be aware what you are actually want to try to achieve with your implementation. Regular use cases are:

*   Logging
*   XSS Filtering
*   Cookie Handling

##### Implementation

The Filter interface consists of three methods: 

*   init
*   doFilter
*   destroy

`.init()` and `.destroy()` are pretty selfexplaining by the names, those are the methods which get called to initialize the Filter and also to destroy it. The important method for implementing a filter is the _.doFilter()_ method, which is your entry point.

Be aware that the Filter execution is part of a chain, so if you want to continue on the chain, you have to manually continue the [FilterChain](https://tomcat.apache.org/tomcat-5.5-doc/servletapi/javax/servlet/FilterChain.html).

```
public class DemoFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DemoFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("I am a filter!");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
```

##### Configuration

You can also specify for which routes a Filter can be applied with in the configuration. Normally you find the filter definition within the web.xml but with Spring you can also use the IoC component-scan to detect and add filters to the chain automatically.

#### HandlerInterceptor

To understand the [HandlerInterceptor](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/HandlerInterceptor.html), we need to take a close look at the general implementation of the SpringMVC framework and how it handles method calls. The Entrypoint for the SpringMVC framework is the [DispatcherServlet](https://stackoverflow.com/questions/2769467/what-is-dispatcher-servlet-in-spring), which will call a HandlerAdapter to invoke the "desired" method. The HandlerInterceptor actually allows us to interact with our "request":

*   before execution, 
*   after execution and
*   after completion (view gets rendered).

![springMVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/images/mvc-context-hierarchy.png)

##### Implementation

The API-Interface is pretty "generic" and is only containing an "Object" which you probably need to cast to the corresponding object. This handler-object normally contains all the informations you need, and you can check for Annotations etc. on the method you try to invoke or was invoked.

```
public class DemoInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(DemoInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // before execution of the mapper
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
        throws Exception {
    // after execution of the mapper
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            log.info("i am the interceptor and this is the called method '{}' of '{}' -"
                    + "my ModelAndView is '{}'",
                handlerMethod.getMethod().getName(),
                handlerMethod.getBeanType().getSimpleName(),
                modelAndView);
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    // after view got rendered
    }
}
```

#### ControllerAdvice

The third an easiest option to intercept is a [ControllerAdvice](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-controller-advice). A ControllerDevice is easily created, it is just a class which is annotated by [@ControllerAdvice](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html) and can contain methods with annotations of  [@ModelAttribute](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/ModelAttribute.html), [@ExceptionHandler](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/ExceptionHandler.html) and [@InitBinder.](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/InitBinder.html)

```
@ControllerAdvice
public class DemoAdvice {}
```

The usage of a ControllerAdvice can be limited by providing different field in the annotation.

```
// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = RestController.class)
public class AnnotationAdvice {}

// Target all Controllers within specific packages
@ControllerAdvice("org.example.controllers")
public class BasePackageAdvice {}

// Target all Controllers assignable to specific classes
@ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
public class AssignableTypesAdvice {}

(https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-controller-advice)

```

##### ModelAttribute

The ModelAttribute allows you to extend the ModelMap with additional attributes after execution. This could be a solution to add an API-Version to all requests or similar usage.

```
@ControllerAdvice
public class DemoAdvice {
    @ModelAttribute("injectedByAdvice")
    public String getInjectedByAdvice() {
        return "injectedByAdvice";
    }
}
```

This example will add the field "InjectedByAdvice" to the ModelMap. Be aware that this is only working for normal ModelAndView implementations. When you are using the @ResponseBody annotation, you can not intercept with a simple @ControllerAdvice and @ModelAttribute annoation, as the output at this stage is mostly written to the response already.

##### ExceptionHandler

One of the most useful usages for ControllerAdvices is to handle exceptions. It allows you to easily react to different type of exceptions, and manipulate the output. This can be also used to handle RestReponse methods. 

```
@ControllerAdvice
public class DemoExceptionHandlerAdvice {
    @ExceptionHandler(Exception.class)
    public String handler(DemoException e) {
        return "error";
    }
}
```

The exception handling with ControllerAdvice also lets you change the respones HTTP Status quite easily, you simply have to annotate the handler-method with [@ResponseStatus](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/ResponseStatus.html)().

```
@ExceptionHandler(Exception.class)
@ResponseStatus(HttpStatus.CONFLICT)
public String handler(DemoException e) {
    return "error";
}
```

##### ResponseBody and Advices

The [ResponseBody](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ResponseBody.html) annotation changes how you can interact with ControllerAdvices. The reason is, that normally the response is already written, when it reaches the Advice, and is only extended with the value. As the ResponseBody annotation will generate a "completed" response, it will not allow any modification with like the `@ModelAttribute` annoation. Therefore we need to extend the class [ResponseBodyAdvice](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/ResponseBodyAdvice.html)<T> which will provide as a method `.beforeBodyWrite()` which we can use to modify our output. This class is more powerful, than the normal `@ControllerAdvice`, as you can also check for null values, and adapt the responseStatus based on this informations.

```
@ControllerAdvice
public class RestDemoResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (body == null) {
            body = "NOOOOOO";
        }
        return body;
    }
}
```

## Conclusion

With this article we showed you different ways to modify your response in a generic way. This should help you to reduce the amount of boilerplate code, exception handling and null checks within simple controllers. In our Project we could reduce the line number of some Controllers to a third. 

I hope this little article/talk gave you an impression, about the powerful toolset SpringMVC actually is, and what you can do - I also want to encourage you, to take a closer look at all the refernce documentation we have at hand. As there is always something new to discover.
