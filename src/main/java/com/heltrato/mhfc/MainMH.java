package com.heltrato.mhfc;

import com.heltrato.mhfc.gui.KeybindingsMH;
import com.heltrato.mhfc.world.DimensionsMH;
import com.heltrato.mhfc.world.biome.BiomesMH;
import com.heltrato.mhfc.world.surfacebuilders.SurfaceBuildersMH;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.heltrato.mhfc.blocks.BlocksMH;
import com.heltrato.mhfc.entity.EntitiesMH;
import com.heltrato.mhfc.generator.BlockStatesMH;
import com.heltrato.mhfc.generator.ItemModelsMH;
import com.heltrato.mhfc.generator.LanguagesMH;
import com.heltrato.mhfc.generator.LootTableMH;
import com.heltrato.mhfc.generator.RecipesMH;
import com.heltrato.mhfc.items.ItemGroupMH;
import com.heltrato.mhfc.items.ItemsMH;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MainMH.MODID)
public class MainMH {
	public final static String MODID = "mhfc";
	public final static Logger log = LogManager.getLogger();

	final static ItemGroup ITEM_GROUP = new ItemGroupMH("groupMHFC");

	public MainMH() {
		IEventBus var = FMLJavaModLoadingContext.get().getModEventBus();
		var.addGenericListener(Item.class, this::addItem);
		var.addGenericListener(Block.class, this::addBlock);
		var.addGenericListener(SurfaceBuilder.class, this::addSurfaceBuilders);
		var.addGenericListener(Biome.class, this::addBiome);
		var.addListener(this::setFMLClientEvent);
		var.addListener(this::setFMLCommonEvent);
		var.addListener(this::setDataGatherEvent);
		setDeferredRegisterBus(var);
		MinecraftForge.EVENT_BUS.register(this);
	}

	// Register all features here.
	final void setDeferredRegisterBus(final IEventBus arg) {

		EntitiesMH.MOD_ENTITY.register(arg);
		log.info("Adding all " + MainMH.MODID.toUpperCase() + " ENTITIES!");
		// Checker subscriber
		log.info("Adding all " + MainMH.MODID.toUpperCase() + " "
				+ "deferred objects into the game using ForgeRegistries.");
	}

	// Data Generator event.
	final void setDataGatherEvent(final GatherDataEvent arg) {
		DataGenerator var = arg.getGenerator();
		if (arg.includeClient()) {
			var.addProvider(new ItemModelsMH(var, arg.getExistingFileHelper()));
			var.addProvider(new BlockStatesMH(var, arg.getExistingFileHelper()));
			var.addProvider(new LanguagesMH(var));
			log.info("Added client for " + MainMH.MODID.toUpperCase() + " data generator. ");
		}
		if (arg.includeServer()) {
			var.addProvider(new LootTableMH(var));
			var.addProvider(new RecipesMH(var));
			log.info("Added server for " + MainMH.MODID.toUpperCase() + " data generator. ");

		}
	}

	// Method features are put here
	final void setFMLCommonEvent(final FMLCommonSetupEvent arg) {
		EntitiesMH.addEntitySpawnPlacement();
		log.info("Implementing Spawn Placements for " + MainMH.MODID.toUpperCase() + " Entities. ");
		EntitiesMH.addEntityAttributes();
		log.info("Implementing Attributes for " + MainMH.MODID.toUpperCase() + " Entities. ");
		DimensionsMH.addBiomeProvider();
		log.info("Implementing Biome Provider for " + MainMH.MODID.toUpperCase());
		DimensionsMH.addChunkGenerator();
		log.info("Implementing Chunk Generator " + MainMH.MODID.toUpperCase());

		log.info("Firing" + MainMH.MODID.toUpperCase() + "FML Common Setup Event!");
	}

	// Client related features.
	@OnlyIn(Dist.CLIENT)
	final void setFMLClientEvent(final FMLClientSetupEvent arg) {
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BlocksMH::classifyBlockRenderTypes);
		log.info("Running block render method for " + MainMH.MODID.toUpperCase() + " at client side only. ");
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EntitiesMH::addEntityRenderer); 
		log.info("Running entity render method for " + MainMH.MODID.toUpperCase() + " at client side only. ");
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> KeybindingsMH::addKeyBindings);
		log.info("Running key bindings method for" + MainMH.MODID.toUpperCase() + " at client side only. ");
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> DimensionsMH::addDimensionRenderInfo);
		log.info("Running key Dimension Render Info for" + MainMH.MODID.toUpperCase() + " at client side only. ");
		log.info("Firing" + MainMH.MODID.toUpperCase() + "FML Client Setup Event!");
	}

	public static ItemGroup getItemGroup() {
		return ITEM_GROUP;
	}



	public void addSurfaceBuilders(final RegistryEvent.Register<SurfaceBuilder<?>> args){
			SurfaceBuildersMH.addSurfaceBuilders();
			log.info("Implementing Surface Builders for " + MainMH.MODID.toUpperCase());
	}

	public void addBiome(final RegistryEvent.Register<Biome> args) {
		BiomesMH.addBiomeIDS();
		log.info("Implementing Biomes for " + MainMH.MODID.toUpperCase());
	}

	public void addBlock(final RegistryEvent.Register<Block> arg){
		BlocksMH.initateBlocks();
		log.info("Adding all " + MainMH.MODID.toUpperCase() + " BLOCKS!");
	}

	public void addItem(final RegistryEvent.Register<Item> arg){
		ItemsMH.initiateItems();
		log.info("Adding all " + MainMH.MODID.toUpperCase() + " ITEMS!");
	}



}
