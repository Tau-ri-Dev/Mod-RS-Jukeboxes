package dev.tauri.rsjukeboxes.integration.cctweaked.methods;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.ObjectArguments;
import dan200.computercraft.api.network.Packet;
import dan200.computercraft.api.network.PacketReceiver;
import dan200.computercraft.api.network.PacketSender;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.tauri.rsjukeboxes.integration.cctweaked.CCDevice;
import dev.tauri.rsjukeboxes.integration.cctweaked.CCTweakedHelper;
import dev.tauri.rsjukeboxes.util.ClassSeeker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractCCMethods<TILE extends BlockEntity> implements IPeripheral, ICCDevice, PacketReceiver, PacketSender {
    protected final TILE deviceTile;
    protected final CCDevice device;

    protected final List<IComputerAccess> computers = new ArrayList<>();

    public AbstractCCMethods(TILE deviceTile, CCDevice device) {
        this.deviceTile = deviceTile;
        this.device = device;
    }

    @Override
    public Object getTarget() {
        return deviceTile;
    }

    @Override
    public void connectToWirelessNetwork() {
        if (deviceTile.getLevel() == null) return;
        if (deviceTile.getLevel().getServer() == null) return;
        var wirelessNetwork = ComputerCraftAPI.getWirelessNetwork(deviceTile.getLevel().getServer());
        wirelessNetwork.addReceiver(this);
    }

    @Override
    public void disconnectFromWirelessNetwork() {
        if (deviceTile.getLevel() == null) return;
        if (deviceTile.getLevel().getServer() == null) return;
        var wirelessNetwork = ComputerCraftAPI.getWirelessNetwork(deviceTile.getLevel().getServer());
        wirelessNetwork.removeReceiver(this);
    }

    @Override
    public void attach(IComputerAccess computer) {
        computers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        computers.remove(computer);
    }

    @Override
    public void sendSignal(String eventName, Object... objects) {
        for (IComputerAccess computer : computers) {
            int length = objects.length + 1;
            Object[] attachmentObjects = new Object[length];
            attachmentObjects[0] = computer.getAttachmentName();
            System.arraycopy(objects, 0, attachmentObjects, 1, length - 1);
            computer.queueEvent(eventName, attachmentObjects);
        }
        int length = objects.length + 1;
        Object[] attachmentObjects = new Object[length];
        attachmentObjects[0] = eventName;
        System.arraycopy(objects, 0, attachmentObjects, 1, length - 1);
        var packet = new Packet(10, 11, attachmentObjects, this);
        if (deviceTile.getLevel() == null) return;
        if (deviceTile.getLevel().getServer() == null) return;
        var wirelessNetwork = ComputerCraftAPI.getWirelessNetwork(deviceTile.getLevel().getServer());
        wirelessNetwork.transmitSameDimension(packet, 64);
    }

    @Override
    public String getType() {
        return device.deviceName;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return (other instanceof AbstractCCMethods<?>) && (this.deviceTile == ((AbstractCCMethods<?>) other).deviceTile);
    }

    @Override
    public Level getLevel() {
        return deviceTile.getLevel();
    }

    @Override
    public Vec3 getPosition() {
        return deviceTile.getBlockPos().getCenter();
    }

    @Override
    public String getSenderID() {
        return getType();
    }

    @Override
    public double getRange() {
        return 64;
    }

    @Override
    public boolean isInterdimensional() {
        return false;
    }

    @Override
    public void receiveSameDimension(Packet packet, double distance) {
        if (packet.sender() == this) return;
        if (packet.channel() != 11) return;

        var mess = packet.payload();
        if (mess instanceof String s) {
            mess = new Object[]{s};
        }
        if (mess instanceof Map<?, ?> map) {
            var list = CCTweakedHelper.getCorrectlyOrderedTableValues(map);
            var array = new Object[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            mess = array;
        }
        if ((!(mess instanceof Object[] data) || data.length < 1)) {
            return;
        }

        Object[] response = null;

        var methods = ClassSeeker.getMethodsAnnotatedWith(this.getClass(), LuaFunction.class);
        for (var m : methods) {
            if (response != null) break;
            if (m.getName().equalsIgnoreCase(data[0].toString())) {
                var params = new Object[data.length - 1];
                if (params.length == 0) {
                    try {
                        response = (Object[]) m.invoke(this);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                System.arraycopy(data, 1, params, 0, data.length - 1);
                try {
                    response = (Object[]) m.invoke(this, null, new ObjectArguments(params));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }


        var p = new Packet(packet.replyChannel(), 11, response, this);
        if (deviceTile.getLevel() == null) return;
        if (deviceTile.getLevel().getServer() == null) return;
        var wirelessNetwork = ComputerCraftAPI.getWirelessNetwork(deviceTile.getLevel().getServer());
        wirelessNetwork.transmitSameDimension(p, 64);
    }

    @Override
    public void receiveDifferentDimension(Packet packet) {

    }
}
