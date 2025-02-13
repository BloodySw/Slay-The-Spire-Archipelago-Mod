package ArchipelagoMW;

import ArchipelagoMW.ui.RewardMenu.BossRelicRewardScreen;
import ArchipelagoMW.ui.topPannel.ArchipelagoIcon;
import ArchipelagoMW.util.IDCheckDontTouchPls;
import ArchipelagoMW.util.TextureLoader;
import basemod.BaseMod;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


@SpireInitializer
public class ArchipelagoMW implements
        EditStringsSubscriber,
        PostInitializeSubscriber {
    // Make sure to implement the subscribers *you* are using (read basemod wiki). Editing cards? EditCardsSubscriber.
    // Making relics? EditRelicsSubscriber. etc., etc., for a full list and how to make your own, visit the basemod wiki.
    public static final Logger logger = LogManager.getLogger(ArchipelagoMW.class.getName());
    private static String modID;

    // Mod-settings settings. This is if you want an on/off savable button

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Archipelago Multi-World";
    private static final String AUTHOR = "Kono Tyran & Mavelovent"; // And pretty soon - You!
    private static final String DESCRIPTION = "An Archipelago multiworld mod.";

    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "ArchipelagoMWResources/images/Badge.png";
    public static BossRelicRewardScreen bossRelicRewardScreen;

    // Archipelago Client Varaiables
    public static String address;
    public static String slotName;
    public static String password;

    public static Texture AP_ICON;

    // =============== MAKE IMAGE PATHS =================

    public static String makeUIPath(String resourcePath) {
        return getModID() + "Resources/images/ui/" + resourcePath;
    }

    // =============== /MAKE IMAGE PATHS/ =================

    // =============== /INPUT TEXTURE LOCATION/ =================


    // =============== SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE =================

    public ArchipelagoMW() {
        logger.info("Subscribe to BaseMod hooks");

        BaseMod.subscribe(this);

        setModID("ArchipelagoMW");

        logger.info("Done subscribing");
    }

    // ====== NO EDIT AREA ======
    // DON'T TOUCH THIS STUFF. IT IS HERE FOR STANDARDIZATION BETWEEN MODS AND TO ENSURE GOOD CODE PRACTICES.
    // IF YOU MODIFY THIS I WILL HUNT YOU DOWN AND DOWNVOTE YOUR MOD ON WORKSHOP

    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = ArchipelagoMW.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO

    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH

    private static void pathCheck() { // ALSO NO
        Gson coolG = new Gson(); // NOPE DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i still hate u btw Gdx.files
        InputStream in = ArchipelagoMW.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THISSSSS
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // NAH, NO EDIT
        String packageName = ArchipelagoMW.class.getPackage().getName(); // STILL NO EDIT ZONE
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources"); // PLEASE DON'T EDIT THINGS HERE, THANKS
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) { // LEAVE THIS EDIT-LESS
            if (!packageName.equals(getModID())) { // NOT HERE ETHER
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID()); // THIS IS A NO-NO
            } // WHY WOULD U EDIT THIS
            if (!resourcePathExists.exists()) { // DON'T CHANGE THIS
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources"); // NOT THIS
            }// NO
        }// NO
    }// NO

    // ====== YOU CAN EDIT AGAIN ======


    public static void initialize() {
        logger.info("========================= Initializing Archipelago Multi-World. Hi. =========================");
        ArchipelagoMW Archipelago = new ArchipelagoMW();
        logger.info("========================= /Archipelago Multi-World Initialized. Hello Multi-World./ =========================");
    }

    public static void setConnectionInfo(String addressField, String slotNameField, String passwordField) {
        address = addressField;
        slotName = slotNameField;
        password = passwordField;
    }

    // =============== POST-INITIALIZE =================

    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");

        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);

        AP_ICON = TextureLoader.getTexture(makeUIPath("ap_icon.png"));

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();

        int configPos = 800;
        int configStep = 40;
        configPos -= 90;
        ModLabel validLabel = new ModLabel("Valid Characters:", 350.0F, (float) configPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, (label) -> {
        });
        settingsPanel.addUIElement(validLabel);

        String[] titles = BaseMod.getModdedCharacters().stream().map(p -> p.title).toArray(String[]::new);

        int chunkSize = 4;
        int remainder = titles.length % chunkSize;
        int chunks = titles.length / chunkSize + (remainder > 1 ? 1 : 0);
        for (int i = 0; i <= chunks; i++) {
            configPos -= configStep;
            String[] line;
            if (i == chunks && remainder > 0) {
                line = Arrays.copyOfRange(titles, chunks * chunkSize, titles.length);
            } else {
                line = Arrays.copyOfRange(titles, i * chunkSize, i * chunkSize + chunkSize);
            }
            ModLabel lineLabel = new ModLabel("\"" + String.join("\", \"", line) + "\"", 350.0F, (float) configPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, (label) -> {
            });
            settingsPanel.addUIElement(lineLabel);
        }

        //bossRelicRewardScreen = new BossRelicRewardScreen();

        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
        BaseMod.addTopPanelItem(new ArchipelagoIcon());
        //Settings.isInfo = true;
        //Settings.isDebug = true;


        // =============== EVENTS =================
        // https://github.com/daviscook477/BaseMod/wiki/Custom-Events

        // You can add the event like so:
        // BaseMod.addEvent(IdentityCrisisEvent.ID, IdentityCrisisEvent.class, TheCity.ID);
        // Then, this event will be exclusive to the City (act 2), and will show up for all characters.
        // If you want an event that's present at any part of the game, simply don't include the dungeon ID

        // If you want to have more specific event spawning (e.g. character-specific or so)
        // deffo take a look at that basemod wiki link as well, as it explains things very in-depth
        // btw if you don't provide event type, normal is assumed by default

        // Create a new event builder
        // Since this is a builder these method calls (outside of create()) can be skipped/added as necessary
        //AddEventParams eventParams = new AddEventParams.Builder(IdentityCrisisEvent.ID, IdentityCrisisEvent.class) // for this specific event
        //    .dungeonID(TheCity.ID) // The dungeon (act) this event will appear in
        //    .create();

        // Add the event
        //BaseMod.addEvent(eventParams);

        // =============== /EVENTS/ =================
        BaseMod.removeRelic(RelicLibrary.getRelic("Calling Bell"));
        logger.info("Done loading badge Image and mod options");
    }

    // =============== / POST-INITIALIZE/ =================


    // ================ LOAD THE TEXT ===================

    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings for mod with ID: " + getModID());

        // UIStrings
        BaseMod.loadCustomStringsFile(UIStrings.class,
                getModID() + "Resources/localization/eng/ArchipelagoMW-UI-Strings.json");

        logger.info("Done editing strings");
    }

    // ================ /LOAD THE TEXT/ ===================


    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }
}
