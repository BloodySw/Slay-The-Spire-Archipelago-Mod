package ArchipelagoMW.patches;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomRewardPatch {
    public static final Logger logger = LogManager.getLogger(NeowPatch.class.getName());

    @SpirePatch(clz = CustomReward.class,method = "render")
    public static class CustomizeRewards {

        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderSmartText")) {
                        m.replace("");
                    }
                }
            };
        }
        @SpireInsertPatch(rloc = 146 - 93, localvars = {"c"})
        public static void Insert(RewardItem __instance, SpriteBatch sb, String ___text, float ___y, Color c) {
            if (___text.contains("[] NL")) {
                //float lineHeight = FontHelper.getSmartWidth(FontHelper.cardDescFont_N, ___text, 1000.0F * Settings.scale, 30.0F);
                FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, ___text, (float)Settings.WIDTH * 0.434F, ___y + 35.5F * Settings.scale, 1000.0F * Settings.scale, 26.0F * Settings.scale, c);
            } else {
                FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, ___text, (float)Settings.WIDTH * 0.434F, ___y + 5.0F * Settings.scale, 1000.0F * Settings.scale, 0.0F, c);
            }
        }
    }
}
