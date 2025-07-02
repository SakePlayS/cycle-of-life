package by.sakeplays.cycle_of_life.network;

import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.SyncSelectedDinosaur;
import by.sakeplays.cycle_of_life.network.to_client.SyncTurnDegree2C;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncTurnHistory;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncYHistory;
import by.sakeplays.cycle_of_life.network.to_server.RequestMovement;
import by.sakeplays.cycle_of_life.network.to_server.RequestSelectDinosaur;
import by.sakeplays.cycle_of_life.network.to_server.SyncTurnDegree2S;
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


        registrar.playToServer(RequestSelectDinosaur.TYPE, RequestSelectDinosaur.STREAM_CODEC, RequestSelectDinosaur::handleServer);
        registrar.playToServer(RequestMovement.TYPE, RequestMovement.STREAM_CODEC, RequestMovement::handleServer);
        registrar.playToServer(SyncTurnDegree2S.TYPE, SyncTurnDegree2S.STREAM_CODEC, SyncTurnDegree2S::handleServer);

        registrar.playBidirectional(SyncDinoSprint.TYPE, SyncDinoSprint.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncDinoSprint::handleClient, SyncDinoSprint::handleServer));
        registrar.playBidirectional(SyncDinoWalking.TYPE, SyncDinoWalking.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncDinoWalking::handleClient, SyncDinoWalking::handleServer));
        registrar.playBidirectional(SyncAcceleration.TYPE, SyncAcceleration.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncAcceleration::handleClient, SyncAcceleration::handleServer));
        registrar.playBidirectional(SyncTurnDegree.TYPE, SyncTurnDegree.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncTurnDegree::handleClient, SyncTurnDegree::handleServer));
        registrar.playBidirectional(SyncYHistory.TYPE, SyncYHistory.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncYHistory::handleClient, SyncYHistory::handleServer));
        registrar.playBidirectional(SyncTurnHistory.TYPE, SyncTurnHistory.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncTurnHistory::handleClient, SyncTurnHistory::handleServer));


    }

}
