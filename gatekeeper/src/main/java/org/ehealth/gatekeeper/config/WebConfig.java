package org.ehealth.gatekeeper.config;

import java.util.List;

import org.ehealth.gatekeeper.domain.entity.UserEntity;
import org.ehealth.gatekeeper.repository.UserRepository;
import org.ehealth.gatekeeper.util.annotation.CurrentUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(userRepository));
    }
}

@RequiredArgsConstructor
class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String subject = authentication.getName();
        Class<?> dtoType = parameter.getParameterType();
        if (dtoType.equals(UserEntity.class)) {
            throw new IllegalArgumentException("La clase UserEntity no puede ser utilizada como argumento");
        }
        return userRepository.findUnknownById(Long.parseLong(subject), dtoType).orElseThrow();
    }
}