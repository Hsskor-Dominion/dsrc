package script.npe;

import script.library.groundquests;
import script.location;
import script.obj_id;

public class handoff_to_tatooine extends script.base_script
{
    public handoff_to_tatooine()
    {
    }
    public static final String questNewbieStart = "quest/legacy_button_start";
    public static final String questNewbieStartSmuggler = "quest/speeder_quest";
    public static final String questNewbieStartSpy = "quest/naboo_send_to_lt_jasper";
    public static final String questNewbieStartBH = "quest/stardust_send_to_boba";
    public static final String questCrafterEntertainer = "quest/tatooine_eisley_noncombat";
    public int OnLogin(obj_id self) throws InterruptedException
    {
        location origin = getLocation(self);
        location fighting = new location(-2899.0f, 0.0f, 2125.0f, origin.area);
        location crafty = new location(-2900.0f, 6.0f, 2126.0f, origin.area);
        String profession = getSkillTemplate(self);
        int crafter = profession.indexOf("trader");
        int entertainer = profession.indexOf("entertainer");
        int bountyhunter = profession.indexOf("bounty_hunter");
        int spy = profession.indexOf("spy");
        int smuggler = profession.indexOf("smuggler");
        if (crafter > -1 || entertainer > -1)
        {
            if (!groundquests.isQuestActiveOrComplete(self, questCrafterEntertainer))
            {
                groundquests.grantQuest(self, questCrafterEntertainer);
            }
        }
        else if (bountyhunter > -1)
        {
            if (groundquests.hasCompletedQuest(self, questNewbieStartBH) || groundquests.isQuestActive(self, questNewbieStartBH))
            {
                detachScript(self, "npe.handoff_to_tatooine");
            }
            else 
            {
                groundquests.requestGrantQuest(self, questNewbieStartBH);
            }
        }
        else if (spy > -1)
        {
            if (groundquests.hasCompletedQuest(self, questNewbieStartSpy) || groundquests.isQuestActive(self, questNewbieStartSpy))
            {
                detachScript(self, "npe.handoff_to_tatooine");
            }
            else
            {
                groundquests.requestGrantQuest(self, questNewbieStartSpy);
            }
        }
        else if (smuggler > -1)
        {
            if (groundquests.hasCompletedQuest(self, questNewbieStartSmuggler) || groundquests.isQuestActive(self, questNewbieStartSmuggler))
            {
                detachScript(self, "npe.handoff_to_tatooine");
            }
            else
            {
                groundquests.requestGrantQuest(self, questNewbieStartSmuggler);
            }
        }
        else 
        {
            if (groundquests.hasCompletedQuest(self, questNewbieStart) || groundquests.isQuestActive(self, questNewbieStart))
            {
                detachScript(self, "npe.handoff_to_tatooine");
            }
            else 
            {
                groundquests.requestGrantQuest(self, questNewbieStart);
            }
        }
        if (crafter > -1)
        {
            newbieTutorialSetToolbarElement(self, 10, "/survey");
        }
        newbieTutorialEnableHudElement(self, "radar", true, 0);
        detachScript(self, "npe.handoff_to_tatooine");
        return SCRIPT_CONTINUE;
    }
}
