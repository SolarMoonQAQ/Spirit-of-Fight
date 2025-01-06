package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.entity.state.getForwardMoveVector
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.ParryAnimSkill
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.phys.Vec3
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom

object SOFSkills {

    //    /**
//     * 防止当用战技攻击时与生物贴太近影响观感
//     */
//    @Redirect(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBoundingBox()Lnet/minecraft/world/phys/AABB;", ordinal = 0))
//    private AABB push(LivingEntity instance) {
//        var box = instance.getBoundingBox();
//        if (instance instanceof IFightSkillHolder fighter) {
//            if (fighter.getSkillController() != null) {
//                return box.inflate(0.5);
//            }
//        }
//        return box;
//    }
//
//    @Redirect(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBoundingBox()Lnet/minecraft/world/phys/AABB;", ordinal = 1))
//    private AABB pus2h(LivingEntity instance) {
//        var box = instance.getBoundingBox();
//        if (entity instanceof IFightSkillHolder fighter) {
//            if (fighter.getSkillController() != null) {
//                return box.inflate(0.5);
//            }
//        }
//        return box;
//    }

    @JvmStatic
    fun register() {}

    @JvmStatic
    val SWORD_COMBO_0 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_combo_0")
        .bound { a, b -> AttackAnimSkill(a, b,
            "sword:attack_0",
            1.6,
            0.5,
            { anim -> anim.isTickIn(0.15, 0.45) },
            { anim -> if (anim.isTickIn(0.15, 0.3)) a.animatable.getForwardMoveVector(1/8f) else null }
        ) }
        .build()

    @JvmStatic
    val SWORD_COMBO_1 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_combo_1")
        .bound { a, b -> AttackAnimSkill(a, b,
            "sword:attack_1",
            1.6,
            0.45,
            { anim -> anim.isTickIn(0.15, 0.45) },
            { anim -> if (anim.isTickIn(0.20, 0.3)) a.animatable.getForwardMoveVector(1/10f) else null }
        ) }
        .build()

    @JvmStatic
    val SWORD_COMBO_2 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_combo_2")
        .bound { a, b -> object : AttackAnimSkill(a, b,
            "sword:attack_2",
            1.6,
            null,
            { anim -> anim.isTickIn(0.1, 0.5) },
            { anim -> if (anim.isTickIn(0.15, 0.3)) a.animatable.getForwardMoveVector(1/6f) else null }
        ) {
            override fun whenFirstAttacked(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
                super.whenFirstAttacked(o1, o2, buffer, attackSystem)
                SparkVisualEffects.CAMERA_SHAKE.shake(2, 0.5f)
            }
        } }
        .build()

    @JvmStatic
    val SWORD_SPRINTING_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_sprinting_attack")
        .bound { a, b -> object : AttackAnimSkill(a, b,
            "sword:attack_sprinting",
            1.6,
            0.75,
            { anim -> anim.isTickIn(0.25, 0.55) },
            { anim -> if (anim.isTickIn(0.0, 0.25)) a.animatable.getForwardMoveVector(1/5f) else if (anim.isTickIn(0.25, 0.55)) a.animatable.getForwardMoveVector(1/2.5f) else null }
        ) {
            override fun whenFirstAttacked(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
                super.whenFirstAttacked(o1, o2, buffer, attackSystem)
                SparkVisualEffects.CAMERA_SHAKE.shake(2, 0.5f)
            }
        } }
        .build()

    @JvmStatic
    val SWORD_JUMP_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_jump_attack")
        .bound { a, b -> object : AttackAnimSkill(a, b,
            "sword:attack_jump",
            1.6,
            0.55,
            { anim -> anim.isTickIn(0.15, 0.45) },
            { null }
        ) {
            override fun whenFirstAttacked(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
                super.whenFirstAttacked(o1, o2, buffer, attackSystem)
                SparkVisualEffects.CAMERA_SHAKE.shake(2, 0.5f)
            }
        } }
        .build()

    @JvmStatic
    val SWORD_GUARD = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, CommonGuardAnimSkill>()
        .id("sword_guard")
        .bound { a, b -> CommonGuardAnimSkill(a, b, "sword:guard", 150.0) }
        .build()

    @JvmStatic
    val SWORD_PARRY = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, ParryAnimSkill>()
        .id("sword_parry")
        .bound { a, b -> ParryAnimSkill(a, b,
            "sword:parry",
            150.0
        ) { it.isTickIn(0.0, 0.25) }
        }
        .build()

    @JvmStatic
    val COMMON_DODGE = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, DodgeAnimSkill>()
        .id("common_dodge")
        .bound { a, b -> DodgeAnimSkill(a, b,
            "common:dodge",
            0.35
        ) { anim, v -> if (anim.isTickIn(0.0, 0.4)) {
            val mul = 0.65 * (1 - anim.getProgress())
            Vec3(v.x * mul, a.animatable.deltaMovement.y, v.z * mul)
        } else null }
        }
        .build()

}