package org.traktor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;

@SpringBootApplication
@Controller		
@EnableWebMvc
public class UI extends WebMvcAutoConfiguration implements CommandLineRunner{

	@Value("${engineURI}")
	private String engineURI;
	
	private static Logger logger = LoggerFactory.getLogger(UI.class);
	
	
	
	public static void main(String[] args) {
		SpringApplication.run(UI.class, args);
	}
	
	@RequestMapping("/")
	ModelAndView index() {
		return new ModelAndView("index").addObject("values", new GetValuesCommand(engineURI).execute());
    }
	
	@Bean
	public ServletRegistrationBean servletRegistrationBean(){
	    return new ServletRegistrationBean(new HystrixMetricsStreamServlet(),"/hystrix.stream");
	}

	@Override
	public void run(String... args) throws Exception {
		HystrixPlugins.getInstance().registerMetricsPublisher( HystrixServoMetricsPublisher.getInstance());
	}

}

class GetValuesCommand extends HystrixCommand<Collection<?>> {
	private static final Setter cachedSetter = 
	        Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Traktor"))
	            .andCommandKey(HystrixCommandKey.Factory.asKey("GetLastestValues"));  
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	private final String engineURI;
	
	public GetValuesCommand(String engineURI) {
		super(cachedSetter);
		this.engineURI = Objects.requireNonNull(engineURI, "Engine URI is null!");
	}

	@Override
	protected Collection<?> run() throws Exception {
		return restTemplate.getForObject(engineURI+ "/sampling", Collection.class);
	}
	
	@Override
	protected Collection<?> getFallback() {
		return Arrays.asList(new EngineDown(engineURI));
	}
}