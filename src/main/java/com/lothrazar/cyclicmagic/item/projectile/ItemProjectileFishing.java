package com.lothrazar.cyclicmagic.item.projectile;
import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.entity.projectile.EntityFishingBolt;
import com.lothrazar.cyclicmagic.entity.projectile.EntityThrowableDispensable;
import com.lothrazar.cyclicmagic.item.base.BaseItemProjectile;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.UtilItemStack;
import com.lothrazar.cyclicmagic.util.UtilPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class ItemProjectileFishing extends BaseItemProjectile implements IHasRecipe {
  public ItemProjectileFishing() {
    super();
    this.setMaxDamage(1000);
    this.setMaxStackSize(1);
  }
  @Override
  public EntityThrowableDispensable getThrownEntity(World world, double x, double y, double z) {
    return new EntityFishingBolt(world, x, y, z);
  }
  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapelessRecipe(new ItemStack(this)
        , "enderpearl", "gunpowder", "string");
  }
  @Override
  public  void onItemThrow(ItemStack held, World world, EntityPlayer player, EnumHand hand) {
    this.doThrow(world, player, hand, new EntityFishingBolt(world, player));
    UtilItemStack.damageItem(player, held);
  }
  @Override
  public SoundEvent getSound() {
    // TODO Auto-generated method stub
    return SoundEvents.ENTITY_EGG_THROW;
  }
}
