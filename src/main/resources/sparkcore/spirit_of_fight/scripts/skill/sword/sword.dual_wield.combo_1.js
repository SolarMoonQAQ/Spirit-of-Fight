Skill.create("spirit_of_fight:sword.dual_wield.combo_1", builder => {
    builder.accept(skill => {
        const entity = skill.getHolderWrapper().asEntity()
        const animatable = skill.getHolderWrapper().asAnimatable()
        const level = skill.getLevel()
        const attackSystem = PhysicsHelper.createAttackSystem()

        if (entity == null || animatable == null) return

        const config = skill.getConfig()
        config.setCanCriticalHit(false)
        config.setCanSweepAttack(false)
        config.setIgnoreAttackSpeed(true)
        config.setDamageMultiplier(1)

        const anim = animatable.createAnimation('spirit_of_fight:spirit_of_fight/animations/player/fight_skill/fightskill_sword', 'sword.dual_wield.combo_1')
        anim.setShouldTurnBody(true)
        const attackBody = PhysicsHelper.createCollisionBoxBoundToBone(animatable, 'rightItem', SpMath.vec3(1.0, 1.0, 2.0), SpMath.vec3(0.0, 0.0, -0.75))
        attackBody.onAttackCollide('attack', {
            preAttack: (isFirst, attacker, target, o1, o2, manifoldId, attackSystem) => {
                skill.addTarget(target)
                if (isFirst) {
                    entity.cameraShake(2, 1, 2)
                    animatable.changeSpeed(7, 0.05)
                }
                entity.addFightSpirit(50)
            },
            doAttack: (attacker, target, o1, o2, manifoldId, attackSystem) => {
                entity.commonAttack(target)
            },
            postAttack: (attacker, target, o1, o2, manifoldId, attackSystem) => {
                skill.removeTarget(target)
            }
        }, attackSystem)

        attackBody.onCollisionActive(() => {
            entity.setCameraLock(true)
            attackSystem.reset()
            level.playSound(entity.getOnPos().above(), "spirit_of_fight:sharp_wield_1", "players", 1, 1.1)
        })

        skill.onTargetActualHurtPost(event => {
            level.playSound(entity.getOnPos().above(), "spirit_of_fight:sharp_under_attack_1", "players", 1, 1.1)
            SOFParticlePresets.summonQuadraticParticle(event.getSource(), 15, 'minecraft:block', '{"block_state": {"Name": "minecraft:redstone_block"}}')
        })

        anim.onEnd(event => {
            skill.end()
        })

        skill.onActiveStart(() => {
            animatable.playAnimation(anim, 0)
        })

        skill.onActive(() => {
            const animTime = anim.getTime()
            if (animTime >= 0.2 && animTime <= 0.3) {
                entity.move(SpMath.vec3(0.0, entity.getDeltaMovement().y, 0.25), false)
            }

            if ((animTime >= 0.25 && animTime <= 0.35) || (animTime >= 0.45 && animTime <= 0.8)) {
                attackBody.setCollideWithGroups(1)
            } else {
                attackBody.setCollideWithGroups(0)
            }

            if (animTime >= 0.85) {
                entity.getPreInput().execute()
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