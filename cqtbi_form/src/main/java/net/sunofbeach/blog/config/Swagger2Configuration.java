package net.sunofbeach.blog.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class Swagger2Configuration {
    //版本
    public static final String VERSION = "1.0.0";

    /*
    * 门户api 接口前缀：portal
    * */
    @Bean
    public Docket portalApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(portalApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.sunofbeach.blog.controller.portal"))
                .paths(PathSelectors.any())
                .build()
                .groupName("前端门户");
    }

    private ApiInfo portalApiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档") //设置文档的标题
                .description("门户接口文档") //设置文档的描述
                .version(VERSION) //设置文档版本信息
                .build();
    }

    /*
    * 管理中心api 接口前缀：admin
    * */
    @Bean
    public Docket adminApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(adminApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.sunofbeach.blog.controller.admin"))
                .paths(PathSelectors.any())
                .build()
                .groupName("管理中心");
    }

    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档") //设置文档的标题
                .description("管理中心接口") //设置文档的描述
                .version(VERSION) //设置文档版本信息
                .build();
    }

    /*
     * 用户中心api 接口前缀：user
     * */
    @Bean
    public Docket UserApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(UserApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.sunofbeach.blog.controller.user"))
                .paths(PathSelectors.any())
                .build()
                .groupName("用户中心");
    }

    private ApiInfo UserApiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档") //设置文档的标题
                .description("用户接口文档") //设置文档的描述
                .version(VERSION) //设置文档版本信息
                .build();
    }
}
