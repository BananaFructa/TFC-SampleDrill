package BananaFructa.TFCSampleDrill;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySampleDrill;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TileEntitySamplerDrillModified extends TileEntitySampleDrill {

    private ItemStack getOreStack(World world, BlockPos pos, IBlockState state, boolean ignoreGrade)
    {
        for (VeinType vein : VeinRegistry.INSTANCE.getVeins().values())
        {
            if (vein.isOreBlock(state))
            {
                Block block = state.getBlock();
                if (vein.getOre() != null && vein.getOre().isGraded() && !ignoreGrade)
                {
                    ItemStack result = block.getPickBlock(state, null, world, pos, null);
                    result.setItemDamage(Ore.Grade.NORMAL.getMeta()); // ignore grade
                    return result;
                }
                else
                {
                    return block.getPickBlock(state, null, world, pos, null);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    private Map<String, Integer> scanSurroundingBlocks(World world, BlockPos center)
    {
        Map<String, Integer> results = new HashMap<>();
        net.minecraft.util.math.ChunkPos posc = world.getChunkFromBlockCoords(center).getPos();
        BlockPos firstCorner = new BlockPos(posc.x * 16,center.getY(), posc.z * 16);
        BlockPos secondCorner = new BlockPos(firstCorner.getX() + 16,0,firstCorner.getZ() + 16);
        for (BlockPos.MutableBlockPos pos : BlockPos.MutableBlockPos.getAllInBoxMutable(firstCorner,secondCorner))
        {
            ItemStack stack = getOreStack(world, pos, world.getBlockState(pos), true);
            if (!stack.isEmpty())
            {
                String oreName = stack.getDisplayName();

                if (results.containsKey(oreName))
                {
                    results.put(oreName,results.get(oreName) + 1);
                }
                else
                {
                    results.put(oreName, 1);
                }
            }
        }
        return results;
    }

    @Nonnull
    @Override
    public ItemStack createCoreSample(World world, int chunkX, int chunkZ, @Nullable ExcavatorHandler.MineralWorldInfo info) {
        ItemStack stack = new ItemStack(IEContent.itemCoresample);
        ItemNBTHelper.setLong(stack, "timestamp", world.getTotalWorldTime());
        ItemNBTHelper.setIntArray(stack, "coords", new int[]{world.provider.getDimension(), chunkX, chunkZ});
        if (info == null) {
            return stack;
        } else {

            if (!Config.hideIEMinerals) {
                if (info.mineralOverride != null) {
                    ItemNBTHelper.setString(stack, "mineral", info.mineralOverride.name);
                } else {
                    if (info.mineral == null) {
                        return stack;
                    }

                    ItemNBTHelper.setString(stack, "mineral", info.mineral.name);
                }
            }

            Map<String, Integer> results = scanSurroundingBlocks(world,this.pos);

            ItemNBTHelper.setInt(stack,"tfcOreEntries",results.size());
            int index = 0;
            for (String key : results.keySet()) {
                ItemNBTHelper.setString(stack,"tfcOreName" + index,key);
                ItemNBTHelper.setInt(stack,"tfcOreAmount" + index,results.get(key));
                index++;
            }

            if (!Config.hideIEMinerals) {
                if (ExcavatorHandler.mineralVeinCapacity >= 0 && info.depletion >= 0) {
                    ItemNBTHelper.setInt(stack, "depletion", info.depletion);
                } else {
                    ItemNBTHelper.setBoolean(stack, "infinite", true);
                }
            }

            return stack;
        }
    }

}
