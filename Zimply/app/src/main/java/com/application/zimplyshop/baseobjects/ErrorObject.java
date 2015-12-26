package com.application.zimplyshop.baseobjects;

import com.application.zimplyshop.utils.JSONUtils;

import java.io.Serializable;

public class ErrorObject implements Serializable{
	private int errorCode;

	private String errorMessage;

	public ErrorObject(String obj, int statusCode) {
		this.errorMessage = JSONUtils.getStringfromJSON(
				JSONUtils.getJSONObject(obj), "error");
		this.errorCode = statusCode;
	}

	public ErrorObject(String error, int statusCode, int i) {
		this.errorMessage = error;
		this.errorCode = statusCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
