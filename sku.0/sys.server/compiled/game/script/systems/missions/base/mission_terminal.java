package script.systems.missions.base;

import script.*;
import script.library.*;

import static script.library.factions.isSmuggler;

public class mission_terminal extends script.base_script
{
    public mission_terminal()
    {
    }
    public static final string_id SID_MNU_REDEEM = new string_id("sui", "mnu_redeem");
    public static final string_id SID_REDEEM_PROMPT = new string_id("sui", "redeem_data_item_prompt");
    public static final string_id SID_REDEEM_TITLE = new string_id("sui", "redeem_data_item_title");
    public static final string_id SID_SLICE = new string_id("slicing/slicing", "slice");
    public static final string_id SID_FAIL_SLICE = new string_id("slicing/slicing", "terminal_fail");
    public static final string_id SID_SUCCESS_SLICE = new string_id("slicing/slicing", "terminal_success");
    public static final string_id SID_NOT_YET = new string_id("slicing/slicing", "not_yet");
    public static final string_id SID_PLANETARY_BOUNTY = new string_id("city/city", "planetary_bounty");
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if (hasObjVar(self, structure.VAR_TERMINAL_HEADING))
        {
            setYaw(self, getFloatObjVar(self, structure.VAR_TERMINAL_HEADING));
        }
        removeObjVar(self, "slice_start");
        removeObjVar(self, "sliced_by");
        attachScript(self, "planet_map.map_loc_attach");
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        int mnu = -1;
        if (hasSkill(player, "class_smuggler_phase1_novice"))
        {
            mi.addRootMenu(menu_info_types.SERVER_MENU2, SID_SLICE);
        }
        menu_info_data mid = mi.getMenuItemByType(menu_info_types.MISSION_TERMINAL_LIST);
        if (mid == null)
        {
            mid = mi.getMenuItemByType(menu_info_types.ITEM_USE);
            if (mid == null && (!isDead(player) && !isIncapacitated(player)))
            {
                mnu = mi.addRootMenu(menu_info_types.MISSION_TERMINAL_LIST, new string_id("", ""));
            }
        }
        String terminalName = getTemplateName(self);
        if (terminalName.toLowerCase().contains("bounty"))
        {
            mi.addRootMenu(menu_info_types.SERVER_MENU8, SID_PLANETARY_BOUNTY);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.SERVER_MENU1)
        {
        }
        else if (item == menu_info_types.SERVER_MENU2)
        {
            if (!isSmuggler(player))
            {
                return SCRIPT_CONTINUE;
            }
            if (utils.hasScriptVar(player, "slicing.terminal_time"))
            {
                int lastHackTime = utils.getIntScriptVar(player, "slicing.terminal_time");
                int curTime = getGameTime();
                if (curTime < lastHackTime + 120)
                {
                    int timeDiff = (lastHackTime + 120) - curTime;
                    prose_package pp = prose.getPackage(SID_NOT_YET, timeDiff);
                    sendSystemMessageProse(player, pp);
                    return SCRIPT_CONTINUE;
                }
                else
                {
                    utils.removeScriptVar(player, "slicing.terminal_time");
                }
            }
            slicing.startSlicing(player, self, "finishSlicing", "terminal");
        }
        else if (item == menu_info_types.SERVER_MENU8)
        {
            handlePlanetaryBounty(self, player);
        }
        return SCRIPT_CONTINUE;
    }
    public int finishSlicing(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null)
        {
            return SCRIPT_CONTINUE;
        }
        int success = params.getInt("success");
        obj_id player = params.getObjId("player");
        if (success == 1)
        {
            sendSystemMessage(player, SID_SUCCESS_SLICE);
            utils.setScriptVar(player, "slicing.terminal", self);
            utils.setScriptVar(player, "slicing.terminal_bonus", 1.5f);
            xp.grant(player, "slicing", 100);
        }
        else 
        {
            sendSystemMessage(player, SID_FAIL_SLICE);
        }
        utils.setScriptVar(player, "slicing.terminal_time", getGameTime());
        return SCRIPT_CONTINUE;
    }
    // ===========================
    // PLANETARY BOUNTY INTEGRATION
    // ===========================
    public boolean townspersonEnemy(obj_id player, obj_id self) throws InterruptedException
    {
        float townspersonFaction = factions.getFactionStanding(player, "townsperson");
        return townspersonFaction <= -1000;
    }
    public void townspersonBounty(obj_id player, obj_id self) throws InterruptedException
    {
        money.requestPayment(player, self, smuggler.TIER_5_GENERIC_FRONT_COST, "none", null, true);
        int mission_bounty = 5000;
        int current_bounty = 0;
        mission_bounty += rand(1, 2000);
        if (hasObjVar(player, "bounty.amount"))
        {
            current_bounty = getIntObjVar(player, "bounty.amount");
        }
        current_bounty += mission_bounty;
        setObjVar(player, "bounty.amount", current_bounty);
        setObjVar(player, "smuggler.bounty", mission_bounty);
        setJediBountyValue(player, current_bounty);
        updateJediScriptData(player, "smuggler", 1);
    }
    public boolean hasDeclaredResidencyOnPlanet(obj_id player, String planetName) throws InterruptedException
    {
        String residencyPlanet = getStringObjVar(player, "residency_planet");
        return (residencyPlanet != null && residencyPlanet.equals(planetName));
    }

    public void handlePlanetaryBounty(obj_id self, obj_id player) throws InterruptedException
    {
        String planetName = getCurrentSceneName();
        boolean isResident = hasDeclaredResidencyOnPlanet(player, planetName);
        boolean isEnemy = townspersonEnemy(player, self);

        if (isEnemy)
        {
            sendSystemMessage(player, new string_id("city/city", "planetary_diplomacy_poor_townsperson_faction_bounty_fee"));
            townspersonBounty(player, self);
            return;
        }

        if (!isResident)
        {
            sendSystemMessage(player, new string_id("city/city", "planetary_bounty_signal_not_qualified"));
            return;
        }

        sendSystemMessage(player, new string_id("city/city", "planetary_bounty_signal_start_pvp_risk"));
        factions.goOvertWithDelay(player, 0.0f);

        obj_id[] allPlayers = getAllPlayers(getLocation(player), 10000.0f);
        int count = 0;
        for (obj_id nearby : allPlayers)
        {
            if (isPlayer(nearby) && nearby != player)
                count++;
        }
        if (count == 0)
        {
            sendSystemMessage(player, new string_id("city/city", "planetary_bounty_no_players_found"));
            return;
        }

        obj_id[] nearbyPlayers = new obj_id[count];
        String[] playerNames = new String[count];
        int idx = 0;
        for (obj_id nearby : allPlayers)
        {
            if (isPlayer(nearby) && nearby != player)
            {
                nearbyPlayers[idx] = nearby;
                playerNames[idx] = getName(nearby);
                idx++;
            }
        }

        utils.setScriptVar(player, "planet_bounty.list", nearbyPlayers);

        sui.listbox(
                self,
                player,
                "@city/city:select_bounty_target",
                sui.OK_CANCEL,
                "@city/city:set_city_bounty",
                playerNames,
                "handlePlanetaryBountySelection",
                true
        );
    }

    public int handlePlanetaryBountySelection(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id player = sui.getPlayerId(params);
        if (!isIdValid(player))
            return SCRIPT_CONTINUE;

        int selected = sui.getListboxSelectedRow(params);
        if (selected < 0)
        {
            sendSystemMessage(player, new string_id("city/city", "city_bounty_cancelled"));
            return SCRIPT_CONTINUE;
        }

        obj_id[] nearbyPlayers = utils.getObjIdArrayScriptVar(player, "planet_bounty.list");
        if (nearbyPlayers == null || selected >= nearbyPlayers.length)
        {
            sendSystemMessage(player, new string_id("city/city", "city_bounty_invalid_selection"));
            return SCRIPT_CONTINUE;
        }

        obj_id target = nearbyPlayers[selected];
        if (target == player)
        {
            sendSystemMessage(player, new string_id("city/city", "city_bounty_cannot_target_self"));
            return SCRIPT_CONTINUE;
        }

        bounty_hunter.showSetBountySUI(player, target);
        utils.removeScriptVar(player, "planet_bounty.list");

        return SCRIPT_CONTINUE;
    }
}
