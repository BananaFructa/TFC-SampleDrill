package BananaFructa.TFCSampleDrill;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntitySampleDrill;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Mod(modid = TFCSampleDrill.modId,name = TFCSampleDrill.name,version = TFCSampleDrill.version)
public class TFCSampleDrill {

    public static final String modId = "tfcsampledrill";
    public static final String name = "TFC Sample Drill";
    public static final String version = "1.0.1";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.load(event.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        if (Config.hideIEMinerals) ExcavatorHandler.mineralList.clear();
        GameRegistry.registerTileEntity(TileEntitySamplerDrillModified.class,new ResourceLocation(modId,TileEntitySamplerDrillModified.class.getSimpleName()));
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote) return;
        TileEntity entity = event.getWorld().getTileEntity(event.getPos());
        if (entity instanceof TileEntitySampleDrill) {
            event.getWorld().setTileEntity(entity.getPos(), new TileEntitySamplerDrillModified());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onToolTip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == IEContent.itemCoresample) {
            List<String> toolTip = event.getToolTip();
            NBTTagCompound compound = event.getItemStack().getTagCompound();
            toolTip.remove(1);
            if (compound != null && compound.hasKey("tfcOreEntries") && compound.getInteger("tfcOreEntries") != 0) {
                int oreEntriesCount = compound.getInteger("tfcOreEntries");
                for (int o = 0; o < oreEntriesCount; o++) {
                    toolTip.add(1, "\u00A7b"+compound.getInteger("tfcOreAmount" + o) + " " + compound.getString("tfcOreName" + o));
                }
            } else {
                toolTip.add(1,"\u00A7cNo Ores");
            }
        }
    }

}
