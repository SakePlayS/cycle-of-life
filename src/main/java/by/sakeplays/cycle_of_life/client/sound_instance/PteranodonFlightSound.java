package by.sakeplays.cycle_of_life.client.sound_instance;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class PteranodonFlightSound extends AbstractTickableSoundInstance {

    private final Player player;

    public PteranodonFlightSound(Player player) {
        super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        looping = true;
    }

    @Override
    public void tick() {

        if (!player.getData(DataAttachments.DINO_DATA).isFlying()) {
            this.stop();
            return;
        }

        x = player.getX();
        y = player.getY();
        z = player.getZ();

        double speed = player.getDeltaMovement().length();

        volume = (float) Mth.clamp(speed - 0.33f, 0.075f, 2f);
        pitch = (float) Mth.clamp(speed, 0.8f, 1.33f);
    }
}
