package ArchipelagoMW.ui.RewardMenu;

    import ArchipelagoMW.patches.NeowPatch;
    import ArchipelagoMW.patches.RewardItemPatch;
    import ArchipelagoMW.util.TextureLoader;
    import basemod.abstracts.CustomReward;
    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import static ArchipelagoMW.ArchipelagoMW.makeUIPath;

public class MaxHPreward extends CustomReward {
    public static final Logger logger = LogManager.getLogger(MaxHPreward.class.getName());
    private static final Texture ICON = TextureLoader.getTexture(makeUIPath("FullHeal64v2.png"));

    public int amount;
    public static int defaultAmount = 5;
    public MaxHPreward(String player, String location) {
        super(ICON, "+" + defaultAmount + " Max HP [] NL " + player + " [] NL " + location, RewardItemPatch.RewardType.ARCHIPELAGO_MAX_HP);
        RewardItemPatch.CustomFields.apReward.set(this, true);
        this.amount = defaultAmount;
    }

    @Override
    public boolean claimReward() {
        AbstractDungeon.player.increaseMaxHp(this.amount,true);
        return true;
    }
}