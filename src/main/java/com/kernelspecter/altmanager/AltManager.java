package com.kernelspecter.altmanager;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

/**
 * Elixe-style alt manager: switch to offline accounts from the main menu or multiplayer screen, or with /alts and a
 * keybind where those menus are replaced (Lunar).
 */
@Mod(modid = "altmanager", name = "Alt Manager", version = "1.0",
     acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class AltManager {
    private static final int BUTTON_ID = 0x416C74;

    private final Minecraft mc = Minecraft.getMinecraft();
    private AltStore store;
    private SessionSwitcher switcher;
    private KeyBinding openKey;
    private boolean openNextTick;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        store = new AltStore(new File(e.getModConfigurationDirectory(), "altmanager.json"));
        store.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        switcher = new SessionSwitcher(mc);
        openKey = new KeyBinding("Open Alt Manager", Keyboard.KEY_NONE, "Alt Manager");
        ClientRegistry.registerKeyBinding(openKey);
        ClientCommandHandler.instance.registerCommand(new AltsCommand());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static boolean hasButton(GuiScreen gui) {
        return gui instanceof GuiMainMenu || gui instanceof GuiMultiplayer;
    }

    private void open(GuiScreen parent) {
        mc.displayGuiScreen(new GuiAltManager(parent, store, switcher));
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post e) {
        if (hasButton(e.gui)) {
            e.buttonList.add(new GuiButton(BUTTON_ID, 4, 4, 90, 20, "Alt Manager"));
        }
    }

    @SubscribeEvent
    public void onAction(GuiScreenEvent.ActionPerformedEvent.Pre e) {
        if (hasButton(e.gui) && e.button.id == BUTTON_ID) {
            e.setCanceled(true);
            open(e.gui);
        }
    }

    @SubscribeEvent
    public void onDraw(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (hasButton(e.gui)) {
            mc.fontRendererObj.drawStringWithShadow("Logged in as §a" + mc.getSession().getUsername(), 100, 10, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) {
        if (openKey.isPressed() && mc.currentScreen == null) {
            open(null);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        // /alts runs while the chat is open, which closes right after; open the screen once it has.
        if (e.phase == TickEvent.Phase.END && openNextTick) {
            openNextTick = false;
            open(mc.currentScreen);
        }
    }

    private final class AltsCommand extends CommandBase {
        @Override
        public String getCommandName() {
            return "alts";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/alts";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            openNextTick = true;
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }

        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            return true;
        }
    }
}
