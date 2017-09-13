package rikka.librikka.properties;

import java.lang.ref.WeakReference;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyRef<V> implements IUnlistedProperty<V> {
	public static final IUnlistedProperty<WeakReference<TileEntity>> propertyTile = new UnlistedPropertyRef<>("tile");
    
	public static TileEntity get(IBlockState blockState) {
        if (!(blockState instanceof IExtendedBlockState))
            //Normally this should not happen, just in case, to prevent crashing
            return null;
		
		IExtendedBlockState exBlockState = (IExtendedBlockState) blockState;
        WeakReference<TileEntity> ref = exBlockState.getValue(UnlistedPropertyRef.propertyTile);
        return ref==null ? null : ref.get();
	}
	
	public static <T extends TileEntity> T get(IBlockState blockState, Class<T> tileEntityClass) {
		TileEntity te = get(blockState);
		if (te == null)
			return null;
		
		return (te.getClass() == tileEntityClass) ? (T)te : null;
	}
	
	public final String name;

    public UnlistedPropertyRef(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isValid(V value) {
        return value != null;
    }

    @Override
    public Class getType() {
        return Object.class;
    }

    @Override
    public String valueToString(V value) {
        return value.toString();
    }
}
