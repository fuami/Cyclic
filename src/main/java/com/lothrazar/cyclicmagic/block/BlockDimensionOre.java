package com.lothrazar.cyclicmagic.block;

import java.util.Random;
import com.lothrazar.cyclicmagic.registry.WorldGenRegistry;
import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockDimensionOre extends BlockOre {

	private Item	dropped;
	private int		droppedMeta;
	private int		randomMax;
	
	private int spawnChance = 0;
	private SpawnType spawn = null;
	public static enum SpawnType{
		ENDERMITE, SILVERFISH
	}

	public BlockDimensionOre(Item drop) {
		this(drop, 0);
	}

	public BlockDimensionOre(Item drop, int dmg) {
		this(drop, 0, 1);
	}

	public BlockDimensionOre(Item drop, int dmg, int max) {

		super();
		dropped = drop;
		droppedMeta = dmg;
		randomMax = max;
		this.setStepSound(SoundType.STONE);
		this.setHardness(3.0F).setResistance(5.0F);
	}
	
	public void setSpawnType(SpawnType t, int chance){
		this.spawn = t;
		this.spawnChance = chance;
	}

	public void trySpawnTriggeredEntity(World world, BlockPos pos){

		if(WorldGenRegistry.oreSpawns == false){
			return;//config has disabled spawning no matter what
		}
		
		if(this.spawn != null){
			int rand = world.rand.nextInt(100);
			if(rand < this.spawnChance){
				Entity e;
				 
				switch(this.spawn){
				case ENDERMITE:
					e = new EntityEndermite(world);
					e.setPosition(pos.getX(), pos.getY(), pos.getZ());
					break;
				case SILVERFISH:
					e = new EntitySilverfish(world); 
					//magma cube: setSlimeSize is private BOO
					e.setPosition(pos.getX(), pos.getY(), pos.getZ());
					break;
				default:
					e = null;
					break;
				}
				
				if(e != null)
					world.spawnEntityInWorld(e);
			}
		}
	}
	
	public int damageDropped(IBlockState state) {
		return droppedMeta;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return dropped;
	}

	public int quantityDropped(Random random) {
		if (randomMax == 1) { return 1; }
		return 1 + random.nextInt(randomMax);
	}

	@Override
	public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
		Random rand = world instanceof World ? ((World) world).rand : new Random();
		return MathHelper.getRandomIntegerInRange(rand, 2, 5);
	}
}