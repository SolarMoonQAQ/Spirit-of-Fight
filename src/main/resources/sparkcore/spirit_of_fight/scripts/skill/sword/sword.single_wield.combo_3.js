Skill.create("spirit_of_fight:sword.single_wield.combo_3", builder => {
    builder.accept(skill => {
        const entity = skill.getHolderWrapper().asEntity()
        const animatable = skill.getHolderWrapper().asAnimatable()
        const level = skill.getLevel()

        if (entity == null || animatable == null) return

        const config = skill.getConfig()
        config.setCanCriticalHit(false)
        config.setCanSweepAttack(false)
        config.setIgnoreAttackSpeed(true)
        config.setDamageMultiplier(1.25)

        const anim = animatable.createAnimation('spirit_of_fight:spirit_of_fight/animations/player/fight_skill/fightskill_sword','sword.single_wield.combo_3')
        anim.setShouldTurnBody(true)
        const attackBody = PhysicsHelper.createCollisionBoxBoundToBone(animatable, 'rightItem', SpMath.vec3(1.0, 1.0, 2.0), SpMath.vec3(0.0, 0.0, -0.75))
        // 创建全局 AttackSystem 用于管理攻击状态
        const globalAttackSystem = PhysicsHelper.createAttackSystem()
        
        // 跟踪当前攻击段
        let currentAttackPhase = 0 // 0: 无攻击, 1: 第一段, 2: 第二段

        attackBody.onAttackCollide('attack', {
            preAttack: (isFirst, attacker, target, o1, o2, manifoldId, attackSystem) => {
                skill.addTarget(target)
                if (isFirst) {
                    entity.cameraShake(3, 1.5, 3)
                    animatable.changeSpeed(7, 0.05)
                }
                entity.addFightSpirit(100)
            },
            doAttack: (attacker, target, o1, o2, manifoldId, attackSystem) => {
                entity.commonAttack(target)
            },
            postAttack: (attacker, target, o1, o2, manifoldId, attackSystem) => {
                skill.removeTarget(target)
            }
        }, globalAttackSystem)

        attackBody.onCollisionActive(() => {
            entity.setCameraLock(true)
            globalAttackSystem.reset()
            level.playSound(entity.getOnPos().above(), "spirit_of_fight:sharp_wield_1", "players", 1, 0.8)
        })

        skill.onTargetActualHurtPost(event => {
            level.playSound(entity.getOnPos().above(), "spirit_of_fight:sharp_under_attack_1", "players", 1, 0.9)
            SOFParticlePresets.summonQuadraticParticle(event.getSource(), 15, 'minecraft:block', '{"block_state": {"Name": "minecraft:redstone_block"}}')
        })

        anim.onEnd(event => {
            skill.end()
        })

        skill.onActiveStart(() => {
            animatable.playAnimation(anim, 0)
            // 技能开始时重置 AttackSystem 和攻击段
            currentAttackPhase = 0
            globalAttackSystem.reset()
        })

        skill.onActive(() => {
            const animTime = anim.getTime()
            if (animTime >= 0.1 && animTime <= 0.35) {
                entity.move(SpMath.vec3(0.0, entity.getDeltaMovement().y, 0.5), false)
            }

            // 攻击段管理和 AttackSystem 重置
            if (animTime >= 0.25 && animTime <= 0.5) {
                attackBody.setCollideWithGroups(1)
            } else {
                attackBody.setCollideWithGroups(0)
            }
        })

        skill.onLocalInputUpdate(event => {
            if (event.getInput().down) event.getEntity().setDeltaMovement(0.0, entity.getDeltaMovement().y, 0.0)
            EntityHelper.preventLocalInput(event)
        })

        skill.onEnd(() => {
            entity.setCameraLock(false)
            attackBody.remove()
        })
    })
})