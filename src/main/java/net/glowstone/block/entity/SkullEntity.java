package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.block.entity.state.GlowSkull;
import net.glowstone.constants.GlowBlockEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.SkullType;
import org.bukkit.material.Skull;

public class SkullEntity extends BlockEntity {

    private byte type;
    private byte rotation;
    private PlayerProfile owner;

    public SkullEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:skull");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        type = tag.getByte("SkullType");

        if (BlockSkull.canRotate((Skull) getBlock().getState().getData())) {
            rotation = tag.getByte("Rot");
        }
        if (tag.containsKey("Owner")) {
            CompoundTag ownerTag = tag.getCompound("Owner");
            owner = PlayerProfile.fromNBT(ownerTag);
        } else if (tag.containsKey("ExtraType")) {
            // Pre-1.8 uses just a name, instead of a profile object
            String name = tag.getString("ExtraType");
            if (name != null && !name.isEmpty()) {
                owner = PlayerProfile.getProfile(name);
            }
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putByte("SkullType", type);
        if (BlockSkull.canRotate((Skull) getBlock().getState().getData())) {
            tag.putByte("Rot", rotation);
        }
        if (type == BlockSkull.getType(SkullType.PLAYER) && owner != null) {
            tag.putCompound("Owner", owner.toNBT());
        }
    }

    @Override
    public GlowBlockState getState() {
        return new GlowSkull(block);
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);
        CompoundTag nbt = new CompoundTag();
        saveNbt(nbt);
        player.sendBlockEntityChange(getBlock().getLocation(), GlowBlockEntity.SKULL, nbt);
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getRotation() {
        return rotation;
    }

    public void setRotation(byte rotation) {
        this.rotation = rotation;
    }

    public PlayerProfile getOwner() {
        return owner;
    }

    public void setOwner(PlayerProfile owner) {
        this.owner = owner;
        type = BlockSkull.getType(SkullType.PLAYER);
    }
}
