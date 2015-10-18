/**
 * enum to retrieve the precendence level of each particular operator
 */
public enum Operators {
	ADD {
		@Override
		public int getPrecedence() {
			return 13;
		}
	},
	NEGATION {
		@Override
		public int getPrecedence() {
			return 15;
		}

	},
	SUBTRACT {
		@Override
		public int getPrecedence() {
			return 13;
		}

	},
	MULTIPLY {
		@Override
		public int getPrecedence() {
			return 14;
		}

	},
	EXP {
		@Override
		public int getPrecedence() {
			return 16;
		}
	};

	public abstract int getPrecedence();
}