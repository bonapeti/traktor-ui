package org.traktor;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@Controller
@EnableWebMvc
public class UI extends WebMvcAutoConfiguration {

	@Value("${engineURI}")
	private String engineURI;
	
	private static Logger logger = LoggerFactory.getLogger(UI.class);
	
	private RestTemplate restTemplate = new RestTemplate();
	
	public static void main(String[] args) {
		SpringApplication.run(UI.class, args);
	}
	
	@RequestMapping("/")
	ModelAndView index() {
		try {
			return new ModelAndView("index").addObject("values", restTemplate.getForObject(engineURI+ "/sampling", Collection.class));
		} catch (RestClientException ce) {
			logger.warn(ce.getMessage());
			return new ModelAndView("index").addObject("values", new ArrayList());
		}
    }

}
