//package nl.fontys.config;
//
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
//import org.thymeleaf.spring5.SpringTemplateEngine;
//import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
//import org.thymeleaf.spring5.view.ThymeleafViewResolver;
//import org.thymeleaf.templatemode.TemplateMode;
//import org.thymeleaf.templateresolver.UrlTemplateResolver;
//
//@Configuration
//public class ThymeleafConfig implements ApplicationContextAware {
//    private ApplicationContext applicationContext;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//
//    @Bean
//    public SpringResourceTemplateResolver templateResolver(){
//        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
//        templateResolver.setApplicationContext(this.applicationContext);
//        templateResolver.setPrefix("/resources/templates/");
//        templateResolver.setSuffix(".html");
//
//        templateResolver.setTemplateMode(TemplateMode.HTML);
//        templateResolver.setCacheable(true);
//
//        return templateResolver;
//    }
//
//    @Bean
//    public SpringTemplateEngine templateEngine(){
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
////        templateEngine.addTemplateResolver(templateResolver());
//        templateEngine.addTemplateResolver(new UrlTemplateResolver());
//
//        templateEngine.setEnableSpringELCompiler(true);
//        templateEngine.addDialect(new SpringSecurityDialect());
//
//        return templateEngine;
//    }
//
//    @Bean
//    public ThymeleafViewResolver viewResolver(){
//        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
//        viewResolver.setTemplateEngine(templateEngine());
//        return viewResolver;
//    }
//}
