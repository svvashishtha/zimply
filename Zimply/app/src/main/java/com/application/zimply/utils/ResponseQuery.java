package com.application.zimply.utils;

public class ResponseQuery {
	// private variables

	String URL;
	byte[] Object;
	long TTL;
	long ZOMID;
	String Type;
	long timestamp;

	// Empty constructor
	public ResponseQuery() {

	}

	// constructor
	public ResponseQuery(String url, String Type, long TTL, long timestamp, long zomid, byte[] object) {
		this.URL = url;
		this.Object = object;
		this.TTL = TTL;
		this.timestamp = timestamp;
		this.ZOMID = zomid;
		this.Type = Type;
		// this.timestamp = timestamp;
	}

	// constructor
	public ResponseQuery(String Query, byte[] Result, int TTL) {
		this.URL = Query;
		this.Object = Result;
		this.TTL = TTL;
		// this.timestamp = timestamp;
	}

	// constructor
	public ResponseQuery(String Query, byte[] Result) {
		this.URL = Query;
		this.Object = Result;
	}
	// getting ID

	// setting id
	public void setTTL(int TTL) {
		this.TTL = TTL;
	}

	public void setTimestamp(long l) {
		this.timestamp = l;
	}

	// getting name
	public byte[] getObject() {
		return this.Object;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getUrl() {
		return this.URL;
	}

	public long getTtl() {
		return this.TTL;
	}

	public String getType() {
		return this.Type;
	}

	public long getZomid() {
		return this.ZOMID;
	}

}
