package de.vsfexperts.latex.server.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.http.HttpStatus;

@XmlRootElement
public class ErrorMessage {

	@XmlElement
	private Integer status;

	@XmlElement
	private String error = "";

	@XmlElement
	private String message = "";

	@XmlElement
	private String timeStamp = "";

	@XmlElement
	private String trace = "";

	public ErrorMessage(final HttpStatus status, final String message) {
		this.status = status.value();
		this.message = message;
		this.timeStamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
	}

	public ErrorMessage(final int status, final Map<String, Object> errorAttributes) {
		this.status = status;
		this.error = (String) errorAttributes.get(ErrorAttributes.ERROR);
		this.message = (String) errorAttributes.get(ErrorAttributes.MESSAGE);
		this.timeStamp = errorAttributes.get(ErrorAttributes.TIMESTAMP).toString();
		this.trace = (String) errorAttributes.get(ErrorAttributes.TRACE);
	}

	protected ErrorMessage() {
	}

	public Integer getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public String getTrace() {
		return trace;
	}

	private static final class ErrorAttributes {

		public static final String ERROR = "error";
		public static final String MESSAGE = "message";
		public static final String TIMESTAMP = "timestamp";
		public static final String TRACE = "trace";

		private ErrorAttributes() {
			throw new IllegalStateException("Do not instantiate");
		}

	}

}
