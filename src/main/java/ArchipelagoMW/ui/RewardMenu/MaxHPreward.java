package ArchipelagoMW.ui.RewardMenu;

    import ArchipelagoMW.patches.RewardItemPatch;
    import ArchipelagoMW.util.TextureLoader;
    import basemod.abstracts.CustomReward;
    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.graphics.Texture;
    import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

    import static ArchipelagoMW.ArchipelagoMW.makeUIPath;

public class MaxHPreward extends CustomReward {
    private static final Texture ICON = TextureLoader.getTexture(makeUIPath("FullHeal64v2.png"));

    public int amount;

    public static int defaultAmount = 5;

    //public MaxHPreward() {
    //    this(defaultAmount);
    //}
    public MaxHPreward(String player, String location) {
        super(ICON, "dummy text", RewardItemPatch.RewardType.ARCHIPELAGO_MAX_HP);
        this.text =("+" + defaultAmount + " Max HP [] NL " + player + " [] NL " + location);
        RewardItemPatch.CustomFields.apReward.set(this, true);
        this.amount = defaultAmount;
    }

    @Override
    public boolean claimReward() {
        AbstractDungeon.player.increaseMaxHp(this.amount,true);
        return true;
    }
}