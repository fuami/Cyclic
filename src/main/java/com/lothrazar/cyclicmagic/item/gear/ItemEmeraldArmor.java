package com.lothrazar.cyclicmagic.item.gear;
import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.module.EmeraldArmorModule;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemEmeraldArmor extends ItemArmor implements IHasRecipe {
  public ItemEmeraldArmor(EntityEquipmentSlot armorType) {
    super(EmeraldArmorModule.ARMOR_MATERIAL, 0, armorType);
  }
  @Override
  public void addRecipe() {
    switch (this.armorType) {
    case CHEST:
      GameRegistry.addShapedRecipe(new ItemStack(this), "e e", "eee", "eee", 'e', new ItemStack(EmeraldArmorModule.REPAIR));
      break;
    case FEET:
      GameRegistry.addShapedRecipe(new ItemStack(this), "e e", "e e", "   ", 'e', new ItemStack(EmeraldArmorModule.REPAIR));
      GameRegistry.addShapedRecipe(new ItemStack(this), "   ", "e e", "e e", 'e', new ItemStack(EmeraldArmorModule.REPAIR));
      break;
    case HEAD:
      GameRegistry.addShapedRecipe(new ItemStack(this), "eee", "e e", "   ", 'e', new ItemStack(EmeraldArmorModule.REPAIR));
      GameRegistry.addShapedRecipe(new ItemStack(this), "   ", "eee", "e e", 'e', new ItemStack(EmeraldArmorModule.REPAIR));
      break;
    case LEGS:
      GameRegistry.addShapedRecipe(new ItemStack(this), "eee", "e e", "e e", 'e', new ItemStack(EmeraldArmorModule.REPAIR));
      break;
    case MAINHAND:
      break;
    case OFFHAND:
      break;
    default:
      break;
    }
  }
}