package nl.fontys.utils.modelmapper;

import nl.fontys.utils.modelmapper.converters.ToKwetterResourceModelConverter;
import nl.fontys.utils.modelmapper.converters.ToUserResourceModelConverter;
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
