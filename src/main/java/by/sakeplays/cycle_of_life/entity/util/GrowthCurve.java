package by.sakeplays.cycle_of_life.entity.util;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class GrowthCurve {

    private List<Pair<Float, Float>> speedCurve = new ArrayList<>();
    private List<Pair<Float, Float>> weightCurve = new ArrayList<>();
    private List<Pair<Float, Float>> scaleCurve = new ArrayList<>();
    private List<Pair<Float, Float>> jumpStrength = new ArrayList<>();
    private List<Pair<Float, Float>> runBaseAnimSpeed = new ArrayList<>();
    private List<Pair<Float, Float>> walkBaseAnimSpeed = new ArrayList<>();
    private List<Pair<Float, Float>> cameraDistance = new ArrayList<>();

    public GrowthCurve() {
    }

    private List<Pair<Float, Float>> getList(GrowthCurveStat stat) {
        return switch (stat) {
            case SCALE -> scaleCurve;
            case SPEED -> speedCurve;
            case WEIGHT -> weightCurve;
            case JUMP_STRENGTH -> jumpStrength;
            case RUN_BASE_ANIM_SPEED -> runBaseAnimSpeed;
            case WALK_BASE_ANIM_SPEED -> walkBaseAnimSpeed;
            case CAMERA_DISTANCE -> cameraDistance;

        };
    }

    public GrowthCurve addKeypoint(GrowthCurveStat stat, float growth, float value) {

        if (growth < 0 || growth > 1) throw new IllegalArgumentException("Growth point cannot be less than 0 or greater than 1");

        List<Pair<Float, Float>> list = getList(stat);

        if (growth <= (list.isEmpty() ? -1 : list.getLast().first())) throw new IllegalArgumentException("Growth point must be greater than the last one. Found " + growth + ", but must be greater than " + list.getLast().first());

        list.add(Pair.of(growth, value));

        return this;
    }

    public float getFirstPoint(GrowthCurveStat stat) {
        List<Pair<Float, Float>> list = getList(stat);

        return list.getFirst().second();
    }

    public float getLastPoint(GrowthCurveStat stat) {
        List<Pair<Float, Float>> list = getList(stat);

        return list.getLast().second();
    }

    public float calculate(float growth, GrowthCurveStat stat) {

        List<Pair<Float, Float>> list = getList(stat);

        if (list.isEmpty()) throw new IllegalStateException("A growth curve list is empty: " + list);
        if (growth <= list.getFirst().first()) return list.getFirst().second();
        if (growth >= list.getLast().first()) return list.getLast().second();


        for (int i = 0; i < list.size() - 1; i++) {
            Pair<Float, Float> current = list.get(i);
            Pair<Float, Float> next = list.get(i + 1);

            if (growth >= current.first() && growth < next.first()) {
                float sectionProgress = (growth - current.first())/(next.first() - current.first());

                return Mth.lerp(sectionProgress, current.second(), next.second());
            }
        }

        return list.getLast().second();
    }
    
    public static final GrowthCurve DEINONYCHUS_CURVE = new GrowthCurve()
            .addKeypoint(GrowthCurveStat.SCALE, 0f, 0.07f)
            .addKeypoint(GrowthCurveStat.SCALE, 0.25f, 0.25f)
            .addKeypoint(GrowthCurveStat.SCALE, 0.66f, 0.65f)
            .addKeypoint(GrowthCurveStat.SCALE, 1f, 0.8f)

            .addKeypoint(GrowthCurveStat.WEIGHT, 0f, 0.5f)
            .addKeypoint(GrowthCurveStat.WEIGHT, 0.25f, 15f)
            .addKeypoint(GrowthCurveStat.WEIGHT, 0.66f, 30f)
            .addKeypoint(GrowthCurveStat.WEIGHT, 1f, 75f)

            .addKeypoint(GrowthCurveStat.SPEED, 0f, 0.05f)
            .addKeypoint(GrowthCurveStat.SPEED, 0.25f, 0.75f)
            .addKeypoint(GrowthCurveStat.SPEED, 0.66f, 1.25f)
            .addKeypoint(GrowthCurveStat.SPEED, 1f, 1f)

            .addKeypoint(GrowthCurveStat.JUMP_STRENGTH, 0f, 0.05f)
            .addKeypoint(GrowthCurveStat.JUMP_STRENGTH, 0.25f, 0.45f)
            .addKeypoint(GrowthCurveStat.JUMP_STRENGTH, 0.66f, 1.05f)
            .addKeypoint(GrowthCurveStat.JUMP_STRENGTH, 1f, 1f)

            .addKeypoint(GrowthCurveStat.RUN_BASE_ANIM_SPEED, 0f, 0.9f)
            .addKeypoint(GrowthCurveStat.RUN_BASE_ANIM_SPEED, 1f, 1f)

            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0f, 1.3f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.1f, 2.2f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.2f, 2f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.3f, 1.9f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.4f, 1.8f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.5f, 1.72f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.6f, 1.75f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.7f, 1.6f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.8f, 1.5f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 1f, 1.4f)
            ;

    public static final GrowthCurve PACHY_CURVE = new GrowthCurve()
            .addKeypoint(GrowthCurveStat.SCALE, 0f, 0.07f)
            .addKeypoint(GrowthCurveStat.SCALE, 0.25f, 0.5f)
            .addKeypoint(GrowthCurveStat.SCALE, 0.4f, 0.8f)
            .addKeypoint(GrowthCurveStat.SCALE, 0.8f, 0.98f)
            .addKeypoint(GrowthCurveStat.SCALE, 1f, 1.05f)

            .addKeypoint(GrowthCurveStat.WEIGHT, 0f, 0.6f)
            .addKeypoint(GrowthCurveStat.WEIGHT, 0.4f, 75f)
            .addKeypoint(GrowthCurveStat.WEIGHT, 0.66f, 270f)
            .addKeypoint(GrowthCurveStat.WEIGHT, 1f, 425f)

            .addKeypoint(GrowthCurveStat.SPEED, 0f, 0.05f)
            .addKeypoint(GrowthCurveStat.SPEED, 0.25f, 0.75f)
            .addKeypoint(GrowthCurveStat.SPEED, 0.66f, 1.25f)
            .addKeypoint(GrowthCurveStat.SPEED, 1f, 1f)

            .addKeypoint(GrowthCurveStat.JUMP_STRENGTH, 0f, 0.05f)
            .addKeypoint(GrowthCurveStat.JUMP_STRENGTH, 0.25f, 0.45f)
            .addKeypoint(GrowthCurveStat.JUMP_STRENGTH, 0.66f, 0.8f)
            .addKeypoint(GrowthCurveStat.JUMP_STRENGTH, 1f, 1f)

            .addKeypoint(GrowthCurveStat.RUN_BASE_ANIM_SPEED, 0f, 0.9f)
            .addKeypoint(GrowthCurveStat.RUN_BASE_ANIM_SPEED, 1f, 1f)

            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0f, 1.3f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.1f, 2.2f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.2f, 2f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.3f, 1.9f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.4f, 1.8f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.5f, 1.72f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.6f, 1.75f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.7f, 1.6f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 0.8f, 1.5f)
            .addKeypoint(GrowthCurveStat.WALK_BASE_ANIM_SPEED, 1f, 1.4f)
            ;


}
