package script.systems.jedi;

import script.obj_id;

public class darksaber_particle extends script.base_script
{
    public darksaber_particle() {}

    // Called when script is attached or the saber is loaded into the world
    public int OnAttach(obj_id self) throws InterruptedException
    {
        applyParticleEffect(self);
        return SCRIPT_CONTINUE;
    }

    public int OnInitialize(obj_id self) throws InterruptedException
    {
        applyParticleEffect(self);
        return SCRIPT_CONTINUE;
    }

    private void applyParticleEffect(obj_id saber) throws InterruptedException
    {
        if (!isIdValid(saber))
            return;

        // Viewers include the saber itself
        obj_id[] viewers = new obj_id[]{saber};

        // Primary Darksaber particle effect (white blade)
//        String bladeParticle = "sw_light_saber_white.swh";
        String bladeParticle ="sw_streaky_glow_coolwhite.swh";
        playClientEffectObj(viewers, bladeParticle, saber, "");
    }
}
