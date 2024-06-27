package co.id.bcafinance.finalproject.configuration;

import co.id.bcafinance.finalproject.FinalprojectApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FinalprojectApplication.class);
    }
}
