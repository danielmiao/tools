package com.danielmiao.route;

import java.util.Stack;

import com.danielmiao.route.annotation.Mode;
import com.danielmiao.route.annotation.Split;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;

import com.danielmiao.route.domain.StackNode;

@Aspect
public class SplitInterceptor implements Ordered {
	private int order;

	public SplitInterceptor(int order) {
		super();
		this.order = order;
	}

	private static final ThreadLocal<Stack<StackNode>> _stack = ThreadLocal.withInitial(Stack::new);

	@Around("@annotation(split)")
	public static Object split(ProceedingJoinPoint point, Split split) throws Throwable {
		String method = point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName();
		Stack<StackNode> stack = _stack.get();
		if (DataBaseRoute._trans.get() && DataBaseRoute._mode.get() != split.mode()) {
			if (stack.isEmpty()) {
				throw new IllegalArgumentException(
						String.format("@Split must be in same or before @Transactional. [%s] ", method));
			} else {
				throw new IllegalArgumentException(
						String.format("@Split Mode can not change in @Transactional method. [%s] Call [%s]",
								stack.lastElement().getMethod(), method));
			}
		}
		Integer key = null;
		if (split.mode() == Mode.SPLIT) {
			key = DataBaseRoute.split(point.getArgs());
			if (!DataBaseRoute._trans.get()) {
				DataBaseRoute._select.set(key);
			} else if (key.equals(DataBaseRoute._select.get())){
				throw new IllegalArgumentException(String.format("@Split transmit can not change. [%s] Call [%s]",
						_stack.get().lastElement().getMethod(), method));
			}
		} else {
			DataBaseRoute._select.remove();
		}
		DataBaseRoute._mode.set(split.mode());
		StackNode node = new StackNode(method, split.mode(), key);
		try {
			stack.push(node);
			return point.proceed();
		} finally {
			stack.pop();
			if (stack.empty()) {
				DataBaseRoute._mode.set(Mode.NO_SPLIT);
				DataBaseRoute._select.remove();
			} else if (!DataBaseRoute._trans.get()) {
				node = stack.lastElement();
				DataBaseRoute._mode.set(node.getMode());
				DataBaseRoute._select.set(node.getSplitKey());
			}
		}

	}

	@Override
	public int getOrder() {
		return this.order;
	}
}
