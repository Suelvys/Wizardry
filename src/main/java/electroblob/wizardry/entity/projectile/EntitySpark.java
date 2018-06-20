package electroblob.wizardry.entity.projectile;

import java.util.List;

import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySpark extends EntityMagicProjectile {

	public EntitySpark(World world){
		super(world);
	}

	@Override
	protected void onImpact(RayTraceResult rayTrace){
		
		Entity entityHit = rayTrace.entityHit;

		if(entityHit != null){

			float damage = 6 * damageMultiplier;
			entityHit.attackEntityFrom(MagicDamage.causeIndirectMagicDamage(this, this.getThrower(), DamageType.SHOCK),
					damage);

		}

		this.playSound(WizardrySounds.SPELL_SPARK, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

		// Particle effect
		if(world.isRemote){
			for(int i = 0; i < 8; i++){
				double x = this.posX + rand.nextDouble() - 0.5;
				double y = this.posY + this.height / 2 + rand.nextDouble() - 0.5;
				double z = this.posZ + rand.nextDouble() - 0.5;
				ParticleBuilder.create(Type.SPARK).pos(x, y, z).spawn(world);
			}
		}

		this.setDead();
	}

	public void onUpdate(){

		super.onUpdate();

		if(!this.collided && !world.isRemote){

			double seekingRange = 5.0d;

			List<EntityLivingBase> entities = WizardryUtilities.getEntitiesWithinRadius(seekingRange, this.posX,
					this.posY, this.posZ, this.world);
			Entity target = null;

			for(Entity possibleTarget : entities){
				// Decides if current entity should be replaced.
				if(target == null || this.getDistance(target) > this.getDistance(possibleTarget)){
					// Decides if new entity is a valid target.
					if(WizardryUtilities.isValidTarget(this.getThrower(), possibleTarget)){
						target = possibleTarget;
					}
				}
			}

			if(target != null && Math.abs(this.motionX) < 1 && Math.abs(this.motionY) < 1
					&& Math.abs(this.motionZ) < 1){
				this.addVelocity((target.posX - this.posX) / 30, (target.posY + target.height / 2 - this.posY) / 30,
						(target.posZ - this.posZ) / 30);
			}
		}

		if(this.ticksExisted > 100){
			this.setDead();
		}
	}
	
	@Override
	public boolean hasNoGravity(){
		return true;
	}

	@Override
	public boolean canRenderOnFire(){
		return false;
	}
}
