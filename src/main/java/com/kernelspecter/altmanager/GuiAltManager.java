package com.kernelspecter.altmanager;

import java.io.IOException;
import java.util.Random;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

/** The alt list: add, log in as, delete, random name, back to the main account. */
public class GuiAltManager extends GuiScreen {
    private static final int ADD = 0;
    private static final int RANDOM = 1;
    private static final int LOGIN = 2;
    private static final int DELETE = 3;
    private static final int MAIN = 4;
    private static final int DONE = 5;
    private static final int GREEN = 0x55FF55;
    private static final int RED = 0xFF5555;

    private final GuiScreen parent;
    private final AltStore store;
    private final SessionSwitcher switcher;
    private final Random random = new Random();
    private AltList list;
    private GuiTextField nameField;
    private GuiButton loginButton;
    private GuiButton deleteButton;
    private GuiButton mainButton;
    private int selected = -1;
    private String status = "";
    private int statusColor = GREEN;

    public GuiAltManager(GuiScreen parent, AltStore store, SessionSwitcher switcher) {
        this.parent = parent;
        this.store = store;
        this.switcher = switcher;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        String typed = nameField == null ? "" : nameField.getText();
        list = new AltList();
        list.registerScrollButtons(7, 8);
        int row1 = height - 54;
        int row2 = height - 28;
        int left = width / 2 - 154;
        nameField = new GuiTextField(10, fontRendererObj, left, row1, 150, 20);
        nameField.setMaxStringLength(16);
        nameField.setText(typed);
        nameField.setFocused(true);
        buttonList.clear();
        buttonList.add(new GuiButton(ADD, left + 158, row1, 74, 20, "Add"));
        buttonList.add(new GuiButton(RANDOM, left + 236, row1, 74, 20, "Random"));
        buttonList.add(loginButton = new GuiButton(LOGIN, left, row2, 74, 20, "Login"));
        buttonList.add(deleteButton = new GuiButton(DELETE, left + 78, row2, 74, 20, "Delete"));
        buttonList.add(mainButton = new GuiButton(MAIN, left + 158, row2, 74, 20, "Main account"));
        buttonList.add(new GuiButton(DONE, left + 236, row2, 74, 20, "Done"));
        updateButtons();
    }

    @Override
    public void updateScreen() {
        nameField.updateCursorCounter();
        updateButtons();
    }

    private boolean hasSelection() {
        return selected >= 0 && selected < store.alts().size();
    }

    private void updateButtons() {
        loginButton.enabled = hasSelection() || !nameField.getText().trim().isEmpty();
        deleteButton.enabled = hasSelection();
        mainButton.enabled = !switcher.onOriginal();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        if (button.id == ADD) {
            add(nameField.getText().trim());
        } else if (button.id == RANDOM) {
            nameField.setText(Names.random(random));
        } else if (button.id == LOGIN) {
            String typed = nameField.getText().trim();
            login(!typed.isEmpty() ? typed : store.alts().get(selected));
        } else if (button.id == DELETE) {
            String name = store.alts().get(selected);
            store.remove(name);
            store.save();
            selected = Math.min(selected, store.alts().size() - 1);
            say("Deleted " + name, GREEN);
        } else if (button.id == MAIN) {
            if (switcher.restore()) {
                say("Back on your main account, " + switcher.originalName() + appliesWhen(), GREEN);
            } else {
                say("Couldn't switch back (see the log)", RED);
            }
        } else if (button.id == DONE) {
            mc.displayGuiScreen(parent);
        }
    }

    private void add(String name) {
        AltStore.Result r = store.add(name);
        if (r == AltStore.Result.ADDED) {
            store.save();
            selected = store.alts().size() - 1;
            nameField.setText("");
            say("Added " + name, GREEN);
        } else if (r == AltStore.Result.DUPLICATE) {
            say(name + " is already in the list", RED);
        } else {
            say(invalidMessage(name), RED);
        }
    }

    private void login(String name) {
        if (!Names.valid(name)) {
            say(invalidMessage(name), RED);
        } else if (switcher.switchTo(name)) {
            say("Logged in as " + name + appliesWhen(), GREEN);
        } else {
            say("Couldn't switch account (see the log)", RED);
        }
    }

    private static String invalidMessage(String name) {
        return name.isEmpty() ? "Type a username first" : "Names are 3-16 letters, numbers or _";
    }

    /** The world you're in keeps the old name; the next one you join gets the new one. */
    private String appliesWhen() {
        return mc.theWorld != null ? " (from the next world or server you join)" : "";
    }

    private void say(String message, int color) {
        status = message;
        statusColor = color;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        } else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            actionPerformed(loginButton);
        } else {
            nameField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        list.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        list.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRendererObj, "Alt Manager", width / 2, 8, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Logged in as §a" + mc.getSession().getUsername()
                + (switcher.onOriginal() ? " §7(main account)" : " §7(offline)"), width / 2, 20, 0xAAAAAA);
        nameField.drawTextBox();
        if (nameField.getText().isEmpty() && !nameField.isFocused()) {
            fontRendererObj.drawString("Username", nameField.xPosition + 4, nameField.yPosition + 6, 0x707070);
        }
        if (store.alts().isEmpty()) {
            drawCenteredString(fontRendererObj, "No alts yet: type a name and press Add", width / 2, height / 2 - 20, 0x808080);
        }
        drawCenteredString(fontRendererObj, status, width / 2, height - 66, statusColor);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private final class AltList extends GuiSlot {
        AltList() {
            super(GuiAltManager.this.mc, GuiAltManager.this.width, GuiAltManager.this.height, 32,
                    GuiAltManager.this.height - 70, 14);
        }

        @Override
        protected int getSize() {
            return store.alts().size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            selected = index;
            nameField.setText("");
            if (doubleClick) {
                login(store.alts().get(index));
            }
        }

        @Override
        protected boolean isSelected(int index) {
            return index == selected;
        }

        @Override
        protected void drawBackground() {}

        @Override
        protected void drawSlot(int index, int x, int y, int slotHeight, int mouseX, int mouseY) {
            String name = store.alts().get(index);
            boolean active = !switcher.onOriginal() && name.equals(mc.getSession().getUsername());
            fontRendererObj.drawString(name + (active ? "  §7(active)" : ""), x + 2, y + 2, active ? GREEN : 0xFFFFFF);
        }
    }
}
