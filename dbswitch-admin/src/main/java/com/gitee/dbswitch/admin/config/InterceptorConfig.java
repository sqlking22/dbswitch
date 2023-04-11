// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.config;

import com.gitee.dbswitch.admin.common.annotation.TokenCheck;
import com.gitee.dbswitch.admin.common.exception.DbswitchException;
import com.gitee.dbswitch.admin.common.response.ResultCode;
import com.gitee.dbswitch.admin.dao.SystemUserDAO;
import com.gitee.dbswitch.admin.entity.SystemUserEntity;
import com.gitee.dbswitch.admin.util.CacheUtils;
import com.gitee.dbswitch.admin.util.ServletUtils;
import com.gitee.dbswitch.admin.util.TokenUtils;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration("dbswitchInterceptorConfig")
public class InterceptorConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authenticationInterceptor())
        .addPathPatterns("/**");
  }

  @Bean
  public HandlerInterceptor authenticationInterceptor() {
    return new HandlerInterceptor() {

      @Resource
      private SystemUserDAO systemUserDAO;

      /**
       * 只拦截带有@TokenCheck注解且needCheck属性为true的接口
       */
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
          Object handler) {
        if (!(handler instanceof HandlerMethod)) {
          return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if (!method.isAnnotationPresent(TokenCheck.class)) {
          return true;
        }

        TokenCheck annotation = method.getAnnotation(TokenCheck.class);
        if (!annotation.needCheck()) {
          return true;
        }

        String accessToken = TokenUtils.getRequestToken(request);
        if (StringUtils.isEmpty(accessToken)) {
          throw new DbswitchException(ResultCode.ERROR_ACCESS_FORBIDDEN, "无Token认证失败，请登录");
        }

        Object cache = CacheUtils.get(accessToken);
        if (null == cache) {
          throw new DbswitchException(ResultCode.ERROR_ACCESS_FORBIDDEN, "token不存在或已经失效，请重新登录!");
        }

        //判断数据库中的User实体对象的有效性
        SystemUserEntity systemUserEntity = (SystemUserEntity) cache;
        SystemUserEntity user = systemUserDAO.findByUsername(systemUserEntity.getUsername());
        if (null == user) {
          throw new DbswitchException(ResultCode.ERROR_ACCESS_FORBIDDEN, "token所使用的认证用户不存在!");
        } else if (Boolean.TRUE.equals(user.getLocked())) {
          throw new DbswitchException(ResultCode.ERROR_ACCESS_FORBIDDEN, "token所使用的认证用户已经被锁定");
        }

        ServletUtils.getHttpServletRequest().setAttribute("username", user.getUsername());
        return true;
      }
    };
  }

}
