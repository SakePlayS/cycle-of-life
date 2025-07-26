package by.sakeplays.cycle_of_life.network;

import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.*;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncTurnHistory;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncYHistory;
import by.sakeplays.cycle_of_life.network.to_server.*;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.*;
import by.sakeplays.cycle_of_life.network.to_server.attacks.pachycephalosaurus.RequestPachyBash;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModPackets {

    private static final String PROTOCOL_VERSION = "1";


    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(SyncSelectedDinosaur.TYPE, SyncSelectedDinosaur.STREAM_CODEC, SyncSelectedDinosaur::handleClient);
        registrar.playToClient(SyncTurnDegree2C.TYPE, SyncTurnDegree2C.STREAM_CODEC, SyncTurnDegree2C::handleClient);
        registrar.playToClient(SyncGrowth.TYPE, SyncGrowth.STREAM_CODEC, SyncGrowth::handleClient);
        registrar.playToClient(SyncWeight.TYPE, SyncWeight.STREAM_CODEC, SyncWeight::handleClient);
        registrar.playToClient(SyncInitialized.TYPE, SyncInitialized.STREAM_CODEC, SyncInitialized::handleClient);
        registrar.playToClient(SyncBloodLevel.TYPE, SyncBloodLevel.STREAM_CODEC, SyncBloodLevel::handleClient);
        registrar.playToClient(SyncFoodLevel.TYPE, SyncFoodLevel.STREAM_CODEC, SyncFoodLevel::handleClient);
        registrar.playToClient(SyncWaterLevel.TYPE, SyncWaterLevel.STREAM_CODEC, SyncWaterLevel::handleClient);
        registrar.playToClient(SyncIsMale.TYPE, SyncIsMale.STREAM_CODEC, SyncIsMale::handleClient);
        registrar.playToClient(SyncAdaptation.TYPE, SyncAdaptation.STREAM_CODEC, SyncAdaptation::handleClient);
        registrar.playToClient(SyncAttackCooldown.TYPE, SyncAttackCooldown.STREAM_CODEC, SyncAttackCooldown::handleClient);
        registrar.playToClient(SyncTurningLock.TYPE, SyncTurningLock.STREAM_CODEC, SyncTurningLock::handleClient);
        registrar.playToClient(ApplyKnockback.TYPE, ApplyKnockback.STREAM_CODEC, ApplyKnockback::handleClient);


        registrar.playToServer(RequestSelectDinosaur.TYPE, RequestSelectDinosaur.STREAM_CODEC, RequestSelectDinosaur::handleServer);
        registrar.playToServer(RequestMovement.TYPE, RequestMovement.STREAM_CODEC, RequestMovement::handleServer);
        registrar.playToServer(SyncTurnDegree2S.TYPE, SyncTurnDegree2S.STREAM_CODEC, SyncTurnDegree2S::handleServer);
        registrar.playToServer(RequestPlayHurtSound.TYPE, RequestPlayHurtSound.STREAM_CODEC, RequestPlayHurtSound::handleServer);
        registrar.playToServer(RequestDeinonychusBite.TYPE, RequestDeinonychusBite.STREAM_CODEC, RequestDeinonychusBite::handleServer);
        registrar.playToServer(RequestDeinonychusDoubleSlash.TYPE, RequestDeinonychusDoubleSlash.STREAM_CODEC, RequestDeinonychusDoubleSlash::handleServer);
        registrar.playToServer(RequestDeinonychusSlash.TYPE, RequestDeinonychusSlash.STREAM_CODEC, RequestDeinonychusSlash::handleServer);
        registrar.playToServer(RequestGrabFood.TYPE, RequestGrabFood.STREAM_CODEC, RequestGrabFood::handleServer);
        registrar.playToServer(RequestPachyBash.TYPE, RequestPachyBash.STREAM_CODEC, RequestPachyBash::handleServer);


        registrar.playBidirectional(SyncDinoSprint.TYPE, SyncDinoSprint.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncDinoSprint::handleClient, SyncDinoSprint::handleServer));
        registrar.playBidirectional(SyncDinoWalking.TYPE, SyncDinoWalking.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncDinoWalking::handleClient, SyncDinoWalking::handleServer));
        registrar.playBidirectional(SyncAcceleration.TYPE, SyncAcceleration.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncAcceleration::handleClient, SyncAcceleration::handleServer));
        registrar.playBidirectional(SyncPlayerRotation.TYPE, SyncPlayerRotation.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncPlayerRotation::handleClient, SyncPlayerRotation::handleServer));
        registrar.playBidirectional(SyncYHistory.TYPE, SyncYHistory.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncYHistory::handleClient, SyncYHistory::handleServer));
        registrar.playBidirectional(SyncTurnHistory.TYPE, SyncTurnHistory.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncTurnHistory::handleClient, SyncTurnHistory::handleServer));
        registrar.playBidirectional(SyncAttackMainTwo.TYPE, SyncAttackMainTwo.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncAttackMainTwo::handleClient, SyncAttackMainTwo::handleServer));
        registrar.playBidirectional(SyncAttackMainOne.TYPE, SyncAttackMainOne.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncAttackMainOne::handleClient, SyncAttackMainOne::handleServer));
        registrar.playBidirectional(SyncHealth.TYPE, SyncHealth.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncHealth::handleClient, SyncHealth::handleServer));
        registrar.playBidirectional(SyncBleed.TYPE, SyncBleed.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncBleed::handleClient, SyncBleed::handleServer));
        registrar.playBidirectional(SyncAttackTurnaround.TYPE, SyncAttackTurnaround.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncAttackTurnaround::handleClient, SyncAttackTurnaround::handleServer));
        registrar.playBidirectional(SyncFullReset.TYPE, SyncFullReset.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncFullReset::handleClient, SyncFullReset::handleServer));
        registrar.playBidirectional(SyncStamina.TYPE, SyncStamina.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncStamina::handleClient, SyncStamina::handleServer));
        registrar.playBidirectional(SyncRestingState.TYPE, SyncRestingState.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncRestingState::handleClient, SyncRestingState::handleServer));
        registrar.playBidirectional(SyncTurnProgress.TYPE, SyncTurnProgress.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncTurnProgress::handleClient, SyncTurnProgress::handleServer));
        registrar.playBidirectional(SyncIsSliding.TYPE, SyncIsSliding.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncIsSliding::handleClient, SyncIsSliding::handleServer));
        registrar.playBidirectional(SyncAttemptingPairing.TYPE, SyncAttemptingPairing.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncAttemptingPairing::handleClient, SyncAttemptingPairing::handleServer));
        registrar.playBidirectional(SyncPairingWith.TYPE, SyncPairingWith.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncPairingWith::handleClient, SyncPairingWith::handleServer));
        registrar.playBidirectional(SyncIsPaired.TYPE, SyncIsPaired.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncIsPaired::handleClient, SyncIsPaired::handleServer));
        registrar.playBidirectional(SyncPairingState.TYPE, SyncPairingState.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncPairingState::handleClient, SyncPairingState::handleServer));
        registrar.playBidirectional(SyncSkinData.TYPE, SyncSkinData.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncSkinData::handleClient, SyncSkinData::handleServer));
        registrar.playBidirectional(RequestDrinking.TYPE, RequestDrinking.STREAM_CODEC, new DirectionalPayloadHandler<>(RequestDrinking::handleClient, RequestDrinking::handleServer));
        registrar.playBidirectional(SyncTurningState.TYPE, SyncTurningState.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncTurningState::handleClient, SyncTurningState::handleServer));
        registrar.playBidirectional(SyncAdditionalTurn.TYPE, SyncAdditionalTurn.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncAdditionalTurn::handleClient, SyncAdditionalTurn::handleServer));
        registrar.playBidirectional(SyncSpeed.TYPE, SyncSpeed.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncSpeed::handleClient, SyncSpeed::handleServer));
        registrar.playBidirectional(SyncHitboxes.TYPE, SyncHitboxes.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncHitboxes::handleClient, SyncHitboxes::handleServer));
        registrar.playBidirectional(SyncKnockdownTime.TYPE, SyncKnockdownTime.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncKnockdownTime::handleClient, SyncKnockdownTime::handleServer));
        registrar.playBidirectional(SyncCharging.TYPE, SyncCharging.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncCharging::handleClient, SyncCharging::handleServer));


    }

}
