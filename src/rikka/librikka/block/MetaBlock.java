package rikka.librikka.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.PropertyMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class MetaBlock extends BlockBase implements ISubBlock {
    public final IProperty<Integer> propertyMeta;
    private final String[] subNames;
    
    public MetaBlock(String unlocalizedName, String[] subNames, Material material, Class<? extends ItemBlockBase> itemBlockClass) {
        super(registerMetaUpperBound(unlocalizedName, subNames), material, itemBlockClass);

        if (metaUpperBound != metaUpperBounds.get(unlocalizedName))
        	throw new RuntimeException("Parameter Corrupted!");
        
        this.subNames = new String[subNames.length];
        for (int i = 0; i < subNames.length; i++)
            this.subNames[i] = subNames[i];
        
        this.propertyMeta = (IProperty<Integer>) getBlockState().getProperty("meta");
        setDefaultState(this.getDefaultState(blockState.getBaseState()));
    }

    @Override
    public final String[] getSubBlockUnlocalizedNames() {
        return this.subNames;
    }

    
    private static final Map<String, Integer> metaUpperBounds = new HashMap();
    private static int metaUpperBound;
    private static String registerMetaUpperBound(String unlocalizedName, String[] subNames) {
    	metaUpperBound = subNames.length - 1;
    	metaUpperBounds.put(unlocalizedName, metaUpperBound);
        return unlocalizedName;
    }
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected final BlockStateContainer createBlockState() {
        ArrayList<IProperty> properties = new ArrayList();
        ArrayList<IUnlistedProperty> unlisted = new ArrayList();

        this.createProperties(properties, unlisted);

        IProperty[] propertyArray = properties.toArray(new IProperty[properties.size()]);
        IUnlistedProperty[] unlistedArray = unlisted.toArray(new IUnlistedProperty[unlisted.size()]);

        if (unlisted.isEmpty()) {
            return new BlockStateContainer(this, propertyArray);
        } else {
            return new ExtendedBlockState(this, propertyArray, unlistedArray);
        }
    }

    /**
     * Override this to add more normal/unlisted properties
     *
     * @param properties
     */
    protected void createProperties(ArrayList<IProperty> properties, ArrayList<IUnlistedProperty> unlisted) {
        properties.add(new PropertyMeta("meta", metaUpperBound + 1));
    }

    /**
     * Before the initialization is done, propertyMeta is null,
     *
     * @return @NonNullable propertyMeta
     */
    public final IProperty<Integer> getPropertyMeta() {
        if (this.propertyMeta == null)
            return (IProperty<Integer>) blockState.getProperty("meta");
        return this.propertyMeta;
    }

    private IBlockState getDefaultState(IBlockState baseState) {
        return baseState.withProperty(this.propertyMeta, 0);
    }

    /**
     * This gets called before during the initialization, propertyMeta is not ready yet
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(this.getPropertyMeta(), meta & 15);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(this.getPropertyMeta());
        meta = meta & 15;
        return meta;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(this.propertyMeta);
    }
}