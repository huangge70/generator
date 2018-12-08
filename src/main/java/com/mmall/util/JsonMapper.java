package com.mmall.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMapper {
    private static Logger log= LoggerFactory.getLogger(JsonMapper.class);
    private static ObjectMapper objectMapper=new ObjectMapper();
    static{
        //config
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }
    public static <T> String obj2String(T src){
        if(src==null){
            return null;
        }
        try {
            return src instanceof String?(String) src:objectMapper.writeValueAsString(src);
        }catch (Exception e){
            log.warn("parse object to string object");
            return null;
        }
    }
    public static <T> T string2Object(String src, TypeReference typeReference){
        if(src==null||typeReference==null){
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class)?src:objectMapper.readValue(src,typeReference));
        }catch (Exception e){
            log.warn("parse String to object error,String:{},type:{},exception:{}",src,typeReference,e);
            return null;
        }
    }
}
