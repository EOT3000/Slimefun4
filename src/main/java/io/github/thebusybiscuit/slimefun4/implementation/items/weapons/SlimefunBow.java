package io.github.thebusybiscuit.slimefun4.implementation.items.weapons;

import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BowShootHandler;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This class represents a {@link SlimefunItem} that is a Bow.
 * It comes with a {@link BowShootHandler} to handle actions that shall be performed
 * whenever an {@link Arrow} fired from this {@link SlimefunBow} hits a target.
 * 
 * @author TheBusyBiscuit
 *
 */
public abstract class SlimefunBow extends SlimefunItem {

	public SlimefunBow(SlimefunItemStack item, ItemStack[] recipe) {
		super(Categories.WEAPONS, item, RecipeType.MAGIC_WORKBENCH, recipe);
	}
	
	@Override
	public void preRegister() {
		super.preRegister();
		addItemHandler(onShoot());
	}
	
	public abstract BowShootHandler onShoot();

}
