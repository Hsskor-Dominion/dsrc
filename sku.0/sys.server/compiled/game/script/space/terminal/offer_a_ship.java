package script.space.terminal;

import script.dictionary;
import script.library.*;
import script.menu_info_types;
import script.obj_id;
import script.string_id;

public class offer_a_ship extends script.base_script
{
    public offer_a_ship()
    {
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.ITEM_USE)
        {
            if (!space_utils.hasShip(player))
            {
                if (utils.hasScriptVar(player, "offer_a_ship.openSui"))
                {
                    int oldSui = utils.getIntScriptVar(player, "offer_a_ship.openSui");
                    utils.removeScriptVar(player, "offer_a_ship.openSui");
                    if (oldSui > -1)
                    {
                        forceCloseSUIPage(oldSui);
                    }
                }
                string_id title = new string_id("new_player", "offer_a_ship_title");
                string_id textMsg = new string_id("new_player", "offer_a_ship_msg");
                string_id okButton = new string_id("new_player", "default_okay_button");
                string_id cancelButton = new string_id("new_player", "default_cancel_button");
                String TITLE_MSG = utils.packStringId(title);
                String TEXT_MSG = utils.packStringId(textMsg);
                String OK_BUTTON = utils.packStringId(okButton);
                String CANCEL_BUTTON = utils.packStringId(cancelButton);
                int pid = sui.createSUIPage(sui.SUI_MSGBOX, self, player, "handleSpaceTerminalOfferAShip");
                setSUIProperty(pid, sui.MSGBOX_TITLE, sui.PROP_TEXT, TITLE_MSG);
                setSUIProperty(pid, sui.MSGBOX_PROMPT, sui.PROP_TEXT, TEXT_MSG);
                sui.msgboxButtonSetup(pid, sui.YES_NO);
                setSUIProperty(pid, sui.MSGBOX_BTN_OK, sui.PROP_TEXT, OK_BUTTON);
                setSUIProperty(pid, sui.MSGBOX_BTN_CANCEL, sui.PROP_TEXT, CANCEL_BUTTON);
                utils.setScriptVar(player, "offer_a_ship.openSui", pid);
                sui.showSUIPage(pid);
            }

            // Check for quest and auto-warp if active
            String questName = "smuggle_stardust";
            if (groundquests.isQuestActive(player, questName)) {
                sendSystemMessage(player, new string_id("space/space_interaction", "trandoshan_trafficking"));

                // Generate random number 1-100
                int roll = rand(1, 100);

                // Warp based on random roll
                if (roll >= 1 && roll <= 20) {
                    warpPlayer(player, "kashyyyk_hunting", -616, 8, 889, null, 0, 0, 0, "", false);
                }
                else if (roll >= 21 && roll <= 99) {
                    warpPlayer(player, "kashyyyk_main", 85, 8, 162, null, 0, 0, 0, "", false);
                }
                else if (roll == 100) {
                    warpPlayer(player, "dathomir", -6466, 8, 894, null, 0, 0, 0, "", false);
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int handleSpaceTerminalOfferAShip(obj_id self, dictionary params) throws InterruptedException
    {
        if ((params == null) || (params.isEmpty()))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id player = sui.getPlayerId(params);
        if (!isIdValid(player))
        {
            return SCRIPT_CONTINUE;
        }
        int bp = sui.getIntButtonPressed(params);
        switch (bp)
        {
            case sui.BP_OK:
            space_quest.grantNewbieShipNPE(player, "neutral");
            break;
            case sui.BP_CANCEL:
            break;
        }
        return SCRIPT_CONTINUE;
    }
}
