package spring.reborn.domain.oauth.helper.coverter;

import org.springframework.context.annotation.Configuration;
import spring.reborn.domain.oauth.helper.constants.SocialLoginType;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class SocialLoginTypeConverter implements Converter<String, SocialLoginType> {
    @Override
    public SocialLoginType convert(String s) {
        return SocialLoginType.valueOf(s.toUpperCase());
    }
}