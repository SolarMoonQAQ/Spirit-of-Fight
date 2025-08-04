Skill.createBy("spirit_of_fight:sword.dodge", "spirit_of_fight:dodge", builder => {
    builder.acceptConfig(config => {
        config.put('enable_perfect_dodge', true)
        config.put('max_perfect_dodge_times', 1)
    })
    builder.accept(skill => {
        const entity = skill.getHolderWrapper().asEntity()
        const animatable = skill.getHolderWrapper().asAnimatable()
        const level = skill.getLevel()

        if (entity == null || animatable == null) return

        const anim = SOFHelper.createAnimByDirection(animatable, "sword.dodge", "forward")

        anim.onEnd(event => {
            skill.end()
        })

        skill.onActiveStart(() => {
            animatable.playAnimation(anim, 0)
        })

        skill.onActive(() => {
            const animTime = anim.getTime()

            if (animTime >= 0.0 && animTime <= 0.1) {
                entity.move(SpMath.vec3(0.0, entity.getDeltaMovement().y, 1.25), true)
            }

            if (animTime >= 0.35) {
                entity.getPreInput().executeExcept("dodge")
            }
        })

        skill.onHurt(event => {
            event.setCanceled(true)
            if (skill.getTickCount() <= 10) {
                skill.perfectDodge(event)
            }
        })

        skill.onLocalInputUpdate(event => {
            EntityHelper.preventLocalInput(event)
        })

        skill.onPerfectDodge((event, times) => {
            level.playSound(entity.getOnPos().above(), "spirit_of_fight:perfect_dodge", "players")
            animatable.summonShadow(20, 0x808080)
        })
    })
})