package ArchipelagoMW.patches;

import ArchipelagoMW.LocationTracker;
import basemod.BaseMod;
import basemod.DevConsole;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.neow.NeowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NeowPatch {

    public static final Logger logger = LogManager.getLogger(NeowPatch.class.getName());
    public static boolean act2portalAvailable = false;
    public static boolean act3portalAvailable = false;
    public static boolean portals = true;

    public NeowPatch() {
    }

    @SpirePatch(clz = NeowEvent.class, method = SpirePatch.CLASS)
    public static class CustomFields {
        public static SpireField<Boolean> finished = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = NeowEvent.class, method = "buttonEffect")
    public static class createPortalOptions {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(NeowEvent __instance, int buttonPressed, int ___screenNum) {
            if(!portals){
                return SpireReturn.Continue();
            }
            if (___screenNum == 99) {
                boolean portalEngaged = false;
                int incomingAct = 1;
                switch (buttonPressed) {
                    case 0:
                        BaseMod.console.setText("act TheBeyond");
                        portalEngaged = true;
                        incomingAct = 3;
                        break;
                    case 1:
                        BaseMod.console.setText("act TheCity");
                        portalEngaged = true;
                        incomingAct = 2;
                        break;
                }
                if (portalEngaged) {
                    LocationTracker.SetLocationCounters(incomingAct);
                    CustomFields.finished.set(__instance, true);
                    __instance.roomEventText.clear();
                    CardCrawlGame.music.fadeOutBGM();
                    DevConsole.execute();
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        @SpirePostfixPatch
        public static void Postfix(NeowEvent __instance, int buttonPressed, int ___screenNum) {
            boolean finished = CustomFields.finished.get(__instance);
            if (finished || !portals) {
                return;
            }
            if (___screenNum == 99) {
                __instance.roomEventText.clear();
                __instance.roomEventText.addDialogOption("Portal to act 3", !act3portalAvailable);
                __instance.roomEventText.addDialogOption("Portal to act 2", !act2portalAvailable);
                __instance.roomEventText.addDialogOption("[Leave]");
            }
        }
    }
}
