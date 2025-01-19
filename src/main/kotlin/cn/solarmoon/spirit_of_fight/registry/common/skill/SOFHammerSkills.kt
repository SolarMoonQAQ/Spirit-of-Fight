package cn.solarmoon.spirit_of_fight.registry.common.skill

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.getForwardMoveVector
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.FreezeUntilHitAttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.SpecialAttackAnimSkill
import cn.solarmoon.spirit_of_fight.registry.common.SOFHitTypes
import net.minecraft.world.phys.Vec3

object SOFHammerSkills {

    @JvmStatic
    fun register() {}

    @JvmStatic
    val COMBO_0 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("hammer_combo_0")
        .bound { a, b -> AttackAnimSkill(a, b,
            "hammer:attack_0",
            1.0,
            1.0,
            0.65,
            SOFHitTypes.LIGHT_CHOP.get(),
            { anim -> anim.time in 0.4..0.6 },
            { anim -> if (anim.time in 0.4..0.6) a.animatable.getForwardMoveVector(1/8f) else null }
        ) }
        .build()

    @JvmStatic
    val COMBO_1 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("hammer_combo_1")
        .bound { a, b -> AttackAnimSkill(a, b,
            "hammer:attack_1",
            1.0,
            1.0,
            0.8,
            SOFHitTypes.HEAVY_SWIPE.get(),
            { anim -> anim.time in 0.35..0.75 },
            { anim -> if (anim.time in 0.35..0.6) a.animatable.getForwardMoveVector(1/5f) else null }
        ) }
        .build()

    @JvmStatic
    val COMBO_2 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("hammer_combo_2")
        .bound { a, b -> AttackAnimSkill(a, b,
            "hammer:attack_2",
            1.5,
            1.0,
            null,
            SOFHitTypes.KNOCKDOWN_CHOP.get(),
            { anim -> anim.time in 0.25..0.75 },
            { anim -> if (anim.time in 0.0..0.65) a.animatable.getForwardMoveVector(1/7f) else null }
        ) }
        .build()

    @JvmStatic
    val COMBO_C1 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("hammer_combo_c1")
        .bound { a, b -> FreezeUntilHitAttackAnimSkill(a, b,
            "hammer:attack_c1",
            0.5,
            1.0,
            SOFHitTypes.HEAVY_STAB.get(),
            { anim -> anim.time in 0.15..0.45 },
            { anim -> if (anim.time in 0.15..0.35) a.animatable.getForwardMoveVector(1/8f) else null },
        ) }
        .build()

    @JvmStatic
    val COMBO_C2 = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("hammer_combo_c2")
        .bound { a, b -> AttackAnimSkill(a, b,
            "hammer:attack_c2",
            1.0,
            1.0,
            0.65,
            SOFHitTypes.LIGHT_CHOP.get(),
            { anim -> anim.time in 0.4..0.6 },
            { anim -> if (anim.time in 0.4..0.6) a.animatable.getForwardMoveVector(1/8f) else null }
        ) }
        .build()

    @JvmStatic
    val GUARD = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, CommonGuardAnimSkill>()
        .id("hammer_guard")
        .bound { a, b -> CommonGuardAnimSkill(a, b, "hammer:guard", 150.0)}
        .build()

    @JvmStatic
    val SPRINTING_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("hammer_sprinting_attack")
        .bound { a, b -> AttackAnimSkill(a, b,
            "hammer:attack_sprinting",
            1.25,
            1.0,
            1.25,
            SOFHitTypes.KNOCKDOWN_SWIPE.get(),
            { anim -> anim.time in 0.55..1.25 },
            { anim -> if (anim.time in 0.0..0.5) a.animatable.getForwardMoveVector(1/5f) else if (anim.time in 0.7..1.1) a.animatable.getForwardMoveVector(1/2.5f) else null }
        ) }
        .build()

    @JvmStatic
    val JUMP_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, AttackAnimSkill>()
        .id("hammer_jump_attack")
        .bound { a, b -> AttackAnimSkill(a, b,
            "hammer:attack_jump",
            1.25,
            1.0,
            0.55,
            SOFHitTypes.KNOCKDOWN_CHOP.get(),
            { anim -> anim.time in 0.15..0.3 },
            { null }
        ) }
        .build()

    @JvmStatic
    val DODGE = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, DodgeAnimSkill>()
        .id("hammer_dodge")
        .bound { a, b -> DodgeAnimSkill(a, b,
            "hammer:dodge",
            0.35
        ) { anim, v -> val mul = 0.65 * (1 - anim.getProgress())
            Vec3(v.x * mul, a.animatable.deltaMovement.y, v.z * mul) }
        }
        .build()

    @JvmStatic
    val SPECIAL_ATTACK = SpiritOfFight.REGISTER.skillType<IEntityAnimatable<*>, SpecialAttackAnimSkill>()
        .id("hammer_special_attack")
        .bound { a, b -> SpecialAttackAnimSkill(a, b,
            "hammer:attack_special",
            2.0,
            1.0,
            null,
            SOFHitTypes.KNOCKDOWN_UPSTROKE.get(),
            { anim -> anim.time in 0.7..0.95 },
            { null }
        ) }
        .build()

}