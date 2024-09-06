package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.registry.TabRegistry;
import dev.tauri.rsjukeboxes.util.ItemHelper;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRSJukebox extends BlockJukebox implements RSJBlock {
    public AbstractRSJukebox(String blockName) {
        super();
        setRegistryName(RSJukeboxes.MOD_ID + ":" + blockName);
        setUnlocalizedName(RSJukeboxes.MOD_ID + "." + blockName);
        this.setDefaultState(getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
        setCreativeTab(TabRegistry.TAB_JUKEBOXES);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        ItemHelper.applyGenericToolTip(getUnlocalizedName(), tooltip, advanced);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(World pLevel, BlockPos pPos, IBlockState pState, EntityLivingBase pPlacer, ItemStack pStack) {
        super.onBlockPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        EnumFacing facing = pPlacer.getHorizontalFacing().getOpposite();
        pLevel.setBlockState(pPos, pState.withProperty(BlockHorizontal.FACING, facing), 3);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HAS_RECORD, BlockHorizontal.FACING);
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean onBlockActivated(World pLevel, BlockPos pPos, IBlockState state, EntityPlayer pPlayer, EnumHand pHand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!pLevel.isRemote) {
            TileEntity blockentity = pLevel.getTileEntity(pPos);
            if (blockentity instanceof AbstractRSJukeboxBE) {
                AbstractRSJukeboxBE jukebox = (AbstractRSJukeboxBE) blockentity;
                ItemStack item = pPlayer.getHeldItem(pHand);
                if (jukebox.hasPlayableItem()) {
                    jukebox.popOutRecord(0);
                    jukebox.markDirty();
                    return true;
                }
                if (item.getItem() instanceof ItemRecord && !item.isEmpty()) {
                    jukebox.itemStackHandler.setStackInSlot(0, item.copy());
                    item.shrink(1);
                    jukebox.markDirty();
                    pPlayer.addStat(StatList.RECORD_PLAYED);
                    return true;
                }
            }
        }

        return false;
    }

    @ParametersAreNonnullByDefault
    public void onBlockHarvested(World pLevel, BlockPos pPos, IBlockState pState, EntityPlayer player) {
        TileEntity blockentity = pLevel.getTileEntity(pPos);
        if (blockentity instanceof AbstractRSJukeboxBE) {
            ((AbstractRSJukeboxBE) blockentity).popOutRecords();
        }

        super.onBlockHarvested(pLevel, pPos, pState, player);
    }

    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(IBlockState pState) {
        return true;
    }

    public AbstractRSJukeboxBE getJukeboxBE(IBlockAccess level, BlockPos pos) {
        if (level == null) return null;
        if (pos == null) return null;
        TileEntity tile = level.getTileEntity(pos);
        if (tile instanceof AbstractRSJukeboxBE)
            return (AbstractRSJukeboxBE) tile;
        return null;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getStrongPower(IBlockState pState, IBlockAccess pLevel, BlockPos pPos, EnumFacing direction) {
        if (pLevel instanceof WorldClient) return 0;
        AbstractRSJukeboxBE jukeboxBE = getJukeboxBE(pLevel, pPos);
        if (jukeboxBE == null) return 0;
        EnumFacing blockDirection = pState.getValue(BlockHorizontal.FACING);
        EnumFacing directionRotated = EnumFacing.fromAngle(blockDirection.getHorizontalAngle() + direction.getHorizontalAngle());
        if (blockDirection.getAxis() == EnumFacing.Axis.Z) directionRotated = directionRotated.getOpposite();
        return getOutputSignal(pState, pLevel, pPos, direction.getAxis() == EnumFacing.Axis.Y ? direction : directionRotated, jukeboxBE);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onNeighborChange(IBlockAccess pLevel, BlockPos pPos, BlockPos pFromPos) {
        super.onNeighborChange(pLevel, pPos, pFromPos);
        if (pLevel instanceof WorldClient) return;
        AbstractRSJukeboxBE jukeboxBE = getJukeboxBE(pLevel, pPos);
        if (jukeboxBE == null) return;
        IBlockState pState = pLevel.getBlockState(pPos);
        EnumFacing blockDirection = pState.getValue(BlockHorizontal.FACING);
        Map<EnumFacing, Integer> signals = new HashMap<>();
        for (EnumFacing direction : EnumFacing.values()) {
            if (!pPos.offset(direction).equals(pFromPos)) continue;
            EnumFacing directionRotated = EnumFacing.fromAngle(blockDirection.getHorizontalAngle() + direction.getHorizontalAngle());
            if (blockDirection.getAxis() == EnumFacing.Axis.Z) directionRotated = directionRotated.getOpposite();
            signals.put(direction.getAxis() == EnumFacing.Axis.Y ? direction : directionRotated, pLevel.getStrongPower(pPos.offset(direction), direction));
        }
        processInputSignal(pState, pLevel, pPos, pFromPos, signals, jukeboxBE);
    }


    @ParametersAreNonnullByDefault
    public int getOutputSignal(IBlockState state, IBlockAccess level, BlockPos pos, EnumFacing direction, AbstractRSJukeboxBE jukeboxBE) {
        if (jukeboxBE.isPlaying()) {
            return 15;
        }
        return 0;
    }

    @ParametersAreNonnullByDefault
    public void processInputSignal(IBlockState state, IBlockAccess level, BlockPos pos, BlockPos changedPos, Map<EnumFacing, Integer> signals, AbstractRSJukeboxBE jukeboxBE) {
    }

    @ParametersAreNonnullByDefault
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public int getWeakPower(IBlockState blockState, IBlockAccess pLevel, BlockPos pPos, EnumFacing side) {
        TileEntity blockentity = pLevel.getTileEntity(pPos);
        if (blockentity instanceof AbstractRSJukeboxBE) {
            Item item = ((AbstractRSJukeboxBE) blockentity).getPlayingItem().getItem();
            if (item instanceof ItemRecord) {
                return Item.getIdFromItem(item) + 1 - Item.getIdFromItem(Items.RECORD_13);
            }
        }

        return 0;
    }

    @Override
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createBlockEntity(world, state);
    }

    public abstract TileEntity createBlockEntity(World world, IBlockState state);
}
