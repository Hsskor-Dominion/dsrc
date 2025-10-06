package script.systems.vehicle_system;

import script.*;
import script.library.*;
import script.library.vehicle;

import static script.library.vehicle.*;

public class vehicle_base extends script.base_script
{
    public vehicle_base()
    {
    }
    public static final String MENU_FILE = "pet/pet_menu";
    public static final String VCDPING_VEHICLE_SCRIPT_NAME = "systems.vehicle_system.vehicle_ping";
    public static final String MESSAGE_VEHICLE_ID = "vehicleId";
    public static final string_id SID_CITY_GARAGE_BANNED = new string_id("city/city", "garage_banned");
    public static final string_id SID_NO_GROUND_VEHICLE_IN_SPACE = new string_id("space/space_interaction", "no_ground_vehicle_in_space");
    public static final boolean debug = false;
    public static final int VEHICLE_DECAY_CYCLE = 60;
    public static void decayVehicle(obj_id vehicle) throws InterruptedException {
        if (!isIdValid(vehicle)) {
            return;
        }

        int now = getGameTime();
        float decay_rate = getVehicleDecayRate(vehicle);

        int decayAmt;
        if (utils.hasScriptVar(vehicle, "decay.stamp")) {
            int stamp = utils.getIntScriptVar(vehicle, "decay.stamp");
            int delta = now - stamp;
            float ratio = delta / VEHICLE_DECAY_CYCLE;
            decayAmt = Math.round(ratio * decay_rate);
        } else {
            decayAmt = Math.round(decay_rate / 2.0f);
        }

        decayAmt = Math.max(decayAmt, 1); // Set a minimum of X damage

        int currentHP = getHitpoints(vehicle);
        currentHP -= decayAmt;
        setHitpoints(vehicle, currentHP);
        obj_id vcd = callable.getCallableCD(vehicle);
        dictionary params = new dictionary();
        params.put("hp", currentHP);
        params.put("penalty", decayAmt);
        messageTo(vcd, "handleStoreVehicleDamage", params, 0.0f, false);
        utils.setScriptVar(vehicle, "decay.stamp", now + VEHICLE_DECAY_CYCLE); // Update decay stamp for the next cycle

        // Schedule the next decay cycle
        messageTo(vehicle, "handleVehicleDecay", null, VEHICLE_DECAY_CYCLE, false);
    }

    public int handleVehicleDecay(obj_id self, dictionary params) throws InterruptedException {
        decayVehicle(self); // Call the decay process
        return SCRIPT_CONTINUE;
    }
    public int revertVehicleMod(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null || !params.containsKey("type"))
        {
            return SCRIPT_CONTINUE;
        }
        int type = params.getInt("type");
        if (type == vehicle.MOD_TYPE_MAX_SPEED)
        {
            float oldSpeed = 0.0f;
            if (hasObjVar(self, vehicle.OBJVAR_MOD_MAX_SPEED_OLD))
            {
                oldSpeed = getFloatObjVar(self, vehicle.OBJVAR_MOD_MAX_SPEED_OLD);
                removeObjVar(self, vehicle.OBJVAR_MOD_MAX_SPEED_OLD);
            }
            if (hasObjVar(self, vehicle.OBJVAR_MOD_MAX_SPEED_DURATION))
            {
                removeObjVar(self, vehicle.OBJVAR_MOD_MAX_SPEED_DURATION);
            }
            if (oldSpeed > 0.0f)
            {
                setMaximumSpeed(self, oldSpeed);
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setAttributeAttained(self, attrib.VEHICLE);
        messageTo(self, "handleVehicleDecay", null, VEHICLE_DECAY_CYCLE, false);
        if (!hasScript(self, VCDPING_VEHICLE_SCRIPT_NAME))
        {
            if (debug)
            {
                LOG("vcdping-debug", "vehicle_base.OnInitialize(): attaching script [" + VCDPING_VEHICLE_SCRIPT_NAME + "] to vehicle id=[" + self + "]");
            }
            attachScript(self, VCDPING_VEHICLE_SCRIPT_NAME);
        }
        sendDestroyUnattendedVehicleSignal(self);
        return SCRIPT_CONTINUE;
    }
    public int checkForJetpack(obj_id self, dictionary params) throws InterruptedException
    {
        String creature_name = getTemplateName(self);
        obj_id player = params.getObjId("player");
        boolean storeJetpack = false;
        if (vehicle.isJetPackVehicle(self))
        {
            if (getMountsEnabled())
            {
                debugServerConsoleMsg(player, "+++ pet . onObjectMenuSelect +++ getMountsEneabled returnted TRUE");
                if (pet_lib.canMount(self, player))
                {
                    debugServerConsoleMsg(player, "+++ pet . onObjectMenuSelect +++ pet_lib.canMount(self,player) returned TRUE");
                    queueCommand(player, (-536363215), self, creature_name, COMMAND_PRIORITY_FRONT);
                    debugServerConsoleMsg(player, "+++ pet . onObjectMenuSelect +++ just attempted to Enqueue MOUNT command");
                }
                else 
                {
                    storeJetpack = true;
                }
            }
            else 
            {
                storeJetpack = true;
            }
        }
        if (storeJetpack)
        {
            string_id jetpackStoredMsg = new string_id("pet/pet_menu", "jetpack_stored");
            sendSystemMessage(player, jetpackStoredMsg);
            obj_id petControlDevice = callable.getCallableCD(self);
            vehicle.storeVehicle(petControlDevice, player);
        }
        return SCRIPT_CONTINUE;
    }
    public static String getVehicleReference(obj_id controlDevice) throws InterruptedException
    {
        if (!isIdValid(controlDevice))
        {
            return null;
        }
        return getStringObjVar(controlDevice, "vehicle_attribs.object_ref");
    }
    public static float getVehicleDecayRate(obj_id vehicle) throws InterruptedException {
        if (!isIdValid(vehicle)) {
            return -1.0f;
        }

        // Anti-decay mod override
        if (hasObjVar(vehicle, "vehicle_mod.decay_reduction")) {
            float reduction = getFloatObjVar(vehicle, "vehicle_mod.decay_reduction");
            if (reduction >= 1.0f) {
                return 0.0f; // fully negates decay
            }
        }

        obj_id controlDevice = callable.getCallableCD(vehicle);
        if (!isIdValid(controlDevice)) {
            return -1.0f;
        }

        String ref = getVehicleReference(controlDevice);
        if (ref == null || ref.equals("")) {
            return -1.0f;
        }

        float baseDecay = dataTableGetFloat(create.VEHICLE_TABLE, ref, "DECAY_RATE");

        // apply fractional reduction if present (e.g., 0.25f means 25% less decay)
        if (hasObjVar(vehicle, "vehicle_mod.decay_reduction")) {
            float reduction = getFloatObjVar(vehicle, "vehicle_mod.decay_reduction");
            baseDecay = baseDecay * (1.0f - reduction);
        }

        return Math.max(baseDecay, 0.0f);
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (isDead(self) || ai_lib.aiIsDead(player) || self == null || self == obj_id.NULL_ID || !isIdValid(self))
        {
            return SCRIPT_CONTINUE;
        }
        boolean isMountedOn = isMountedOnCreatureQueried(self, player);
        boolean isOwnedByPlayer = player == getMaster(self);
        sendDestroyUnattendedVehicleSignal(self);
        if (!isOwnedByPlayer && !isMountedOn)
        {
            LOG("special_sign", "!isOwnedByPlayer && !isMountedOn");
            if (!doesMountHaveRoom(self))
            {
                LOG("special_sign", "!doesMountHaveRoom(self)");
                return SCRIPT_CONTINUE;
            }
            else if (!vehicle.mountPermissionCheck(self, player, false))
            {
                LOG("special_sign", "vehicle.mountPermissionCheck ");
                return SCRIPT_CONTINUE;
            }
        }
        else 
        {
            LOG("special_sign", "isOwnedByPlayer || isMountedOn");
        }
        obj_id petControlDevice = callable.getCallableCD(self);
        String vehicle_name = getTemplateName(self);
        if (getMountsEnabled())
        {
            if (!vehicle.isJetPackVehicle(self))
            {
                if (pet_lib.canMount(self, player) || isMountedOn)
                {
                    mi.addRootMenu(menu_info_types.SERVER_VEHICLE_ENTER_EXIT, new string_id(MENU_FILE, "menu_enter_exit"));
                }
            }
        }
        if (!isOwnedByPlayer)
        {
            return SCRIPT_CONTINUE;
        }
        if (ai_lib.isInCombat(player) || pet_lib.wasInCombatRecently(self, player, false))
        {
            return SCRIPT_CONTINUE;
        }
        if (!hasObjVar(self, battlefield.VAR_CONSTRUCTED) && !ai_lib.isInCombat(player))
        {
            mi.addRootMenu(menu_info_types.PET_STORE, new string_id(MENU_FILE, "menu_store"));
        }
        if (ai_lib.isAiDead(self))
        {
            return SCRIPT_CONTINUE;
        }
        if (utils.hasScriptVar(self, "inRepairZone") && (!isDisabled(self) || (vehicle.canRepairDisabledVehicle(petControlDevice) && isDisabled(self))))
        {
            obj_id garage = utils.getObjIdScriptVar(self, "inRepairZone");
            if (isIdValid(garage) && exists(garage) && garage.isLoaded())
            {
                mi.addRootMenu(menu_info_types.SERVER_MENU1, new string_id(MENU_FILE, "menu_repair_vehicle"));
            }
        }
        if (isOwnedByPlayer && utils.hasScriptVar(self, "inRepairZone")) {
            int rootMenu = mi.addRootMenu(menu_info_types.SERVER_MENU2, new string_id("pet/pet_menu", "mod_vehicle"));
            mi.addSubMenu(rootMenu, menu_info_types.SERVER_MENU13, new string_id("pet/pet_menu", "mod_anti_decay"));
            mi.addSubMenu(rootMenu, menu_info_types.SERVER_MENU14, new string_id("pet/pet_menu", "mod_accelerant"));
            mi.addSubMenu(rootMenu, menu_info_types.SERVER_MENU15, new string_id("pet/pet_menu", "mod_shield"));
        }
        else if (isDisabled(self) && hasBarcRepairKit(player))
        {
            mi.addRootMenu(menu_info_types.SERVER_MENU1, new string_id("barc_repair", "refurbish_barc"));
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (isSpaceScene())
        {
            sendSystemMessage(player, SID_NO_GROUND_VEHICLE_IN_SPACE);
            return SCRIPT_CONTINUE;
        }
        if (ai_lib.aiIsDead(player))
        {
            return SCRIPT_CONTINUE;
        }
        if (ai_lib.isInCombat(player) || pet_lib.wasInCombatRecently(self, player, false))
        {
            return SCRIPT_CONTINUE;
        }
        if (item != menu_info_types.SERVER_VEHICLE_ENTER_EXIT && pet_lib.isPet(self) && pet_lib.hasMaster(self) && player != getMaster(self))
        {
            return SCRIPT_CONTINUE;
        }
        if (ai_lib.isAiDead(player))
        {
            return SCRIPT_CONTINUE;
        }
        String creature_name = getName(self);
        obj_id petControlDevice = callable.getCallableCD(self);
        if (item == menu_info_types.PET_STORE)
        {
            if (!hasObjVar(self, battlefield.VAR_CONSTRUCTED))
            {
                vehicle.storeVehicle(petControlDevice, player);
            }
            return SCRIPT_CONTINUE;
        }
        else if (item == menu_info_types.SERVER_VEHICLE_ENTER_EXIT)
        {
            debugServerConsoleMsg(player, "+++ pet . onObjectMenuSelect +++ SERVER_VEHICLE_ENTER_EXIT menu object selected");
            if (getMountsEnabled())
            {
                debugServerConsoleMsg(player, "+++ pet . onObjectMenuSelect +++ getMountsEneabled returnted TRUE");
                if (isMountedOnCreatureQueried(self, player))
                {
                    queueCommand(player, (117012717), self, creature_name, COMMAND_PRIORITY_FRONT);
                }
                else if (pet_lib.canMount(self, player))
                {
                    debugServerConsoleMsg(player, "+++ pet . onObjectMenuSelect +++ pet_lib.canMount(self,player) returned TRUE");
                    queueCommand(player, (-536363215), self, creature_name, COMMAND_PRIORITY_FRONT);
                    debugServerConsoleMsg(player, "+++ pet . onObjectMenuSelect +++ just attempted to Enqueue MOUNT command");
                }
            }
            else 
            {
                debugServerConsoleMsg(player, "+++ pet . onObjectMenuSelect +++ getMountsEneabled returnted FALSE");
            }
        }
        else if (item == menu_info_types.SERVER_MENU1)
        {
            if (ai_lib.isInCombat(player))
            {
                return SCRIPT_CONTINUE;
            }
            if (utils.hasScriptVar(self, "inRepairZone") && (!isDisabled(self) || (vehicle.canRepairDisabledVehicle(petControlDevice) && isDisabled(self))))
            {
                int city_id = getCityAtLocation(getLocation(self), 0);
                if ((city_id > 0) && city.isCityBanned(player, city_id))
                {
                    sendSystemMessage(player, SID_CITY_GARAGE_BANNED);
                    return SCRIPT_CONTINUE;
                }
                vehicle.repairVehicle(player, self);
                sendDirtyObjectMenuNotification(self);
            }
            if (utils.hasScriptVar(self, "inRepairZone") && (!isDisabled(self) || (vehicle.canRepairDisabledVehicle(petControlDevice) && isDisabled(self))))
            {
                int city_id = getCityAtLocation(getLocation(self), 0);
                if ((city_id > 0) && city.isCityBanned(player, city_id))
                {
                    sendSystemMessage(player, SID_CITY_GARAGE_BANNED);
                    return SCRIPT_CONTINUE;
                }
                vehicle.repairVehicle(player, self);
                sendDirtyObjectMenuNotification(self);//this is where I need to add "mod"
            }
            else if (isDisabled(self) && hasBarcRepairKit(player))
            {
                vehicle.restoreVehicle(player, self);
                sendDirtyObjectMenuNotification(self);
            }
        }
        else if (item == menu_info_types.SERVER_MENU13) // Anti-Decay
        {
            if (hasBarcRepairKit(player)) {
                vehicle.restoreVehicle(player, self);

                // Apply anti-decay modifier
                setObjVar(self, "vehicle_mod.decay_reduction", true);

                sendSystemMessage(player, new string_id("vehicle_mod", "applied_antidecay"));
            }
            else {
                sendSystemMessage(player, new string_id("vehicle_mod", "missing_restoration_kit"));
            }
        }
        else if (item == menu_info_types.SERVER_MENU14) // Accelerant
        {
            if (utils.playerHasItemByTemplate(player, "object/tangible/food/spice/spice_thruster_head.iff")
                    || utils.playerHasItemByTemplate(player, "object/tangible/loot/npc_loot/spice_thruster_head_generic.iff"))
            {
                boostVehicle(player, self);
                removeObjVar(self, "vehicle_mod.decay_reduction");
                sendSystemMessage(player, new string_id("vehicle_mod", "applied_temporary_accelerant"));
            }
            else
            {
                sendSystemMessage(player, new string_id("vehicle_mod", "missing_rhydonium_spice"));
            }
        }
        else if (item == menu_info_types.SERVER_MENU15) // Shield
        {
            // Get the utility belt object in the slot
            obj_id utilityBelt = getObjectInSlot(player, "utility_belt");

            // Validate the belt
            if (!isIdValid(utilityBelt) || getGameObjectType(utilityBelt) != GOT_armor_psg) {
                // Send failure message to the player
                prose_package pp = new prose_package();
                pp = prose.setStringId(pp, new string_id("spam", "psg_belt_required"));
                sendSystemMessageProse(player, pp);
            }
            else {
                shieldVehicle(player, self);
                buff.applyBuff(self, "vehicle_5");
                //buff.applyBuff(player, "vehicle_at_rt");//former model applied buff to rider, this shakes it up a bit
                sendSystemMessage(player, new string_id("vehicle_mod", "installed_temporary_shield"));
            }
        }
        return SCRIPT_CONTINUE;
    }
    public static void boostVehicle(obj_id player, obj_id self) throws InterruptedException {
        obj_id spice = utils.getItemPlayerHasByTemplate(player, "object/tangible/food/spice/spice_thruster_head.iff");
        if (isIdValid(spice)) {
            destroyObject(spice);
        }
        obj_id spice2 = utils.getItemPlayerHasByTemplate(player, "object/tangible/loot/npc_loot/spice_thruster_head_generic.iff");
        if (isIdValid(spice2)) {
            destroyObject(spice2);
        }
        setMaximumSpeed(self, 73.2F);
        int currentHP = getHitpoints(self);
        currentHP -= 100;
        setHitpoints(self, currentHP);
    }

    public void shieldVehicle(obj_id player, obj_id self) throws InterruptedException {
        // Get the utility belt object in the slot
        obj_id utilityBelt = getObjectInSlot(player, "utility_belt");

        // Validate the belt
        if (!isIdValid(utilityBelt) || getGameObjectType(utilityBelt) != GOT_armor_psg) {
            // Send failure message to the player
            prose_package pp = new prose_package();
            pp = prose.setStringId(pp, new string_id("spam", "psg_belt_required"));
            sendSystemMessageProse(player, pp);
            return; // stop execution if no valid PSG
        }

        // Damage the PSG and vehicle when used
        damageItem(utilityBelt, 50);

        int currentHP = getHitpoints(self);
        currentHP -= 100;
        setHitpoints(self, currentHP);

        // Success message
        prose_package pp = new prose_package();
        pp = prose.setStringId(pp, new string_id("spam", "psg_activated_with_decay"));
        sendSystemMessageProse(player, pp);
    }

    private void damageItem(obj_id item, int amount) throws InterruptedException {
        int curHp = getHitpoints(item);
        int newHp = curHp - amount;

        if (newHp <= 0) {
            // Item is destroyed
            destroyObject(item);
        } else {
            setHitpoints(item, newHp);
        }
    }

    public boolean isMountedOnCreatureQueried(obj_id pet, obj_id player) throws InterruptedException
    {
        if (!isIdValid(pet))
        {
            return false;
        }
        if (!isIdValid(player))
        {
            return false;
        }
        obj_id playerCurrentMount = getMountId(player);
        if (!isIdValid(playerCurrentMount))
        {
            return false;
        }
        if (playerCurrentMount != pet)
        {
            return false;
        }
        return true;
    }
    public boolean canTrainAsMount(obj_id pet, obj_id player) throws InterruptedException
    {
        if (!isIdValid(pet))
        {
            debugServerConsoleMsg(player, "+++ PET_LIB . canTrainAsMount +++ isIdValid failed for (pet)");
            return false;
        }
        if (!isIdValid(player))
        {
            debugServerConsoleMsg(player, "+++ PET_LIB . canTrainAsMount +++ isIdValid failed for (player)");
            return false;
        }
        if (!(couldPetBeMadeMountable(pet) == MSC_CREATURE_MOUNTABLE))
        {
            debugServerConsoleMsg(player, "+++ PET_LIB . canTrainAsMount +++ couldPetBeMadeMountable (pet) returned something other than MSC_CREATURE_MOUNTABLE ");
            debugServerConsoleMsg(player, "+++ PET_LIB . canTrainAsMount +++ couldPetBeMadeMountable (pet) returned " + couldPetBeMadeMountable(pet));
            return false;
        }
        return true;
    }
    public boolean trainMount(obj_id pet, obj_id player) throws InterruptedException
    {
        if (!isIdValid(pet) || !isIdValid(player))
        {
            return false;
        }
        if (!canTrainAsMount(pet, player))
        {
            return false;
        }
        if (!makePetAMount(pet, player))
        {
            debugServerConsoleMsg(player, "+++ VEHICLE . onAttach +++ makePetAMount(self,player) returned FALSE");
            return false;
        }
        else 
        {
            debugServerConsoleMsg(player, "+++ VEHICLE . onAttach +++ makePetAMount(self,player) returned TRUE. YEAH!");
        }
        return true;
    }
    public boolean makePetAMount(obj_id pet, obj_id player) throws InterruptedException
    {
        if (!isIdValid(pet) || !isIdValid(player))
        {
            return false;
        }
        obj_id petControlDevice = callable.getCallableCD(pet);
        if (!(couldPetBeMadeMountable(pet) == 0))
        {
            debugServerConsoleMsg(player, "+++ VEHICLE . onAttach +++ couldPetBeMadeMountable(pet) returned FALSE.");
            return false;
        }
        if (!makePetMountable(pet))
        {
            debugServerConsoleMsg(player, "+++ VEHICLE . onAttach +++ makePetMountable(pet) returned FALSE.");
            return false;
        }
        else 
        {
            setObjVar(petControlDevice, "ai.pet.trainedMount", 1);
        }
        return true;
    }
    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        int idx = utils.getValidAttributeIndex(names);
        if (idx == -1)
        {
            return SCRIPT_CONTINUE;
        }
        if (hasObjVar(self, "ai.pet.masterName"))
        {
            names[idx] = "owner";
            attribs[idx] = getStringObjVar(self, "ai.pet.masterName");
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectDisabled(obj_id self, obj_id killer) throws InterruptedException
    {
        obj_id owner = getMaster(self);
        if (isIdValid(owner))
        {
            obj_id vcd = callable.getCallableCD(self);
            if (isIdValid(vcd))
            {
                String killerDesc = null;
                if (isIdValid(killer))
                {
                    String killerName = getPlayerName(killer);
                    if (killerName != null && killerName.length() > 0)
                    {
                        killerDesc = "player " + killer + "(" + killerName + ")";
                    }
                    else 
                    {
                        killerDesc = "npc " + killer + "(" + getName(killer) + ")";
                    }
                }
                else if (getIntObjVar(vcd, "attrib.hit_points") > 0)
                {
                    killerDesc = "decay";
                }
                if (killerDesc != null)
                {
                    CustomerServiceLog("vehicle", "vehicle template:" + getTemplateName(self) + " vcd:" + vcd + " owner:" + owner + "(" + getName(owner) + ") disabled by " + killerDesc);
                }
            }
            sendSystemMessage(owner, pet_lib.SID_SYS_VEHICLE_DISABLED);
            removeObjVar(self, "vehicle_mod.decay_reduction");
            LOG("vehicle_base", "It is destroyed");
            obj_id rider = getRiderId(self);
            if (isIdValid(rider))
            {
                utils.dismountRiderJetpackCheck(rider);
            }
            if (ai_lib.isInCombat(self))
            {
                vehicle.storeVehicle(vcd, rider, false);
            }
            else 
            {
                messageTo(self, "handleDisabledPackRequest", null, 120, false);
            }
            int vehicleBuff = buff.getBuffOnTargetFromGroup(rider, "vehicle");
            if (vehicleBuff != 0)
            {
                buff.removeBuff(rider, vehicleBuff);
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnDestroy(obj_id self) throws InterruptedException
    {
        if (hasObjVar(self, "pet.controlDestroyed"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id master = getMaster(self);
        boolean isFactionPet = (ai_lib.isNpc(self) || ai_lib.aiGetNiche(self) == NICHE_VEHICLE || ai_lib.isAndroid(self));
        obj_id petControlDevice = callable.getCallableCD(self);
        if (hasObjVar(self, battlefield.VAR_CONSTRUCTED) || (isFactionPet && ai_lib.aiIsDead(self)))
        {
            if (isIdValid(petControlDevice))
            {
                messageTo(petControlDevice, "handleFlagDeadCreature", null, 0, false);
            }
        }
        if (isIdValid(petControlDevice))
        {
            obj_id currentPet = callable.getCDCallable(petControlDevice);
            if (isIdValid(currentPet) && currentPet == self)
            {
                pet_lib.savePetInfo(self, petControlDevice);
                setObjVar(petControlDevice, "pet.timeStored", getGameTime());
                callable.setCDCallable(petControlDevice, null);
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int handleDisabledPackRequest(obj_id self, dictionary params) throws InterruptedException
    {
        if (isDisabled(self))
        {
            messageTo(self, "handlePackRequest", null, 1, false);
        }
        return SCRIPT_CONTINUE;
    }
    public int handlePackRequest(obj_id self, dictionary params) throws InterruptedException
    {
        debugServerConsoleMsg(null, "+++ vehicle_base.messageHandler handlePackRequest +++ entered HANDLEPACKREQUEST message handler");
        obj_id rider = getRiderId(self);
        if (isIdValid(rider))
        {
            boolean dismountSuccess = pet_lib.doDismountNow(rider);
            if (!dismountSuccess)
            {
                LOG("mounts-bug", "vehicle_base.messageHandler handlePackRequest(): creature [" + self + "], rider [" + rider + "] failed to dismount, aborting pack request.  This mount/vehicle probably is in an invalid state now.");
                return SCRIPT_CONTINUE;
            }
        }
        debugServerConsoleMsg(null, "+++ vehicle_base.messageHandler handlePackRequest +++ destroying the vehicle now");
        obj_id vehicleControlDevice = callable.getCallableCD(self);
        vehicle.saveVehicleInfo(vehicleControlDevice, self);
        utils.setScriptVar(self, "stored", true);
        dictionary messageData = new dictionary();
        messageData.put(MESSAGE_VEHICLE_ID, self);
        sendDirtyObjectMenuNotification(vehicleControlDevice);
        if (destroyObject(self))
        {
            messageTo(vehicleControlDevice, "handleRemoveCurrentVehicle", messageData, 1, false);
        }
        else 
        {
            debugServerConsoleMsg(null, "+++ vehicle_base.messageHandler handlePackRequest +++ WARNINGWARNING - FAILED TO DESTROY SELF");
        }
        return SCRIPT_CONTINUE;
    }
    public int destroyNow(obj_id self, dictionary params) throws InterruptedException
    {
        setObjVar(self, "pet.controlDestroyed", true);
        CustomerServiceLog("vehicle_bug", "vehicle_base - destroyNow::Recieved signal to destroy with ID: " + params.getInt("signalId"));
        destroyObject(self);
        return SCRIPT_OVERRIDE;
    }
    public int OnCreatureDamaged(obj_id self, obj_id attacker, obj_id weapon, int[] damage) throws InterruptedException
    {
        utils.setScriptVar(self, "pet.combatEnded", getGameTime());
        return SCRIPT_CONTINUE;
    }
    public int OnObjectDamaged(obj_id self, obj_id attacker, obj_id weapon, int damage) throws InterruptedException
    {
        utils.setScriptVar(self, "pet.combatEnded", getGameTime());
        return SCRIPT_CONTINUE;
    }
    public int handleSetColors(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null || params.isEmpty())
        {
            return SCRIPT_CONTINUE;
        }
        setColors(self, params);
        return SCRIPT_CONTINUE;
    }
    public int handleSetCustomization(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null || params.isEmpty())
        {
            return SCRIPT_CONTINUE;
        }
        dictionary dc = params.getDictionary("dc");
        boolean updatedColors = setColors(self, dc);
        params.remove("dc");
        obj_id tool = params.getObjId("tool");
        if (!isIdValid(tool))
        {
            return SCRIPT_CONTINUE;
        }
        if (updatedColors)
        {
            messageTo(tool, "customizationSuccess", params, 0.0f, false);
        }
        else 
        {
            messageTo(tool, "customizationFailed", params, 0.0f, false);
        }
        return SCRIPT_CONTINUE;
    }
    public boolean setColors(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null || params.isEmpty())
        {
            return false;
        }
        boolean litmus = true;
        java.util.Enumeration keys = params.keys();
        while (keys.hasMoreElements())
        {
            String var = (String)keys.nextElement();
            int idx = params.getInt(var);
            litmus &= hue.setColor(self, var, idx);
        }
        return litmus;
    }
    public boolean hasBarcRepairKit(obj_id player) throws InterruptedException
    {
        obj_id tool = utils.getItemPlayerHasByTemplate(player, "object/tangible/item/ep3/barc_repair_tool.iff");
        return (isIdValid(tool));
    }
    public void sendDestroyUnattendedVehicleSignal(obj_id vehicle) throws InterruptedException
    {
        trial.bumpSession(vehicle);
        messageTo(vehicle, "handleDestroyUnattended", trial.getSessionDict(vehicle), 600, false);
    }
    public int handleDestroyUnattended(obj_id self, dictionary params) throws InterruptedException
    {
        if (!trial.verifySession(self, params))
        {
            return SCRIPT_CONTINUE;
        }
        if (vehicle.isBattlefieldVehicle(self))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id rider = getRiderId(self);
        if (isIdValid(rider))
        {
            sendDestroyUnattendedVehicleSignal(self);
            return SCRIPT_CONTINUE;
        }
        obj_id vcd = callable.getCallableCD(self);
        if (!isIdValid(vcd))
        {
            params.put("signalId", 0);
            messageTo(self, "destroyNow", params, 0, false);
            return SCRIPT_CONTINUE;
        }
        obj_id currentVehicle = callable.getCDCallable(vcd);
        if (!isIdValid(currentVehicle) || currentVehicle != self)
        {
            params.put("signalId", 1);
            messageTo(self, "destroyNow", params, 0, false);
            CustomerServiceLog("vehicle_bug", "vehicle_base - handleDestroyUnattended::Validating current vehicle(" + currentVehicle + ") vs self(" + self + "): " + params.getInt("signalId"));
            return SCRIPT_CONTINUE;
        }
        obj_id player = utils.getContainingPlayer(vcd);
        vehicle.storeVehicle(vcd, player);
        return SCRIPT_CONTINUE;
    }
}
