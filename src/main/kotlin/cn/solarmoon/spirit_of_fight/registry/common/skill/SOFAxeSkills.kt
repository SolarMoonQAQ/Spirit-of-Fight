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

object SOFAxeSkills {

    @JvmStatic
    fun register() {}

    @JvmStatic
    val COMBO_0 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("axe_combo_0")
        .bound { a, b -> AttackAnimSkill(a, b,
            "axe:attack_0",
            1.0,
            1.0,
            0.5,
            SOFHitTypes.LIGHT_CHOP.get(),
            { anim -> anim.time in 0.15..0.45 },
            { anim -> if (anim.time in 0.15..0.3) a.animatable.getForwardMoveVector(1/8f) else null }
        ) }
        .build()

    @JvmStatic
    val COMBO_1 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("axe_combo_1")
        .bound { a, b -> AttackAnimSkill(a, b,
            "axe:attack_1",
            1.0,
            1.0,
            0.45,
            SOFHitTypes.LIGHT_SWIPE.get(),
            { anim -> anim.time in 0.20..0.45 },
            { anim -> if (anim.time in 0.20..0.3) a.animatable.getForwardMoveVector(1/10f) else null }
        ) }
        .build()

    @JvmStatic
    val COMBO_2 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("axe_combo_2")
        .bound { a, b -> AttackAnimSkill(a, b,
            "axe:attack_2",
            1.5,
            1.0,
            null,
            SOFHitTypes.HEAVY_SWIPE.get(),
            { anim -> anim.time in 0.4..0.8 },
            { anim -> null }
        ) }
        .build()

    @JvmStatic
    val SPRINTING_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("axe_sprinting_attack")
        .bound { a, b -> AttackAnimSkill(a, b,
            "axe:attack_sprinting",
            1.25,
            1.0,
            0.75,
            SOFHitTypes.HEAVY_SWIPE.get(),
            { anim -> anim.time in 0.55..0.8 },
            { anim -> if (anim.time in 0.0..0.55) a.animatable.getForwardMoveVector(1/5f) else if (anim.time in 0.6..0.85) a.animatable.getForwardMoveVector(1/2f) else null }
        ) }
        .build()

    @JvmStatic
    val JUMP_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("axe_jump_attack")
        .bound { a, b -> AttackAnimSkill(a, b,
            "axe:attack_jump",
            1.25,
            1.0,
            0.55,
            SOFHitTypes.HEAVY_CHOP.get(),
            { anim -> anim.time in 0.15..0.45 },
            { null }
        ) }
        .build()

    @JvmStatic
    val SPECIAL_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, SpecialAttackAnimSkill>()
        .id("axe_special_attack")
        .bound { a, b -> object : SpecialAttackAnimSkill(a, b,
            "axe:attack_special",
            2.0,
            1.0,
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
        .id("axe_guard")
        .bound { a, b -> CommonGuardAnimSkill(a, b, "axe:guard", 150.0) }
        .build()

    @JvmStatic
    val PARRY = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, ParryAnimSkill>()
        .id("axe_parry")
        .bound { a, b -> ParryAnimSkill(a, b,
            "axe:parry",
            150.0
        ) { it.time in 0.0..0.25 }
        }
        .build()

    @JvmStatic
    val DODGE = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, DodgeAnimSkill>()
        .id("axe_dodge")
        .bound { a, b -> DodgeAnimSkill(a, b,
            "axe:dodge",
            0.35
        ) { anim, v -> val mul = 0.65 * (1 - anim.getProgress())
            Vec3(v.x * mul, a.animatable.deltaMovement.y, v.z * mul) }
        }
        .build()



}