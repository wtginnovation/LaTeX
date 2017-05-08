package de.vsfexperts.latex.server.service;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Stolen/Adapted from https://gist.github.com/jonikarppinen/662c38fb57a23de61c8b
 */
@RestController
public class GlobalErrorController implements ErrorController {

	private static final String ERROR_PATH = "/error";

	@Autowired
	private ErrorAttributes errorAttributes;

	@RequestMapping(value = ERROR_PATH, produces = APPLICATION_JSON_UTF8_VALUE)
	public ErrorJson handleError(final HttpServletRequest request, final HttpServletResponse response) {
		return new ErrorJson(response.getStatus(), getErrorAttributes(request));
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	private Map<String, Object> getErrorAttributes(final HttpServletRequest request) {
		final RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return errorAttributes.getErrorAttributes(requestAttributes, false);
	}

	class ErrorJson {

		public Integer status;
		public String error;
		public String message;
		public String timeStamp;
		public String trace;

		public ErrorJson(final int status, final Map<String, Object> errorAttributes) {
			this.status = status;
			this.error = (String) errorAttributes.get("error");
			this.message = (String) errorAttributes.get("message");
			this.timeStamp = errorAttributes.get("timestamp").toString();
			this.trace = (String) errorAttributes.get("trace");
		}

	}

}
