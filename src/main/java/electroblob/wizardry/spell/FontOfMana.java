package electroblob.wizardry.spell;

import java.util.List;

import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class FontOfMana extends Spell {

	public FontOfMana(){
		super("font_of_mana", Tier.MASTER, Element.HEALING, SpellType.UTILITY, 100, 250, EnumAction.BOW, false);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers){

		List<EntityPlayer> targets = WizardryUtilities.getEntitiesWithinRadius(
				5 * modifiers.get(WizardryItems.blast_upgrade), caster.posX, caster.posY, caster.posZ, world,
				EntityPlayer.class);

		for(EntityPlayer target : targets){
			if(WizardryUtilities.isPlayerAlly(caster, target) || target == caster){
				// Damage multiplier can only ever be 1 or 1.6 for master spells, so there's little point in actually
				// calculating this.
				target.addPotionEffect(new PotionEffect(WizardryPotions.font_of_mana,
						(int)(600 * modifiers.get(WizardryItems.duration_upgrade)),
						modifiers.get(SpellModifiers.POTENCY) > 1 ? 1 : 0));
			}
		}

		if(world.isRemote){
			for(int i = 0; i < 100 * modifiers.get(WizardryItems.blast_upgrade); i++){
				
				double radius = (1 + world.rand.nextDouble() * 4) * modifiers.get(WizardryItems.blast_upgrade);
				double angle = world.rand.nextDouble() * Math.PI * 2;
				float hue = world.rand.nextFloat() * 0.4f;
				
				double x = caster.posX + radius * Math.cos(angle);
				double y = caster.getEntityBoundingBox().minY;
				double z = caster.posZ + radius * Math.sin(angle);
				
				ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.03, 0).lifetime(50)
				.colour(1, 1 - hue, 0.6f + hue).spawn(world);
			}
		}

		WizardryUtilities.playSoundAtPlayer(caster, WizardrySounds.SPELL_HEAL, 0.7F,
				world.rand.nextFloat() * 0.4F + 1.0F);
		return true;
	}

}
