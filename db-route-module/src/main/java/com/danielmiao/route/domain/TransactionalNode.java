package com.danielmiao.route.domain;

public class TransactionalNode {

	private String method;

	private boolean transactional;

	public TransactionalNode(String method, boolean transactional) {
		super();
		this.method = method;
		this.transactional = transactional;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @return the transactional
	 */
	public boolean isTransactional() {
		return transactional;
	}
}
