package com.greg.thinkinginjava.StaticOfInitialization;

public class StaticInitialization {
	public static void main(String[] args) {
		System.out.println("Creating new Cupboard() in main");
		new Cupboard();
		System.out.println("Creating new Cupboard() in main");
		new Cupboard();
		t2.f2(1);
		t3.f3(1);
	}

	static Table t2 = new Table();
	static Cupboard t3 = new Cupboard();
} /// :~
