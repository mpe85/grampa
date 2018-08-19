package com.mpe85.grampa.rule;

/**
 * A parser command. A command is a special {@link Action} that always succeeds (i.e. always returns true)
 * 
 * @author mpe85
 *
 * @param <T>
 *        the type of the stack elements
 */
@FunctionalInterface
public interface Command<T> {
	
	/**
	 * Executes the parser command.
	 * 
	 * @param context
	 *                an action context
	 */
	void execute(ActionContext<T> context);
	
	/**
	 * Converts the parser command to a parser action.
	 * 
	 * @return a parser command
	 */
	public default Action<T> toAction() {
		return ctx -> {
			this.execute(ctx);
			return true;
		};
	}
	
}
