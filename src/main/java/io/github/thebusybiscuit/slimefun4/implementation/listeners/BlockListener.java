package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.inventory.ItemUtils;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.utils.FireworkUtils;
import me.mrCookieSlime.Slimefun.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.NotPlaceable;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockPlaceHandler;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;

public class BlockListener implements Listener {
	
	// Materials that require a Block under it, e.g. Pressure Plates
	private final Set<Material> sensitiveMaterials = new HashSet<>();
	
	public BlockListener(SlimefunPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		sensitiveMaterials.add(Material.STONE_PRESSURE_PLATE);
		sensitiveMaterials.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		sensitiveMaterials.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		sensitiveMaterials.addAll(Tag.SAPLINGS.getValues());
		sensitiveMaterials.addAll(Tag.WOODEN_PRESSURE_PLATES.getValues());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockRegister(BlockPlaceEvent e) {
		if (BlockStorage.hasBlockInfo(e.getBlock())) {
			e.setCancelled(true);
			return;
		}
		
		ItemStack item = e.getItemInHand();
		
		SlimefunItem sfItem = SlimefunItem.getByItem(item);
		if (sfItem != null && Slimefun.isEnabled(e.getPlayer(), sfItem, true) && !(sfItem instanceof NotPlaceable)) {
			if (!Slimefun.hasUnlocked(e.getPlayer(), sfItem, true)) {
				e.setCancelled(true);
			}
			else {
				BlockState state = e.getBlock().getState();
				boolean supportsPersistentData = state instanceof TileState;
				
				if (supportsPersistentData) {
					SlimefunPlugin.getBlockDataService().setBlockData((TileState) state, sfItem.getID());
				}
				
				BlockStorage.addBlockInfo(e.getBlock(), "id", sfItem.getID(), true);
				
				SlimefunBlockHandler blockHandler = SlimefunPlugin.getRegistry().getBlockHandlers().get(sfItem.getID());
				if (blockHandler != null) {
					blockHandler.onPlace(e.getPlayer(), e.getBlock(), sfItem);
				} 
				else {
					sfItem.callItemHandler(BlockPlaceHandler.class, handler ->
						handler.onBlockPlace(e, item)
					);
				}
			}
		}
		else {
			for (ItemHandler handler : SlimefunItem.getHandlers(BlockPlaceHandler.class)) {
				if (((BlockPlaceHandler) handler).onBlockPlace(e, item)) break;
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		ItemStack item = e.getItemInHand();

		if (SlimefunManager.isItemSimilar(item, SlimefunItems.BASIC_CIRCUIT_BOARD, true)) e.setCancelled(true);
		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.ADVANCED_CIRCUIT_BOARD, true)) e.setCancelled(true);
		
		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.CARBON, false)) e.setCancelled(true);
		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.COMPRESSED_CARBON, false)) e.setCancelled(true);
		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.CARBON_CHUNK, false)) e.setCancelled(true);

		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.ANDROID_MEMORY_CORE, false)) e.setCancelled(true);
		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.LAVA_CRYSTAL, false)) e.setCancelled(true);

		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.TINY_URANIUM, false)) e.setCancelled(true);
		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.SMALL_URANIUM, false)) e.setCancelled(true);
		
		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.BROKEN_SPAWNER, false)) e.setCancelled(true);
		else if (SlimefunManager.isItemSimilar(item, SlimefunItems.CHRISTMAS_PRESENT, false)) {
			e.setCancelled(true);
			
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				ItemUtils.consumeItem(item, false);
			}
			
			FireworkUtils.launchRandom(e.getPlayer(), 3);
			List<ItemStack> gifts = new ArrayList<>();
			
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_HOT_CHOCOLATE, 1));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_CHOCOLATE_APPLE, 4));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_CARAMEL_APPLE, 4));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_CAKE, 4));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_COOKIE, 8));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_PRESENT, 1));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_EGG_NOG, 1));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_MILK, 1));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_APPLE_CIDER, 1));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_FRUIT_CAKE, 4));
			gifts.add(new CustomItem(SlimefunItems.CHRISTMAS_APPLE_PIE, 4));
			gifts.add(new ItemStack(Material.EMERALD));
			
			e.getBlockPlaced().getWorld().dropItemNaturally(e.getBlockPlaced().getLocation(), gifts.get(ThreadLocalRandom.current().nextInt(gifts.size())));
		}
		else if (e.getBlock().getY() != e.getBlockAgainst().getY() && (SlimefunManager.isItemSimilar(item, SlimefunItems.CARGO_INPUT, false) || SlimefunManager.isItemSimilar(item, SlimefunItems.CARGO_OUTPUT, false) || SlimefunManager.isItemSimilar(item, SlimefunItems.CARGO_OUTPUT_ADVANCED, false))) {
			SlimefunPlugin.getLocal().sendMessage(e.getPlayer(), "machines.CARGO_NODES.must-be-placed", true);
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		boolean allow = true;
		List<ItemStack> drops = new ArrayList<>();
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
		int fortune = getFortuneLevel(item, e.getBlock());
		
		Block block2 = e.getBlock().getRelative(BlockFace.UP);
		
		if (sensitiveMaterials.contains(block2.getType())) {
			SlimefunItem sfItem = BlockStorage.check(e.getBlock().getRelative(BlockFace.UP));
			
			if (sfItem == null) {
				BlockState state = block2.getState();
				
				if (state instanceof TileState) {
					Optional<String> blockData = SlimefunPlugin.getBlockDataService().getBlockData((TileState) state);
					
					if (blockData.isPresent()) {
						sfItem = SlimefunItem.getByID(blockData.get());
					}
				}
			}
			
			if (sfItem != null && !(sfItem instanceof HandledBlock)) {
				SlimefunBlockHandler blockHandler = SlimefunPlugin.getRegistry().getBlockHandlers().get(sfItem.getID());
				
				if (blockHandler != null) {
					allow = blockHandler.onBreak(e.getPlayer(), block2, sfItem, UnregisterReason.PLAYER_BREAK);
				} 
				
				if (allow) {
					block2.getWorld().dropItemNaturally(block2.getLocation(), BlockStorage.retrieve(block2));
					block2.setType(Material.AIR);
				}
				else {
					e.setCancelled(true);
					return;
				}
			}
		}

		SlimefunItem sfItem = BlockStorage.check(e.getBlock());
		
		if (sfItem == null) {
			BlockState state = e.getBlock().getState();
			
			if (state instanceof TileState) {
				Optional<String> blockData = SlimefunPlugin.getBlockDataService().getBlockData((TileState) state);
				
				if (blockData.isPresent()) {
					sfItem = SlimefunItem.getByID(blockData.get());
				}
			}
		}
		
		if (sfItem != null && !(sfItem instanceof HandledBlock)) {
			SlimefunBlockHandler blockHandler = SlimefunPlugin.getRegistry().getBlockHandlers().get(sfItem.getID());
			
			if (blockHandler != null) {
				allow = blockHandler.onBreak(e.getPlayer(), e.getBlock(), sfItem, UnregisterReason.PLAYER_BREAK);
			} 
			else {
				sfItem.callItemHandler(BlockBreakHandler.class, handler ->
					handler.onBlockBreak(e, item, fortune, drops)
				);
			}
			
			if (allow) {
				drops.addAll(sfItem.getDrops());
				BlockStorage.clearBlockInfo(e.getBlock());
			}
			else {
				e.setCancelled(true);
				return;
			}
		}
		if (item.getType() != Material.AIR) {
			for (ItemHandler handler : SlimefunItem.getHandlers(BlockBreakHandler.class)) {
				if (((BlockBreakHandler) handler).onBlockBreak(e, item, fortune, drops)) break;
			}
		}
		
		if (!drops.isEmpty()) {
			e.getBlock().setType(Material.AIR);
			
			if (e.isDropItems()) {
				for (ItemStack drop : drops) {
					if (drop != null) {
						e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
					}
				}
			}
		}
	}

	private int getFortuneLevel(ItemStack item, Block b) {
		int fortune = 1;
		
		if (item != null && item.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS) && !item.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
			Random random = ThreadLocalRandom.current();
			
			fortune = random.nextInt(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) + 2) - 1;
			if (fortune <= 0) fortune = 1;
			fortune = (b.getType() == Material.LAPIS_ORE ? 4 + random.nextInt(5) : 1) * (fortune + 1);
		}
		
		return fortune;
	}
}
