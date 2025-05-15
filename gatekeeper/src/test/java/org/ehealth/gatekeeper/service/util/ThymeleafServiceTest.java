package org.ehealth.gatekeeper.service.util;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@ExtendWith(MockitoExtension.class)
public class ThymeleafServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private ThymeleafService thymeleafService;

    @Test
    void renderTemplate() {
        // given
        String template = "this is a template";
        String expectedHtml = "<html>this was template</html>";
        Map<String, Object> variables = Map.of("this", "is", "a", "variable");
        Context context = new Context();
        context.setVariables(Map.copyOf(variables));

        given(templateEngine.process(eq(template), refEq(context))).willReturn(expectedHtml);

        // when
        String actualHtml = thymeleafService.renderTemplate(template, variables);

        // then
        then(actualHtml).isEqualTo(expectedHtml);
    }
}
