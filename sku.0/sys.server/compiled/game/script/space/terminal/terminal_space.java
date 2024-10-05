package script.space.terminal;

import script.*;
import script.library.*;
import java.util.Arrays;
import java.util.Vector;

import static script.library.gcw.*;
import static script.library.space_transition.launch;

public class terminal_space extends script.terminal.base.base_terminal
{
    public terminal_space()
    {
    }
    public static final float TERMINAL_USE_DISTANCE = 8.0f;
    public static final string_id SID_LAUNCH_SHIP = new string_id("space/space_terminal", "launch_ship");
    public static final string_id SID_MUSTAFAR = new string_id("space/space_terminal", "mustafar_exception");
    public static final string_id SID_NOT_IN_COMBAT = new string_id("travel", "not_in_combat");
    public static final string_id SID_PVP_NOW_OVERT2 = new string_id("space/space_interaction", "pvp_now_overt2");
    public static final int MT_TOTAL = 2;
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        requestPreloadCompleteTrigger(self);
        return SCRIPT_CONTINUE;
    }
    public int OnPreloadComplete(obj_id self) throws InterruptedException
    {
        String strFileName = "datatables/space_zones/launch_locations.iff";
        String strName = "mos_eisley";
        location locTest = getLocation(self);
        region[] rgnCities = getRegionsWithGeographicalAtPoint(locTest, regions.GEO_CITY);
        dictionary dctTeleportInfo = null;
        if (rgnCities == null || rgnCities.length == 0)
        {
            setName(self, "BUSTED TERMINAL! PUT ME IN A CITY!@!@!@!@");
            return SCRIPT_CONTINUE;
        }
        else 
        {
            for (region rgnTest : rgnCities) {
                strName = rgnTest.getName();
                if (strName.startsWith("@")) {
                    string_id strTest = utils.unpackString(strName);
                    strName = strTest.getAsciiId();
                    dctTeleportInfo = dataTableGetRow(strFileName, strName);
                    if (dctTeleportInfo != null) {
                        break;
                    }
                }
            }
            if (dctTeleportInfo == null)
            {
                setName(self, "NO ENTRY FOR " + strName + " in teleport datatable. Busted terminal");
                return SCRIPT_CONTINUE;
            }
        }
        utils.setScriptVar(self, "space.loc.space", new location(dctTeleportInfo.getFloat("spaceX"), dctTeleportInfo.getFloat("spaceY"), dctTeleportInfo.getFloat("spaceZ"), dctTeleportInfo.getString("spaceScene")));
        utils.setScriptVar(self, "space.locationName", "w" + dctTeleportInfo.getInt("spaceLocationIndex"));
        utils.setScriptVar(self, "space.loc.ground", new location(dctTeleportInfo.getFloat("groundX"), dctTeleportInfo.getFloat("groundY"), dctTeleportInfo.getFloat("groundZ"), dctTeleportInfo.getString("groundScene")));
        utils.setScriptVar(self, "ground.locationName", dctTeleportInfo.getString("pointName"));
        return SCRIPT_CONTINUE;
    }
    public int OnAboutToLaunchIntoSpace(obj_id self, obj_id player, obj_id shipControlDevice, obj_id[] membersApprovedByShipOwner, String destinationGroundPlanet, String destinationGroundTravelPoint) throws InterruptedException
    {
        LOG("space", "triggered OnAboutToLaunchIntoSpace");
        if (!doSpacePrecheck(player))
        {
            return SCRIPT_CONTINUE;
        }
        if (!features.isSpaceEdition(player))
        {
            LOG("space", "NO EXPANSION");
            string_id strSpam = new string_id("space/space_interaction", "no_space_expansion");
            sendSystemMessage(player, strSpam);
            if (!isGod(player))
            {
                LOG("space", "NO EXPANSION - RETURNING");
                return SCRIPT_CONTINUE;
            }
        }
        if (ai_lib.isInCombat(player))
        {
            sendSystemMessage(player, SID_NOT_IN_COMBAT);
            return SCRIPT_CONTINUE;
        }
        boolean isStarportToStarportLaunch = destinationGroundPlanet != null && !destinationGroundPlanet.equals("");
        if (travel.isTravelBlocked(player, !isStarportToStarportLaunch))
        {
            return SCRIPT_CONTINUE;
        }
        if (getState(self, STATE_GLOWING_JEDI) != 0)
        {
            setState(self, STATE_GLOWING_JEDI, false);
        }
        location warpLocation = utils.getLocationScriptVar(self, "space.loc.space");
        if (warpLocation == null)
        {
            LOG("space", "OnAboutToLaunchIntoSpace warpLocation space.loc.space was null on terminal");
        }
        else 
        {
            LOG("space", "getting scd and stuff");
            LOG("space", "scd is " + shipControlDevice);
            if (isIdValid(shipControlDevice))
            {
                obj_id ship = space_transition.getShipFromShipControlDevice(shipControlDevice);
                blog("component_fix", "TERMINAL - WORKING");
                boolean success = space_crafting.checkForCollectionReactor(shipControlDevice, ship);
                if (success)
                {
                    CustomerServiceLog("ShipComponents", "Collection reactor found and handled for: (" + self + ") " + getName(self));
                    return SCRIPT_CONTINUE;
                }
                space_crafting.setCollectionReactorChecked(shipControlDevice);
                if (!space_utils.isShipUsable(ship, player))
                {
                    return SCRIPT_CONTINUE;
                }
                float currentShipMass = getChassisComponentMassCurrent(ship);
                float maxAllowableShipMass = getChassisComponentMassMaximum(ship);
                if ((currentShipMass > maxAllowableShipMass) && !isGod(player))
                {
                    string_id strSpam = new string_id("space/space_interaction", "too_heavy");
                    sendSystemMessage(player, strSpam);
                    return SCRIPT_CONTINUE;
                }
                if (space_utils.isShipWithInterior(ship))
                {
                    obj_id[] objControlDevices = space_transition.findShipControlDevicesForPlayer(player, true);
                    int intCount = 0;
                    for (obj_id objControlDevice : objControlDevices) {
                        obj_id objTestShip = space_transition.getShipFromShipControlDevice(objControlDevice);
                        if (space_utils.isShipWithInterior(objTestShip)) {
                            if (objTestShip != ship) {
                                int intItemCount = player_structure.getStructureNumItems(objTestShip);
                                if (intItemCount > 0) {
                                    intCount = intCount + 1;
                                    if (intCount >= 2) {
                                        string_id strSpam = new string_id("space/space_interaction", "too_many_pobs");
                                        sendSystemMessage(player, strSpam);
                                        return SCRIPT_CONTINUE;
                                    }
                                }
                            }
                        }
                    }
                }
                if (isIdValid(ship))
                {
                    utils.setLocalVar(player, "objControlDevice", shipControlDevice);
                    callable.storeCallables(player);
                    vehicle.storeAllVehicles(player);
                    if (utils.hasScriptVar(player, "currentHolo"))
                    {
                        performance.holographicCleanup(player);
                    }
                    if (isStarportToStarportLaunch)
                    {
                        doStarportToStarportLaunch(player, ship, membersApprovedByShipOwner, destinationGroundPlanet, destinationGroundTravelPoint);
                    }
                    else 
                    {
                        LOG("space", "location is " + warpLocation);
                        location locDestination = space_utils.getRandomLocationInSphere(warpLocation, 150, 300);
                        LOG("space", "launching to " + locDestination);
                        location locFinalDestination = getFinalHyperspaceLocation(player, locDestination);
                        if (locFinalDestination == null)
                        {
                            string_id tooFull = new string_id("shared_hyperspace", "zone_too_full_use_travel");
                            sendSystemMessage(player, tooFull);
                            return SCRIPT_CONTINUE;
                        }
                        utils.setScriptVar(player, "strLaunchPointName", utils.getStringScriptVar(self, "space.locationName"));
                        location groundLoc = utils.getLocationScriptVar(self, "space.loc.ground");
                        session.logActivity(player, session.ACTIVITY_SPACE_LAUNCH);
                        if (hasScript(player, performance.DANCE_HEARTBEAT_SCRIPT))
                        {
                            performance.stopDance(player);
                        }
                        if (hasScript(player, performance.MUSIC_HEARTBEAT_SCRIPT))
                        {
                            performance.stopMusic(player);
                        }
                        if (membersApprovedByShipOwner != null && membersApprovedByShipOwner.length > 0)
                        {
                            for (obj_id obj_id : membersApprovedByShipOwner) {
                                if (hasScript(obj_id, performance.DANCE_HEARTBEAT_SCRIPT)) {
                                    performance.stopDance(obj_id);
                                }
                                if (hasScript(obj_id, performance.MUSIC_HEARTBEAT_SCRIPT)) {
                                    performance.stopMusic(obj_id);
                                }
                            }
                        }
                        launch(player, ship, membersApprovedByShipOwner, locFinalDestination, groundLoc);
                        removeObjVar(player, "npe");
                    }
                    return SCRIPT_CONTINUE;
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
    public void doStarportToStarportLaunch(obj_id player, obj_id ship, obj_id[] membersApprovedByShipOwner, String planet, String pointName) throws InterruptedException {
        // Proceed with launch if the player is in control of space
        if (inControlOfSpace(player)) {
            performStarportLaunch(player, ship, membersApprovedByShipOwner, planet, pointName);
        } else {
            sendSystemMessage(player, new string_id("travel", "blocked_by_authorities_gcw"));
        }
    }

    public void performStarportLaunch(obj_id player, obj_id ship, obj_id[] membersApprovedByShipOwner, String planet, String pointName) throws InterruptedException {
        // Validate travel point
        if (!getPlanetTravelPointInterplanetary(planet, pointName)) {
            sendSystemMessage(player, new string_id("travel", "invalid_travel_point"));
            return;
        }

        // Handle episode 3 expansion restriction
        if (planet.equals("kashyyyk_main") && !features.hasEpisode3Expansion(player)) {
            sendSystemMessage(player, new string_id("travel", "unauthorized_kashyyyk"));
            return;
        }

        // Basic ship travel check
        if (space_utils.isBasicShip(ship)) {
            location locTest = getLocation(player);
            if (!planet.equals(locTest.area)) {
                sendSystemMessage(player, new string_id("space/space_interaction", "no_travel_basic"));
                return;
            }
        }

        // Handle callables
        if (callable.hasAnyCallable(player)) {
            callable.storeCallables(player);
        }

        LOG("space", "performStarportLaunch called");

        // Initial charge
        int initialPaymentAmount = 20000;
        int additionalChargeAmount = 5000;
        int totalChargeAmount = initialPaymentAmount + additionalChargeAmount;

        // Charge the player
        obj_id spacePort = getContainedBy(player);
        if (!money.requestPayment(player, spacePort, totalChargeAmount, "handlePaymentResult", null, true)) {
            sendSystemMessage(player, new string_id("travel", "payment_failed"));
            return;
        }

        // Send system message for payment processing
        sendSystemMessage(player, new string_id("travel", "processing_payment"));

        // Proceed with launch
        Vector groupMembersToWarp = new Vector();
        groupMembersToWarp.add(player);
        Vector groupMemberStartIndex = new Vector();
        groupMemberStartIndex.add(0);

        Vector shipStartLocations = space_transition.getShipStartLocations(ship);

        if (shipStartLocations != null && !shipStartLocations.isEmpty()) {
            int startIndex = 0;
            location playerLoc = getLocation(player);
            if (isIdValid(playerLoc.cell)) {
                for (obj_id member : membersApprovedByShipOwner) {
                    if (member != player && exists(member) && getLocation(member).cell == playerLoc.cell) {
                        startIndex = space_transition.getNextStartIndex(shipStartLocations, startIndex);
                        if (startIndex < shipStartLocations.size()) {
                            groupMembersToWarp.add(member);
                            groupMemberStartIndex.add(startIndex);
                        }
                        if (callable.hasAnyCallable(member)) {
                            callable.storeCallables(member);
                        }
                    }
                }
            }
        }

        // Debug statement to check the list of members to warp
        LOG("space", "Group members to warp: " + groupMembersToWarp);

        // Warp all group members to the destination
        for (int i = 0; i < groupMembersToWarp.size(); i++) {
            obj_id member = (obj_id)groupMembersToWarp.get(i);
            travel.movePlayerToDestination(member, planet, pointName);
            // Debug statement for each member being warped
            LOG("space", "Warping player: " + member + " to planet: " + planet + " at point: " + pointName);
        }
    }

    public boolean inControlOfSpace(obj_id player) throws InterruptedException {
        // Get the current space region based on the player's location
        String currentRegion = getCurrentSpaceRegion(player);

        if (currentRegion == null || currentRegion.isEmpty()) {
            return true; // Assume control if region information is not available
        }

        int faction = pvpGetAlignedFaction(player);
        int gcwScore = getGcwImperialScorePercentile(currentRegion);

        // Check faction control
        if (faction == -615855020) { // Imperial faction ID
            return gcwScore >= 70;
        } else if (faction == 370444368) { // Rebel faction ID
            return gcwScore <= 30;
        } else if (hasSkill(player, "sm_title_bootlegger")) { // underworld smuggler
            return true;
        }

        return false; // Default to no control for other factions
    }

    public String getCurrentSpaceRegion(obj_id player) throws InterruptedException {
        // Use the existing getGcwRegion method to get the region name
        return gcw.getGcwRegion(player);
    }

    public boolean doSpacePrecheck(obj_id objPlayer) throws InterruptedException
    {
        if (isIncapacitated(objPlayer))
        {
            string_id strSpam = new string_id("space/space_interaction", "no_use_terminal_incapacitated");
            sendSystemMessage(objPlayer, strSpam);
            return false;
        }
        if (isDead(objPlayer))
        {
            string_id strSpam = new string_id("space/space_interaction", "no_use_terminal_dead");
            sendSystemMessage(objPlayer, strSpam);
            return false;
        }
        int intState = getState(objPlayer, STATE_COMBAT);
        if (intState > 0)
        {
            string_id strSpam = new string_id("space/space_interaction", "no_use_terminal_combat");
            sendSystemMessage(objPlayer, strSpam);
            return false;
        }
        return true;
    }
    public void doHyperspaceSelector(obj_id self, obj_id objPlayer) throws InterruptedException
    {
        String strFileName = "datatables/space/hyperspace/hyperspace_locations.iff";
        String[] strPointNames = dataTableGetStringColumnNoDefaults(strFileName, "HYPERSPACE_POINT_NAME");
        utils.setScriptVar(self, "strPointNames", strPointNames);
        String[] strLocalizedNames = new String[strPointNames.length];
        for (int intI = 0; intI < strPointNames.length; intI++)
        {
            strLocalizedNames[intI] = utils.packStringId(new string_id("hyperspace_points_n", strPointNames[intI]));
        }
        String strPrompt = "Choose a Hyperspace point";
        sui.listbox(self, objPlayer, strPrompt, strLocalizedNames, "chooseHyperspaceSpot");
    }
    public int chooseHyperspaceSpot(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null)
        {
            LOG("space", "params");
            return SCRIPT_CONTINUE;
        }
        obj_id player = sui.getPlayerId(params);
        if (!isIdValid(player))
        {
            LOG("space", "invalid ");
            return SCRIPT_CONTINUE;
        }
        int btn = sui.getIntButtonPressed(params);
        if (btn == sui.BP_CANCEL)
        {
            LOG("space", "cancel");
            return SCRIPT_CONTINUE;
        }
        int intIndex = sui.getListboxSelectedRow(params);
        if (intIndex == -1)
        {
            LOG("space", "Row");
            return SCRIPT_CONTINUE;
        }
        String[] strPointNames = utils.getStringArrayScriptVar(self, "strPointNames");
        if (intIndex > strPointNames.length - 1)
        {
            LOG("space", "array length");
            return SCRIPT_CONTINUE;
        }
        String strFileName = "datatables/space/hyperspace/hyperspace_locations.iff";
        dictionary dctPointInfo = dataTableGetRow(strFileName, strPointNames[intIndex]);
        if (dctPointInfo == null)
        {
            LOG("space", "no point");
            return SCRIPT_CONTINUE;
        }
        location warpLocation = new location();
        warpLocation.x = dctPointInfo.getInt("X");
        warpLocation.y = dctPointInfo.getInt("Y");
        warpLocation.z = dctPointInfo.getInt("Z");
        warpLocation.area = dctPointInfo.getString("SCENE");
        obj_id shipControlDevice = utils.getObjIdLocalVar(player, "objControlDevice");
        LOG("space", "shipControlDevice is " + shipControlDevice);
        obj_id ship = space_transition.getShipFromShipControlDevice(shipControlDevice);
        if (!space_utils.isShipUsable(ship, player))
        {
            LOG("space", "bnadship");
            return SCRIPT_CONTINUE;
        }
        if (space_utils.isShipWithInterior(ship))
        {
        }
        if (isIdValid(ship))
        {
            obj_id[] membersApprovedByShipOwner = new obj_id[0];
            location locDestination = space_utils.getRandomLocationInSphere(warpLocation, 150, 300);
            location groundLoc = utils.getLocationScriptVar(self, "space.loc.ground");
            launch(player, ship, membersApprovedByShipOwner, locDestination, groundLoc);
            return SCRIPT_CONTINUE;
        }
        else 
        {
        }
        sendDirtyObjectMenuNotification(self);
        return SCRIPT_CONTINUE;
    }
    public location getFinalHyperspaceLocation(obj_id objPlayer, location locTest) throws InterruptedException
    {
        return locTest;
    }
    public location checkZonePopulation(location[] locTest) throws InterruptedException
    {
        for (location location : locTest) {
            if (!isAreaTooFullForTravel(location.area, 0, 0)) {
                return location;
            }
        }
        return null;
    }
    public boolean blog(String category, String msg) throws InterruptedException
    {
        return true;
    }
}
