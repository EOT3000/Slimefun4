package io.github.thebusybiscuit.slimefun4.core.attributes;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.energy.EnergyNet;
import me.mrCookieSlime.Slimefun.api.energy.EnergyNetComponentType;

/**
 * This Interface, when attached to a class that inherits from {@link SlimefunItem}, marks
 * the Item as an electric Block.
 * This will make this Block interact with an {@link EnergyNet}.
 * 
 * You can specify the Type of Block via {@link EnergyNetComponent#getEnergyComponentType()}.
 * You can also specify a capacity for this Block via {@link EnergyNetComponent#getCapacity()}.
 * 
 * @author TheBusyBiscuit
 *
 */
public interface EnergyNetComponent {
	
	/**
	 * This method returns the Type of {@link EnergyNetComponentType} this {@link SlimefunItem} represents.
	 * It describes how this Block will interact with an {@link EnergyNet}.
	 * 
	 * @return	The {@link EnergyNetComponentType} this {@link SlimefunItem} represents.
	 */
	EnergyNetComponentType getEnergyComponentType();
	
	/**
	 * This method returns the max amount of electricity this Block can hold.
	 * If the capacity is zero, then this Block cannot hold any electricity.
	 * 
	 * @return	The max amount of electricity this Block can store.
	 */
	int getCapacity();

}
