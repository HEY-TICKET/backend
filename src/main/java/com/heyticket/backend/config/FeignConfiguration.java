package com.heyticket.backend.config;

import feign.codec.Decoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

@Configuration
public class FeignConfiguration {

    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignConfiguration(final ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    public FormEncoder feignFormEncoder() {
        return new FormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public Decoder feignDecoder() {
        MappingJackson2XmlHttpMessageConverter c = new MappingJackson2XmlHttpMessageConverter();
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(c);
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

}
