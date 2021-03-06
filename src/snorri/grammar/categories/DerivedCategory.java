package snorri.grammar.categories;

import snorri.grammar.ComposeRule;

public class DerivedCategory implements Category {
	
	public static final char LEFT = '\\';
	public static final char RIGHT = '/';

	private Category left;
	private Category right;
	private char direction;
	private ComposeRule rule;

	public DerivedCategory(Category left, char direction, Category right, ComposeRule rule) {
		this.left = left;
		this.direction = direction;
		this.right = right;
		this.rule = rule;
	}
	
	public DerivedCategory(Category left, char direction, Category right) {
		this(left, direction, right, ComposeRule.DEFAULT);
	}
	
	@Override
	public Category apply(Category other) {
		String otherString = other.toString();
		if (direction == LEFT && left.toString().equals(otherString)) {
			return right;
		}
		if (direction == RIGHT && right.toString().equals(otherString)) {
			return left;
		}
		return null;
	}

	@Override
	public String toString() {
		String left = this.left.toString();
		if (this.left instanceof DerivedCategory) {
			left = "(" + left + ")";
		}
		
		String right = this.right.toString();
		if (this.right instanceof DerivedCategory) {
			right = "(" + right + ")";
		}
		return left + " " + direction + " " + right;
	}

	@Override
	public boolean equals(Object other) {
		return toString().contentEquals(other.toString());
	}
	
	@Override
	public ComposeRule getRule() {
		return rule;
	}
	
	@Override
	public char getDirection() {
		return direction;
	}

}
