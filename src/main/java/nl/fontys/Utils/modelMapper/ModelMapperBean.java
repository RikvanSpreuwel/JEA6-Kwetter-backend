package nl.fontys.utils.modelMapper;

import nl.fontys.utils.modelMapper.converters.ToKwetterResourceModelConverter;
import nl.fontys.utils.modelMapper.converters.ToUserResourceModelConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class ModelMapperBean {
    @Bean
    ModelMapper modelMapper(){
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(new ToUserResourceModelConverter());
        mapper.addConverter(new ToKwetterResourceModelConverter());
        return mapper;
    }
}
