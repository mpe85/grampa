package com.mpe85.grampa.rule;

@FunctionalInterface
public interface Command<T> {
	
	void execute(ActionContext<T> context);
	
	public default Action<T> toAction() {
		return ctx -> {
			this.execute(ctx);
			return true;
		};
	}
	
}
