package ArchipelagoMW.ui.RewardMenu;

import ArchipelagoMW.patches.RewardItemPatch;
import ArchipelagoMW.util.TextureLoader;
import basemod.BaseMod;
import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ArchipelagoMW.ArchipelagoMW.makeUIPath;

public class CardRemovalReward extends CustomReward {
    public static final Logger logger = LogManager.getLogger(CardRemovalReward.class.getName());
    private static final Texture ICON = TextureLoader.getTexture(makeUIPath("CardRemoval64.png"));

    boolean cardSelected = true;
    public CardRemovalReward(String player, String location) {
        super(ICON, "Card removal [] NL " + player + " [] NL " + location, RewardItemPatch.RewardType.ARCHIPELAGO_CARD_REMOVAL);
        RewardItemPatch.CustomFields.apReward.set(this, true);
    }

    @Override
    public boolean claimReward() {
        cardSelected = false; // 2. Tell the relic that we haven't bottled the card yet
        if (AbstractDungeon.isScreenUp) { // 3. If the map is open - hide it.
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;

        }
        logger.info("you are going to remove a card!");
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
        // 4. Set the room to INCOMPLETE - don't allow us to use the map, etc.
        CardGroup group = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck); // 5. Get a card group of all currently unbottled cards
        AbstractDungeon.gridSelectScreen.open(group, 1, "Select a card for Archipelago gods to remove", false, false, false, true);

        return false;
    }
    @Override
    public void update() {
        super.update(); //Do all of the original update() method in AbstractRelic

        if (!cardSelected && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            // If the card hasn't been bottled yet and we have cards selected in the gridSelectScreen (from onEquip)
            cardSelected = true; //Set the cardSelected boolean to be true - we're about to bottle the card.
            AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(0); // The custom Savable "card" is going to equal
            // The card from the selection screen (it's only 1, so it's at index 0)
            //BottledPlaceholderField.inBottledPlaceholderField.set(card, true); // Use our custom spire field to set that card to be bottled.
            logger.info("you have removed a card: " + card.name);
            CardCrawlGame.sound.play("CARD_EXHAUST");
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            AbstractDungeon.player.masterDeck.removeCard(card);
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE; // The room phase can now be set to complete (From INCOMPLETE in onEquip)
            AbstractDungeon.gridSelectScreen.selectedCards.clear(); // Always clear your grid screen after using it.

            //ArchipelagoRewardScreen.close();
            ArchipelagoRewardScreen.rewards.remove(this);
            ArchipelagoRewardScreen.positionRewards();//TODO crashes due to iterator bug

            //ArchipelagoRewardScreen.open();
        }
    }
}