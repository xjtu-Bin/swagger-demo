# Swagger + Mybatis-plus

* API框架——简化前后端集成联调。

- Restful Api 文档在线自动生成器 => **API 文档 与API 定义同步更新**
- 直接运行，在线测试API
- 支持多种语言 （如：Java，PHP等）
- 官网：https://swagger.io/

> ### SpringBoot集成Swagger

1: 新建项目，导入Meaven依赖

~~~xml
 			<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger2 -->
            <dependency>
                <groupId>io.springfox</groupId>
                <artifactId>springfox-swagger2</artifactId>
                <version>2.9.2</version>
            </dependency>
            <!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui -->
            <dependency>
                <groupId>io.springfox</groupId>
                <artifactId>springfox-swagger-ui</artifactId>
                <version>2.9.2</version>
            </dependency>
~~~

2: 编写HelloController，测试确保运行成功！

3: 创建config包，创建SwaggerConfigl类

~~~java
package com.gf.swaggerdemo.config;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

}
~~~

4:访问测试 ：http://localhost:8080/swagger-ui.html ，可以看到swagger的界面。

**如果报空指针异常是因为应该是SpringBoot2.6.0和Swagger2.9.2不兼容。**

![image-20220511174143883](C:\Users\lb\AppData\Roaming\Typora\typora-user-images\image-20220511174143883.png)

> ### 配置Swagger

Swagger实例Bean是Docket，所以通过配置Docket实例来配置Swaggger。

~~~java
package com.gf.swaggerdemo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
    }

    //配置文档信息
    private ApiInfo apiInfo() {
        Contact contact = new Contact("刘斌", "http://xxx.xxx.com/联系人访问链接", "303862074@qq.com");
        return new ApiInfo(
                "Swagger学习", // 标题
                "学习演示如何配置Swagger", // 描述
                "v1.0", // 版本
                "http://terms.service.url/组织链接", // 组织链接
                contact, // 联系人信息
                "Apach 2.0 许可", // 许可
                "许可链接", // 许可连接
                new ArrayList<>()// 扩展
        );
    }
}
~~~

![image-20220511175318248](C:\Users\lb\AppData\Roaming\Typora\typora-user-images\image-20220511175318248.png)

> ### 配置扫描接口

项目中会有很多接口，怎么配置让Swagger扫描到指定接口呢？

1、构建Docket时通过select()方法配置怎么扫描接口。

~~~java
@Bean
public Docket docket() {
    return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()// 通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
            .apis(RequestHandlerSelectors.basePackage("com.gf.swaggerdemo.controller"))
            .build();
    }
~~~

2、除了通过包路径配置扫描接口外，还可以通过配置其他方式扫描接口，这里注释一下所有的配置方式：

~~~java
any() // 扫描所有，项目中的所有接口都会被扫描到
none() // 不扫描接口
// 通过方法上的注解扫描，如withMethodAnnotation(GetMapping.class)只扫描get请求
withMethodAnnotation(final Class<? extends Annotation> annotation)
// 通过类上的注解扫描，如.withClassAnnotation(Controller.class)只扫描有controller注解的类中的接口
withClassAnnotation(final Class<? extends Annotation> annotation)
basePackage(final String basePackage) // 根据包路径扫描接口
~~~

3、除此之外，我们还可以配置接口扫描过滤：

~~~java
@Bean
public Docket docket() {
   return new Docket(DocumentationType.SWAGGER_2)
      .apiInfo(apiInfo())
      .select()// 通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
      .apis(RequestHandlerSelectors.basePackage("com.kuang.swagger.controller"))
       // 配置如何通过path过滤,即这里只扫描请求以/kuang开头的接口
      .paths(PathSelectors.ant("/kuang/**"))
      .build();
}
~~~

4、这里的可选值还有

~~~java
any() // 任何请求都扫描
none() // 任何请求都不扫描
regex(final String pathRegex) // 通过正则表达式控制
ant(final String antPattern) // 通过ant()控制
~~~

> ### 配置Swagger开关

因为项目开发时存在生产环境和上线环境环境，怎么配置Swagger在指定环境生效呢？

~~~java
@Bean
public Docket docket(Environment environment) {
    // 设置要显示swagger的环境
    Profiles of = Profiles.of("dev");
    // 判断当前是否处于该环境
    // 通过 enable() 接收此参数判断是否要显示
    boolean b = environment.acceptsProfiles(of);
    return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .enable(b)
            .select()// 通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
            .apis(RequestHandlerSelectors.basePackage("com.gf.swaggerdemo.controller"))
            .build();
    }
~~~

> ### 配置API分组

意思就是可以配置多个docket对象

![image-20220511221803943](C:\Users\lb\AppData\Roaming\Typora\typora-user-images\image-20220511221803943.png)

~~~java
@Bean
public Docket docket1(){
    return new Docket(DocumentationType.SWAGGER_2).groupName("group1");
}
~~~

> ### 实体配置

只要这个实体在**请求接口**的返回值上（即使是泛型），都能映射到实体项中：

@ApiModel为类添加注释

@ApiModelProperty为类属性添加注释

> ### 常用注解

Swagger的所有注解定义在io.swagger.annotations包下

| @Api(tags = "xxx模块说明")                             | 作用在模块类上                                       |
| ------------------------------------------------------ | ---------------------------------------------------- |
| @ApiOperation("xxx接口说明")                           | 作用在接口方法上                                     |
| @ApiModel("xxxPOJO说明")                               | 作用在模型类上：如VO、BO                             |
| @ApiModelProperty(value = "xxx属性说明",hidden = true) | 作用在类方法和属性上，hidden设置为true可以隐藏该属性 |
| @ApiParam("xxx参数说明")                               | 作用在参数、方法和字段上，类似@ApiModelProperty      |

总结：

1. 可以通过Swagger给一些比较难理解的属性或者接口，增加注释信息。

2. 接口文档实时更新。
3. 可以在线测试。

注意正式发布关闭Swagger，节省内存。

> Mybatis-plus自动生成Swagger只需要在全局配置上开启Swagger

~~~java
//实体属性 Swagger2 注解
gc.setSwagger2(true);
~~~

