package com.danielmiao.route;

import java.util.Stack;

import com.danielmiao.route.domain.TransactionalNode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.Transactional;

@Aspect
public class TransInterceptor implements Ordered {

	private int order;

	public TransInterceptor(int order) {
		super();
		this.order = order;
	}

	private static final ThreadLocal<Stack<TransactionalNode>> _stack =ThreadLocal.withInitial(Stack::new);

	@Around("@annotation(transactional)")
	public static Object pointTransactional(ProceedingJoinPoint point, Transactional transactional) throws Throwable {
		String method = point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName();
		Stack<TransactionalNode> stack = _stack.get();
		DataBaseRoute._trans.set(true);
		TransactionalNode node = new TransactionalNode(method, true);
		try {
			stack.push(node);
			return point.proceed();
		} finally {
			stack.pop();
			if (stack.empty() || !stack.lastElement().isTransactional()) {
				DataBaseRoute._trans.set(false);
			}
		}
	}

	@Override
	public int getOrder() {
		return this.order;
	}

}
