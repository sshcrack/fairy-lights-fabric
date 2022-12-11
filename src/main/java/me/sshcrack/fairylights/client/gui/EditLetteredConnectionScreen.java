package me.sshcrack.fairylights.client.gui;

import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.client.gui.component.ColorButton;
import me.sshcrack.fairylights.client.gui.component.PaletteButton;
import me.sshcrack.fairylights.client.gui.component.StyledTextFieldWidget;
import me.sshcrack.fairylights.client.gui.component.ToggleButton;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import me.sshcrack.fairylights.util.styledstring.StylingPresence;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class EditLetteredConnectionScreen<C extends Connection & Lettered> extends Screen {
    public static final Identifier WIDGETS_TEXTURE = new Identifier(FairyLightsMod.ModID, "textures/gui/widgets.png");

    private final C connection;

    private StyledTextFieldWidget textField;

    private ButtonWidget doneBtn;

    private ButtonWidget cancelBtn;

    private ColorButton colorBtn;

    private ToggleButton boldBtn;

    private ToggleButton italicBtn;

    private ToggleButton underlineBtn;

    private ToggleButton strikethroughBtn;

    private PaletteButton paletteBtn;

    public EditLetteredConnectionScreen(final C connection) {
        super(Text.empty());
        this.connection = connection;
    }

    @Override
    public void init() {
        this.client.keyboard.setRepeatEvents(true);
        final int pad = 4;
        final int buttonWidth = 150;
        this.doneBtn = this.addDrawable(new ButtonWidget(this.width / 2 - pad - buttonWidth, this.height / 4 + 120 + 12, buttonWidth, 20, Text.translatable("gui.done"), b -> {
            FairyLightsMod.NETWORK.sendToServer(new EditLetteredConnectionMessage<>(this.connection, this.textField.getValue()));
            this.onClose();
        }));
        this.cancelBtn = this.addDrawable(new ButtonWidget(this.width / 2 + pad, this.height / 4 + 120 + 12, buttonWidth, 20, Text.translatable("gui.cancel"), b -> this.onClose()));
        final int textFieldX = this.width / 2 - 150;
        final int textFieldY = this.height / 2 - 10;
        int buttonX = textFieldX;
        final int buttonY = textFieldY - 25;
        final int bInc = 24;
        this.colorBtn = this.addDrawable(new ColorButton(buttonX, buttonY, Text.empty(), b -> this.paletteBtn.visible = !this.paletteBtn.visible));
        this.paletteBtn = this.addDrawable(new PaletteButton(buttonX - 4, buttonY - 30, this.colorBtn, Text.translatable("fairylights.color"), b -> this.textField.updateStyling(this.colorBtn.getDisplayColor(), true)));
        this.boldBtn = this.addDrawable(new ToggleButton(buttonX += bInc, buttonY, 40, 0, Text.empty(), b -> this.updateStyleButton(Formatting.BOLD, this.boldBtn)));
        this.italicBtn = this.addDrawable(new ToggleButton(buttonX += bInc, buttonY, 60, 0, Text.empty(), b -> this.updateStyleButton(Formatting.ITALIC, this.italicBtn)));
        this.underlineBtn = this.addDrawable(new ToggleButton(buttonX += bInc, buttonY, 80, 0, Text.empty(), b -> this.updateStyleButton(Formatting.UNDERLINE, this.underlineBtn)));
        this.strikethroughBtn = this.addDrawable(new ToggleButton(buttonX += bInc, buttonY, 100, 0, Text.empty(), b -> this.updateStyleButton(Formatting.STRIKETHROUGH, this.strikethroughBtn)));
        this.textField = new StyledTextFieldWidget(this.font, this.colorBtn, this.boldBtn, this.italicBtn, this.underlineBtn, this.strikethroughBtn, textFieldX, textFieldY, 300, 20, Text.translatable("fairylights.letteredText"));
        this.textField.setValue(this.connection.getText());
        this.textField.setCaretStart();
        this.textField.setIsBlurable(false);
        this.textField.registerChangeListener(this::validateText);
        this.textField.setCharInputTransformer(this.connection.getInputTransformer());
        this.textField.setFocused(true);
        this.addWidget(this.textField);
        this.paletteBtn.visible = false;
        final StylingPresence ss = this.connection.getSupportedStyling();
        this.colorBtn.visible = ss.hasColor();
        this.boldBtn.visible = ss.hasBold();
        this.italicBtn.visible = ss.hasItalic();
        this.underlineBtn.visible = ss.hasUnderline();
        this.strikethroughBtn.visible = ss.hasStrikethrough();
        this.setInitialFocus(this.textField);
    }

    private void validateText(final StyledString text) {
        this.doneBtn.active = this.connection.isSupportedText(text) && !this.connection.getText().equals(text);
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public void mouseMoved(final double x, final double y) {
        this.textField.mouseMoved(x, y);
    }

    @Override
    public void tick() {
        this.textField.tick();
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        this.paletteBtn.visible = false;
        if (isControlOp(keyCode, GLFW.GLFW_KEY_B)) {
            this.toggleStyleButton(Formatting.BOLD, this.boldBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_I)) {
            this.toggleStyleButton(Formatting.ITALIC, this.italicBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_U)) {
            this.toggleStyleButton(Formatting.UNDERLINE, this.underlineBtn);
            return true;
        } else if (isControlOp(keyCode, GLFW.GLFW_KEY_S)) {
            this.toggleStyleButton(Formatting.STRIKETHROUGH, this.strikethroughBtn);
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && this.doneBtn.active) {
            this.doneBtn.onPress();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.cancelBtn.onPress();
            return true;
        }
        return false;
    }

    private void toggleStyleButton(final Formatting styling, final ToggleButton btn) {
        btn.setValue(!btn.getValue());
        this.updateStyleButton(styling, btn);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        this.paletteBtn.visible = false;
        return false;
    }

    private void updateStyleButton(final Formatting styling, final ToggleButton btn) {
        if (btn.visible) {
            this.textField.updateStyling(styling, btn.getValue());
        }
    }

    @Override
    public void render(final PoseStack stack, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, Text.translatable("fairylights.editLetteredConnection"), this.width / 2, 20, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, delta);
        this.textField.render(stack, mouseX, mouseY, delta);
        final String allowed = this.connection.getAllowedDescription();
        if (!allowed.isEmpty()) {
            drawString(stack, this.font,
                Text.translatable("fairylights.editLetteredConnection.allowed_characters", allowed)
                    .withStyle(Formatting.GRAY),
                this.textField.x,
                this.textField.y + 24,
                0xFFFFFFFF
            );
        }
    }

    public static boolean isControlOp(final int key, final int controlKey) {
        return key == controlKey && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }
}
