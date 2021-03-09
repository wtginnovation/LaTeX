package de.vsfexperts.latex.server.service;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Stolen/Adapted from https://gist.github.com/jonikarppinen/662c38fb57a23de61c8b
 */
@RestController
public class GlobalErrorController implements ErrorController {

	private static final String ERROR_PATH = "/error";

	@Autowired
	private ErrorAttributes errorAttributes;

	@RequestMapping(value = ERROR_PATH, produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
	public ErrorMessage handleError(final HttpServletRequest request, final HttpServletResponse response) {
		return new ErrorMessage(response.getStatus(), getErrorAttributes(request));
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	private Map<String, Object> getErrorAttributes(final HttpServletRequest request) {
		final WebRequest webRequest = new ServletWebRequest(request);
		return errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
	}

}
