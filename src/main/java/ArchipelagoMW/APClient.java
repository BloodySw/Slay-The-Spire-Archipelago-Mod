package ArchipelagoMW;

import ArchipelagoMW.ui.RewardMenu.ArchipelagoRewardScreen;
import ArchipelagoMW.ui.connection.ConnectionPanel;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.events.ConnectionAttemptEvent;
import gg.archipelago.APClient.events.ConnectionResultEvent;
import gg.archipelago.APClient.network.BouncedPacket;
import gg.archipelago.APClient.network.ConnectionResult;
import gg.archipelago.APClient.parts.NetworkItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

public class APClient extends gg.archipelago.APClient.APClient {

    public static final Logger logger = LogManager.getLogger(APClient.class.getName());

    public static APClient apClient;

    public static void newConnection(String address, String slotName, String password) {
        if(apClient != null) {
            apClient.close();
        }
        apClient = new APClient("", 0);
        apClient.setPassword(password);
        apClient.setName(slotName);
        apClient.setItemsHandlingFlags(0b111);
        try {
            apClient.connect(address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private APClient(String saveID, int slotID) {
        super(saveID, slotID);
        this.setGame("Slay the Spire");
    }

    @Override
    public void onConnectResult(ConnectionResultEvent connectionResultEvent) {
        ArchipelagoRewardScreen.rewardsQueued = 0;
        String msg = "Connecting to AP...";
        switch ( connectionResultEvent.getResult()) {
            case SlotAlreadyTaken:
                msg = "Slot already in use.";
                break;
            case Success:
                msg = "Connected Starting Game.";
                break;
            case InvalidSlot:
                msg = "Invalid Slot Name. Please make sure you typed it correctly.";
                break;
            case InvalidPassword:
                msg = "Invalid Password";
                ConnectionPanel.showPassword = true;
                break;
            case IncompatibleVersion:
                msg = "Server Rejected our connection due to an incompatible communication protocol.";
                break;
            default:
                msg = "Unknown Error";
        }
        ConnectionPanel.connectionResultText = msg;

        if(connectionResultEvent.getResult() != ConnectionResult.Success)
            return;

        if(CardCrawlGame.mode != CardCrawlGame.GameMode.CHAR_SELECT)
            return;

        logger.info("about to parse slot data");
        try {
            SlotData data = connectionResultEvent.getSlotData(SlotData.class);
            logger.info("slot data parsed");
            AbstractPlayer.PlayerClass character;
            switch(data.character) {
                case 1:
                    character = AbstractPlayer.PlayerClass.THE_SILENT;
                    break;
                case 2:
                    character = AbstractPlayer.PlayerClass.DEFECT;
                    break;
                case 3:
                    character = AbstractPlayer.PlayerClass.WATCHER;
                    break;
                case 0:
                default:
                    character = AbstractPlayer.PlayerClass.IRONCLAD;
            }

            logger.info("character: "+character.name());
            logger.info("heart: "+data.heartRun);
            logger.info("seed: "+data.seed);
            logger.info("ascension: "+data.ascension);
            /*
            AbstractDungeon.player = CardCrawlGame.characterManager.recreateCharacter(character);
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                relic.updateDescription(AbstractDungeon.player.chosenClass);
                relic.onEquip();
            }

            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if(card.rarity != AbstractCard.CardRarity.BASIC) {
                    CardHelper.obtain(card.cardID, card.rarity, card.color);
                }
            }*/

            CardCrawlGame.chosenCharacter = character;
            CardCrawlGame.mainMenuScreen.isFadingOut = true;
            CardCrawlGame.mainMenuScreen.fadeOutMusic();

            Settings.isFinalActAvailable = (data.heartRun >= 1);
            SeedHelper.setSeed(data.seed);

            AbstractDungeon.isAscensionMode = (data.ascension > 0);
            AbstractDungeon.ascensionLevel = data.ascension;

            AbstractDungeon.generateSeeds();
            Settings.seedSet = true;

            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;

            LocationTracker.reset();
            ArchipelagoRewardScreen.rewards.clear();
            ArchipelagoRewardScreen.index = 0;

            LocationTracker.scoutFirstLocations();
            Set<Long> checkedLocations = getLocationManager().getCheckedLocations();
            for (Long checkedLocation : checkedLocations) {
                logger.info("checkedLocation found: " + checkedLocation);
            }
            //NeowEvent.
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onJoinRoom() {

    }

    @Override
    public void onPrint(String s) {

    }

    @Override
    public void onPrintJson(APPrint apPrint, String s, int i, NetworkItem networkItem) {

    }

    @Override
    public void onBounced(BouncedPacket bouncedPacket) {

    }

    @Override
    public void onError(Exception e) {
        ConnectionPanel.connectionResultText = "Server Error [] NL " + e.getMessage();
    }

    @Override
    public void onClose(String message, int i) {
        ConnectionPanel.connectionResultText = "Connection Closed [] NL " + message;
    }

    @Override
    public void onReceiveItem(NetworkItem networkItem) {
        //ignore received items that happen while we are not yet loaded
        logger.info("NetworkItem received: " + networkItem.itemName);
        ArchipelagoRewardScreen.rewardsQueued +=1 ;
        if (AbstractDungeon.isPlayerInDungeon()) {
            AbstractRoom room;
            try {
                room =  AbstractDungeon.getCurrRoom();
                logger.info("Player is in dungeon! Adding it! He is in room: " + room);
                ArchipelagoRewardScreen.addReward(networkItem);
            }
            catch (NullPointerException e) {
                logger.info("Player is not in the dungeon yet? GetCurrRoom Failed. Most likely on second and further runs");
            }

        }
    }

    @Override
    public void onLocationInfo(ArrayList<NetworkItem> networkItems) {
        LocationTracker.addToScoutedLocations(networkItems);
    }

    @Override
    public void onLocationChecked(long l) {

    }

    @Override
    public void onAttemptConnection(ConnectionAttemptEvent connectionAttemptEvent) {

    }
}
