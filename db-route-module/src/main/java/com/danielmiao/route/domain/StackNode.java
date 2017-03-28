package com.danielmiao.route.domain;

import com.danielmiao.route.annotation.Mode;

public class StackNode {

	private String method;

	private Mode mode;

	private Integer splitKey;

	public StackNode(String method, Mode mode, Integer splitKey) {
		super();
		this.method = method;
		this.mode = mode;
		this.splitKey = splitKey;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @return the splitKey
	 */
	public Integer getSplitKey() {
		return splitKey;
	}

}
