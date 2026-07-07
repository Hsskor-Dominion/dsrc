package script.systems.spawning.dropship;

import script.dictionary;
import script.library.ai_lib;
import script.library.create;
import script.library.static_item;
import script.library.utils;
import script.location;
import script.obj_id;

import java.util.Vector;

public class lambda extends script.systems.spawning.dropship.base
{
    public lambda()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        messageTo(self, "handleAttachDelay", null, 2.0f, false);
        return super.OnAttach(self);
    }
    public int pykeSpawnCrate(obj_id self, dictionary params) throws InterruptedException
    {
        // just re-schedule the real spawn
        messageTo(self, "pykeSpawnCrateDo", params, 20.0f, false);
        return SCRIPT_CONTINUE;
    }
    public int pykeSpawnCrateDo(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id owner = params.getObjId("owner");
        location loc = params.getLocation("loc");
        int supplyId = params.getInt("supplyId");

        obj_id crate = createObject(
                "object/tangible/container/drum/supply_drop_crate.iff",
                loc
        );

        if (isIdValid(crate))
        {
            static_item.createNewItemFunction("item_smuggler_contraband_crate_01_02", crate);
            static_item.createNewItemFunction("item_stardust_contraband_crate", crate);

            String[] spiceItems =
                    {
                            "item_roadmap_spice_shadowpaw_01_02",
                            "item_roadmap_spice_thruster_head_01_02",
                            "item_roadmap_spice_crash_n_burn_01_02"
                    };

            for (int i = 0; i < 3; i++)
            {
                int r = rand(0, spiceItems.length - 1);
                static_item.createNewItemFunction(spiceItems[r], crate);
            }

            dictionary d = new dictionary();
            d.put("owner", owner);

            messageTo(crate, "startTakeOffSequence", d, 2.0f, false);
        }

        return SCRIPT_CONTINUE;
    }
    public int handleAttachDelay(obj_id self, dictionary params) throws InterruptedException
    {
        stop(self);
        setPosture(self, POSTURE_PRONE);
        messageTo(self, "spawnPayload", null, 20.0f, true);
        queueCommand(self, (-1465754503), self, "", COMMAND_PRIORITY_FRONT);
        return SCRIPT_CONTINUE;
    }
    public int changePosture(obj_id self, dictionary params) throws InterruptedException
    {
        setPosture(self, POSTURE_UPRIGHT);
        messageTo(self, "selfCleanUp", null, 60.0f, false);
        return SCRIPT_CONTINUE;
    }
    public int spawnPayload(obj_id self, dictionary params) throws InterruptedException
    {
        String[] spawnNames = utils.getStringArrayScriptVar(self, "spawnNames");
        if (spawnNames != null && spawnNames.length > 0)
        {
            location here = getLocation(self);
            if (here != null)
            {
                int pos = 0;
                obj_id leader = null;
                Vector spawns = new Vector();
                spawns.setSize(0);
                obj_id thisSpawn;
                for (int i = 0; i < spawnNames.length; i++)
                {
                    thisSpawn = create.object(spawnNames[i], here);
                    if (isIdValid(thisSpawn) && !isIdValid(leader))
                    {
                        leader = thisSpawn;
                        spawns = utils.addElement(spawns, thisSpawn);
                        if (i > 0 && isIdValid(leader))
                        {
                            ai_lib.wander(thisSpawn);
                            attachScript(thisSpawn, "ai.imperial_presence.harass");
                        }
                    }
                    if (isIdValid(thisSpawn))
                    {
                        detachScript(leader, "ai.soldier");
                        setMovementRun(thisSpawn);
                        utils.removeObjVar(thisSpawn, "ai.diction");
                        ai_lib.followInFormation(thisSpawn, leader, 4, ++pos);
                    }
                }
                if (spawns != null && spawns.size() > 0)
                {
                    dictionary spawnParams = utils.getDictionaryScriptVar(self, "spawnParameters");
                    if (spawnParams != null && !spawnParams.isEmpty())
                    {
                        messageTo(((obj_id)spawns.get(0)), "handleSpawnParameters", spawnParams, 1.0f, false);
                    }
                }
            }
        }
        messageTo(self, "changePosture", null, 5, false);
        return SCRIPT_CONTINUE;
    }
    public int selfCleanUp(obj_id self, dictionary params) throws InterruptedException
    {
        if (isIdValid(self))
        {
            destroyObject(self);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_CONTINUE;
    }
}
