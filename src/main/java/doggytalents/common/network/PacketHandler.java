package doggytalents.common.network;

import doggytalents.common.entity.texture.DogAllowedSkinManager;
import doggytalents.common.network.packet.*;
import doggytalents.common.network.packet.data.*;
import doggytalents.common.network.packet.data.ParticleData.*;
import doggytalents.common.network.packet.ParticlePackets.*;

public final class PacketHandler {

    private static int disc = 0;
    private static boolean initialized;

    public static synchronized void init() {
        if (initialized) return;

        registerServerbound(new TrainWolfToDogPacket(), TrainWolfToDogData.class);
        registerServerbound(new DogModePacket(), DogModeData.class);
        registerServerbound(new DogNamePacket(), DogNameData.class);
        registerServerbound(new DogObeyPacket(), DogObeyData.class);
        registerServerbound(new DogTalentPacket(), DogTalentData.class);
        registerServerbound(new FriendlyFirePacket(), FriendlyFireData.class);
        registerServerbound(new OpenDogScreenPacket(), OpenDogScreenData.class);
        registerServerbound(new DogInventoryPagePacket(), DogInventoryPageData.class);
        registerServerbound(new DogTexturePacket(), DogTextureData.class);
        registerServerbound(new HeelByNamePacket(), HeelByNameData.class);
        registerServerbound(new WhistleRequestModePacket(), WhistleRequestModeData.class);
        registerClientbound(new CritEmitterPacket(), CritEmitterData.class);
        registerClientbound(new DogEatingParticlePacket(), DogEatingParticleData.class);
        registerClientbound(new DogShakingPacket(), DogShakingData.class);
        registerServerbound(new ConductingBonePackets.RequestDogsPacket(), ConductingBoneData.RequestDogsData.class);
        registerClientbound(new ConductingBonePackets.ResponseDogsPackets(), ConductingBoneData.ResponseDogsData.class);
        registerServerbound(new ConductingBonePackets.RequestDistantTeleportDogPacket(), ConductingBoneData.RequestDistantTeleportDogData.class);
        registerServerbound(new ChangeAccessoryPacket(), ChangeAccessoriesData.class);
        registerServerbound(new StatsSyncPackets.Request(), StatsSyncData.Request.class);
        registerClientbound(new StatsSyncPackets.Response(), StatsSyncData.Response.class);
        registerClientbound(new DogMountPacket(), DogMountData.class);
        registerServerbound(new DogRegardTeamPlayersPacket(), DogRegardTeamPlayersData.class);
        registerServerbound(new DogForceSitPacket(), DogForceSitData.class);
        registerServerbound(new CanineTrackerPackets.RequestDogsPacket(), CanineTrackerData.RequestDogsData.class);
        registerClientbound(new CanineTrackerPackets.ResponseDogsPackets(), CanineTrackerData.ResponseDogsData.class);
        registerServerbound(new CanineTrackerPackets.StartLocatingPacket(), CanineTrackerData.StartLocatingData.class);
        registerServerbound(new CanineTrackerPackets.RequestPosUpdatePacket(), CanineTrackerData.RequestPosUpdateData.class);
        registerClientbound(new CanineTrackerPackets.ResponsePosUpdatePacket(), CanineTrackerData.ResponsePosUpdateData.class);
        registerServerbound(new DogDeTrainPacket(), DogDeTrainData.class);
        registerServerbound(new DogUntamePacket(), DogUntameData.class);
        registerServerbound(new DogMigrateOwnerPacket(), DogMigrateOwnerData.class);
        registerServerbound(new WhistleEditHotKeyPacket(), WhisltleEditHotKeyData.class);
        registerServerbound(new WhistleUsePacket(), WhistleUseData.class);
        registerServerbound(new DogGroupPackets.EDIT(), DogGroupsData.EDIT.class);
        registerServerbound(new DogGroupPackets.FETCH_REQUEST(), DogGroupsData.FETCH_REQUEST.class);
        registerClientbound(new DogGroupPackets.UPDATE(), DogGroupsData.UPDATE.class);
        registerServerbound(new HeelByGroupPackets.REQUEST_GROUP_LIST(), HeelByGroupData.REQUEST_GROUP_LIST.class);
        registerClientbound(new HeelByGroupPackets.RESPONSE_GROUP_LIST(), HeelByGroupData.RESPONSE_GROUP_LIST.class);
        registerServerbound(new HeelByGroupPackets.REQUEST_HEEL(), HeelByGroupData.REQUEST_HEEL.class);
        registerServerbound(new DogLowHealthStrategyPacket(), DogLowHealthStrategyData.class);
        registerServerbound(new CrossOriginTpPacket(), CrossOriginTpData.class);
        registerServerbound(new ChangeArtifactPacket(), ChangeArtifactData.class);
        registerServerbound(new DogIncapMsgPackets.Request(), DogIncapMsgData.Request.class);
        registerClientbound(new DogIncapMsgPackets.Response(), DogIncapMsgData.Response.class);
        registerServerbound(new PatrolTargetLockPacket(), PatrolTargetLockData.class);
        registerServerbound(new HideArmorPacket(), HideArmorData.class);
        registerServerbound(new CombatReturnStrategyPacket(), CombatReturnStrategyData.class);
        registerServerbound(new DogAutoMountPacket(), DogAutoMountData.class);
        registerServerbound(new ForceChangeOwnerPacket(), ForceChangeOwnerData.class);
        registerClientbound(new DogSyncDataPacket(), DogSyncData.class);
        registerClientbound(new DogExplosionPacket(), DogExplosionData.class);
        registerServerbound(new AllStandSwitchModePacket(), AllStandSwitchModeData.class);
        registerServerbound(new DogPettingPacket(), DogPettingData.class);
        registerServerbound(new DogTalentOptionSetPacket(), DogTalentOptionSetData.class);
        registerServerbound(new ForceClearKillStatsPacket(), ForceClearKillStatsData.class);
        registerServerbound(new DogAnimDebugPackets.UpdateItemSettings(), DogAnimDebugData.UpdateItemSettingsData.class);
        registerClientbound(new DogInterruptibleSoundPacket(), DogInterruptibleSoundData.class);
        registerServerbound(new DogOnDutyPacket(), DogOnDutyData.class);
        registerServerbound(new CarryMePacket(), CarryMeData.class);
        registerClientbound(new DogAllowedSkinManager.Packet(), DogAllowedSkinManager.AllowedSkinEntryCompressed.class);

        initialized = true;
    }

    public static <MSG> void send(PacketDistributor.PacketTarget<?> target, MSG message) {
        DTNNetworkHandler.send(target, message);
    }

    private static <D> void registerServerbound(IPacket<D> packet, Class<D> dataClass) {
        registerPacket(packet, dataClass, PayloadDirection.SERVERBOUND);
    }

    private static <D> void registerClientbound(IPacket<D> packet, Class<D> dataClass) {
        registerPacket(packet, dataClass, PayloadDirection.CLIENTBOUND);
    }

    private static <D> void registerPacket(IPacket<D> packet, Class<D> dataClass, PayloadDirection direction) {
        DTNNetworkHandler.registerMessage(PacketHandler.disc++, dataClass,
            packet::encode, packet::decode, packet::handle, direction);
    }
}
