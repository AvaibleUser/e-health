package org.ehealth.gatekeeper.service.util;

import java.util.Map;

public interface ITemplateService {

    String renderTemplate(String templateName, Map<String, Object> variables);
}
