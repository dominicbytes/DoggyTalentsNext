package doggytalents.client;

public class PettingArmPoseParams {

    public static Object declareParameter(int param_index, Class<?> param_type) {
        switch (param_index) {
        case 0: return Boolean.FALSE; // twoHanded
        case 1: return Boolean.FALSE; // affectsOffhandPose
        default: throw new IllegalStateException("Undefined param_index: " + param_index);
        }
    }

}
