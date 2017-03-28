package com.danielmiao.route;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DataBaseRouteRegister {

	public static void registClass(Class<?> clazz, String routeKey) {
		try {
			Field field = clazz.getDeclaredField(routeKey);
			PropertyDescriptor property = new PropertyDescriptor(field.getName(), clazz);
			Method method = property.getReadMethod();
			DataBaseRoute.methods.put(clazz.getName(), new DataBaseRoute.RouteNode(clazz, method.getReturnType(), method));
		} catch (NoSuchFieldException | SecurityException | IntrospectionException e) {
			throw new IllegalArgumentException("regist route class error.", e);
		}
	}
}
