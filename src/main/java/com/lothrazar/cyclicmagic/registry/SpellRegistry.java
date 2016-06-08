package com.lothrazar.cyclicmagic.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lothrazar.cyclicmagic.item.ItemCyclicWand;
import com.lothrazar.cyclicmagic.spell.ISpell;
import com.lothrazar.cyclicmagic.spell.SpellInventory;
import com.lothrazar.cyclicmagic.spell.SpellLaunch;
import com.lothrazar.cyclicmagic.spell.SpellPlaceCircle;
import com.lothrazar.cyclicmagic.spell.SpellPlaceLine;
import com.lothrazar.cyclicmagic.spell.SpellPlaceStair;
import com.lothrazar.cyclicmagic.spell.SpellPotion;
import com.lothrazar.cyclicmagic.spell.SpellRangeBuild;
import com.lothrazar.cyclicmagic.spell.SpellRangePull;
import com.lothrazar.cyclicmagic.spell.SpellRangePush;
import com.lothrazar.cyclicmagic.spell.SpellRangeReplace;
import com.lothrazar.cyclicmagic.spell.SpellRangeRotate;
import com.lothrazar.cyclicmagic.util.UtilSpellCaster;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class SpellRegistry {
	public static boolean								renderOnLeft;

	private static Map<Integer, ISpell>	hashbook;
	 
	public static class Spells {

		// on purpose, not all spells are in here. only ones that needed to be
		// exposed
		public static SpellRangeRotate	rotate;
		public static SpellRangePush		push;
		public static SpellRangePull		pull;
		public static SpellRangeReplace	replacer;
		private static SpellInventory		inventory;
		private static SpellRangeBuild		reachdown;
		private static SpellLaunch				launch;
		private static SpellRangeBuild		reachup;
		private static SpellRangeBuild		reachplace;
		private static SpellPlaceLine		placeline;
		private static SpellPlaceCircle	placecircle;
		private static SpellPlaceStair		placestair;
		// public static SpellPlaceFloor placefloor;
	}

	public static void register() {
 
		hashbook = new HashMap<Integer, ISpell>();
		 
		int spellId = -1;// the smallest spell gets id zero
		
		Spells.inventory = new SpellInventory(++spellId, "inventory");
		registerSpell(Spells.inventory);
		
		Spells.rotate = new SpellRangeRotate(++spellId, "rotate");
		registerSpell(Spells.rotate);

		Spells.push = new SpellRangePush(++spellId, "push");
		registerSpell(Spells.push);

		Spells.pull = new SpellRangePull(++spellId, "pull");
		registerSpell(Spells.pull);

		Spells.replacer = new SpellRangeReplace(++spellId, "replacer");
		registerSpell(Spells.replacer);

		Spells.reachup = new SpellRangeBuild(++spellId, "reachup", SpellRangeBuild.PlaceType.UP);
		registerSpell(Spells.reachup);

		Spells.reachplace = new SpellRangeBuild(++spellId, "reachplace", SpellRangeBuild.PlaceType.PLACE);
		registerSpell(Spells.reachplace);

		Spells.reachdown = new SpellRangeBuild(++spellId, "reachdown", SpellRangeBuild.PlaceType.DOWN);
		registerSpell(Spells.reachdown);
		 
		SpellRangeBuild reachleft = new SpellRangeBuild(++spellId, "reachleft", SpellRangeBuild.PlaceType.LEFT);
		registerSpell(reachleft); 

		SpellRangeBuild reachright = new SpellRangeBuild(++spellId, "reachright", SpellRangeBuild.PlaceType.RIGHT);
		registerSpell(reachright); 
		
		SpellPotion levitate = new SpellPotion(++spellId, "levitation");
		levitate.setPotion(MobEffects.LEVITATION, 9, 0);
		registerSpell(levitate);

		SpellPotion slowfall = new SpellPotion(++spellId, "slowfall");
		slowfall.setPotion(PotionRegistry.slowfall, 9, 0);
		registerSpell(slowfall);

		//TODO: currently there is no tool for this
		// it would not have the BUILD TOGGLE TYPE.. once its working
		Spells.placeline = new SpellPlaceLine(++spellId, "placeline");
		registerSpell(Spells.placeline);

		Spells.placecircle = new SpellPlaceCircle(++spellId, "placecircle");
		registerSpell(Spells.placecircle);

		Spells.placestair = new SpellPlaceStair(++spellId, "placestair");
		registerSpell(Spells.placestair);
 
		Spells.launch = new SpellLaunch(++spellId, "launch");
		registerSpell(Spells.launch);
		

		ArrayList<ISpell>		 spellbookFly = new ArrayList<ISpell>();
		spellbookFly.add(Spells.launch);
//		spellbookFly.add(slowfall);
//		spellbookFly.add(levitate);
 
		ArrayList<ISpell>	spellbookBuild = new ArrayList<ISpell>();
		spellbookBuild.add(Spells.inventory);
		spellbookBuild.add(Spells.replacer);
		spellbookBuild.add(Spells.reachup);
		spellbookBuild.add(Spells.reachplace);
		spellbookBuild.add(Spells.reachdown);
		spellbookBuild.add(reachleft);
		spellbookBuild.add(reachright);
//		spellbookBuild.add(Spells.rotate);
//		spellbookBuild.add(Spells.push);
//		spellbookBuild.add(Spells.pull);
 
		ItemRegistry.cyclic_wand_build.setSpells(spellbookBuild);
	}
 
	private static void registerSpell(ISpell spell) {
		hashbook.put(spell.getID(), spell);
	}

	public static boolean spellsEnabled(EntityPlayer player) {
		// current requirement is only a wand
		return UtilSpellCaster.getPlayerWandIfHeld(player) != null;
	}

	public static ISpell getSpellFromID(int id) {
		if (hashbook.containsKey(id)) {
			return hashbook.get(id); 
		}

		return null;
	}

	public static void syncConfig(Configuration config) {

	}

	public static List<ISpell> getSpellbook(ItemStack wand) {
		return ((ItemCyclicWand)wand.getItem()).getSpells();
	}
	public static ISpell next(ItemStack wand, ISpell spell) {

		List<ISpell> book = getSpellbook(wand);

		int indexCurrent = book.indexOf(spell);

		int indexNext = indexCurrent + 1;
		if (indexNext >= book.size()) {
			indexNext = 0;
		}

		return book.get(indexNext);
	}

	public static ISpell prev(ItemStack wand, ISpell spell) {

		List<ISpell> book = getSpellbook(wand);

		int indexCurrent = book.indexOf(spell);
		int indexPrev;

		if (indexCurrent <= 0)// not that it ever WOULD be.. negative.. yeah
			indexPrev = book.size() - 1;
		else
			indexPrev = indexCurrent - 1;

		ISpell ret = book.get(indexPrev);

		return ret;
	}
}
