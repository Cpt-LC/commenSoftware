//package com.lianzheng.h5.common;
//
//import com.lianzheng.h5.annotation.TokenIntercept;
//import com.lianzheng.h5.util.ResponseUtil;
//import com.lianzheng.h5.util.TextUtil;
//import com.lianzheng.h5.util.TokenUtil;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.lang.Nullable;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
////声明这是一个配置
//@Configuration
//public class GlobalTokenInterceptor implements WebMvcConfigurer {
//    //  写/**表示所有
//    private final String[] myAddPathPatterns = {
//            "/**",
//    };
//
//    private final String[] myExcludePathPatterns = {
//            "/swagger**/**", "/webjars/**", "/v2/**", "/doc.html", "/zsh/**",
//    };
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        HandlerInterceptor inter = new HandlerInterceptor() {
//            //在业务处理器处理请求之前被调用。预处理，可以进行编码、安全控制、权限校验等处理；
//            @Override
//            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//                String currentRequestToken = request.getHeader("token");
//                String platformName = request.getHeader("platformName");
//
//                ///判断是否一定需要Token.
//                if (handler instanceof HandlerMethod) {
//                    HandlerMethod methodHandler = ((HandlerMethod) handler);
//                    boolean isNeedAppToken = methodHandler.hasMethodAnnotation(TokenIntercept.class);
//
//                    if (isNeedAppToken) {
//                        if (TextUtil.isEmpty(currentRequestToken)) {
//                            ResponseUtil.renderJson(response, ApiResult.errorMsg(ApiResult.TOKEN_ERROR, "token有误,请检查"));
//                            return false;
//                        }
//                        boolean isPassed = isTokenSecurityVerificationPassed(currentRequestToken, platformName, response);
//                        if (!isPassed) return false;
//                    }
//                }
//
//                ///当存在Token时,解析Token
//                if (TextUtil.isNoEmpty(currentRequestToken) && !"null".equals(currentRequestToken)) {
//                    boolean isPassed = isTokenSecurityVerificationPassed(currentRequestToken, platformName, response);
//                    if (!isPassed) return false;
//                }
//
//                System.out.println();
//                return true;
//            }
//
//            //在业务处理器处理请求执行完成后，生成视图之前执行。后处理（调用了Service并返回ModelAndView，但未进行页面渲染），有机会修改ModelAndView
//            @Override
//            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                                   @Nullable ModelAndView modelAndView)
//                    throws Exception {
//            }
//
//            //在DispatcherServlet完全处理完请求后被调用，可用于清理资源等。返回处理（已经渲染了页面）；
//            @Override
//            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
//                                        @Nullable Exception ex)
//                    throws Exception {
//
//            }
//        };
////        addPathPatterns   添加需要拦截的命名空间；
////        excludePathPatterns   添加排除拦截命名空间
//        registry.addInterceptor(inter)
//                .addPathPatterns(myAddPathPatterns)
//                .excludePathPatterns(myExcludePathPatterns);
//    }
//
//    ///是否token验证通过.
//    boolean isTokenSecurityVerificationPassed(String token, String platformName, HttpServletResponse response) throws IOException {
//        boolean isDated = TokenUtil.isTokenDated(token);
//        if (isDated) {
//            ResponseUtil.renderJson(response, ApiResult.errorMsg(ApiResult.TOKEN_ERROR, "token过期,请重新登录"));
//            return false;
//        }
//        String uuid = TokenUtil.getTokenUserId(token);
//        if (uuid == null) {
//            ResponseUtil.renderJson(response, ApiResult.errorMsg(ApiResult.TOKEN_ERROR, "token有误,请检查"));
//            return false;
//        }
//
////        if ("android".equals(platformName) || "ios".equals(platformName)) {
////
////        }
//        return true;
//    }
//}
