package com.sm1les.meowmur;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class MeowmurItem extends Item {
	/** Метка на всех котах, выпущенных Мяумуром. */
	public static final String CAT_TAG = "meowmur_cat";
	private static final int COOLDOWN_TICKS = 8;

	public MeowmurItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		// на клиенте просто показываем взмах руки, вся логика на сервере
		if (!(world instanceof ServerWorld serverWorld)) {
			return ActionResult.SUCCESS;
		}

		if (user.isSneaking()) {
			// Shift + ПКМ: собрать всех котов Мяумура в радиусе 128 блоков
			List<CatEntity> cats = serverWorld.getEntitiesByClass(
					CatEntity.class,
					user.getBoundingBox().expand(128.0),
					cat -> cat.getCommandTags().contains(CAT_TAG)
			);
			for (CatEntity cat : cats) {
				cat.discard();
			}
			user.sendMessage(Text.literal("Котов собрано: " + cats.size()), true);
			return ActionResult.SUCCESS;
		}

		Vec3d look = user.getRotationVec(1.0f);
		Vec3d start = user.getEyePos().add(look.multiply(1.2));

		CatEntity cat = EntityType.CAT.create(serverWorld, SpawnReason.MOB_SUMMONED);
		if (cat == null) {
			return ActionResult.FAIL;
		}
		cat.setPosition(start.x, start.y - 0.3, start.z);
		cat.setYaw(user.getYaw());
		// случайная окраска кота
		cat.initialize(serverWorld, serverWorld.getLocalDifficulty(cat.getBlockPos()), SpawnReason.MOB_SUMMONED, null);
		cat.setInvulnerable(true);   // бессмертный
		cat.setPersistent();         // не исчезает сам
		cat.addCommandTag(CAT_TAG);
		cat.setVelocity(look.multiply(1.6).add(0.0, 0.15, 0.0));
		serverWorld.spawnEntity(cat);

		serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(),
				SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.PLAYERS, 1.0f, 1.0f);
		user.getItemCooldownManager().set(user.getStackInHand(hand), COOLDOWN_TICKS);
		return ActionResult.SUCCESS;
	}
}
