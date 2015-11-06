package com.application.zimply.utils;

public interface UploadManagerCallback {

	void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId);
	
	void uploadStarted(int requestType, String objectId, int parserId, Object data);
	
}
