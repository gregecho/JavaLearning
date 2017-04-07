package com.greg.thinkinginjava.OrderOfInitialization;

class Card {
	Tag t1 = new Tag(1); // Before constructor

	Card() {
		// Indicate we're in the constructor:
		System.out.println("Card()");
		t3 = new Tag(33); // Re-initialize t3
	}

	Tag t2 = new Tag(2); // After constructor

	void f() {
		System.out.println("f()");
	}

	Tag t3 = new Tag(3); // At end
}
