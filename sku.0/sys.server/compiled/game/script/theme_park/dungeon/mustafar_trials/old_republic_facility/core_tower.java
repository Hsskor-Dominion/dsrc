package script.theme_park.dungeon.mustafar_trials.old_republic_facility;

import script.base_script;
import script.library.create;
import script.library.groundquests;
import script.location;
import script.obj_id;

public class core_tower extends base_script
{
    public core_tower() {}

    public int OnReceivedItem(obj_id self, obj_id srcContainer, obj_id transferer, obj_id item) throws InterruptedException
    {
        // Only proceed if the item is a player
        if (!isPlayer(item))
        {
            return SCRIPT_CONTINUE;
        }
        // Get the building object
        obj_id building = getTopMostContainer(self);

        // Only affect players coming from Naboo
        if (hasObjVar(item, "stardust.return_to_naboo"))
        {
            playMusic(item, self, "sound/mus_duel_of_the_fates_lcv.snd", 0, true);
        }

        return SCRIPT_CONTINUE;
    }

    private static final String CREATURE_TABLE = "datatables/mob/creatures.iff";

//    private void spawnMaul(obj_id player, obj_id tower) throws InterruptedException
//    {
//        // Get player's location (including correct cell)
//        location spawnLoc = getLocation(player);
//
//        // Optional slight offset so Maul doesn’t spawn directly inside the player
//        spawnLoc.x += 1.0f;
//        spawnLoc.z += 1.0f;
//
//        // Spawn Darth Maul directly at player's position
//        obj_id darthMaul = create.createCreature("stardust_maul", spawnLoc, true);
//
//        if (isIdValid(darthMaul))
//        {
//            // Play Duel of the Fates music for dramatic effect
//            playMusic(player, darthMaul, "sound/mus_duel_of_the_fates_lcv.snd", 0, true);
//        }
//    }
}