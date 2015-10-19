package com.eweblib.dbhelper;

public enum DataBaseQueryOpertion {

	LARGER_THAN {

		@Override
		public String toString() {
			return ">";
		}

	},
	LESS_THAN {

		@Override
		public String toString() {
			return "<";
		}

	},
	EQUAILS {

		@Override
		public String toString() {
			return "=";
		}

	},
	LARGER_THAN_EQUALS {

		@Override
		public String toString() {
			return ">=";
		}

	},
	LESS_THAN_EQUAILS {

		@Override
		public String toString() {
			return "<=";
		}

	},
	NOT_NULL {

		@Override
		public String toString() {
			return "is not null";
		}

	},
	NULL {

		@Override
		public String toString() {
			return "is null";
		}

	},
	IS_TRUE {

		@Override
		public String toString() {
			return "is true";
		}

	},
	IS_FALSE {

		@Override
		public String toString() {
			return "is false";
		}

	},
	NOT_IN {

		@Override
		public String toString() {
			return "not in";
		}

	},
	IN {

		@Override
		public String toString() {
			return "in";
		}

	},
	NOT_EQUALS {

		@Override
		public String toString() {
			return "!=";
		}

	},
	BETWEEN_AND {

		@Override
		public String toString() {
			return ">";
		}

	},
	LIKE {

		@Override
		public String toString() {
			return "like";
		}

	}, NOT_LIKE {

		@Override
		public String toString() {
			return "not like";
		}

	};


}
