/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.cyclicmagic.item.enderbook;

import java.util.ArrayList;
import java.util.List;
import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.data.BlockPosDim;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.gui.ForgeGuiHandler;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.item.core.BaseItem;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.registry.LootTableRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilEntity;
import com.lothrazar.cyclicmagic.util.UtilNBT;
import com.lothrazar.cyclicmagic.util.UtilWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnderBook extends BaseItem implements IHasRecipe, IContent {

  public static String KEY_LOC = "location";
  public static String KEY_LARGEST = "loc_largest";
  public static int maximumSaved = 16;
  public static int expDistRatio = 10;
  public static final int BTNS_PER_COLUMN = 8;

  public ItemEnderBook() {
    super();
    this.setMaxStackSize(1);
  }

  public static ArrayList<BlockPosDim> getLocations(ItemStack itemStack) {
    ArrayList<BlockPosDim> list = new ArrayList<BlockPosDim>();
    String KEY;
    int end = getLargestSlot(itemStack);
    for (int i = 0; i <= end; i++) {
      KEY = KEY_LOC + "_" + i;
      String csv = UtilNBT.getItemStackNBT(itemStack).getString(KEY);
      if (csv == null || csv.isEmpty()) {
        continue;
      }
      list.add(new BlockPosDim(csv));
    }
    return list;
  }

  private static int getLocationsCount(ItemStack itemStack) {
    return getLocations(itemStack).size();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
    tooltip.add(UtilChat.lang(getTooltip()) + getLocationsCount(stack));
  }

  public static int getLargestSlot(ItemStack itemStack) {
    return UtilNBT.getItemStackNBT(itemStack).getInteger(KEY_LARGEST);
  }

  public static int getEmptySlotAndIncrement(ItemStack itemStack) {
    int empty = UtilNBT.getItemStackNBT(itemStack).getInteger(KEY_LARGEST);
    if (empty == 0) {
      empty = 1;
    } // first index is 1 not zero
    UtilNBT.getItemStackNBT(itemStack).setInteger(KEY_LARGEST, empty + 1);
    return empty;
  }

  public static ItemStack getPlayersBook(EntityPlayer player) {
    ItemStack book = player.getHeldItem(EnumHand.MAIN_HAND);
    if (book == null || book.getItem() instanceof ItemEnderBook == false) {
      book = player.getHeldItem(EnumHand.OFF_HAND);
    }
    UtilNBT.getItemStackNBT(book);
    return book;
  }

  public static void deleteWaypoint(EntityPlayer player, int slot) {
    ItemStack book = getPlayersBook(player);
    book.getTagCompound().removeTag(KEY_LOC + "_" + slot);
  }

  public static void saveCurrentLocation(EntityPlayer player, String name) {
    ItemStack book = getPlayersBook(player);
    int id = getEmptySlotAndIncrement(book);
    BlockPosDim loc = new BlockPosDim(id, player, name);
    book.getTagCompound().setString(KEY_LOC + "_" + id, loc.toCSV());
  }

  private static BlockPosDim getLocation(ItemStack stack, int slot) {
    String csv = stack.getTagCompound().getString(ItemEnderBook.KEY_LOC + "_" + slot);
    if (csv == null || csv.isEmpty()) {
      return null;
    }
    return new BlockPosDim(csv);
  }

  public static BlockPos getLocationPos(ItemStack stack, int slot) {
    BlockPosDim loc = getLocation(stack, slot);
    if (loc == null) {
      return null;
    }
    return new BlockPos(loc.X, loc.Y, loc.Z);
  }

  public static boolean teleport(EntityPlayer player, int slot) {
    ItemStack book = getPlayersBook(player);
    String csv = book.getTagCompound().getString(ItemEnderBook.KEY_LOC + "_" + slot);
    if (csv == null || csv.isEmpty()) {
      return false;
    }
    BlockPosDim loc = getLocation(book, slot);
    if (player.dimension != loc.dimension) {
      return false;//button was disabled anyway,... but just in case 
    }
    //something in vanilla 
    if (player instanceof EntityPlayerMP) {//server only
      // thanks so much to
      // http://www.minecraftforge.net/forum/index.php?topic=18308.0 
      //also moving up so  not stuck in floor
      boolean success = UtilEntity.enderTeleportEvent(player, player.world, loc.X, loc.Y + 0.1, loc.Z);
      if (success) { // try and force chunk loading it it worked 
        player.getEntityWorld().getChunk(new BlockPos(loc.X, loc.Y, loc.Z)).setModified(true);
      }
    }
    return true;
  }

  @Override
  public IRecipe addRecipe() {
    RecipeRegistry.addShapelessRecipe(new ItemStack(this), new ItemStack(this));
    return RecipeRegistry.addShapedRecipe(new ItemStack(this), "ene", "ebe", "eee",
        'e', "enderpearl",
        'b', Items.BOOK,
        'n', "blockEmerald");
    // if you want to clean out the book and start over
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entityPlayer, EnumHand hand) {
    ItemStack stack = entityPlayer.getHeldItem(hand);
    if (stack == null || stack.getItem() == null) {
      return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
    }
    //Minecraft.getMinecraft().displayGuiScreen(new GuiEnderBook(entityPlayer, stack));
    entityPlayer.openGui(ModCyclic.instance, ForgeGuiHandler.GUI_INDEX_WAYPOINT, world, 0, 0, 0);
    return super.onItemRightClick(world, entityPlayer, hand);
  }

  public static int getExpCostPerTeleport(EntityPlayer player, ItemStack book, int slot) {
    if (expDistRatio <= 0) {
      return 0;
    }
    BlockPos toPos = getLocationPos(book, slot);
    int distance = (int) UtilWorld.distanceBetweenHorizontal(toPos, player.getPosition());
    return Math.round(distance / expDistRatio);
  }

  @Override
  public void register() {
    ItemRegistry.register(this, "book_ender", GuideCategory.TRANSPORT);
    LootTableRegistry.registerLoot(this);
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("EnderBook", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
    maximumSaved = config.getInt("EnderBookMaxSaved", Const.ConfigCategory.modpackMisc,
        16, 1, 64, "Maximum number of saved waypoints in the ender book.  It still uses " + BTNS_PER_COLUMN +
            " per column, and putting too many may send it offscreen");
    expDistRatio = config.getInt("EnderBookExpCostRatio", Const.ConfigCategory.modpackMisc,
        10, 0, 100, "The exp cost of teleporting is [the horizontal distance] divided by [this number] rounded to the nearest integer.  For example, if this number is 10, then teleporting 20 blocks costs 2 exp");
  }
}
