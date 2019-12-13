package com.emoke.core.dto.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dto {
  /**
   * 对应class类
   * */
  Class<?> dtoClass();

  /**目标class对应的属性名称*/
  String name();

  /**如果目标属性是对象，则需要配置该值*/
  String field() default "";
}