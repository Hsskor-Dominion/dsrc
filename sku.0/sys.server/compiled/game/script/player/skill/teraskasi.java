package script.player.skill;

import script.library.*;
import script.obj_id;
import script.prose_package;

import static script.library.healing.healDamage;

public class teraskasi extends script.systems.combat.combat_base
{
    public teraskasi()
    {
    }
    public int cmdForceOfWill(obj_id self, obj_id target, String params, float defaultTime) throws InterruptedException
    {
        int modval = meditation.getMeditationSkillMod(self);
        if (modval < 1)
        {
            return SCRIPT_CONTINUE;
        }

        // Must be incapacitated
        if (getPosture(self) != POSTURE_INCAPACITATED)
        {
            return SCRIPT_CONTINUE;
        }

        int stamp = getIntObjVar(self, meditation.VAR_FORCE_OF_WILL_ACTIVE);
        if (stamp < 0)
        {
            return SCRIPT_CONTINUE;
        }

        int now = getGameTime();
        int delta = now - (stamp + 3600);
        if (delta < 0)
        {
            String time_string = player_structure.assembleTimeRemaining(
                    player_structure.convertSecondsTime(-delta)
            );
            prose_package ppUnavailable =
                    prose.getPackage(meditation.SID_FORCEOFWILL_UNAVAILABLE, time_string);
            sendSystemMessageProse(self, ppUnavailable);
            return SCRIPT_CONTINUE;
        }

        int roll = rand(0, 100);//this should check against "meditate" skill
        if (roll < 5 || modval < roll)
        {
            // Failure burns the charge
            setObjVar(self, meditation.VAR_FORCE_OF_WILL_ACTIVE, -1);
            return SCRIPT_CONTINUE;
        }

        // SUCCESS: heal 1 point of health damage
        healDamage(self, HEALTH, 1);

        // Put ability on cooldown
        setObjVar(self, meditation.VAR_FORCE_OF_WILL_ACTIVE, now);

        return SCRIPT_CONTINUE;
    }
    public int cmdForceOfWillFail(obj_id self, obj_id target, String params, float defaultTime) throws InterruptedException
    {
        return SCRIPT_CONTINUE;
    }
    public int cmdPowerBoost(obj_id self, obj_id target, String params, float defaultTime) throws InterruptedException
    {
        if (!isIdValid(self))
        {
            return SCRIPT_OVERRIDE;
        }
        int modval = meditation.getMeditationSkillMod(self);
        if ((modval < 1))
        {
            return SCRIPT_OVERRIDE;
        }
        if (!meditation.isMeditating(self))
        {
            combat.sendCombatSpamMessage(self, meditation.SID_POWERBOOST_FAIL);
            return SCRIPT_OVERRIDE;
        }
        if (buff.hasBuff(self, "powerBuff"))
        {
            combat.sendCombatSpamMessage(self, meditation.SID_POWERBOOST_ACTIVE);
            return SCRIPT_OVERRIDE;
        }
        float duration = 300.0f + ((modval) * 3.0f);
        if (!combatStandardAction("powerBoost", self, self, null, "", ""))
        {
            return SCRIPT_OVERRIDE;
        }
        buff.applyBuff(self, "powerBoost", duration);
        combat.sendCombatSpamMessage(self, meditation.SID_POWERBOOST_BEGIN);
        return SCRIPT_CONTINUE;
    }
    public int cmdPowerBoostFail(obj_id self, obj_id target, String params, float defaultTime) throws InterruptedException
    {
        combat.sendCombatSpamMessage(self, meditation.SID_POWERBOOST_FAIL);
        return SCRIPT_CONTINUE;
    }
}
