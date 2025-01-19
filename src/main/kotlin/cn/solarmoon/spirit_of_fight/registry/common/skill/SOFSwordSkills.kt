package cn.solarmoon.spirit_of_fight.registry.common.skill

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.getForwardMoveVector
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.FreezeUntilHitAttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.ParryAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.SpecialAttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.hit.HitType
import cn.solarmoon.spirit_of_fight.registry.common.SOFHitTypes
import net.minecraft.world.phys.Vec3

object SOFSwordSkills {

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
    val COMBO_0 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_combo_0")
        .bound { a, b -> AttackAnimSkill(a, b,
            "sword:attack_0",
            1.0,
            1.6,
            0.5,
            SOFHitTypes.LIGHT_CHOP.get(),
            { anim -> anim.time in 0.15..0.45 },
            { anim -> if (anim.time in 0.15..0.3) a.animatable.getForwardMoveVector(1/8f) else null }
        ) }
        .build()

    @JvmStatic
    val COMBO_1 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_combo_1")
        .bound { a, b -> AttackAnimSkill(a, b,
            "sword:attack_1",
            1.0,
            1.6,
            0.45,
            SOFHitTypes.LIGHT_SWIPE.get(),
            { anim -> anim.time in 0.20..0.45 },
            { anim -> if (anim.time in 0.20..0.3) a.animatable.getForwardMoveVector(1/10f) else null }
        ) }
        .build()

    @JvmStatic
    val COMBO_2 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_combo_2")
        .bound { a, b -> AttackAnimSkill(a, b,
            "sword:attack_2",
            1.5,
            1.6,
            null,
            SOFHitTypes.HEAVY_STAB.get(),
            { anim -> anim.time in 0.1..0.5 },
            { anim -> if (anim.time in 0.15..0.3) a.animatable.getForwardMoveVector(1/6f) else null }
        ) }
        .build()

    @JvmStatic
    val SPRINTING_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_sprinting_attack")
        .bound { a, b -> AttackAnimSkill(a, b,
            "sword:attack_sprinting",
            1.25,
            1.6,
            0.75,
            SOFHitTypes.HEAVY_SWIPE.get(),
            { anim -> anim.time in 0.25..0.55 },
            { anim -> if (anim.time in 0.0..0.25) a.animatable.getForwardMoveVector(1/5f) else if (anim.time in 0.25..0.55) a.animatable.getForwardMoveVector(1/2.5f) else null }
        ) }
        .build()

    @JvmStatic
    val JUMP_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("sword_jump_attack")
        .bound { a, b -> AttackAnimSkill(a, b,
            "sword:attack_jump",
            1.25,
            1.6,
            0.55,
            SOFHitTypes.HEAVY_CHOP.get(),
            { anim -> anim.time in 0.15..0.45 },
            { null }
        ) }
        .build()

    @JvmStatic
    val SPECIAL_ATTACK_S = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, SpecialAttackAnimSkill>()
        .id("sword_special_attack")
        .bound { a, b -> object : SpecialAttackAnimSkill(a, b,
            "sword:attack_special",
            1.0,
            1.6,
            null,
            SOFHitTypes.KNOCKDOWN_CHOP.get(),
            { anim -> anim.time in 0.1..0.5 || anim.time in 0.95..1.25 },
            { null }
        ) {
            override fun getHitType(): HitType {
                val animTime = holder.animController.getPlayingAnim()?.time
                return if (animTime != null && animTime in 0.95..1.25) super.getHitType() else SOFHitTypes.LIGHT_CHOP.get()
            }

            override fun getDamageMultiply(): Double {
                val animTime = holder.animController.getPlayingAnim()?.time
                return if (animTime != null && animTime in 0.95..1.25) 2.0 else super.getDamageMultiply()
            }
        } }
        .build()

    @JvmStatic
    val GUARD = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, CommonGuardAnimSkill>()
        .id("sword_guard")
        .bound { a, b -> CommonGuardAnimSkill(a, b, "sword:guard", 150.0) }
        .build()

    @JvmStatic
    val PARRY = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, ParryAnimSkill>()
        .id("sword_parry")
        .bound { a, b -> ParryAnimSkill(a, b,
            "sword:parry",
            150.0
        ) { it.time in 0.0..0.25 }
        }
        .build()

    @JvmStatic
    val DODGE = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, DodgeAnimSkill>()
        .id("sword_dodge")
        .bound { a, b -> DodgeAnimSkill(a, b,
            "sword:dodge",
            0.35
        ) { anim, v -> val mul = 0.65 * (1 - anim.getProgress())
            Vec3(v.x * mul, a.animatable.deltaMovement.y, v.z * mul) }
        }
        .build()

}