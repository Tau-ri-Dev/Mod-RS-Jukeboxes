package dev.tauri.rsjukeboxes.state;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Implemented by {@link BlockEntity} which provides at least one {@link State}
 * 
 * @author MrJake
 */
public interface StateProviderInterface {
	
	/**
	 * Server-side method. Called on {@link BlockEntity} to get specified {@link State}.
	 * 
	 * @param stateType {@link StateTypeEnum} State to be collected/returned
	 * @return {@link State} instance
	 */
	State getState(StateTypeEnum stateType);
	
	/**
	 * Client-side method. Called on {@link BlockEntity} to get specified {@link State} instance
	 * to recreate State by deserialization
	 * 
	 * @param stateType {@link StateTypeEnum} State to be deserialized
	 * @return deserialized {@link State}
	 */
	State createState(StateTypeEnum stateType);

	/**
	 * Client-side method. Sets appropriate fields in client-side tile entity for it
	 * to mirror the server-side tile entity
	 * 
	 * @param stateType {@link StateTypeEnum} State to be applied
	 * @param state {@link State} instance obtained from packet
	 */
	@OnlyIn(Dist.CLIENT)
	void setState(StateTypeEnum stateType, State state);
}
