package com.chs.cafeapp.global.config.paging;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    SortHandlerMethodArgumentResolver sortArgumentResolver = new SortHandlerMethodArgumentResolver();
    sortArgumentResolver.setSortParameter("sortBy");
    sortArgumentResolver.setPropertyDelimiter("-");

    PageableHandlerMethodArgumentResolver pageableArgumentResolver = new PageableHandlerMethodArgumentResolver(sortArgumentResolver);
    pageableArgumentResolver.setOneIndexedParameters(true);
    pageableArgumentResolver.setMaxPageSize(500);
    pageableArgumentResolver.setFallbackPageable(PageRequest.of(0,10));
    argumentResolvers.add(pageableArgumentResolver);
  }
}
