package com.danielmiao.route;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.danielmiao.route.annotation.Mode;
import com.danielmiao.route.domain.RouteKey;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DataBaseRoute extends AbstractRoutingDataSource {

	protected static final ThreadLocal<Integer> _select = new ThreadLocal<>();

	protected static final ThreadLocal<Boolean> _trans = ThreadLocal.withInitial(() -> false);

	protected static final ThreadLocal<Mode> _mode = ThreadLocal.withInitial(() -> Mode.NO_SPLIT);

	protected final static HashMap<String, RouteNode> methods = new HashMap<>();

	private static int shift = 1;

	protected static class RouteNode {
		private Method method;
		private Class<?> clazz;
		private Class<?> type;

		public RouteNode(Class<?> clazz, Class<?> type, Method method) {
			if (type != Integer.class && type != int.class && type != Long.class && type != long.class) {
				throw new IllegalArgumentException("return type not support");
			}
			this.clazz = clazz;
			this.type = type;
			this.method = method;
		}

		public int process(Object arg) {
			try {
				if (arg.getClass() != clazz) {
					throw new IllegalAccessError();
				}
				if (type == Integer.class || type == int.class) {
					return ((int) method.invoke(arg, new Object[0])) & shift;
				} else if (type == Long.class || type == long.class) {
					return (int) ((long) method.invoke(arg, new Object[0])) & shift;
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalArgumentException("this argument not support.");
			}
			return 0;
		}
	}

	/**
	 * 重载该函数可按需分库
	 * 
	 */
	protected static Integer split(Object[] args) {
		Integer index = null;
		if (args != null && args.length > 0) {
			Object arg = args[0];
			if (arg instanceof Long) {
				index = (int) ((Long) arg & shift);
				;
			} else if (arg instanceof RouteKey) {
				index = (int) (((RouteKey) arg).getKey() & shift);
			} else {
				RouteNode method ;
				if ((method = methods.get(arg.getClass().getName())) != null) {
					index = method.process(arg);
				} else {
					throw new IllegalArgumentException("The first argument not support.");
				}
			}
		}
		return index;

	}

	// /////////////////////////////////////////////
	// 分库切面
	// /////////////////////////////////////////////

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		int size = targetDataSources.size();
		shift = size - 1;
		int num = (int) Math.sqrt(size);
		if ((int) Math.pow(2, num) != size) {
			throw new IllegalArgumentException("DataSource must be a multiple of 2.");
		}
		for (int i = 0; i < size; i++) {
			if (!targetDataSources.containsKey(i)) {
				throw new IllegalArgumentException("DataSource key must be continuous");
			}
		}
		super.setTargetDataSources(targetDataSources);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return _select.get();
	}

}
