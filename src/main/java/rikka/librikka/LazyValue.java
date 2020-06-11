package rikka.librikka;

import java.util.function.Supplier;

/**
 * For 1.15.2 compatibility
 */
public class LazyValue<T> {
	private Supplier<T> supplier = null;
	private T cachedVal = null;
	
	public LazyValue(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public T get() {
		if (this.supplier != null) {
			this.cachedVal = this.supplier.get();
			this.supplier = null;
		}
		
		return cachedVal;
	}
}
