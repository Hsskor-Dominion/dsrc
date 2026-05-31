package script.systems.camping;

import script.*;
import script.library.*;

public class camp_master extends script.base_script
{
    public camp_master()
    {
    }
    public static final String TOTAL_XP = "total_xp";
    public static final int CAMP_TICK_XP = 25;
    public static final int XP_MAX[] = 
    {
        0,
        350,
        600,
        800,
        1000,
        1100,
        1200
    };
    public static final float XP_MULT[] = 
    {
            0.0f,
            1.0f,
        1.1f,
        1.2f,
        1.3f,
        1.4f,
        1.5f
    };
    public static final String IGNORE_RESTORE_MESSAGE = "ignoreRestoreMsg";
    public static final String VAR_CAMP_CAMPFIRE = "campfire";
    public static final String TEMPLATE_LOGS_FRESH = "object/static/structure/general/campfire_fresh.iff";
    public static final String TEMPLATE_LOGS_BURNT = "object/static/structure/general/campfire_burnt.iff";
    public static final String TEMPLATE_LOGS_SMOLDERING = "object/static/structure/general/campfire_smoldering.iff";
    public static final String TEMPLATE_LOGS_ASH = "object/static/structure/general/campfire_ash.iff";
    public static final string_id SID_SYS_COMBAT_ABANDONED = new string_id("camp", "sys_combat_abandoned");
    public static final string_id SID_SYS_ABANDONED_CAMP = new string_id("camp", "sys_abandoned_camp");
    public static final string_id SID_SYS_CAMP_HEAL = new string_id("camp", "sys_camp_heal");
    public static final int HEALING_PULSE_MIN = 100;
    public static final int HEALING_PULSE_MAX = 300;
    public static final int WOUND_HEAL = 5;
    public static final int SHOCK_HEAL = 1;
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setObjVar(self, camping.VAR_BEEN_INITIALIZED, true);
        createTriggerVolume("camp_" + self, camping.getCampSize(self), true);
        camping.setCampMasterName(self);
        obj_id owner = getObjIdObjVar(self, camping.VAR_OWNER);
        obj_id[] players = getPlayerCreaturesInRange(getLocation(self), camping.getCampSize(self));
        setObjVar(self, camping.VAR_OWNER_NEAR, true);
        int campPower = getIntObjVar(self, camping.VAR_CAMP_POWER);
        int campPowerBonus = campPower;
        if (campPowerBonus > 5)
        {
            campPowerBonus = 5;
        }
        int min = HEALING_PULSE_MIN;
        int max = HEALING_PULSE_MAX - (50 * campPowerBonus);
        int pulse = rand(min, max);
        messageTo(self, "OnHealingLoop", null, pulse, false);
        return SCRIPT_CONTINUE;
    }
    public int OnDestroy(obj_id self) throws InterruptedException
    {
        // ------------------------
        // Cleanup campfire
        // ------------------------
        obj_id item = getObjIdObjVar(self, VAR_CAMP_CAMPFIRE);

        if ((item != null) && (item != obj_id.NULL_ID))
        {
            destroyObject(item);
        }

        // ------------------------
        // Validate owner
        // ------------------------
        obj_id owner = getObjIdObjVar(self, camping.VAR_OWNER);

        if (!isIdValid(owner) || !exists(owner))
        {
            return SCRIPT_CONTINUE;
        }

        // ------------------------
        // Legacy accumulated XP
        // ------------------------
        int legacyXp = getIntObjVar(self, camping.VAR_CAMP_XP);

        // ------------------------
        // Time-based XP
        // ------------------------
        int creationTime = hasObjVar(self, camping.VAR_CREATION_TIME)
                ? getIntObjVar(self, camping.VAR_CREATION_TIME)
                : 0;

        int now = getCalendarTime();

        int uptimeSeconds;

        if (creationTime <= 0 || creationTime > now)
        {
            uptimeSeconds = 60;
        }
        else
        {
            uptimeSeconds = Math.max(now - creationTime, 60);
        }

        int uptimeMinutes = uptimeSeconds / 60;

        // ------------------------
        // Base XP
        // ------------------------
        int BASE_XP_PER_MINUTE = 10;

        int timeXp = uptimeMinutes * BASE_XP_PER_MINUTE;

        // ------------------------
        // Occupancy bonus
        // ------------------------
        int occCount = getIntObjVar(self, "occ_count");

        if (occCount < 1)
        {
            occCount = 1;
        }

        float OCC_BONUS_PER = 0.05f;
        float MAX_OCC_BONUS = 1.0f;

        float occBonus = Math.min(
                occCount * OCC_BONUS_PER,
                MAX_OCC_BONUS
        );

        // ------------------------
        // Visitor bonus
        // ------------------------
        int visitorCount = getIntObjVar(self, "visitor_count");

        float VISITOR_BONUS_PER = 0.02f;
        float MAX_VISITOR_BONUS = 0.50f;

        float visitorBonus = Math.min(
                visitorCount * VISITOR_BONUS_PER,
                MAX_VISITOR_BONUS
        );

        // ------------------------
        // Camp module bonus
        // ------------------------
        int moduleCount = camping.getCampModuleCount(self);

        float MODULE_BONUS_PER = 0.10f;
        float MAX_MODULE_BONUS = 0.90f;

        float moduleBonus = Math.min(
                moduleCount * MODULE_BONUS_PER,
                MAX_MODULE_BONUS
        );

        // ------------------------
        // Final dynamic XP
        // ------------------------
        float finalMultiplier =
                1.0f +
                        occBonus +
                        visitorBonus +
                        moduleBonus;

        int dynamicXp = Math.round(timeXp * finalMultiplier);

        // ------------------------
        // Combine legacy + dynamic
        // ------------------------
        int xpGranted = legacyXp + dynamicXp;

//        // ------------------------
//        // Final clamps
//        // ------------------------
//        int MIN_CAMP_XP = 25;
//        int MAX_CAMP_XP = 25000;
//
//        xpGranted = Math.max(xpGranted, MIN_CAMP_XP);
//        xpGranted = Math.min(xpGranted, MAX_CAMP_XP);

        // ------------------------
        // Grant XP
        // ------------------------
        camping.grantCampXp(owner, xpGranted);

        // ------------------------
        // Debug logging
        // ------------------------
        LOG("camping",
                "Camp destroyed: owner=" + owner +
                        " legacyXp=" + legacyXp +
                        " dynamicXp=" + dynamicXp +
                        " totalXp=" + xpGranted +
                        " uptimeMin=" + uptimeMinutes +
                        " occ=" + occCount +
                        " visitors=" + visitorCount +
                        " modules=" + moduleCount);

        return SCRIPT_CONTINUE;
    }
    public int theaterFinished(obj_id self, dictionary params) throws InterruptedException
    {
        notifyChildren(self, "handleCampPrep");
        return SCRIPT_CONTINUE;
    }
    public void notifyChildren(obj_id self, String msg) throws InterruptedException
    {
        dictionary outparams = new dictionary();
        outparams.put("master", self);
        obj_id[] children = getObjIdArrayObjVar(self, theater.VAR_CHILDREN);
        if ((children == null) || (children.length == 0))
        {
            return;
        }
        else 
        {
            int j = 0;
            for (obj_id child : children) {
                if ((child == null) || (child == obj_id.NULL_ID)) {
                } else {
                    messageTo(child, msg, outparams, 0, false);
                }
            }
        }
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if (hasObjVar(self, camping.VAR_BEEN_INITIALIZED))
        {
            camping.nukeCamp(self);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnUnloadedFromMemory(obj_id self) throws InterruptedException
    {
        camping.nukeCamp(self);
        return SCRIPT_CONTINUE;
    }
    public int OnTriggerVolumeEntered(obj_id self, String volName, obj_id who) throws InterruptedException
    {
        if (who == self)
        {
            return SCRIPT_CONTINUE;
        }
        if (volName.equals("camp_" + self))
        {
            obj_id owner = getObjIdObjVar(self, camping.VAR_OWNER);
            if ((owner == null) || (owner == obj_id.NULL_ID))
            {
                sendSystemMessage(who, camping.SID_CAMP_ENTER);
                sendSystemMessage(who, SID_SYS_CAMP_HEAL);
            }
            else 
            {
                addCampMember(self, owner, who);
            }
        }
        return SCRIPT_CONTINUE;
    }
    public void addCampMember(obj_id self, obj_id owner, obj_id who) throws InterruptedException
    {
        if (who == owner)
        {
            setObjVar(self, camping.VAR_OWNER_NEAR, true);
            int status = camping.getStatus(self);
            if (status == camping.STATUS_ABANDONED)
            {
                camping.setStatus(self, camping.STATUS_MAINTAIN);
                camping.sendCampMaintenanceHeartbeat(self);
                setObjVar(self, IGNORE_RESTORE_MESSAGE, true);
            }
        }
        camping.setCurrentCamp(who, self);
        prose_package pp = prose.getPackage(camping.PROSE_CAMP_ENTER, getName(self));//this is returning you have entered camp master's camp, weird
        sendSystemMessageProse(who, pp);
        sendSystemMessage(who, SID_SYS_CAMP_HEAL);
        int count = getIntObjVar(self, "visitor_count");
        count++;
        setObjVar(self, "visitor_count", count);
        int occ = getIntObjVar(self, "occ_count");
        occ++;
        setObjVar(self, "occ_count", occ);
    }
    public int OnTriggerVolumeExited(obj_id self, String volName, obj_id who) throws InterruptedException
    {
        if (who == self)
        {
            return SCRIPT_CONTINUE;
        }
        if (volName.equals("camp_" + self))
        {
            obj_id owner = getObjIdObjVar(self, camping.VAR_OWNER);
            if ((owner == null) || (owner == obj_id.NULL_ID))
            {
                sendSystemMessage(who, camping.SID_CAMP_EXIT);
            }
            else 
            {
                if (who == owner)
                {
                    setObjVar(self, camping.VAR_OWNER_NEAR, false);
                }
                camping.clearCurrentCamp(who);
                prose_package pp = prose.getPackage(camping.PROSE_CAMP_EXIT, getName(self));
                sendSystemMessageProse(who, pp);
            }
            int occ = getIntObjVar(self, "occ_count");
            occ--;
            setObjVar(self, "occ_count", occ);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnHealingLoop(obj_id self, dictionary params) throws InterruptedException
    {
        int campPower = getIntObjVar(self, camping.VAR_CAMP_POWER);
        obj_id[] players = getPlayerCreaturesInRange(getLocation(self), camping.getCampSize(self));
        if (players != null)
        {
            for (obj_id player : players) {
                for (int j = 0; j < NUM_ATTRIBUTES; j++) {
                    if (healing.isWounded(player, j)) {
                        int xpAmt = getIntObjVar(self, camping.VAR_CAMP_XP);
                        int xpMax = XP_MAX[campPower];
                        if (xpAmt < xpMax) {
                            int toGrant = CAMP_TICK_XP;
                            xpAmt += toGrant;
                            setObjVar(self, camping.VAR_CAMP_XP, xpAmt);
                        }
                    }
                }
            }
        }
        int campPowerBonus = campPower;
        if (campPowerBonus > 5)
        {
            campPowerBonus = 5;
        }
        int min = HEALING_PULSE_MIN;
        int max = HEALING_PULSE_MAX - (25 * campPowerBonus);
        int pulse = rand(min, max);
        messageTo(self, "OnHealingLoop", null, pulse, false);
        return SCRIPT_CONTINUE;
    }
    public int handleSetStatus(obj_id self, dictionary params) throws InterruptedException
    {
        if ((params == null) || (params.isEmpty()))
        {
            return SCRIPT_CONTINUE;
        }
        int status = params.getInt(camping.DICT_NEW_STATUS);
        switch (status)
        {
            case camping.STATUS_NEW:
            setCampfire(self, TEMPLATE_LOGS_FRESH);
            break;
            case camping.STATUS_CREATION:
            break;
            case camping.STATUS_MAINTAIN:
            setCampfire(self, TEMPLATE_LOGS_FRESH);
            break;
            case camping.STATUS_ABANDONED:
            default:
            setCampfire(self, TEMPLATE_LOGS_SMOLDERING);
            break;
        }
        return SCRIPT_CONTINUE;
    }
    public void setCampfire(obj_id self, String tpf) throws InterruptedException
    {
        obj_id item = getObjIdObjVar(self, VAR_CAMP_CAMPFIRE);
        if ((item == null) || (item == obj_id.NULL_ID))
        {
        }
        else 
        {
            String itemTemplate = getTemplateName(item);
            if (itemTemplate.equals(tpf))
            {
                return;
            }
            destroyObject(item);
        }
        location here = getLocation(self);
        item = createObject(tpf, here);
        setObjVar(self, VAR_CAMP_CAMPFIRE, item);
    }
    public int handleCampCreationHeartbeat(obj_id self, dictionary params) throws InterruptedException
    {
        if (!hasObjVar(self, camping.VAR_OWNER))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        obj_id owner = getObjIdObjVar(self, camping.VAR_OWNER);
        if ((owner == null) || (owner == obj_id.NULL_ID))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        if ((!exists(owner)) || (!isInWorld(owner)) || (!owner.isLoaded()))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        if (camping.getStatus(self) == camping.STATUS_NEW)
        {
            camping.setStatus(self, camping.STATUS_CREATION);
        }
        camping.sendCampCreationComplete(self);
        return SCRIPT_CONTINUE;
    }
    public int handleCampComplete(obj_id self, dictionary params) throws InterruptedException
    {
        if (!hasObjVar(self, camping.VAR_OWNER))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        obj_id owner = getObjIdObjVar(self, camping.VAR_OWNER);
        if ((owner == null) || (owner == obj_id.NULL_ID))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        if ((!exists(owner)) || (!isInWorld(owner)) || (!owner.isLoaded()))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        if (!camping.isOwnerInVicinity(self))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        camping.setStatus(self, camping.STATUS_MAINTAIN);
        camping.sendCampMaintenanceHeartbeat(self);
        return SCRIPT_CONTINUE;
    }
    public int handleCampMaintenanceHeartbeat(obj_id self, dictionary params) throws InterruptedException
    {
        if (!hasObjVar(self, camping.VAR_OWNER))
        {
            setObjVar(self, camping.VAR_OWNER, obj_id.NULL_ID);
            camping.sendCampRestoreHeartbeat(self);
            return SCRIPT_CONTINUE;
        }
        obj_id owner = getObjIdObjVar(self, camping.VAR_OWNER);
        if ((owner == null) || (owner == obj_id.NULL_ID))
        {
            camping.campAbandoned(self);
            camping.sendCampRestoreHeartbeat(self);
            return SCRIPT_CONTINUE;
        }
        if ((!exists(owner)) || (!isInWorld(owner)) || (!owner.isLoaded()))
        {
            camping.campAbandoned(self);
            camping.sendCampRestoreHeartbeat(self);
            return SCRIPT_CONTINUE;
        }
        if (ai_lib.isInCombat(owner))
        {
            sendSystemMessage(owner, SID_SYS_COMBAT_ABANDONED);
            camping.campAbandoned(self);
            camping.sendCampRestoreHeartbeat(self);
            return SCRIPT_CONTINUE;
        }
        int ticks = getIntObjVar(self, "camp.ticks");
        ticks++;
        setObjVar(self, "camp.ticks", ticks);
        if (ticks > 100)
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        if (!camping.isOwnerInVicinity(self))
        {
            camping.setStatus(self, camping.STATUS_ABANDONED);
            camping.sendCampRestoreHeartbeat(self);
            return SCRIPT_CONTINUE;
        }
        int campOcc = getIntObjVar(self, "occ_count");
        int roll = rand(0, 100);
        roll += (campOcc - 1) * 5;
        if (campOcc > 5)
        {
            campOcc = 5;
        }
        if (roll > 40)
        {
            int xpAmt = getIntObjVar(self, camping.VAR_CAMP_XP);
            int campPower = getIntObjVar(self, camping.VAR_CAMP_POWER);
            int xpMax = XP_MAX[campPower];
            if (xpAmt < xpMax)
            {
                int toGrant = (int)(CAMP_TICK_XP * campOcc * XP_MULT[campPower]);
                xpAmt += toGrant;
                setObjVar(self, camping.VAR_CAMP_XP, xpAmt);
            }
        }
        camping.sendCampMaintenanceHeartbeat(self);
        return SCRIPT_CONTINUE;
    }
    public int handleCampRestoreHeartbeat(obj_id self, dictionary params) throws InterruptedException
    {
        if (!hasObjVar(self, camping.VAR_OWNER))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        if (hasObjVar(self, IGNORE_RESTORE_MESSAGE))
        {
            removeObjVar(self, IGNORE_RESTORE_MESSAGE);
            return SCRIPT_CONTINUE;
        }
        obj_id owner = getObjIdObjVar(self, camping.VAR_OWNER);
        if ((owner == null) || (owner == obj_id.NULL_ID))
        {
            camping.nukeCamp(self);
            return SCRIPT_CONTINUE;
        }
        else 
        {
            if (camping.isOwnerInVicinity(self))
            {
                camping.setStatus(self, camping.STATUS_MAINTAIN);
                camping.sendCampMaintenanceHeartbeat(self);
                return SCRIPT_CONTINUE;
            }
            else 
            {
                sendSystemMessage(owner, SID_SYS_ABANDONED_CAMP);
                camping.clearCampOwner(self);
                setObjVar(self, camping.VAR_CAMP_XP, 0);
                camping.setCampMasterName(self);
                camping.sendCampRestoreHeartbeat(self);
                camping.campAbandoned(self);
                return SCRIPT_CONTINUE;
            }
        }
    }
    public int handleNuke(obj_id self, dictionary params) throws InterruptedException
    {
        camping.nukeCamp(self);
        return SCRIPT_CONTINUE;
    }
}
