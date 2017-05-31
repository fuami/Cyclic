package com.lothrazar.cyclicmagic.component.placer;
import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.block.BlockBaseFacingInventory;
import com.lothrazar.cyclicmagic.block.BlockBaseFacingOmni;
import com.lothrazar.cyclicmagic.block.IBlockHasTESR;
import com.lothrazar.cyclicmagic.block.tileentity.MachineTESR;
import com.lothrazar.cyclicmagic.gui.ModGuiHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPlacer extends BlockBaseFacingOmni implements IHasRecipe {
  public BlockPlacer() {
    super(Material.IRON);
    this.setHardness(3.0F).setResistance(5.0F);
    this.setSoundType(SoundType.METAL);
    this.setTickRandomly(true);
    this.setGuiId( ModGuiHandler.GUI_INDEX_PLACER);
  }
  @Override
  public TileEntity createTileEntity(World worldIn, IBlockState state) {
    return new TileEntityPlacer();
  }

  @Override
  public IRecipe addRecipe() {
    return GameRegistry.addShapedRecipe(new ItemStack(this),
        "rsr",
        "gbg",
        "ooo",
        'o', Blocks.COBBLESTONE,
        'g', Items.IRON_INGOT,
        's', Blocks.DISPENSER,
        'r', Blocks.STONE,
        'b', Items.REDSTONE);
  }
}
