package com.danielmiao.route;

public class TestService {

	public static void main(String[] args) {
		DataBaseRouteRegister.registClass(TestMethod.class, "id");
		TestMethod method = new TestMethod();
		for (int i = 0; i < 100; i++) {
			method.setId(i);
			System.out.println(DataBaseRoute.split(new Object[] { method }));
		}
	}

}
