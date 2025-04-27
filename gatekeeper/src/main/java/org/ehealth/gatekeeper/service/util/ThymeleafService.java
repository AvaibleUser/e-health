package org.ehealth.gatekeeper.service.util;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThymeleafService implements ITemplateService {

    private final SpringTemplateEngine templateEngine;

    public String renderTemplate(String template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        return templateEngine.process(template, context);
    }
}
