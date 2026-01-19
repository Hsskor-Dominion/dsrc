package script.item.container;

import script.*;
import script.library.*;

public class locked_slicable extends script.base_script
{
    public locked_slicable() { }

    public static final string_id SID_SLICE = new string_id("slicing/slicing", "slice");
    public static final string_id SID_LOCKED = new string_id("slicing/slicing", "locked");
    public static final string_id SID_BROKEN = new string_id("slicing/slicing", "broken");
    public static final string_id SID_SUCCESS = new string_id("slicing/slicing", "container_success");
    public static final string_id SID_FAIL = new string_id("slicing/slicing", "container_fail");

    public static final string_id SID_SPYNET_ENCRYPT = new string_id("slicing/slicing", "spynet_encrypt");
    public static final string_id SID_ENCRYPT_SUCCESS = new string_id("slicing/slicing", "encrypt_success");
    public static final string_id SID_ENCRYPT_CAPPED = new string_id("slicing/slicing", "encrypt_credit_capped");

    public int OnAttach(obj_id self) throws InterruptedException
    {
        if (!hasObjVar(self, "slicing.locked"))
        {
            setObjVar(self, "slicing.locked", 1);
        }
        if (!hasObjVar(self, "slicing.slicable"))
        {
            setObjVar(self, "slicing.slicable", 1);
        }
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        // Smuggler slice option
        if (hasSkill(player, "class_smuggler_phase1_novice") && hasObjVar(self, "slicing.locked"))
        {
            mi.addRootMenu(menu_info_types.SERVER_MENU1, SID_SLICE);
        }

        // Spy encrypt option
        if (hasSkill(player, "class_spy_phase1_novice"))
        {
            mi.addRootMenu(menu_info_types.SERVER_MENU2, SID_SPYNET_ENCRYPT);
        }

        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        // --- Smuggler Slice (unlock + payout) ---
        if (item == menu_info_types.SERVER_MENU1)
        {
            if (!hasSkill(player, "class_smuggler_phase1_novice"))
                return SCRIPT_CONTINUE;

            if (!hasObjVar(self, "slicing.slicable"))
            {
                sendSystemMessage(player, SID_BROKEN);
                return SCRIPT_CONTINUE;
            }

            if (factions.getFactionStanding(player, "underworld") < -100)//this is sorta useless
            {
                sendSystemMessage(player, new string_id("slicing/slicing", "not_enough_underworld"));
                return SCRIPT_CONTINUE;
            }

            // Add faction + xp
            factions.addFactionStanding(player, "underworld", 5);
            xp.grant(player, "slicing", 100);
//            sendSystemMessageProse(player, prose.getPackage(SID_BIO_EXP, "bio_engineer_dna_harvesting", xpAmount));

            // --- Retrieve stored credits and loot ---
            int storedCredits = getIntObjVar(self, "slicing.storedCredits");
            String[] storedLoot = getStringArrayObjVar(self, "slicing.storedLoot");
            int encryptionCount = getIntObjVar(self, "slicing.encryptionCount");

            obj_id container = self;

            // --- Always give some credits ---
            if (storedCredits <= 0)
            {
                storedCredits = 1000;
                sendSystemMessageTestingOnly(player, "Minimal encrypted credits recovered (1000).");
            }
            else
            {
                sendSystemMessageTestingOnly(player, "You recovered " + storedCredits + " encrypted credits!");
            }

            // Generate credit chip
            obj_id creditChip = createObject("object/tangible/item/loot_credit_chip.iff", container, "");
            if (isIdValid(creditChip))
            {
                setCount(creditChip, storedCredits);
                setObjVar(creditChip, "loot.intCredits", storedCredits);
            }

            // --- Generate stored loot (static + IFF) ---
            if (storedLoot != null && storedLoot.length > 0)
            {
                int itemCount = Math.min(encryptionCount, storedLoot.length);
                itemCount = Math.min(itemCount, 4); // cap at 4 items

                sendSystemMessageTestingOnly(player, "Decryption level " + encryptionCount + ": yielded " + itemCount + " recovered item(s).");

                for (int i = 0; i < itemCount; i++)
                {
                    String template = storedLoot[i];
                    obj_id createdItem = null;

                    if (template.startsWith("item_")) // static/datatable item
                    {
                        createdItem = static_item.createNewItemFunction(template, container);
                    }
                    else // regular IFF object
                    {
                        createdItem = createObject(template, container, "");
                    }

                    if (isIdValid(createdItem))
                        sendSystemMessageTestingOnly(player, "Recovered item: " + template);
                }
            }

            // --- Reset slicing state ---
            setObjVar(self, "slicing.storedCredits", 0);
            removeObjVar(self, "slicing.storedLoot");
            setObjVar(self, "slicing.encryptionCount", 0);
            removeObjVar(self, "slicing.locked");

            sendSystemMessage(player, SID_SUCCESS);
            return SCRIPT_CONTINUE;
        }

        // --- Spy Encryption (sets up stored loot and credits) ---
        if (item == menu_info_types.SERVER_MENU2)
        {
            if (!hasSkill(player, "class_spy_phase1_novice"))
                return SCRIPT_CONTINUE;

            // Cooldown (60s)
            int currentTime = getGameTime();
            int lastEncrypt = getIntObjVar(self, "slicing.lastEncryptTime");
            if (lastEncrypt > 0 && (currentTime - lastEncrypt) < 120)
            {
                int remaining = 120 - (currentTime - lastEncrypt);
                sendSystemMessageTestingOnly(player, "You must wait " + remaining + " seconds before encrypting again.");
                return SCRIPT_CONTINUE;
            }

            // Faction check
            if (factions.getFactionStanding(player, "sif") < 1)
            {
                sendSystemMessage(player, new string_id("slicing/slicing", "not_enough_spynet"));
                return SCRIPT_CONTINUE;
            }

            // Deduct faction
            factions.addFactionStanding(player, "sif", -1);

            int encryptionCount = getIntObjVar(self, "slicing.encryptionCount");
            encryptionCount++;

            // --- Quadratic credit growth ---
            int baseMin = 5000 + (encryptionCount * encryptionCount * 1500);
            int baseMax = 6000 + (encryptionCount * encryptionCount * 2500);
            if (baseMax > 100000)
            {
                baseMax = 100000;
                baseMin = 100000;
                sendSystemMessage(player, SID_ENCRYPT_CAPPED);
            }
            int rolledCredits = rand(baseMin, baseMax);

            // --- Random loot pool ---
            String[] lootOptions = {
                    "object/intangible/data_item/warren_encryption_key.iff",
                    "object/tangible/dungeon/death_watch_bunker/passkey_hall.iff",
                    "object/tangible/dungeon/death_watch_bunker/passkey_mine.iff",
                    "object/tangible/dungeon/death_watch_bunker/passkey_storage.iff",
                    "object/tangible/dungeon/keypad_terminal.iff",
                    "object/static/item/item_key_electronic.iff",
                    "object/static/worldbuilding/terminal/wall_door_keypad_01.iff",
                    "object/tangible/collection/reward/col_magseal_keycard_01.iff",
                    "object/tangible/loot/dungeon/geonosian_mad_bunker/engineering_key.iff",
                    "object/tangible/loot/dungeon/geonosian_mad_bunker/passkey.iff",
                    "object/tangible/loot/misc/key_electronic_s01.iff",
                    "object/tangible/loot/npc_loot/electronic_key_generic.iff",
                    "object/tangible/tcg/series5/decorative_deathstar_hologram.iff",
                    "object/tangible/loot/quest/nym_research_passkey.iff",
                    "object/tangible/mission/quest_item/warren_device_encryption_key.iff",
                    "object/tangible/mission/quest_item/warren_passkey_s01.iff",
                    "object/tangible/mission/quest_item/warren_passkey_s02.iff",
                    "object/tangible/mission/quest_item/warren_passkey_s03.iff",
                    "object/tangible/mission/quest_item/warren_passkey_s04.iff",
                    "object/tangible/collection/deathtrooper_gamma_datadisk_diary_01.iff",
                    "object/tangible/quest/imperial/itp_emperor_datadisk.iff",
                    "object/tangible/collection/deathtrooper_alpha_datadisk_letter_01.iff",
                    "object/draft_schematic/item/item_shellfish_harvester.iff",
                    "object/tangible/item/data_cube.iff",
                    "object/tangible/item/rare_loot_chest_spice.iff",
                    "object/tangible/item/plant/force_melon.iff",
                    "object/tangible/encoded_disk/dead_eye_decoder.iff",
                    "object/tangible/encoded_disk/dead_eye_disk.iff",
                    "object/tangible/encoded_disk/encoded_disk_base.iff",
                    "object/tangible/encoded_disk/imperial_slicer_disk.iff",
                    "object/tangible/encoded_disk/message_fragment_base.iff",
                    "object/tangible/encoded_disk/message_assembled_base.iff",
                    "object/tangible/gambling/wheel/roulette.iff",
                    "object/tangible/gambling/slot/standard.iff",
                    "object/tangible/hologram/hologram_ff_space_battle_2010.iff",
                    "object/tangible/loot/loot_schematic/corellian_corvette_landspeeder_av21_schematic.iff",
                    "object/tangible/loot/loot_schematic/corellian_corvette_rifle_berserker_schematic.iff",
                    "object/tangible/loot/loot_schematic/geonosian_sonic_blaster_schematic.iff",
                    "object/tangible/loot/loot_schematic/yt1300_schematic.iff",
                    "object/tangible/parrot_cage/parrot_cage.iff",
                    "object/tangible/space/special_loot/encoded_document.iff",
                    "object/tangible/space/special_loot/firespray_schematic_part1.iff",
                    "object/tangible/space/special_loot/firespray_schematic_part2.iff",
                    "object/tangible/space/special_loot/firespray_schematic_part3.iff",
                    "object/tangible/space/special_loot/firespray_schematic_part4.iff",
                    "object/tangible/space/special_loot/firespray_schematic_part5.iff",
                    "object/tangible/space/special_loot/firespray_schematic_part6.iff",
                    "object/tangible/space/special_loot/firespray_schematic_part7.iff",
                    "object/tangible/space/special_loot/firespray_schematic_part8.iff",
                    "object/tangible/space/special_loot/interdiction_data_disk.iff",
                    "object/tangible/space/special_loot/piracy_crate.iff",
                    "object/tangible/content/final_data_disk.iff",
                    "object/tangible/content/final_data_disk_rebel.iff",
                    "object/tangible/theme_park/alderaan/act2/decoder_comp_housing.iff",
                    "object/tangible/theme_park/alderaan/act2/decoder_comp_power.iff",
                    "object/tangible/theme_park/alderaan/act2/decoder_comp_processor.iff",
                    "object/tangible/theme_park/alderaan/act2/decoder_comp_reader.iff",
                    "object/tangible/theme_park/alderaan/act2/decoder_comp_screen.iff",
                    "object/tangible/theme_park/alderaan/act2/decoder_comp_translation.iff",
                    "object/tangible/theme_park/alderaan/act2/interface_override_device.iff",
                    "object/tangible/theme_park/alderaan/act2/relay_station_terminal.iff",
                    "object/tangible/theme_park/alderaan/act3/alderaan_flora.iff",
                    "object/tangible/theme_park/alderaan/act3/broken_grav_unit.iff",
                    "object/tangible/theme_park/alderaan/act3/dead_eye_prototype.iff",
                    "object/tangible/theme_park/alderaan/act3/encoded_data_disk.iff",
                    "object/tangible/theme_park/alderaan/act3/grav_unit_repair_kit.iff",
                    "object/tangible/slicing/slicing_laser_knife.iff",
                    "object/tangible/wearables/armor/bounty_hunter/armor_bounty_hunter_helmet.iff",
                    "item_pgc_token_03", // static/datatable item
                    "rare_loot_chest_quality_1", // static/datatable item
                    "rare_loot_chest_quality_2", // static/datatable item
                    "rare_loot_chest_quality_3", // static/datatable item
                    "item_heroic_token_axkva_01_01",
                    "item_heroic_token_tusken_01_01",
                    "item_heroic_token_ig88_01_01",
                    "item_heroic_token_black_sun_01_01",
                    "item_heroic_token_exar_01_01",
                    "item_heroic_token_echo_base_01_01",
                    "item_battlefield_rebel_token_massassi_isle",
                    "item_battlefield_imperial_token_massassi_isle",
                    "item_battlefield_rebel_token_battlefield2",
                    "item_battlefield_imperial_token_battlefield2",
                    "item_battlefield_rebel_token_battlefield3",
                    "item_battlefield_imperial_token_battlefield3",
                    "item_battlefield_rebel_token_battlefield4",
                    "item_battlefield_imperial_token_battlefield4",
                    "object/tangible/jedi/no_drop_jedi_holocron_light.iff",
                    "object/tangible/jedi/no_drop_jedi_holocron_dark.iff"
            };

            // --- Add one new random loot item ---
            String[] existingLoot = getStringArrayObjVar(self, "slicing.storedLoot");
            if (existingLoot == null)
                existingLoot = new String[0];

            int lootIndex = rand(0, lootOptions.length - 1);
            String newLoot = lootOptions[lootIndex];

            int newLength = Math.min(existingLoot.length + 1, 5);
            String[] updatedLoot = new String[newLength];
            for (int i = 0; i < existingLoot.length && i < newLength; i++)
                updatedLoot[i] = existingLoot[i];
            if (existingLoot.length < newLength)
                updatedLoot[existingLoot.length] = newLoot;

            // --- STORE RESULTS ---
            setObjVar(self, "slicing.storedCredits", rolledCredits);
            setObjVar(self, "slicing.storedLoot", updatedLoot);
            setObjVar(self, "slicing.encryptionCount", encryptionCount);
            setObjVar(self, "slicing.lastEncryptTime", currentTime);
            setObjVar(self, "slicing.locked", 1);

            sendSystemMessageTestingOnly(player,
                    "Encryption successful (" + encryptionCount + "x). Stored " + updatedLoot.length + " encrypted items and " + rolledCredits + " credits.");
            sendSystemMessage(player, SID_ENCRYPT_SUCCESS);

            return SCRIPT_CONTINUE;
        }

        return SCRIPT_CONTINUE;
    }

    public int OnAboutToOpenContainer(obj_id self, obj_id opener) throws InterruptedException
    {
        if (hasObjVar(self, "slicing.locked"))
        {
            sendSystemMessage(opener, SID_LOCKED);
            return SCRIPT_OVERRIDE;
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
            removeObjVar(self, "slicing.locked");
            sendSystemMessage(player, SID_SUCCESS);
            messageTo(self, "handleSlicingSuccess", null, 0.0f, true);
        }
        else
        {
            removeObjVar(self, "slicing.slicable");
            sendSystemMessage(player, SID_FAIL);
        }

        return SCRIPT_CONTINUE;
    }

    public static int rand(int min, int max)
    {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}