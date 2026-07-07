package script.theme_park.heroic.star_destroyer;

import script.*;
import script.library.create;
import script.library.trial;

public class hangar_access extends script.base_script
{
    public hangar_access()
    {
    }
    public static final int STATE_NONE = 0;
    public static final int STATE_ACTIVE = 1;
    public static final int STATE_DONE = 2;
    public static final int MENU_TAKE_COMMAND = 200;
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info item) throws InterruptedException
    {
        int state = getEventState(self);

        switch (state)
        {
            case STATE_NONE:
                item.addRootMenu(
                        menu_info_types.ITEM_USE,
                        new string_id("instance", "hangar_state_none"));
                break;

            case STATE_ACTIVE:
                item.addRootMenu(
                        menu_info_types.ITEM_USE,
                        new string_id("instance", "hangar_state_active"));
                break;

            case STATE_DONE:
                item.addRootMenu(
                        menu_info_types.ITEM_USE,
                        new string_id("instance", "hangar_state_complete"));
                break;
        }

        // Determine the Star Destroyer object
        obj_id currentCell = getContainedBy(self);
        obj_id starDestroyer = getContainedBy(currentCell);
        {
            item.addRootMenu(
                    MENU_TAKE_COMMAND,
                    new string_id("instance", "take_command"));
        }

        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == MENU_TAKE_COMMAND)
        {
            if (!hasSkill(player, "stardust_admiral_imperial") &&
                    !hasSkill(player, "stardust_admiral_republic"))
            {
                sendSystemMessage(player,
                        "Admiral command authority required.",
                        null);
                return SCRIPT_CONTINUE;
            }

            obj_id currentCell = getContainedBy(self);
            obj_id starDestroyer = getContainedBy(currentCell);

//             Record commander
            setObjVar(starDestroyer, "commanderId", player);
            setObjVar(starDestroyer, "commanderName", getName(player));

            // Ship already has a commander
            if (hasObjVar(starDestroyer, "commanderName"))
            {
                String commanderName = getStringObjVar(starDestroyer, "commanderName");

                sendSystemMessage(player,
                        "This ship is under the command of " + commanderName + ".",
                        null);
            }

            // Unlock progression
            fireTrigger(self, "remove_gates");
//            fireTrigger(self, "access_bridge");

            obj_id commandDeckCellObj = getCellId(starDestroyer, "commandeck");

            if (isIdValid(commandDeckCellObj))
            {
                location bridgeLoc = getLocation(player);

                bridgeLoc.cell = commandDeckCellObj;
                bridgeLoc.x = 0.29f;
                bridgeLoc.y = 453.6f;
                bridgeLoc.z = 323.63f;

                setLocation(player, bridgeLoc);

                // Spawn Chiss Navigator on command deck
                location navLoc = getLocation(player);

                navLoc.cell = commandDeckCellObj;
                navLoc.x = 0.26f;
                navLoc.y = 453.0f;
                navLoc.z = 335.0f;

                obj_id navigator = create.object(
                        "object/mobile/ep3/ep3_etyyy_chiss_poacher_smuggler_01.iff",
                        navLoc);

                if (isIdValid(navigator))
                {
                    attachScript(navigator,
                            "stardust.conversation.endor.chiss_navigator");
                }
            }

            sendSystemMessage(player,
                    "Admiral command authority accepted. All security bulkheads and lift controls have been unlocked.",
                    null);

            sendDirtyObjectMenuNotification(self);

            return SCRIPT_CONTINUE;
        }

        if (item != menu_info_types.ITEM_USE)
        {
            return SCRIPT_CONTINUE;
        }

        int state = getEventState(self);

        trial.bumpSession(self, "key");
        sendDirtyObjectMenuNotification(self);

        switch (state)
        {
            case STATE_NONE:
                messageTo(self,
                        "kickoff_event",
                        trial.getSessionDict(self, "key"),
                        1.0f,
                        false);
                break;

            case STATE_ACTIVE:
                break;

            case STATE_DONE:
                break;
        }

        return SCRIPT_CONTINUE;
    }
    public int getEventState(obj_id self) throws InterruptedException
    {
        return getIntObjVar(self, "event_state");
    }
    public boolean isInactive(obj_id self) throws InterruptedException
    {
        return getEventState(self) == STATE_NONE;
    }
    public boolean isActive(obj_id self) throws InterruptedException
    {
        return getEventState(self) == STATE_ACTIVE;
    }
    public boolean isComplete(obj_id self) throws InterruptedException
    {
        return getEventState(self) == STATE_DONE;
    }
    public int kickoff_event(obj_id self, dictionary params) throws InterruptedException
    {
        if (!trial.verifySession(self, params, "key"))
        {
            return SCRIPT_CONTINUE;
        }
        dictionary dict = trial.getSessionDict(trial.getTop(self));
        dict.put("triggerType", "triggerId");
        dict.put("triggerName", "spawn_krix");
        messageTo(trial.getTop(self), "triggerFired", dict, 0.0f, false);
        return SCRIPT_CONTINUE;
    }
    public void fireTrigger(obj_id self, String triggerName) throws InterruptedException
    {
        dictionary dict = trial.getSessionDict(trial.getTop(self));

        dict.put("triggerType", "triggerId");
        dict.put("triggerName", triggerName);

        messageTo(trial.getTop(self), "triggerFired", dict, 0.0f, false);
    }
}
