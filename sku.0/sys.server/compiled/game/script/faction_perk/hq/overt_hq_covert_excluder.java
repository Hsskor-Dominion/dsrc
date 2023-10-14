package script.faction_perk.hq;

import script.library.*;
import script.library.factions;
import script.library.utils;
import script.obj_id;
import script.string_id;

public class overt_hq_covert_excluder extends script.base_script
{
    public overt_hq_covert_excluder()
    {
    }
    private static final string_id SID_YOURE_NEUTRAL_EXCLUDED = new string_id("faction/faction_hq/faction_hq_response", "youre_neutral_excluded");
    private static final string_id SID_YOURE_COVERT_EXCLUDED = new string_id("faction/faction_hq/faction_hq_response", "youre_covert_excluded");
    public int OnAboutToReceiveItem(obj_id self, obj_id srcContainer, obj_id transferer, obj_id item) throws InterruptedException
    {
        LOG("LOG_CHANNEL", "permanent_structure::OnAboutToReceiveItem --" + transferer + " - " + item);
        if (isPlayer(item))
        {
            if (isNeutral(item) && !isIdValid(srcContainer))
            {
                LOG("LOG_CHANNEL", "permanent_structure::OnAboutToReceiveItem -- player checked out as neutral, so we're disallowing them entry.");
                sendSystemMessage(item, SID_YOURE_NEUTRAL_EXCLUDED);
                return SCRIPT_OVERRIDE;
            }
            if (hasObjVar(self, "isPvpBase"))
            {
                if (isCovert(item) && !isIdValid(srcContainer))
                {
                    LOG("LOG_CHANNEL", "permanent_structure::OnAboutToReceiveItem -- player checked out as covert, so we're disallowing them entry to the base flagged with isPvpBase objvar.");
                    sendSystemMessage(item, SID_YOURE_COVERT_EXCLUDED);
                    return SCRIPT_OVERRIDE;
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnReceivedItem(obj_id self, obj_id srcContainer, obj_id transferer, obj_id item) throws InterruptedException
    {
        utils.setScriptVar(item, factions.IN_ADHOC_PVP_AREA, true);

        // Apply the corresponding buff to the player upon entering the HQ structure
        if (isPlayer(item) && factions.isDeclared(item) && (factions.getFaction(item)).equals(factions.getFaction(self)))
        {
            String structureTemplate = getTemplateName(self);
            String buffToApply = "";

            if (structureTemplate.contains("hq_s02"))
            {
                buffToApply = "banner_buff_medic";
            }
            else if (structureTemplate.contains("hq_s03"))
            {
                buffToApply = "banner_buff_entertainer";
            }
            else if (structureTemplate.contains("hq_s04"))
            {
                buffToApply = "banner_buff_trader";
            }

            if (!buffToApply.equals(""))
            {
                buff.applyBuff(item, buffToApply);
            }
        }

        return SCRIPT_CONTINUE;
    }
    public int OnAboutToLoseItem(obj_id self, obj_id destContainer, obj_id transferer, obj_id item) throws InterruptedException
    {
        utils.removeScriptVar(item, factions.IN_ADHOC_PVP_AREA);
        return SCRIPT_CONTINUE;
    }
    private boolean isCovert(obj_id target) throws InterruptedException
    {
        return pvpGetType(target) == PVPTYPE_COVERT;
    }
    private boolean isNeutral(obj_id target) throws InterruptedException
    {
        return pvpGetType(target) == PVPTYPE_NEUTRAL;
    }
}
