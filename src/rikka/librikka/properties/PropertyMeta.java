package rikka.librikka.properties;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.block.properties.IProperty;

public class PropertyMeta implements IProperty<Integer>{
	private final ImmutableSet<Integer> allowedValues;
	private final String name;
	
	/**
	 * @param name
	 * @param numOfState upper bound +1
	 */
	public PropertyMeta(String name, int numOfState) {
        Set<Integer> set = Sets.<Integer>newHashSet();

        for (int i = 0; i < numOfState; i++)
            set.add(Integer.valueOf(i));

        this.allowedValues = ImmutableSet.copyOf(set);
        this.name = name;
	}
	
	@Override
	public String getName() { return name;}

	@Override
	public Collection<Integer> getAllowedValues() {return allowedValues;}

	@Override
	public Class<Integer> getValueClass() {return Integer.class;}

	@Override
	public Optional<Integer> parseValue(String value) {
        try {
            Integer integer = Integer.valueOf(value);
            return this.allowedValues.contains(integer) ? Optional.of(integer) : Optional.<Integer>absent();
        } catch (NumberFormatException e) {
            return Optional.<Integer>absent();
        }
	}

	@Override
	public String getName(Integer value) { return value.toString();	}
	
	
	
	
	@Override
    public String toString() {
	    return "name=" + this.name + ", length=" + this.getAllowedValues().size();
    }
    
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof PropertyMeta) {
        	PropertyMeta propertyinteger = (PropertyMeta)obj;
            return this.allowedValues.size() == propertyinteger.allowedValues.size();
        } else {
            return false;
        }
    }

	@Override
    public int hashCode() {
        return 31 * super.hashCode() + this.allowedValues.hashCode();
    }
}