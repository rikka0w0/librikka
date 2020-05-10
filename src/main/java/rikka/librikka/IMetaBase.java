package rikka.librikka;

/**
 * Implemented by an enum class which defines sub types
 */
public interface IMetaBase {
	String name();
	int ordinal();
	boolean equals(Object obj);
}
