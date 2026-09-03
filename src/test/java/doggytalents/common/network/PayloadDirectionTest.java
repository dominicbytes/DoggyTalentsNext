package doggytalents.common.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import doggytalents.common.entity.texture.DogAllowedSkinManager;
import doggytalents.common.network.packet.data.*;
import doggytalents.common.network.packet.data.ParticleData.CritEmitterData;
import net.minecraft.network.protocol.PacketFlow;

class PayloadDirectionTest {

    @BeforeAll
    static void initializePackets() {
        PacketHandler.init();
    }

    @Test
    void net01RegistersServerboundPayloadsOnServerboundFlowOnly() {
        assertDirection(PayloadDirection.SERVERBOUND,
            TrainWolfToDogData.class, DogModeData.class, DogNameData.class,
            DogObeyData.class, DogTalentData.class, FriendlyFireData.class,
            OpenDogScreenData.class, DogInventoryPageData.class, DogTextureData.class,
            HeelByNameData.class, WhistleRequestModeData.class,
            ConductingBoneData.RequestDogsData.class,
            ConductingBoneData.RequestDistantTeleportDogData.class,
            ChangeAccessoriesData.class, StatsSyncData.Request.class,
            DogRegardTeamPlayersData.class, DogForceSitData.class,
            CanineTrackerData.RequestDogsData.class,
            CanineTrackerData.StartLocatingData.class,
            CanineTrackerData.RequestPosUpdateData.class,
            DogDeTrainData.class, DogUntameData.class, DogMigrateOwnerData.class,
            WhisltleEditHotKeyData.class, WhistleUseData.class,
            DogGroupsData.EDIT.class, DogGroupsData.FETCH_REQUEST.class,
            HeelByGroupData.REQUEST_GROUP_LIST.class, HeelByGroupData.REQUEST_HEEL.class,
            DogLowHealthStrategyData.class, CrossOriginTpData.class,
            ChangeArtifactData.class, DogIncapMsgData.Request.class,
            PatrolTargetLockData.class, HideArmorData.class,
            CombatReturnStrategyData.class, DogAutoMountData.class,
            ForceChangeOwnerData.class, AllStandSwitchModeData.class,
            DogPettingData.class, DogTalentOptionSetData.class,
            ForceClearKillStatsData.class, DogAnimDebugData.UpdateItemSettingsData.class,
            DogOnDutyData.class, CarryMeData.class);
    }

    @Test
    void net01RegistersClientboundPayloadsOnClientboundFlowOnly() {
        assertDirection(PayloadDirection.CLIENTBOUND,
            CritEmitterData.class, DogEatingParticleData.class, DogShakingData.class,
            ConductingBoneData.ResponseDogsData.class, StatsSyncData.Response.class,
            DogMountData.class, CanineTrackerData.ResponseDogsData.class,
            CanineTrackerData.ResponsePosUpdateData.class, DogGroupsData.UPDATE.class,
            HeelByGroupData.RESPONSE_GROUP_LIST.class, DogIncapMsgData.Response.class,
            DogSyncData.class, DogExplosionData.class, DogInterruptibleSoundData.class,
            DogAllowedSkinManager.AllowedSkinEntryCompressed.class);
    }

    @Test
    void net02PreservesProtocolDiscriminatorOrder() {
        var protocolOrder = new Class<?>[] {
            TrainWolfToDogData.class, DogModeData.class, DogNameData.class,
            DogObeyData.class, DogTalentData.class, FriendlyFireData.class,
            OpenDogScreenData.class, DogInventoryPageData.class, DogTextureData.class,
            HeelByNameData.class, WhistleRequestModeData.class, CritEmitterData.class,
            DogEatingParticleData.class, DogShakingData.class,
            ConductingBoneData.RequestDogsData.class, ConductingBoneData.ResponseDogsData.class,
            ConductingBoneData.RequestDistantTeleportDogData.class, ChangeAccessoriesData.class,
            StatsSyncData.Request.class, StatsSyncData.Response.class, DogMountData.class,
            DogRegardTeamPlayersData.class, DogForceSitData.class,
            CanineTrackerData.RequestDogsData.class, CanineTrackerData.ResponseDogsData.class,
            CanineTrackerData.StartLocatingData.class, CanineTrackerData.RequestPosUpdateData.class,
            CanineTrackerData.ResponsePosUpdateData.class, DogDeTrainData.class,
            DogUntameData.class, DogMigrateOwnerData.class, WhisltleEditHotKeyData.class,
            WhistleUseData.class, DogGroupsData.EDIT.class, DogGroupsData.FETCH_REQUEST.class,
            DogGroupsData.UPDATE.class, HeelByGroupData.REQUEST_GROUP_LIST.class,
            HeelByGroupData.RESPONSE_GROUP_LIST.class, HeelByGroupData.REQUEST_HEEL.class,
            DogLowHealthStrategyData.class, CrossOriginTpData.class, ChangeArtifactData.class,
            DogIncapMsgData.Request.class, DogIncapMsgData.Response.class,
            PatrolTargetLockData.class, HideArmorData.class, CombatReturnStrategyData.class,
            DogAutoMountData.class, ForceChangeOwnerData.class, DogSyncData.class,
            DogExplosionData.class, AllStandSwitchModeData.class, DogPettingData.class,
            DogTalentOptionSetData.class, ForceClearKillStatsData.class,
            DogAnimDebugData.UpdateItemSettingsData.class, DogInterruptibleSoundData.class,
            DogOnDutyData.class, CarryMeData.class,
            DogAllowedSkinManager.AllowedSkinEntryCompressed.class
        };

        for (int id = 0; id < protocolOrder.length; ++id) {
            assertEquals(id, DTNNetworkHandler.idFor(protocolOrder[id]), protocolOrder[id].getName());
        }
    }

    private static void assertDirection(PayloadDirection expected, Class<?>... dataClasses) {
        var acceptedFlow = expected == PayloadDirection.SERVERBOUND
            ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND;
        var rejectedFlow = acceptedFlow == PacketFlow.SERVERBOUND
            ? PacketFlow.CLIENTBOUND : PacketFlow.SERVERBOUND;

        for (var dataClass : dataClasses) {
            var actual = DTNNetworkHandler.directionFor(dataClass);
            assertEquals(expected, actual, dataClass.getName());
            assertTrue(actual.accepts(acceptedFlow), dataClass.getName());
            assertFalse(actual.accepts(rejectedFlow), dataClass.getName());
        }
    }
}
