package com.oauth2.exception;

import com.oauth2.exception.code.ErrorCode;
import lombok.Data;

@Data
public class CustomException extends RuntimeException {

	private static final long serialVersionUID = -420621210859639120L;

	private ErrorCode err;

	private int code;

	private String desc;

	private String descCn;

	public CustomException(ErrorCode err) {
		super(err.getDesc());
		this.err = err;
		this.code = err.getCode();
		this.desc = err.getDesc();
		this.descCn = err.getDescCn();
	}

	public CustomException(int code, String desc) {
		super(desc);
		this.code = code;
		this.desc = desc;
	}

	public CustomException(int code, String desc, String descCn) {
		super(desc);
		this.code = code;
		this.desc = desc;
		this.descCn = descCn;
	}
}
