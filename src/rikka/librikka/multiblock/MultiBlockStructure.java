package rikka.librikka.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Inspired by Immersive Engineering
 *
 * @author Rikka0_0
 */
public class MultiBlockStructure {
    /**
     * NSWE, entries: [Direction.ordinal()-2][y][z][x]
     */
    private final MultiBlockStructure.BlockInfo[][][][] unmirrored = new MultiBlockStructure.BlockInfo[4][][][];
    private final MultiBlockStructure.BlockInfo[][][][] mirroredAboutZ = new MultiBlockStructure.BlockInfo[4][][][];

    public final boolean checkForMirrored;
    private final int height;
    private final int searchAreaSize;

    public MultiBlockStructure(BlockMapping[][][] configuration) {
    	this(configuration, true);
    }
    
    /**
     * @param configuration y,z,x facing NORTH(Z-), do not change
     */
    public MultiBlockStructure(BlockMapping[][][] configuration, boolean checkForMirrored) {
        this.height = configuration.length;
        this.checkForMirrored = checkForMirrored;
        
        //Find the bounding box
        int[] xzSize = getSearchSizeXZ(configuration);
        int zSize = xzSize[1], xSize = xzSize[0];
        searchAreaSize = xSize > zSize ? xSize : zSize;


        this.unmirrored[0] = new MultiBlockStructure.BlockInfo[height][zSize][xSize];    //North, Unmirrored
        this.unmirrored[3] = new MultiBlockStructure.BlockInfo[height][xSize][zSize];    //East, Unmirrored
        this.unmirrored[1] = new MultiBlockStructure.BlockInfo[height][zSize][xSize];    //South, Unmirrored
        this.unmirrored[2] = new MultiBlockStructure.BlockInfo[height][xSize][zSize];    //West, Unmirrored
        this.mirroredAboutZ[0] = new MultiBlockStructure.BlockInfo[height][zSize][xSize];    //North
        this.mirroredAboutZ[3] = new MultiBlockStructure.BlockInfo[height][xSize][zSize];    //East
        this.mirroredAboutZ[1] = new MultiBlockStructure.BlockInfo[height][zSize][xSize];    //South
        this.mirroredAboutZ[2] = new MultiBlockStructure.BlockInfo[height][xSize][zSize];    //West
        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < configuration[y].length; z++) {
                for (int x = 0; x < configuration[y][z].length; x++) {
                    BlockMapping blockMapping = configuration[y][z][x];
                    MultiBlockStructure.BlockInfo blockInfo = null;
                    
                    if (blockMapping != null) {
                        blockInfo = new BlockInfo(x, y ,z, blockMapping);
                    }

                    unmirrored[0][y][z][x] = blockInfo;                    //North
                    unmirrored[3][y][x][zSize - 1 - z] = blockInfo;            //East, newX = zSize-1 - oldZ, newZ = oldX
                    unmirrored[1][y][zSize - 1 - z][xSize - 1 - x] = blockInfo;    //South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
                    unmirrored[2][y][xSize - 1 - x][z] = blockInfo;            //West, newX = oldZ, newZ = xSize-1 - oldX;

                    mirroredAboutZ[0][y][z][xSize - 1 - x] = blockInfo;
                }
            }
        }

        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < zSize; z++) {
                for (int x = 0; x < xSize; x++) {
                    MultiBlockStructure.BlockInfo blockInfo = this.mirroredAboutZ[0][y][z][x];

                    mirroredAboutZ[3][y][x][zSize - 1 - z] = blockInfo;            //East, newX = zSize-1 - oldZ, newZ = oldX
                    mirroredAboutZ[1][y][zSize - 1 - z][xSize - 1 - x] = blockInfo;    //South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
                    mirroredAboutZ[2][y][xSize - 1 - x][z] = blockInfo;            //West, newX = oldZ, newZ = xSize-1 - oldX;
                }
            }
        }
    }

    public static int[] offsetFromOrigin(int rotation, boolean mirrored, int x, int y, int z) {
        int[] ret = new int[3];

        if (mirrored)
            x = -x;

        switch (rotation) {
            case 0:    //North
                ret = new int[]{x, y, z};
                break;
            case 3: //East, newX = zSize-1 - oldZ, newZ = oldX
                ret = new int[]{-z, y, x};
                break;
            case 1:    //South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
                ret = new int[]{-x, y, -z};
                break;
            case 2: //West, newX = oldZ, newZ = xSize-1 - oldX;
                ret = new int[]{z, y, -x};
                break;
            default:
                ret = null;
                break;
        }

        if (ret == null)
            return null;

        return ret;
    }

    private boolean check(BlockState[][][] states, MultiBlockStructure.BlockInfo[][][] configuration, int xOrigin, int yOrigin, int zOrigin) {
        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < configuration[y].length; z++) {
                for (int x = 0; x < configuration[y][z].length; x++) {
                    MultiBlockStructure.BlockInfo config = configuration[y][z][x];
                    BlockState state = states[xOrigin + x][yOrigin + y][zOrigin + z];
                    if (config != null && config.mapping.cancelPlacement(state))
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * @param states
     * @param configuration
     * @return offset, null = mismatch
     */
    private int[] check(BlockState[][][] states, MultiBlockStructure.BlockInfo[][][] configuration) {
        for (int zOrigin = 0; zOrigin < this.searchAreaSize; zOrigin++) {
            for (int xOrigin = 0; xOrigin < this.searchAreaSize; xOrigin++) {
                for (int yOrigin = 0; yOrigin < this.height; yOrigin++) {
                    if (this.check(states, configuration, xOrigin, yOrigin, zOrigin))
                        return new int[]{xOrigin, yOrigin, zOrigin};
                }
            }
        }

        return null;
    }

    public Result attempToBuild(World world, BlockPos start) {
        int xStart = start.getX(), yStart = start.getY(), zStart = start.getZ();
        //XYZ
        BlockState[][][] states = new BlockState[searchAreaSize * 2 - 1][height * 2 - 1][searchAreaSize * 2 - 1];

        //Origin of the search area
        int xOrigin = xStart - searchAreaSize + 1;
        int yOrigin = yStart - height + 1;
        int zOrigin = zStart - searchAreaSize + 1;

        for (int x = xOrigin, i = 0; x < xStart + searchAreaSize; x++, i++) {
            for (int y = yOrigin, j = 0; y < yStart + height; y++, j++) {
                for (int z = zOrigin, k = 0; z < zStart + searchAreaSize; z++, k++) {
                    states[i][j][k] = world.getBlockState(new BlockPos(x, y, z));

//                    if (states[i][j][k] == Blocks.AIR.getDefaultState())
//                        states[i][j][k] = null;
                }
            }
        }


        //Check unmirrored
        for (int dir = 0; dir < 4; dir++) {
            int[] offset = this.check(states, this.unmirrored[dir]);
            if (offset != null)
                return new Result(this, dir, false, world, xOrigin + offset[0], yOrigin + offset[1], zOrigin + offset[2]);
        }

        if (!this.checkForMirrored)
        	return null;
        
        //Check mirrored
        for (int dir = 0; dir < 4; dir++) {
            int[] offset = this.check(states, this.mirroredAboutZ[dir]);
            if (offset != null)
                return new Result(this, dir, true, world, xOrigin + offset[0], yOrigin + offset[1], zOrigin + offset[2]);
        }

        return null;
    }

    public int[] getSearchSizeXZ(Object[][][] config) {
        //Find the bounding box
        int zSize = 0, xSize = 0;
        for (int y = 0; y < this.height; y++) {
            Object[][] zxc = config[y];
            for (int z = 0; z < zxc.length; z++) {
            	Object[] xc = zxc[z];
                if (xc.length > xSize)
                    xSize = xc.length;
            }

            if (zxc.length > zSize)
                zSize = zxc.length;
        }
        
        return new int[] {xSize, zSize};
    }
    
    public int[] getCenterXZ(int xSize, int zSize) {        
        if ((xSize>>1)<<1==xSize || (zSize>>1)<<1==zSize)
        	return null;	// Even size, unable to determine the center pos
        
        int centerOffsetX = (xSize-1) >> 1, centerOffsetZ = (zSize-1) >> 1;
        
        return new int[] {centerOffsetX, centerOffsetZ};
    }
    
    public Result attempToBuild(World world, BlockPos start, Direction facing) {
    	boolean mirrored = false;
    	int rotation = facing.ordinal() - 2;    	
    	MultiBlockStructure.BlockInfo[][][] config =  mirrored ? unmirrored[rotation] : mirroredAboutZ[rotation];
    	int[] xzSize = getSearchSizeXZ(config);
    	int xSize = xzSize[0];
    	int zSize = xzSize[1];
    	
    	int[] centerXZ = getCenterXZ(xSize, zSize);
    	if (centerXZ == null)
    		return null;	// Not a valid structure for this method
    	
    	int xOrigin = start.getX()-centerXZ[0];
    	int yOrigin = start.getY();
    	int zOrigin = start.getZ()-centerXZ[1];
    	BlockPos origin = new BlockPos(xOrigin, yOrigin, zOrigin);
    	
        // Cache states, [X][Y][Z]
        BlockState[][][] states = new BlockState[xSize][height][zSize];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < zSize; k++) {
                    states[i][j][k] = world.getBlockState(new BlockPos(xOrigin+i, yOrigin+j, zOrigin+k));
                }
            }
        }

        if (!check(states, config, 0, 0, 0))
        	return null;

    	return new Result(this, rotation, false, world, xOrigin, yOrigin, zOrigin);
    }
    
    public void restoreStructure(TileEntity te, BlockState stateJustRemoved, boolean dropConstructionBlockAsItem) {
        if (te instanceof IMultiBlockTile) {
            MultiBlockTileInfo mbInfo = ((IMultiBlockTile) te).getMultiBlockTileInfo();
            if (!mbInfo.formed)
                return;    //Avoid circulation, better performance!

            if (dropConstructionBlockAsItem) {
            	BlockState stateToDrop = this.getConstructionBlock(mbInfo);
            	//System.out.println("drop!!!!!!!!!!!!!!!");
            	// TODO: Check drop behavior
            	Block.spawnDrops(stateToDrop, te.getWorld(), te.getPos());
            }
            
            Set<IMultiBlockTile> removedTile = new HashSet();

            World world = te.getWorld();

            int facing = mbInfo.facing.ordinal() - 2;
            boolean mirrored = mbInfo.mirrored;
            //YZX
            MultiBlockStructure.BlockInfo[][][] configuration = mirrored ? mirroredAboutZ[facing] : unmirrored[facing];
            BlockPos originActual = mbInfo.origin;

            int zSize = configuration[0].length;
            int xSize = configuration[0][0].length;

            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < zSize; j++) {
                    for (int k = 0; k < xSize; k++) {
                        MultiBlockStructure.BlockInfo blockInfo = configuration[i][j][k];

                        if (blockInfo != null) {
                            //Traverse the structure
                            int[] offset = MultiBlockStructure.offsetFromOrigin(facing, mirrored, blockInfo.x, blockInfo.y, blockInfo.z);

                            BlockState theState;

                            BlockPos pos = originActual.add(offset[0], offset[1], offset[2]);

                            if (pos == te.getPos()) {
                                theState = stateJustRemoved;
                            } else {
                                theState = world.getBlockState(pos);

                                if (!theState.isAir(world, pos) && !blockInfo.mapping.cancelRestore(theState)) {
                                    TileEntity te2 = world.getTileEntity(pos);

                                    if (te2 instanceof IMultiBlockTile) {
                                    	MultiBlockTileInfo mbInfo2 = ((IMultiBlockTile) te2).getMultiBlockTileInfo();
                                    	if (mbInfo2 == null)
                                    		throw new RuntimeException("Unable to get MBInfo during structure destruction");
                                    	else
                                    		mbInfo2.formed = false;
                                        removedTile.add((IMultiBlockTile) te2);
                                    }

                                    //Play Destroy Effect
                                    world.playEvent(2001, pos, Block.getStateId(theState));
                                    world.setBlockState(pos, blockInfo.mapping.getStateForRestore(Direction.byIndex(facing+2)));
                                }
                            }
                        }
                    }
                }
            }

            removedTile.add((IMultiBlockTile) te);

            for (IMultiBlockTile tile : removedTile) {
                tile.onStructureRemoved();
            }
        }
    }

    /**
     * 
     * @param facing
     * @param mirrored
     * @param xOffset coordinates in structure definition
     * @param yOffset
     * @param zOffset
     * @return
     */
    public BlockInfo getBlockInfo(int xOffset, int yOffset, int zOffset) {
        return unmirrored[0][yOffset][zOffset][xOffset];
    }
    
    public BlockState getConstructionBlock(MultiBlockTileInfo mbInfo) {
    	BlockInfo info = this.getBlockInfo(mbInfo.xOffset, mbInfo.yOffset, mbInfo.zOffset);
    	return info==null? null : info.mapping.getStateForRestore(mbInfo.facing);
    }
    
    private static class BlockInfo {
        /**
         * Relative position in structure definition
         */
        private final int x, y, z;
        
        private final BlockMapping mapping;

        private BlockInfo(int x, int y, int z, BlockMapping mapping) {
        	this.x = x;
        	this.y = y;
        	this.z = z;
            this.mapping = mapping;
        }

    	@Override
        public String toString() {
    		return mapping.toString();
    	}
    }

    public static class Result {
        public final MultiBlockStructure structure;
        /**
         * facing
         */
        public final int rotation;
        public final boolean mirrored;


        /**
         * NSWE YZX
         */
        public final MultiBlockStructure.BlockInfo[][][] configuration;
        /**
         * Origin of blockInfo, Actual location in the world
         */
        public final int xOrigin, yOrigin, zOrigin;
        public final int zSize, xSize;

        public final World world;
        /**
         * Actual location in the world
         */
        public final int xOriginActual, yOriginActual, zOriginActual;

        private Result(MultiBlockStructure structure, int rotation, boolean mirrored,
                       World world, int xOrigin, int yOrigin, int zOrigin) {
            this.structure = structure;
            this.rotation = rotation;
            this.mirrored = mirrored;
            this.world = world;
            this.xOrigin = xOrigin;
            this.yOrigin = yOrigin;
            this.zOrigin = zOrigin;


            if (mirrored) {
                configuration = structure.mirroredAboutZ[rotation];
            } else {
                configuration = structure.unmirrored[rotation];
            }

            zSize = configuration[0].length;
            xSize = configuration[0][0].length;

            int xOriginActual = this.xOrigin;
            int yOriginActual = this.yOrigin;
            int zOriginActual = this.zOrigin;


            switch (rotation) {
                case 0:    //North
                    if (mirrored)
                        xOriginActual = xOriginActual + xSize - 1;
                    break;
                case 3: //East, newX = zSize-1 - oldZ, newZ = oldX
                    xOriginActual += xSize - 1;
                    if (mirrored)
                        zOriginActual += zSize - 1;
                    break;
                case 1:    //South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
                    if (mirrored) {
                        zOriginActual += zSize - 1;
                    } else {
                        xOriginActual += xSize - 1;
                        zOriginActual += zSize - 1;
                    }
                    break;
                case 2: //West, newX = oldZ, newZ = xSize-1 - oldX;
                    if (!mirrored)
                        zOriginActual += zSize - 1;
                    break;
                default:
                    xOriginActual = -1;
                    yOriginActual = -1;
                    zOriginActual = -1;
                    break;
            }

            this.xOriginActual = xOriginActual;
            this.yOriginActual = yOriginActual;
            this.zOriginActual = zOriginActual;
        }

        public void createStructure() {
            Set<IMultiBlockTile> createdTile = new HashSet();

            for (int i = 0; i < this.structure.height; i++) {
                for (int j = 0; j < this.zSize; j++) {
                    for (int k = 0; k < this.xSize; k++) {
                        MultiBlockStructure.BlockInfo blockInfo = this.configuration[i][j][k];

                        if (blockInfo != null) {
                            //Traverse the structure
                            int[] offset = MultiBlockStructure.offsetFromOrigin(this.rotation, this.mirrored, blockInfo.x, blockInfo.y, blockInfo.z);
                            Direction facing = Direction.byIndex(this.rotation + 2);

                            BlockPos pos = new BlockPos(this.xOriginActual + offset[0], this.yOriginActual + offset[1], this.zOriginActual + offset[2]);
                            BlockState toPlace = blockInfo.mapping.getStateForPlacement(facing);
                            this.world.setBlockState(pos, toPlace);
                            //world.removeTileEntity(pos);	//Remove the incorrect TileEntity
                            TileEntity te = this.world.getTileEntity(pos);

                            if (te instanceof IMultiBlockTile) {
                                MultiBlockTileInfo mbInfo = new MultiBlockTileInfo(
                                        facing, this.mirrored, blockInfo.x, blockInfo.y, blockInfo.z, this.xOriginActual, this.yOriginActual, this.zOriginActual
                                );
                                ((IMultiBlockTile) te).onStructureCreating(mbInfo);
                                createdTile.add((IMultiBlockTile) te);
                            }
                        }

                    }
                }
            }

            for (IMultiBlockTile tile : createdTile)
                tile.onStructureCreated();
        }
    }
}
